/**
 * 
 */
package org.universAAL.lddi.abstraction.config.data.ont;

import org.universAAL.lddi.abstraction.CommunicationGateway;
import org.universAAL.middleware.owl.ManagedIndividual;

/**
 * Using this class, the set of logical / physical components connected via a {@link CommunicationGateway}
 * can be specified in the configuration file of that {@link CommunicationGateway}. {@link Datapoint}s have
 * to be linked to components by referring to the sequence number defined in this class. Each component must
 * have a type defined in an ontology. If the component corresponds to some physical object, also its
 * location can be specified.
 * 
 * As an example, with an instance of this class you may specify that the component with the sequence id 0
 * is a dimmable multi-color lamp (type) that is in the middle of ceiling of the living room (location).
 * 
 * @author mtazari
 *
 */
public class Component extends ManagedIndividual {

	public static final String MY_URI = CGwDataConfigOntology.NAMESPACE + "Component";

	// class properties
	/**
	 * The sequence number starting with zero, used in configuration files as ID for easier reference
	 * in the specification of {@link Datapoint}s. That is, the first component defined in the
	 * configuration file should have id=0, the second one id=1, .., and the nth one id=n-1.
	 * Therefore the value set for this property must be a non-negative integer number.
	 */
	public static final String PROP_COMPONENT_ID = CGwDataConfigOntology.NAMESPACE + "componentID";
	
	/**
	 * The property for specifying the type of the component at hand by providing the URI of a class defined in an ontology.
	 */
	public static final String PROP_COMPONENT_TYPE_URI = CGwDataConfigOntology.NAMESPACE + "componentTypeURI";
	
	/**
	 * If the component at hand is a physical object like a device, this property can be used to specify its location in terms
	 * of a URI.
	 */
	public static final String PROP_COMPONENT_LOCATION_URI = CGwDataConfigOntology.NAMESPACE + "componentLocationURI";

	public Component() {
		super();
	}

	public Component(String uri) {
		super(uri);
	}

	public String getClassURI() {
		return MY_URI;
	}

	public int getPropSerializationType(String propURI) {
		return PROP_SERIALIZATION_FULL;
	}

	public boolean isWellFormed() {
		return getID()>-1  &&  getTypeURI()!=null;
	}

	// getter / setters

	public int getID() {
		Integer i = (Integer) props.get(PROP_COMPONENT_ID);
		return (i == null) ? -1 : i.intValue();
	}
	
	public String getTypeURI() {
		return (String) props.get(PROP_COMPONENT_TYPE_URI);
	}
	
	public String getLocationURI() {
		return (String) props.get(PROP_COMPONENT_LOCATION_URI);
	}
}
