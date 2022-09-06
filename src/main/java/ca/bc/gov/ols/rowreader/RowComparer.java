package ca.bc.gov.ols.rowreader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.map.hash.THashMap;

public class RowComparer {
	private static final Logger logger = LoggerFactory.getLogger(RowComparer.class.getCanonicalName());
	
	private RowReader rr1;
	private RowReader rr2;
	private int outputLimit = 10;
	private Function<FlexObj, Boolean> filter = o -> true;
	private boolean outputAverageDistance = false;
	private RowWriter geomDiffRowWriter;
	private String geomCol = "geom";
	
	public RowComparer(RowReader rr1, RowReader rr2) {
		this.rr1 = rr1;
		this.rr2 = rr2;
	}
	
	public void setOutputLimit(int limit) {
		this.outputLimit = limit;
	}
	
	public void setFilter(Function<FlexObj, Boolean> filter) {
		this.filter = filter;
	}
	
	public void setOutputAverageDistance() {
		outputAverageDistance = true;
	}
	
	public void setOutputGeomDiff(RowWriter rw) {
		geomDiffRowWriter = rw;
	}
	
	public void compare(List<String> compareCols) {
		compare(compareCols, compareCols.get(0));
	}

	public void compare(List<String> compareCols, String keyCol) {
		compare(compareCols, compareCols, o -> o.get(keyCol).toString());
	}

	public void compare(List<String> allCols, List<String> compareCols, Function<FlexObj, String> getKey) {
		Map<String, Integer> schema = FlexObj.createSchema(allCols);
		List<FlexObj> list1 = new ArrayList<FlexObj>();
		List<FlexObj> list2 = new ArrayList<FlexObj>();
		int filteredCount1 = 0, filteredCount2 = 0;
		while(rr1.next()) {
			FlexObj rec = new FlexObj(schema);
			for(String col : allCols) {
				rec.set(col, rr1.getObject(col));
			}
			if(filter.apply(rec)) {
				list1.add(rec);
			} else {
				filteredCount1++;
			}
		}
		while(rr2.next()) {
			FlexObj rec = new FlexObj(schema);
			for(String col : allCols) {
				rec.set(col, rr2.getObject(col));
			}
			if(filter.apply(rec)) {
				list2.add(rec);
			} else {
				filteredCount2++;
			}
		}
		Comparator<FlexObj> comparator = (FlexObj o1, FlexObj o2) -> getKey.apply(o1).compareTo(getKey.apply(o2));
		list1.sort(comparator);
		list2.sort(comparator);
		int idx1 = 0;
		int idx2 = 0;
		int matches = 0;
		int diffs = 0;
		int extras = 0;
		int missing = 0;
		Double totalDistance = 0d;
		Double maxDist = 0d;
		while(idx1 < list1.size() && idx2 < list2.size()) {
			FlexObj o1 = list1.get(idx1);
			FlexObj o2 = list2.get(idx2);
			Object key1 = getKey.apply(o1);
			Object key2 = getKey.apply(o2);
			if(key1.equals(key2)) {
				// compare the two and output differences if any
				StringBuilder diff = new StringBuilder();
				for(String col : compareCols) {
					Object val1 = o1.get(col);
					Object val2 = o2.get(col);
					if(!Objects.equals(val1, val2)) {
						diff.append(col + ": " + val1 + " | " + val2 + " ");
					}
				}
				if(diff.length() == 0) {
					matches++;
				} else {
					diffs++;
					if(outputLimit < 0 || diffs <= outputLimit) {
						logger.info("{} diff: {}", key1, diff);
					}
				}
				if(outputAverageDistance) {
					double dist = ((Geometry)o1.get(geomCol)).distance((Geometry)o2.get(geomCol));
					totalDistance += dist;
					if(dist > maxDist) maxDist = dist;
				}
				if(geomDiffRowWriter != null) {
					Map<String, Object> row = new THashMap<String, Object>();
					row.put("key", key1);
					DistanceOp distOp = new DistanceOp((Geometry)o1.get(geomCol), (Geometry)o2.get(geomCol));
					Coordinate[] coords = distOp.nearestPoints();
					LineString diffLine = ((Geometry)o1.get(geomCol)).getFactory().createLineString(coords);
					row.put("geom", diffLine);
					geomDiffRowWriter.writeRow(row);
				}
				idx1++;
				idx2++;
			} else if(key1.toString().compareTo(key2.toString()) < 0) {
				// record "o1" in list1 is not in list2
				extras++;
				if(outputLimit < 0 || extras <= outputLimit) {
					logger.info("+{}", key1);
				}
				idx1++;
			} else {
				// record "o2" in list2 is not in list1
				missing++;
				if(outputLimit < 0 || missing <= outputLimit) {
					logger.info("-{}", key2);
				}
				idx2++;
			}
		}
		// output any extra unmatched output
		for(; idx1 < list1.size(); idx1++) {
			// record "a" is not in the output we are comparing to
			FlexObj o1 = list1.get(idx1);
			extras++;
			if(outputLimit < 0 || extras <= outputLimit) {
				logger.info("+{}", getKey.apply(o1));
			}
		}
		// output any extra unmatched compared output
		for(; idx2 < list2.size(); idx2++) {
			// record "o2" is in the compared output but not ours
			FlexObj o2 = list2.get(idx2);
			missing++;
			if(outputLimit < 0 || missing <= outputLimit) {
				logger.info("-{}", getKey.apply(o2));
			}
			
		}
		logger.info("matches: {}, diffs: {}, extras: {}, missing: {}, filtered: {}/{}", matches, diffs, extras, missing, filteredCount1, filteredCount2);
		if(outputAverageDistance) {
			logger.info("average distance between matches and diffs: {} max distance: {}", totalDistance / (matches+diffs), maxDist);
		}
	}
}
