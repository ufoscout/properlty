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
package com.properlty.reader.decorator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.properlty.ProperltyBaseTest;
import com.properlty.reader.Properties;
import com.properlty.reader.PropertyValue;

public class PriorityQueueDecoratorReaderTest extends ProperltyBaseTest {

	@Test
	public void shouldReturnEmptyMapIfEmpty() {
		final PriorityQueueDecoratorReader queue = new PriorityQueueDecoratorReader();
		final Map<String, PropertyValue> prop = queue.read();
		assertNotNull(prop);
		assertTrue(prop.isEmpty());
	}

	@Test
	public void shouldMergeEntriesFromMapWithDifferentPriority() {
		final PriorityQueueDecoratorReader queue = new PriorityQueueDecoratorReader();

		queue.add(Properties.add("k1", "v1").add("k2", "v2"), 11);
		queue.add(Properties.add("k3", "v3").add("k4", "v4"), 1);

		final Map<String, PropertyValue> prop = queue.read();
		assertNotNull(prop);
		assertEquals(4, prop.size());

		assertEquals("v1", prop.get("k1").getValue());
		assertEquals("v2", prop.get("k2").getValue());
		assertEquals("v3", prop.get("k3").getValue());
		assertEquals("v4", prop.get("k4").getValue());
	}

	@Test
	public void shouldMergeEntriesFromMapWithSamePriority() {
		final PriorityQueueDecoratorReader queue = new PriorityQueueDecoratorReader();

		queue.add(Properties.add("k1", "v1").add("k2", "v2"), 11);
		queue.add(Properties.add("k3", "v3").add("k4", "v4"), 11);

		final Map<String, PropertyValue> prop = queue.read();
		assertNotNull(prop);
		assertEquals(4, prop.size());

		assertEquals("v1", prop.get("k1").getValue());
		assertEquals("v2", prop.get("k2").getValue());
		assertEquals("v3", prop.get("k3").getValue());
		assertEquals("v4", prop.get("k4").getValue());
	}

	@Test
	public void shouldTakeIntoAccountPriorityInCaseOfCollisions() {
		final PriorityQueueDecoratorReader queue = new PriorityQueueDecoratorReader();

		queue.add(Properties.add("k1", "v1").add("k2", "v2-first"), 2);
		queue.add(Properties.add("k3", "v3").add("k2", "v2-second"), 1);

		final Map<String, PropertyValue> prop = queue.read();
		assertNotNull(prop);
		assertEquals(3, prop.size());

		assertEquals("v1", prop.get("k1").getValue());
		assertEquals("v2-second", prop.get("k2").getValue());
		assertEquals("v3", prop.get("k3").getValue());

	}

	@Test
	public void shouldTakeIntoAccountInsertionOrderForSamePriorityInCaseOfCollisions() {
		final PriorityQueueDecoratorReader queue = new PriorityQueueDecoratorReader();

		queue.add(Properties.add("k3", "v3").add("k2", "v2-second"), 1);
		queue.add(Properties.add("k1", "v1").add("k2", "v2-first"), 1);

		final Map<String, PropertyValue> prop = queue.read();
		assertNotNull(prop);
		assertEquals(3, prop.size());

		assertEquals("v1", prop.get("k1").getValue());
		assertEquals("v2-first", prop.get("k2").getValue());
		assertEquals("v3", prop.get("k3").getValue());

	}

	@Test
	public void shouldTakeIntoAccountInsertionOrderAndPriority() {
		final PriorityQueueDecoratorReader queue = new PriorityQueueDecoratorReader();

		queue.add(Properties.add("k1", "v1-first").add("k2", "v2-first").add("k4", "v4-first"), 10);
		queue.add(Properties.add("k1", "v1-second").add("k2", "v2-second").add("k3", "v3-second"), 10);
		queue.add(Properties.add("k2", "v2-third").add("k3", "v3-third").add("k5", "v5-third"), 5);

		final Map<String, PropertyValue> prop = queue.read();
		assertNotNull(prop);
		assertEquals(5, prop.size());

		assertEquals("v1-second", prop.get("k1").getValue());
		assertEquals("v2-third",  prop.get("k2").getValue());
		assertEquals("v3-third",  prop.get("k3").getValue());
		assertEquals("v4-first",  prop.get("k4").getValue());
		assertEquals("v5-third",  prop.get("k5").getValue());

	}

}
