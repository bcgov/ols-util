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

import java.util.Iterator;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

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
	public Boolean getBoolean(String column) {
		return curRow.getBoolean(column);
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
	public LineString getLineString(String column) {
			throw new UnsupportedOperationException("DatastacresultSetRowReader does not support getLineString()");
	}

	@Override
	public Polygon getPolygon() {
			throw new UnsupportedOperationException("DatastacresultSetRowReader does not support getPolygon()");
	}

	@Override
	public Polygon getPolygon(String column) {
			throw new UnsupportedOperationException("DatastacresultSetRowReader does not support getPolygon()");
	}

	@Override
	public Geometry getGeometry() {
			throw new UnsupportedOperationException("DatastacresultSetRowReader does not support getGeometry()");
	}

	@Override
	public Geometry getGeometry(String column) {
			throw new UnsupportedOperationException("DatastacresultSetRowReader does not support getGeometry()");
	}

	@Override
	public void close() {
	}
	
}