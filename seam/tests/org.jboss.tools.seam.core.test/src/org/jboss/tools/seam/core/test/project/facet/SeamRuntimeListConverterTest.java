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
package org.jboss.tools.seam.core.test.project.facet;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeListConverter;
import org.jboss.tools.seam.core.project.facet.SeamVersion;

/**
 * @author eskimo
 *
 */
public class SeamRuntimeListConverterTest extends TestCase {

	/**
	 * Test method for {@link org.jboss.tools.seam.core.project.facet.SeamRuntimeListConverter#getMap(java.lang.String)}.
	 */
	public void testSerializeSeamRuntimeListToString() {
		Map<String,SeamRuntime> runtimes = new HashMap<String,SeamRuntime>();
		SeamRuntime rt1 = new SeamRuntime();
		rt1.setName("rt1");
		rt1.setHomeDir("homeDir");
		rt1.setVersion(SeamVersion.parseFromString("1.2"));
		runtimes.put(rt1.getName(),rt1);
		SeamRuntime rt2 = new SeamRuntime();
		rt2.setName("rt2");
		rt2.setHomeDir("homeDir");
		rt2.setVersion(SeamVersion.parseFromString("1.2"));
		runtimes.put(rt2.getName(),rt2);
		SeamRuntimeListConverter converter = new SeamRuntimeListConverter();
		String config = converter.getString(runtimes);
		assertNotNull("Saved runtime list cannot be null", config);
		assertTrue("Saved list of runtimes cannot be empty.", !"".equals(config.trim()));
		Map<String,SeamRuntime> result = converter.getMap(config);

		Comparator comparator = new SeamRuntimeComparator();
		for (SeamRuntime runtime : runtimes.values()) {
			SeamRuntime curr = result.get(runtime.getName());
			assertEquals("Saved runtime was not restored correctly", 0, comparator.compare(runtime, curr));
		}
	}

	/**
	 * Test method for {@link org.jboss.tools.seam.core.project.facet.SeamRuntimeListConverter#getString(java.util.List)}.
	 */
	public void testLoadingSeamRuntimeListFromString() {
		SeamRuntimeListConverter converter = new SeamRuntimeListConverter();
		Map<String,SeamRuntime> runtimes = 
			converter.getMap("name|rt1|homeDir|homeDirPath1|version|1.2|default|true," +
					"name|rt2|homeDir|homeDirPath2|version|1.2|default|false");
		assertNotNull("Loading runtime from string is failed",runtimes.get("rt1"));
		assertNotNull("Loading runtime from string is failed",runtimes.get("rt2"));
		SeamRuntime rt = runtimes.get("rt1");
		assertTrue("Seam Runtime loaded incorrect", 
				"rt1".equals(rt.getName())
				&& "homeDirPath1".equals(rt.getHomeDir())
				&& SeamVersion.SEAM_1_2 ==rt.getVersion()
				&& rt.isDefault());
		rt = runtimes.get("rt2");
		assertTrue("Seam Runtime loaded incorrect", 
				"rt2".equals(rt.getName())
				&& "homeDirPath2".equals(rt.getHomeDir())
				&& SeamVersion.SEAM_1_2 ==rt.getVersion()
				&& !rt.isDefault());
		runtimes = converter.getMap("");
		assertTrue("Loading runtimes from empty string should produce empty map",runtimes.size()==0);
		runtimes = converter.getMap(null);
		assertTrue("Loading runtimes from null string should produce empty map", runtimes.size()==0);
	}

	public static class SeamRuntimeComparator implements Comparator<SeamRuntime> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(SeamRuntime o1, SeamRuntime o2) {
			if(o1.getName().equals(o2.getName()) 
					&& o1.getVersion()==o2.getVersion() 
					&& o1.getHomeDir().equals(o2.getHomeDir())) {
				return 0;
			} 
			return 1;
		}
	}
}