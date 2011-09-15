/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.seam.core.test.international;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.internal.core.el.CdiElResolver;
import org.jboss.tools.cdi.seam.core.test.SeamCoreTest;
import org.jboss.tools.common.el.core.ELReference;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolver;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.jst.text.ext.hyperlink.ELHyperlinkDetector;
import org.jboss.tools.jst.web.kb.PageContextFactory;

/**
 * The JUnit test cases for JBIDE-9480 issue 
 * 
 * @author Victor Rubezhny
 */
public class SeamResourceBundleCdiElResolverTest extends SeamCoreTest {
	private static final String PAGE_NAME = "WebContent/seam-international.xhtml";

	public void testSeamResourceBundleCdiElResolver() throws Exception {
		IFile page = getTestProject().getFile(PAGE_NAME);
		assertTrue("Test page not found: " + PAGE_NAME, (page != null && page.exists())); 
		ELContext context = PageContextFactory.createPageContext(page);

		ELResolver[] elResolvers = context.getElResolvers();
		ELResolver cdiElResolver = null;
		if (elResolvers != null) {
			for (ELResolver r : elResolvers) {
				if (r instanceof CdiElResolver) {
					cdiElResolver = r;
					break;
				}
			}
		}
		assertNotNull("CDI EL resolver is not set up on the project", cdiElResolver);

		List<TextProposal> proposals = cdiElResolver.getProposals(context, "value=\"#{", 1);
		assertTrue("CDI EL resolver must return at least one proposal!", 
				(proposals != null && proposals.size() > 0));

		// For all the following cases no proposals is to be returned 
		proposals = cdiElResolver.getProposals(context, "value=\"#{bundles", 1);
		assertFalse("CDI EL resolver must not to resolve resource bundles/properties but it does!", 
				(proposals != null && proposals.size() > 0));

		proposals = cdiElResolver.getProposals(context, "value=\"#{bundles.messages", 1);
		assertFalse("CDI EL resolver must not to resolve resource bundles/properties but it does!", 
				(proposals != null && proposals.size() > 0));

		proposals = cdiElResolver.getProposals(context, "value=\"#{bundles.messages.home_header", 1);
		assertFalse("CDI EL resolver must not to resolve resource bundles/properties but it does!", 
				(proposals != null && proposals.size() > 0));
	}
	
	public void testSeamResourceBundleCdiElResolverResolution() throws Exception {
		IFile page = getTestProject().getFile(PAGE_NAME);
		assertTrue("Test page not found: " + PAGE_NAME, (page != null && page.exists())); 
		ELContext context = PageContextFactory.createPageContext(page);

		doSeamResourceBundleCdiElResolverResolutionTest(context, 381);
		doSeamResourceBundleCdiElResolverResolutionTest(context, 389);
		doSeamResourceBundleCdiElResolverResolutionTest(context, 398);
	}
	
	private void doSeamResourceBundleCdiElResolverResolutionTest(ELContext context, int offset) {
		ELReference reference = context.getELReference(offset);
		if(reference != null){
			ELInvocationExpression expression = ELHyperlinkDetector.findInvocationExpressionByOffset(reference, offset);
			if(expression != null){
				ELResolver[] resolvers = context.getElResolvers();
				ELResolver cdiElResolver = null;
				if (resolvers != null) {
					for (ELResolver r : resolvers) {
						if (r instanceof CdiElResolver) {
							cdiElResolver = r;
							break;
						}
					}
				}
				assertNotNull("CDI EL resolver is not set up on the project", cdiElResolver);

				ELResolution resolution = cdiElResolver.resolve(context, expression, offset);
				if(resolution != null){
					ELSegment segment = resolution.findSegmentByOffset(offset-reference.getStartPosition());
					assertFalse("CDI EL resolver must not to resolve resource bundles/properties but it does!",
							segment != null && segment.isResolved());
				}
			}
		}		
	}
}
