package cz.kfkl.mstruct.gui.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamContainer;
import cz.kfkl.mstruct.gui.model.crystals.CrystalModel;
import cz.kfkl.mstruct.gui.model.instrumental.ExcludeXElement;
import cz.kfkl.mstruct.gui.model.instrumental.InstrumentalModel;
import cz.kfkl.mstruct.gui.model.instrumental.PowderPatternElement;
import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.utils.validation.UnexpectedException;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementList;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ObjCrystModel extends XmlLinkedModelElement implements ParamContainer {

	@XmlElementList
	public ObservableList<CrystalModel> crystals = FXCollections.observableArrayList();

	@XmlElementList
	public ObservableList<InstrumentalModel> instruments = FXCollections.observableArrayList();

	public IntegerProperty parametersCount = new SimpleIntegerProperty(0);
	public IntegerProperty refinedParameters = new SimpleIntegerProperty(0);

	private ChangeListener<? super Boolean> refinedParamsListener;

	public ObjCrystModel() {
		refinedParamsListener = (ov, o, n) -> {
			int inc = 0;
			if (o && !n) {
				inc = -1;
			} else if (!o && n) {
				inc = 1;
			}

			if (inc != 0) {
				this.refinedParameters.set(this.refinedParameters.get() + inc);
			}
		};
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
		list.addAll(instruments);

		return list;
	}

	public Set<String> findUsedCrystals() {
		Set<String> usedCrystalNames = new LinkedHashSet<String>();
		for (InstrumentalModel<?> inst : instruments) {
			usedCrystalNames.addAll(inst.findUsedCrystals());
		}
		return usedCrystalNames;
	}

	public void registerParameter(ParUniqueElement parUniqueElement) {
		this.parametersCount.set(parametersCount.get() + 1);

		if (parUniqueElement.refinedProperty.getValue()) {
			this.refinedParameters.set(refinedParameters.get() + 1);
		}
		// no way to find out if the refinedParamsListener has been added before, remove
		// and add prevents it to be registered multiple time
		parUniqueElement.refinedProperty.removeListener(refinedParamsListener);
		parUniqueElement.refinedProperty.addListener(refinedParamsListener);
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

	// TODO if we support multiple Instruments (not only "pattern0") this would be
	// done differently
	public PowderPatternElement getFirstPowderPattern() {
		for (InstrumentalModel instModel : instruments) {
			if (instModel instanceof PowderPatternElement) {
				return (PowderPatternElement) instModel;
			}
		}

		throw new UnexpectedException("An instrumtal PowderPattern model should be created first.");
	}

	public List<ExcludeXElement> getExcludeRegions() {
		return getFirstPowderPattern().excludeRegions;
	}

	public void replaceExcludeRegions(List<ExcludeXElement> newRegions) {
		getFirstPowderPattern().replaceExcludeRegions(newRegions);
	}

}
