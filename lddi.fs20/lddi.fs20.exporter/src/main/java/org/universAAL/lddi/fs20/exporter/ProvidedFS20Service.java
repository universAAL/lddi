/*	
	Copyright 2007-2014 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institut f�r Graphische Datenverarbeitung
	
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

package org.universAAL.lddi.fs20.exporter;

import java.util.Hashtable;

import org.universAAL.middleware.owl.OntologyManagement;
import org.universAAL.middleware.owl.SimpleOntology;
import org.universAAL.middleware.rdf.PropertyPath;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.rdf.ResourceFactory;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.activityhub.UsageSensor;
import org.universAAL.ontology.av.device.LoudSpeaker;
import org.universAAL.ontology.device.LightActuator;
import org.universAAL.ontology.device.MotionSensor;
import org.universAAL.ontology.device.SwitchActuator;
import org.universAAL.ontology.phThing.DeviceService;




/**
 * all the services of Controlled FS20 Devices (FS20Gong && FS20Display)
 * @author steeven
 */
public class ProvidedFS20Service extends DeviceService {

//    public static final String FS20CONTROLLER_NAMESPACE="http://ontology.persona.ima.igd.fhg.de/FS20Controlled.owl#";
   
    public static final String FS20_SERVER_NAMESPACE = System.getProperty("org.universAAL.middleware.samples.lighting.server.namespace",
        	"http://ontology.igd.fhg.de/FS20Server.owl#");
   
    public static final String MY_URI = FS20_SERVER_NAMESPACE + "FS20Service";
   
    public static final String PROP_CONTROLS_DISPLAYS;
    
    public static final String PROP_DISPLAY_ACTION;
    
    public static final String PROP_CONTROLS_GONGS;
    
    public static final String PROP_GONG_ACTIVATED;
    
    public static final String PROP_CONTROLS_FS20ST;
    
    public static final String PROP_CONTROLS_PIRx;
    
    public static final String PROP_CONTROLS_FMS;
    
    public static final String PROP_FS20ST_ACTIVATED;
   
    public static final String SERVICE_ACTIVATE_GONG = FS20_SERVER_NAMESPACE + "activateGong";
   
    public static final String SERVICE_GET_FS20GONG = FS20_SERVER_NAMESPACE + "getFS20Gong";
    
    public static final String SERVICE_GET_FS20ST = FS20_SERVER_NAMESPACE + "getFS20ST";
    
    public static final String SERVICE_TURN_ON_FS20ST = FS20_SERVER_NAMESPACE + "turnOnFS20ST";
    
    public static final String SERVICE_TURN_OFF_FS20ST = FS20_SERVER_NAMESPACE + "turnOffFS20ST";
   
    public static final String SERVICE_GET_FS20DISPLAY = FS20_SERVER_NAMESPACE + "getFS20Display";
    
    public static final String SERVICE_DISPLAY_ANIMATION_1 = FS20_SERVER_NAMESPACE + "displayAnimation1";
   
    public static final String SERVICE_DISPLAY_ANIMATION_2 = FS20_SERVER_NAMESPACE + "displayAnimation2";
   
    public static final String SERVICE_DISPLAY_ANIMATION_3 = FS20_SERVER_NAMESPACE + "displayAnimation3";
   
    public static final String SERVICE_DISPLAY_ANIMATION_4 = FS20_SERVER_NAMESPACE + "displayAnimation4";
   
    public static final String SERVICE_DISPLAY_ANIMATION_5 = FS20_SERVER_NAMESPACE + "displayAnimation5";
  
    public static final String SERVICE_DISPLAY_ANIMATION_6 = FS20_SERVER_NAMESPACE + "displayAnimation6";
   
    public static final String SERVICE_DISPLAY_ANIMATION_7 = FS20_SERVER_NAMESPACE + "displayAnimation7";
  
    public static final String SERVICE_DISPLAY_ANIMATION_8 = FS20_SERVER_NAMESPACE + "displayAnimation8";
  
    public static final String SERVICE_DISPLAY_ANIMATION_9 = FS20_SERVER_NAMESPACE + "displayAnimation9";
   
    public static final String SERVICE_DISPLAY_ANIMATION_10 = FS20_SERVER_NAMESPACE + "displayAnimation10";
   
    public static final String SERVICE_DISPLAY_ANIMATION_11 = FS20_SERVER_NAMESPACE + "displayAnimation11";
    
    public static final String SERVICE_DISPLAY_ANIMATION_12 = FS20_SERVER_NAMESPACE + "displayAnimation12";

    public static final String SERVICE_GET_FS20FMS = FS20_SERVER_NAMESPACE + "getFS20FMS";
    
    public static final String SERVICE_GET_FS20PIRX = FS20_SERVER_NAMESPACE + "getFS20PIRx";
    
    static final String INPUT_URI_GONG = FS20_SERVER_NAMESPACE + "deviceURI_Gong";
    
    static final String INPUT_URI_FS20ST = FS20_SERVER_NAMESPACE + "deviceURI_FS20ST";
    
    static final String INPUT_URI_DISPLAY = FS20_SERVER_NAMESPACE + "deviceURI_Display";
   
    static final String OUTPUT_RadioControlledBell = FS20_SERVER_NAMESPACE + "outputFS20Gongs";
    
    static final String OUTPUT_FS20DISPLAYS = FS20_SERVER_NAMESPACE + "outputFS20Displays";
    
    static final String OUTPUT_FS20ST = FS20_SERVER_NAMESPACE + "FS20Device#outputFS20STs";
    
    static final String OUTPUT_FS20FMS = FS20_SERVER_NAMESPACE + "outputFS20FMSs";
    
    static final String OUTPUT_FS20PIRX = FS20_SERVER_NAMESPACE + "outputFS20PIRx";
   
    static final String INPUT_LOCATION = FS20_SERVER_NAMESPACE + "AbsLocation";
  
    static final String INPUT_ANIMATION = FS20_SERVER_NAMESPACE + "animation";
    
    static final String OUTPUT_CONTROLLED_SWITCHES = FS20_SERVER_NAMESPACE + "controlledSwitches";
  
    static final String OUTPUT_SWITCH_LOCATION = FS20_SERVER_NAMESPACE + "location";

    static final ServiceProfile[] profiles = new ServiceProfile[20];

    @SuppressWarnings("unchecked")
    private static Hashtable serverFS20DeviceRestrictions = new Hashtable(13);

    static {
	
	
	PROP_CONTROLS_DISPLAYS= FS20_SERVER_NAMESPACE +"FS20Device#controlsDisplays"; //FS20Controller.PROP_CONTROLS_DISPLAY;
	PROP_DISPLAY_ACTION = FS20_SERVER_NAMESPACE + "FS20Device#displayAction";
	
    PROP_CONTROLS_GONGS = FS20_SERVER_NAMESPACE+"FS20Device#controlsGongs";
    PROP_GONG_ACTIVATED = FS20_SERVER_NAMESPACE+"FS20Device#gongActivated";
    
    PROP_CONTROLS_FS20ST =FS20_SERVER_NAMESPACE+"FS20Device#controlsFS20ST";
    PROP_FS20ST_ACTIVATED = FS20_SERVER_NAMESPACE +"FS20Device#FS20STActivated";
    
    PROP_CONTROLS_PIRx =FS20_SERVER_NAMESPACE+"FS20Device#controlsFS20PIRx";
    
    PROP_CONTROLS_FMS =FS20_SERVER_NAMESPACE+"FS20Device#controlsFS20FMS";
	
	OntologyManagement.getInstance().register(Activator.mc,
			new SimpleOntology(MY_URI, DeviceService.MY_URI,
				new ResourceFactory() {
				    public Resource createInstance(String classURI,
					    String instanceURI, int factoryIndex) {
					return new ProvidedFS20Service(instanceURI);
				    }
				}));

	
	String[] ppControls_DISPLAYS = new String[] {ProvidedFS20Service.PROP_CONTROLS_DISPLAYS};
	
	String[] ppControls_GONGS = new String[] {ProvidedFS20Service.PROP_CONTROLS_GONGS};
	
	String[] turnOnGong = new String[] { ProvidedFS20Service.PROP_CONTROLS_GONGS,PROP_GONG_ACTIVATED };

	String[] ppDisplayAction = new String[] {ProvidedFS20Service.PROP_CONTROLS_DISPLAYS, PROP_DISPLAY_ACTION };
	
	String[] ppControls_FS20ST = new String[] {ProvidedFS20Service.PROP_CONTROLS_FS20ST};
	String[] switchFS20ST = new String[] { ProvidedFS20Service.PROP_CONTROLS_FS20ST,PROP_FS20ST_ACTIVATED };
	
	String[] ppControls_PIRx = new String[] {ProvidedFS20Service.PROP_CONTROLS_PIRx};
	
	String[] ppControls_FMS = new String[] {ProvidedFS20Service.PROP_CONTROLS_FMS};
	
//	String[] ppAbsLocation = new String[] {ProvidedFS20Service.PROP_CONTROLS_DISPLAYS, FS20Device.PROP_PHYSICAL_LOCATION };
	
	PropertyPath ValuePath = new PropertyPath(null, true, ppDisplayAction);

        
	//get Gongs
	ProvidedFS20Service getFS20Gongs= new ProvidedFS20Service(SERVICE_GET_FS20GONG);
	getFS20Gongs.addOutput(OUTPUT_RadioControlledBell, LoudSpeaker.MY_URI, 0, 0, ppControls_GONGS);
	profiles[0]=getFS20Gongs.myProfile;
	
	//turn Gongs on
	ProvidedFS20Service activateGong = new ProvidedFS20Service(SERVICE_ACTIVATE_GONG);
	activateGong.addFilteringInput(INPUT_URI_GONG, LoudSpeaker.MY_URI, 1, 0,ppControls_GONGS);
	activateGong.myProfile.addChangeEffect(turnOnGong, new String("on"));
	profiles[1] = activateGong.myProfile;

	
	
	//get Displays
	ProvidedFS20Service getFS20Display=new ProvidedFS20Service(SERVICE_GET_FS20DISPLAY);
	getFS20Display.addOutput(OUTPUT_FS20DISPLAYS, LightActuator.MY_URI, 0, 0, ppControls_DISPLAYS);
	profiles[2]=getFS20Display.myProfile;
	
	//turn the diplay on, in this case there are twelve animations are defined in the @FS20Gong 
	
	//animation 1
	ProvidedFS20Service startAnimation1Service = new ProvidedFS20Service(
		SERVICE_DISPLAY_ANIMATION_1);
	startAnimation1Service.addFilteringInput(INPUT_URI_DISPLAY, LightActuator.MY_URI,
		1, 1, ppControls_DISPLAYS);
	startAnimation1Service.myProfile.addChangeEffect(ValuePath.getThePath(),
		new Integer(1));
	profiles[3] = startAnimation1Service.myProfile;
       
	//animation 2
	ProvidedFS20Service startAnimation2Service = new ProvidedFS20Service(
		SERVICE_DISPLAY_ANIMATION_2);
	startAnimation2Service.addFilteringInput(INPUT_URI_DISPLAY, LightActuator.MY_URI,
		1, 1, ppControls_DISPLAYS);
	startAnimation2Service.myProfile.addChangeEffect(ValuePath.getThePath(),
		new Integer(2));
	profiles[4] = startAnimation2Service.myProfile;
	
	//animation 3 
	ProvidedFS20Service startAnimation3Service = new ProvidedFS20Service(
		SERVICE_DISPLAY_ANIMATION_3);
	startAnimation3Service.addFilteringInput(INPUT_URI_DISPLAY, LightActuator.MY_URI,
		1, 1, ppControls_DISPLAYS);
	startAnimation3Service.myProfile.addChangeEffect(ValuePath.getThePath(),
		new Integer(3));
	profiles[5] = startAnimation3Service.myProfile;

	//animation 4 
	ProvidedFS20Service startAnimation4Service = new ProvidedFS20Service(
		SERVICE_DISPLAY_ANIMATION_4);
	startAnimation4Service.addFilteringInput(INPUT_URI_DISPLAY, LightActuator.MY_URI,
		1, 1, ppControls_DISPLAYS);
	startAnimation4Service.myProfile.addChangeEffect(ValuePath.getThePath(),
		new Integer(4));
	profiles[6] = startAnimation4Service.myProfile;

	//animation 5 
	ProvidedFS20Service startAnimation5Service = new ProvidedFS20Service(
		SERVICE_DISPLAY_ANIMATION_5);
	startAnimation5Service.addFilteringInput(INPUT_URI_DISPLAY, LightActuator.MY_URI,
		1, 1, ppControls_DISPLAYS);
	startAnimation5Service.myProfile.addChangeEffect(ValuePath.getThePath(),
		new Integer(5));
	profiles[7] = startAnimation5Service.myProfile;

	//animation 6 
	ProvidedFS20Service startAnimation6Service = new ProvidedFS20Service(
		SERVICE_DISPLAY_ANIMATION_6);
	startAnimation6Service.addFilteringInput(INPUT_URI_DISPLAY, LightActuator.MY_URI,
		1, 1, ppControls_DISPLAYS);
	startAnimation6Service.myProfile.addChangeEffect(ValuePath.getThePath(),
		new Integer(6));
	profiles[8] = startAnimation6Service.myProfile;

	//animation 7 
	ProvidedFS20Service startAnimation7Service = new ProvidedFS20Service(
		SERVICE_DISPLAY_ANIMATION_7);
	startAnimation7Service.addFilteringInput(INPUT_URI_DISPLAY, LightActuator.MY_URI,
		1, 1, ppControls_DISPLAYS);
	startAnimation7Service.myProfile.addChangeEffect(ValuePath.getThePath(),
		new Integer(7));
	profiles[9] = startAnimation7Service.myProfile;

	//animation 8 
	ProvidedFS20Service startAnimation8Service = new ProvidedFS20Service(
		SERVICE_DISPLAY_ANIMATION_8);
	startAnimation8Service.addFilteringInput(INPUT_URI_DISPLAY, LightActuator.MY_URI,
		1, 1, ppControls_DISPLAYS);
	startAnimation8Service.myProfile.addChangeEffect(ValuePath.getThePath(),
		new Integer(8));
	profiles[10] = startAnimation8Service.myProfile;

	//animation 9 
	ProvidedFS20Service startAnimation9Service = new ProvidedFS20Service(
		SERVICE_DISPLAY_ANIMATION_9);
	startAnimation9Service.addFilteringInput(INPUT_URI_DISPLAY, LightActuator.MY_URI,
		1, 1, ppControls_DISPLAYS);
	startAnimation9Service.myProfile.addChangeEffect(ValuePath.getThePath(),
		new Integer(9));
	profiles[11] = startAnimation9Service.myProfile;

	//animation 10 
	ProvidedFS20Service startAnimation10Service = new ProvidedFS20Service(
		SERVICE_DISPLAY_ANIMATION_10);
	startAnimation10Service.addFilteringInput(INPUT_URI_DISPLAY,
			LightActuator.MY_URI, 1, 1, ppControls_DISPLAYS);
	startAnimation10Service.myProfile.addChangeEffect(ValuePath.getThePath(),
		new Integer(10));
	profiles[12] = startAnimation10Service.myProfile;

	//animation 11
	ProvidedFS20Service startAnimation11Service = new ProvidedFS20Service(
		SERVICE_DISPLAY_ANIMATION_11);
	startAnimation11Service.addFilteringInput(INPUT_URI_DISPLAY,
			LightActuator.MY_URI, 1, 1, ppControls_DISPLAYS);
	startAnimation11Service.myProfile.addChangeEffect(ValuePath.getThePath(),
		new Integer(11));
	profiles[13] = startAnimation11Service.myProfile;

	//animation 12
	ProvidedFS20Service startAnimation12Service = new ProvidedFS20Service(
		SERVICE_DISPLAY_ANIMATION_12);
	startAnimation12Service.addFilteringInput(INPUT_URI_DISPLAY,
			LightActuator.MY_URI, 1, 1, ppControls_DISPLAYS);
	startAnimation12Service.myProfile.addChangeEffect(ValuePath.getThePath(),
		new Integer(12));
	profiles[14] = startAnimation12Service.myProfile;
	
	//get FS20ST
	ProvidedFS20Service getFS20ST= new ProvidedFS20Service(SERVICE_GET_FS20ST);
	getFS20ST.addOutput(OUTPUT_FS20ST, SwitchActuator.MY_URI, 0, 0,ppControls_FS20ST );
	profiles[15]=getFS20ST.myProfile;
	
	//turn FS20ST on
	ProvidedFS20Service turnOnFS20ST = new ProvidedFS20Service(SERVICE_TURN_ON_FS20ST);
	turnOnFS20ST.addFilteringInput(INPUT_URI_FS20ST, SwitchActuator.MY_URI, 1, 0,ppControls_FS20ST);
	turnOnFS20ST.myProfile.addChangeEffect(switchFS20ST, new Integer(1));
	profiles[16] = turnOnFS20ST.myProfile;
	
	//turn FS20ST off
	ProvidedFS20Service turnOffFS20ST = new ProvidedFS20Service(SERVICE_TURN_OFF_FS20ST);
	turnOffFS20ST.addFilteringInput(INPUT_URI_FS20ST, SwitchActuator.MY_URI, 1, 0,ppControls_FS20ST);
	turnOffFS20ST.myProfile.addChangeEffect(switchFS20ST, new Integer(0));
	profiles[17] = turnOffFS20ST.myProfile;
	
	//get FS20FMS
	ProvidedFS20Service getFS20FMS= new ProvidedFS20Service(SERVICE_GET_FS20FMS);
	getFS20FMS.addOutput(OUTPUT_FS20FMS, UsageSensor.MY_URI, 0, 0, ppControls_FMS);
	profiles[18]=getFS20FMS.myProfile;
	
	//get FS20PIRx
	ProvidedFS20Service getFS20PIRx= new ProvidedFS20Service(SERVICE_GET_FS20PIRX);
	getFS20PIRx.addOutput(OUTPUT_FS20PIRX, MotionSensor.MY_URI, 0, 0, ppControls_PIRx);
	profiles[19]=getFS20PIRx.myProfile;

    }

    public ProvidedFS20Service() {
	super();
    }

    public ProvidedFS20Service(String uri) {
	super(uri);
    }
    
    public String getClassURI() {
	return MY_URI;
    }

}
