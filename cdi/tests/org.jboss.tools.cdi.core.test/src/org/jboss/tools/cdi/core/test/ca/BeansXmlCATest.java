/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.cdi.core.test.ca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IInterceptor;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.internal.core.ca.BeansXmlProcessor;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.jst.web.kb.KbQuery;

/**
 * @author Alexey Kazakov
 */
public class BeansXmlCATest extends TCKTest {

	public void testEmptyList() {
		KbQuery query = new KbQuery();
		query.setParentTags(new String[]{"alternatives", "class"});
		query.setValue(" test ");
		TextProposal[] proposals = BeansXmlProcessor.getInstance().getProposals(query, tckProject);
		assertEquals("The list of proposals should be empty.", 0, proposals.length);
	}

	public void testAllAlternativeBeans() {
		KbQuery query = new KbQuery();
		query.setParentTags(new String[]{"alternatives", "class"});
		query.setValue("");

		TextProposal[] proposals = BeansXmlProcessor.getInstance().getProposals(query, tckProject);
		Map<String, TextProposal> map = generateMap(proposals);
		IBean[] alternatives = cdiProject.getAlternatives();
		List<IClassBean> alternativeClassBeans = new ArrayList<IClassBean>();
		for (IBean bean : alternatives) {
			if(bean instanceof IClassBean) {
				if(map.get(bean.getBeanClass().getFullyQualifiedName())==null) {
					fail("The list of proposals doesn't contain following alternative bean: " + bean.getBeanClass().getFullyQualifiedName());
				}
				alternativeClassBeans.add((IClassBean)bean);
			}
		}
		assertEquals("The number of proposals doesn't equel to the number of alternative beans.", alternativeClassBeans.size(), proposals.length);
	}

	public void testSomeAlternativeBeans() {
		KbQuery query = new KbQuery();
		query.setParentTags(new String[]{"alternatives", "class"});
		query.setValue("org.jboss.jsr299.tck.tests.jbt.ca.");

		TextProposal[] proposals = BeansXmlProcessor.getInstance().getProposals(query, tckProject);
		String[] types = new String[]{"org.jboss.jsr299.tck.tests.jbt.ca.AlternativeClass", "org.jboss.jsr299.tck.tests.jbt.ca.AlternativeClassBean"};
		assertEqualTypes(proposals, types);
	}

	public void testAllAlternativeStereotypes() {
		KbQuery query = new KbQuery();
		query.setParentTags(new String[]{"alternatives", "stereotype"});
		query.setValue(" ");

		TextProposal[] proposals = BeansXmlProcessor.getInstance().getProposals(query, tckProject);
		Map<String, TextProposal> map = generateMap(proposals);
		IStereotype[] alternatives = cdiProject.getStereotypes();
		List<IStereotype> alternativeStereotype = new ArrayList<IStereotype>();
		for (IStereotype stereotype : alternatives) {
			if(stereotype.isAlternative()) {
				if(map.get(stereotype.getSourceType().getFullyQualifiedName())==null) {
					fail("The list of proposals doesn't contain following alternative stereotype: " + stereotype.getSourceType().getFullyQualifiedName());
				}
				alternativeStereotype.add(stereotype);
			}
		}
		assertEquals("The number of proposals doesn't equel to the number of alternative stereotypes.", alternativeStereotype.size(), proposals.length);
	}

	public void testSomeAlternativeStereotypes() {
		KbQuery query = new KbQuery();
		query.setParentTags(new String[]{"alternatives", "stereotype"});
		query.setValue("  org.jboss.jsr299.tck.tests.jbt.ca.");

		TextProposal[] proposals = BeansXmlProcessor.getInstance().getProposals(query, tckProject);
		String[] types = new String[]{"org.jboss.jsr299.tck.tests.jbt.ca.AlternativeStereotype", "org.jboss.jsr299.tck.tests.jbt.ca.AlternativeStereotype2"};
		assertEqualTypes(proposals, types);
	}

	public void testAllDecorators() {
		KbQuery query = new KbQuery();
		query.setParentTags(new String[]{"decorators", "class"});
		query.setValue("");

		TextProposal[] proposals = BeansXmlProcessor.getInstance().getProposals(query, tckProject);
		Map<String, TextProposal> map = generateMap(proposals);
		IDecorator[] decorators = cdiProject.getDecorators();
		for (IDecorator decorator : decorators) {
			if(map.get(decorator.getBeanClass().getFullyQualifiedName())==null) {
				fail("The list of proposals doesn't contain following decorator: " + decorator.getBeanClass().getFullyQualifiedName());
			}
		}
		assertEquals("The number of proposals doesn't equel to the number of decorators.", decorators.length, proposals.length);
	}

	public void testSomeDecorators() {
		KbQuery query = new KbQuery();
		query.setParentTags(new String[]{"decorators", "class"});
		query.setValue("org.jboss.jsr299.tck.tests.jbt.ca.");

		TextProposal[] proposals = BeansXmlProcessor.getInstance().getProposals(query, tckProject);
		String[] types = new String[]{"org.jboss.jsr299.tck.tests.jbt.ca.LoggerDecorator"};
		assertEqualTypes(proposals, types);
	}

	public void testAllInterceptors() {
		KbQuery query = new KbQuery();
		query.setParentTags(new String[]{"interceptors", "class"});
		query.setValue("");

		TextProposal[] proposals = BeansXmlProcessor.getInstance().getProposals(query, tckProject);
		Map<String, TextProposal> map = generateMap(proposals);
		IInterceptor[] interceptors = cdiProject.getInterceptors();
		for (IInterceptor interceptor : interceptors) {
			if(map.get(interceptor.getBeanClass().getFullyQualifiedName())==null) {
				fail("The list of proposals doesn't contain following interceptor: " + interceptor.getBeanClass().getFullyQualifiedName());
			}
		}
		assertEquals("The number of proposals doesn't equel to the number of interceptors.", interceptors.length, proposals.length);
	}

	public void testSomeInterceptors() {
		KbQuery query = new KbQuery();
		query.setParentTags(new String[]{"interceptors", "class"});
		query.setValue("org.jboss.jsr299.tck.tests.jbt.ca.");

		TextProposal[] proposals = BeansXmlProcessor.getInstance().getProposals(query, tckProject);
		String[] types = new String[]{"org.jboss.jsr299.tck.tests.jbt.ca.InterceptorBean"};
		assertEqualTypes(proposals, types);
	}

	private Map<String, TextProposal> generateMap(TextProposal[] proposals) {
		Map<String, TextProposal> map = new HashMap<String, TextProposal>();
		for (TextProposal proposal : proposals) {
			map.put(proposal.getReplacementString(), proposal);
		}
		return map;
	}

	private void assertEqualTypes(TextProposal[] proposals, String[] types) {
		assertEquals("The number of proposals doesn't equel to the number of types.", types.length, proposals.length);
		Map<String, TextProposal> map = generateMap(proposals);
		for (String type : types) {
			if(map.get(type)==null) {
				fail("Can't find " + type + " proposal.");
			}
		}
	}
}