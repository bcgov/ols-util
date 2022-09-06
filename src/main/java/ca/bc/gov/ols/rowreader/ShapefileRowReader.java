package ca.bc.gov.ols.rowreader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShapefileRowReader extends AbstractBasicRowReader {
	private static final Logger logger = LoggerFactory.getLogger(ShapefileRowReader.class.getCanonicalName());

	private String fileName;
	private DataStore dataStore; // we have to keep a reference to the dataStore or it will get finalized, which causes problems with locks
	private FeatureIterator<SimpleFeature> features;
	private SimpleFeature feature;
	private int readCount = 0;
	
	public ShapefileRowReader(String fileName) {
		try {
			logger.info("ShapefileRowReader opened for file: {}.", fileName);
			File file = new File(fileName);
			this.fileName = fileName;
	        Map<String, Object> map = new HashMap<>();
	        map.put("url", file.toURI().toURL());

	        dataStore = DataStoreFinder.getDataStore(map);
	        String typeName = dataStore.getTypeNames()[0];
	        FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);

	        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures();
	        features = collection.features();
		} catch(IOException ioe) {
			logger.error("Error opening stream for file {}.", fileName, ioe);
			throw new RuntimeException(ioe);
		}
	}


	@Override
	public boolean next() {
		if(features.hasNext()) {
            feature = features.next();
            readCount++;
            return true;
		}
		return false;
	}

	@Override
	public Object getObject(String column) {
		if(feature != null) {
			return feature.getAttribute(column);
		}
		return null;
	}
	
	@Override
	public Geometry getGeometry(String column) {
		if(feature != null) {
			return (Geometry) feature.getDefaultGeometryProperty().getValue();
		}
		return null;
	}

	@Override
	public void close() {
		if(features != null) {
			logger.info("ShapefileRowReader for file: {} closed after reading: {} records", fileName, readCount);
			features.close();
		}
	}

}
