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

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import gnu.trove.map.hash.THashMap;

public abstract class XsvRowReader extends AbstractBasicWktRowReader implements RowReader {
	private static final Logger logger = LoggerFactory.getLogger(XsvRowReader.class.getCanonicalName());

	public static final String UTF8_BOM = "\uFEFF";
	private String[] nextLine;
	private Map<String,Integer> schema;
	private CSVReader reader;
	protected String fileName;
	private int readCount;

	protected XsvRowReader(GeometryFactory gf) {
		super(gf);
	}

	protected void construct(Reader inReader, char separator) {
		schema = new THashMap<String,Integer>();
		try {
			CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
			reader = new CSVReaderBuilder(inReader).withCSVParser(parser).build();
			String[] header = reader.readNext(); 
			if(header == null) {
				throw new RuntimeException("XSV file empty: " + fileName);
			}
			for(int i = 0; i < header.length; i++) {
				if(i == 0 && header[i].startsWith(UTF8_BOM)) {
					header[i] = header[i].substring(1);
				}
				schema.put(header[i].trim().toLowerCase(),i);
			}
		} catch(IOException | CsvValidationException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public boolean next() {
		try {
			// loop until we get a non-empty line or hit the end of the file
			nonempty:
			while(true) {
				// get the next line
				nextLine = reader.readNext();
				// if it is null we are at end, return false
				if(nextLine == null) return false;
				// check if it is an array of null or empty strings
				for(String entry : nextLine) {
					if(entry != null && !entry.isBlank()) {
						// not an empty line so bail out
						break nonempty;
					}
				}
				// if we get here it is an empty line so we will loop and get the next line 
			}
			// only count the non-empty lines
			readCount++;
			return true;
		} catch (IOException | CsvValidationException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns a list of the columns, in the file-specified order
	 * @return a list of the columns, in the file-specified order
	 */
	public List<String> getSchema() {
		String[] columns = new String[schema.size()];
		schema.forEach((s,i) -> columns[i] = s);
		return List.of(columns);
	}
	
	@Override
	public Object getObject(String column) {
		Integer colNum = schema.get(column.toLowerCase());
		if(colNum == null) {
			return null;
		}
		String val = nullSafeTrim(nextLine[colNum]);
		if(val == null || val.isEmpty()) {
			return null;
		}
		return val;
	}

	@Override
	public Point getPoint() {
		return getPoint("");
	}

	@Override
	public Point getPoint(String prefix) {
		Object xObj = getObject(prefix + "x");
		Object yObj = getObject(prefix + "y");
		if(xObj == null || xObj.toString().isEmpty()
				|| yObj == null || yObj.toString().isEmpty()) {
			return null;
		}
		double x = Double.valueOf(xObj.toString());
		double y = Double.valueOf(yObj.toString());
		return gf.createPoint(new Coordinate(x, y));
	}

	private static String nullSafeTrim(String string) {
		if(string == null) {
			return null;
		}
		return string.trim();
	}

	@Override
	public void close() {
		try {
			logger.info("XsvRowReader {} closed after reading: {} records", fileName == null ? "" : "for file: " + fileName, readCount);
			reader.close();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

}