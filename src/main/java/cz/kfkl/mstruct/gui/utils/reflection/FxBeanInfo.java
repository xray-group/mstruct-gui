package cz.kfkl.mstruct.gui.utils.reflection;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class FxBeanInfo {

	private List<BeanProperty> beanProperties;
	private LinkedHashMap<String, BeanProperty> beanPropertiesByName = new LinkedHashMap<>();

	public FxBeanInfo(List<BeanProperty> beanProperties) {
		this.beanProperties = beanProperties;
		for (BeanProperty prop : beanProperties) {
			beanPropertiesByName.put(prop.getName(), prop);
		}
	}

	public List<BeanProperty> getAllProperties() {
		return beanProperties;
	}

	public Collection<BeanProperty> getDistictProperties() {
		return beanPropertiesByName.values();
	}

	public BeanProperty getPropety(String name) {
		return beanPropertiesByName.get(name);
	}

}
