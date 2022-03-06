package cz.kfkl.mstruct.gui.model.phases;

public enum ReflectionProfileType {
	PVoightA(ReflectionProfilePVoigtAElement.class, "PVoightA", "strainProf"),
	SizeLn(ReflectionProfileSizeLnElement.class, "SizeLn", "sizeProf"),
	StressSimple(ReflectionProfileStressSimpleElement.class, "StressSimple", "stressCorr"),
	RefractionCorr(ReflectionProfileRefractionCorrElement.class, "RefractionCorr", "refractionCorr");

	private Class<? extends ReflectionProfileModel<?>> modelClass;
	private String typeName;
	private String namePrefix;

	private ReflectionProfileType(Class<? extends ReflectionProfileModel<?>> modelClass, String typeName, String prefix) {
		this.modelClass = modelClass;
		this.typeName = typeName;
		this.namePrefix = prefix;
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

	public String getNamePrefix() {
		return namePrefix;
	}

}
