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
