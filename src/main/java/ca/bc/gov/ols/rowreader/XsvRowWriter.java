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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;


public class XsvRowWriter implements RowWriter  {
	private static final Logger logger = LoggerFactory.getLogger(XsvRowWriter.class.getCanonicalName());

	private ICSVWriter csvWriter;
	private List<String> schema;
	private int writeCount = 0;
	private String fileName;
	private String[] data; // reusable row data storage

	public XsvRowWriter(Writer writer, char separator, List<String> schema, boolean quotes) {
		construct(writer, separator, schema, quotes);
	}
	
	public XsvRowWriter(OutputStream out, char separator, List<String> schema, boolean quotes) {
		logger.info("XsvRowWriter opened for OutputStream: {}", out);
		construct(new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8)), separator, schema, quotes);
	}

	public XsvRowWriter(File file, char separator, List<String> schema, boolean quotes) {
		logger.info("XsvRowWriter opened for file: {}", file);
		fileName = file.getPath();
		try {
			construct(new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8)), separator, schema, quotes);
		} catch(IOException ioe) {
			logger.error("Unable to open XsvWriter for file: {}", file);
			throw new RuntimeException(ioe);
		}
	}

	private void construct(Writer writer, char separator, List<String> schema, boolean quotes) {
		if(quotes) {
			csvWriter = new CSVWriterBuilder(writer).withSeparator(separator).build();
		} else {
			csvWriter = new CSVWriterBuilder(writer).withSeparator(separator).withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER).build();
		}
		// write header line
		csvWriter.writeNext(schema.toArray(new String[schema.size()]));
		this.schema = schema;
		writeCount = 0;
		data = new String[schema.size()];		
		
	}
	
	@Override
	public <T extends Object> void writeRow(Map<String, T> row) {
		for(int i = 0; i < schema.size(); i++) {
			Object obj = row.get(schema.get(i));
			if(obj == null) {
				data[i] = null;
			} else {
				data[i] = obj.toString();
			}
		}
		csvWriter.writeNext(data);
		writeCount++;
	}

	@Override
	public void close() {
		try {
			logger.info("XsvRowWriter {} closed after writing: {} records", fileName == null ? "" : "for file: " + fileName, writeCount);
			csvWriter.close();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

}
