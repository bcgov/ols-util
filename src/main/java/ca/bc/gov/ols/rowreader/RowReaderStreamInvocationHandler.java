package ca.bc.gov.ols.rowreader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RowReaderStreamInvocationHandler<T> implements InvocationHandler {

	private Stream<T> stream; // proxy will intercept method calls to such stream
	private RowReader rr;

	public RowReaderStreamInvocationHandler(RowReader rr, Function<RowReader, T> mappingFunction) {
		stream = Stream.generate(new RowReaderSupplier(rr, mappingFunction)).takeWhile(item -> item != null);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		if (method == null)
			throw new RuntimeException("null method null");

		if (method.getName().equals("close") && args == null) {
			// invoked close(), no arguments
			if (rr != null) {
				rr.close(); // closes RowReader too
			}
		}

		return method.invoke(stream, args);
	}

	private class RowReaderSupplier implements Supplier<T> {
		private final RowReader rr;
		private final Function<RowReader, T> mappingFunction;
	
		private RowReaderSupplier(RowReader rr, Function<RowReader, T> mappingFunction) {
		    this.rr = rr;
		    this.mappingFunction = mappingFunction;
		  }
	
		@Override
		public T get() {
			if(rr.next()) {
				return mappingFunction.apply(rr);
			}
			return null;
		}
	}
}