package ca.bc.gov.ols.config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;
import com.datastax.oss.driver.internal.core.config.typesafe.DefaultDriverConfigLoader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import au.com.bytecode.opencsv.CSVReader;

public class CassandraConfigurationStore implements ConfigurationStore {
	private static final Logger logger = LoggerFactory.getLogger(CassandraConfigurationStore.class.getCanonicalName());
	
	protected static final String APP_ID = "BGEO";

	protected Properties bootstrapConfig;

	protected String keyspace;
	
	protected CqlSession session;


	public CassandraConfigurationStore(Properties bootstrapConfig) {
		this.bootstrapConfig = bootstrapConfig;
		keyspace = bootstrapConfig.getProperty("OLS_CASSANDRA_KEYSPACE");
		this.session = CqlSession.builder()
				.withConfigLoader(new DefaultDriverConfigLoader(() -> {
						ConfigFactory.invalidateCaches();
						return ConfigFactory.load(getClass().getClassLoader()).getConfig(DefaultDriverConfigLoader.DEFAULT_ROOT_PATH);
				}))
				.withClassLoader(getClass().getClassLoader())
				.addContactPoint(new InetSocketAddress(bootstrapConfig.getProperty("OLS_CASSANDRA_CONTACT_POINT"), 9042))
				.withLocalDatacenter(bootstrapConfig.getProperty("OLS_CASSANDRA_LOCAL_DATACENTER"))
				.build();
		validateKeyspace();
	}

	@Override
	public Stream<ConfigurationParameter> getConfigParams() {
		List<ConfigurationParameter> configParams = new ArrayList<ConfigurationParameter>();
		ResultSet rs = session.execute("SELECT app_id, config_param_name, config_param_value FROM " 
				+ keyspace + ".BGEO_CONFIGURATION_PARAMETERS WHERE app_id = '" + APP_ID + "'" );
		for (Row row : rs) {
			configParams.add(new ConfigurationParameter(row.getString("app_id"),
					row.getString("config_param_name"), row.getString("config_param_value")));
		}
		return configParams.stream(); 
	}
	
	@Override 
	public Optional<String> getConfigParam(String name) {
		ResultSet rs = session.execute("SELECT config_param_value FROM " + keyspace 
				+ ".BGEO_CONFIGURATION_PARAMETERS WHERE app_id = '" + APP_ID + "' AND config_param_name = '" + name + "'");
		Row row = rs.one();
		if(row != null) {
			return Optional.of(row.getString("config_param_value"));
		}
		return Optional.empty();
	}

	@Override
	public void setConfigParam(ConfigurationParameter param) {
		SimpleStatement statement = SimpleStatement.builder("INSERT INTO " + keyspace + ".BGEO_CONFIGURATION_PARAMETERS(app_id, config_param_name, config_param_value) VALUES(?, ?, ?)") 
				.addPositionalValues(APP_ID, param.getConfigParamName(), param.getConfigParamValue()).build();
		session.execute(statement);
	}

	@Override
	public void removeConfigParam(ConfigurationParameter param) {
		SimpleStatement statement = SimpleStatement.builder("DELETE FROM" + keyspace + ".BGEO_CONFIGURATION_PARAMETERS WHERE app_id = ? AND config_param_name = ?")
				.addPositionalValues(APP_ID, param.getConfigParamName()).build();
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
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
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
		//List<CompletionStage<AsyncResultSet>> futures = new ArrayList<CompletionStage<AsyncResultSet>>();
		configStore.getConfigParams().map(configParam ->
				session.executeAsync(pStatement.bind(configParam.getAppId(), configParam.getConfigParamName(), 
					configParam.getConfigParamValue())).toCompletableFuture())
				.map(CompletableFuture::join);
	}

	@Override
	public void close() {
		session.close();
	}

}