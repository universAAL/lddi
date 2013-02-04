package lddi.zigbee.commissioning.devices.api;

import lddi.zigbee.commissioning.clusters.api.IASZoneAAL;
import it.cnr.isti.zigbee.ha.driver.ArraysUtil;
import it.cnr.isti.zigbee.ha.driver.core.HADevice;
import it.cnr.isti.zigbee.ha.driver.core.HAProfile;

public interface IAS_ZoneAAL extends HADevice {

	public static final int DEVICE_ID = 0x0402;
	public static final String NAME = "IAS Zone 'stabilized'";
	public static final int[] MANDATORY = ArraysUtil.append(HADevice.MANDATORY, new int[]{
			HAProfile.IAS_ZONE
	});
	public static final int[] OPTIONAL = HADevice.OPTIONAL;
	public static final int[] STANDARD = ArraysUtil.append(MANDATORY, OPTIONAL);
	public static final int[] CUSTOM = {};

	public IASZoneAAL getIASZone();
}