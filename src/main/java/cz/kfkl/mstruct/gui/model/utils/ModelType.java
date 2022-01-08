package cz.kfkl.mstruct.gui.model.utils;

public class ModelType {

	private Class<?> modelClass;
	private String typeName;

	public ModelType(Class<?> modelClass, String typeName) {
		super();
		this.modelClass = modelClass;
		this.typeName = typeName;
	}

	public Class<?> getModelClass() {
		return modelClass;
	}

	public String getTypeName() {
		return typeName;
	}

}
