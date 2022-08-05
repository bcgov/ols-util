package ca.bc.gov.ols.config;

import java.util.stream.Stream;
import java.util.List;


public class InMemoryConfigurationStore implements ConfigurationStore {

    protected InMemoryConfigurationStore() {}

    @Override
    public Stream<ConfigurationParameter> getConfigParams() {
        ConfigurationParameter cp1 = new ConfigurationParameter("BGEO", "dataSource.className", "ca.bc.gov.ols.geocoder.datasources.TestDataSource");
        ConfigurationParameter cp2 = new ConfigurationParameter("BGEO", "baseSrsCode", "3005");
        ConfigurationParameter cp3 = new ConfigurationParameter("BGEO", "baseSrsBounds", "200000,300000,1900000,1800000");

        return Stream.of(cp1, cp2, cp3);
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
