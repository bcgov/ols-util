/**
 * Copyright 2008-2019, Province of British Columbia
 *  All rights reserved.
 */
package ca.bc.gov.ols.rowreader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;


public class XsvRowWriter implements RowWriter  {
	private static final Logger logger = LoggerFactory.getLogger(XsvRowWriter.class.getCanonicalName());

	private CSVWriter csvWriter;
	private List<String> schema;
	private int writeCount = 0;
	private String[] data; // reusable row data storage

	public XsvRowWriter(Writer writer, char separator, List<String> schema, boolean quotes) {
		construct(writer, separator, schema, quotes);
	}
	
	public XsvRowWriter(OutputStream out, char separator, List<String> schema, boolean quotes) {
		construct(new BufferedWriter(new OutputStreamWriter(out)), separator, schema, quotes);
	}

	public XsvRowWriter(File file, char separator, List<String> schema, boolean quotes) {
		try {
			construct(new BufferedWriter(new FileWriter(file)), separator, schema, quotes);
		} catch(IOException ioe) {
			logger.error("Unable to open XsvWriter for file: {}", file);
		}
	}

	private void construct(Writer writer, char separator, List<String> schema, boolean quotes) {
		logger.info("XsvRowWriter opened for writer: {}", writer);
		if(quotes) {
			csvWriter = new CSVWriter(writer, separator);
		} else {
			csvWriter = new CSVWriter(writer, separator, CSVWriter.NO_QUOTE_CHARACTER);
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
			logger.info("CsvRowWriter closed after writing: {} records", writeCount);
			csvWriter.close();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

}
