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

public enum RoadClass {
	ALLEYWAY("alleyway", "RA", true, 1),
	ARTERIAL_MAJOR("arterial_major", "RA1", true, 2),
	ARTERIAL_MINOR("arterial_minor", "RA2", true, 2),
	COLLECTOR_MAJOR("collector_major", "RC1", true, 2),
	COLLECTOR_MINOR("collector_minor", "RC2", true, 2),
	CONTROLLED("controlled", "RCT", false, 1),
	DRIVEWAY("driveway", "RPD", true, 1),
	FERRY("ferry", "F", true, 0),
	FERRY_PASSENGER("ferry_passenger", "FP", false, 0),
	FREEWAY("freeway", "RF", true, 3),
	HIGHWAY_MAJOR("highway_major", "RH1", true, 3),
	HIGHWAY_MINOR("highway_minor", "RH2", true, 3),
	LANE("lane", "RLN", true, 1),
	LOCAL("local", "RLO", true, 1),
	PEDESTRIAN_MALL("pedestrian_mall", "RPM", false, 1),
	RAMP("ramp", "RRP", true, 2),
	RECREATION("recreation", "RRC", true, 1),
	RESOURCE("resource", "RRD", true, 1),
	RESTRICTED("restricted", "RRT", true, 1),
	RUNWAY("runway", "RR", false, 0),
	SERVICE("service", "RSV", true, 1),
	STRATA("strata", "RST", true, 1),
	TRAIL("trail", "TD", false, 0),
	TRAIL_RECREATION("trail_recreation", "TR", false, 0),
	WATER_ACCESS("water_access", "RWA", false, 0),
	YIELD_LANE("yield_lane", "RYL", true, 1),
	UNKNOWN("unknown", "U", false, 1);
	
	private static final Logger logger = LoggerFactory.getLogger(RoadClass.class.getCanonicalName());
	
	private final String label;
	private final String code;
	private final boolean routeable;
	private final int group;
	
	private RoadClass(String label, String code, boolean routeable, int group) {
		this.label = label;
		this.code = code;
		this.routeable = routeable;
		this.group = group;
	}
	
	/**
	 * Takes a string value and returns the corresponding RoadClass object.
	 * 
	 * @param roadClass string representation of the RoadClass
	 * @return the RoadClass corresponding to the given string representation.
	 */
	public static RoadClass convert(String roadClass) {
		for(RoadClass rc : values()) {
			if(rc.label.equalsIgnoreCase(roadClass)) {
				return rc;
			}
			if(rc.code.equalsIgnoreCase(roadClass)) {
				return rc;
			}
		}
		logger.warn("Invalid RoadClass value: '{}'.", roadClass);
		return UNKNOWN;
	}
	
	/**
	 * @return the string representation of this RoadClass object
	 */
	@Override
	public String toString() {
		return label;
	}
	
	public String getCode() {
		return code;
	}
	
	public boolean isRouteable() {
		return routeable;
	}
	
	public int getGroup() {
		return group;
	}
	
}
