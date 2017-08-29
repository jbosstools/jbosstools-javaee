/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.jboss.tools.cdi.core.CDIVersion;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages10;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages11;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages12;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages20;

import junit.framework.TestCase;

/**
 * CDIValidationMessages is validated by rules described in the annotation to it.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDIValidationMessagesTest extends TestCase {
	static Class[] versionMessages = {
		CDIValidationMessages10.class,
		CDIValidationMessages11.class,
		CDIValidationMessages12.class,
		CDIValidationMessages20.class
	};

	public CDIValidationMessagesTest() {}

	public void testMessages() throws Exception {
		StringBuffer errorList = new StringBuffer();
		for (Field f: CDIValidationMessages.class.getFields()) {
			if(Modifier.isStatic(f.getModifiers())) {
				String name = f.getName();
				f.setAccessible(true);
				
				String[] values = (String[])f.get(null);
				if(values.length != versionMessages.length || values.length != CDIVersion.getVersionCount()) {
					errorList.append("Wrong array length " + values.length + " for message " + name);
				} else {
					String[] contributions = new String[values.length];
					for (int i = 0; i < values.length; i++) {
						try {
							Field fi = versionMessages[i].getField(name);
							fi.setAccessible(true);
							contributions[i] = (String)fi.get(null);
						} catch (NoSuchFieldException e) {
							//field may be missing, it is not an error.
						}
					}
					for (int i = 0; i < values.length; i++) {
						String value = values[i];
						if(value == null) {
							if(contributions[i] != null) {
								errorList.append("Value " + i + " for message " + name + " is not assigned.");
							}
						} else {
							String v = null;
							for (int j = i; v == null && j >= 0; j--) {
								if(contributions[j] != null) v = contributions[j];
							}
							if(!value.equals(v)) {
								errorList.append("Value " + i + " for message " + name + " is assigned to a wrong value.");
							}
						}
					}
				}
				
			}
		}
		
		assertTrue(errorList.toString(), errorList.length() == 0);
	}

}
