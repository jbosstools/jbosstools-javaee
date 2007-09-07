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
 * @author eskimo
 *
 */
public class SeamRuntimeListConverter1 {
	
	public Map<String,SeamRuntime> getMap(String input) {
		
		Map<String,SeamRuntime> result = new HashMap<String,SeamRuntime>();
		if(input==null || "".equals(input.trim())) {
			return result;
		}
		
		StringTokenizer runtimes = new StringTokenizer(input,",");
		while (runtimes.hasMoreTokens()) {
			String runtime = runtimes.nextToken();
			String[] map = runtime.split("\\|");
			SeamRuntime rt = new SeamRuntime();
			for (int i=0;i<map.length;i+=2) {
				String name = map[i];
				String value = i+1<map.length?map[i+1]:"";
				if("name".equals(name)) {
					rt.setName(value);
				} else if("homeDir".equals(name)) {
					rt.setHomeDir(value);
				} else if ("version".equals(name)) {
					rt.setVersion(SeamVersion.parseFromString(value));
				} else if("default".equals(name)) {
					rt.setDefault(Boolean.parseBoolean(value));
				}
			}
			result.put(rt.getName(),rt);
		}
		
		return result;
	}

	public String getString(Map<String,SeamRuntime> runtimeMap) {
		StringBuffer buffer = new StringBuffer();
		SeamRuntime[] runtimes = runtimeMap.values().toArray(new SeamRuntime[runtimeMap.size()]);
		for (int i=0;i<runtimes.length;i++) {
			buffer.append("name|");
			buffer.append(runtimes[i].getName());
			buffer.append("|version|");
			buffer.append(runtimes[i].getVersion().toString());
			buffer.append("|homeDir|");
			buffer.append(runtimes[i].getHomeDir());
			buffer.append("|default|");
			buffer.append(runtimes[i].isDefault());
			if(i!=runtimes.length-1) buffer.append(",");
		}		
		return buffer.toString();
	}
}
