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