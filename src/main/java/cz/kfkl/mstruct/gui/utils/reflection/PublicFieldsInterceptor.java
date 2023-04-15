package cz.kfkl.mstruct.gui.utils.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class PublicFieldsInterceptor implements PropertyInterceptor {

	@Override
	public List<BeanProperty> scan(Class<?> cls) {

		List<BeanProperty> props = new ArrayList<>();

		while (!cls.equals(Object.class)) {
			List<BeanProperty> addProps = new ArrayList<>();
			for (Field f : cls.getDeclaredFields()) {
				if (isPublic(f)) {

					addProps.add(createBeanProperty(f));
				}
			}

			props.addAll(0, addProps);
			cls = cls.getSuperclass();
		}

		return props;
	}

	private BeanProperty createBeanProperty(Field f) {
		if (FxFieldBeanProperty.isFxProperty(f)) {
			return new FxFieldBeanProperty(f);
		} else {
			return new FieldBeanProperty(f);
		}
	}

	private boolean isPublic(Field f) {
		return Modifier.isPublic(f.getModifiers());
	}

}
