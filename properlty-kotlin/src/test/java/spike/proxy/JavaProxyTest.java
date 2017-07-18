/*******************************************************************************
 * Copyright 2017 Francesco Cina'
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package spike.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;

import com.ufoscout.properlty.ProperltyBaseTest;

public class JavaProxyTest extends ProperltyBaseTest {

	interface ITest {
		String getString();
		int getInt();
		void doSomething();
	}

	static class TestImpl implements ITest {

		private final String string;
		private final int number;

		TestImpl(String string, int number) {
			this.string = string;
			this.number = number;
		}

		@Override
		public String getString() {return string;}

		@Override
		public int getInt() {return number;}

		@Override
		public void doSomething() {
		}

	}

    static class ProxyHandler<T> implements InvocationHandler {

        private final T original;
        public ProxyHandler(T original) {
            this.original = original;
        }

        @Override
		public Object invoke(Object proxy, Method method, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            return method.invoke(original, args);
        }

    }

    @Test
	public void testCreateProxy() {
		final ITest original = new TestImpl("stringValue", 12345);

        final ITest proxy = proxy(original, ITest.class);

        assertEquals(original.getString(), proxy.getString());
        assertEquals(original.getInt(), proxy.getInt());
        proxy.doSomething();
	}


	@Test
	public void proxyBenchmark() {
		final ITest original = new TestImpl("stringValue", 12345);
        final ITest proxy = proxy(original, ITest.class);


        // warm up
        loop(original, 1000);
        loop(proxy, 1000);

        final int loops = 1000_000;

        final long originalTime = loop(original, loops);
        final long proxyTime = loop(proxy, loops);

        System.out.println("Original time : " + originalTime + "ms");
        System.out.println("Proxy time : " + proxyTime + "ms");

	}

	private long loop(ITest obj, int loops) {
        final long start = System.currentTimeMillis();
        for (int i=0; i<loops; i++) {
        	assertNotNull(obj.getString());
        }
        final long end = System.currentTimeMillis();
        return end - start;
	}



	private <T> T proxy(T instance, Class<T> interf) {
		final ProxyHandler<T> handler = new ProxyHandler<>(instance);
        return (T) Proxy.newProxyInstance(instance.getClass().getClassLoader(), new Class[] { interf }, handler);
	}

}
