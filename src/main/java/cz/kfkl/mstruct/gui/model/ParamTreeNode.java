package cz.kfkl.mstruct.gui.model;

import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public interface ParamTreeNode {

	public ObservableList<? extends ParamTreeNode> getChildren();

	public boolean isParameter();

	StringProperty getParamContainerNameProperty();

	public BooleanProperty getRefinedProperty();

	public BooleanProperty getLimitedProperty();

	public StringProperty getMinProperty();

	public StringProperty getMaxProperty();

	public StringProperty getValueProperty();

	public boolean isIhklParameter();

	public BooleanProperty getFittedProperty();

	public StringProperty getFittedValueProperty();

	default public void applyRecursively(Consumer<ParamTreeNode> consumer,
			Consumer<ObservableList<? extends ParamTreeNode>> childrenConsumer) {
		applyRecursively(getChildren(), consumer, childrenConsumer);
	}

	static void applyRecursively(List<? extends ParamTreeNode> children, Consumer<ParamTreeNode> parConsumer,
			Consumer<ObservableList<? extends ParamTreeNode>> childrenConsumer) {
		for (ParamTreeNode childNode : children) {
			if (childNode.isParameter()) {
				parConsumer.accept(childNode);
			} else {
				childNode.applyRecursively(parConsumer, childrenConsumer);
				childrenConsumer.accept(childNode.getChildren());
			}
		}
	}

}
