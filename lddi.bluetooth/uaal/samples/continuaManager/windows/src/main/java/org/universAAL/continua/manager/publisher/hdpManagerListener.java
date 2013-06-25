package org.universAAL.continua.manager.publisher;
/**
 *  Interface that should be necessary implemented by the HDP manager in order to attend "connection", 
 *	"disconnection" and "received data" agents (Continua sources) events
 *  
 *  @author Angel Martinez (amartinez@tsbtecnologias.es)
 *  @author Luis Gigante (lgigante@tsbtecnologias.es) *  
 *  @version 0 June, 2012
 * 
 */


// Imports

// Class
public interface hdpManagerListener {

	public void onChannelConnected();	
	public void onChannelDisconnected();	
	public void onWeightDataReceived(String str);
	public void onDiastolicDataReceived(String str);
	public void onSystolicDataReceived(String str);
	public void onHeartRateDataReceived(String str);	
	public void onMessage(String str);
	
}
