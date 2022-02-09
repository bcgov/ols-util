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

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

public class MultiRowReader implements RowReader {
	
	private RowReader[] readers;
	private int curReader = 0;
	
	public MultiRowReader(RowReader ... readers) {
		this.readers = readers;
	}

	@Override
	public boolean next() {
		if(curReader >= readers.length) {
			// all readers are exhausted
			return false;
		}
		if(readers[curReader].next()) {
			// the current reader has more
			return true;
		}
		// try the next reader in the list
		curReader++;
		return next();
	}

	@Override
	public Object getObject(String column) {
		return readers[curReader].getObject(column);
	}

	@Override
	public int getInt(String column) {
		return readers[curReader].getInt(column);
	}

	@Override
	public Integer getInteger(String column) {
		return readers[curReader].getInteger(column);
	}

	@Override
	public double getDouble(String column) {
		return readers[curReader].getDouble(column);
	}

	@Override
	public String getString(String column) {
		return readers[curReader].getString(column);
	}

	@Override
	public Boolean getBoolean(String column) {
		return readers[curReader].getBoolean(column);
	}

	@Override
	public LocalDate getDate(String column) {
		return readers[curReader].getDate(column);
	}

	@Override
	public Point getPoint() {
		return readers[curReader].getPoint();
	}

	@Override
	public Point getPoint(String column) {
		return readers[curReader].getPoint(column);
	}

	@Override
	public LineString getLineString() {
		return readers[curReader].getLineString();
	}

	@Override
	public void close() {
		for(RowReader r : readers) {
			r.close();
		}
	}

}
