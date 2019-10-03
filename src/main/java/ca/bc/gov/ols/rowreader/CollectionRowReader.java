package ca.bc.gov.ols.rowreader;

import java.util.Collection;
import java.util.Iterator;

import org.locationtech.jts.geom.GeometryFactory;

public class CollectionRowReader<T> extends AbstractBasicRowReader {

	private Iterator<T> itemIterator;
	
	public CollectionRowReader(Collection<T> items, GeometryFactory gf) {
		super(gf);
		itemIterator = items.iterator();
	}

	@Override
	public boolean next() {
		return itemIterator.hasNext();
	}

	@Override
	public T getObject(String column) {
		return itemIterator.next();
	}

	
	@Override
	public void close() {
		itemIterator = null;
	}

}
