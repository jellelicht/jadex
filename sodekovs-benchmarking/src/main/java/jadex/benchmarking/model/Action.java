//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.05 at 05:33:24 PM CET 
//


package jadex.benchmarking.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}Properties"/>
 *       &lt;/sequence>
 *       &lt;attribute name="componentname" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="componenttype" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="componentmodel" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="numberOfComponents" type="{http://www.w3.org/2001/XMLSchema}int" default="1" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "properties"
})
@XmlRootElement(name = "Action")
public class Action {

    @XmlElement(name = "Properties", required = true)
    protected Properties properties;
    @XmlAttribute(name = "componentname", required = true)
    protected String componentname;
    @XmlAttribute(name = "componenttype", required = true)
    protected String componenttype;
    @XmlAttribute(name = "componentmodel", required = true)
    protected String componentmodel;
    @XmlAttribute(name = "numberOfComponents")
    protected Integer numberOfComponents;

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link Properties }
     *     
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link Properties }
     *     
     */
    public void setProperties(Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the componentname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComponentname() {
        return componentname;
    }

    /**
     * Sets the value of the componentname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComponentname(String value) {
        this.componentname = value;
    }

    /**
     * Gets the value of the componenttype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComponenttype() {
        return componenttype;
    }

    /**
     * Sets the value of the componenttype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComponenttype(String value) {
        this.componenttype = value;
    }

    /**
     * Gets the value of the componentmodel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComponentmodel() {
        return componentmodel;
    }

    /**
     * Sets the value of the componentmodel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComponentmodel(String value) {
        this.componentmodel = value;
    }

    /**
     * Gets the value of the numberOfComponents property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getNumberOfComponents() {
        if (numberOfComponents == null) {
            return  1;
        } else {
            return numberOfComponents;
        }
    }

    /**
     * Sets the value of the numberOfComponents property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberOfComponents(Integer value) {
        this.numberOfComponents = value;
    }

}
