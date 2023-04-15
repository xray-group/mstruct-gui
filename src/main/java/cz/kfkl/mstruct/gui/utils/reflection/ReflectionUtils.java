package cz.kfkl.mstruct.gui.utils.reflection;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cz.kfkl.mstruct.gui.model.instrumental.PowderPatternElement;

public class ReflectionUtils {

	private static final PropertyScanner scanner = new PropertyScanner(
			new PropertyInterceptor[] { new PublicFieldsInterceptor() });

	public static List<Field> getAllFields(Class cls) {
		List<Field> fields = new ArrayList<>();

		while (!cls.equals(Object.class)) {
			fields.addAll(0, List.of(cls.getDeclaredFields()));
			cls = cls.getSuperclass();
		}

		return fields;
	}

	public static List<BeanProperty> getProperties(Class cls) {
		return scanner.scan(cls);
	}

	public static void main(String[] args) throws IntrospectionException {
		Class<?> cls = PowderPatternElement.class;
		for (Field f : getAllFields(cls)) {
			System.out.println(f);
		}

		for (BeanProperty f : getProperties(cls)) {
			System.out.println(f);
		}

//		BeanInfo beanInfo = Introspector.getBeanInfo(cls);
//
//		System.out.println("\nBeanInfo PropertyDescriptors:");
//		for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
//			System.out.println(pd);
//		}
//
//		System.out.println("\nBeanInfo MethodDescriptors:");
//		for (MethodDescriptor md : beanInfo.getMethodDescriptors()) {
//			System.out.println(md);
//		}
	}

}
