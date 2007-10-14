package org.jboss.tools.seam.core.test.project.facet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetInstallDataModelProvider;

/**
 * Base class for facet related tests; based on the facet test class found in
 * WTP test suite.
 * 
 * @author max
 * 
 */
public abstract class AbstractSeamFacetTest extends TestCase {
	
	protected static final IWorkspace ws = ResourcesPlugin.getWorkspace();
	
	private SeamRuntime seamRuntime;
	protected static final String SEAM_1_2_0 = "Seam 1.2.0";
	protected static final IProjectFacetVersion seamFacetVersion;
	protected static final IProjectFacetVersion dynamicWebVersion;
	protected static final IProjectFacetVersion javaVersion;
	protected static final IProjectFacetVersion javaFacesVersion;
	
	private static final IProjectFacet seamFacet;
	
	static {
		seamFacet = ProjectFacetsManager.getProjectFacet("jst.seam");
		seamFacetVersion = seamFacet.getVersion("1.2");
		
		javaVersion = ProjectFacetsManager.getProjectFacet("jst.java").getVersion("5.0");
		dynamicWebVersion = ProjectFacetsManager.getProjectFacet("jst.web").getVersion("2.5");
		javaFacesVersion = ProjectFacetsManager.getProjectFacet("jst.jsf").getVersion("1.2");
		
	}
	
	protected final Set<IResource> resourcesToCleanup = new HashSet<IResource>();
	protected final List<Runnable> tearDownOperations = new ArrayList<Runnable>();

	protected AbstractSeamFacetTest(final String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		File folder = new File(System.getProperty("jbosstools.test.seam.1.2.1.eap.home", "/home/max/rhdevstudio/jboss-eap/seam"));
		
		SeamRuntimeManager.getInstance().addRuntime(SEAM_1_2_0, folder.getAbsolutePath(), SeamVersion.SEAM_1_2, true);
		seamRuntime = SeamRuntimeManager.getInstance().findRuntimeByName(SEAM_1_2_0);
		
	}
	protected void tearDown()

	throws CoreException

	{
		for (IResource r : this.resourcesToCleanup) {
			r.delete(true, null);
		}

		for (Runnable runnable : this.tearDownOperations) {
			runnable.run();
		}
	}

	protected final void addResourceToCleanup(final IResource resource) {
		this.resourcesToCleanup.add(resource);
	}

	protected final void addTearDownOperation(final Runnable runnable) {
		this.tearDownOperations.add(runnable);
	}
	
	protected IFacetedProject createFacetedProject(final String name) throws CoreException

	{
		assertFalse(ws.getRoot().getProject(name).exists());
		final IFacetedProject fpj = ProjectFacetsManager.create(name, null,
				null);
		final IProject pj = fpj.getProject();
		assertTrue(pj.exists());
		addResourceToCleanup(pj);

		return fpj;
	}

	protected IFacetedProject createSeamWarProject() throws CoreException {
		final IFacetedProject fproj = ProjectFacetsManager.create("seam12Project", null,
				null);
	
		installDependentFacets(fproj);
		
		IDataModel config = createSeamDataModel("war");
		
		fproj.installProjectFacet(seamFacetVersion, config, null);
		
		final IProject proj = fproj.getProject();

		assertNotNull(proj);
		assertTrue(proj.exists());

		assertTrue(proj.getWorkspace().getRoot().getProject(proj.getName() + "-test").exists());
	
		this.resourcesToCleanup.add(proj);		

		return fproj;
	}

	protected IDataModel createSeamDataModel(String deployType) {
		IDataModel config = (IDataModel) new SeamFacetInstallDataModelProvider().create();
		config.setStringProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, SEAM_1_2_0);
		config.setBooleanProperty(ISeamFacetDataModelProperties.DB_ALREADY_EXISTS, true);
		config.setBooleanProperty(ISeamFacetDataModelProperties.RECREATE_TABLES_AND_DATA_ON_DEPLOY, false);
		config.setStringProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS, deployType);
		config.setStringProperty(ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_NAME, "org.session.beans");
		config.setStringProperty(ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME, "org.entity.beans");
		config.setStringProperty(ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME, "org.test.beans");
		config.setStringProperty(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE, "noop-connection");
		config.setProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH, new String[] { "noop-driver.jar" });
		return config;
	}

	protected void installDependentFacets(final IFacetedProject fproj) throws CoreException {
		fproj.installProjectFacet(javaVersion, null, null);
		fproj.installProjectFacet(dynamicWebVersion, null, null);
		fproj.installProjectFacet(javaFacesVersion, null, null);
	}

	protected IFacetedProject createSeamEarProject() throws CoreException {
		final IFacetedProject fproj = ProjectFacetsManager.create("seamear12Project", null,
				null);
	
		installDependentFacets(fproj);
		
		IDataModel config = createSeamDataModel("ear");
		
		fproj.installProjectFacet(seamFacetVersion, config, null);
		
		final IProject proj = fproj.getProject();
		assertNotNull(proj);
		
		IProject testProject = proj.getWorkspace().getRoot().getProject(proj.getName() + "-test");
		IProject ejbProject = proj.getWorkspace().getRoot().getProject(proj.getName() + "-ejb");
		IProject earProject = proj.getWorkspace().getRoot().getProject(proj.getName() + "-ear");
		
		this.resourcesToCleanup.add(proj);
		this.resourcesToCleanup.add(testProject);
		this.resourcesToCleanup.add(ejbProject);
		this.resourcesToCleanup.add(earProject);

		assertTrue(proj.exists());
		assertTrue(testProject.exists());
		assertTrue(ejbProject.exists());
		assertTrue(earProject.exists());

		return fproj;
	}

}