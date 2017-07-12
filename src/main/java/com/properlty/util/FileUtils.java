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
package com.properlty.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public final class FileUtils {

	public static final String FILE_PATH_PREFIX = "file:";
	public static final String CLASSPATH_PREFIX = "classpath:";
	private FileUtils() {}

	/**
	 * It returns an {@link InputStream} on the resource.
	 * The resourcePath can be:
	 * - ./path/file : path of a file in the filesystem
	 * - file:./path/file : same as previous case, a path of a file in the filesystem
	 * - classpath:/path/file : path of a resource in the classpath
	 *
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	public static InputStream getStream(String resourcePath) throws FileNotFoundException {
		if (resourcePath.startsWith(CLASSPATH_PREFIX)) {
			final String resourceName = resourcePath.substring(CLASSPATH_PREFIX.length());
			final InputStream is = FileUtils.class.getClassLoader().getResourceAsStream(resourceName);
			if (is == null) {
				throw new FileNotFoundException("Cannot retrieve classpath resource [" + resourceName + "]");
			}
			return is;
		}
		if (resourcePath.startsWith(FILE_PATH_PREFIX)) {
			return new FileInputStream(resourcePath.substring(FILE_PATH_PREFIX.length()));
		}
		return new FileInputStream(resourcePath);
	}

}
