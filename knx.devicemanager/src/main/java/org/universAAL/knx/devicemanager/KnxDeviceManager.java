package org.universAAL.knx.devicemanager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;

import org.universAAL.knx.devicemodel.KnxDevice;
import org.universAAL.knx.devicemodel.KnxDeviceFactory;
import org.universAAL.knx.utils.KnxGroupAddress;

/**
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public class KnxDeviceManager implements ManagedService {

	private BundleContext context;
	private LogService logger;
	private String knxConfigFile;
	private List<KnxGroupAddress> knxImportedGroupAddresses;
	private List<KnxDevice> deviceList ;
	
	public KnxDeviceManager(BundleContext context, LogService log) {
		this.context=context;
		this.logger=log;

		this.registerManagedService();
		this.logger.log(LogService.LOG_DEBUG,"KnxDeviceManager started!");
	}
	
	/***
	 * Register this class as Managed Service
	 */
	private void registerManagedService() {
		Properties propManagedService=new Properties();
		propManagedService.put(Constants.SERVICE_PID, this.context.getBundle().getSymbolicName());
		this.context.registerService(ManagedService.class.getName(), this, propManagedService);
	}
	
	/***
	 * get updated from ConfigurationAdmin
	 */
	@SuppressWarnings("unchecked")
	public void updated(Dictionary properties) throws ConfigurationException {
		this.logger.log(LogService.LOG_INFO, "KnxDeviceManager.updated: " + properties);

		if (properties != null){
			this.knxConfigFile = (String) properties.get("knxConfigFile");

			try {

				if (knxConfigFile != null && knxConfigFile != "") {
					InputStream is = new FileInputStream(knxConfigFile);
					this.knxImportedGroupAddresses = new KnxImporter()
					.importETS4Configuration(is);
					this.logger.log(LogService.LOG_INFO,
							"Knx devices found in configuration: "
							+ this.knxImportedGroupAddresses.toString());
					
					this.deviceList = new ArrayList<KnxDevice>();
					for ( KnxGroupAddress knxGroupAddress : knxImportedGroupAddresses ) {
						
						if ( checkKnxGroupAddress(knxGroupAddress) ) {
							
							String dptMain = knxGroupAddress.getMainDpt();
							int dptMainNumber = Integer.parseInt(dptMain);
							
							// create appropriate device from dpt main number
							KnxDevice knxDevice = KnxDeviceFactory.getKnxDevice(dptMainNumber);

							// set instance alive
							knxDevice.setParams(knxGroupAddress,this.logger);
							
//							KnxDevice knxDevice = new KnxDevice(knxGroupAddress,this.logger);
							
							deviceList.add(knxDevice);

							// register device in OSGi registry
							Properties propDeviceService=new Properties();

							propDeviceService.put(
									org.osgi.service.device.Constants.DEVICE_CATEGORY, 
									knxDevice.getDeviceCategory());
							// more possible properties: description, serial, id
							
							this.logger.log(LogService.LOG_INFO, "Register KNX device " +
									knxDevice.getDeviceId() + " in OSGi registry under " +
									"device category: " + knxDevice.getDeviceCategory());
							
							this.context.registerService(
									org.osgi.service.device.Device.class.getName(), knxDevice, 
									propDeviceService);
							
						} else {
							this.logger.log(LogService.LOG_ERROR, "KNX device with group address " +
									knxGroupAddress.getGroupAddress() + " has incorrect DPT property.");
						}
						
					}
					
					
					
				} else {
					this.logger.log(LogService.LOG_ERROR, "KNX configuration file name is empty!");
				}

			} catch (FileNotFoundException e) {
				this.logger.log(LogService.LOG_ERROR, "KNX configuration xml file " +
						knxConfigFile + " could not be opened!");
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			this.logger.log(LogService.LOG_ERROR, "Property file for knx.devicemanager not found!");
		}
		
	}

	/**
	 * check for null properties
	 * @param knxGroupAddress
	 * @return
	 */
	private boolean checkKnxGroupAddress(KnxGroupAddress knxGroupAddress) {
		// TODO more checks for wellformedness of KNX DPT
		if ( knxGroupAddress.getDpt() != null && knxGroupAddress.getDpt().contains(".") ) {
			return true;
		} else {
			return false;
		}
		
	}
	
}
