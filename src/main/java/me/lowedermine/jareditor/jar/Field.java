package me.lowedermine.jareditor.jar;

import me.lowedermine.jareditor.jar.annotations.Annotation;
import me.lowedermine.jareditor.jar.annotations.TypeAnnotation;
import me.lowedermine.jareditor.jar.constants.ConstantPool;
import me.lowedermine.jareditor.jar.constants.ConstantPoolBuilder;
import me.lowedermine.jareditor.jar.descriptors.FieldDescriptor;
import me.lowedermine.jareditor.jar.descriptors.IDescriptor;
import me.lowedermine.jareditor.jar.infos.FieldInfo;
import me.lowedermine.jareditor.jar.signatures.ClassSignature;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Field {
    public FieldInfoSegment info = new FieldInfoSegment();
    public AnnotationSegment annotation = new AnnotationSegment();

    public Field(Field copy) {
        info = copy.info;
        annotation = copy.annotation;
    }

    public Field(DataInputStream in, ConstantPool cp) throws IOException {
        info.accessFlags = AccessFlags.fromCode(in.readUnsignedShort(), AccessFlags.Type.FIELD);
        String name = (String) cp.ro(in.readUnsignedShort());
        String desc = (String) cp.ro(in.readUnsignedShort());
        info.info = new FieldInfo(name, (FieldDescriptor) IDescriptor.parse(desc));
        int attributeLength = in.readUnsignedShort();
        for (int i = 0; i < attributeLength; i++) parseAttribute(in, cp);
        recalculateSegments();
    }

    private void parseAttribute(DataInputStream in, ConstantPool cp) throws IOException {
        String name = (String) cp.ro(in.readUnsignedShort());
        in.readInt();
        switch (name) {
            case "ConstantValue":
                info.constantValue = cp.ro(in.readUnsignedShort());
                break;
            case "Synthetic":
                info.synthetic = true;
                break;
            case "Deprecated":
                info.deprecated = true;
                break;
            case "Signature":
                info.signature = new ClassSignature((String) cp.ro(in.readUnsignedShort()));
                break;
            case "RuntimeVisibleAnnotations":
                annotation.visible = new Annotation[in.readUnsignedShort()];
                for (int i = 0; i < annotation.visible.length; i++) annotation.visible[i] = new Annotation(in, cp);
                break;
            case "RuntimeInvisibleAnnotations":
                annotation.invisible = new Annotation[in.readUnsignedShort()];
                for (int i = 0; i < annotation.invisible.length; i++) annotation.invisible[i] = new Annotation(in, cp);
                break;
            case "RuntimeVisibleTypeAnnotations":
                annotation.visibleType = new TypeAnnotation[in.readUnsignedShort()];
                for (int i = 0; i < annotation.visibleType.length; i++) annotation.visibleType[i] = new TypeAnnotation(in, cp);
                break;
            case "RuntimeInvisibleTypeAnnotations":
                annotation.invisibleType = new TypeAnnotation[in.readUnsignedShort()];
                for (int i = 0; i < annotation.invisibleType.length; i++) annotation.invisibleType[i] = new TypeAnnotation(in, cp);
                break;
        }
    }

    public void toPool(ConstantPoolBuilder cp) {
        {
            cp.add(info.info.name);
            cp.add(info.info.desc.toRaw());
            if (info.deprecated) {
                cp.add("Deprecated");
            }
            if (info.synthetic) {
                cp.add("Synthetic");
            }
            if (info.signature != null) {
                cp.add("Signature");
                cp.add(info.signature.toRaw());
            }
            if (info.constantValue != null) {
                cp.add("ConstantValue");
                cp.add(info.constantValue);
            }
        }
        if (annotation != null) {
            if (annotation.visible != null) {
                cp.add("RuntimeVisibleAnnotations");
                for (Annotation annotation1 : annotation.visible) annotation1.toPool(cp);
            }
            if (annotation.invisible != null) {
                cp.add("RuntimeInvisibleAnnotations");
                for (Annotation annotation1 : annotation.invisible) annotation1.toPool(cp);
            }
            if (annotation.visibleType != null) {
                cp.add("RuntimeVisibleTypeAnnotations");
                for (TypeAnnotation annotation1 : annotation.visibleType) annotation1.toPool(cp);
            }
            if (annotation.invisibleType != null) {
                cp.add("RuntimeInvisibleTypeAnnotations");
                for (TypeAnnotation annotation1 : annotation.invisibleType) annotation1.toPool(cp);
            }
        }
    }

    public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
        out.writeShort(AccessFlags.toCode(info.accessFlags));
        out.writeShort(cp.indexOf(info.info.name));
        out.writeShort(cp.indexOf(info.info.desc.toRaw()));
        int attributes = 0;
        {
            if (info.synthetic)
                attributes++;
            if (info.deprecated)
                attributes++;
            if (info.constantValue != null)
                attributes++;
            if (info.signature != null)
                attributes++;
        }
        if (annotation != null) {
            if (annotation.visible != null)
                attributes++;
            if (annotation.invisible != null)
                attributes++;
            if (annotation.visibleType != null)
                attributes++;
            if (annotation.invisibleType != null)
                attributes++;
        }
        out.writeShort(attributes);
        {
            if (info.synthetic) {
                out.writeShort(cp.indexOf("Synthetic"));
                out.writeInt(0);
            }
            if (info.deprecated) {
                out.writeShort(cp.indexOf("Deprecated"));
                out.writeInt(0);
            }
            if (info.constantValue != null) {
                out.writeShort(cp.indexOf("ConstantValue"));
                out.writeInt(2);
                out.writeShort(cp.indexOf(info.constantValue));
            }
            if (info.signature != null) {
                out.writeShort(cp.indexOf("Signature"));
                out.writeInt(2);
                out.writeShort(cp.indexOf(info.signature.toRaw()));
            }
        }
        if (annotation != null) {
            if (annotation.visible != null) {
                out.writeShort(cp.indexOf("RuntimeVisibleAnnotations"));
                int length = 2;
                for (Annotation annotation1 : annotation.visible) length += annotation1.toLength();
                out.writeInt(length);
                out.writeShort(annotation.visible.length);
                for (Annotation annotation1 : annotation.visible) annotation1.toStream(out, cp);
            }
            if (annotation.invisible != null) {
                out.writeShort(cp.indexOf("RuntimeInvisibleAnnotations"));
                int length = 2;
                for (Annotation annotation1 : annotation.invisible) length += annotation1.toLength();
                out.writeInt(length);
                out.writeShort(annotation.invisible.length);
                for (Annotation annotation1 : annotation.invisible) annotation1.toStream(out, cp);
            }
            if (annotation.visibleType != null) {
                out.writeShort(cp.indexOf("RuntimeVisibleTypeAnnotations"));
                int length = 2;
                for (TypeAnnotation annotation1 : annotation.visibleType) length += annotation1.toLength();
                out.writeInt(length);
                out.writeShort(annotation.visibleType.length);
                for (TypeAnnotation annotation1 : annotation.visibleType) annotation1.toStream(out, cp);
            }
            if (annotation.invisibleType != null) {
                out.writeShort(cp.indexOf("RuntimeInvisibleTypeAnnotations"));
                int length = 2;
                for (TypeAnnotation annotation1 : annotation.invisibleType) length += annotation1.toLength();
                out.writeInt(length);
                out.writeShort(annotation.invisibleType.length);
                for (TypeAnnotation annotation1 : annotation.invisibleType) annotation1.toStream(out, cp);
            }
        }
    }

    public void recalculateSegments() {
        if (annotation != null) {
            if (annotation.visible != null && annotation.visible.length == 0)
                annotation.visible = null;
            if (annotation.invisible != null && annotation.invisible.length == 0)
                annotation.invisible = null;
            if (annotation.visibleType != null && annotation.visibleType.length == 0)
                annotation.visibleType = null;
            if (annotation.invisibleType != null && annotation.invisibleType.length == 0)
                annotation.invisibleType = null;
            if (annotation.visible == null && annotation.invisible == null && annotation.visibleType == null && annotation.invisibleType == null)
                annotation = null;
        }
    }

    public static class FieldInfoSegment {
        public AccessFlags[] accessFlags;
        public FieldInfo info;
        public ClassSignature signature;
        public Object constantValue;
        public boolean synthetic;
        public boolean deprecated;
    }

    public static class AnnotationSegment {
        public Annotation[] visible = null;
        public Annotation[] invisible = null;
        public TypeAnnotation[] visibleType = null;
        public TypeAnnotation[] invisibleType = null;
    }
}
