/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.ajax4jsf.test;

import org.jboss.tools.vpe.ui.test.ComponentContentTest;

public class Ajax4JsfComponentContentTest extends ComponentContentTest {

	public Ajax4JsfComponentContentTest(String name) {
		super(name);
		setCheckWarning(false);
	}

	public void testAjaxListener() throws Throwable {
		performInvisibleTagTest("components/ajaxListener.xhtml", "ajaxListener"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testKeepAlive() throws Throwable {
		performInvisibleTagTest("components/keepAlive.xhtml", "keepAlive");//$NON-NLS-1$ //$NON-NLS-2$
	}

	public void _testActionparam() throws Throwable {
		performInvisibleTagTest("components/actionparam.xhtml", "actionparam1"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testCommandButton() throws Throwable {
		performContentTest("components/commandButton.xhtml");//$NON-NLS-1$
	}

	public void testCommandLink() throws Throwable {
		performContentTest("components/commandLink.xhtml");//$NON-NLS-1$
	}

	public void _testForm() throws Throwable {
		performContentTest("components/form.xhtml");//$NON-NLS-1$
	}

	public void testHtmlCommandLink() throws Throwable {
		performContentTest("components/htmlCommandLink.xhtml");//$NON-NLS-1$
	}

	public void testJsFunction() throws Throwable {
		performInvisibleTagTest("components/jsFunction.xhtml", "jsFunction"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testInclude() throws Throwable {
		performContentTest("components/include.xhtml");//$NON-NLS-1$
	}

	public void testLoadBundle() throws Throwable {
		performInvisibleTagTest("components/loadBundle.xhtml", "loadBundle"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testLoadScript() throws Throwable {
		performInvisibleTagTest("components/loadScript.xhtml", "loadScript"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testLoadStyle() throws Throwable {
		performInvisibleTagTest("components/loadStyle.xhtml", "loadStyle"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testLog() throws Throwable {
		performContentTest("components/log.xhtml"); //$NON-NLS-1$
	}

	public void testMediaOutput() throws Throwable {
		performContentTest("components/mediaOutput.xhtml");//$NON-NLS-1$
	}

	public void testOutputPanel() throws Throwable {
	}

	public void _testPage() throws Throwable {
		performContentTest("components/page/page.xhtml");//$NON-NLS-1$
	}

	public void testPoll() throws Throwable {
		performInvisibleTagTest("components/poll.xhtml", "poll"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testPortlet() throws Throwable {
		performContentTest("components/portlet.xhtml");//$NON-NLS-1$
	}

	public void testPush() throws Throwable {
		performInvisibleTagTest("components/push.xhtml", "push"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testQueue() throws Throwable {
		performInvisibleTagTest("components/queue.jsp", "a4jQueue"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void _testRegion() throws Throwable {
		performContentTest("components/region.xhtml");//$NON-NLS-1$
	}

	public void testRepeat() throws Throwable {
		performInvisibleTagTest("components/repeat.xhtml", "repeat"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testStatus() throws Throwable {
		performContentTest("components/status.xhtml");//$NON-NLS-1$
	}

	public void testSupport() throws Throwable {
		performInvisibleTagTest("components/support.xhtml", "support"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testActionListener() throws Throwable {
		performInvisibleTagTest(
				"components/actionListener.xhtml", "actionListener"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testAjax() throws Throwable {
		performInvisibleTagTest("components/ajax.xhtml", "ajax"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testAttachQueue() throws Throwable {
		performInvisibleTagTest("components/attachQueue.xhtml", "attachQueue"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testParam() throws Throwable {
		performInvisibleTagTest("components/param.xhtml", "param"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected String getTestProjectName() {
		return Ajax4JsfAllTests.IMPORT_PROJECT_NAME;
	}

}
