package cz.kfkl.mstruct.gui.model;

public enum ReflectionProfileType {
	PVoightA(ReflectionProfilePVoigtAElement.class, "PVoightA"), SizeLn(ReflectionProfileSizeLnElement.class, "SizeLn"),
	StressSimple(ReflectionProfileStressSimpleElement.class, "StressSimple"),
	RefractionCorr(ReflectionProfileRefractionCorrElement.class, "RefractionCorr");

	private Class<? extends ReflectionProfileModel<?>> modelClass;
	private String typeName;

	private ReflectionProfileType(Class<? extends ReflectionProfileModel<?>> modelClass, String typeName) {
		this.modelClass = modelClass;
		this.typeName = typeName;
	}

	public Class<? extends ReflectionProfileModel<?>> getModelClass() {
		return modelClass;
	}

	public String getTypeName() {
		return typeName;
	}

	@Override
	public String toString() {
		return typeName;
	}

}
