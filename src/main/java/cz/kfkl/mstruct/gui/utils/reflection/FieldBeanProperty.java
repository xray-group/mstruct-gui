package cz.kfkl.mstruct.gui.utils.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;

public class FieldBeanProperty extends CommonBeanProperty {

	private Field field;
	private Class valueClass;

	public FieldBeanProperty(Field f) {
		super(f.getName());
		this.field = f;

		valueClass = getRawClass(field.getType());
		if (!getRawClass(field.getType()).equals(f.getType())) {
			System.out.println("NOT SAME: [" + getRawClass(field.getType()) + "] vs [" + f.getType() + "]");
		}
		setWritable(isFieldWritable(field));
	}

	private static boolean isFieldWritable(Field f) {
		int modifiers = f.getModifiers();
		return Modifier.isPublic(modifiers) && !Modifier.isFinal(modifiers);
	}

	@Override
	public void setValue(Object instance, Object value) throws IllegalArgumentException, IllegalAccessException {
		field.set(instance, value);
	}

	@Override
	public Object getValue(Object instance) throws IllegalArgumentException, IllegalAccessException {
		return field.get(instance);
	}

	@Override
	public Property getProperty(Object instance) {
		return null;
	}

	@Override
	public ReadOnlyProperty getReadOnlyProperty(Object instance) {
		return null;
	}

	@Override
	public <S extends Annotation> S getAnnotation(Class<S> class1) {
		return field.getAnnotation(class1);
	}

	@Override
	public String toString() {
		return field.getDeclaringClass() + "." + getName();
	}

	@Override
	public AnnotatedType getAnnotatedType() {
		return field.getAnnotatedType();
	}

	@Override
	public Class getValueClass() {
		return valueClass;
	}

}
