//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.05 at 05:33:24 PM CET 
//


package jadex.benchmarking.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *         &lt;element name="TerminateCondition">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{}TerminationTime"/>
 *                   &lt;element ref="{}SemanticCondition"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SytemUnderTest">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{}Properties"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Sequences">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{}Sequence" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="scaleFactor" type="{http://www.w3.org/2001/XMLSchema}double" default="1.0" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Evaluation">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{}Dataproviders"/>
 *                   &lt;element ref="{}Dataconsumers"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{}Imports"/>
 *         &lt;element ref="{}AdaptationAnalysis"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="Workload"/>
 *             &lt;enumeration value="Faultload"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="warmUpTime" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "terminateCondition",
    "sytemUnderTest",
    "sequences",
    "evaluation",
    "imports",
    "adaptationAnalysis"
})
@XmlRootElement(name = "Schedule")
public class Schedule {

    @XmlElement(name = "TerminateCondition", required = true)
    protected Schedule.TerminateCondition terminateCondition;
    @XmlElement(name = "SytemUnderTest", required = true)
    protected Schedule.SytemUnderTest sytemUnderTest;
    @XmlElement(name = "Sequences", required = true)
    protected Schedule.Sequences sequences;
    @XmlElement(name = "Evaluation", required = true)
    protected Schedule.Evaluation evaluation;
    @XmlElement(name = "Imports", required = true)
    protected Imports imports;
    @XmlElement(name = "AdaptationAnalysis", required = true)
    protected AdaptationAnalysis adaptationAnalysis;
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "warmUpTime")
    protected Long warmUpTime;

    /**
     * Gets the value of the terminateCondition property.
     * 
     * @return
     *     possible object is
     *     {@link Schedule.TerminateCondition }
     *     
     */
    public Schedule.TerminateCondition getTerminateCondition() {
        return terminateCondition;
    }

    /**
     * Sets the value of the terminateCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link Schedule.TerminateCondition }
     *     
     */
    public void setTerminateCondition(Schedule.TerminateCondition value) {
        this.terminateCondition = value;
    }

    /**
     * Gets the value of the sytemUnderTest property.
     * 
     * @return
     *     possible object is
     *     {@link Schedule.SytemUnderTest }
     *     
     */
    public Schedule.SytemUnderTest getSytemUnderTest() {
        return sytemUnderTest;
    }

    /**
     * Sets the value of the sytemUnderTest property.
     * 
     * @param value
     *     allowed object is
     *     {@link Schedule.SytemUnderTest }
     *     
     */
    public void setSytemUnderTest(Schedule.SytemUnderTest value) {
        this.sytemUnderTest = value;
    }

    /**
     * Gets the value of the sequences property.
     * 
     * @return
     *     possible object is
     *     {@link Schedule.Sequences }
     *     
     */
    public Schedule.Sequences getSequences() {
        return sequences;
    }

    /**
     * Sets the value of the sequences property.
     * 
     * @param value
     *     allowed object is
     *     {@link Schedule.Sequences }
     *     
     */
    public void setSequences(Schedule.Sequences value) {
        this.sequences = value;
    }

    /**
     * Gets the value of the evaluation property.
     * 
     * @return
     *     possible object is
     *     {@link Schedule.Evaluation }
     *     
     */
    public Schedule.Evaluation getEvaluation() {
        return evaluation;
    }

    /**
     * Sets the value of the evaluation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Schedule.Evaluation }
     *     
     */
    public void setEvaluation(Schedule.Evaluation value) {
        this.evaluation = value;
    }

    /**
     * Gets the value of the imports property.
     * 
     * @return
     *     possible object is
     *     {@link Imports }
     *     
     */
    public Imports getImports() {
        return imports;
    }

    /**
     * Sets the value of the imports property.
     * 
     * @param value
     *     allowed object is
     *     {@link Imports }
     *     
     */
    public void setImports(Imports value) {
        this.imports = value;
    }

    /**
     * Gets the value of the adaptationAnalysis property.
     * 
     * @return
     *     possible object is
     *     {@link AdaptationAnalysis }
     *     
     */
    public AdaptationAnalysis getAdaptationAnalysis() {
        return adaptationAnalysis;
    }

    /**
     * Sets the value of the adaptationAnalysis property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdaptationAnalysis }
     *     
     */
    public void setAdaptationAnalysis(AdaptationAnalysis value) {
        this.adaptationAnalysis = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the warmUpTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getWarmUpTime() {
        return warmUpTime;
    }

    /**
     * Sets the value of the warmUpTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setWarmUpTime(Long value) {
        this.warmUpTime = value;
    }


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
     *         &lt;element ref="{}Dataproviders"/>
     *         &lt;element ref="{}Dataconsumers"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "dataproviders",
        "dataconsumers"
    })
    public static class Evaluation {

        @XmlElement(name = "Dataproviders", required = true)
        protected Dataproviders dataproviders;
        @XmlElement(name = "Dataconsumers", required = true)
        protected Dataconsumers dataconsumers;

        /**
         * Gets the value of the dataproviders property.
         * 
         * @return
         *     possible object is
         *     {@link Dataproviders }
         *     
         */
        public Dataproviders getDataproviders() {
            return dataproviders;
        }

        /**
         * Sets the value of the dataproviders property.
         * 
         * @param value
         *     allowed object is
         *     {@link Dataproviders }
         *     
         */
        public void setDataproviders(Dataproviders value) {
            this.dataproviders = value;
        }

        /**
         * Gets the value of the dataconsumers property.
         * 
         * @return
         *     possible object is
         *     {@link Dataconsumers }
         *     
         */
        public Dataconsumers getDataconsumers() {
            return dataconsumers;
        }

        /**
         * Sets the value of the dataconsumers property.
         * 
         * @param value
         *     allowed object is
         *     {@link Dataconsumers }
         *     
         */
        public void setDataconsumers(Dataconsumers value) {
            this.dataconsumers = value;
        }

    }


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
     *         &lt;element ref="{}Sequence" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *       &lt;attribute name="scaleFactor" type="{http://www.w3.org/2001/XMLSchema}double" default="1.0" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "sequence"
    })
    public static class Sequences {

        @XmlElement(name = "Sequence", required = true)
        protected List<Sequence> sequence;
        @XmlAttribute(name = "scaleFactor")
        protected Double scaleFactor;

        /**
         * Gets the value of the sequence property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the sequence property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSequence().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Sequence }
         * 
         * 
         */
        public List<Sequence> getSequence() {
            if (sequence == null) {
                sequence = new ArrayList<Sequence>();
            }
            return this.sequence;
        }

        /**
         * Gets the value of the scaleFactor property.
         * 
         * @return
         *     possible object is
         *     {@link Double }
         *     
         */
        public double getScaleFactor() {
            if (scaleFactor == null) {
                return  1.0D;
            } else {
                return scaleFactor;
            }
        }

        /**
         * Sets the value of the scaleFactor property.
         * 
         * @param value
         *     allowed object is
         *     {@link Double }
         *     
         */
        public void setScaleFactor(Double value) {
            this.scaleFactor = value;
        }

    }


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
    public static class SytemUnderTest {

        @XmlElement(name = "Properties", required = true)
        protected Properties properties;

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

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;choice>
     *         &lt;element ref="{}TerminationTime"/>
     *         &lt;element ref="{}SemanticCondition"/>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "terminationTime",
        "semanticCondition"
    })
    public static class TerminateCondition {

        @XmlElement(name = "TerminationTime")
        protected TerminationTime terminationTime;
        @XmlElement(name = "SemanticCondition")
        protected SemanticCondition semanticCondition;

        /**
         * Gets the value of the terminationTime property.
         * 
         * @return
         *     possible object is
         *     {@link TerminationTime }
         *     
         */
        public TerminationTime getTerminationTime() {
            return terminationTime;
        }

        /**
         * Sets the value of the terminationTime property.
         * 
         * @param value
         *     allowed object is
         *     {@link TerminationTime }
         *     
         */
        public void setTerminationTime(TerminationTime value) {
            this.terminationTime = value;
        }

        /**
         * Gets the value of the semanticCondition property.
         * 
         * @return
         *     possible object is
         *     {@link SemanticCondition }
         *     
         */
        public SemanticCondition getSemanticCondition() {
            return semanticCondition;
        }

        /**
         * Sets the value of the semanticCondition property.
         * 
         * @param value
         *     allowed object is
         *     {@link SemanticCondition }
         *     
         */
        public void setSemanticCondition(SemanticCondition value) {
            this.semanticCondition = value;
        }

    }

}
