/*
 	Copyright (C) 2007 Stefan Strömberg
 	
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
