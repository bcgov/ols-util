/**
 * Copyright 2008-2019, Province of British Columbia
 * All rights reserved.
 */
package ca.bc.gov.ols.enums;

/**
 * The possible addressing schemes (aka. parity)
 */
public enum AddressScheme {
	EVEN, ODD, CONTINUOUS, SINGLE, NONE;
	public static AddressScheme convert(String scheme) {
		switch(scheme.substring(0,1).toUpperCase()) {
		case "E":
			return EVEN;
		case "O":
			return ODD;
		case "C":
			return CONTINUOUS;
		case "S":
			return SINGLE;
		case "N":
			return NONE;
		}
		throw new IllegalArgumentException("Invalid address scheme (aka. parity) value: '" + scheme
				+ "' (must be one of 'E', 'O', 'C', 'S')");
	}
	
	public static String parityToString(AddressScheme scheme) {
		if(AddressScheme.EVEN.equals(scheme)) {
			return "E";
		} else if(AddressScheme.ODD.equals(scheme)) {
			return "O";
		} else if(AddressScheme.CONTINUOUS.equals(scheme)) {
			return "C";
		} else if(AddressScheme.SINGLE.equals(scheme)) {
			return "S";
		} else if(AddressScheme.NONE.equals(scheme)) {
			return "N";
		}
		return null;
	}
	
	public boolean includes(int i) {
		switch(this) {
		case EVEN:
			return i % 2 == 0;
		case ODD:
			return i % 2 != 0;
		case CONTINUOUS:
			return true;
		default:
			return false;
		}
	}
	
}