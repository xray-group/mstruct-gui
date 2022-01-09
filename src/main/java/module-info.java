module MStructGUI {
	exports cz.kfkl.mstruct.gui.ui.chart;
	exports cz.kfkl.mstruct.gui.utils.validation;
	exports cz.kfkl.mstruct.gui.utils;
	exports cz.kfkl.mstruct.gui.core;
	exports cz.kfkl.mstruct.gui.ui;
	exports cz.kfkl.mstruct.gui.xml.annotation;
	exports cz.kfkl.mstruct.gui.model;
	exports cz.kfkl.mstruct.gui.model.utils;

	requires java.desktop;
	requires java.xml;
	requires javafx.base;
	requires transitive javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires jdom2;
	requires com.google.common;
	requires org.slf4j;
	requires javafx.web;

	opens cz.kfkl.mstruct.gui.ui to javafx.graphics, javafx.fxml, javafx.base;
	opens cz.kfkl.mstruct.gui.model to javafx.graphics, javafx.fxml, javafx.base;
	// opens cz.kfkl.mstruct.gui.test to com.dooapp.fxform, javafx.base;
}