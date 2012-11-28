package org.universAAL.lddi.iso11073.activityhub.location;
 
 /**
  * Definition of location codes from ISO 11073-10471 Nomenclature
  * plus util mehtods.
  * 
  * @author Thomas Fuxreiter (foex@gmx.at)
  */
public abstract class ActivityHubLocationUtil {

	/**
	 * convert String to enum item
	 * 
	 * @param str
	 * @return enum item
	 */
    public static ActivityHubLocation toActivityHubLocation(String str)
    {
        try {
            return ActivityHubLocation.valueOf(str);
        } 
        catch (Exception ex) {
            return null;
        }
    }

    /**
     * location format from ISO 11073-10471 Nomenclature
     */
    public enum ActivityHubLocation {
    	MDC_AI_LOCATION_UNKNOWN,
    	MDC_AI_LOCATION_UNSPECIFIED,
    	MDC_AI_LOCATION_RESIDENT,
    	MDC_AI_LOCATION_LOCALUNIT,
    	MDC_AI_LOCATION_BEDROOM,
    	MDC_AI_LOCATION_BEDROOMMASTER,
    	MDC_AI_LOCATION_TOILET,
    	MDC_AI_LOCATION_TOILETMAIN,
    	MDC_AI_LOCATION_OUTSIDETOILET,
    	MDC_AI_LOCATION_SHOWERROOM,
    	MDC_AI_LOCATION_KITCHEN,
    	MDC_AI_LOCATION_KITCHENMAIN,
    	MDC_AI_LOCATION_LIVINGAREA,
    	MDC_AI_LOCATION_LIVINGROOM,
    	MDC_AI_LOCATION_DININGROOM,
    	MDC_AI_LOCATION_STUDY,
    	MDC_AI_LOCATION_HALL,
    	MDC_AI_LOCATION_LANDING,
    	MDC_AI_LOCATION_STAIRS,
    	MDC_AI_LOCATION_HALLLANDINGSTAIRS,
    	MDC_AI_LOCATION_GARAGE,
    	MDC_AI_LOCATION_GARDENGARAGE,
    	MDC_AI_LOCATION_GARDENGARAGEAREA,
    	MDC_AI_LOCATION_FRONTGARDEN,
    	MDC_AI_LOCATION_BACKGARDEN,
    	MDC_AI_LOCATION_SHED,
    	MDC_AI_LOCATION_KETTLE,
    	MDC_AI_LOCATION_TELEVISION,
    	MDC_AI_LOCATION_STOVE,
    	MDC_AI_LOCATION_MICROWAVE,
    	MDC_AI_LOCATION_TOASTER,
    	MDC_AI_LOCATION_VACUUM,
    	MDC_AI_LOCATION_APPLIANCE,
    	MDC_AI_LOCATION_FAUCET,
    	MDC_AI_LOCATION_FRONTDOOR,
    	MDC_AI_LOCATION_BACKDOOR,
    	MDC_AI_LOCATION_FRIDGEDOOR,
    	MDC_AI_LOCATION_MEDCABDOOR,
    	MDC_AI_LOCATION_WARDROBEDOOR,
    	MDC_AI_LOCATION_FRONTCUPBOARDDOOR,
    	MDC_AI_LOCATION_OTHERDOOR,
    	MDC_AI_LOCATION_BED,
    	MDC_AI_LOCATION_CHAIR,
    	MDC_AI_LOCATION_SOFA,
    	MDC_AI_LOCATION_TOILET_SEAT,
    	MDC_AI_LOCATION_STOOL

    }

	
}
