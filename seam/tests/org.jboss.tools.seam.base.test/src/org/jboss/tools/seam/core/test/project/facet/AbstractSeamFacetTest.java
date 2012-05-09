package org.jboss.tools.seam.core.test.project.facet;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.AntCopyUtils.FileSet;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.project.facet.Seam23FacetInstallDelegate;
import org.jboss.tools.seam.internal.core.project.facet.Seam23ProjectCreator;
import org.jboss.tools.seam.internal.core.project.facet.Seam2FacetInstallDelegate;
import org.jboss.tools.seam.internal.core.project.facet.Seam2ProjectCreator;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetInstallDataModelProvider;
import org.jboss.tools.seam.internal.core.project.facet.SeamLibFileSetProvider;
import org.jboss.tools.seam.internal.core.project.facet.SeamProjectCreator;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * Base class for facet related tests; based on the facet test class found in
 * WTP test suite.
 * 
 * @author max
 * 
 */
public abstract class AbstractSeamFacetTest extends TestCase {
	
	protected static final IWorkspace ws = ResourcesPlugin.getWorkspace();
	
	protected SeamRuntime seamRuntime;
	
	protected static final String SEAM_1_2_0 = "Seam 1.2.0";
	protected static final String SEAM_2_0_0 = "Seam 2.0.0";
	
	protected static final String SEAM_1_2_HOME = "jbosstools.test.seam.1.2.1.eap.home";
	protected static final String SEAM_2_0_HOME = "jbosstools.test.seam.2.0.1.GA.home";
	
	protected static final IProjectFacetVersion seamFacetVersion;
	protected static final IProjectFacetVersion dynamicWebVersion;
	protected static final IProjectFacetVersion javaVersion;
	protected static final IProjectFacetVersion javaFacesVersion;
	
	private static final IProjectFacet seamFacet;
	
	static {
		seamFacet = ProjectFacetsManager.getProjectFacet("jst.seam");
		seamFacetVersion = seamFacet.getVersion("1.2");
		
		javaVersion = JavaFacet.JAVA_50; 
		dynamicWebVersion = ProjectFacetsManager.getProjectFacet("jst.web").getVersion("2.5");
		javaFacesVersion = ProjectFacetsManager.getProjectFacet("jst.jsf").getVersion("1.2");
		
	}
	
	protected final ArrayList<IResource> resourcesToCleanup = new ArrayList<IResource>();
	protected final List<Runnable> tearDownOperations = new ArrayList<Runnable>();

	protected AbstractSeamFacetTest(final String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		assertSeamHomeAvailable();
		File folder = getSeamHomeFolder();

		SeamRuntimeManager.getInstance().addRuntime(SEAM_1_2_0, folder.getAbsolutePath(), SeamVersion.SEAM_1_2, true);
		seamRuntime = SeamRuntimeManager.getInstance().findRuntimeByName(SEAM_1_2_0);
	}

	public static final String SYS_PROP_IS_NOT_DEFINED = "System property {0} is not defined";
	
	protected File getSeamHomeFolder() {
		String seamHomeFolder = System.getProperty(getSystemPropertyName());
		if(seamHomeFolder==null) {
			throw new IllegalStateException(
					MessageFormat.format(SYS_PROP_IS_NOT_DEFINED, getSystemPropertyName())
					);
		}
		return new File(seamHomeFolder).getAbsoluteFile();
	}
	
	public static final String SEAM_EAP_121_HOME_PROPERY = "jbosstools.test.seam.1.2.1.eap.home";
	
	protected String getSystemPropertyName() {
		return SEAM_EAP_121_HOME_PROPERY;
	}
	
	protected void tearDown() throws Exception

	{
		
		// Wait until all jobs is finished to avoid delete project problems
		
	    boolean oldAutoBuilding = true; 
		Exception last = null;
		try {
			oldAutoBuilding = ResourcesUtils.setBuildAutomatically(false);
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		    JobUtils.waitForIdle(); 
			for (IResource r : this.resourcesToCleanup) {
				try {
					System.out.println("Deleting resource " + r.getLocation());
					if(r instanceof IProject) {
						IProject project = (IProject)r;
						for (String natureId : project.getDescription().getNatureIds()) {
							IProjectNature nature = project.getNature(natureId);
							nature.deconfigure();
						}						
					}
					try {
						r.delete(true, null);
					} catch (Exception ex) {
						// Ignore any exceptions here (mostly because ResourceException rising is possible here)
						// But we cannot break tearDown() procedures in test cases which widely use this method
						// So, just print an exception stacktrace to see it in console log
						ex.printStackTrace();
					}
				} catch(Exception e) {
					e.printStackTrace();
					last = e;
				}
			}

			for (Runnable runnable : this.tearDownOperations) {
				runnable.run();
			}
		} finally {
			ResourcesUtils.setBuildAutomatically(oldAutoBuilding); 
		}
		
// 		Do not throw any exceptions in tearDown procedure unless they are important
//		if(last!=null) throw last;
	}

	protected final void addResourceToCleanup(final IResource resource) {
		if(resource==null) throw new IllegalArgumentException();
		if(resourcesToCleanup.contains(resource))  throw new IllegalArgumentException();
		this.resourcesToCleanup.add(resource);
//		System.out.println("Resource is added to clean up list: " + resource);
	}

	protected final void addResourcesToCleanup(final IResource[] resource) {
		if(resource==null) throw new IllegalArgumentException();
		this.resourcesToCleanup.addAll(Arrays.asList(resource));
//		for (IResource r : resource) {
//			System.out.println("Resource is added to clean up list: " + r);
//		}
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

	
	protected IDataModel createSeamDataModel(String deployType) {
		IDataModel config = (IDataModel) new SeamFacetInstallDataModelProvider().create();
		config.setStringProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, SEAM_1_2_0);
		config.setBooleanProperty(ISeamFacetDataModelProperties.DB_ALREADY_EXISTS, true);
		config.setBooleanProperty(ISeamFacetDataModelProperties.RECREATE_TABLES_AND_DATA_ON_DEPLOY, false);
		config.setStringProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS, deployType);
		config.setStringProperty(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME, "org.session.beans");
		config.setStringProperty(ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME, "org.entity.beans");
		config.setStringProperty(ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME, "org.test.beans");
		config.setStringProperty(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE, "noop-connection");
		config.setProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH, new String[] { "noop-driver.jar" });
		config.setBooleanProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_LIBRARIES_COPYING, true);
		config.setBooleanProperty(ISeamFacetDataModelProperties.SEAM_TEMPLATES_AND_LIBRARIES_COPYING, true);
		return config;
	}

	protected void installDependentFacets(final IFacetedProject fproj) throws CoreException {
		fproj.installProjectFacet(javaVersion, null, null);
		fproj.installProjectFacet(dynamicWebVersion, null, null);
		fproj.installProjectFacet(javaFacesVersion, null, null);
	}
	
	protected void setIfEmpty(IDataModel model, String propertyName, String value){
		String oldValue = model.getStringProperty(propertyName);
		if(oldValue == null || "".equals(oldValue)){
			model.setStringProperty(propertyName, value);
		}
	}

	protected IFacetedProject createSeamProject(String baseProjectName, final IDataModel config) throws CoreException {
		final IFacetedProject fproj = ProjectFacetsManager.create(baseProjectName, null,
				null);
	
		installDependentFacets(fproj);
//		new SeamFacetPreInstallDelegate().execute(fproj.getProject(), getSeamFacetVersion(), config, null);
		fproj.installProjectFacet(getSeamFacetVersion(), config, null);
		
		SeamProjectsSet seamProjectsSet = new SeamProjectsSet(fproj.getProject());
		assertTrue(seamProjectsSet.getActionFolder().exists());
		assertTrue(seamProjectsSet.getModelFolder().exists());
		
		final IProject proj = fproj.getProject();
		
		setIfEmpty(config, ISeamFacetDataModelProperties.SEAM_TEST_PROJECT, baseProjectName+"-test");
		setIfEmpty(config, ISeamFacetDataModelProperties.SEAM_EAR_PROJECT, baseProjectName+"-ear");
		setIfEmpty(config, ISeamFacetDataModelProperties.SEAM_EJB_PROJECT, baseProjectName+"-ejb");
		
		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				String seamVersionString = config.getProperty(IFacetDataModelProperties.FACET_VERSION_STR).toString();
				SeamVersion seamVersion = SeamVersion.parseFromString(seamVersionString);
				SeamProjectCreator creator = null;
				if(seamVersion == SeamVersion.SEAM_1_2) {
					creator = new SeamProjectCreator(config, proj, new SeamLibFileSetProvider() {
						@Override
						public FileSet getEarLibFileSet() {
							return null;
						}
						@Override
						public FileSet getWarLibFileSet() {
							return null;
						}
						@Override
						public FileSet getWarLibFileSetForEar() {
							return null;
						}
					});
				} else if(seamVersion == SeamVersion.SEAM_2_0 ||
						seamVersion == SeamVersion.SEAM_2_1 ||
						seamVersion == SeamVersion.SEAM_2_2) {
					creator = new Seam2ProjectCreator(config, proj, new SeamLibFileSetProvider() {
						@Override
						public FileSet getEarLibFileSet() {
							return Seam2FacetInstallDelegate.SEAM2_JBOSS_EAR_LIB;
						}
						@Override
						public FileSet getWarLibFileSet() {
							return Seam2FacetInstallDelegate.SEAM2_JBOSS_WAR_LIB_FILESET_WAR_CONFIG;
						}
						@Override
						public FileSet getWarLibFileSetForEar() {
							return Seam2FacetInstallDelegate.SEAM2_JBOSS_WAR_LIB_FILESET_WAR_CONFIG;
						}
					});
				} else if(seamVersion == SeamVersion.SEAM_2_3) {
					creator = new Seam23ProjectCreator(config, proj, new SeamLibFileSetProvider() {
						@Override
						public FileSet getEarLibFileSet() {
							return Seam23FacetInstallDelegate.getEarLibFileSet(config);
						}
						@Override
						public FileSet getWarLibFileSet() {
							return Seam23FacetInstallDelegate.getWarLibFileSet(config);
						}
						@Override
						public FileSet getWarLibFileSetForEar() {
							return Seam23FacetInstallDelegate.getWarLibFileSetForEar(config);
						}
					});
				} else {
					throw new RuntimeException("Can't get seam version from seam facet model");
				}
				creator.execute(monitor);
				proj.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			}
			
		},new NullProgressMonitor());

		// Fix for JBIDE-JBIDE-7690: Let all the project to be created before we'll add them to the clean up list
		JobUtils.waitForIdle();
		SeamProjectsSet seamAllProjectsSet = new SeamProjectsSet(fproj.getProject());
		this.addResourcesToCleanup(seamAllProjectsSet.getAllProjects());

		return fproj;
	}

	protected IFacetedProject createSeamWarProject(String name) throws CoreException {
		final IFacetedProject fproj = createSeamProject(name, createSeamDataModel("war"));
		
		final IProject proj = fproj.getProject();

		assertNotNull(proj);
		assertTrue(proj.exists());

		assertTrue(proj.getWorkspace().getRoot().getProject(proj.getName() + "-test").exists());
		IProject testProject = proj.getWorkspace().getRoot().getProject(proj.getName() + "-test");

		
		return fproj;
	}

	protected IFacetedProject createSeamEarProject(String name) throws CoreException {
		final IFacetedProject fproj = createSeamProject(name, createSeamDataModel("ear"));
		SeamProjectsSet seamProjectsSet = new SeamProjectsSet(fproj.getProject());
		final IProject proj = fproj.getProject();
		assertNotNull(proj);

		assertTrue(seamProjectsSet.getWarProject().exists());
		assertTrue(seamProjectsSet.getTestProject().exists());
		assertTrue(seamProjectsSet.getEjbProject().exists());
		assertTrue(seamProjectsSet.getEarProject().exists());
		
		return fproj;
	}
	
	protected IProjectFacetVersion getSeamFacetVersion() {
		return seamFacetVersion;
	}
	
	public void assertSeamHomeAvailable() {
		File folder = getSeamHomeFolder();
		
		assertNotNull("seam home folder was null!", folder);
		assertTrue(folder.getAbsolutePath() + " does not exist", folder.exists());
		
		//System.out.println("Listing " + folder);
		File[] list = folder.listFiles();
		for (int i = 0; i < list.length; i++) {
			File string = list[i];
			//System.out.println(i + ": " + string.getName() +(string.isDirectory()?" (dir)":""));
		}
		
		File seamgen = new File(folder, "seam-gen");
		assertNotNull("seam gen folder was null!", seamgen);
		assertTrue(seamgen.getName() + " seamgen does not exist", seamgen.exists());
		
		//System.out.println("Listing seamgen " + seamgen);
		list = seamgen.listFiles();
		for (int i = 0; i < list.length; i++) {
			File string = list[i];
			//System.out.println(i + ": " + string.getName() +(string.isDirectory()?" (dir)":""));
		}
			
	}
	
}