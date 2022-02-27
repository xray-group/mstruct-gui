package cz.kfkl.mstruct.gui.ui.matrix;

public class TupleKey {

	private String rowIndex;
	private String colIndex;

	public TupleKey(String rowIndex, String colIndex) {
		this.rowIndex = rowIndex;
		this.colIndex = colIndex;
	}

	public static TupleKey createSymetric(String rowIndex, String colIndex) {
		if (rowIndex != null && colIndex != null && rowIndex.compareTo(colIndex) > 0) {
			return new TupleKey(colIndex, rowIndex);
		} else {
			return new TupleKey(rowIndex, colIndex);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((colIndex == null) ? 0 : colIndex.hashCode());
		result = prime * result + ((rowIndex == null) ? 0 : rowIndex.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TupleKey other = (TupleKey) obj;
		if (colIndex == null) {
			if (other.colIndex != null)
				return false;
		} else if (!colIndex.equals(other.colIndex))
			return false;
		if (rowIndex == null) {
			if (other.rowIndex != null)
				return false;
		} else if (!rowIndex.equals(other.rowIndex))
			return false;
		return true;
	}

}
