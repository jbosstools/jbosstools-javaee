/*******************************************************************************
 * Copyright (c) 2010-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.uiutils.actions.NewCDIFileWizard;
import org.jboss.tools.ui.bot.ext.SWTOpenExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.gen.ActionItem.NewObject.JavaAnnotation;

public class CDIWizardBaseExt {
	
	public CDIWizardBase qualifier(String pkg, String name,
			boolean inherited, boolean comments) {
		return create(CDIWizardType.QUALIFIER, pkg, name, inherited, comments);
	}

	public CDIWizardBase scope(String pkg, String name, boolean inherited,
			boolean comments, boolean normalScope, boolean passivating) {
		CDIWizardBase w = create(CDIWizardType.SCOPE, pkg, name, inherited,
				comments);
		w = w.setNormalScope(normalScope);
		return normalScope ? w.setPassivating(passivating) : w;
	}

	public CDIWizardBase binding(String pkg, String name, String target,
			boolean inherited, boolean comments) {
		CDIWizardBase w = create(CDIWizardType.INTERCEPTOR_BINDING, pkg, name,
				inherited, comments);
		return target != null ? w.setTarget(target) : w;
	}

	public CDIWizardBase stereotype(String pkg, String name, String scope,
			String target, boolean inherited, boolean named,
			boolean alternative, boolean regInBeansXML, boolean comments) {
		CDIWizardBase w = create(CDIWizardType.STEREOTYPE, pkg, name, inherited,
				comments).setAlternative(alternative).setNamed(named);
		if (scope != null) {
			w = w.setScope(scope);
		}
		if (alternative && regInBeansXML) {
			w.setRegisterInBeansXml(regInBeansXML);
		}
		return target != null ? w.setTarget(target) : w;
	}

	public CDIWizardBase decorator(String pkg, String name, String intf,
			String fieldName, boolean isPublic, boolean isAbstract,
			boolean isFinal, boolean comments) {
		CDIWizardBase w = create(CDIWizardType.DECORATOR, pkg, name, comments);
		w = w.addInterface(intf).setPublic(isPublic).setFinal(isFinal)
				.setAbstract(isAbstract);
		return fieldName != null ? w.setFieldName(fieldName) : w;
	}

	public CDIWizardBase interceptor(String pkg, String name,
			String ibinding, String superclass, String method, boolean comments) {
		CDIWizardBase w = create(CDIWizardType.INTERCEPTOR, pkg, name, comments);
		if (superclass != null) {
			w = w.setSuperclass(superclass);
		}
		if (method != null) {
			w = w.setMethodName(method);
		}
		if (ibinding != null) {
			w = w.addIBinding(ibinding);
		}
		return w;
	}

	public CDIWizardBase bean(String pkg, String name, boolean isPublic,
			boolean isAbstract, boolean isFinal, boolean comments, 
			boolean alternative, boolean registerInBeansXML,
			String named, String interfaces, String scope, String qualifier) {
		CDIWizardBase w = create(CDIWizardType.BEAN, pkg, name, comments);
		if (named != null) {
			w.setNamed(true);
			if (!"".equals(named.trim())) {
				w.setNamedName(named);
			}
		}
		w = w.setPublic(isPublic).setFinal(isFinal).setAbstract(isAbstract).setAlternative(alternative);
		//w = w.setPublic(isPublic).setFinal(isFinal).setAbstract(isAbstract);
		if (interfaces != null && !"".equals(interfaces.trim())) {
			w.addInterface(interfaces);
		}
		if (scope != null && !"".equals(scope.trim())) {
			w.setScope(scope);
		}
		if (qualifier != null && !"".equals(qualifier.trim())) {
			w.addQualifier(qualifier);
		}
		if (alternative && registerInBeansXML) {
			w.setRegisterInBeansXml(registerInBeansXML);
		}
		return w;
	}

	public void annotation(SWTOpenExt open, SWTUtilExt util, String pkg,
			String name) {
		SWTBot openWizard = open.newObject(JavaAnnotation.LABEL);
		openWizard.textWithLabel("Name:").setText(name);
		openWizard.textWithLabel("Package:").setText(pkg);
		openWizard.button("Finish").click();
		util.waitForNonIgnoredJobs();
	}

	public CDIWizardBase annLiteral(String pkg, String name,
			boolean isPublic, boolean isAbstract, boolean isFinal,
			boolean comments, String qualifier) {
		assert qualifier != null && !"".equals(qualifier.trim()) : "Qualifier has to be set";
		CDIWizardBase w = create(CDIWizardType.ANNOTATION_LITERAL, pkg, name,
				comments);
		return w.setPublic(isPublic).setFinal(isFinal).setAbstract(isAbstract)
				.addQualifier(qualifier);
	}

	public CDIWizardBase beansXML(String pkg) {
		CDIWizardBase w = new NewCDIFileWizard(CDIWizardType.BEANS_XML).run();
		w.setSourceFolder(pkg);		
		return w;		
	}

	public CDIWizardBase create(CDIWizardType type, String pkg, String name,
			boolean inherited, boolean comments) {
		return create(type, pkg, name, comments).setInherited(inherited);
	}

	public CDIWizardBase create(CDIWizardType type, String pkg, String name,
			boolean comments) {
		CDIWizardBase p = new NewCDIFileWizard(type).run();
		return p.setPackage(pkg).setName(name).setGenerateComments(comments);
	}


}
