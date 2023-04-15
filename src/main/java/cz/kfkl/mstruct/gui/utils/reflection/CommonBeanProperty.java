package cz.kfkl.mstruct.gui.utils.reflection;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

abstract public class CommonBeanProperty implements BeanProperty {

	private String name;
	private boolean writable = false;

	public CommonBeanProperty(String name) {
		super();
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isWritable() {
		return writable;
	}

	@Override
	public boolean isFxProperty() {
		return false;
	}

	protected void setWritable(boolean writable) {
		this.writable = writable;
	}

	protected static Class<?> getRawClass(AnnotatedType at) {
		return getRawClass(at.getType());
	}

	protected static Class<?> getRawClass(Type ta) {
		Class<?> genericTypeClass = null;
		if (ta instanceof Class) {
			genericTypeClass = (Class<?>) ta;
		} else if (ta instanceof ParameterizedType) {
			ParameterizedType pta = (ParameterizedType) ta;
			genericTypeClass = (Class<?>) pta.getRawType();
		}
		return genericTypeClass;
	}

}
