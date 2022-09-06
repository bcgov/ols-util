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
package ca.bc.gov.ols.rowreader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.locationtech.jts.geom.GeometryFactory;

public class CsvRowReader extends XsvRowReader {

	public CsvRowReader(String fileName, GeometryFactory gf) {
		super(gf);
		this.fileName = fileName; 
		try {
			construct(new BufferedReader(new FileReader(fileName)), ',');
		} catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	public CsvRowReader(String fileName, GeometryFactory gf, Charset charset) {
		super(gf);
		this.fileName = fileName; 
		try {
			construct(new BufferedReader(new FileReader(fileName, charset)), ',');
		} catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	public CsvRowReader(InputStream inStream, GeometryFactory gf) {
		super(gf);
		construct(new InputStreamReader(inStream), ',');
	}
	
	public CsvRowReader(Reader inReader, GeometryFactory gf) {
		super(gf);
		construct(inReader, ',');
	}
	
}