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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;

import com.properlty.ioc.Bean.BeanDescriptor;

public final class PliCo {

	public static <T> PliCoBuilder add(BeanDescriptor<T> beanDescriptor) {
		return new PliCoBuilder().add(beanDescriptor);
	}

	public static <T> PliCoBuilder add(Function<PliCoContext, T> factory) {
		return new PliCoBuilder().add(factory);
	}

	public static <T> PliCoBuilder add(T bean) {
		return new PliCoBuilder().add(bean);
	}

	public static final class PliCoBuilder {

		private final List<BeanDescriptor<?>> beanDescriptors = new Vector<>();

		public <T> PliCoBuilder add(BeanDescriptor<T> beanDescriptor) {
			beanDescriptors.add(beanDescriptor);
			return this;
		}

		public <T> PliCoBuilder add(Function<PliCoContext, T> factory) {
			beanDescriptors.add(Bean.of(factory));
			return this;
		}

		public <T> PliCoBuilder add(T bean) {
			beanDescriptors.add(Bean.of(p -> bean));
			return this;
		}

		public PliCoContext build() {
			return new PliCoContext(new ArrayList<>(beanDescriptors));
		}
	}
}
