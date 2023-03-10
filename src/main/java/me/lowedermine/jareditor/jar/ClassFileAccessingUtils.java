package me.lowedermine.jareditor.jar;

import me.lowedermine.jareditor.EditingClassloader;
import me.lowedermine.jareditor.jar.annotations.Annotation;
import me.lowedermine.jareditor.jar.annotations.ElementValuePair;
import me.lowedermine.jareditor.jar.descriptors.FieldDescriptor;
import me.lowedermine.jareditor.jar.infos.ClassInfo;
import me.lowedermine.jareditor.jar.infos.ClassMethodInfo;
import me.lowedermine.jareditor.jar.infos.MethodInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class ClassFileAccessingUtils {

    private final EditingClassloader classloader;

    public ClassFileAccessingUtils(EditingClassloader classloader) {
        this.classloader = classloader;
    }

    public ClassFile forName(ClassInfo name) {
        if (name == null) return null;
        return classloader.readRaw(name);
    }
    public ClassFile superClass(ClassFile classInfo) {
        if (classInfo.info.superName == null) return null;
        return classloader.readRaw(classInfo.info.superName);
    }
    public ClassFile interfaces(ClassFile classInfo, ClassInfo name) {
        if (classInfo.info.interfaces == null || !Arrays.asList(classInfo.info.interfaces).contains(name)) return null;
        return classloader.readRaw(name);
    }
    public ClassFile[] interfaces(ClassFile classInfo) {
        if (classInfo.info.interfaces == null) return null;
        return Arrays.stream(classInfo.info.interfaces).map(classloader::readRaw).toArray(ClassFile[]::new);
    }

    public ClassFile nestHost(ClassFile classInfo) {
        if (classInfo.nested == null || classInfo.nested.nestHost == null) return null;
        return classloader.readRaw(classInfo.nested.nestHost);
    }
    public ClassFile nestMembers(ClassFile classInfo, ClassInfo name) {
        if (classInfo.nested == null || classInfo.nested.nestMembers == null || !Arrays.asList(classInfo.nested.nestMembers).contains(name)) return null;
        return classloader.readRaw(name);
    }
    public ClassFile[] nestMembers(ClassFile classInfo) {
        if (classInfo.nested == null || classInfo.nested.nestMembers == null) return null;
        return Arrays.stream(classInfo.nested.nestMembers).map(classloader::readRaw).toArray(ClassFile[]::new);
    }
    public Method enclosingMethod(ClassFile classInfo) {
        if (classInfo.nested == null || classInfo.nested.enclosingMethod == null || classInfo.nested.enclosingMethod.name == null) return null;
        ClassMethodInfo info = classInfo.nested.enclosingMethod;
        ClassFile holder = classloader.readRaw(info.clazz);
        return method(holder, info.getInfo());
    }
    public ClassFile enclosingClass(ClassFile classInfo) {
        if (classInfo.nested == null || classInfo.nested.enclosingMethod == null) return null;
        return classloader.readRaw(classInfo.nested.enclosingMethod.clazz);
    }

    public Field field(ClassFile file, String name) {
        if (file.main != null && file.main.fields != null)
            for (Field field1 : file.main.fields)
                if (field1.info.info.name.equals(name)) return field1;
        return null;
    }
    public Method method(ClassFile file, MethodInfo info) {
        if (file.main != null && file.main.methods != null)
            for (Method method1 : file.main.methods)
                if (method1.info.info.equals(info)) return method1;
        return null;
    }
    public Method[] method(ClassFile file, String name) {
        List<Method> results = new ArrayList<>();
        if (file.main != null && file.main.methods != null)
            for (Method method1 : file.main.methods)
                if (method1.info.info.name.equals(name)) results.add(method1);
        return results.isEmpty() ? null : results.toArray(new Method[0]);
    }

    public Annotation annotation(ClassFile file, FieldDescriptor name, AnnotationType lookFor) {
        if (file.annotation != null) {
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.NORMAL || lookFor == AnnotationType.VISIBLE || lookFor == AnnotationType.NORMAL_VISIBLE)
                if (file.annotation.visible != null)
                    for (Annotation annotation : file.annotation.visible)
                        if (annotation.type.equals(name)) return annotation;
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.NORMAL || lookFor == AnnotationType.INVISIBLE || lookFor == AnnotationType.NORMAL_INVISIBLE)
                if (file.annotation.invisible != null)
                    for (Annotation annotation : file.annotation.invisible)
                        if (annotation.type.equals(name)) return annotation;
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.TYPE || lookFor == AnnotationType.VISIBLE || lookFor == AnnotationType.TYPE_VISIBLE)
                if (file.annotation.visibleType != null)
                    for (Annotation annotation : file.annotation.visibleType)
                        if (annotation.type.equals(name)) return annotation;
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.TYPE || lookFor == AnnotationType.INVISIBLE || lookFor == AnnotationType.TYPE_INVISIBLE)
                if (file.annotation.invisibleType != null)
                    for (Annotation annotation : file.annotation.invisibleType)
                        if (annotation.type.equals(name)) return annotation;
        }
        return null;
    }
    public Annotation annotation(Field field, FieldDescriptor name, AnnotationType lookFor) {
        if (field.annotation != null) {
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.NORMAL || lookFor == AnnotationType.VISIBLE || lookFor == AnnotationType.NORMAL_VISIBLE)
                if (field.annotation.visible != null)
                    for (Annotation annotation : field.annotation.visible)
                        if (annotation.type.equals(name)) return annotation;
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.NORMAL || lookFor == AnnotationType.INVISIBLE || lookFor == AnnotationType.NORMAL_INVISIBLE)
                if (field.annotation.invisible != null)
                    for (Annotation annotation : field.annotation.invisible)
                        if (annotation.type.equals(name)) return annotation;
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.TYPE || lookFor == AnnotationType.VISIBLE || lookFor == AnnotationType.TYPE_VISIBLE)
                if (field.annotation.visibleType != null)
                    for (Annotation annotation : field.annotation.visibleType)
                        if (annotation.type.equals(name)) return annotation;
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.TYPE || lookFor == AnnotationType.INVISIBLE || lookFor == AnnotationType.TYPE_INVISIBLE)
                if (field.annotation.invisibleType != null)
                    for (Annotation annotation : field.annotation.invisibleType)
                        if (annotation.type.equals(name)) return annotation;
        }
        return null;
    }
    public Annotation annotation(Method method, FieldDescriptor name, AnnotationType lookFor) {
        if (method.annotation != null) {
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.NORMAL || lookFor == AnnotationType.VISIBLE || lookFor == AnnotationType.NORMAL_VISIBLE)
                if (method.annotation.visible != null)
                    for (Annotation annotation : method.annotation.visible)
                        if (annotation.type.equals(name)) return annotation;
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.NORMAL || lookFor == AnnotationType.INVISIBLE || lookFor == AnnotationType.NORMAL_INVISIBLE)
                if (method.annotation.invisible != null)
                    for (Annotation annotation : method.annotation.invisible)
                        if (annotation.type.equals(name)) return annotation;
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.PARAMETER || lookFor == AnnotationType.VISIBLE || lookFor == AnnotationType.PARAMETER_VISIBLE)
                if (method.annotation.visibleParameter != null)
                    for (Annotation[] annotations : method.annotation.visibleParameter)
                        for (Annotation annotation : annotations)
                            if (annotation.type.equals(name)) return annotation;
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.PARAMETER || lookFor == AnnotationType.INVISIBLE || lookFor == AnnotationType.PARAMETER_INVISIBLE)
                if (method.annotation.invisibleParameter != null)
                    for (Annotation[] annotations : method.annotation.invisibleParameter)
                        for (Annotation annotation : annotations)
                            if (annotation.type.equals(name)) return annotation;
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.TYPE || lookFor == AnnotationType.VISIBLE || lookFor == AnnotationType.TYPE_VISIBLE)
                if (method.annotation.visibleType != null)
                    for (Annotation annotation : method.annotation.visibleType)
                        if (annotation.type.equals(name)) return annotation;
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.TYPE || lookFor == AnnotationType.INVISIBLE || lookFor == AnnotationType.TYPE_INVISIBLE)
                if (method.annotation.invisibleType != null)
                    for (Annotation annotation : method.annotation.invisibleType)
                        if (annotation.type.equals(name)) return annotation;
        }
        return null;
    }
    public Annotation annotation(Method.CodeSegment code, FieldDescriptor name, AnnotationType lookFor) {
        if (code.annotation != null) {
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.TYPE || lookFor == AnnotationType.VISIBLE || lookFor == AnnotationType.TYPE_VISIBLE)
                if (code.annotation.visible != null)
                    for (Annotation annotation : code.annotation.visible)
                        if (annotation.type.equals(name)) return annotation;
            if (lookFor == AnnotationType.EVERY || lookFor == AnnotationType.TYPE || lookFor == AnnotationType.INVISIBLE || lookFor == AnnotationType.TYPE_INVISIBLE)
                if (code.annotation.invisible != null)
                    for (Annotation annotation : code.annotation.invisible)
                        if (annotation.type.equals(name)) return annotation;
        }
        return null;
    }

    public Object annotationValue(Annotation annotation, String name) {
        ElementValuePair.ElementValue value = null;
        for (ElementValuePair pair : annotation.pairs) {
            if (pair.name.equals(name)) value = pair.value;
        }
        if (value == null) {
            Method[] methods = method(classloader.readRaw(((ClassInfo)annotation.type.type)), name);
            if (methods == null) return null;
            Method defaultValue = methods[0];
            if (defaultValue.info.annotationDefault != null)
                value = defaultValue.info.annotationDefault;
        }
        if (value == null)
            return null;
        if (value.value instanceof ElementValuePair.ElementValue[])
            return Arrays.stream(((ElementValuePair.ElementValue[]) value.value)).map(val -> val.value).toArray();
        return value.value;
    }

    public enum AnnotationType {
        EVERY, VISIBLE, INVISIBLE, NORMAL, NORMAL_VISIBLE, NORMAL_INVISIBLE, PARAMETER, PARAMETER_VISIBLE, PARAMETER_INVISIBLE, TYPE,  TYPE_VISIBLE, TYPE_INVISIBLE;
    }
}
