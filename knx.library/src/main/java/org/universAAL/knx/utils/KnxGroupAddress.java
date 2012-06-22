package org.universAAL.knx.utils;

public class KnxGroupAddress {

    private String name;
    private String groupAddress;
    private String dpt;
    private String command = "N/A";
    private Boolean importGA = Boolean.FALSE;
    
    private String description;
    private String bpType; //BuildingPart Type
    private String bpName; //BuildingPart Name
    private String bpDescription;
    //location (room)
    
    public KnxGroupAddress(String dpt, String groupAddress, String name) {
        super();
        this.dpt = dpt;
        this.groupAddress = groupAddress;
        this.name = name;
    }

    /**
	 * @param name
	 * @param groupAddress
	 * @param dpt
	 * @param command
	 * @param importGA
	 * @param description
	 * @param bpType
	 * @param bpName
	 * @param bpDescription
	 */
	public KnxGroupAddress(String dpt, String groupAddress, String name,
			String description, String bpType, String bpName, String bpDescription) {
		this.name = name;
		this.groupAddress = groupAddress;
		this.dpt = dpt;
		this.description = description;
		this.bpType = bpType;
		this.bpName = bpName;
		this.bpDescription = bpDescription;
	}

	/**
	 * @return description of KNX device
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return type of the BuildingPart where the KNX device is located
	 */
	public String getBpType() {
		return bpType;
	}

	/**
	 * @return Name of the BuildingPart where the KNX device is located
	 */
	public String getBpName() {
		return bpName;
	}

	/**
	 * @return description of the BuildingPart where the KNX device is located
	 */
	public String getBpDescription() {
		return bpDescription;
	}

	/**
	 * @return name of the KNX device
	 */
	public String getName() {
        return name;
    }
        
    /**
     * @return group address as String in format "M/L/D"
     */
    public String getGroupAddress() {
        return groupAddress;
    }

    /**
     * @return datapoint type of the KNX device
     */
    public String getDpt() {
        return dpt;
    }

    /**
     * @return name, group address, datapoint type and  of the KNX device
     */
    public String toString() {
    	return getName() + "; " +
    		getGroupAddress() + "; " +
    		getDpt() + "; " +
    		getDescription() + "; " +
    		getBpType() + "; " +
    		getBpName() + "; " +
    		getBpDescription();
    }
    
    /**
     * @return main datapoint type number of the KNX device 
     */
    public String getDptMain() {
		String temp = this.getDpt().trim();
		return temp.substring(0, temp.indexOf(".") );
    }

    /**
     * @return sub datapoint type number of the KNX device 
     */
    public String getDptSub() {
		String temp = this.getDpt().trim();
		return temp.substring(temp.indexOf(".")+1);
    }
    

    public Boolean getImportGA() {
        return importGA;
    }

    public String getCommand() {
        return command;
    }

}
