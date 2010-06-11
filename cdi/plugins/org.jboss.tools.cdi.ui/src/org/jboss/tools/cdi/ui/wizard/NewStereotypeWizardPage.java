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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.common.ui.widget.editor.ITaggedFieldEditor;
import org.jboss.tools.common.ui.widget.editor.ListFieldEditor;

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
	protected StereotypesSelectionProvider stereotypesProvider = new StereotypesSelectionProvider();
	protected InterceptorBindingSelectionProvider interceptorBindingsProvider = new InterceptorBindingSelectionProvider();
	protected ListFieldEditor stereotypes = null;
	protected ListFieldEditor interceptorBindings = null;
	

	public NewStereotypeWizardPage() {
		setTitle(CDIUIMessages.NEW_STEREOTYPE_WIZARD_PAGE_NAME);
	}

	protected void addAnnotations(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		addStereotypeAnnotation(imports, sb, lineDelimiter);
		addInheritedAnnotation(imports, sb, lineDelimiter);
		addAlternativeAnnotation(imports, sb, lineDelimiter);
		addScopeAnnotation(imports, sb, lineDelimiter);
		addNamedAnnotation(imports, sb, lineDelimiter);
		addInterceptorBindingAnnotations(imports, sb, lineDelimiter);
		addSuperStereotypeAnnotations(imports, sb, lineDelimiter);
		addTargetAnnotation(imports, sb, lineDelimiter, getTargets());
		addRetentionAnnotation(imports, sb, lineDelimiter);
		addDocumentedAnnotation(imports, sb, lineDelimiter);
	}

	protected void addStereotypeAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		addAnnotation(CDIConstants.STEREOTYPE_ANNOTATION_TYPE_NAME, imports, sb, lineDelimiter);
	}

	protected void addAlternativeAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		if(alternative != null && alternative.composite.getValue() == Boolean.TRUE) {
			addAnnotation(CDIConstants.ALTERNATIVE_ANNOTATION_TYPE_NAME, imports, sb, lineDelimiter);
		}
	}

	protected void addNamedAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		if(named != null && named.composite.getValue() == Boolean.TRUE) {
			addAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME, imports, sb, lineDelimiter);
		}
	}

	protected void addScopeAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		if(scope != null && scope.getValue() != null && scope.getValue().toString().length() > 0) {
			String scopeName = scope.getValue().toString();
			String qScopeName = scopes.get(scopeName);
			addAnnotation(qScopeName, imports, sb, lineDelimiter);
		}
	}

	@SuppressWarnings("unchecked")
	protected void addSuperStereotypeAnnotations(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		if(stereotypes != null) {
			List list = (List)stereotypes.getValue();
			for (Object o: list) {
				if(o instanceof ICDIAnnotation) {
					ICDIAnnotation a = (ICDIAnnotation)o;
					String typeName = a.getSourceType().getFullyQualifiedName();
					addAnnotation(typeName, imports, sb, lineDelimiter);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void addInterceptorBindingAnnotations(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		if(interceptorBindings != null) {
			List list = (List)interceptorBindings.getValue();
			for (Object o: list) {
				if(o instanceof ICDIAnnotation) {
					ICDIAnnotation a = (ICDIAnnotation)o;
					String typeName = a.getSourceType().getFullyQualifiedName();
					addAnnotation(typeName, imports, sb, lineDelimiter);
				}
			}
		}
	}

	@Override
	protected void createCustomFields(Composite composite) {
		createInheritedField(composite, false);
		createAlternativeField(composite);
		createNamedField(composite);
		createScopeField(composite);
		createTargetField(composite);
		createInterceptorBindingField(composite);
		createStereotypeField(composite);
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
		scope = createComboField("scope", CDIUIMessages.FIELD_EDITOR_SCOPE_LABEL, composite, vs);
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

	protected void createInterceptorBindingField(Composite composite) {
		interceptorBindings = new ListFieldEditor("interceptorBindings", CDIUIMessages.FIELD_EDITOR_INTERCEPTOR_BINDINGS_LABEL, new ArrayList<Object>());
		interceptorBindings.setProvider(interceptorBindingsProvider);
		interceptorBindingsProvider.setEditorField(interceptorBindings);
		interceptorBindings.doFillIntoGrid(composite);
		setInterceptorBindings(getPackageFragmentRoot());
		interceptorBindings.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Object o = evt.getNewValue();
				if(o instanceof List && !((List)o).isEmpty()) {
					target.setValue("TYPE");
					target.setEnabled(false);
				} else {
					target.setEnabled(true);
				}
			}});
	}

	protected void createStereotypeField(Composite composite) {
		stereotypes = new ListFieldEditor("stereotypes", CDIUIMessages.FIELD_EDITOR_STEREOTYPES_LABEL, new ArrayList<Object>());
		stereotypes.setProvider(stereotypesProvider);
		stereotypesProvider.setEditorField(stereotypes);
		stereotypes.doFillIntoGrid(composite);
		setStereotypes(getPackageFragmentRoot());
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

	void setInterceptorBindings(IPackageFragmentRoot root) {
		interceptorBindingsProvider.setProject(null);
		if(root != null) {
			IJavaProject jp = root.getJavaProject();
			ICDIProject cdi = CDICorePlugin.getCDIProject(jp.getProject(), true);
			if(cdi != null) {
				interceptorBindingsProvider.setProject(cdi);
			}
		}
	}
	
	void setStereotypes(IPackageFragmentRoot root) {
		stereotypesProvider.setProject(null);
		if(root != null) {
			IJavaProject jp = root.getJavaProject();
			ICDIProject cdi = CDICorePlugin.getCDIProject(jp.getProject(), true);
			if(cdi != null) stereotypesProvider.setProject(cdi);
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
