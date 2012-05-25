/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.deltaspike.core.test;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.test.DependentProjectTest;
import org.jboss.tools.cdi.deltaspike.core.DeltaspikeAuthorityMethod;
import org.jboss.tools.cdi.deltaspike.core.DeltaspikeConstants;
import org.jboss.tools.cdi.deltaspike.core.DeltaspikeSecurityBindingConfiguration;
import org.jboss.tools.cdi.deltaspike.core.DeltaspikeSecurityExtension;
import org.jboss.tools.cdi.deltaspike.core.SecurityBindingDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class DeltaspikeBeansTest extends DeltaspikeCoreTest {

	public void testExcluded() throws Exception {
		IProject project = getTestProject();
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		CDICoreNature n = cdi.getNature();
		TypeDefinition def = n.getDefinitions().getTypeDefinition("deltaspike.exclude.ExcludedBean"); //$NON-NLS-1$
		assertNotNull(def);
		assertTrue(def.isAnnotationPresent(CDIConstants.NAMED_QUALIFIER_TYPE_NAME));
		assertTrue(def.isAnnotationPresent(DeltaspikeConstants.EXCLUDE_ANNOTATION_TYPE_NAME));
		
		Set<IBean> bs = cdi.getBeans(def.getResource().getFullPath());
		assertTrue(bs.isEmpty());
	}

	public void testConfigProperty() throws Exception {
		IProject project = getTestProject();
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);

		IInjectionPointField f1 = DependentProjectTest.getInjectionPointField(cdi, project, "/src/deltaspike/config/SettingsBean.java", "property1"); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(f1);
		assertTrue(f1.isAnnotationPresent(DeltaspikeConstants.CONFIG_PROPERTY_ANNOTATION_TYPE_NAME));

	}

	public void testMessages() throws Exception {
		IProject project = getTestProject();
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);

		IInjectionPointField f1 = DependentProjectTest.getInjectionPointField(cdi, project, "/src/deltaspike/message/MyBean.java", "messages"); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(f1);
		Set<IBean> bs = cdi.getBeans(true, f1);
		assertEquals(1, bs.size());
		
		IInjectionPointField f2 = DependentProjectTest.getInjectionPointField(cdi, project, "/src/deltaspike/message/MyBean.java", "messageContext"); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(f2);
		bs = cdi.getBeans(true, f2);
		assertEquals(1, bs.size());
	}

	public void testHandler() throws Exception {
		IProject project = getTestProject();
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);

		Set<IBean> bs = cdi.getBeans(new Path("/DeltaspikeCoreTest/src/deltaspike/handler/MyHandlers.java")); //$NON-NLS-1$
		IClassBean cb = null;
		for (IBean b: bs) {
			if(b instanceof IClassBean) cb = (IClassBean)b;
		}
		Set<IInjectionPoint> ps = cb.getInjectionPoints();
		assertEquals(1, ps.size());
		IInjectionPoint p = ps.iterator().next();
		assertTrue(p instanceof IInjectionPointParameter);
		bs = cdi.getBeans(true, p);
		assertEquals(1, bs.size());
	}

	public void testSecurity() throws Exception {
		IProject project = getTestProject();
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		DeltaspikeSecurityExtension extension = DeltaspikeSecurityExtension.getExtension(cdi.getNature());
		assertNotNull(extension);

		DeltaspikeSecurityBindingConfiguration c = extension.getContext().getConfiguration("deltaspike.security.CustomSecurityBinding"); //$NON-NLS-1$
		assertNotNull(c);
		Set<DeltaspikeAuthorityMethod> as = c.getAuthorizerMembers();
		assertEquals(3, as.size());
		Map<AbstractMemberDefinition, SecurityBindingDeclaration> bs = c.getBoundMembers();
		assertEquals(3, bs.size());
		
		for (AbstractMemberDefinition d: bs.keySet()) {
			String methodName = ((MethodDefinition)d).getMethod().getElementName();
			SecurityBindingDeclaration b = bs.get(d);
			int k = 0;
			for (DeltaspikeAuthorityMethod a: as) {
				if(a.isMatching(b.getBinding())) {
					k++;
				}
			}
			if("doSomething1".equals(methodName)) { //$NON-NLS-1$
				assertEquals(1, k);
			} else if("doSomething2".equals(methodName)) { //$NON-NLS-1$
				assertEquals(2, k);				
			} else if("doSomething3".equals(methodName)) { //$NON-NLS-1$
				assertEquals(0, k);				
			}
		}
		
	}
}
