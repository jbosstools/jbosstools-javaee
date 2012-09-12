/*************************************************************************************
 * Copyright (c) 2011 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.runtime.seam.detector.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import junit.framework.Assert;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.jboss.ide.eclipse.as.core.util.IJBossToolingConstants;
import org.jboss.tools.runtime.as.detector.IJBossRuntimePluginConstants;
import org.jboss.tools.runtime.core.JBossRuntimeLocator;
import org.jboss.tools.runtime.core.RuntimeCoreActivator;
import org.jboss.tools.runtime.core.model.IRuntimeDetector;
import org.jboss.tools.runtime.core.model.RuntimeDefinition;
import org.jboss.tools.runtime.core.model.RuntimePath;
import org.jboss.tools.runtime.ui.RuntimeUIActivator;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author snjeza
 * @author rob stryker
 */
public class SeamRuntimeDetectionTest extends Assert implements IJBossRuntimePluginConstants {
	private final static String seamVersionAttributeName = "Seam-Version";
	private final static String SKIP_PRIVATE = "org.jboss.tools.tests.skipPrivateRequirements";
	
	@BeforeClass
	public static void create() {
		RuntimeCoreActivator.getDefault();
		RuntimeUIActivator.getDefault();
	}

	@Before
	public void setUp() {
		clearAll();
	}
	@After
	public void tearDown() {
		clearAll();
	}
	
	public void clearAll() {
		// remove all wtp servers
		IServer[] s = ServerCore.getServers();
		for( int i = 0; i < s.length; i++ ) {
			try {
				s[i].delete();
			} catch(CoreException ce ) {
				ce.printStackTrace();
				fail();
			}
		}

		// Remove all wtp runtimes
		IRuntime[] r = ServerCore.getRuntimes();
		for( int i = 0; i < r.length; i++ ) {
			try {
				r[i].delete();
			} catch(CoreException ce ) {
				ce.printStackTrace();
				fail();
			}
		}
		// Remove all seam runtimes
		SeamRuntime[] seamRuntimes = SeamRuntimeManager.getInstance().getRuntimes();
		for( int i = 0; i < seamRuntimes.length; i++ ) {
			SeamRuntimeManager.getInstance().removeRuntime(seamRuntimes[i]);
		}
		SeamRuntimeManager.getInstance().save();
	}
	
	
	@Test
	public void testRuntimeDetectors() {
		Set<IRuntimeDetector> detectors = RuntimeCoreActivator.getDefault().getRuntimeDetectors();
		assertTrue("Runtime detectors don't exist.", detectors.size() > 0);
		assertTrue(handlerExists("org.jboss.tools.runtime.handlers.SeamHandler"));
	}
	
	private boolean handlerExists(String id) {
		Set<IRuntimeDetector> detectors = RuntimeCoreActivator.getDefault().getRuntimeDetectors();
		boolean found = false;
		Iterator<IRuntimeDetector> i = detectors.iterator();
		while(i.hasNext()) {
			IRuntimeDetector next = i.next();
			String nid = next.getId();
			if( id.equals(nid))
				found = true;
		}
		return found;
	}

	@Test
	public void testJBossAs42() {
		testOneApplicationServer(IRuntimeDetectionConstants.JBOSS_42_HOME, IJBossToolingConstants.AS_42);
	}
	
	@Test
	public void testJBossAs51() {
		testOneApplicationServer(IRuntimeDetectionConstants.JBOSS_51_HOME, IJBossToolingConstants.AS_51);
	}
	
	@Test
	public void testJBossAs70() {
		testOneApplicationServer(IRuntimeDetectionConstants.JBOSS_70_HOME, IJBossToolingConstants.AS_70);
	}

	public void testOneApplicationServer(String homeDir, String typeId) {
		List<RuntimeDefinition> runtimeDefinitions = initializeOnePath(homeDir);
		RuntimeDefinition def1 = runtimeDefinitions.get(0);
		
		def1.setEnabled(false);
		initializeDefinitions(runtimeDefinitions);
		int count = countRuntimesOfType(typeId);
		assertEquals(0, count);

		def1.setEnabled(true);
		initializeDefinitions(runtimeDefinitions);
		count = countRuntimesOfType(typeId);
		assertEquals(1, count);
	}

	private List<RuntimeDefinition> initializeOnePath(String homeDir) {
		RuntimePath path = createRuntimePath(homeDir, false);
		setRuntimePaths(path, true);
		File file = new File(path.getPath());
		assertTrue("The '" + file.getAbsolutePath() + "' path isn't valid.", file.isDirectory());

		List<RuntimeDefinition> runtimeDefinitions = createDefinitionsForPath(path);
		assertEquals(1, runtimeDefinitions.size());
		RuntimeDefinition def1 = runtimeDefinitions.get(0);

		File location = def1.getLocation();
		assertTrue("The '" + location.getAbsolutePath() + "' path isn't valid.", location.isDirectory());
		return runtimeDefinitions;
	}
	
	
	// 1.2 is bundled in eap ?
	//	public void testSeam12() {
//		testOneSeamLocation(IRuntimeDetectionConstants., SeamVersion.SEAM_1_2, "1.2");
//		int count = countSeamRuntimesForVersion(SeamVersion.SEAM_1_2);
//		assertEquals(1, count);
//	}
	

	@Test
	public void testJBossEap43() {
		if( Boolean.getBoolean(SKIP_PRIVATE))
			return;
		
		List<RuntimeDefinition> runtimeDefinitions = initializeOnePath(IRuntimeDetectionConstants.EAP_43_HOME);
		RuntimeDefinition def1 = runtimeDefinitions.get(0);
		
		// Should setting this to false cascade to the nested definitions?
		//def1.setEnabled(false);
		setServerDefinitionsEnabledRecurse(def1, false);
		initializeDefinitions(runtimeDefinitions);
		int count = countRuntimesOfType(IJBossToolingConstants.EAP_43);
		assertEquals(0, count);
		count = countSeamRuntimesForVersion(SeamVersion.SEAM_1_2);
		assertEquals(0, count);
		
		setServerDefinitionsEnabledRecurse(def1, true);
		initializeDefinitions(runtimeDefinitions);
		count = countRuntimesOfType(IJBossToolingConstants.EAP_43);
		assertEquals(1, count);
		count = countSeamRuntimesForVersion(SeamVersion.SEAM_1_2);
		assertEquals(1, count);
	}
	private void setServerDefinitionsEnabledRecurse(RuntimeDefinition def, boolean enabled) {
		def.setEnabled(enabled);
		List<RuntimeDefinition> nested = def.getIncludedRuntimeDefinitions();
		Iterator<RuntimeDefinition> i = nested.iterator();
		while(i.hasNext()) {
			setServerDefinitionsEnabledRecurse(i.next(), enabled);
		}
	}
	@Test
	public void testSeam20() throws Exception {
		testOneSeamLocation(IRuntimeDetectionConstants.SEAM_20_HOME, SeamVersion.SEAM_2_0, "2.0");
	}
	
	@Test
	public void testSeam22() throws Exception  {
		testOneSeamLocation(IRuntimeDetectionConstants.SEAM_22_HOME, SeamVersion.SEAM_2_2, "2.2");
	}


	public void testOneSeamLocation(String homeDir, SeamVersion version, String versionString) throws Exception {
		testSeamHome(homeDir, versionString);

		List<RuntimeDefinition> runtimeDefinitions = initializeOnePath(homeDir);
		RuntimeDefinition def1 = runtimeDefinitions.get(0);
		
		def1.setEnabled(false);
		initializeDefinitions(runtimeDefinitions);
		int count = countSeamRuntimesForVersion(version); 
		assertEquals(0, count);

		def1.setEnabled(true);
		initializeDefinitions(runtimeDefinitions);
		count = countSeamRuntimesForVersion(version);
		assertEquals(1, count);
		
		List<RuntimeDefinition> nested = def1.getIncludedRuntimeDefinitions();
		System.out.println(nested.size());
	}
	
	
	private void testSeamHome(String seamHome, String seamVersion) throws IOException {
		File file = new File(seamHome);
		assertTrue("The '" + file.getAbsolutePath()
				+ "' path isn't valid.", file.isDirectory());
		String[] seamFiles = file.list(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				if ("seam-gen".equals(name)) {
					return true;
				}
				if ("lib".equals(name)) {
					return true;
				}
				return false;
			}
		});
		assertTrue("seamFiles : " + seamFiles, seamFiles != null
				&& seamFiles.length == 2);
		File jarFile = new File(seamHome, "lib/jboss-seam.jar");
		assertTrue("The '" + jarFile.getAbsolutePath() + "' path isn't valid.",
				jarFile.isFile());
		JarFile jar = new JarFile(jarFile);
		Attributes attributes = jar.getManifest().getMainAttributes();
		String version = attributes.getValue(seamVersionAttributeName);
		assertTrue("seamVersion: " + version, version != null && version.startsWith(seamVersion));
	}
	
	

	/* 
	 *  Utility methods for counting things 
	 */
	
	/* Pass to all handlers the list of runtime definitions and let them initialize them */
	private void initializeDefinitions(List<RuntimeDefinition> runtimeDefinitions) {
		Set<IRuntimeDetector> detectors = RuntimeCoreActivator.getDefault()
				.getRuntimeDetectors();
		for (IRuntimeDetector detector : detectors) {
			if (detector.isEnabled()) {
				detector.initializeRuntimes(runtimeDefinitions);
			}
		}
	}
	private List<RuntimeDefinition> createDefinitionsForPath(RuntimePath runtimePath) {
		JBossRuntimeLocator locator = new JBossRuntimeLocator();
		List<RuntimeDefinition> serverDefinitions = locator
				.searchForRuntimes(runtimePath.getPath(),
						new NullProgressMonitor());
//		
//		// Shouldn't this be done by the framework somehow??
//		runtimePath.getRuntimeDefinitions().clear();
//		for (RuntimeDefinition serverDefinition : serverDefinitions) {
//			serverDefinition.setRuntimePath(runtimePath);
//		}
//		runtimePath.getRuntimeDefinitions().addAll(serverDefinitions);
		return serverDefinitions;
	}
	
	private RuntimePath createRuntimePath(String path, boolean enableForStartup) {
		RuntimePath runtimePath = new RuntimePath(path);
		runtimePath.setScanOnEveryStartup(enableForStartup);
		return runtimePath;
	}
	private void setRuntimePaths(RuntimePath runtimePath, boolean save) {
		//Set<RuntimePath> runtimePaths = RuntimeUIActivator.getDefault().getRuntimePaths();
		//runtimePaths.clear();
		//runtimePaths.add(runtimePath);
		if( save )
			RuntimeUIActivator.getDefault().saveRuntimePreferences();
	}


	private int countSeamRuntimes() {
		return SeamRuntimeManager.getInstance().getRuntimes().length;
	}
	private int countSeamRuntimesForVersion(SeamVersion version) {
		SeamRuntime[] seamRuntimes = SeamRuntimeManager.getInstance().getRuntimes();
		int count = 0;
		for (SeamRuntime seamRuntime : seamRuntimes) {
			SeamVersion version2 = seamRuntime.getVersion();
			if (version.equals(version2)) {
				count++;
			}
		}
		return count;
	}


	private int countRuntimesOfType(String typeId) {
		IRuntime[] runtimes = ServerCore.getRuntimes();
		int count = 0;
		for (IRuntime runtime : runtimes) {
			IRuntimeType runtimeType = runtime.getRuntimeType();
			if (typeId.equals(runtimeType.getId())) {
				count++;
			}
		}
		return count;
	}

	public void testWtpRuntimes() {
		IRuntime[] runtimes = ServerCore.getRuntimes();
		assertTrue("runtimes.length\nExpected: 4\nWas: " + runtimes.length,
				runtimes.length == 4);
	}
	
	public void testIncludedDefinitions() {
		for (RuntimeDefinition serverDefinition:RuntimeUIActivator.getDefault().getServerDefinitions()){
			String type = serverDefinition.getType();
			if (EAP.equals(type)) {
				assertTrue("EAP has to include server definitions", serverDefinition.getIncludedRuntimeDefinitions().size() > 0);
				for(RuntimeDefinition included:serverDefinition.getIncludedRuntimeDefinitions()) {
					assertTrue("Invalid parent definition", included.getParent() == serverDefinition);
				}
			}
		}
	}
	
	
	
	// Things to test in runtime core test suite, not here
	
//	
//	@Test
//	public void testSavePreferences() throws Exception {
//		// saves preferences
//		Bundle bundle = Platform.getBundle(RuntimeUIActivator.PLUGIN_ID);
//		bundle.stop();
//		// loads preferences
//		bundle.start();
//		// calls tests again
//		testServerDefinitions();
//		testIncludedDefinitions();
//		testRuntimePaths();
//		testRuntimeDetectors();
//		testLocations();
//		testSeamRuntimes();
//		testWtpRuntimes();
//		testSeam22();
//		testSeam22Location();
//	}
//	
//	@Test
//	public void testOldWorkspace() throws Exception {
//		String runtimes = ConfigurationScope.INSTANCE.getNode(
//				RuntimeUIActivator.PLUGIN_ID).get(
//				RuntimeUIActivator.RUNTIME_PATHS, null);
//		// removes version and included definitions
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//	    Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(runtimes)));
//		Element runtimePaths = (Element) doc.getElementsByTagName(RuntimeUIActivator.RUNTIME_PATHS).item(0);
//		runtimePaths.removeAttribute(RuntimeUIActivator.PREFERENCES_VERSION);
//		removeIncluded(doc);
//		runtimes = serialize(doc);
//	    // restarts the bundle
//		Bundle bundle = Platform.getBundle(RuntimeUIActivator.PLUGIN_ID);
//		bundle.stop();
//		bundle.start();
//		// saves preferences
//		ConfigurationScope.INSTANCE.getNode(RuntimeUIActivator.PLUGIN_ID).put(
//				RuntimeUIActivator.RUNTIME_PATHS, runtimes);
//		// calls tests again 
//		testIncludedDefinitions();
//		testServerDefinitions();
//	}
//	
//	private void removeIncluded(Node node) {
//		if (node.getNodeType() == Node.ELEMENT_NODE
//				&& node.getNodeName().equals("included")) {
//			node.getParentNode().removeChild(node);
//		} else {
//			NodeList list = node.getChildNodes();
//			for (int i = 0; i < list.getLength(); i++) {
//				removeIncluded(list.item(i));
//			}
//		}
//	}
//	
//	private String serialize(Document doc) throws TransformerException {
//		StringWriter stringWriter = new StringWriter(); 
//		Transformer serializer = TransformerFactory.newInstance().newTransformer();
//        serializer.transform(new DOMSource(doc), new StreamResult(stringWriter));
//        return stringWriter.toString(); 
//	}
}
