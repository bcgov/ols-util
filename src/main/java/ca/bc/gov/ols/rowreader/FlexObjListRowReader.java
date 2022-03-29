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
import java.util.Collection;
import java.util.Iterator;

import org.locationtech.jts.geom.Geometry;

public class FlexObjListRowReader extends AbstractBasicRowReader {
	
	private Collection<FlexObj> data;
	private Iterator<FlexObj> iter = null;
	private FlexObj curRow = null;
	
	public FlexObjListRowReader(Collection<FlexObj> data) {
		this.data = data;
		defaultGeometryColumn = "geom";
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
	public Boolean getBoolean(String column) {
		if(curRow == null) {
			return null;
		}
		return (Boolean)(curRow.get(column));
	}
	
	@Override
	public LocalDate getDate(String column) {
		if(curRow == null) {
			return null;
		}
		return (LocalDate)(curRow.get(column));
	}
	
	@Override
	public Geometry getGeometry(String column) {
		if(curRow == null) {
			return null;
		}
		return (Geometry)curRow.get(column);
	}
	
	@Override
	public void close() {
		data = null;
		iter = null;
		curRow = null;
	}
	
}
