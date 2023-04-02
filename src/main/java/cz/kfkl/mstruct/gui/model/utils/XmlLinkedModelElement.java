package cz.kfkl.mstruct.gui.model.utils;

import static cz.kfkl.mstruct.gui.utils.validation.Validator.assertNotBlank;
import static cz.kfkl.mstruct.gui.utils.validation.Validator.assertNotEmpty;
import static cz.kfkl.mstruct.gui.utils.validation.Validator.assertNotNull;
import static cz.kfkl.mstruct.gui.utils.validation.Validator.assertTrue;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import cz.kfkl.mstruct.gui.ui.ObjCrystModel;
import cz.kfkl.mstruct.gui.utils.validation.UnexpectedException;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.XmlUtils;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementList;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementValueProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlMappedSubclasses;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElementKey;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javafx.util.converter.BooleanStringConverter;
import javafx.util.converter.NumberStringConverter;

public class XmlLinkedModelElement {

	private static final Logger LOG = LoggerFactory.getLogger(XmlLinkedModelElement.class);

	protected XmlLinkedModelElement parentModelElement;

	protected Element xmlElement;

	protected List<Content> importedXmlContent;

	protected int xmlLevel = 0;

	public ObjCrystModel rootModel;

	private Collection<Element> ownedSiblings = new ArrayList<>();

	public XmlLinkedModelElement() {
		super();
	}

	/**
	 * parentModelElement is allowed to be null only for the Root model element, i.e
	 * the {@link ObjCrystModel}
	 */
	public void bindToElement(XmlLinkedModelElement parentModelElement, Element wrappedElement) {
		this.parentModelElement = parentModelElement;
		this.rootModel = decideRoot(parentModelElement);
		this.xmlLevel = parentModelElement == null ? 0 : parentModelElement.xmlLevel + 1;
		this.xmlElement = wrappedElement;
		try {

			Content lastElement = null;
			for (Field field : getAllFields(this.getClass())) {
				XmlAttributeProperty propAnnotation = field.getAnnotation(XmlAttributeProperty.class);
				if (propAnnotation != null) {
					assertPublicOrProtected(field);
					bindAttribute(field, decideAttributeName(field, propAnnotation.value()), propAnnotation.converter());
				}
				XmlElementValueProperty elementValueAnnotation = field.getAnnotation(XmlElementValueProperty.class);
				if (elementValueAnnotation != null) {
					assertPublicOrProtected(field);
					bindElementContent(field, elementValueAnnotation.converter());
				}

				XmlUniqueElementKey uniqueKeyAttributeAnnotation = field.getAnnotation(XmlUniqueElementKey.class);
				if (uniqueKeyAttributeAnnotation != null) {
					assertPublicOrProtected(field);
					bindUniqueKeyAttribute(field, decideAttributeName(field, uniqueKeyAttributeAnnotation.value()));
				}

				XmlUniqueElement uniqueElementAnnotation = field.getAnnotation(XmlUniqueElement.class);
				if (uniqueElementAnnotation != null) {
					assertPublicOrProtected(field);
					Object fieldValue = getFieldValueNotNull(field);

					String elementName = decideUniqueElementName(uniqueElementAnnotation, field, fieldValue.getClass());

					Map<String, String> keyAtributeValues = findKeyAttributeValues(field, fieldValue, elementName);

					XmlIndentingStyle indentStyle = null;
					if (fieldValue instanceof XmlLinkedModelElement) {
						indentStyle = ((XmlLinkedModelElement) fieldValue).getXmlIndentingStyle();
					}

					Element childEl = null;
					if (uniqueElementAnnotation.isSibling()) {
						childEl = parentModelElement.findOrCreateUniqueElement(elementName, keyAtributeValues, xmlElement,
								indentStyle);
						ownedSiblings.add(childEl);
					} else {
						childEl = findOrCreateUniqueElement(elementName, keyAtributeValues, lastElement, indentStyle);
					}
					lastElement = childEl;

					if (fieldValue instanceof XmlLinkedModelElement) {
						XmlLinkedModelElement fieldValueAsXmlLinked = (XmlLinkedModelElement) fieldValue;
//						if (fieldValueAsXmlLinked.isNew()) {
//							setKeyAttributeValues(field, fieldValue, keyAtributeValues, elementName);
//						}

						fieldValueAsXmlLinked.bindToElement(this, childEl);

						lastElement = fieldValueAsXmlLinked.getLastOwnedXmlElement();
					}

				}

				XmlElementList elementListAnnotation = field.getAnnotation(XmlElementList.class);
				if (elementListAnnotation != null) {
					assertPublicOrProtected(field);

					Class<?> genericTypeClass = assertFieldTypeAndGetSingleTypeArgument(field, List.class);

					Map<String, Class<?>> mappedClasses = findMappedClasses(field, genericTypeClass);

					Content previousFieldLastXmlEl = lastElement;
					Element firstListElement = null;

					List<Object> list = getFieldValueAsList(field);
					for (Element child : xmlElement.getChildren()) {
						String elementName = child.getName();
						Class<?> mappedClass = mappedClasses.get(elementName);
						if (mappedClasses.get(elementName) != null) {
							lastElement = child;
							if (firstListElement == null) {
								firstListElement = child;
							}
							Object newInstance = null;
							try {
								Constructor<?> noArgsConstructor = mappedClass.getConstructor();
								newInstance = noArgsConstructor.newInstance();

								list.add(newInstance);

							} catch (NoSuchMethodException | InstantiationException | IllegalArgumentException
									| InvocationTargetException e) {
								throw new UnexpectedException(e, "Failed to find or invoke no argument constructor on [%s]",
										mappedClass);
							}

							if (newInstance instanceof XmlLinkedModelElement) {
								XmlLinkedModelElement fieldValueAsXmlLinked = (XmlLinkedModelElement) newInstance;
								fieldValueAsXmlLinked.bindToElement(this, child);

								lastElement = fieldValueAsXmlLinked.getLastOwnedXmlElement();
							}
						}

					}

					if (list instanceof ObservableList) {
						Content marker = new Text("");
						if (firstListElement == null) {
							XmlUtils.addContentAfter(this.xmlElement, List.of(marker), previousFieldLastXmlEl);
							lastElement = marker;
						} else {
							List<Content> firstElContent = XmlUtils.elementWithPreceedingBallast(this.xmlElement,
									firstListElement);
							assertNotEmpty(firstElContent, "The element [{}] should be a child of [{}]", firstListElement,
									this.xmlElement);
							firstElContent.get(0);

							XmlUtils.addContentBefore(this.xmlElement, List.of(marker), firstElContent.get(0));
						}
						XmlLinkedObservableListListener listener = new XmlLinkedObservableListListener(this, marker);
						((ObservableList) list).addListener(listener);
					}
				}
			}
		} catch (IllegalAccessException iae) {
			throw new UnexpectedException(iae, "Exception when processing fields of class [%s]", this.getClass());
		}
	}

	protected ObjCrystModel decideRoot(XmlLinkedModelElement parentModelElement) {
		assertNotNull(parentModelElement,
				"The [%s] is created incorrectly. The parentModelElement is allowed to be null only for the Root model element",
				this);
		return parentModelElement.rootModel;
	}

	private List<Object> getFieldValueAsList(Field field) throws IllegalAccessException {
		Object fieldValue = getFieldValueNotNull(field);
		assertTrue(fieldValue instanceof List, "Field [%s] value [%s] should be an instance of List", field, fieldValue);
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) fieldValue;
		return list;
	}

	private Class<?> assertFieldTypeAndGetSingleTypeArgument(Field field, Class<List> expectedType) {
		Class<?> genericTypeClass = null;
		AnnotatedType at = field.getAnnotatedType();
		Type type = at.getType();
		LOG.trace("Examining type [{}]", type);
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			if (pt.getRawType() instanceof Class) {

				if (pt.getRawType() instanceof Class && expectedType.isAssignableFrom((Class<?>) pt.getRawType())) {
					Type[] actualTypeArguments = pt.getActualTypeArguments();
					assertTrue(actualTypeArguments.length == 1,
							"The field [%s] which is of type List should have one type argument, got [%s]", field,
							actualTypeArguments.length);
					Type ta = actualTypeArguments[0];
					if (ta instanceof Class) {
						genericTypeClass = (Class<?>) ta;
					} else if (ta instanceof ParameterizedType) {
						ParameterizedType pta = (ParameterizedType) ta;
						genericTypeClass = (Class<?>) pta.getRawType();
					}
				}
			}
		}

		assertNotNull(genericTypeClass, "The  field [%s] should be of type [%s] with single generic type argument defined.",
				field, expectedType);
		return genericTypeClass;
	}

	private Element findOrCreateUniqueElement(String elementName, Map<String, String> keyAtributeValues, Content lastXmlContent,
			XmlIndentingStyle indentStyle) {
		String uniqueElementSearchCondition = createUniqueElementSearchCondition(elementName, keyAtributeValues);
		XPathFactory xpf = XPathFactory.instance();
		XPathExpression<Element> expr = xpf.compile(uniqueElementSearchCondition, Filters.element());

		List<Element> list = expr.evaluate(xmlElement);
		if (list.size() > 1) {
			// TODO report as warning
		}

		Element uniqueEl = list.isEmpty() ? null : list.get(0);

		if (uniqueEl == null) {
			uniqueEl = new Element(elementName);
//			for (Entry<String, String> attValueEntry : keyAtributeValues.entrySet()) {
//				uniqueEl.setAttribute(attValueEntry.getKey(), attValueEntry.getValue());
//			}
			addAfter(xmlElement, uniqueEl, lastXmlContent, indentStyle);
		}
		return uniqueEl;
	}

	private List<Field> getAllFields(Class<?> cls) {
		List<Field> fields = new ArrayList<>();

		while (!cls.equals(Object.class)) {
			fields.addAll(0, List.of(cls.getDeclaredFields()));
			cls = cls.getSuperclass();
		}

		return fields;
	}

	private String createUniqueElementSearchCondition(String elementName, Map<String, String> keyAtributeValues) {
		StringBuilder sb = new StringBuilder(elementName);

		if (!keyAtributeValues.isEmpty()) {
			sb.append("[");
			Joiner joiner = Joiner.on(" and ");
			joiner.appendTo(sb, keyAtributeValues.entrySet().stream()
					.map((Entry<String, String> e) -> "@" + e.getKey() + "='" + e.getValue() + "'").iterator());
			sb.append("]");
		}

		String uniqueElementSearchPattern = sb.toString();
		return uniqueElementSearchPattern;

	}

	private Map<String, String> findKeyAttributeValues(Field field, Object fieldValue, String elementName)
			throws IllegalAccessException {
		Map<String, String> keyAtributeValues = new LinkedHashMap<>();
		for (Field typeField : getAllFields(fieldValue.getClass())) {
			if (isPublicOrProtected(typeField)) {
				XmlUniqueElementKey keyFieldAnnotation = typeField.getAnnotation(XmlUniqueElementKey.class);
				if (keyFieldAnnotation != null) {
					String keyAttName = keyFieldAnnotation.value();
					if (Strings.isNullOrEmpty(keyAttName)) {
						keyAttName = typeField.getName();
					}

					Object valueObj = typeField.get(fieldValue);
					assertNotNull(valueObj,
							"Field [%s] on object [%s] must be set by default as it is used as a XmlUniqueElementKey.", typeField,
							fieldValue);

					String keyValue = null;
					if (valueObj instanceof String) {
						keyValue = (String) valueObj;
					} else if (valueObj instanceof StringProperty) {
						keyValue = ((StringProperty) valueObj).getValue();
						assertNotNull(keyValue,
								"The string property field [%s] on object [%s] must be set as it is used as a XmlUniqueElementKey.",
								typeField, fieldValue);
					} else {
						throw new UnexpectedException(
								"XmlUniqueElementKey can be used only on field [%s] with type String or StringProperty but was [%s].",
								typeField, fieldValue.getClass());
					}

					keyAtributeValues.put(keyAttName, keyValue);
				}
			}
		}
		return keyAtributeValues;
	}

//	private boolean isNew() {
//		return xmlElement == null;
//	}
//
//	private void setKeyAttributeValues(Field field, Object fieldValue, Map<String, String> keyAtributeValues, String elementName)
//			throws IllegalAccessException {
//		for (Field typeField : getAllFields(fieldValue.getClass())) {
//			assertPublicOrProtected(typeField);
//			XmlUniqueElementKey keyFieldAnnotation = typeField.getAnnotation(XmlUniqueElementKey.class);
//			if (keyFieldAnnotation != null) {
//				String keyAttName = keyFieldAnnotation.value();
//				if (Strings.isNullOrEmpty(elementName)) {
//					keyAttName = field.getName();
//				}
//
//				typeField.set(fieldValue, keyAtributeValues.get(keyAttName));
//			}
//		}
//	}

	private void assertPublicOrProtected(Field field) {
		assertTrue(isPublicOrProtected(field), "Field [%s] should be public or protected.", field);
	}

	private boolean isPublicOrProtected(Field field) {
		return Modifier.isPublic(field.getModifiers()) || Modifier.isProtected(field.getModifiers());
	}

	private String decideUniqueElementName(XmlUniqueElement uniqueElementAnnotation, Field field,
			Class<? extends Object> fieldValueClass) {
		String elementName = uniqueElementAnnotation.value();
		if (Strings.isNullOrEmpty(elementName)) {
			elementName = decideElementName(fieldValueClass);
		}

		assertNotBlank(elementName, "Element name was evaluated to a blank string which is not valid. Class [%s], field [%s]",
				fieldValueClass.getClass(), field);
		return elementName;
	}

	private Map<String, Class<?>> findMappedClasses(Field field, Class<? extends Object> genericTypeClass) {
		Map<String, Class<?>> map = new LinkedHashMap<>();

		XmlMappedSubclasses mappedSubclasses = genericTypeClass.getAnnotation(XmlMappedSubclasses.class);
		if (mappedSubclasses == null) {
			map.put(decideElementName(genericTypeClass), genericTypeClass);
		} else {
			for (Class<?> subClass : mappedSubclasses.value()) {
				map.put(decideElementName(subClass), subClass);
			}
		}
		return map;
	}

	public static String decideElementName(Class<? extends Object> elementClass) {
		String elementName = null;
		XmlElementName typeAnnotation = elementClass.getAnnotation(XmlElementName.class);
		if (typeAnnotation != null) {
			elementName = typeAnnotation.value();
		}
		if (Strings.isNullOrEmpty(elementName)) {
			elementName = elementClass.getSimpleName();
		}

		return elementName;
	}

	private void bindUniqueKeyAttribute(Field field, String attributeName) throws IllegalAccessException {
		Object fieldValue = field.get(this);

		if (fieldValue instanceof StringProperty) {
			XmlAttributeUpdater<String> updater = new XmlAttributeUpdater<>(xmlElement, attributeName);
			StringProperty fieldValueSp = (StringProperty) fieldValue;
			fieldValueSp.addListener(updater);
		} else if (fieldValue instanceof String) {
			xmlElement.setAttribute(attributeName, (String) fieldValue);
		}
	}

	private void bindAttribute(Field field, String attributeName, Class<?> converter) throws IllegalAccessException {
		bindXmlValue(field, getFieldValueNotNull(field), new XmlAttributeUpdater<String>(xmlElement, attributeName), converter);
	}

	private void bindElementContent(Field field, Class<?> converter) throws IllegalAccessException {
		bindXmlValue(field, getFieldValueNotNull(field), new XmlElementContentUpdater<String>(xmlElement), converter);
	}

	private void bindXmlValue(Field field, Object fieldValue, XmlValueUpdater<String> updater, Class<?> converter) {
		StringConverter converterInstance = instantiateConverter(converter);

		if (converterInstance == null) {
			if (fieldValue instanceof StringProperty) {

			} else if (fieldValue instanceof DoubleProperty) {
				// TODO: maybe need special converter ? (Number d) ->
				// Double.toString(d.doubleValue())));
				converterInstance = new NumberStringConverter();
			} else if (fieldValue instanceof IntegerProperty) {
				// TODO: maybe need special converter? (Number d) ->
				// Integer.toString(d.intValue())));
				converterInstance = new NumberStringConverter();
			} else if (fieldValue instanceof BooleanProperty) {
				converterInstance = new BooleanStringConverter();
			} else {
				throw new UnexpectedException(
						"Field [%s] type [%s] is not mappable, no covereter found or can be decided for Property type..", field,
						fieldValue.getClass());
			}
		}
		if (fieldValue instanceof Property<?>) {
			updater.bind((Property) fieldValue, converterInstance);
		} else {
			throw new UnexpectedException("Field [%s] with type [%s] should be a property.", field, fieldValue.getClass());
		}
	}

	private StringConverter<?> instantiateConverter(Class<?> converter) {
		StringConverter<?> converterInstance = null;
		if (!StringConverter.class.equals(converter)) {
			Constructor<?> noArgsConstructor;
			try {
				noArgsConstructor = converter.getConstructor();
				Object converterInstanceObj = noArgsConstructor.newInstance();
				if (converterInstanceObj instanceof StringConverter) {
					converterInstance = (StringConverter) converterInstanceObj;
				} else {
					throw new UnexpectedException("Class [%s] is not an instance of StringConverter.", converter);
				}
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				throw new UnexpectedException(e, "Cannot instantiate coverter calss [%s] using default constructor.", converter);
			}

		}
		return converterInstance;
	}

	private Object getFieldValueNotNull(Field field) throws IllegalAccessException {
		Object fieldValue = field.get(this);
		assertNotNull(fieldValue, "Field [%s] on class [%s] must be initialized.", field.getName(), field.getDeclaringClass());
		return fieldValue;
	}

	private String decideAttributeName(Field field, String annotationValue) {
		String attributeName = annotationValue;
		if (Strings.isNullOrEmpty(attributeName)) {
			attributeName = field.getName();
		}

		assertNotBlank(attributeName, "Attribute was evaluated to a blank string which is not valid. Field [%s]", field);
		return attributeName;
	}

	public <T extends XmlLinkedModelElement> void bindAndAddAfter(T addedItem, Content addAfterEl) {
		XmlIndentingStyle indentStyle = addedItem.getXmlIndentingStyle();

		Element newElement = null;
		// the "content" is set for elements imported from another file and will contain
		// the xml element and the "Preceding Ballast"
		List<Content> content = addedItem.getImportedXmlContent();
		if (content == null) {
			newElement = new Element(XmlLinkedModelElement.decideElementName(addedItem.getClass()));
			addAfter(xmlElement, newElement, addAfterEl, indentStyle);
		} else {
			// if the addedItem is imported from another XML the getXmlElement will be set
			newElement = addedItem.getXmlElement();
			XmlUtils.addContentAfter(xmlElement, content, addAfterEl);
		}

		addedItem.bindToElement(this, newElement);
	}

	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.MULTILINE;
	}

	public <T extends XmlLinkedModelElement> void addAfter(Element parentEl, Element newEl, Content addAfterContent,
			XmlIndentingStyle indentStyle) {
		XmlUtils.addNewElementAfter(parentEl, newEl, addAfterContent, xmlLevel + 1, indentStyle);
	}

	public int getXmlLevel() {
		return xmlLevel;
	}

	public XmlLinkedModelElement getParentModelElement() {
		return parentModelElement;
	}

	public Element getXmlElement() {
		return xmlElement;
	}

	public void setXmlElement(Element xmlElement) {
		this.xmlElement = xmlElement;
	}

	public Element getLastOwnedXmlElement() {
		return xmlElement;
	}

	List<Content> getImportedXmlContent() {
		return importedXmlContent;
	}

	public void setImportedXmlContent(List<Content> importedXmlContent) {
		this.importedXmlContent = importedXmlContent;
	}

	public Collection<Element> getOwnedSiblings() {
		return ownedSiblings;
	}

}
