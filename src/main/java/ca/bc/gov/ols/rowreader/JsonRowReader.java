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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import gnu.trove.map.hash.THashMap;

public class JsonRowReader extends AbstractBasicRowReader {
	private static final Logger logger = LoggerFactory.getLogger(JsonRowReader.class.getCanonicalName());

	private JsonReader jsonReader = null;
	private GeometryFactory gf = null;
	private Map<String,Object> curRow = null;
	private int readCount = 0;
	private Gson gson = new Gson();
	static ArrayList<Coordinate> coordBuffer = new ArrayList<Coordinate>(1000);
	private Map<String,String> dates = new HashMap<String,String>();
	
	
	public JsonRowReader(String fileName, GeometryFactory gf) {
		this.gf = gf;
		try {
			construct(new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8)));
		} catch(IOException ioe) {
			logger.error("Error opening stream for file {}.", fileName, ioe);
			throw new RuntimeException(ioe);
		}
	}
	
	public JsonRowReader(InputStream inStream, GeometryFactory gf) {
		this.gf = gf;
		construct(new InputStreamReader(inStream, StandardCharsets.UTF_8));
	}
	
	public JsonRowReader(Reader inReader, GeometryFactory gf) {
		this.gf = gf;
		construct(inReader);
	}


	private void construct(Reader inReader) {
		jsonReader = new JsonReader(inReader);
		try {
			JsonToken tok = jsonReader.peek();
			if(tok == JsonToken.BEGIN_ARRAY) {
				jsonReader.beginArray(); // array of feature types
				jsonReader.beginObject(); // dates feature type
				while(jsonReader.hasNext()) {
					if("features".equals(jsonReader.nextName())) {
						jsonReader.beginArray(); // features
						jsonReader.beginObject(); // feature
						while(jsonReader.hasNext()) {
							if("properties".equals(jsonReader.nextName())) {
								jsonReader.beginObject();
								while(jsonReader.hasNext()) {
									String name = jsonReader.nextName().toUpperCase();
									String date = jsonReader.nextString();
									dates.put(name, date);
								}
								jsonReader.endObject(); // properties
							} else {
								jsonReader.skipValue();
							}
						}
						jsonReader.endObject(); // feature
						jsonReader.endArray(); // features
						break;
					} else {
						jsonReader.skipValue();
					}
				}
				jsonReader.endObject(); // dates feature type
			}
			jsonReader.beginObject(); // actual data feature type
			while(jsonReader.hasNext()) {
				if("features".equals(jsonReader.nextName())) {
					jsonReader.beginArray();
					break;
				} else {
					jsonReader.skipValue();
				}
			}
			readCount = 0;
		} catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	@Override
	public boolean next() {
		curRow = new THashMap<String,Object>();
		try {
			if(jsonReader.hasNext()) {
				jsonReader.beginObject();
				// loop over feature object's variables
				while(jsonReader.hasNext()) {
					switch(jsonReader.nextName()) {
					case "geometry":
						curRow.put("geom", parseJsonGeometry(jsonReader, gf));
						break;
					case "properties":
						jsonReader.beginObject();
						while(jsonReader.hasNext()) {
							String name = jsonReader.nextName().toLowerCase();
							switch(jsonReader.peek()) {
							case STRING:
								curRow.put(name, jsonReader.nextString());
								break;
							case NUMBER:
								curRow.put(name, new BigDecimal(jsonReader.nextString()));
					            break;
					        case BOOLEAN:
					        	curRow.put(name, jsonReader.nextBoolean());
					            break;
					        case NULL:
					        	jsonReader.nextNull();
					            curRow.put(name, null);
					            break;
					        case BEGIN_OBJECT:
					        	curRow.put(name, gson.fromJson(jsonReader, JsonObject.class));
					        	break;
							default:
								jsonReader.skipValue();
							}
						}
						jsonReader.endObject();
						break;
					default:
						jsonReader.skipValue();
					}
				}
				jsonReader.endObject();
				readCount++;
				return true;
			} else {
				return false;
			}
		} catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	public static Geometry parseJsonGeometry(JsonReader jsonReader, GeometryFactory gf) throws IOException {
		JsonToken tok = jsonReader.peek();
		if(tok.equals(JsonToken.NULL)) {
			jsonReader.nextNull();
			return null;
		}
		Geometry geom = null;
		jsonReader.beginObject();
		String type = null;
		while(jsonReader.hasNext()) {
			double x;
			double y;
			switch(jsonReader.nextName()) {
			case "type":
				type = jsonReader.nextString();
				break;
			case "coordinates":
				if(type == null) {
					throw new RuntimeException("GeoJSON geometry type must come before coordinates.");
				}
				switch(type) {
				case "Point":
					jsonReader.beginArray();
					x = jsonReader.nextDouble();
					y = jsonReader.nextDouble();
					jsonReader.endArray();
					geom =  gf.createPoint(new Coordinate(x,y));
					break;
				case "LineString":
					coordBuffer.clear();
					jsonReader.beginArray();
					while(jsonReader.hasNext()) {
						jsonReader.beginArray();
						x = jsonReader.nextDouble();
						y = jsonReader.nextDouble();
						coordBuffer.add(new Coordinate(x,y));
						jsonReader.endArray();
					}
					jsonReader.endArray();
					geom = gf.createLineString(coordBuffer.toArray(new Coordinate[coordBuffer.size()]));
					break;
				default:
					throw new RuntimeException("Unsupported GeoJSON geometry type: " + type );
				}
				break;
			default:
				jsonReader.skipValue();
			}
		}
		jsonReader.endObject();
		return geom;
	}
	
	@Override
	public Object getObject(String column) {
		if(curRow == null) {
			return null;
		}
		return curRow.get(column.toLowerCase());
	}
	
	private BigDecimal getNumber(String column) {
		if(curRow == null) return null;
		Object value = curRow.get(column.toLowerCase());
		if(value == null) return null;
		if(value instanceof String) {
			if(((String)value).isEmpty()) {
				return null;
			} else {
				//TODO could try to parse the string as a number
				return null;
			}
		}
		return (BigDecimal)value;
	}
	@Override
	public int getInt(String column) {
		BigDecimal value = getNumber(column);
		if(value == null) {
			return NULL_INT_VALUE;
		}
		return value.intValue();
	}

	@Override
	public Integer getInteger(String column) {
		BigDecimal value = getNumber(column);
		if(value == null) {
			return null;
		}
		return value.intValue();
	}
	
	@Override
	public double getDouble(String column) {
		BigDecimal value = getNumber(column);
		if(value == null) {
			return Double.NaN;
		}
		return value.doubleValue();
	}
	
	@Override
	public String getString(String column) {
		if(curRow == null) {
			return null;
		}
		return (String)(curRow.get(column.toLowerCase()));
	}

	@Override
	public JsonObject getJson(String column) {
		if(curRow == null) {
			return null;
		}
		return (JsonObject)(curRow.get(column.toLowerCase()));
	}

	@Override
	public LocalDate getDate(String column) {
		if(curRow == null) {
			return null;
		}
		return (LocalDate)(curRow.get(column.toLowerCase()));
	}
	
	@Override
	public Geometry getGeometry(String column) {
		if(curRow == null) {
			return null;
		}
		// ignore column, only valid name is geom
		return (Geometry)(curRow.get("geom"));
	}
	
	public Map<String, String> getDates() {
		return dates;
	}

	@Override
	public void close() {
		try {
			logger.info("JsonRowReader closed after reading: {} records", readCount);
			jsonReader.endArray();
			jsonReader.endObject();
			jsonReader.close();
			jsonReader = null;
			gf = null;
			curRow = null;
		} catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}


}
