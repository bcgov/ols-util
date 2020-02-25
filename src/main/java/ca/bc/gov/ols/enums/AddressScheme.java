/**
 * Copyright © 2008-2019, Province of British Columbia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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