package cz.kfkl.mstruct.gui.utils.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;

public interface BeanProperty {

	String getName();

	boolean isWritable();

	boolean isFxProperty();

	void setValue(Object instance, Object value) throws IllegalArgumentException, IllegalAccessException;

	Object getValue(Object instance) throws IllegalArgumentException, IllegalAccessException;

	Property getProperty(Object instance);

	ReadOnlyProperty getReadOnlyProperty(Object instance);

	<S extends Annotation> S getAnnotation(Class<S> class1);

	AnnotatedType getAnnotatedType();

	Class getValueClass();

}
