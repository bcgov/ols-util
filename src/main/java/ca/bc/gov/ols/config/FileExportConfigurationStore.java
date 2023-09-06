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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.stream.JsonReader;

public class FileExportConfigurationStore implements ConfigurationStore {

	protected String exportDate;
	protected List<String> errors = Collections.synchronizedList(new ArrayList<String>());
	protected List<String> messages = Collections.synchronizedList(new ArrayList<String>());
	protected List<ConfigurationParameter> configParams = new ArrayList<ConfigurationParameter>();
	protected int configParamCount = 0;

	public FileExportConfigurationStore() {}

	public FileExportConfigurationStore(InputStream inStream) {
		try {
			JsonReader jsonReader = new JsonReader(
					new InputStreamReader(inStream, Charset.forName("UTF-8")));
			jsonReader.beginObject();
			while(jsonReader.hasNext()) {
				switch(jsonReader.nextName()) {
				case "exportDate":
					exportDate = jsonReader.nextString();
					break;
				case "BGEO_CONFIGURATION_PARAMETERS":
					jsonReader.beginObject();
					while(jsonReader.hasNext()) {
						switch(jsonReader.nextName()) {
							case "rows":
								jsonReader.beginArray();
								while(jsonReader.hasNext()) {
									configParams.add(new ConfigurationParameter(jsonReader, messages));
								}
								jsonReader.endArray();
								break;
							case "rowCount":
								configParamCount = jsonReader.nextInt();
								break;
							default:
								messages.add("Unexpected key/value: " + jsonReader.getPath() 
										+ " = " + jsonReader.nextString());
						}
					}
					jsonReader.endObject();
					break;
				default:
					messages.add("Unexpected key/value: " + jsonReader.getPath() 
							+ " = " + jsonReader.nextString());
				}
			}
			validate();
		} catch(IOException ioe) {
			errors.add("IOException was thrown while reading configuration: " + ioe.toString());
		} catch(IllegalStateException ise) {
			errors.add("Invalid JSON input; error message was: " + ise.toString());
		}
	}

	protected void validate() {
		if(configParamCount != configParams.size()) {
			errors.add("BGEO_CONFIGURATION_PARAMETERS count: " 
					+ configParams.size() + " does not match expected count " + configParamCount);			
		}
	}

	@Override
	public Stream<ConfigurationParameter> getConfigParams() {
		return configParams.stream();
	}

	public int getConfigParamCount() {
		return configParamCount;
	}
	
	@Override
	public void setConfigParam(ConfigurationParameter param) {
		configParams.stream().filter(p -> p.getConfigParamName().equals(param.getConfigParamName()))
				.findFirst().ifPresent(p -> p.setConfigParamValue(param.getConfigParamValue()));
	}

	@Override
	public void removeConfigParam(ConfigurationParameter param) {
		configParams.remove(param);
	}

	@Override
	public void replaceWith(ConfigurationStore configStore) {
		configParams = configStore.getConfigParams().collect(Collectors.toList());		
	}

	public String getExportDate() {
		return exportDate;
	}

	public List<String> getErrors() {
		return errors;
	}

	public List<String> getMessages() {
		return messages;
	}

	@Override
	public void close() {
		// no-op
	}

}