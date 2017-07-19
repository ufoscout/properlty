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
package com.ufoscout.properlty.reader;

import com.ufoscout.properlty.exception.ResourceNotFoundException;
import com.ufoscout.properlty.util.FileUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Return a {@link Map} with all values from a properties file.
 *
 * @author Francesco Cina
 *
 */
public class PropertiesResourceReader implements Reader {

	private final String resourcePath;
	private boolean ignoreNotFound = false;
	private Charset charset = StandardCharsets.UTF_8;

	/**
	 *
	 *	Build a properties reader for the resource of a specified path.
	 *  The path can be in the form:
	 *
	 * - ./path/file : path of a file in the filesystem
	 * - file:./path/file : same as previous case, a path of a file in the filesystem
	 * - classpath:/path/file : path of a resource in the classpath
	 *
	 * @param resourcePath the path on the filesystem of the properties file
	 */
	public static PropertiesResourceReader build(String resourcePath) {
		return new PropertiesResourceReader(resourcePath);
	}

	private PropertiesResourceReader(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	@Override
	public Map<String, PropertyValue> read() {
        try(InputStream inputStream = FileUtils.getStream(resourcePath))
        {
        	final Properties prop = new Properties();
        	final Map<String, PropertyValue> map = new HashMap<>();
            prop.load(new InputStreamReader(inputStream, charset));
            for (final Entry<Object, Object> entry : prop.entrySet()) {
            	map.put((String) entry.getKey(), PropertyValue.of((String) entry.getValue()));
            }
            return map;
        }
        catch (final FileNotFoundException e) {
        	if (ignoreNotFound) {
        		return new HashMap<>();
        	} else {
        		throw new ResourceNotFoundException(e);
        	}
        }
        catch (final Exception e) {
       		throw new RuntimeException(e);
        }

	}

	/**
	 * @return the ignoreNotFound
	 */
	public boolean isIgnoreNotFound() {
		return ignoreNotFound;
	}

	/**
	 * Whether to ignore if the resource is not found.
	 * Default is false.
	 *
	 * @param ignoreNotFound
	 */
	public PropertiesResourceReader ignoreNotFound(boolean ignoreNotFound) {
		this.ignoreNotFound = ignoreNotFound;
		return this;
	}

	/**
	 * @return the {@link Charset}
	 */
	public Charset getCharset() {
		return charset;
	}

	/**
	 * The {@link Charset} of the resource.
	 * Default is UTF8.
	 *
	 * @param charset
	 */
	public PropertiesResourceReader charset(Charset charset) {
		this.charset = charset;
		return this;
	}

}
