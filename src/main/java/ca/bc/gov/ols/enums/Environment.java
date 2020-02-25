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

public enum Environment {
	DEVEL, DELIV, TEST, PROD;
	
	/**
	 * Converts from a string representation of the Environment value to the Environment object.
	 * 
	 * @param environment the string representation of the Environment
	 * @return the Environment object corresponding to the given string representation
	 */
	public static Environment convert(String environment) {
		for(Environment env : values()) {
			if(env.toString().equalsIgnoreCase(environment)) {
				return env;
			}
		}
		return null;
	}
	
}