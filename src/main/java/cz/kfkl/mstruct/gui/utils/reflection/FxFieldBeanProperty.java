package cz.kfkl.mstruct.gui.utils.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cz.kfkl.mstruct.gui.utils.validation.PopupErrorException;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public class FxFieldBeanProperty extends CommonBeanProperty {

	private static final String PROPERTY_SUFFIX = "Property";
	private static final int PROPERTY_SUFFIX_LENGTH = PROPERTY_SUFFIX.length();
	private Field field;
	private Class valueClass;

	public FxFieldBeanProperty(Field f) {
		super(decideName(f));
		this.field = f;

		checkProperty();
	}

	private static String decideName(Field f) {
		String fieldName = f.getName();
		if (fieldName.endsWith(PROPERTY_SUFFIX)) {
			return fieldName.substring(0, fieldName.length() - PROPERTY_SUFFIX_LENGTH);
		} else {
			return fieldName;
		}
	}

	static boolean isFxProperty(Field field) {
		boolean isProperty = false;
//		if (field.getName().endsWith(PROPERTY_SUFFIX)) {
		AnnotatedType at = field.getAnnotatedType();
		Class rawClass = getRawClass(at.getType());

		isProperty = ReadOnlyProperty.class.isAssignableFrom(rawClass);
//		}

		return isProperty;

	}

	private void checkProperty() {
		AnnotatedType at = field.getAnnotatedType();
		Type type = at.getType();
		Class rawClass = getRawClass(type);

		if (ReadOnlyProperty.class.isAssignableFrom(rawClass)) {
			if (Property.class.isAssignableFrom(rawClass)) {
				setWritable(true);
			}

			if (ReadOnlyStringProperty.class.isAssignableFrom(rawClass)) {
				valueClass = String.class;
			} else if (ReadOnlyBooleanProperty.class.isAssignableFrom(rawClass)) {
				valueClass = Boolean.class;
			} else if (ReadOnlyIntegerProperty.class.isAssignableFrom(rawClass)) {
				valueClass = Integer.class;
			} else {
				inferFromGenericSuperType(at, type, rawClass);
			}

		} else

		{
			throw new PopupErrorException("The field [%s] type [%s] is not compatible with FX Property.", field, type);
		}
	}

	private void inferFromGenericSuperType(AnnotatedType at, Type type, Class rawClass) {

		AnnotatedType currentAt = at;
		while (currentAt != null) {
			valueClass = assertFieldTypeAndGetSingleParameterType(currentAt, ReadOnlyProperty.class);
			if (valueClass != null) {
				return;
			}

			Class<?> atRaw = getRawClass(currentAt);
			for (AnnotatedType sat : atRaw.getAnnotatedInterfaces()) {
				valueClass = assertFieldTypeAndGetSingleParameterType(sat, ReadOnlyProperty.class);
				if (valueClass != null) {
					return;
				}
			}
			currentAt = atRaw.getAnnotatedSuperclass();
		}
		// TODO
		if (valueClass == null) {
			throw new PopupErrorException("The field type [%s] of field [%s] is not supported...", type, field);
		}
	}

	@Override
	public boolean isFxProperty() {
		return true;
	}

	private Class<?> assertFieldTypeAndGetSingleParameterType(AnnotatedType at, Class expectedType) {

		ParameterizedType ptAssignable = typeAssignableFrom(at, expectedType);

		Class<?> genericTypeClass = null;
		if (ptAssignable != null) {
			genericTypeClass = singleTypeArgument(ptAssignable);
		}

		return genericTypeClass;
	}

	private ParameterizedType typeAssignableFrom(AnnotatedType annotatedType, Class expectedType) {
		ParameterizedType ptAssignable = null;

		Type type = annotatedType.getType();
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			if (pt.getRawType() instanceof Class && expectedType.isAssignableFrom((Class<?>) pt.getRawType())) {
				ptAssignable = pt;
			}
		}
		return ptAssignable;
	}

	private Class<?> singleTypeArgument(ParameterizedType pt) {

		Class<?> genericTypeClass = null;
		Type[] actualTypeArguments = pt.getActualTypeArguments();
		if (actualTypeArguments.length == 1) {
			Type ta = actualTypeArguments[0];
			genericTypeClass = getRawClass(ta);
		}
		return genericTypeClass;
	}

	@Override
	public void setValue(Object instance, Object value) throws IllegalArgumentException, IllegalAccessException {
		getProperty(instance).setValue(value);
	}

	@Override
	public Object getValue(Object instance) throws IllegalArgumentException, IllegalAccessException {
		return getReadOnlyProperty(instance).getValue();
	}

	@Override
	public Property getProperty(Object instance) {
		Object val = getFieldValue(instance);
		if (val instanceof Property) {
			return (Property) val;
		} else {
			throw new PopupErrorException("The value [%s] of field [%s] should be an instance of Property", val, field);
		}

	}

	private Object getFieldValue(Object instance) {
		try {
			return field.get(instance);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new PopupErrorException(e, "Failed to obatin a value of field [%s].", field);
		}
	}

	@Override
	public ReadOnlyProperty getReadOnlyProperty(Object instance) {
		Object val = getFieldValue(instance);
		if (val instanceof ReadOnlyProperty) {
			return (ReadOnlyProperty) val;
		} else {
			throw new PopupErrorException("The value [%s] of field [%s] should be an instance of ReadOnlyProperty", val, field);
		}
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
