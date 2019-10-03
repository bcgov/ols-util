package ca.bc.gov.ols.rowreader;

import java.util.Iterator;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

public class DatastaxResultSetRowReader implements RowReader {
	
	protected ResultSet rs;
	protected Iterator<Row> iterator;
	protected Row curRow;
	
	public DatastaxResultSetRowReader(ResultSet rs) {
		this.rs = rs;
		iterator = rs.iterator();
	}
	
	@Override
	public boolean next() {
		if(iterator.hasNext()) {
			curRow = iterator.next();
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public Object getObject(String column) {
		return curRow.getObject(column);
	}
	
	@Override
	public int getInt(String column) {
		return curRow.getInt(column);
	}
	
	@Override
	public Integer getInteger(String column) {
		return (Integer)curRow.getObject(column);
	}
	
	@Override
	public double getDouble(String column) {
		return curRow.getDouble(column);
	}
	
	@Override
	public String getString(String column) {
		return curRow.getString(column);
	}
	
	@Override
	public java.time.LocalDate getDate(String column) {
		return curRow.getLocalDate(column);
	}
	
	@Override
	public Point getPoint() {
		throw new UnsupportedOperationException("DatastacresultSetRowReader does not support getPoint()");
	}
	
	@Override
	public Point getPoint(String column) {
		throw new UnsupportedOperationException("DatastacresultSetRowReader does not support getPoint(column)");
	}
	
	@Override
	public LineString getLineString() {
			throw new UnsupportedOperationException("DatastacresultSetRowReader does not support getLineString()");
	}
	
	@Override
	public void close() {
	}
	
}