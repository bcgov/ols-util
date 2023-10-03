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
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * RowReader defines a generic interface similar to JDBC ResultSet, allowing for compatibility with
 * other tabular data sources.
 * 
 * @author chodgson@refractions.net
 */
public interface RowReader extends AutoCloseable {
	static final int NULL_INT_VALUE = Integer.MIN_VALUE;

	/**
	 * Increments the row pointer to the next row of data.
	 * 
	 * @return false if there are no more rows of data available, true otherwise
	 */
	public boolean next();
	
	/**
	 * Returns the value of the specified column as an Object.
	 * 
	 * @param column the name of the column whose value to return
	 * @return the value of the specified column
	 */
	public Object getObject(String column);
	
	/**
	 * Returns the value of the specified column as an int.
	 * 
	 * @param column the name of the column whose value to return
	 * @return the value of the specified column
	 */
	public int getInt(String column);
		
	/**
	 * Returns the value of the specified column as an Integer.
	 * 
	 * @param column the name of the column whose value to return
	 * @return the value of the specified column
	 */
	public Integer getInteger(String column);
	
	/**
	 * Returns the value of the specified column as a double.
	 * 
	 * @param column the name of the column whose value to return
	 * @return the value of the specified column
	 */
	public double getDouble(String column);
	
	/**
	 * Returns the value of the specified column as a String.
	 * 
	 * @param column the name of the column whose value to return
	 * @return the value of the specified column
	 */
	public String getString(String column);
	
	/**
	 * Returns the value of the specified column as a JsonElement.
	 * 
	 * @param column the name of the column whose value to return
	 * @return the value of the specified column
	 */
	default public JsonObject getJson(String column) {
		Object result = getObject(column);
		if(result == null) {
			return null;
		}
		return (JsonObject)JsonParser.parseString(result.toString());
	}
	
	/**
	 * Returns the value of the specified column as a Boolean.
	 * 
	 * @param column the name of the column whose value to return
	 * @return the value of the specified column
	 */
	public Boolean getBoolean(String column);
	
	/**
	 * Returns the value of the specified column as a Date.
	 * 
	 * @param column the name of the column whose value to return
	 * @return the value of the specified column
	 */
	public LocalDate getDate(String column);
	
	/**
	 * Returns a Point object for the default location represented by this row of data.
	 * 
	 * @return the Point location of this row of data
	 */
	public Point getPoint();
	
	/**
	 * Returns a Point object for the named column.
	 * 
	 * @param column the name of the column to get the point from.
	 * @return the point object from the named column
	 */
	Point getPoint(String column);
	
	/**
	 * Returns a LineString object for the linear geometry represented by this row of data. Uses the
	 * default column name "wkt" to reference the geometry column.
	 * 
	 * @return the linear geometry represented by this row of data
	 */
	public LineString getLineString();

	/**
	 * Returns a LineString object for the linear geometry represented by this row of data. Uses the
	 * given column name to reference the geometry column.
	 * 
	 * @param column the name of the column to get the point from
	 * @return the linear geometry represented by this row of data
	 */
	public LineString getLineString(String column);

	/**
	 * Returns a Polygon object for the polygonal geometry represented by this row of data. Uses the
	 * default column name "wkt" to reference the geometry column.
	 * 
	 * @return the polygon geometry represented by this row of data
	 */
	public Polygon getPolygon();

	/**
	 * Returns a Polygon object for the polygonal geometry represented by this row of data. Uses the
	 * given column name to reference the geometry column.
	 * 
	 * @param column the name of the column to get the point from
	 * @return the polygon geometry represented by this row of data
	 */
	public Polygon getPolygon(String column);

	/**
	 * Returns a Geometry object for the geometry represented by this row of data. Uses the
	 * default column name "wkt" to reference the geometry column.
	 * 
	 * @return the polygon geometry represented by this row of data
	 */
	public Geometry getGeometry();
	
	/**
	 * Returns a Geometry object for the geometry represented by this row of data. Uses the
	 * given column name to reference the geometry column.
	 * 
	 * @param column the name of the column to get the point from
	 * @return the polygon geometry represented by this row of data
	 */
	public Geometry getGeometry(String column);
	
	/**
	 * Returns the value of the specified column as a Uuid.
	 * 
	 * @param column the name of the column whose value to return
	 * @return the linear geometry represented by this row of data
	 */
	public default UUID getUuid(String column) {
		String s = getString(column);
		if(s == null) return null;
		return UUID.fromString(s);
	}
	
	default public <T> Stream<T> asStream(Function<RowReader, T> mappingFunction) {
		return Stream.generate(() -> null)
				.takeWhile(x -> this.next())
				.map(n -> mappingFunction.apply(this));
	}
	
	/**
	 * Closes this RowReader, releasing any resources (file handles, database handles/connections,
	 * etc.) associated with it.
	 */
	public void close();

}
