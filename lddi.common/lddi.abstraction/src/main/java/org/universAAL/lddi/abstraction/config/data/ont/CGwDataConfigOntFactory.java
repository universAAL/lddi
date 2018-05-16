/**
 * 
 */
package org.universAAL.lddi.abstraction.config.data.ont;

import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.rdf.ResourceFactory;

/**
 * @author mtazari
 *
 */
public class CGwDataConfigOntFactory implements ResourceFactory {

	/* (non-Javadoc)
	 * @see org.universAAL.middleware.rdf.ResourceFactory#createInstance(java.lang.String, java.lang.String, int)
	 */
	public Resource createInstance(String classURI, String instanceURI, int factoryIndex) {
		switch (factoryIndex) {
		case 0:
			if (Component.MY_URI.equals(classURI)) 
				return new Component(instanceURI);
			return null;
		case 1:
			if (DatapointValueType.MY_URI.equals(classURI)) 
				return new DatapointValueType(instanceURI);
			return null;
		case 2:
			if (Datapoint.MY_URI.equals(classURI)) 
				return new Datapoint(instanceURI);
			return null;
			
		}
		return null;
	}

}
