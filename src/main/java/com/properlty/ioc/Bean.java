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

import java.util.Optional;
import java.util.function.Function;

public class Bean {

	public static <T> BeanBuilder<T> name(String name) {
		return new BeanBuilder<T>().name(name);
	}

	public static <T> BeanBuilder<T> lazy(Class<T> clazz) {
		return new BeanBuilder<T>().lazy(clazz);
	}

	public static <T> BeanDescriptor<T> of(Function<PliCoContext, T> pc) {
		return new BeanBuilder<T>().of(pc);
	}

	public static class BeanBuilder<T> {
		private String name;
		private boolean lazy;
		private Class<T> clazz;


		public BeanBuilder<T> lazy(Class<T> clazz) {
			this.lazy = true;
			this.clazz = clazz;
			return this;
		}

		public BeanBuilder<T> name(String name) {
			this.name = name;
			return this;
		}

		public BeanDescriptor<T> of(Function<PliCoContext, T> pc) {
			return new BeanDescriptor<>(pc, Optional.ofNullable(clazz), Optional.ofNullable(name), lazy);
		}
	}

	public static class BeanDescriptor<T> {

		private final Function<PliCoContext, T> beanBuilder;
		private final Optional<String> beanName;
		private final boolean lazy;
		private final Optional<Class<T>> clazz;

		private BeanDescriptor(Function<PliCoContext, T> beanBuilder, Optional<Class<T>> clazz, Optional<String> beanName, boolean lazy) {
			this.clazz = clazz;
			this.beanBuilder = beanBuilder;
			this.beanName = beanName;
			this.lazy = lazy;
		}

		public Function<PliCoContext, T> getBeanBuilder() {
			return beanBuilder;
		}

		public Optional<String> getBeanName() {
			return beanName;
		}

		public boolean isLazy() {
			return lazy;
		}

		public Optional<Class<T>> getBeanClass() {
			return clazz;
		}

	}
}
