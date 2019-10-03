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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.ols.rowreader.CsvRowReader;
import ca.bc.gov.ols.rowreader.RowReader;
import ca.bc.gov.ols.rowreader.RowWriter;
import ca.bc.gov.ols.rowreader.XsvRowWriter;

/*
 * It might be nice to cache the config file(s) but until the performance is tested that
 * may be premature optimization, so it is left that out for now.
 */
public class FileConfigurationStore implements ConfigurationStore {
	protected static final String CONFIGURATION_PARAMETERS_FILENAME = "bgeo_configuration_parameters.csv";

	private final static Logger logger = LoggerFactory.getLogger( 
			FileConfigurationStore.class.getCanonicalName());
	
	protected String baseUrl;
	
	protected FileConfigurationStore() {
	}
	
	public FileConfigurationStore(Properties bootstrapConfig) {
		baseUrl = bootstrapConfig.getProperty("OLS_FILE_CONFIGURATION_URL");
		if(!baseUrl.endsWith("/")) {
			baseUrl = baseUrl.concat("/");
		}
		validate();
	}
	
	protected void validate() {
		ConfigurationStore defaults = new DefaultConfigurationStore();
		if(!fileExists(CONFIGURATION_PARAMETERS_FILENAME)) {
			//createConfigurationParameters();
			writeConfigParams(defaults.getConfigParams().collect(Collectors.toList()));			
		} else {
			addMissingConfigurationParameters(defaults);
		}
	}
	
	protected boolean fileExists(String filename) {
		try {
			getInputStreamRaw(filename).close();
			return true;
		} catch (IOException ioe) {
			// no-op
		}
		return false;
	}

	private void addMissingConfigurationParameters(ConfigurationStore defaults) {
		List<ConfigurationParameter> curConfigParams = getConfigParams().collect(Collectors.toList());
		List<ConfigurationParameter> defaultConfigParams = defaults.getConfigParams().collect(Collectors.toList());
		List<ConfigurationParameter> newConfigParams = new ArrayList<ConfigurationParameter>();
		Collections.sort(curConfigParams);
		Collections.sort(defaultConfigParams);
		int cIndex = 0;
		int dIndex = 0;
		while(cIndex < curConfigParams.size() && dIndex < defaultConfigParams.size() ) {
			ConfigurationParameter nextC = curConfigParams.get(cIndex);
			ConfigurationParameter nextD = defaultConfigParams.get(dIndex);
			int comp = nextC.compareTo(nextD);
			if(comp == 0) {
				// this param is in both, carry on
				cIndex++;
				dIndex++;
			} else if(comp < 0) {
				// we have a current param that doesn't exist in the default, 
				// it is probably no longer valid, but we'll leave it  
				cIndex++;
			} else if(comp > 0) {
				// this param is in the default but not the current
				// we will add it after
				newConfigParams.add(nextD);
				dIndex++;
			}
		}
		// if there are more items left in the defaults, 
		// they are missing from the current and need to be added
		while(dIndex < defaultConfigParams.size()) {
			newConfigParams.add(defaultConfigParams.get(dIndex));
			dIndex++;			
		}
		if(!newConfigParams.isEmpty()) {
			curConfigParams.addAll(newConfigParams);
			writeConfigParams(curConfigParams);
		}
	}
	
	@Override
	public Stream<ConfigurationParameter> getConfigParams() {
		InputStream in = getInputStream(CONFIGURATION_PARAMETERS_FILENAME);
		RowReader rr = new CsvRowReader(in,null);
		return rr.asStream(ConfigurationParameter::new);
	}

	@Override
	public void setConfigParam(ConfigurationParameter param) {
		List<ConfigurationParameter> configParams = getConfigParams().collect(Collectors.toList());
		configParams.stream().filter(p -> p.getConfigParamName().equals(param.getConfigParamName()))
			.findFirst().ifPresentOrElse(p -> p.setConfigParamValue(param.getConfigParamValue()), () -> configParams.add(param));
		writeConfigParams(configParams);
	}

	@Override
	public void removeConfigParam(ConfigurationParameter param) {
		List<ConfigurationParameter> configParams = getConfigParams().collect(Collectors.toList());
		configParams.remove(param);
		writeConfigParams(configParams);
	}

	@Override
	public void replaceWith(ConfigurationStore configStore) {
		writeConfigParams(configStore.getConfigParams().collect(Collectors.toList()));
	}

	protected void writeConfigParams(List<ConfigurationParameter> configParams) {
		OutputStream out = getOutputStream(CONFIGURATION_PARAMETERS_FILENAME);
		RowWriter rw = new XsvRowWriter(out, ',', List.of("app_id", "config_param_name", "config_param_value"),true);
		HashMap<String, String> row = new HashMap<String,String>();
		for(ConfigurationParameter cp : configParams) {
			row.put("app_id", cp.getAppId());
			row.put("config_param_name", cp.getConfigParamName());
			row.put("config_param_value", cp.getConfigParamValue());
			rw.writeRow(row);
		}
		rw.close();
	}
	
	@Override
	public void close() {
		// no-op
	}

	protected InputStream getInputStream(String filename) {
		try {
			return getInputStreamRaw(filename);
		} catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	protected InputStream getInputStreamRaw(String filename) throws IOException {
		String fileUrlString = baseUrl + filename; 
		logger.info("Reading from file: " + fileUrlString);
			if(fileUrlString.startsWith("file:")) {
				return new FileInputStream(new File(fileUrlString.substring(5)));
			}
			URL fileUrl = new URL(fileUrlString);
			return fileUrl.openStream();
	}

	protected OutputStream getOutputStream(String filename) {
		try {
			return getOutputStreamRaw(filename);
		} catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	

	protected OutputStream getOutputStreamRaw(String filename) throws IOException {
		String fileUrlString = baseUrl + filename; 
		if(fileUrlString.startsWith("file:")) {
			logger.info("Writing to file: " + fileUrlString);
			try {
				return new FileOutputStream(new File(fileUrlString.substring(5)));
			} catch(IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
		throw new RuntimeException("Can't write to file: " + fileUrlString);
	}
}
