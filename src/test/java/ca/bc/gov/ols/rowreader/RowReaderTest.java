package ca.bc.gov.ols.rowreader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;


public class RowReaderTest {
	
	private static final String BASE_FILE_NAME = "row_reader_test";
	private static List<String> schema;
	private static Map<String,Object> testRow;		
	static {
		testRow = new HashMap<String, Object>();
		testRow.put("boolean_true", Boolean.TRUE);
		testRow.put("boolean_false", Boolean.FALSE);
		testRow.put("string_true", "true");
		testRow.put("string_false", "false");
		testRow.put("string_t", "t");
		testRow.put("string_f", "f");
		testRow.put("string_y", "y");
		testRow.put("string_n", "n");
		testRow.put("string_1", "1");
		testRow.put("string_0", "0");
		testRow.put("integer_1", 1);
		testRow.put("integer_0", 0);
		schema = new ArrayList<String>(testRow.keySet());
	}
	
	@Test
	public void testCsv() {
		String filename = BASE_FILE_NAME + ".csv";
		File file = new File(filename);
		try(XsvRowWriter rw = new XsvRowWriter(file, ',', schema, true)) {
			writeRows(rw);
		}
		try(CsvRowReader rr = new CsvRowReader(filename, new GeometryFactory())) {
			readRows(rr);
		}
		file.delete();
	}

	@Test
	public void testTsv() {
		String filename = BASE_FILE_NAME + ".tsv";
		File file = new File(filename);
		try(XsvRowWriter rw = new XsvRowWriter(file, '\t', schema, false)) {
			writeRows(rw);
		}
		try(TsvRowReader rr = new TsvRowReader(filename, new GeometryFactory())) {
			readRows(rr);
		}
		file.delete();
	}

	@Test
	public void testJson() {
		String filename = BASE_FILE_NAME + ".json";
		File file = new File(filename);
		try(JsonRowWriter rw = new JsonRowWriter(file, "test")) {
			writeRows(rw);
		}
		try(JsonRowReader rr = new JsonRowReader(filename, new GeometryFactory())) {
			readRows(rr);
		}
		file.delete();
	}

	private void writeRows(RowWriter rw) {
		rw.writeRow(testRow);
	}

	private void readRows(RowReader rr) {
		rr.next();
		Assertions.assertTrue(rr.getBoolean("boolean_true"));
		Assertions.assertTrue(rr.getBoolean("string_true"));
		Assertions.assertTrue(rr.getBoolean("string_t"));
		Assertions.assertTrue(rr.getBoolean("string_y"));
		Assertions.assertTrue(rr.getBoolean("string_1"));
		Assertions.assertTrue(rr.getBoolean("integer_1"));
		Assertions.assertFalse(rr.getBoolean("boolean_false"));
		Assertions.assertFalse(rr.getBoolean("string_false"));
		Assertions.assertFalse(rr.getBoolean("string_f"));
		Assertions.assertFalse(rr.getBoolean("string_n"));
		Assertions.assertFalse(rr.getBoolean("string_0"));
		Assertions.assertFalse(rr.getBoolean("integer_0"));
	}
	
}
