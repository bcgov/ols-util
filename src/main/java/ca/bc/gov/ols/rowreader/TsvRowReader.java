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

import org.locationtech.jts.geom.GeometryFactory;

public class TsvRowReader extends XsvRowReader {

	public TsvRowReader(String fileName, GeometryFactory gf) {
		super(gf);
		this.fileName = fileName; 
		try {
			construct(new BufferedReader(new FileReader(fileName)), '\t');
		} catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	public TsvRowReader(InputStream inStream, GeometryFactory gf) {
		super(gf);
		construct(new InputStreamReader(inStream), '\t');
	}
	
	public TsvRowReader(Reader inReader, GeometryFactory gf) {
		super(gf);
		construct(inReader, '\t');
	}
	
}