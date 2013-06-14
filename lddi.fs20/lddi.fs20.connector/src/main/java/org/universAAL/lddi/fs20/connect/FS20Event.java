/*
 * Copyright (C) 2007 Stefan Strömberg
 * 
 * File: FS20Event.java
 * Project: HomeManager
 * 
 * This source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * History:
 * 2007 jan 5	Created 
 */

package org.universAAL.lddi.fs20.connect;

public class FS20Event {
	protected int houseCode = 0;
	protected byte function = 0;
	protected byte button = 0;
	
	
	public FS20Event(int houseCode, byte function, byte button) {
		super();
		this.houseCode = houseCode;
		this.function = function;
		this.button = button;
	}
	
	public String toString() {
		return "HouseCode: " + Integer.toHexString(houseCode) + 
		" Button: " + Integer.toString(button) + " Function: " + Integer.toString(function);
	}
	/**
	 * @return the button
	 */
	public byte getButton() {
		return button;
	}
	/**
	 * @param button the button to set
	 */
	public void setButton(byte button) {
		this.button = button;
	}
	/**
	 * @return the function
	 */
	public byte getFunction() {
		return function;
	}
	/**
	 * @param function the function to set
	 */
	public void setFunction(byte function) {
		this.function = function;
	}
	/**
	 * @return the houseCode
	 */
	public int getHouseCode() {
		return houseCode;
	}
	/**
	 * @param houseCode the houseCode to set
	 */
	public void setHouseCode(int houseCode) {
		this.houseCode = houseCode;
	}

}
