/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.common.ui.widget.editor.ITaggedFieldEditor;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewStereotypeWizardPage extends NewCDIAnnotationWizardPage {
	protected CheckBoxEditorWrapper alternative = null;
	protected CheckBoxEditorWrapper named = null;
	protected ITaggedFieldEditor scope = null;
	protected Map<String, String> scopes = new TreeMap<String, String>();

	public NewStereotypeWizardPage() {
		setTitle(CDIUIMessages.NEW_STEREOTYPE_WIZARD_PAGE_NAME);
	}

	protected void addAnnotations(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		addInheritedAnnotation(imports, sb, lineDelimiter);
		addAlternativeAnnotation(imports, sb, lineDelimiter);
		addScopeAnnotation(imports, sb, lineDelimiter);
		addNamedAnnotation(imports, sb, lineDelimiter);
		addStereotypeAnnotation(imports, sb, lineDelimiter);
		addTargetAnnotation(imports, sb, lineDelimiter, getTargets());
		addRetentionAnnotation(imports, sb, lineDelimiter);
		addDocumentedAnnotation(imports, sb, lineDelimiter);
	}

	protected void addStereotypeAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		imports.addImport("javax.enterprise.inject.Stereotype");		
		sb.append("@Stereotype").append(lineDelimiter);
	}

	protected void addAlternativeAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		if(alternative != null && alternative.composite.getValue() == Boolean.TRUE) {
			imports.addImport(CDIConstants.ALTERNATIVE_ANNOTATION_TYPE_NAME);
			sb.append("@Alternative").append(lineDelimiter);
		}
	}

	protected void addNamedAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		if(named != null && named.composite.getValue() == Boolean.TRUE) {
			imports.addImport(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
			sb.append("@Named").append(lineDelimiter);
		}
	}

	protected void addScopeAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		if(scope != null && scope.getValue() != null && scope.getValue().toString().length() > 0) {
			String scopeName = scope.getValue().toString();
			String qScopeName = scopes.get(scopeName);
			imports.addImport(qScopeName);
			sb.append(scopeName).append(lineDelimiter);
		}
	}

	@Override
	protected void createCustomFields(Composite composite) {
		createInheritedField(composite, false);
		createAlternativeField(composite);
		createNamedField(composite);
		createScopeField(composite);
		createTargetField(composite);
	}

	protected void createAlternativeField(Composite composite) {
		String label = "Add @Alternative";
		alternative = createCheckBoxField(composite, "isAlternative", label, false);
	}

	protected void createNamedField(Composite composite) {
		String label = "Add @Named";
		named = createCheckBoxField(composite, "isNamed", label, false);
	}

	protected void createScopeField(Composite composite) {
		List<String> vs = new ArrayList<String>();
		vs.add("");
		scope = createComboField("Scope", "Scope", composite, vs);
		setScopes(getPackageFragmentRoot());
	}

	protected void createTargetField(Composite composite) {
		List<String> targetOptions = new ArrayList<String>();
		targetOptions.add("TYPE,METHOD,FIELD");
		targetOptions.add("METHOD,FIELD");
		targetOptions.add("TYPE");
		targetOptions.add("METHOD");
		targetOptions.add("FIELD");
		createTargetField(composite, targetOptions);
	}

	public void setPackageFragmentRoot(IPackageFragmentRoot root, boolean canBeModified) {
		super.setPackageFragmentRoot(root, canBeModified);
		setScopes(root);
	}

	void setScopes(IPackageFragmentRoot root) {
		if(root != null) {
			IJavaProject jp = root.getJavaProject();
			ICDIProject cdi = CDICorePlugin.getCDIProject(jp.getProject(), true);
			if(cdi != null) {
				Set<String> scopes = cdi.getScopeNames();
				String[] tags = scopes.toArray(new String[0]);
				setScopes(tags);
			} else {
				setScopes(new String[]{""});
			}
		} else {
			setScopes(new String[]{""});
		}
	}
	
	void setScopes(String[] tags) {
		scopes.clear();
		scopes.put("", "");
		for (String tag: tags) {
			if(tag.length() == 0) continue;
			int i = tag.lastIndexOf('.');
			String name = "@" + tag.substring(i + 1);
			scopes.put(name, tag);
		}
		if(scope != null) {
			scope.setTags(scopes.keySet().toArray(new String[0]));
			scope.setValue("");
		}
	}

	public void setNamed(boolean b) {
		if(named != null) named.composite.setValue(b);
	}

}
