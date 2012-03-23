package org.universAAL.knx.devicemanager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;

/**
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public class KnxDeviceManager implements ManagedService {

	private BundleContext context;
	private LogService logger;
	private String knxConfigFile;
	private List<KnxGroupAddress> knxImportedGroupAddresses;
	
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
				} else {
					this.logger.log(LogService.LOG_ERROR, "KNX configuration file name is empty!");
				}

			} catch (FileNotFoundException e) {
				this.logger.log(LogService.LOG_ERROR, "KNX configuration xml file " +
						knxConfigFile + " could not be opened!");
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			this.logger.log(LogService.LOG_ERROR, "Property file for knx.devicemanager not found!");
		}
		
	}

}
