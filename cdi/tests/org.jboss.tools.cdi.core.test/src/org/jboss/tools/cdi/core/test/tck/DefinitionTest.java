/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test.tck;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ITypeDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * Section 2 - Concepts
 *
 * @author Alexey Kazakov
 */
public class DefinitionTest extends TCKTest {

	/**
	 * a) A bean comprises of a (nonempty) set of bean types.
	 */
	public void testBeanTypesNonEmpty() {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/bean/RedSnapper.java");
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		assertEquals("There should be the only bean in org.jboss.jsr299.tck.tests.definition.bean.RedSnapper", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertTrue("No legal types were found for org.jboss.jsr299.tck.tests.definition.bean.RedSnapper bean.", bean.getLegalTypes().size() > 0);
		Set<ITypeDeclaration> declarations = bean.getAllTypeDeclarations();
		assertEquals("There should be two type declarations in org.jboss.jsr299.tck.tests.definition.bean.RedSnapper bean.", declarations.size(), 2);
		// TODO use correct start position instead of 0. 
		assertLocationEquals(declarations, 0, 10);
		assertLocationEquals(declarations, 0, 6);
	}

	/**
	 * b) A bean comprises of a (nonempty) set of qualifiers.
	 */
	public void testQualifiersNonEmpty() {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/bean/RedSnapper.java");
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		assertTrue("No qualifiers were found for org.jboss.jsr299.tck.tests.definition.bean.RedSnapper bean.", beans.iterator().next().getQualifiers().size() > 0);
	}

	private void assertLocationEquals(Set<? extends ITextSourceReference> references, int startPosition, int length) {
		for (ITextSourceReference reference : references) {
			if(reference.getStartPosition()==startPosition) {
				assertLocationEquals(reference, startPosition, length);
				return;
			}
		}
		StringBuffer message = new StringBuffer("Location [start positopn=").append(startPosition).append(", lengt=").append(length).append("] has not been found among ");
		for (ITextSourceReference reference : references) {
			message.append("[").append(reference.getStartPosition()).append(", ").append(reference.getLength()).append("] ");
		}
		fail(message.toString());
	}

	private void assertLocationEquals(ITextSourceReference reference, int startPosition, int length) {
		assertEquals("Wrong start position", reference.getStartPosition(), startPosition);
		assertEquals("Wrong length", reference.getLength(), length);
	}
}