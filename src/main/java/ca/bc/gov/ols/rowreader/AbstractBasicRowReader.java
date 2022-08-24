package ca.bc.gov.ols.rowreader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public abstract class AbstractBasicRowReader implements RowReader {
	public static final int NULL_INT_VALUE = Integer.MIN_VALUE;
	public String defaultGeometryColumn = "wkt";

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
		return (Point)getGeometry();
	}
	
	@Override
	public Point getPoint(String column) {
		return (Point)getGeometry(column);
	}
	
	@Override
	public LineString getLineString() {
		return (LineString)getGeometry(defaultGeometryColumn);
	}

	@Override
	public LineString getLineString(String column) {
		return (LineString)getGeometry(column);
	}
	
	@Override
	public Polygon getPolygon() {
		return (Polygon)getGeometry(defaultGeometryColumn);
	}

	@Override
	public Polygon getPolygon(String column) {
		return (Polygon)getGeometry(column);
	}

	@Override
	public Geometry getGeometry() {
		return getGeometry(defaultGeometryColumn);
	}
	
}
