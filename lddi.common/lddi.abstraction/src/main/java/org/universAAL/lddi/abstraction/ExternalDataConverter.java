/**
 * 
 */
package org.universAAL.lddi.abstraction;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

/**
 * @author mtazari
 *
 */
public interface ExternalDataConverter {
	public static final String NON_DISCRETE_VALUE_TYPE = "uaal:lddi.abstraction:ExternalDataConverter#nonDiscreteValueType";

	// actual conversion role
	public String getExternalTypeSystemURI();
	public Object exportValue(String typeURI, String propURI, Object internalValue);
	public Object importValue(Object externalValue, String typeURI, String propURI);

	// needed by simulation and config tools
	public String toString(String typeURI, String propURI, Object internalValue);
	public Object valueOf(String internalValue, String typeURI, String propURI);
	
	// needed by simulation tool
	public boolean isPercentage(String typeURI, String propURI);
	public Object getInitialValue(String typeURI, String propURI);
	public Hashtable<Object, URL> getAlternativeValues(String typeURI, String propURI) throws MalformedURLException;
}
