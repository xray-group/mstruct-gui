package cz.kfkl.mstruct.gui.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javafx.util.StringConverter;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlElementValueProperty {

	public Class<?> converter() default StringConverter.class;

}
