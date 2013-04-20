/*
 * Created on 2013-04-14
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.degoworks.util;


public class KeyValue {
	
	String value;
	String key;  // this is the label
	public KeyValue(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public KeyValue(String key, Integer value) {
		this.key = key;
		this.value = value.toString();
	}
 
	public String getValue() { return value; }
	public String getKey() { return key; }
 
	@Override
	public String toString() { return key; }
 
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof KeyValue) {
			KeyValue kv = (KeyValue) obj;
			return (kv.value.equals(this.value));
		}
		return false;
	}
	

}
