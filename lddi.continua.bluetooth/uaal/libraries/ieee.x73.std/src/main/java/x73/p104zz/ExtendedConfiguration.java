package x73.p104zz;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.LinkedList;

import org.bn.IDecoder;
import utils.ASNUtils;
import x73.nomenclature.NomenclatureCodes;
import x73.p20601.AVA_Type;
import x73.p20601.AttributeList;
import x73.p20601.ConfigObject;
import x73.p20601.ConfigReport;
import x73.p20601.HANDLE;
import x73.p20601.dim.Attribute;
import x73.p20601.dim.Numeric;

/**
 * Class for processing the unknown configuration of an Agent device. Usually, it will be an extended one
 * Please note that in this moment, only Numeric objects are supported.
 * @author lgigante
 *
 */
public class ExtendedConfiguration extends DeviceSpecialization {

//	IDecoder decoder = null;
	ByteArrayInputStream bais = null;
	
	public ExtendedConfiguration(IDecoder decoder) {
		super(decoder);
//		this.decoder = decoder;
	}
	
	public void createNumeric( ConfigObject cfgobj) throws Exception{
		
		LinkedList<Attribute> numericattrs = new LinkedList<Attribute>();
		
		// Get the HANDLE of this Numeric object and store it as an Attribute.
		HANDLE handle = cfgobj.getObj_handle();
		Attribute handattr = new Attribute(NomenclatureCodes.MDC_ATTR_ID_HANDLE, handle);
		int int_handle = handle.getValue().getValue();
		numericattrs.add(handattr);
		
		
		// Get the Attribute List of the Numeric object in which the information of the measurement will be stored.
		AttributeList attrlist = cfgobj.getAttributes();
		Iterator<AVA_Type> itattr = attrlist.getValue().iterator();
		
		while(itattr.hasNext()){
			AVA_Type ava = itattr.next();
			
			int attr_id = ava.getAttribute_id().getValue().getValue();
//			System.out.println(attr_id);
			byte[] ava_value = ava.getAttribute_value();
			bais = new ByteArrayInputStream(ava_value);
			Object object = decoder.decode(bais, ASNUtils.getAttributeClass(attr_id));
			Attribute attr = new Attribute(attr_id, object);
			numericattrs.add(attr);
		}
		
		Numeric num = null;
		try {
			// add the Numeric object to the DIM of the Agent which are modeling in the Manager memory.
			num = new Numeric(numericattrs);
			addObjecttoDim(int_handle,num);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createScanner(ConfigObject cfgobj){
		//TODO  modeling of Scanners objects.
//		addScanner(scanner);
	}
	
	public void createEnumeration(ConfigObject cfgobj){
		//TODO modeling of Enumeration objects.
//		addEnumeration(enumeration)
	}
	
	public void createRT_SA(ConfigObject cfgobj){
		//TODO modeling of RealTime objects.
//		addRT_SA(rt_sa)
	}

	public void createEpisodicScanner(ConfigObject cfgobj) {
		// TODO modeling of Episodic Scanner objects
//		EpiCfgscanner epi;
//		try {
//			epi = new EpiCfgscanner(attrList);
//			addScanner(epi);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public void createPeriodicScanner(ConfigObject cfgobj) {
		// TODO modeling of Periodic Scanner objects
//		PeriCfgScanner peri;
//		try {
//			peri = new PeriCfgScanner(attrList);
//			addScanner(peri);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
