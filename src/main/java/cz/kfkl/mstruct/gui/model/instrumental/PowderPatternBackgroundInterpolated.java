package cz.kfkl.mstruct.gui.model.instrumental;

import java.util.List;

import org.jdom2.Element;

import cz.kfkl.mstruct.gui.model.OptionUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamTreeNode;
import cz.kfkl.mstruct.gui.model.SingleValueUniqueElement;
import cz.kfkl.mstruct.gui.model.phases.PowderPatternComponentElement;
import cz.kfkl.mstruct.gui.ui.TableOfDoubles;
import cz.kfkl.mstruct.gui.ui.TabularDataParser;
import cz.kfkl.mstruct.gui.ui.instrumental.PowderPatternBackgroundInterpolatedController;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import cz.kfkl.mstruct.gui.utils.BooleanZeroOneStringFormatter;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.xml.XmlUtils;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

@XmlElementName("PowderPatternBackground")
public class PowderPatternBackgroundInterpolated
		extends PowderPatternBackgroundModel<PowderPatternBackgroundInterpolatedController> {

	private static final String FXML_FILE_NAME = "powderPatternBackgroundInterpolated.fxml";

	@XmlUniqueElement
	public OptionUniqueElement interpolationModelOption = new OptionUniqueElement("Interpolation Model", 1, "Linear", "Spline");

	@XmlUniqueElement("XIntensityList")
	public SingleValueUniqueElement xIntensityListElement = new SingleValueUniqueElement("");

	@XmlUniqueElement(isSibling = true)
	public PowderPatternComponentElement powderPatternComponent = new PowderPatternComponentElement(nameProperty);

	public ObservableList<XIntensityListItem> xIntensityList = FXCollections.observableArrayList();

	@Override
	public void bindToElement(Element wrappedElement) {
		super.bindToElement(wrappedElement);

		TabularDataParser parser = new TabularDataParser();
		TableOfDoubles tabularData = parser.parse(xIntensityListElement.valueProperty.get());

		for (double[] row : tabularData.getRows()) {
			XIntensityListItem item = new XIntensityListItem();

			if (row.length > 0) {
				item.x.set(row[0]);
			}

			if (row.length > 1) {
				item.y.set(row[1]);
			}

			if (row.length > 2) {
				// TODO: not very pretty converting string back and forward to double
				item.refinedProperty.set(BooleanZeroOneStringFormatter.parseString(JvStringUtils.toStringNoDotZero(row[2])));
			}

			item.x.addListener(BindingUtils.newChanged(c -> serializeItems()));
			item.y.addListener(BindingUtils.newChanged(c -> serializeItems()));
			item.refinedProperty.addListener(BindingUtils.newChanged(c -> serializeItems()));

			xIntensityList.add(item);
		}

		xIntensityList.addListener((ListChangeListener<? super XIntensityListItem>) c -> serializeItems());
	}

	private void serializeItems() {
		String strValue = serializeList(xIntensityList);
		xIntensityListElement.valueProperty.set(strValue);
	}

	private String serializeList(List<? extends XIntensityListItem> list) {
		StringBuilder sb = new StringBuilder();
		if (!list.isEmpty()) {

			for (XIntensityListItem ri : list) {
				sb.append(XmlUtils.newLineAndIndentString(this.xmlLevel + 2));
				sb.append(JvStringUtils.toStringNoDotZero(ri.x.getValue()));
				sb.append(' ');
				sb.append(JvStringUtils.toStringNoDotZero(ri.y.getValue()));
				sb.append(' ');
				sb.append(BooleanZeroOneStringFormatter.formatString(ri.refinedProperty.get()));
			}

			sb.append(XmlUtils.newLineAndIndentString(this.xmlLevel + 1));
		}

		return sb.toString();
	}

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public PowderPatternBackgroundType getType() {
		return PowderPatternBackgroundType.Interpolated;
	}

	@Override
	public Element getLastOwnedXmlElement() {
		return powderPatternComponent.getLastOwnedXmlElement();
	}

	@Override
	public ObservableList<? extends ParamTreeNode> getChildren() {
		return FXCollections.emptyObservableList();
	}

}
