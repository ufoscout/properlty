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
package com.properlty.ioc;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.junit.Test;

import com.properlty.ioc.exception.BeanNotFoundException;
import com.properlty.ioc.exception.NotUniqueBeanException;

public class PliCoTest {

	@Test
	public void shouldResolveSimpleBeans() {
		final PliCoContext plico = PliCo
				.add("MyString")
				.add(1234)
				.build();
		plico.start();
		assertEquals("MyString", plico.get(String.class));
		assertEquals(1234, plico.get(Integer.class).intValue());
	}

	@Test
	public void shouldResolveSimpleBeansFromFactories() {
		final PliCoContext plico = PliCo
				.add(new Function<PliCoContext, String>() {
					@Override
					public String apply(PliCoContext t) {
						return "MyString";
					}
				})
				.add(c -> 1234)
				.build();
		plico.start();
		assertEquals("MyString", plico.get(String.class));
		assertEquals(1234, plico.get(Integer.class).intValue());
	}

	@Test(expected=BeanNotFoundException.class)
	public void shouldFailIfBeanDoesNotExists() {
		final PliCoContext plico = PliCo
				.add("MyString")
				.build();
		plico.start();
		plico.get(Integer.class);
	}

	@Test(expected=NotUniqueBeanException.class)
	public void shouldFailIfMultipleBeansOfSameType() {
		final PliCoContext plico = PliCo
				.add("MyString")
				.add("MyOtherString")
				.build();
		plico.start();
		plico.get(String.class);
	}

	@Test
	public void shouldResolveBeansWithDependencies() {
		final PliCoContext plico = PliCo
				.add(c -> 1234)
				.add(new Function<PliCoContext, String>() {
					@Override
					public String apply(PliCoContext pc) {
						return "MyString-" + pc.get(Integer.class);
					}
				})
				.build();
		plico.start();
		assertEquals("MyString-1234", plico.get(String.class));
		assertEquals(1234, plico.get(Integer.class).intValue());
	}

	@Test
	public void shouldResolveBeansWithDependenciesDeclaredAfterwards() {
		final PliCoContext plico = PliCo
				.add(new Function<PliCoContext, String>() {
					@Override
					public String apply(PliCoContext pc) {
						return "MyString-" + pc.get(Integer.class);
					}
				})
				.add(c -> 1234)
				.build();
		plico.start();
		assertEquals("MyString-1234", plico.get(String.class));
		assertEquals(1234, plico.get(Integer.class).intValue());
	}

	@Test
	public void shouldCallFactoryCreationAtMostOnce() {
		final AtomicInteger count = new AtomicInteger(0);
		final PliCoContext plico = PliCo
				.add(Bean.lazy(String.class).of(pc -> {
					try {
						Thread.sleep(200);
					} catch (final InterruptedException e) {
					}
					return "MyString-" + count.getAndIncrement();
					}))
				.build();
		plico.start();

		final AtomicInteger calls = new AtomicInteger(0);
		final List<Thread> threads = new ArrayList<>();
		final int attempts = 2500;

		for (int i=0; i<attempts; i++) {
			final Thread thread = new Thread(() -> {
				assertEquals("MyString-0", plico.get(String.class));
				calls.incrementAndGet();
			});
			thread.start();
			threads.add(thread);
		}

		threads.forEach(t -> {
			try {
				t.join();
			} catch (final InterruptedException e) {
			}
		});

		assertEquals(1, count.get());
		assertEquals(attempts, calls.get());
		assertEquals("MyString-0", plico.get(String.class));

	}
}
