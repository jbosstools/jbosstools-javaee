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
package org.jboss.tools.vpe.ui.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * @author Max Areshkau
 * 
 * Class created to run all ui tests for VPE together
 */
public class VpeAllTests {

	public static final String VPE_TEST_EXTENTION_POINT_ID = "org.jboss.tools.jsf.vpe.ui.tests"; //$NON-NLS-1$

	public static final String TEST_SUITE_PARAM = "testSuite"; //$NON-NLS-1$
	
	public static final String METHOD_SUITE_NAME = "suite"; //$NON-NLS-1$

	public static Test suite() {

		TestSuite result = new TestSuite();
		IExtensionRegistry extensionRepository = Platform
				.getExtensionRegistry();

		IExtensionPoint extensionPoint = extensionRepository
				.getExtensionPoint(VPE_TEST_EXTENTION_POINT_ID);
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] confElements = extension
					.getConfigurationElements();
			for (IConfigurationElement configurationElement : confElements) {
				String clazz = configurationElement
						.getAttribute(TEST_SUITE_PARAM);
				try {
					Bundle bundle = Platform.getBundle(configurationElement
							.getNamespaceIdentifier());
					Class<?> testObject = bundle.loadClass(clazz);
					Method method = testObject.getMethod(METHOD_SUITE_NAME, null);
					// null -because static method
					Object res = method.invoke(null, null);
					if (res instanceof Test) {
						Test testSuite = (Test) res;
						result.addTest(testSuite);
					}
				} catch (ClassNotFoundException e) {
					VPETestPlugin.getDefault().logError(e);
				} catch (SecurityException e) {
					VPETestPlugin.getDefault().logError(e);
				} catch (NoSuchMethodException e) {
					VPETestPlugin.getDefault().logError(e);
				} catch (IllegalArgumentException e) {
					VPETestPlugin.getDefault().logError(e);
				} catch (IllegalAccessException e) {
					VPETestPlugin.getDefault().logError(e);
				} catch (InvocationTargetException e) {
					VPETestPlugin.getDefault().logError(e);
				}
			}
		}
		return result;

	}

}
