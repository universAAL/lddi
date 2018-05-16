/**
 * 
 */
package org.universAAL.lddi.abstraction.config.data.ont;

import org.universAAL.middleware.owl.DataRepOntology;
import org.universAAL.middleware.owl.DatatypePropertySetup;
import org.universAAL.middleware.owl.ManagedIndividual;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.owl.ObjectPropertySetup;
import org.universAAL.middleware.owl.OntClassInfoSetup;
import org.universAAL.middleware.owl.Ontology;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.rdf.TypeMapper;
import org.universAAL.middleware.xsd.NonNegativeInteger;
import org.universAAL.ontology.phThing.PhThingOntology;

/**
 * @author mtazari
 *
 */
public class CGwDataConfigOntology extends Ontology {

	private static CGwDataConfigOntFactory factory = new CGwDataConfigOntFactory();
	public static final String NAMESPACE = "http://ontology.universAAL.org/lddi/gateway/DataConfig.owl#";

	public CGwDataConfigOntology(String ontURI) {
		super(ontURI);
	}

	public CGwDataConfigOntology() {
		super(NAMESPACE);
	}

	// This is where you actually define the relationships and restrictions
	public void create() {
		// info about this ontology
		Resource r = getInfo();
		r.setResourceComment("The ontology for the configuration data related to the external datapoints to which a concrete communication gateway has access.");
		r.setResourceLabel("CGw Data Config Ontology");
		
		// imported ontologies
		addImport(DataRepOntology.NAMESPACE);
		addImport(PhThingOntology.NAMESPACE);

		// Getting prepared for the ont spec
		OntClassInfoSetup oci;
		DatatypePropertySetup dps;
		ObjectPropertySetup ops;

		// adding class "component"
		oci = createNewOntClassInfo(Component.MY_URI, factory, 0);
		oci.setResourceComment("Using this class, the set of logical / physical components connected via a CommunicationGateway can be specified in the configuration file of that CommunicationGateway. Datapoints have to be linked to components by referring to the sequence number defined in this class. Each component must have a type defined in an ontology. If the component corresponds to some physical object, also its location can be specified.");
		oci.setResourceLabel("Component");
		oci.addSuperClass(ManagedIndividual.MY_URI);
		// adding prop "id"
		dps = oci.addDatatypeProperty(Component.PROP_COMPONENT_ID);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		dps.setFunctional();
		oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(Component.PROP_COMPONENT_ID,
				TypeMapper.getDatatypeURI(NonNegativeInteger.class), 1, 1));
		// adding prop "type uri"
		dps = oci.addDatatypeProperty(Component.PROP_COMPONENT_TYPE_URI);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		dps.setFunctional();
		oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(Component.PROP_COMPONENT_TYPE_URI,
				TypeMapper.getDatatypeURI(Resource.class), 1, 1));
		// adding prop "location uri"
		dps = oci.addDatatypeProperty(Component.PROP_COMPONENT_LOCATION_URI);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		dps.setFunctional();
		oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(Component.PROP_COMPONENT_LOCATION_URI,
				TypeMapper.getDatatypeURI(Resource.class), 0, 1));

		// adding class "data-point value-type"
		oci = createNewOntClassInfo(DatapointValueType.MY_URI, factory, 1);
		oci.setResourceComment("This class helps in the configuration file of a CommunicationGateway to specify the type of the possible values for each external data-point that is made accessible through the corresponding gateway. Because there may be several data-points with the same value type, it makes sense that the value types are specified separately, by assigning a sequence number to them as their ID. Then in the specification of data-points, the type can be specified by referring to the related sequence number of the corresponding type described separately.");
		oci.setResourceLabel("Value Type");
		oci.addSuperClass(ManagedIndividual.MY_URI);
		// adding prop "id"
		dps = oci.addDatatypeProperty(DatapointValueType.PROP_VT_ID);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		dps.setFunctional();
		oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(DatapointValueType.PROP_VT_ID,
				TypeMapper.getDatatypeURI(NonNegativeInteger.class), 1, 1));
		// adding prop "base type"
		ops = oci.addObjectProperty(DatapointValueType.PROP_BASE_TYPE);
		ops.getProperty().setResourceComment("");
		ops.getProperty().setResourceLabel("");
		ops.setFunctional();
		oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(DatapointValueType.PROP_BASE_TYPE, SimpleType.MY_URI, 1, 1));
		// adding prop "enum values"
		dps = oci.addDatatypeProperty(DatapointValueType.PROP_ENUM_VALUES);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		// adding prop "value format"
		dps = oci.addDatatypeProperty(DatapointValueType.PROP_VALUE_FORMAT);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		dps.setFunctional();
		oci.addRestriction(MergedRestriction.getAllValuesRestriction(DatapointValueType.PROP_VALUE_FORMAT,
				TypeMapper.getDatatypeURI(String.class)));
		// adding prop "min value"
		dps = oci.addDatatypeProperty(DatapointValueType.PROP_VALUE_LOWERLIMIT);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		dps.setFunctional();
		// adding prop "value step"
		dps = oci.addDatatypeProperty(DatapointValueType.PROP_VALUE_STEP);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		dps.setFunctional();
		oci.addRestriction(MergedRestriction.getAllValuesRestriction(DatapointValueType.PROP_VALUE_FORMAT,
				TypeMapper.getDatatypeURI(Float.class)));
		// adding prop "max value"
		dps = oci.addDatatypeProperty(DatapointValueType.PROP_VALUE_UPPERLIMIT);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		dps.setFunctional();

		// adding class "data-point"
		oci = createNewOntClassInfo(Datapoint.MY_URI, factory, 2);
		oci.setResourceComment("This class helps in the configuration file of a CommunicationGateway to specify each external data-point that is made accessible through the corresponding gateway.");
		oci.setResourceLabel("Data Point");
		oci.addSuperClass(ManagedIndividual.MY_URI);
		// adding prop "id"
		dps = oci.addDatatypeProperty(Datapoint.PROP_DP_ID);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		dps.setFunctional();
		oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(Datapoint.PROP_DP_ID,
				TypeMapper.getDatatypeURI(NonNegativeInteger.class), 1, 1));
		// adding prop "type id"
		dps = oci.addDatatypeProperty(Datapoint.PROP_VALUE_TYPE);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		dps.setFunctional();
		oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(Datapoint.PROP_VALUE_TYPE,
				TypeMapper.getDatatypeURI(NonNegativeInteger.class), 1, 1));
		// adding prop "component id"
		dps = oci.addDatatypeProperty(Datapoint.PROP_BELONGS_TO);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		dps.setFunctional();
		oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(Datapoint.PROP_BELONGS_TO,
				TypeMapper.getDatatypeURI(NonNegativeInteger.class), 1, 1));
		// adding prop "related ont property"
		dps = oci.addDatatypeProperty(Datapoint.PROP_RELATED_ONT_PROPERTY);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		dps.setFunctional();
		oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(Datapoint.PROP_RELATED_ONT_PROPERTY,
				TypeMapper.getDatatypeURI(Resource.class), 1, 1));
		// adding prop "pull address"
		dps = oci.addDatatypeProperty(Datapoint.PROP_PULL_ADDRESS);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		dps.setFunctional();
		oci.addRestriction(MergedRestriction.getAllValuesRestriction(Datapoint.PROP_PULL_ADDRESS,
				TypeMapper.getDatatypeURI(String.class)));
		// adding prop "push address"
		dps = oci.addDatatypeProperty(Datapoint.PROP_PUSH_ADDRESS);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		dps.setFunctional();
		oci.addRestriction(MergedRestriction.getAllValuesRestriction(Datapoint.PROP_PUSH_ADDRESS,
				TypeMapper.getDatatypeURI(String.class)));
		// adding prop "value step"
		dps = oci.addDatatypeProperty(Datapoint.PROP_SET_ADDRESS);
		dps.getProperty().setResourceComment("");
		dps.getProperty().setResourceLabel("");
		dps.setFunctional();
		oci.addRestriction(MergedRestriction.getAllValuesRestriction(Datapoint.PROP_SET_ADDRESS,
				TypeMapper.getDatatypeURI(String.class)));
		
		// adding class "simple type"
		oci = createNewOntClassInfo(SimpleType.MY_URI, factory, 3);
		oci.setResourceComment("");
		oci.setResourceLabel("Simple Type");
		oci.addSuperClass(ManagedIndividual.MY_URI);
		oci.toEnumeration(SimpleType.allTypes());

	}

}
