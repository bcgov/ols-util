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
package ca.bc.gov.ols.config;

import java.io.IOException;
import java.util.List;

import com.google.gson.stream.JsonReader;

import ca.bc.gov.ols.rowreader.RowReader;

public class ConfigurationParameter implements Comparable<ConfigurationParameter> {

	private String appId;
	private String configParamName;
	private String configParamValue;
	
	public ConfigurationParameter(String appId, String configParamName, String configParamValue) {
		this.appId = appId;
		this.configParamName = configParamName;
		this.configParamValue = configParamValue;
	}
	
	public ConfigurationParameter(RowReader rr) {
		appId = rr.getString("app_id");
		configParamName = rr.getString("config_param_name");
		configParamValue = rr.getString("config_param_value");
	}

	public ConfigurationParameter(JsonReader jsonReader, List<String> messages) 
			throws IOException {
		jsonReader.beginObject();
		while(jsonReader.hasNext()) {
			switch(jsonReader.nextName()) {
			case "app_id":
				appId = jsonReader.nextString();
				break;
			case "config_param_name":
				configParamName = jsonReader.nextString();
				break;
			case "config_param_value":
				configParamValue = jsonReader.nextString();
				break;
			default:
				messages.add("Unexpected key/value: " + jsonReader.getPath() 
						+ " = " + jsonReader.nextString());
			}
		}
		jsonReader.endObject();
	}

	public String getAppId() {
		return appId;
	}

	public void setConfigParamName(String configParamName) {
		this.configParamName = configParamName;
	}
	
	public String getConfigParamName() {
		return configParamName;
	}

	public void setConfigParamValue(String configParamValue) {
		this.configParamValue = configParamValue;
	}
	
	public String getConfigParamValue() {
		return configParamValue;
	}

	/**
	 * Compares the key part only, not the value.
	 */
	@Override
	public int compareTo(ConfigurationParameter other) {
		int comp = appId.compareTo(other.appId);
		if(comp == 0) {
			comp = configParamName.compareTo(other.configParamName);
		}
		return comp;
	}

	@Override
	public boolean equals(Object other) {
		if(other instanceof ConfigurationParameter) {
			ConfigurationParameter o = (ConfigurationParameter)other;
			if(appId.equals(o.appId) 
					&& configParamName.equals(o.configParamName)
					&& configParamValue.equals(o.configParamValue)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (appId + configParamName + configParamValue).hashCode();
	}

}
