package cz.kfkl.mstruct.gui.model;

public enum PowderPatternBackgroundType {
	Chebyshev(PowderPatternBackgroundChebyshev.class, "Chebyshev"), InvX(PowderPatternBackgroundInvX.class, "InvX");

	private Class<? extends PowderPatternBackgroundModel<?>> modelClass;
	private String typeName;

	private PowderPatternBackgroundType(Class<? extends PowderPatternBackgroundModel<?>> modelClass, String typeName) {
		this.modelClass = modelClass;
		this.typeName = typeName;
	}

	public Class<? extends PowderPatternBackgroundModel<?>> getModelClass() {
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
