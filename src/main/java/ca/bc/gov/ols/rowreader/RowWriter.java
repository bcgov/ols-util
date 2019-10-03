/**
 * Copyright 2008-2019, Province of British Columbia
 *  All rights reserved.
 */
package ca.bc.gov.ols.rowreader;

import java.util.Map;

public interface RowWriter {

	public abstract void close();

	public abstract <T extends Object> void writeRow(Map<String, T> row);

}