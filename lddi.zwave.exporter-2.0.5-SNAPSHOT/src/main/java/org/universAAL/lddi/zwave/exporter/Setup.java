/*
	Copyright 2011-2012 TSB, http://www.tsbtecnologias.es
	TSB - Tecnolog�as para la Salud y el Bienestar
	
	See the NOTICE file distributed with this work for additional 
	information regarding copyright ownership
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	  http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package org.universAAL.lddi.zwave.exporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.universAAL.middleware.container.osgi.util.BundleConfigHome;

public class Setup {
public static final String SETUP_FILENAME = "ZWaveDataPublisher.properties";
	
	private static String setupFileName = null;
	
	private static String configFolderPath = System.getProperty(
            BundleConfigHome.uAAL_CONF_ROOT_DIR, System
                    .getProperty("user.dir"));
	
	static public String getSetupFileName() {
		if (setupFileName != null) {
			return setupFileName;
		}
		File dir1 = new File(".");
		try {
			if(configFolderPath.substring(configFolderPath.length() - 1)!="/"){
				configFolderPath+="/";
			}
			setupFileName = configFolderPath+"ZWaveDataPublisher/"+SETUP_FILENAME;
            System.out.println("Fichero: "+setupFileName);
			return setupFileName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
		
	
	public String getVeraAddress(){
		Properties properties = new Properties();
		try {
			String setup = getSetupFileName();
			System.out.println("setup is in: "+setup);
		    properties.load(new FileInputStream(setup));
		    return properties.getProperty("VeraAddress");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}	
}

