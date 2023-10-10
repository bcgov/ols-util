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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import ca.bc.gov.ols.rowreader.DatastaxResultSetRowReader;

public class CassandraConfigurationStore implements ConfigurationStore {
	private static final Logger logger = LoggerFactory.getLogger(CassandraConfigurationStore.class.getCanonicalName());
	
	protected Properties bootstrapConfig;
	protected String keyspace;
	protected String appId;
	protected CqlSession session;

	public CassandraConfigurationStore(Properties bootstrapConfig) {
		this.bootstrapConfig = bootstrapConfig;
		keyspace = bootstrapConfig.getProperty("OLS_CASSANDRA_KEYSPACE");
		appId = bootstrapConfig.getProperty("OLS_CASSANDRA_APP_ID");
		String[] contactPoints = bootstrapConfig.getProperty("OLS_CASSANDRA_CONTACT_POINT").split(",");
		List<InetSocketAddress> cpAddresses = new ArrayList<InetSocketAddress>(contactPoints.length); 
		for(String cp : contactPoints) {
			if(cp == null || cp.isBlank()) continue;
			InetSocketAddress addr = new InetSocketAddress(cp, 9042);
			if(addr.isUnresolved()) {
				logger.error("Unable to resolve Cassandra contact point address: '" + cp + "'");
			} else {
				cpAddresses.add(addr);
			}
		}
		DriverConfigLoader loader =
			    DriverConfigLoader.programmaticBuilder()
			        .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(10))
			        .build();
		this.session = CqlSession.builder()
				.withConfigLoader(loader)
				.addContactPoints(cpAddresses)
				.withLocalDatacenter(bootstrapConfig.getProperty("OLS_CASSANDRA_LOCAL_DATACENTER"))
				.build();
		validateKeyspace();
	}

	@Override
	public Stream<ConfigurationParameter> getConfigParams() {
//		List<ConfigurationParameter> configParams = new ArrayList<ConfigurationParameter>();
		SimpleStatement st = SimpleStatement.newInstance("SELECT app_id, config_param_name, config_param_value FROM " 
				+ keyspace + ".BGEO_CONFIGURATION_PARAMETERS WHERE app_id = '" + appId + "'");
//		st.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM);
		ResultSet rs = session.execute(st);
//		for (Row row : rs) {
//			configParams.add(new ConfigurationParameter(row.getString("app_id"),
//					row.getString("config_param_name"), row.getString("config_param_value")));
//		}
//		return configParams.stream(); 
		return new DatastaxResultSetRowReader(rs).asStream(ConfigurationParameter::new);
	}
	
	@Override 
	public Optional<String> getConfigParam(String name) {
		SimpleStatement st = SimpleStatement.newInstance("SELECT config_param_value FROM " + keyspace 
				+ ".BGEO_CONFIGURATION_PARAMETERS WHERE app_id = '" + appId + "' AND config_param_name = '" + name + "'");
//		st.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM);
		ResultSet rs = session.execute(st);
		Row row = rs.one();
		if(row != null) {
			return Optional.of(row.getString("config_param_value"));
		}
		return Optional.empty();
	}

	@Override
	public void setConfigParam(ConfigurationParameter param) {
		SimpleStatement statement = SimpleStatement.builder("INSERT INTO " + keyspace + ".BGEO_CONFIGURATION_PARAMETERS(app_id, config_param_name, config_param_value) VALUES(?, ?, ?)") 
				.addPositionalValues(appId, param.getConfigParamName(), param.getConfigParamValue()).build();
		session.execute(statement);
	}

	@Override
	public void removeConfigParam(ConfigurationParameter param) {
		SimpleStatement statement = SimpleStatement.builder("DELETE FROM" + keyspace + ".BGEO_CONFIGURATION_PARAMETERS WHERE app_id = ? AND config_param_name = ?")
				.addPositionalValues(appId, param.getConfigParamName()).build();
		session.execute(statement);
	}

	protected void validateKeyspace() {
		Optional<KeyspaceMetadata> ks = session.getMetadata().getKeyspace(keyspace);
		if(ks.isEmpty()) {
			logger.warn("Cassandra keyspace '" + keyspace + "' does not exist; creating.");
			session.execute("CREATE KEYSPACE " + keyspace + " WITH REPLICATION={ 'class' : 'SimpleStrategy', 'replication_factor' : " + bootstrapConfig.getProperty("OLS_CASSANDRA_REPL_FACTOR") +" };");
		}
		validateConfigParametersTable();
	}

	private void createConfigParametersTable() {
		logger.warn("Creating table " + keyspace + ".BGEO_CONFIGURATION_PARAMETERS");
		session.execute("CREATE TABLE " + keyspace + ".BGEO_CONFIGURATION_PARAMETERS("
				+ "APP_ID TEXT, "
				+ "CONFIG_PARAM_NAME TEXT, "
				+ "CONFIG_PARAM_VALUE TEXT,"
				+ "PRIMARY KEY(APP_ID, CONFIG_PARAM_NAME));");
		populateConfigParametersTable();
	}

	private void populateConfigParametersTable() {
		logger.warn("Populating table " + keyspace + ".BGEO_CONFIGURATION_PARAMETERS");
		InputStream in = getClass().getClassLoader().getResourceAsStream("bgeo_configuration_parameters.csv");
		try (CSVReader reader = new CSVReader(new InputStreamReader(new BufferedInputStream(in), Charset.forName("UTF-8")))) {
			String[] header = reader.readNext(); 
			if(header == null) {
				throw new RuntimeException("CSV file empty: bgeo_configuration_parameters.csv");
			}
			int appIdIdx = -1;
			int nameIdx = -1;
			int valueIdx = -1;
			for(int i = 0; i < header.length; i++) {
				switch(header[i].trim().toLowerCase()) {
				case "app_id":
					appIdIdx = i;
					break;
				case "config_param_name": 
					nameIdx = i;
					break;
				case "config_param_value":
					valueIdx = i;
					break;
				}
			}
			String [] row;
			PreparedStatement pStatement = session.prepare("INSERT INTO " 
					+ keyspace + ".BGEO_CONFIGURATION_PARAMETERS "
					+ "(APP_ID, CONFIG_PARAM_NAME, CONFIG_PARAM_VALUE) " 
					+ "VALUES (?, ?, ?) IF NOT EXISTS;");
			while((row = reader.readNext()) != null) {
				session.execute(pStatement.bind(row[appIdIdx], row[nameIdx], row[valueIdx]));
			}
			reader.close();
		} catch (IOException | CsvValidationException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void validateConfigParametersTable() {
		logger.info("Validating table " + keyspace + ".BGEO_CONFIGURATION_PARAMETERS");
		Optional<KeyspaceMetadata> ks = session.getMetadata().getKeyspace(keyspace);
		Optional<TableMetadata> table = ks.get().getTable("BGEO_CONFIGURATION_PARAMETERS");
		if(table.isEmpty()) {
			logger.warn("Table " + keyspace + ".BGEO_CONFIGURATION_PARAMETERS does not exist");
			createConfigParametersTable();
		} else {
			// adds any necessary parameters
			populateConfigParametersTable();
		}
		// This is where we would check that the table has the right columns
		// and make any changes required by new versions
	}

	@Override
	public void replaceWith(ConfigurationStore configStore) {
		// save BGEO_CONFIGURATION_PARAMETERS
	    // We won't truncate because if we are importing an older config it might not have values for new params
	    // Cassandra inserts act as updates if the key part already exists 
		//logger.info("Truncating table " + keyspace + ".BGEO_CONFIGURATION_PARAMETERS");
		//session.execute("TRUNCATE " + keyspace + ".BGEO_CONFIGURATION_PARAMETERS;");
		PreparedStatement pStatement = session.prepare("INSERT INTO " 
				+ keyspace + ".BGEO_CONFIGURATION_PARAMETERS "
				+ "(APP_ID, CONFIG_PARAM_NAME, CONFIG_PARAM_VALUE) " 
				+ "VALUES (?, ?, ?);");
		configStore.getConfigParams().map(configParam ->
				session.executeAsync(pStatement.bind(configParam.getAppId(), configParam.getConfigParamName(), configParam.getConfigParamValue()))
				.toCompletableFuture()).forEach(CompletableFuture::join);
	}

	@Override
	public void close() {
		session.close();
	}

}