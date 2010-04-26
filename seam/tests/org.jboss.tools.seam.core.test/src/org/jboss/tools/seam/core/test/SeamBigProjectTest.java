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
package org.jboss.tools.seam.core.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.TestProjectProvider;
import org.osgi.framework.Bundle;

/**
 * Test checks that loading Seam model does not depend as N*N on the number of components N.
 * It works as follows:
 * 1. Copy project template to workspace and import it.
 * 2. Create N=1000 java files with seam components in the project and refresh it.
 * 3. Reload Seam model and compute times T1 and T2 needed 
 *    to process first K=200 files and last K=200 files.
 * 4. Test fails if T2 is 3 or more times larger than T1, 
 *    which positively implies N*N dependency.  
 * 
 * @author Viacheslav Kabanovich
 * 
 */
public class SeamBigProjectTest extends TestCase {
	static String BUNDLE = "org.jboss.tools.seam.core.test";
	IProject project;
	TestProjectProvider provider;

	protected void setUp() throws Exception {
		provider = new TestProjectProvider(BUNDLE,"/projects/bigProject" , "bigProject", true);
		project = provider.getProject();
		IFolder folder = project.getFolder(new Path("src/action/p"));
		File template = getTemplateFile();
		SeamBigProjectGenerator g = new SeamBigProjectGenerator();
		g.generate(folder, template);
		JobUtils.waitForIdle();
		//To ensure that the project is built.
		project.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle();
	}
	
	private File getTemplateFile() {
		Bundle bundle = Platform.getBundle(BUNDLE);
		URL url = null;
		try {
			url = FileLocator.resolve(bundle.getEntry("/projects/template.txt"));
		} catch (IOException e) {
			return null; 
		}
		String location = url.getFile();
		return new File(location);
	}
	
	public void testBigProject() {
		ISeamProject sp = getSeamProject();
		ISeamComponent[] cs = sp.getComponents();
		int components = cs.length;
		if(components < 500) {
			fail("Found only " + components + " components. Must be more than 500.");
		}
		SeamProject impl = (SeamProject)sp;
		System.out.println("Full build of " + components + " components completed in " + impl.fullBuildTime + "ms");
		long time = impl.reload();
		System.out.println("Reloaded " + sp.getComponents().length + " components in " + time + "ms");
		List<Long> statistics = impl.statistics;
		impl.statistics = null;
		assertTrue("Statistics contains less than 500 items", statistics.size() >= 500);
		long t1 = 0, t2 = 0;
		for (int i = 0; i < 200; i++) {
			t1 += statistics.get(i);
			t2 += statistics.get(statistics.size() - 1 - i);
		}
		System.out.println("First 200 paths are loaded in " + t1 + "ms");
		System.out.println("Last 200 paths are loaded in " + t2 + "ms");
		double d = 1d * t2 / t1;
		if(d > 5d) {
			fail("It takes " + d + " times longer to load path in the end " 
				+ "of seam model loading than in the beginning.\n"
				+ "That implies that time depends as N*N on the number of components N.");
		}
		
		generateLongXHTML(cs);
	}


	private ISeamProject getSeamProject() {
		ISeamProject seamProject = null;
		try {
			seamProject = (ISeamProject)project.getNature(SeamProject.NATURE_ID);
		} catch (Exception e) {
			JUnitUtils.fail("Cannot get seam nature.",e);
		}
		assertNotNull("Seam project is null", seamProject);
		return seamProject;
	}
	
	@Override
	protected void tearDown() throws Exception {
		ISeamProject sp = getSeamProject();
		SeamProject impl = (SeamProject)sp;
		if(impl != null) impl.clearStorage();
		JobUtils.waitForIdle();
		provider.dispose();
	}

	private void generateLongXHTML(ISeamComponent[] cs) {
		StringBuffer sb = new StringBuffer();
		
		IFolder webContent = project.getFolder("WebContent");
		IFile tmpl = webContent.getFile("login.xhtml");
		String s = FileUtil.readFile(tmpl.getLocation().toFile());
		int i = s.indexOf("<h:form");
		int j = s.indexOf(">", i) + 1;
		sb.append(s.substring(0, j));

		int k = 0;
		for (ISeamComponent c: cs) {
			k++;
			String n = c.getName();
			if(!n.startsWith("x")) continue;
			String q = "\n<h:inputText id=\"k" + k + "\" " + 
                "value=\"#{" + n + ".value}\"/>\n";
			sb.append(q);
		}
		
		sb.append(s.substring(j));
		IFile file = webContent.getFile("long.xhtml");
		try {
//			boolean save = ResourcesUtils.setBuildAutomatically(false);
			file.create(new ByteArrayInputStream(sb.toString().getBytes()), true, new NullProgressMonitor());
			long time = System.currentTimeMillis();
//			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
			JobUtils.waitForIdle();
			long dt = System.currentTimeMillis() - time;
			System.out.println("validated in " + dt);
			
//			ResourcesUtils.setBuildAutomatically(save);

			assertTrue("Validator takes more than 5s (" + ((dt-500)/1000d) + ") for validating generated long.xhtml", ((dt-500)/1000d) < 5);
		} catch (CoreException e) {
			JUnitUtils.fail("", e);
		}
	}

}
