/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.core.project.facet;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Converter is responsible for transformation String to 
 * Map&lt;String, SeamRuntime&gt;  and vice versa
 * 
 * @author eskimo
 */
public class SeamRuntimeListConverter {

	/*
	 * Constants definitions 
	 */
	private static final String REGEXP_ESCAPE = "\\";
	private static final String COMMA = ",";
	private static final String EMPTY_STRING = "";
	private static final String FIELD_SEPARATOR = "|";
	private static final String DEFAULT = "default";
	private static final String VERSION = "version";
	private static final String HOME_DIR = "homeDir";
	private static final String NAME = "name";

	/**
	 * Load String to SeamRuntime map from String
	 * @param input
	 * 		String representation of map
	 * @return
	 * 		Map&lt;String, SeamRuntime&gt; loaded from string
	 * TODO - switch to XML?
	 * TODO - write converter from old serialization format to XML?
	 * TODO - handle errors in string format
	 */
	public Map<String, SeamRuntime> getMap(String input) {

		Map<String, SeamRuntime> result = new HashMap<String, SeamRuntime>();
		if (input == null || EMPTY_STRING.equals(input.trim())) {
			return result;
		}
		StringTokenizer runtimes = new StringTokenizer(input, COMMA);
		while (runtimes.hasMoreTokens()) {
			String runtime = runtimes.nextToken();
			String[] map = runtime.split(REGEXP_ESCAPE + FIELD_SEPARATOR);
			SeamRuntime rt = new SeamRuntime();
			final int step = 2;
			for (int i = 0; i < map.length; i += step) {
				String name = map[i];
				String value = i + 1 < map.length ? map[i + 1] : EMPTY_STRING;
				if (NAME.equals(name)) {
					rt.setName(value);
				} else if (HOME_DIR.equals(name)) {
					rt.setHomeDir(value);
				} else if (VERSION.equals(name)) {
					rt.setVersion(SeamVersion.parseFromString(value));
				} else if (DEFAULT.equals(name)) {
					rt.setDefault(Boolean.parseBoolean(value));
				}
			}
			result.put(rt.getName(), rt);
		}

		return result;
	}

	/**
	 * Convert map String to SeamRUntime to string representation 
	 * @param runtimeMap
	 * 		Map&lt;String, SeamRuntime&gt; - map of String to Seam Runtime to convert 
	 * 		in String 
	 * @return
	 * 		String representation of String to Seam Runtime map
	 */
	public String getString(Map<String, SeamRuntime> runtimeMap) {
		StringBuffer buffer = new StringBuffer();
		SeamRuntime[] runtimes = runtimeMap.values().toArray(
				new SeamRuntime[runtimeMap.size()]);
		for (int i = 0; i < runtimes.length; i++) {
			buffer.append(NAME).append(FIELD_SEPARATOR);
			buffer.append(runtimes[i].getName());
			buffer.append(FIELD_SEPARATOR).append(VERSION).append(
					FIELD_SEPARATOR);
			buffer.append(runtimes[i].getVersion().toString());
			buffer.append(FIELD_SEPARATOR).append(HOME_DIR).append(
					FIELD_SEPARATOR);
			buffer.append(runtimes[i].getHomeDir());
			buffer.append(FIELD_SEPARATOR).append(DEFAULT).append(
					FIELD_SEPARATOR);
			buffer.append(runtimes[i].isDefault());
			if (i != runtimes.length - 1) {
				buffer.append(COMMA);
			}
		}
		return buffer.toString();
	}
}
