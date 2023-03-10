package me.lowedermine.jareditor;

import me.lowedermine.jareditor.editing.modifier.EditingClass;
import me.lowedermine.jareditor.editing.preloads.ClassNameEditor;
import me.lowedermine.jareditor.editing.preloads.IEditor;
import me.lowedermine.jareditor.exceptions.CodeEditingException;
import me.lowedermine.jareditor.jar.ClassFile;
import me.lowedermine.jareditor.jar.infos.ClassInfo;
import me.lowedermine.jareditor.jar.infos.PackageInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Manifest;

@SuppressWarnings("unused")
public class EditingClassloader extends URLClassLoader {

    private final ClassLoader parent;

    private final List<PackageInfo> exceptions = new ArrayList<>();
    private final Map<ClassInfo, List<IEditor>> editors = new HashMap<>();
    private final List<IEditor> globalEditors = new ArrayList<>();

    private final Map<ClassInfo, byte[]> loadedResource = new ConcurrentHashMap<>(1000);
    private final Set<ClassInfo> missedResource = new HashSet<>();


    public EditingClassloader() {
        this(new URL[0], EditingClassloader.class.getClassLoader());
    }

    public EditingClassloader(URL[] urls) {
        this(urls, EditingClassloader.class.getClassLoader());
    }

    public EditingClassloader(ClassLoader parent) {
        this(new URL[0], parent);
    }

    public EditingClassloader(URL[] urls, ClassLoader parent) {
        super(urls, null);
        this.parent = parent;
        exceptions.add(new PackageInfo("java"));
        exceptions.add(new PackageInfo("me.lowedermine.jareditor"));
        try {
            Enumeration<URL> manifests = findResources("META-INF/MANIFEST.MF");
            while (manifests.hasMoreElements()) {
                try (InputStream stream = manifests.nextElement().openStream()) {
                    ClassInfo hubName = new ClassInfo(new Manifest(stream).getMainAttributes().getValue("Preload-Hub"));
                    if (Objects.equals(hubName.name, "")) continue;
                    try {
                        addIfAbsent(hubName.pkg);
                        Class<?> hub = parent.loadClass(hubName.toRaw().replace('/', '.'));
                        if (!PreloadHub.class.isAssignableFrom(hub))
                            throw new IllegalArgumentException("Preload hub must implement any of me.lowedermine.jareditor.IPreloadHub");
                        PreloadHub instance = (PreloadHub) hub.getDeclaredConstructor().newInstance();
                        PackageInfo[] exceptions1 = instance.getExceptions();
                        if (exceptions1 != null)
                            for (PackageInfo exception1 : exceptions1) addIfAbsent(exception1);
                        ClassInfo[] preloads = instance.getPreloads();
                        if (preloads != null)
                            for (ClassInfo preloadName : instance.getPreloads())
                                try {
                                    addIfAbsent(preloadName.pkg);
                                    preloadName = preloadName.getInfo();
                                    Class<?> preload = parent.loadClass(preloadName.toRaw().replace('/', '.'));
                                    IEditor editor;
                                    if (IEditor.class.isAssignableFrom(preload)) {
                                        editor = (IEditor) preload.getDeclaredConstructor().newInstance();
                                        editor.acceptClassloader(this);
                                    } else {
                                        editor = new EditingClass(readRaw(preloadName));
                                        editor.acceptClassloader(this);
                                        if (!((EditingClass) editor).modifying)
                                            throw new IllegalArgumentException("Preload class must either implement IEditor or contain @ClassModifier annotation");
                                    }
                                    ClassInfo[] edited = editor.getEdited();
                                    if (edited == null)
                                        globalEditors.add(editor);
                                    else
                                        for (ClassInfo ed : edited) {
                                            ed = ed.getInfo();
                                            editors.putIfAbsent(ed, new ArrayList<>());
                                            editors.get(ed).add(editor);
                                        }
                                } catch (Exception e) {
                                    throw new CodeEditingException(hubName, preloadName, e);
                                }
                    } catch (CodeEditingException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new CodeEditingException(hubName, null, e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ClassFile readRaw(ClassInfo name) {
        name = name.getInfo();
        String file = name.toRaw() + ".class";
        if (missedResource.contains(name))
            return null;
        URL classResource = findResource(file);
        if (classResource == null) {
            missedResource.add(name);
            return null;
        }
        try (InputStream classStream = classResource.openStream()) {
            return new ClassFile(classStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addIfAbsent(PackageInfo exception1) {
        boolean add = true;
        for (PackageInfo exception : exceptions) {
            if (exception1.startsWith(exception)) {
                add = false;
                break;
            } else if (exception.startsWith(exception1)) {
                exceptions.remove(exception);
            }
        }
        if (add) exceptions.add(exception1);
    }

    private byte[] readClass(ClassInfo name, boolean editable) {
        name = name.getInfo();
        String file = name.toRaw() + ".class";
        if (missedResource.contains(name))
            return null;
        if (loadedResource.containsKey(name))
            return loadedResource.get(name);
        URL classResource = findResource(file);
        if (classResource == null) {
            ClassFile clazz = null;
            for (IEditor editor : globalEditors) {
                if (clazz == null)
                    clazz = editor.apply(null);
                else break;
            }
            if (editors.containsKey(name)) for (IEditor editor : editors.get(name)) {
                if (clazz == null)
                    clazz = editor.apply(null);
                else break;
            }
            if (editable) {
                for (IEditor editor : globalEditors) clazz = editor.apply(clazz);
                if (editors.containsKey(name)) for (IEditor editor : editors.get(name)) clazz = editor.apply(clazz);
            }
            if (clazz == null) {
                missedResource.add(name);
                return null;
            }
            try (ByteArrayOutputStream classOutput = new ByteArrayOutputStream()) {
                clazz.toStream(classOutput);
                byte[] data = classOutput.toByteArray();
                this.loadedResource.put(name, data);
                return data;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (InputStream classStream = classResource.openStream()) {
            ClassFile clazz = new ClassFile(classStream);
            if (editable) {
                for (IEditor editor : globalEditors) clazz = editor.apply(clazz);
                if (editors.containsKey(name)) for (IEditor editor : editors.get(name)) clazz = editor.apply(clazz);
            }
            if (clazz == null) {
                missedResource.add(name);
                return null;
            }
            try (ByteArrayOutputStream classOutput = new ByteArrayOutputStream()) {
                clazz.toStream(classOutput);
                byte[] data = classOutput.toByteArray();
                this.loadedResource.put(name, data);
                return data;
            }

//            byte[] sourceData = new byte[classStream.available()];
//            if (classStream.read(sourceData) != sourceData.length) throw new RuntimeException("Failed to read class");
//            ClassFile clazz;
//            for (byte sourceDatum : sourceData) System.out.print(sourceDatum + " ");
//            System.out.println();
//            try (ByteArrayInputStream stream = new ByteArrayInputStream(sourceData)) {
//                clazz = new ClassFile(stream);
//            }
//            try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
//                clazz.toStream(stream);
//                byte[] data = stream.toByteArray();
//                for (byte datum : data) System.out.print(datum + " ");
//                System.out.println();
//                ByteArrayInputStream testStream = new ByteArrayInputStream(data);
//                ClassFile class2 = new ClassFile(testStream);
//                testStream.close();
//                this.loadedResource.put(name, data);
//                return data;
//            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        System.out.println("Looking for: " + name);
        ClassInfo unmapped = new ClassInfo(name);
        ClassInfo mapped = new ClassInfo(name);
        for (IEditor renamer : globalEditors) {
            if (renamer instanceof ClassNameEditor) {
                ClassNameEditor classRenamer = (ClassNameEditor) renamer;
                ClassInfo newUnmapped = classRenamer.unmap(unmapped);
                while (newUnmapped != unmapped) {
                    unmapped = newUnmapped;
                    newUnmapped = classRenamer.unmap(unmapped);
                }
                ClassInfo newMapped = classRenamer.map(mapped);
                while (newMapped != mapped) {
                    mapped = newMapped;
                    newMapped = classRenamer.map(mapped);
                }
            }
        }
        boolean editable = true;
        for (PackageInfo exception : this.exceptions) {
            if (unmapped.startsWith(exception)) {
                editable = false;
                break;
            }
        }
        byte[] b = readClass(unmapped, editable);
        if (b == null)
            throw new ClassNotFoundException("Could not find class " + name);
        return defineClass(mapped.toRaw().replace('/', '.'), b, 0, b.length);
    }

    @Override
    public URL findResource(String name) {
        URL resource = super.findResource(name);
        return resource == null ? parent.getResource(name) : resource;
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        Enumeration<URL> resources = super.findResources(name);
        Enumeration<URL> parentResources = parent.getResources(name);
        return new Enumeration<URL>() {
            @Override
            public boolean hasMoreElements() {
                return parentResources.hasMoreElements() || resources.hasMoreElements();
            }

            @Override
            public URL nextElement() {
                if (resources.hasMoreElements())
                    return resources.nextElement();
                return parentResources.hasMoreElements() ? parentResources.nextElement() : null;
            }
        };
    }
}