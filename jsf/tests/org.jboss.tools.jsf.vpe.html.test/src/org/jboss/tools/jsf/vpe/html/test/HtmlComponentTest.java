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
package org.jboss.tools.jsf.vpe.html.test;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * Class for testing all html components
 * 
 * @author Sergey Dzmitrovich
 * 
 */
public class HtmlComponentTest extends VpeTest {

	// import project name
	public static final String IMPORT_PROJECT_NAME = "htmlTest";

	public HtmlComponentTest(String name) {
		super(name,IMPORT_PROJECT_NAME,HtmlTestPlugin.getPluginResourcePath());
		setCheckWarning(false);
	}

	/*
	 * test for block html tags
	 */

	public void testDiv() throws Throwable {
		performTestForJsfComponent("components/block/div.html");
	}

	public void testDl() throws Throwable {
		performTestForJsfComponent(
				"components/block/dl.html");
	}

	public void testLists() throws Throwable {
		performTestForJsfComponent(		
				"components/block/lists.html");
	}

	public void testSpan() throws Throwable {
		performTestForJsfComponent(
				"components/block/span.html");
	}

	/*
	 * test for core html tags
	 */

	public void testA() throws Throwable {
		performTestForJsfComponent(
				"components/core/a.html");
	}

	public void testAddress() throws Throwable {
		performTestForJsfComponent(
				"components/core/address.html");
	}

	public void testBasic() throws Throwable {
		performTestForJsfComponent(
				"components/core/basic.html");
	}

	public void testImg() throws Throwable {
		performTestForJsfComponent(
				"components/core/img.html");
	}

	public void testLink() throws Throwable {
		performTestForJsfComponent(
				"components/core/link.html");
	}

	public void testMap() throws Throwable {
		performTestForJsfComponent(
				"components/core/map.html");
	}

	public void testObject() throws Throwable {
		performTestForJsfComponent(
				"components/core/object.html");
	}

	public void testStyle() throws Throwable {
		performTestForJsfComponent(
				"components/core/style.html");
	}

	public void testTitle() throws Throwable {
		performTestForJsfComponent(
				"components/core/title.html");
	}

	/*
	 * test for form html tags
	 */

	public void testButton() throws Throwable {
		performTestForJsfComponent(
				"components/form/button.html");
	}

	public void testFieldset() throws Throwable {
		performTestForJsfComponent(
				"components/form/fieldset.html");
	}

	public void testForm() throws Throwable {
		performTestForJsfComponent(
				"components/form/form.html");
	}

	public void testInput() throws Throwable {
		performTestForJsfComponent(
				"components/form/input.html");
	}

	public void testLabel() throws Throwable {
		performTestForJsfComponent(
				"components/form/label.html");
	}

	public void testSelect() throws Throwable {
		performTestForJsfComponent(
				"components/form/select.html");
	}

	public void testTextArea() throws Throwable {
		performTestForJsfComponent(
				"components/form/textArea.html");
	}

	/*
	 * test for frames html tags
	 */

	public void testFrameset() throws Throwable {
		performTestForJsfComponent(
				"components/frames/frameset.html");
	}

	public void testIframe() throws Throwable {
		performTestForJsfComponent(
				"components/frames/iframe.html");
	}

	/*
	 * test for scripts html tags
	 */

	public void testScript() throws Throwable {
		performTestForJsfComponent(
				"components/scripts/script.html");
	}

	/*
	 * test for table html tags
	 */

	public void testComplexTable() throws Throwable {
		performTestForJsfComponent(
				"components/table/complex_table.html");
	}

	public void testTable() throws Throwable {
		performTestForJsfComponent(
				"components/table/table.html");
	}

	/*
	 * test for text html tags
	 */

	public void testAbbr() throws Throwable {
		performTestForJsfComponent(
				"components/text/abbr.html");
	}

	public void testAcronym() throws Throwable {
		performTestForJsfComponent(
				"components/text/acronym.html");
	}

	public void testB() throws Throwable {
		performTestForJsfComponent(
				"components/text/b.html");
	}

	public void testBig() throws Throwable {
		performTestForJsfComponent(
				"components/text/big.html");
	}

	public void testBlockquote() throws Throwable {
		performTestForJsfComponent(
				"components/text/blockquote.html");
	}

	public void testBr() throws Throwable {
		performTestForJsfComponent(
				"components/text/br.html");
	}

	public void testCite() throws Throwable {
		performTestForJsfComponent(
				"components/text/cite.html");
	}

	public void testCode() throws Throwable {
		performTestForJsfComponent(
				"components/text/code.html");
	}

	public void testDel() throws Throwable {
		performTestForJsfComponent(
				"components/text/del.html");
	}

	public void testDfn() throws Throwable {
		performTestForJsfComponent(
				"components/text/dfn.html");
	}

	public void testEm() throws Throwable {
		performTestForJsfComponent(
				"components/text/em.html");
	}

	public void testHr() throws Throwable {
		performTestForJsfComponent(
				"components/text/hr.html");
	}

	public void testI() throws Throwable {
		performTestForJsfComponent(
				"components/text/i.html");
	}

	public void testIns() throws Throwable {
		performTestForJsfComponent(
				"components/text/ins.html");
	}

	public void testKbd() throws Throwable {
		performTestForJsfComponent(
				"components/text/kbd.html");
	}

	public void testP() throws Throwable {
		performTestForJsfComponent(
				"components/text/p.html");
	}

	public void testPre() throws Throwable {
		performTestForJsfComponent(
				"components/text/pre.html");
	}

	public void testQ() throws Throwable {
		performTestForJsfComponent(
				"components/text/q.html");
	}

	public void testSamp() throws Throwable {
		performTestForJsfComponent(
				"components/text/samp.html");
	}

	public void testSmall() throws Throwable {
		performTestForJsfComponent(
				"components/text/small.html");
	}

	public void testStrong() throws Throwable {
		performTestForJsfComponent(
				"components/text/strong.html");
	}

	public void testSub() throws Throwable {
		performTestForJsfComponent(
				"components/text/sub.html");
	}

	public void testSup() throws Throwable {
		performTestForJsfComponent(
				"components/text/sup.html");
	}

	public void testTt() throws Throwable {
		performTestForJsfComponent(
				"components/text/tt.html");
	}

	public void testVar() throws Throwable {
		performTestForJsfComponent(
				"components/text/var.html");
	}

}
