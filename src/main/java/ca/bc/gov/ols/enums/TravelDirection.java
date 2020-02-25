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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum TravelDirection {
	BIDIRECTIONAL("B"),
	FORWARD("F"),
	REVERSE("R");
	
	private static final Logger logger = LoggerFactory.getLogger(TravelDirection.class.getCanonicalName());

	private String label;
	
	private TravelDirection(String label) {
		this.label = label;
	}
	
	/**
	 * Takes a string value and returns the corresponding TravelDirection object.
	 * 
	 * @param travelDirection string representation of the TravelDirection
	 * @return the TravelDirection corresponding to the given string representation.
	 */
	public static TravelDirection convert(String travelDirection) {
		for(TravelDirection td : values()) {
			if(td.label.equalsIgnoreCase(travelDirection)) {
				return td;
			}
		}
		logger.warn("Invalid TravelDirection value: '{}'.", travelDirection);
		return BIDIRECTIONAL;
	}
	
	public boolean forwardAllowed() {
		return this.equals(BIDIRECTIONAL) || this.equals(FORWARD);
	}
	
	public boolean reverseAllowed() {
		return this.equals(BIDIRECTIONAL) || this.equals(REVERSE);
	}
	
	public boolean isOneWay() {
		return !this.equals(TravelDirection.BIDIRECTIONAL);
	}

	public TravelDirection flip() {
		switch(this) {
		case FORWARD: return REVERSE;
		case REVERSE: return FORWARD;
		default: return BIDIRECTIONAL;
		}
	}
	
	@Override
	public String toString() {
		return label;
	}
}
