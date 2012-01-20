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
package org.jboss.tools.cdi.core.test;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.filesystems.impl.Libs;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.validation.ValidatorManager;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 *   
 * @author V.Kabanovich
 *
 */
public class RemoveJarFromClasspathTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project = null;

	public RemoveJarFromClasspathTest() {}

	public void setUp() throws Exception {
		project = ResourcesUtils.importProject(PLUGIN_ID, "/projects/RemoveJarTest");
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}

	/**
	 * Project RemoveJarTest has cdi-simple.jar added to classpath.
	 * Class test.BeanA from that jar is loaded into CDI model of RemoveJarTest project.
	 * 
	 * When cdi-simple.jar is excluded from classpath, incremental build should clean from context objects loaded 
	 * from cdi-simple.jar.
	 * 
	 * When cdi-simple.jar is restored in classpath, incremental build should again load it into CDI model.
	 * 
	 * Since this is an integration test, it also checks Libs object - cache for classpath used to update CDI model,
	 * in that way we may separate failure caused by underlying common model and failure in CDI builder.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 */
	public void testRemoveJarFromClasspath() throws CoreException, IOException {
		XModel model = EclipseResourceUtil.createObjectForResource(project).getModel();
		Libs libs = FileSystemsHelper.getLibs(model);
		assertTrue("cdi-simple.jar should be included into XModel.", contains(libs, "/cdi-simple.jar"));

		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		TypeDefinition def = cdi.getNature().getDefinitions().getTypeDefinition("test.BeanA");
		assertNotNull("Class test.BeanA should be loaded into CDI model.", def);		
		
		replaceFile(project, "/classpath.1", "/.classpath");
		assertFalse("cdi-simple.jar should be excluded from XModel.", contains(libs, "/cdi-simple.jar"));
		def = cdi.getNature().getDefinitions().getTypeDefinition("test.BeanA");
		assertNull("Class test.BeanA should be cleaned from CDI model.", def);

		replaceFile(project, "/classpath.original", "/.classpath");
		assertTrue("cdi-simple.jar should be included into XModel.", contains(libs, "/cdi-simple.jar"));
		def = cdi.getNature().getDefinitions().getTypeDefinition("test.BeanA");
		assertNotNull("Class test.BeanA should be reloaded into CDI model.", def);
	}

	private boolean contains(Libs libs, String path) {
		List<String> paths = libs.getPaths();
		for (String p: paths) {
			if(p.endsWith(path)) return true;
		}
		return false;
	}

	/**
	 * Util method.
	 * 
	 * @param project
	 * @param sourcePath
	 * @param targetPath
	 * @throws CoreException
	 */
	public static void replaceFile(IProject project, String sourcePath, String targetPath) throws CoreException {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			IFile target = project.getFile(new Path(targetPath));
			IFile source = project.getFile(new Path(sourcePath));
			assertTrue(source.exists());
			ValidatorManager.setStatus(ValidatorManager.RUNNING);
			if(!target.exists()) {
				target.create(source.getContents(), true, new NullProgressMonitor());
			} else {
				target.setContents(source.getContents(), true, false, new NullProgressMonitor());
			}
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
			JobUtils.waitForIdle();
		}
	}

	public void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		project.delete(true, true, null);
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		JobUtils.waitForIdle();
	}
}