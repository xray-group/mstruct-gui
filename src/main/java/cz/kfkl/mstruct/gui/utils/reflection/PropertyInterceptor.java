package cz.kfkl.mstruct.gui.utils.reflection;

import java.util.List;

public interface PropertyInterceptor {

	List<BeanProperty> scan(Class<?> beanClass);

}
