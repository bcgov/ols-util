package ca.bc.gov.ols.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import static org.junit.jupiter.api.Assertions.*;

class LineStringSplitterTest {

	@Test
	void testSplitLineStringListOfPoint() throws ParseException {
		WKTReader reader = new WKTReader();
		LineString ls = (LineString)reader.read("LINESTRING (0 0, 0 10)");
		Point p1 = (Point)reader.read("POINT (1 0)");
		Point p2 = (Point)reader.read("POINT (1 5)");
		Point p3 = (Point)reader.read("POINT (1 7)");
		Point p4 = (Point)reader.read("POINT (1 10)");
		List<Point> points = List.of(p4,p1,p2,p3,p2);
		List<Point> projectedPoints = new ArrayList<>();
		List<LineString> lines = LineStringSplitter.split(ls, points, projectedPoints);
		assertEquals(3, lines.size());
		LineString ls0 = (LineString)reader.read("LINESTRING (0 0, 0 5)");
		LineString ls1 = (LineString)reader.read("LINESTRING (0 5, 0 7)");
		LineString ls2 = (LineString)reader.read("LINESTRING (0 7, 0 10)");
		assertEquals(ls0, lines.get(0));
		assertEquals(ls1, lines.get(1));
		assertEquals(ls2, lines.get(2));
		Point pp1 = (Point)reader.read("POINT (0 0)");
		Point pp2 = (Point)reader.read("POINT (0 5)");
		Point pp3 = (Point)reader.read("POINT (0 7)");
		Point pp4 = (Point)reader.read("POINT (0 10)");
		assertEquals(pp4, projectedPoints.get(0));
		assertEquals(pp1, projectedPoints.get(1));
		assertEquals(pp2, projectedPoints.get(2));
		assertEquals(pp3, projectedPoints.get(3));
		assertEquals(pp2, projectedPoints.get(4));
	}

}
