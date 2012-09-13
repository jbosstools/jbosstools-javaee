/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.text.ext.test;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.text.ext.CDIExtensionsMessages;
import org.jboss.tools.cdi.text.ext.hyperlink.AssignableBeansHyperlink;
import org.jboss.tools.cdi.text.ext.hyperlink.InformationControlManager;
import org.jboss.tools.cdi.text.ext.hyperlink.InjectedPointHyperlink;
import org.jboss.tools.cdi.text.ext.hyperlink.InjectedPointHyperlinkDetector;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil.TestHyperlink;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil.TestRegion;
import org.jboss.tools.common.util.FileUtil;

public class InjectedPointHyperlinkDetectorTest extends TCKTest {
	
	public void testShowHyperlinksDialog() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/injectionpoint/BasicLogger.java");
		
		assertTrue("File must be exist", file.exists());
		
		IEditorPart part = CDIHyperlinkTestUtil.openFileInEditor(file);
		if(part instanceof JavaEditor){
			IDocument document = ((JavaEditor)part).getViewer().getDocument();
			
			assertNotNull("Document not found", document);
			
			IBean[] beans = cdiProject.getBeans();
			IHyperlink[] hyperlinks = new IHyperlink[beans.length];
			int index = 0;
			for(IBean b : beans){
				hyperlinks[index++] = new InjectedPointHyperlink(new Region(1,1), b, document);
			}
			
			IInformationControl informationControl = InformationControlManager.instance.showHyperlinks("Title", ((JavaEditor)part).getViewer(), hyperlinks, true);
			
			assertNotNull("InformationControl not found", informationControl);
			informationControl.setVisible(false);
		}else{
			fail("Editor part must be instance of JavaEditor, was - "+part.getClass());
		}
	}

	public void testInjectedPointHyperlinkDetector() throws Exception {
		String[] paths = new String[]{
			"/tck/JavaSource/org/jboss/jsr299/tck/tests/lookup/injectionpoint/BasicLogger.java",
			"/tck/JavaSource/org/jboss/jsr299/tck/tests/lookup/injectionpoint/TimestampLogger.java"
		};
		Collection<IBean> beans = cdiProject.getBeans("/tck/JavaSource/org/jboss/jsr299/tck/tests/lookup/injectionpoint/BasicLogger.java",	true);
		IBean bean=null;
		for(IBean b : beans){
			bean = b;
		}
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(/*115, 6*/"Inject",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE, paths)
		})); // Inject
		regionList.add(new TestRegion(/*133, 6*/"Logger",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE, paths)
		})); // Logger
		regionList.add(new TestRegion(/*140, 6*/"logger",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE, paths)
		})); // logger
		regionList.add(new TestRegion(/*196, 6*/"logger",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE, paths)
		})); // logger
		regionList.add(new TestRegion(/*250, 6*/"logger",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE, paths)
		})); // logger
		
		CDIHyperlinkTestUtil.checkRegions(tckProject, "JavaSource/org/jboss/jsr299/tck/tests/lookup/injectionpoint/LoggerConsumer.java", regionList, new InjectedPointHyperlinkDetector());
	}
	
	public void testInjectedPointHyperlinkDetectorForAsYouType() throws Exception {
		String[] paths = new String[]{
			"/tck/JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/BasicLogger.java",
			"/tck/JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/TestInjections.java"
		};
		Collection<IBean> beans = cdiProject.getBeans("/tck/JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/BasicLogger.java",	true);
		IBean bean=null;
		for(IBean b : beans){
			bean = b;
		}
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(/*115, 6*/"Inject",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE, paths)
		})); // Inject
		regionList.add(new TestRegion(/*133, 6*/"Logger",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE, paths)
		})); // Logger
		regionList.add(new TestRegion(/*140, 6*/"logger",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE, paths)
		})); // logger
		regionList.add(new TestRegion(/*196, 6*/"logger",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE, paths)
		})); // logger
		regionList.add(new TestRegion(/*250, 6*/"logger",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE, paths)
		})); // logger
		
		CDIHyperlinkTestUtil.checkRegionsForAsYouType(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/TestInjections.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/TestInjections.qfxresult",
				regionList,
				new InjectedPointHyperlinkDetector());
	}

	public void testInjectedPointHyperlinkDetectorWithComment() throws Exception {
		String[] paths = new String[]{
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/BasicLogger.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/TestInjections.java"
			};
		Collection<IBean> beans = cdiProject.getBeans("/tck/JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/BasicLogger.java",	true);
		IBean bean=null;
		for(IBean b : beans){
			bean = b;
		}
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion("Inject",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE, paths)
		})); // Inject
		regionList.add(new TestRegion("Logger",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE, paths)
		})); // Logger
		regionList.add(new TestRegion("logger",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE, paths)
		})); // logger
		regionList.add(new TestRegion("logger",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE, paths)
		})); // logger
		regionList.add(new TestRegion("logger",   new TestHyperlink[]{
				new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
				new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE, paths)
			})); // logger
		
		CDIHyperlinkTestUtil.checkRegions(tckProject, "JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/LoggerConsumer.java", regionList, new InjectedPointHyperlinkDetector());
	}

	public void testInjectedProducerMethodParametersHyperlinkDetector() throws Exception {
		Collection<IBean> beans = cdiProject.getBeans("/tck/JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/CustomProducerImpl.java",	true);
		IBean bean=null;
		for(IBean b : beans){
			bean = b;
		}
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/CustomProducerImpl.java");
		String text = FileUtil.readStream(file);
		
		int orderPosition = text.indexOf("Something order");
		
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(orderPosition, 15,   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " CustomProducerImpl.produce()", bean)
		})); // order

		//It may seem strange that we cannot start with "@Disposes"
		String disposesParam = "Disposes Something toDispose";
		int disposePosition = text.indexOf(disposesParam);
		regionList.add(new TestRegion(disposePosition, disposesParam.length(),   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " CustomProducerImpl.produce()", bean)
		})); // toDispose
		
		
		CDIHyperlinkTestUtil.checkRegions(tckProject, "JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/CustomProducerImpl.java", regionList, new InjectedPointHyperlinkDetector());
	}
	
	public void testInjectedConstructorParametersHyperlinkDetector() throws Exception {
		Collection<IBean> beans = cdiProject.getBeans("/tck/JavaSource/org/jboss/jsr299/tck/tests/context/dependent/Fox.java",	true);
		IBean bean=null;
		for(IBean b : beans){
			bean = b;
		}
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(/*880, 6*/"Inject",  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		regionList.add(new TestRegion(/*894, 3*/"Fox",  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)})); // Fox
		regionList.add(new TestRegion(/*898, 3*/"fox",  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		// Fake TestRegion for search purpose
		regionList.add(new TestRegion("FoxFarm",  new TestHyperlink[]{}));
		regionList.add(new TestRegion(/*975, 3*/"Fox",  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		regionList.add(new TestRegion(/*979, 3*/"fox",  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		regionList.add(new TestRegion(/*1017, 3*/"fox", new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));

		CDIHyperlinkTestUtil.checkRegions(tckProject, "JavaSource/org/jboss/jsr299/tck/tests/context/dependent/FoxFarm.java", regionList, new InjectedPointHyperlinkDetector());
	}
	
	public void testInjectedInitializerParametersHyperlinkDetector() throws Exception {
		Collection<IBean> beans = cdiProject.getBeans("/tck/JavaSource/org/jboss/jsr299/tck/tests/context/dependent/Fox.java",	true);
		IBean bean=null;
		for(IBean b : beans){
			bean = b;
		}
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(/*880, 6*/"Inject",  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		regionList.add(new TestRegion(/*894, 3*/"Fox",  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)})); // Fox
		regionList.add(new TestRegion(/*898, 3*/"fox",  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		regionList.add(new TestRegion("init(",  new TestHyperlink[]{}));
		regionList.add(new TestRegion(/*972, 3*/"Fox",  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		regionList.add(new TestRegion(/*976, 3*/"fox",  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		regionList.add(new TestRegion(/*1014, 3*/"fox", new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));

		CDIHyperlinkTestUtil.checkRegions(tckProject, "JavaSource/org/jboss/jsr299/tck/tests/context/dependent/FoxHole.java", regionList, new InjectedPointHyperlinkDetector());
	}
	
	public void testInjectedPointHyperlinkDetectorForJAR() throws Exception {
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(/*115, 6*/"Inject",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " JarBasicLogger"/*, bean*/),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE)
		})); // Inject
		regionList.add(new TestRegion(/*133, 6*/"JarLogger",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " JarBasicLogger"/*, bean*/),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE)
		})); // Logger
		regionList.add(new TestRegion(/*140, 6*/"logger",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " JarBasicLogger"/*, bean*/),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE)
		})); // logger
		regionList.add(new TestRegion(/*196, 6*/"logger",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " JarBasicLogger"/*, bean*/),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE)
		})); // logger
		regionList.add(new TestRegion(/*250, 6*/"logger",   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " JarBasicLogger"/*, bean*/),
			new TestHyperlink(AssignableBeansHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE)
		})); // logger
		
		CDIHyperlinkTestUtil.checkRegionsInJar(tckProject, "org.jar.test.openon.JarLoggerConsumer", regionList, new InjectedPointHyperlinkDetector());
	}

}