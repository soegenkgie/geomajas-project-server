/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2012 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.geomajas.layer.feature.attribute;

import java.util.Date;
import java.util.HashMap;

import org.geomajas.annotation.Api;
import org.geomajas.configuration.AssociationType;
import org.geomajas.layer.feature.Attribute;

/**
 * <p>
 * Definition of the many-to-one association attribute. This type of attribute is not a simple primitive attribute with
 * a single value, but instead holds the value of an entire bean. This value has been defined in the form of a
 * {@link AssociationValue}.
 * </p>
 * 
 * @author Pieter De Graef
 * @since 1.6.0
 */
@Api(allMethods = true)
public class ManyToOneAttribute extends AssociationAttribute<AssociationValue> {

	private static final long serialVersionUID = 151L;

	private AssociationValue value;

	/**
	 * Create attribute without value (needed for GWT).
	 */
	public ManyToOneAttribute() {
		super();
	}

	/**
	 * Create attribute with specified value.
	 * 
	 * @param value
	 *            value for attribute
	 */
	public ManyToOneAttribute(AssociationValue value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	public AssociationType getType() {
		return AssociationType.MANY_TO_ONE;
	}

	/**
	 * {@inheritDoc}
	 */
	public AssociationValue getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {
		return value == null || value.getAllAttributes() == null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(AssociationValue value) {
		this.value = value;
	}

	/**
	 * Create a clone of this attribute object.
	 * 
	 * @since 1.7.0
	 * @return A copy of this ManyToOne attribute.
	 */
	public Object clone() { // NOSONAR super.clone() not supported by GWT
		ManyToOneAttribute clone = new ManyToOneAttribute();
		if (value != null) {
			clone.setValue((AssociationValue) value.clone());
		}
		clone.setEditable(isEditable());
		return clone;
	}
	
	/**
	 * Returns a string representation of this attribute's value.
	 *
	 * @return string value
	 * @since 1.9.0
	 * @see AssociationValue#toString()
	 */
	public String toString() {
		return value == null ? "null" : value.toString();
	}
	
	/**
	 * Sets the specified boolean attribute to the specified value.
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @since 1.9.0
	 */
	public void setBooleanAttribute(String name, Boolean value) {
		ensureValue();
		getValue().getAllAttributes().put(name, new BooleanAttribute(value));
	}

	/**
	 * Sets the specified currency attribute to the specified value.
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @since 1.9.0
	 */
	public void setCurrencyAttribute(String name, String value) {
		ensureValue();
		getValue().getAllAttributes().put(name, new CurrencyAttribute(value));
	}

	/**
	 * Sets the specified date attribute to the specified value.
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @since 1.9.0
	 */
	public void setDateAttribute(String name, Date value) {
		ensureValue();
		getValue().getAllAttributes().put(name, new DateAttribute(value));
	}

	/**
	 * Sets the specified double attribute to the specified value.
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @since 1.9.0
	 */
	public void setDoubleAttribute(String name, Double value) {
		ensureValue();
		getValue().getAllAttributes().put(name, new DoubleAttribute(value));
	}

	/**
	 * Sets the specified float attribute to the specified value.
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @since 1.9.0
	 */
	public void setFloatAttribute(String name, Float value) {
		ensureValue();
		getValue().getAllAttributes().put(name, new FloatAttribute(value));
	}

	/**
	 * Sets the specified image URL attribute to the specified value.
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @since 1.9.0
	 */
	public void setImageUrlAttribute(String name, String value) {
		ensureValue();
		getValue().getAllAttributes().put(name, new ImageUrlAttribute(value));
	}
	
	/**
	 * Sets the specified integer attribute to the specified value.
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @since 1.9.0
	 */
	public void setIntegerAttribute(String name, Integer value) {
		ensureValue();
		getValue().getAllAttributes().put(name, new IntegerAttribute(value));
	}
	
	/**
	 * Sets the specified long attribute to the specified value.
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @since 1.9.0
	 */
	public void setLongAttribute(String name, Long value) {
		ensureValue();
		getValue().getAllAttributes().put(name, new LongAttribute(value));
	}

	/**
	 * Sets the specified short attribute to the specified value.
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @since 1.9.0
	 */
	public void setShortAttribute(String name, Short value) {
		ensureValue();
		getValue().getAllAttributes().put(name, new ShortAttribute(value));
	}

	/**
	 * Sets the specified string attribute to the specified value.
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @since 1.9.0
	 */
	public void setStringAttribute(String name, String value) {
		ensureValue();
		getValue().getAllAttributes().put(name, new StringAttribute(value));
	}

	/**
	 * Sets the specified URL attribute to the specified value.
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @since 1.9.0
	 */
	public void setUrlAttribute(String name, String value) {
		ensureValue();
		getValue().getAllAttributes().put(name, new UrlAttribute(value));
	}

	private void ensureValue() {
		if (value == null) {
			value = new AssociationValue(null, new HashMap<String, Attribute<?>>(), false);
		}
	}
}
