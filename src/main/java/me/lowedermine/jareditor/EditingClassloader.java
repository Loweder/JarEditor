package me.lowedermine.jareditor;

import me.lowedermine.jareditor.editing.preloads.IEditor;
import me.lowedermine.jareditor.editing.preloads.editors.ClassRenamer;
import me.lowedermine.jareditor.jar.ClassFile;
import me.lowedermine.jareditor.jar.infos.ClassInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Manifest;

public class EditingClassloader extends SecureClassLoader {

    private final ClassLoader parent;

    private final List<String> exceptions = new ArrayList<>();
    private final Map<String, List<IEditor>> editors = new HashMap<>();
    private final List<IEditor> globalEditors = new ArrayList<>();

    private final Map<String, byte[]> loadedResource = new ConcurrentHashMap<>(1000);
    private final Set<String> missedResource = new HashSet<>();

    public EditingClassloader(ClassLoader parent) {
        super(null);
        this.parent = parent;
        exceptions.add("java.");
        exceptions.add("me.lowedermine.jareditor.");
        try {
            Enumeration<URL> manifests = findResources("META-INF/MANIFEST.MF");
            while (manifests.hasMoreElements()) {
                InputStream stream = manifests.nextElement().openStream();
                String hubName = new Manifest(stream).getMainAttributes().getValue("Preload-Hub");
                stream.close();
                if (hubName == null) continue;
                try {
                    Class<?> hub = parent.loadClass(hubName);
                    if (!IPreloadHub.class.isAssignableFrom(hub)) {
                        throw new IllegalArgumentException("Preload hub must implement any of me.lowedermine.jareditor.IPreloadHub");
                    }
                    IPreloadHub instance = (IPreloadHub) hub.getDeclaredConstructor().newInstance();
                    String[] preloads = instance.getPreloads();
                    if (preloads != null) {
                        for (String preloadName :  instance.getPreloads()) {
                            try {
                                Class<?> preload = parent.loadClass(preloadName);
                                if (IEditor.class.isAssignableFrom(preload)) {
                                    IEditor editor = (IEditor) preload.getDeclaredConstructor().newInstance();
                                    List<String> edited = editor.getEdited();
                                    if (edited == null)
                                        globalEditors.add(editor);
                                    else
                                        for (String ed : edited) {
                                            editors.putIfAbsent(ed, new ArrayList<>());
                                            editors.get(ed).add(editor);
                                        }
                                } else {
                                    throw new IllegalArgumentException("Preload must implement any of me.lowedermine.jareditor.preloads.");
                                }
                                if (!exceptions.contains(preloadName))
                                    exceptions.add(preloadName);
                            } catch (Exception e) {
                                System.out.println("Preload hub \"" + hubName + "\" preload \"" + preloadName + "\" cannot be loaded: " + e.getLocalizedMessage());
                            }
                        }
                    }
                    String[] exceptions1 = instance.getExceptions();
                    if (exceptions1 != null) {
                        for (String exception : exceptions1) {
                            if (!exceptions.contains(exception))
                                exceptions.add(exception);
                        }
                    }
                    if (!exceptions.contains(hubName)) exceptions.add(hubName);
                } catch (Exception e) {
                    System.out.println("Preload hub \"" + hubName + "\" cannot be loaded: " + e.getLocalizedMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ClassFile readRaw(String name) {
        String file = name.replace('.', '/') + ".class";
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

    private byte[] readClass(String name, boolean editable) {
        String file = name.replace('.', '/') + ".class";
        if (missedResource.contains(name))
            return null;
        if (loadedResource.containsKey(name))
            return loadedResource.get(name);
        URL classResource = findResource(file);
        if (classResource == null) {
            missedResource.add(name);
            return null;
        }
        try (InputStream classStream = classResource.openStream()) {
            ClassFile clazz = new ClassFile(classStream);
            if (editable) {
                for (IEditor editor : globalEditors) clazz = editor.edit(clazz);
                if (editors.containsKey(name)) for (IEditor editor : editors.get(name)) clazz = editor.edit(clazz);
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
            if (renamer instanceof ClassRenamer) {
                ClassRenamer classRenamer = (ClassRenamer) renamer;
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
        String mappedName = mapped.toRaw().replace('/', '.');
        String unmappedName = unmapped.toRaw().replace('/', '.');
        boolean editable = true;
        for (String exception : this.exceptions) {
            if (unmappedName.startsWith(exception)) {
                editable = false;
                break;
            }
        }
        byte[] b = readClass(unmappedName, editable);
        if (b == null)
            throw new ClassNotFoundException("Could not find class " + name);
        return defineClass(mappedName, b, 0, b.length);
    }

    @Override
    protected URL findResource(String name) {
        return parent.getResource(name);
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        return parent.getResources(name);
    }
}