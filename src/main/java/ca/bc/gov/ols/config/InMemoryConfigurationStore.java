package ca.bc.gov.ols.config;

import java.util.stream.Stream;
import java.util.List;


public class InMemoryConfigurationStore implements ConfigurationStore {

    protected InMemoryConfigurationStore() {}

    @Override
    public Stream<ConfigurationParameter> getConfigParams() {
        ConfigurationParameter cp = new ConfigurationParameter("BGEO", "dataSource.className", "ca.bc.gov.ols.datasources.TestDataSource");
        return Stream.of(cp);
    }

    @Override
    public void setConfigParam(ConfigurationParameter param) {
        // not implemented
    }

    @Override
    public void removeConfigParam(ConfigurationParameter param) {
        // not implemented;
    }

    @Override
    public void replaceWith(ConfigurationStore configStore) {
        // not implemented
    }

    protected void writeConfigParams(List<ConfigurationParameter> configParams) {
        // not implemented
    }

    @Override
    public void close() {
        // no-op
    }

}
