package org.jboss.tools.cdi.core.test;

import java.util.ConcurrentModificationException;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ParametedType;
import org.jboss.tools.common.java.ParametedTypeFactory;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

public class TypeTest extends TestCase {
	IProject project = null;

	public TypeTest() {}
	
	@Override
	protected void setUp() throws Exception {
		project = ResourcesUtils.importProject(DependentProjectsTestSetup.PLUGIN_ID, "/projects/TypeTest");
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}

	public void testType() throws Exception {
		ParametedTypeFactory factory = new ParametedTypeFactory();
		IJavaProject jp = JavaCore.create(project);
		IType type = jp.findType("test.Test1");
		ParametedType t = (ParametedType)factory.newParametedType(type);
		R[] rs = new R[3];
		Thread[] ts = new Thread[rs.length];
		for (int i = 0; i < ts.length; i++) {
			rs[i] = new R(t);
			ts[i] = new Thread(rs[i]);
		}
		for (int i = 0; i < ts.length; i++) {
			ts[i].start();			
		}
		for (int i = 0; i < ts.length; i++) {
			ts[i].join();
		}
		for (int i = 0; i < ts.length; i++) {
			if(rs[i].exception != null) {
				fail("" + rs[i].exception);
			}
			assertEquals(11, rs[i].size);
		}		
	}

	class R implements Runnable {
		ParametedType t;
		int size;
		ConcurrentModificationException exception;

		public R(ParametedType t) {
			this.t = t;
		}

		@Override
		public void run() {
			Set<IParametedType> types = t.getAllTypes();
			size = types.size();
			try {
				for (IParametedType t1: types) {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
					}
				}
			} catch (ConcurrentModificationException e) {
				exception = e;
			}
		}
	}

	public void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		project.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}
}