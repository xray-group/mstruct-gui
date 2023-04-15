package cz.kfkl.mstruct.gui.utils.reflection;

import java.util.ArrayList;
import java.util.List;

public class PropertyScanner {
	private static PropertyInterceptor[] DEFAULT_INTERCEPTORS = new PropertyInterceptor[] {};

	private PropertyInterceptor[] interceptors;

	public PropertyScanner() {
		interceptors = DEFAULT_INTERCEPTORS;
	}

	public PropertyScanner(PropertyInterceptor[] interceptors) {
		super();
		this.interceptors = interceptors;
	}

	public List<BeanProperty> scan(Class<?> beanClass) {
		FxBeanInfo fxBeanInfo = scanInfo(beanClass);
		return fxBeanInfo.getAllProperties();
	}

	public FxBeanInfo scanInfo(Class<?> beanClass) {
		List<BeanProperty> beanProperties = new ArrayList<>();
		for (PropertyInterceptor pi : interceptors) {
			beanProperties.addAll(pi.scan(beanClass));
		}
		// TODO merger ?
		// TODO filter
		// TODO validator

		FxBeanInfo fxBeanInfo = new FxBeanInfo(beanProperties);
		return fxBeanInfo;
	}

}
