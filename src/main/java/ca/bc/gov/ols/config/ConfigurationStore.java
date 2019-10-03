package ca.bc.gov.ols.config;

import java.util.Optional;
import java.util.stream.Stream;

public interface ConfigurationStore {

	Stream<ConfigurationParameter> getConfigParams();

	void setConfigParam(ConfigurationParameter param);

	void removeConfigParam(ConfigurationParameter param);

	void replaceWith(ConfigurationStore configStore);
	
	default Optional<String> getConfigParam(String name) {
		return getConfigParams().filter(p -> p.getConfigParamName().equals(name))
		.findFirst().map(ConfigurationParameter::getConfigParamValue);
	}

	void close();
}