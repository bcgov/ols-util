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
package ca.bc.gov.ols.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.operation.distance.DistanceOp;

public class LineStringSplitter {
	
	/**
	 * Splits the lineString into as many new lineStrings as needed so that the closest
	 * point on the resulting lineStrings to each of the input points is at an end-point
	 * or new split point. The number of splits of the linestring will no necessarily match
	 * the number of input points, eg. if any points are equal to each other, or at either
	 * end of the lineString. The input lineString will not be altered.
	 * 
	 * @param lineString the lineString to split
	 * @param points the points where to split the lineString
	 * @param projectedPoints an empty List that will be populated with the Projected point location for each input Point 
	 * @return
	 */
	public static List<LineString> split(LineString lineString, List<Point> points, List<Point> projectedPoints) {
		if(!projectedPoints.isEmpty()) throw new IllegalArgumentException("projectedPoints must be empty, was: " + projectedPoints.toString());
		List<LineString> lines = new ArrayList<>();
		lines.add(lineString);
		// for each input point
		for(Point p : points) {
			int closestLineIndex = -1;
			double closestDist = Double.POSITIVE_INFINITY;
			Coordinate closestCoord = null;
			// find the closest lineSegment (list will grow as we split)
			for(int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
				DistanceOp distOp = new DistanceOp(lines.get(lineIndex), p);
				double dist = distOp.distance();
				if(dist < closestDist) {
					closestDist = dist;
					closestLineIndex = lineIndex;
					closestCoord = distOp.nearestLocations()[0].getCoordinate();					
				}
			}
			// note that the closestCoord may have its coordinates rounded when made into a LineString
			// because the LineString factory has a fixed precision model while nearestLocations() function
			// uses the floating precisionModel of the input points
			// we need the returned projectedPoints to exactly match the lineString start/end points.
			Point projectedPoint;
			
			// if the point is not at the end of the closest line
			LineString line = lines.get(closestLineIndex);
			if(closestCoord.equals(line.getStartPoint().getCoordinate())) {
				projectedPoint = line.getStartPoint();				
			} else if(closestCoord.equals(line.getEndPoint().getCoordinate())) {
				projectedPoint = line.getEndPoint();
			} else {
				// split the closest linestring
				LineString[] newLineStrings = splitLineString(line, closestCoord);
				lines.remove(closestLineIndex);
				lines.add(closestLineIndex, newLineStrings[1]);
				lines.add(closestLineIndex, newLineStrings[0]);
				projectedPoint = newLineStrings[0].getEndPoint();				
			}
			projectedPoints.add(projectedPoint);
		}
		
		return lines;
	}
	
	/**
	 * Splits the lineString at the point closest to the target. If the closest point 
	 * on the linestring is at either end, the corresponding returned linestring will
	 * be an invalid 2-point LineString with the start and end points at the closest 
	 * endpoint of the input linestring.
	 * 
	 * @param lineString The LineString to be split
     * @param target The Point to use to split the lineString
     * @return an array of two lineStrings, respectively representing
     * 		the parts of the input lineString before and after the target point
	 */
	public static LineString[] split(LineString lineString, Point target) {
		Coordinate closestCoord = new DistanceOp(lineString, target).nearestLocations()[0].getCoordinate();
		if(closestCoord.equals(lineString.getStartPoint().getCoordinate())) {
			return new LineString[] {
					lineString.getFactory().createLineString(
							new Coordinate[] {lineString.getStartPoint().getCoordinate(),lineString.getStartPoint().getCoordinate()}), 
					lineString};
		}
		if(closestCoord.equals(lineString.getEndPoint().getCoordinate())) {
			return new LineString[] {lineString,
					lineString.getFactory().createLineString(
							new Coordinate[] {lineString.getEndPoint().getCoordinate(),lineString.getEndPoint().getCoordinate()})};
		}
		LineString[] newLineStrings = splitLineString(lineString, target.getCoordinate());
		return newLineStrings;
	}
	
	private static LineString[] splitLineString(LineString lineString, Coordinate target) {
		LineSegment[] lineSegments = lineSegments(lineString);
		int i = indexOfClosestLineSegment(lineSegments, target);
		Coordinate split = lineSegments[i].closestPoint(target);
		LineSegment[] splitLineSegments = new LineSegment[] {
				new LineSegment(lineSegments[i].p0, (Coordinate) split.clone()),
				new LineSegment((Coordinate) split.clone(), lineSegments[i].p1)
		};
		return new LineString[] {
				lineString.getFactory().createLineString(coordinates(Arrays.asList(lineSegments).subList(0, i), 
						Collections.singletonList(splitLineSegments[0]))),
				lineString.getFactory().createLineString(coordinates(Collections.singletonList(splitLineSegments[1]), 
						Arrays.asList(lineSegments).subList(i+1, lineSegments.length)))
		};
	}
	
	private static int indexOfClosestLineSegment(LineSegment[] lineSegments,
			Coordinate target) {
		int indexOfClosestLineSegment = 0;
		double closestDist = Double.MAX_VALUE;
		for (int i = 0; i < lineSegments.length; i++) {
			double dist = lineSegments[i].distance(target); 
			if (dist < closestDist) {
				indexOfClosestLineSegment = i;
				closestDist = dist;
			}
		}
		return indexOfClosestLineSegment;
	}
	
	private static Coordinate[] coordinates(List<LineSegment> lineSegmentsA, List<LineSegment> lineSegmentsB) {
		int size = lineSegmentsA.size() + lineSegmentsB.size();
		Coordinate[] coordinates = new Coordinate[size + 1];
		int i = 0;
		for(LineSegment seg : (Iterable<LineSegment>)Stream.concat(lineSegmentsA.stream(), lineSegmentsB.stream())::iterator) {
			coordinates[i++] = seg.p0;
		}
		if(lineSegmentsB.size() > 0) {
			coordinates[size] = lineSegmentsB.get(lineSegmentsB.size()-1).p1;
		} else {
			coordinates[size] = lineSegmentsA.get(lineSegmentsA.size()-1).p1;
		}
		return coordinates;
	}
	
	private static LineSegment[] lineSegments(LineString lineString) {
		LineSegment[] lineSegments = new LineSegment[lineString.getNumPoints()-1];
		for (int i = 1; i < lineString.getNumPoints(); i++) {
			lineSegments[i - 1] = new LineSegment(lineString.getCoordinateN(i - 1),
					lineString.getCoordinateN(i));
		}
		return lineSegments;
	}
}