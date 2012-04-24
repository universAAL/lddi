package org.universAAL.knx.utils;

public class KnxGroupAddress {

    private String name;
    private String groupAddress;
    private String dpt;
    private String command = "N/A";
    private Boolean importGA = Boolean.FALSE;
    
    public KnxGroupAddress(String dpt, String groupAddress, String name) {
        super();
        this.dpt = dpt;
        this.groupAddress = groupAddress;
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns the group address as String in format "M/L/D"
     * @param groupAddress
     */
    public String getGroupAddress() {
        return groupAddress;
    }

    public void setGroupAddress(String groupAddress) {
        this.groupAddress = groupAddress;
    }
    
    public String getDpt() {
        return dpt;
    }
    
    public void setDpt(String dpt) {
        this.dpt = dpt;
    }

    public Boolean getImportGA() {
        return importGA;
    }

    public void setImportGA(Boolean importGA) {
        this.importGA = importGA;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }


    public String toString() {
    	return getName() + "; " +
    		getGroupAddress() + "; " +
    		getDpt() + "; " +
    		getCommand();
    }
    
    public String getMainDpt() {
		String temp = this.getDpt().trim();
		return temp.substring(0, temp.indexOf(".") );
    }
}
