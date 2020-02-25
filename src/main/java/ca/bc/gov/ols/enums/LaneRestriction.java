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

public enum LaneRestriction {
	NONE(""),
	RESTRICTED("R"),
	NARROW("N");
	
	private static final Logger logger = LoggerFactory.getLogger(LaneRestriction.class.getCanonicalName());
	
	private String label;
	
	private LaneRestriction(String label) {
		this.label = label;
	}
	
	/**
	 * Takes a string value and returns the corresponding LaneRestriction object.
	 * 
	 * @param laneRestriction string representation of the LaneRestriction
	 * @return the LaneRestriction corresponding to the given string representation.
	 */
	public static LaneRestriction convert(String laneRestriction) {
		if(laneRestriction == null) {
			return NONE;
		}
		for(LaneRestriction lr : values()) {
			if(lr.label.equalsIgnoreCase(laneRestriction)) {
				return lr;
			}
		}
		logger.warn("Invalid LaneRestriction value: '{}'.", laneRestriction);
		return NONE;
	}
	
	@Override
	public String toString() {
		return label;
	}
}