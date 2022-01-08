package cz.kfkl.mstruct.gui.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.kfkl.mstruct.gui.model.CrystalModel;
import cz.kfkl.mstruct.gui.model.DiffractionModel;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamContainer;
import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementList;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ObjCrystModel extends XmlLinkedModelElement implements ParamContainer {

	@XmlElementList
	public ObservableList<CrystalModel> crystals = FXCollections.observableArrayList();

	@XmlElementList
	public List<DiffractionModel> diffractions = new ArrayList<>();

	public IntegerProperty parametersCount = new SimpleIntegerProperty(0);
	public IntegerProperty refinedParameters = new SimpleIntegerProperty();
	// public List<ParUniqueElement> refinedParameters;

	public ObjCrystModel() {
	}

	@Override
	protected ObjCrystModel decideRoot(XmlLinkedModelElement parentModelElement) {
		return this;
	}

	@Override
	public String formatParamContainerName() {
		return "Root";
	}

	@Override
	public List<ParUniqueElement> getParams() {
		return Collections.emptyList();
	}

	@Override
	public List<ParamContainer> getInnerContainers() {
		List<ParamContainer> list = new ArrayList<>();
		list.addAll(crystals);
		list.addAll(diffractions);

		return list;
	}

	public void registerParameter(ParUniqueElement parUniqueElement) {
		this.parametersCount.set(parametersCount.get() + 1);

		if (parUniqueElement.refinedProperty.getValue()) {
			this.refinedParameters.set(refinedParameters.get() + 1);
		}
		parUniqueElement.refinedProperty.addListener((ov, o, n) -> {
			int inc = 0;
			if (o && !n) {
				inc = -1;
			} else if (!o && n) {
				inc = 1;
			}

			if (inc != 0) {
				this.refinedParameters.set(refinedParameters.get() + inc);
			}

		});
	}

	public void unregisterParameter(ParUniqueElement parUniqueElement) {
		this.parametersCount.set(parametersCount.get() - 1);
		if (parUniqueElement.refinedProperty.get()) {
			this.refinedParameters.set(refinedParameters.get() - 1);
		}

	}

	public void addCrystal(CrystalModel cm) {
		crystals.add(cm);
	}

	public CrystalModel getCrystal(String name) {
		for (CrystalModel cm : crystals) {
			if (name.equals(cm.getName())) {
				return cm;
			}
		}
		return null;
	}

}
