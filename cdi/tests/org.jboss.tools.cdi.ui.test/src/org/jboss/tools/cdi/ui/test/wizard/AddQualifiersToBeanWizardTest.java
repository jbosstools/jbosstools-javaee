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
package org.jboss.tools.cdi.ui.test.wizard;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatusEntry;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.internal.ui.refactoring.RefactoringWizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.internal.core.refactoring.AddQualifiersToBeanProcessor;
import org.jboss.tools.cdi.internal.core.refactoring.ValuedQualifier;
import org.jboss.tools.cdi.ui.test.testmodel.CDIBean;
import org.jboss.tools.cdi.ui.test.testmodel.CDIInjectionPoint;
import org.jboss.tools.cdi.ui.test.testmodel.CDIProject;
import org.jboss.tools.cdi.ui.test.testmodel.CDIQualifierDeclaration;
import org.jboss.tools.cdi.ui.wizard.AddQualifiersToBeanWizard;


public class AddQualifiersToBeanWizardTest extends TestCase{
	private ICDIProject project;
	private ArrayList<ValuedQualifier> availableCheck;
	private ArrayList<ValuedQualifier> deployedCheck;
	private AddQualifiersToBeanProcessor processor;
	private AddQualifiersToBeanWizard wizard;
	private Dialog dialog;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		project = new CDIProject();
		
		IClassBean bean = new CDIBean(project, "org.test.FooBean");
		
		IInjectionPoint injectionPoint = new CDIInjectionPoint(project, bean);
		
		ArrayList<IBean> beans = new ArrayList<IBean>();
		beans.add(bean);
		
		availableCheck = new ArrayList<ValuedQualifier>();
		
		availableCheck.add(new ValuedQualifier(project.getQualifier(CDIConstants.NAMED_QUALIFIER_TYPE_NAME)));
		availableCheck.add(new ValuedQualifier(project.getQualifier(CDIConstants.NEW_QUALIFIER_TYPE_NAME)));
		availableCheck.add(new ValuedQualifier(project.getQualifier(CDIProject.QUALIFIER1)));
		availableCheck.add(new ValuedQualifier(project.getQualifier(CDIProject.QUALIFIER2)));
		availableCheck.add(new ValuedQualifier(project.getQualifier(CDIProject.QUALIFIER3)));
		availableCheck.add(new ValuedQualifier(project.getQualifier(CDIProject.QUALIFIER4)));
		availableCheck.add(new ValuedQualifier(project.getQualifier(CDIProject.QUALIFIER5)));
		availableCheck.add(new ValuedQualifier(project.getQualifier(CDIProject.COMPLICATED_QUALIFIER1)));
		availableCheck.add(new ValuedQualifier(project.getQualifier(CDIProject.COMPLICATED_QUALIFIER2)));
		
		deployedCheck = new ArrayList<ValuedQualifier>();
		
		deployedCheck.add(new ValuedQualifier(project.getQualifier(CDIConstants.ANY_QUALIFIER_TYPE_NAME)));
		deployedCheck.add(new ValuedQualifier(project.getQualifier(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME)));

		processor = new AddQualifiersToBeanProcessor("", injectionPoint, beans, bean);
		ProcessorBasedRefactoring refactoring = new ProcessorBasedRefactoring(processor);
		wizard = new AddQualifiersToBeanWizard(refactoring);
		
		Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		RefactoringStatus fInitialConditions= processor.checkInitialConditions(new NullProgressMonitor());
		if (fInitialConditions.hasFatalError()) {
			for(RefactoringStatusEntry entry : fInitialConditions.getEntries()){
				if(entry.getSeverity() == RefactoringStatus.FATAL)
				fail("Fatal Error - "+entry.getMessage());
			}
		} else {
			wizard.setInitialConditionCheckingStatus(fInitialConditions);
			dialog= new RefactoringWizardDialog(parent, wizard);
			dialog.setBlockOnOpen(false);
			dialog.create();
			dialog.open();
		}
	}

	@Override
	protected void tearDown() throws Exception {
		dialog.close();
		super.tearDown();
	}
	
	private void checkQualifierLists(List<ValuedQualifier> checkQualifiers, List<IQualifier> actualQualifiers){
		for(ValuedQualifier vq : checkQualifiers){
			if(!actualQualifiers.contains(vq.getQualifier()))
				fail("Qualifier - "+vq.getQualifier().getSourceType().getFullyQualifiedName()+" not found");
		}
		for(IQualifier q : actualQualifiers){
			if(!checkQualifiers.contains(new ValuedQualifier(q)))
				fail("Wrong Qualifier - "+q.getSourceType().getFullyQualifiedName()+" found");
		}
	}

	private void checkValuedQualifierLists(List<ValuedQualifier> checkQualifiers, List<ValuedQualifier> actualQualifiers){
		for(ValuedQualifier vq : checkQualifiers){
			if(!actualQualifiers.contains(vq))
				fail("Qualifier - "+vq.getQualifier().getSourceType().getFullyQualifiedName()+" not found");
		}
		for(ValuedQualifier q : actualQualifiers){
			if(!checkQualifiers.contains(q))
				fail("Wrong Qualifier - "+q.getQualifier().getSourceType().getFullyQualifiedName()+" found");
		}
	}
	
	public void testAddQualifier(){
		List<IQualifier> available = wizard.getAvailableQualifiers();
		
		checkQualifierLists(availableCheck, available);
		
		List<ValuedQualifier> deployed = wizard.getDeployedQualifiers();

		checkValuedQualifierLists(deployedCheck, deployed);
		
		// Deploy qualifier
		ValuedQualifier qualifier = new ValuedQualifier(project.getQualifier(CDIProject.QUALIFIER1));
		ValuedQualifier defaultQualifier = new ValuedQualifier(project.getQualifier(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME));
		
		wizard.deploy(qualifier);
		
		available = wizard.getAvailableQualifiers();
		availableCheck.remove(qualifier);
		
		checkQualifierLists(availableCheck, available);
		
		deployed = wizard.getDeployedQualifiers();
		
		deployedCheck.add(qualifier);
		deployedCheck.remove(defaultQualifier);
		
		checkValuedQualifierLists(deployedCheck, deployed);
	}
	
	public void testAddAndRemoveQualifier(){
		List<IQualifier> available = wizard.getAvailableQualifiers();
		
		checkQualifierLists(availableCheck, available);
		
		List<ValuedQualifier> deployed = wizard.getDeployedQualifiers();

		checkValuedQualifierLists(deployedCheck, deployed);
		
		// Deploy qualifier
		ValuedQualifier qualifier = new ValuedQualifier(project.getQualifier(CDIProject.QUALIFIER2));
		ValuedQualifier defaultQualifier = new ValuedQualifier(project.getQualifier(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME));
		
		wizard.deploy(qualifier);
		
		available = wizard.getAvailableQualifiers();
		availableCheck.remove(qualifier);
		
		checkQualifierLists(availableCheck, available);
		
		deployed = wizard.getDeployedQualifiers();
		
		deployedCheck.add(qualifier);
		deployedCheck.remove(defaultQualifier);
		
		checkValuedQualifierLists(deployedCheck, deployed);
		
		// Remove qualifier
		wizard.remove(qualifier);
		
		available = wizard.getAvailableQualifiers();
		availableCheck.add(qualifier);
		
		checkQualifierLists(availableCheck, available);
		
		deployed = wizard.getDeployedQualifiers();
		
		deployedCheck.remove(qualifier);
		deployedCheck.add(defaultQualifier);
		
		checkValuedQualifierLists(deployedCheck, deployed);
	}
	
	public void testAddNamedQualifier(){
		List<IQualifier> available = wizard.getAvailableQualifiers();
		
		checkQualifierLists(availableCheck, available);
		
		List<ValuedQualifier> deployed = wizard.getDeployedQualifiers();

		checkValuedQualifierLists(deployedCheck, deployed);
		
		// Deploy @Named qualifier
		ValuedQualifier named = new ValuedQualifier(project.getQualifier(CDIConstants.NAMED_QUALIFIER_TYPE_NAME)); 
		
		wizard.deploy(named);
		
		available = wizard.getAvailableQualifiers();
		availableCheck.remove(named);
		
		checkQualifierLists(availableCheck, available);
		
		deployed = wizard.getDeployedQualifiers();
		
		deployedCheck.add(named);
		
		checkValuedQualifierLists(deployedCheck, deployed);
	}
	
	public void testValuedQualifier(){
		ValuedQualifier oneQualifier = new ValuedQualifier(project.getQualifier(CDIProject.COMPLICATED_QUALIFIER1));
		String value = oneQualifier.getValue();
		assertEquals("type = \"default\", name = \"User\", realChanky = false, unrealChanky = true, number = 0, size = 125, bTs = 0, bTs2 = 4, posibility = 0.0, posibility2 = 0.9999, ch = ' ', ch2 = 'T'", value);
	}
	
	public void testValuedQualifierWithDeclaration(){
		ValuedQualifier anotherQualifier = new ValuedQualifier(project.getQualifier(CDIProject.COMPLICATED_QUALIFIER2), new CDIQualifierDeclaration());
		String value = anotherQualifier.getValue();
		assertEquals("name = \"John\", size = 5, p = 0.5, ch = 'Q', b = 6", value);
	}
}
