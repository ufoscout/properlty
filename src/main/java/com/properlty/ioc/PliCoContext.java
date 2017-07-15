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

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.properlty.ioc.Bean.BeanDescriptor;
import com.properlty.ioc.exception.BeanNotFoundException;
import com.properlty.ioc.exception.NotUniqueBeanException;

public class PliCoContext {

	private final List<BeanDescriptor<?>> beanDescriptors;
	private final Map<Class<?>, List<?>> beansByClass = new ConcurrentHashMap<>();
	private final Map<String, List<?>> beansByName = new ConcurrentHashMap<>();
	private boolean started = false;

	PliCoContext(List<BeanDescriptor<?>> beanDescriptors) {
		this.beanDescriptors = beanDescriptors;
	}

	public PliCoContext start() {
		if (!started) {
			synchronized (this) {
				if (!started) {
					beansByClass.clear();
					beansByName.clear();
					beanDescriptors.forEach(beanDescriptor -> {
						final Object bean = beanDescriptor.getBeanBuilder().apply(this);
						final Class<Object> clazz = beanDescriptor.getBeanClass().orElse((Class) bean.getClass());
						add(clazz, bean, beansByClass);
						beanDescriptor.getBeanName().ifPresent(name -> {
							add(name, bean, beansByName);
						});
					});
					started = true;
				}
			}
		}
		return this;
	}

	public <T> T get(Class<T> clazz) {
		final List<T> beans = (List<T>) beansByClass.computeIfAbsent(clazz, c -> resolve(c));
		if (beans.size() == 1) {
			return beans.get(0);
		}
		if (beans.isEmpty()) {
			throw new BeanNotFoundException("Expected at least one bean of type [" + clazz +  "] but found zero.");
		}
		throw new NotUniqueBeanException("Expected exactly one bean of type [" + clazz +  "] but found [" + beans.size() + "].");
	}

	private synchronized <T> List<T> resolve(final Class<T> clazz) {
		final List<T> beans = new Vector<>();
		/*
		beanDescriptors.forEach(beanDescriptor -> {
			T bean = (T) beanDescriptor.getBeanBuilder().apply(this);
			clazz = beanDescriptor.getBeanClass().orElse(bean.getClass());
			if (beanDescriptor.getBeanClass().isAssignableFrom(clazz)) {
				beans.add((T) beanDescriptor.getBeanBuilder().apply(this));
			}
		});
		*/
		return beans;
	}

	private <T> void add(T key, Object bean, Map<T, List<?>> map) {
		final int REMOVE_ME;
		System.out.println("Add bean key " + key);
		final List values = map.computeIfAbsent(key, c -> new Vector<>());
		values.add(bean);
	}

}
