package org.jboss.tools.cdi.ui.test.wizard;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.ui.test.testmodel.CDIBean;
import org.jboss.tools.cdi.ui.test.testmodel.CDIInjectionPoint;
import org.jboss.tools.cdi.ui.test.testmodel.CDIProject;
import org.jboss.tools.cdi.ui.wizard.AddQualifiersToBeanWizard;


public class AddQualifiersToBeanWizardTest extends TestCase{
	private AddQualifiersToBeanWizard wizard;
	private WizardDialog dialog;
	private ICDIProject project;
	private ArrayList<IQualifier> availableCheck, deployedCheck;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		project = new CDIProject();
		
		IClassBean bean = new CDIBean(project, "org.test.FooBean");
		
		IInjectionPoint injectionPoint = new CDIInjectionPoint(project, bean);
		
		ArrayList<IBean> beans = new ArrayList<IBean>();
		beans.add(bean);
		
		availableCheck = new ArrayList<IQualifier>();
		
		availableCheck.add(project.getQualifier(CDIConstants.NAMED_QUALIFIER_TYPE_NAME));
		availableCheck.add(project.getQualifier(CDIConstants.NEW_QUALIFIER_TYPE_NAME));
		availableCheck.add(project.getQualifier(CDIProject.QUALIFIER1));
		availableCheck.add(project.getQualifier(CDIProject.QUALIFIER2));
		availableCheck.add(project.getQualifier(CDIProject.QUALIFIER3));
		availableCheck.add(project.getQualifier(CDIProject.QUALIFIER4));
		availableCheck.add(project.getQualifier(CDIProject.QUALIFIER5));
		
		deployedCheck = new ArrayList<IQualifier>();
		
		deployedCheck.add(project.getQualifier(CDIConstants.ANY_QUALIFIER_TYPE_NAME));
		deployedCheck.add(project.getQualifier(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME));

		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		wizard = new AddQualifiersToBeanWizard(injectionPoint, beans, bean);
		dialog = new WizardDialog(shell, wizard);
		
		dialog.setBlockOnOpen(false);
		dialog.open();
	}

	@Override
	protected void tearDown() throws Exception {
		dialog.close();
		super.tearDown();
	}
	
	private void checkQualifierLists(List<IQualifier> checkQualifiers, List<IQualifier> actualQualifiers){
		for(IQualifier q : checkQualifiers){
			if(!actualQualifiers.contains(q))
				fail("Qualifier - "+q.getSourceType().getFullyQualifiedName()+" not found");
		}
		for(IQualifier q : actualQualifiers){
			if(!checkQualifiers.contains(q))
				fail("Wrong Qualifier - "+q.getSourceType().getFullyQualifiedName()+" found");
		}
	}
	
	public void testAddQualifier(){
		List<IQualifier> available = wizard.getAvailableQualifiers();
		
		checkQualifierLists(availableCheck, available);
		
		List<IQualifier> deployed = wizard.getDeployedQualifiers();

		checkQualifierLists(deployedCheck, deployed);
		
		// Deploy qualifier
		IQualifier qualifier = project.getQualifier(CDIProject.QUALIFIER1);
		IQualifier defaultQualifier = project.getQualifier(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		
		wizard.deploy(qualifier);
		
		available = wizard.getAvailableQualifiers();
		availableCheck.remove(qualifier);
		
		checkQualifierLists(availableCheck, available);
		
		deployed = wizard.getDeployedQualifiers();
		
		deployedCheck.add(qualifier);
		deployedCheck.remove(defaultQualifier);
		
		checkQualifierLists(deployedCheck, deployed);
	}
	
	public void testAddAndRemoveQualifier(){
		List<IQualifier> available = wizard.getAvailableQualifiers();
		
		checkQualifierLists(availableCheck, available);
		
		List<IQualifier> deployed = wizard.getDeployedQualifiers();

		checkQualifierLists(deployedCheck, deployed);
		
		// Deploy qualifier
		IQualifier qualifier = project.getQualifier(CDIProject.QUALIFIER2);
		IQualifier defaultQualifier = project.getQualifier(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		
		wizard.deploy(qualifier);
		
		available = wizard.getAvailableQualifiers();
		availableCheck.remove(qualifier);
		
		checkQualifierLists(availableCheck, available);
		
		deployed = wizard.getDeployedQualifiers();
		
		deployedCheck.add(qualifier);
		deployedCheck.remove(defaultQualifier);
		
		checkQualifierLists(deployedCheck, deployed);
		
		// Remove qualifier
		wizard.remove(qualifier);
		
		available = wizard.getAvailableQualifiers();
		availableCheck.add(qualifier);
		
		checkQualifierLists(availableCheck, available);
		
		deployed = wizard.getDeployedQualifiers();
		
		deployedCheck.remove(qualifier);
		deployedCheck.add(defaultQualifier);
		
		checkQualifierLists(deployedCheck, deployed);
	}
	
	public void testAddNamedQualifier(){
		List<IQualifier> available = wizard.getAvailableQualifiers();
		
		checkQualifierLists(availableCheck, available);
		
		List<IQualifier> deployed = wizard.getDeployedQualifiers();

		checkQualifierLists(deployedCheck, deployed);
		
		// Deploy @Named qualifier
		IQualifier named = project.getQualifier(CDIConstants.NAMED_QUALIFIER_TYPE_NAME); 
		
		wizard.deploy(named);
		
		available = wizard.getAvailableQualifiers();
		availableCheck.remove(named);
		
		checkQualifierLists(availableCheck, available);
		
		deployed = wizard.getDeployedQualifiers();
		
		deployedCheck.add(named);
		
		checkQualifierLists(deployedCheck, deployed);
	}

}
