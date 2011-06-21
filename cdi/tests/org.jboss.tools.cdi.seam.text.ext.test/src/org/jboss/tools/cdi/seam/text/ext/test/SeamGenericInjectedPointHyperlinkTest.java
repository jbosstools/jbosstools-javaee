/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.text.ext.test;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.tools.cdi.seam.config.core.test.SeamConfigTest;
import org.jboss.tools.cdi.seam.solder.core.test.SeamSolderTest;
import org.jboss.tools.cdi.seam.text.ext.CDISeamExtPlugin;
import org.jboss.tools.cdi.seam.text.ext.hyperlink.GenericInjectedPointHyperlink;
import org.jboss.tools.cdi.seam.text.ext.hyperlink.GenericInjectedPointHyperlinkDetector;
import org.jboss.tools.cdi.seam.text.ext.hyperlink.GenericInjectedPointListHyperlink;
import org.jboss.tools.cdi.seam.text.ext.hyperlink.SeamConfigInjectedPointHyperlink;
import org.jboss.tools.cdi.seam.text.ext.hyperlink.SeamConfigInjectedPointHyperlinkDetector;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil;
import org.jboss.tools.common.util.FileUtil;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class SeamGenericInjectedPointHyperlinkTest extends SeamSolderTest {
	static final String HYPERLINK_NAME = GenericInjectedPointHyperlink.class.getName();
	static final String LIST_HYPERLYNK_NAME = GenericInjectedPointListHyperlink.class.getName();
	public SeamGenericInjectedPointHyperlinkTest() {}

	public void testFieldInjection() throws Exception {
		IHyperlink hyperlink = SeamConfigInjectedPointHyperlinkTest.checkHyperLinkInJava(
				"src/org/jboss/generic/MyBeanInjections.java", 
				project, 
				"first1", 1, 
				new GenericInjectedPointHyperlinkDetector(), 
				HYPERLINK_NAME);
		hyperlink.open();
		
		SeamConfigInjectedPointHyperlinkTest.checkResult("MyConfigurationProducer.java", "getOneConfig");
	}

	public void testGenericInjection() throws Exception {
		IHyperlink hyperlink = SeamConfigInjectedPointHyperlinkTest.checkHyperLinkInJava(
				"src/org/jboss/generic/MyGenericBean.java", 
				project, 
				"config", 1, 
				new GenericInjectedPointHyperlinkDetector(), 
				LIST_HYPERLYNK_NAME);
//		hyperlink.open();
//		
//		SeamConfigInjectedPointHyperlinkTest.checkResult("MyConfigurationProducer.java", "getOneConfig");
	}

}
