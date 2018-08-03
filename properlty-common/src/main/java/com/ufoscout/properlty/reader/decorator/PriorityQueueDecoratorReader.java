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
package com.ufoscout.properlty.reader.decorator;

import com.ufoscout.properlty.reader.PropertyValue;
import com.ufoscout.properlty.reader.Reader;

import java.util.*;

/**
 * A {@link Reader} that wraps a prioritized list of other Readers.
 * Each {@link Reader} in the list has a priority that is used to resolve conflicts
 * when a key is defined more than once.
 * If two or more {@link Reader}s have the same priority, the one added by last has the highest priority
 *
 * @author Francesco Cina
 *
 */
public class PriorityQueueDecoratorReader implements Reader {

	private final Map<Integer, List<Reader>> readersMap = new TreeMap<>(Collections.reverseOrder());

	@Override
	public Map<String, PropertyValue> read() {
		final Map<String, PropertyValue> result =  new LinkedHashMap<>();
		readersMap.forEach((priority, readers) -> {
			readers.forEach(reader -> {
				final Map<String, PropertyValue> entries = reader.read();
				result.putAll(entries);
			});
		}) ;

		return result;
	}

	public void add(Reader reader, int priority) {
		final List<Reader> readers = readersMap.computeIfAbsent(priority, p -> new ArrayList<>());
		readers.add(reader);
	}

}
