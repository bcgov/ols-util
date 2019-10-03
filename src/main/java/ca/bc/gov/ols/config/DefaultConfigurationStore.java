package ca.bc.gov.ols.config;

import java.io.InputStream;
import java.io.OutputStream;

public class DefaultConfigurationStore extends FileConfigurationStore {

	public DefaultConfigurationStore() {
	}

	@Override
	protected InputStream getInputStreamRaw(String filename) {
		return getClass().getClassLoader().getResourceAsStream(filename);
	}

	@Override
	protected OutputStream getOutputStreamRaw(String filename) {
		throw new RuntimeException("Can't write to DefaultConfigurationStore");
	}
	
}
