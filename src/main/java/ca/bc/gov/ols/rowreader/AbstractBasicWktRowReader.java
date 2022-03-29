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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public abstract class AbstractBasicWktRowReader extends AbstractBasicRowReader {
	public static final int NULL_INT_VALUE = Integer.MIN_VALUE;

	protected GeometryFactory gf;
	private WKTReader wktReader;
	
	public AbstractBasicWktRowReader(GeometryFactory gf) {
		this.gf = gf;
		if(gf != null) {
			wktReader = new WKTReader(gf);
		}
	}
	
	@Override
	public abstract Object getObject(String column);
	
	@Override
	public int getInt(String column) {
		Object result = getObject(column);
		if(result == null) {
			return NULL_INT_VALUE;
		}
		return Integer.valueOf(result.toString());
	}
	
	@Override
	public Integer getInteger(String column) {
		Object result = getObject(column);
		if(result == null || result.toString().isEmpty()) {
			return null;
		}
		return Integer.valueOf(result.toString());
	}
	
	@Override
	public double getDouble(String column) {
		Object result = getObject(column);
		if(result == null) {
			return Double.NaN;
		}
		return Double.valueOf(result.toString());
	}
	
	@Override
	public String getString(String column) {
		Object result = getObject(column);
		if(result == null) {
			return null;
		}
		return result.toString();
	}
	
	@Override
	public Boolean getBoolean(String column) {
		Object result = getObject(column);
		if(result == null || result.toString().isEmpty()) {
			return null;
		}
		if(result instanceof String) {
			String str = (String)result;
			if("FALSE".equalsIgnoreCase(str) 
					|| "F".equalsIgnoreCase(str) 
					|| "N".equalsIgnoreCase(str) 
					|| "0".equals(str)) {
				return Boolean.FALSE;
			} else if("TRUE".equalsIgnoreCase(str) 
					|| "T".equalsIgnoreCase(str) 
					|| "Y".equalsIgnoreCase(str) 
					|| "1".equals(str)) {
				return Boolean.TRUE;
			}
		} else if(result instanceof Integer) {
			int i = (Integer)result;
			if(i == 0) {
				return Boolean.FALSE;
			} else if(i == 1) {
				return Boolean.TRUE;
			}
		}
		return null;
	}
	
	@Override
	public LocalDate getDate(String column) {
		Object result = getObject(column);
		if(result == null) {
			return null;
		}
		String dateStr = result.toString();
		if(dateStr.contains("-")) {
			return LocalDate.parse(dateStr);
		}
		return DateTimeFormatter.BASIC_ISO_DATE.parse(dateStr, LocalDate::from);
	}
	
	@Override
	public Point getPoint() {
		Object xObj = getObject("x");
		Object yObj = getObject("y");
		if(xObj == null || yObj == null) {
			return null;
		}
		double x = Double.valueOf(xObj.toString());
		double y = Double.valueOf(yObj.toString());
		return gf.createPoint(new Coordinate(x, y));
	}
	
	@Override
	public Geometry getGeometry(String column) {
		Object result = getObject(column);
		if(result == null) {
			return null;
		}
		try {
			Geometry geom = wktReader.read(result.toString());
			return geom;
		} catch(ParseException pe) {
			throw new RuntimeException("ParseException while parsing LineString WKB", pe);
		}
	}

}