/**
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2013 ForgeRock AS. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-661 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.01.21 at 10:40:04 AM PST 
//


package com.sun.identity.entitlement.xacml3.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MatchType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MatchType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}AttributeValue"/>
 *         &lt;choice>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}AttributeDesignator"/>
 *           &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}AttributeSelector"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="MatchId" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MatchType", propOrder = {
    "attributeValue",
    "attributeDesignator",
    "attributeSelector"
})
public class Match implements XACMLRootElement {

    @XmlElement(name = "AttributeValue", required = true)
    protected AttributeValue attributeValue;
    @XmlElement(name = "AttributeDesignator")
    protected AttributeDesignator attributeDesignator;
    @XmlElement(name = "AttributeSelector")
    protected AttributeSelector attributeSelector;
    @XmlAttribute(name = "MatchId", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String matchId;

    /**
     * Gets the value of the attributeValue property.
     * 
     * @return
     *     possible object is
     *     {@link AttributeValue }
     *     
     */
    public AttributeValue getAttributeValue() {
        return attributeValue;
    }

    /**
     * Sets the value of the attributeValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributeValue }
     *     
     */
    public void setAttributeValue(AttributeValue value) {
        this.attributeValue = value;
    }

    /**
     * Gets the value of the attributeDesignator property.
     * 
     * @return
     *     possible object is
     *     {@link AttributeDesignator }
     *     
     */
    public AttributeDesignator getAttributeDesignator() {
        return attributeDesignator;
    }

    /**
     * Sets the value of the attributeDesignator property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributeDesignator }
     *     
     */
    public void setAttributeDesignator(AttributeDesignator value) {
        this.attributeDesignator = value;
    }

    /**
     * Gets the value of the attributeSelector property.
     * 
     * @return
     *     possible object is
     *     {@link AttributeSelector }
     *     
     */
    public AttributeSelector getAttributeSelector() {
        return attributeSelector;
    }

    /**
     * Sets the value of the attributeSelector property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributeSelector }
     *     
     */
    public void setAttributeSelector(AttributeSelector value) {
        this.attributeSelector = value;
    }

    /**
     * Gets the value of the matchId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMatchId() {
        return matchId;
    }

    /**
     * Sets the value of the matchId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMatchId(String value) {
        this.matchId = value;
    }

    /**
     * Default toXML Method to Marshal Object into XML.
     * @return String - Marshaled Results into XML String.
     */
    public String toXML() {
        StringBuilder stringBuilder = new StringBuilder();



        // Return Marshaled Data.
        return stringBuilder.toString();
    }

}
