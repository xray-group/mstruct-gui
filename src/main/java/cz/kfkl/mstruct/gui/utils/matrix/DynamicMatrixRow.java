package cz.kfkl.mstruct.gui.utils.matrix;

public class DynamicMatrixRow<V extends Tuple> {

	private String rowKey;
	private DynamicMatrix<V> dynamicMatrix;

	public DynamicMatrixRow(String rowKey) {
		super();
		this.rowKey = rowKey;
	}

	public DynamicMatrixRow(String key, DynamicMatrix<V> dynamicMatrix) {
		this.rowKey = key;
		this.dynamicMatrix = dynamicMatrix;
	}

	public V getValue(String columnKey) {
		return dynamicMatrix.getTuple(rowKey, columnKey);
	}

	public String getRowKey() {
		return rowKey;
	}

}
