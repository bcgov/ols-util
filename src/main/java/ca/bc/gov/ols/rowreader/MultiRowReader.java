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

import org.locationtech.jts.geom.Geometry;

public class MultiRowReader extends AbstractBasicRowReader {
	
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
	public Geometry getGeometry(String column) {
		return readers[curReader].getGeometry(column);
	}

	@Override
	public void close() {
		for(RowReader r : readers) {
			r.close();
		}
	}

}
