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
		if(input==null || "".equals(input.trim())) { //$NON-NLS-1$
			return result;
		}
		
		StringTokenizer runtimes = new StringTokenizer(input,","); //$NON-NLS-1$
		while (runtimes.hasMoreTokens()) {
			String runtime = runtimes.nextToken();
			String[] map = runtime.split("\\|"); //$NON-NLS-1$
			SeamRuntime rt = new SeamRuntime();
			for (int i=0;i<map.length;i+=2) {
				String name = map[i];
				String value = i+1<map.length?map[i+1]:""; //$NON-NLS-1$
				if("name".equals(name)) { //$NON-NLS-1$
					rt.setName(value);
				} else if("homeDir".equals(name)) { //$NON-NLS-1$
					rt.setHomeDir(value);
				} else if ("version".equals(name)) { //$NON-NLS-1$
					rt.setVersion(SeamVersion.parseFromString(value));
				} else if("default".equals(name)) { //$NON-NLS-1$
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
			buffer.append("name|"); //$NON-NLS-1$
			buffer.append(runtimes[i].getName());
			buffer.append("|version|"); //$NON-NLS-1$
			buffer.append(runtimes[i].getVersion().toString());
			buffer.append("|homeDir|"); //$NON-NLS-1$
			buffer.append(runtimes[i].getHomeDir());
			buffer.append("|default|"); //$NON-NLS-1$
			buffer.append(runtimes[i].isDefault());
			if(i!=runtimes.length-1) buffer.append(","); //$NON-NLS-1$
		}		
		return buffer.toString();
	}
}
