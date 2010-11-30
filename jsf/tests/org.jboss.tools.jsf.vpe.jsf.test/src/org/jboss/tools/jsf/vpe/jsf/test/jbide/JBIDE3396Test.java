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
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.common.el.core.ELReferenceList;
import org.jboss.tools.common.resref.core.ResourceReference;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeController;

/**
 * @author Max Areshkau
 *
 */
public class JBIDE3396Test extends VpeTest{

	private IFile firstPage;
	private IFile secondPage;
	private ResourceReference[] firstElValues;
	private ResourceReference[] secondElValues;
	
	public JBIDE3396Test(final String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setException(null);
		this.firstPage = (IFile) TestUtil.getComponentPath("JBIDE/3396/first.jsp", //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);
		this.secondPage = (IFile) TestUtil.getComponentPath("JBIDE/3396/second.jsp",  //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);

		this.firstElValues = new ResourceReference[1];
		this.firstElValues[0] =  new ResourceReference("table.style",ResourceReference.PROJECT_SCOPE); //$NON-NLS-1$
		this.firstElValues[0].setProperties("color:red;"); //$NON-NLS-1$
		
		this.secondElValues =  new ResourceReference[1];
		this.secondElValues[0] = new ResourceReference("book.style", ResourceReference.PROJECT_SCOPE); //$NON-NLS-1$
		this.secondElValues[0].setProperties("color:green;"); //$NON-NLS-1$
		

//        ELReferenceList.getInstance().setAllResources(this.file,entries);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.ui.test.VpeTest#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		ELReferenceList.getInstance().setAllResources(this.firstPage, new ResourceReference[0]);
		ELReferenceList.getInstance().setAllResources(this.secondPage, new ResourceReference[0]);
		if(getException()!=null) {
			throw new Exception(getException());
		}
		super.tearDown();
	}
	
	public void testJBIDE3396() throws Exception{
		//open first page and set resources
		final IEditorInput firstInput = new FileEditorInput(this.firstPage);
		final JSPMultiPageEditor firstEditorPart = openEditor(firstInput);
		//wait while editor will be initialized
		TestUtil.getVpeController(firstEditorPart);
		ELReferenceList.getInstance().setAllResources(this.firstPage, this.firstElValues);
		//wait for visual refresh 
		TestUtil.delay(1000);
		TestUtil.waitForJobs();
		//open second page and 
		final IEditorInput secondInput = new FileEditorInput(this.secondPage);
		final JSPMultiPageEditor secondEditorPart =  openEditor(secondInput);
		final VpeController secondPageVpeController = TestUtil.getVpeController(secondEditorPart);
		final int size = secondPageVpeController.getDomMapping().getVisualMap().size();
		TestUtil.delay(5000);
		ELReferenceList.getInstance().setAllResources(this.secondPage, this.secondElValues);
		//wait for visual refresh
		TestUtil.delay(5000);
		final ResourceReference[] elResoReferences = ELReferenceList.getInstance().getAllResources(this.secondPage);
		for (final ResourceReference resourceReference : elResoReferences) {
			resourceReference.setProperties(resourceReference.getProperties()+'T');
		}
		ELReferenceList.getInstance().setAllResources(this.secondPage, elResoReferences);
		TestUtil.waitForJobs();
		assertEquals("Size of map before and after appling should be equal",size, secondPageVpeController.getDomMapping().getVisualMap().size()); //$NON-NLS-1$
	}
	
}
