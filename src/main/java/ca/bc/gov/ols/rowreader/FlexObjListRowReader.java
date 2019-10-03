/**
 * Copyright 2008-2015, Province of British Columbia
 * All rights reserved.
 */
package ca.bc.gov.ols.rowreader;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

public class FlexObjListRowReader implements RowReader {
	
	private Collection<FlexObj> data;
	private Iterator<FlexObj> iter = null;
	private FlexObj curRow = null;
	
	public FlexObjListRowReader(Collection<FlexObj> data) {
		this.data = data;
	}
	
	@Override
	public boolean next() {
		if(iter == null) {
			iter = data.iterator();
		}
		if(iter.hasNext()) {
			curRow = iter.next();
			return true;
		}
		return false;
	}
	
	@Override
	public Object getObject(String column) {
		if(curRow == null) {
			return null;
		}
		return curRow.get(column);
	}
	
	@Override
	public int getInt(String column) {
		if(curRow == null || curRow.get(column) == null) {
			return NULL_INT_VALUE;
		}
		return (Integer)(curRow.get(column));
	}
	
	@Override
	public Integer getInteger(String column) {
		if(curRow == null) {
			return 0;
		}
		return (Integer)(curRow.get(column));
	}
	
	@Override
	public double getDouble(String column) {
		if(curRow == null) {
			return 0;
		}
		return (Double)(curRow.get(column));
	}
	
	@Override
	public String getString(String column) {
		if(curRow == null) {
			return null;
		}
		return (String)(curRow.get(column));
	}
	
	@Override
	public LocalDate getDate(String column) {
		if(curRow == null) {
			return null;
		}
		return (LocalDate)(curRow.get(column));
	}
	
	@Override
	public Point getPoint() {
		if(curRow == null) {
			return null;
		}
		return (Point)(curRow.get("geom"));
	}
	
	@Override
	public Point getPoint(String col) {
		if(curRow == null) {
			return null;
		}
		return (Point)(curRow.get(col));
	}
	
	@Override
	public LineString getLineString() {
		if(curRow == null) {
			return null;
		}
		return (LineString)(curRow.get("geom"));
	}
	
	@Override
	public void close() {
		data = null;
		iter = null;
		curRow = null;
	}
	
}
