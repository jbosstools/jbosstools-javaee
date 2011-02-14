/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.model.handlers.bean;

import java.util.ArrayList;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.ltk.core.refactoring.*;
import org.eclipse.ltk.core.refactoring.participants.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.common.model.refactoring.RenameModelObjectChange;
import org.jboss.tools.common.model.refactoring.RenameProcessorRunner;
import org.jboss.tools.common.util.BeanUtil;

public class JSFRenameFieldParticipant extends RenameParticipant implements ISharableParticipant {
	public static final String PARTICIPANT_NAME="jsf-RenameFieldParticipant"; //$NON-NLS-1$
	private IField field;
	private IMethod method;
	private XModelObject object;

	public JSFRenameFieldParticipant() {}

	protected boolean initialize(Object element) {
		if (element instanceof IField) {
			field = (IField)element;
		} else if(element instanceof XModelObject) {
			this.object = (XModelObject)element;
		} else if(element instanceof IMethod) {
			method = (IMethod)element;
		}
		return field != null || object != null || method != null;
	}

	public void addElement(Object element, RefactoringArguments arguments) {
		// TODO Auto-generated method stub
		
	}

	public String getName() {
		return PARTICIPANT_NAME;
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return null;
	}
	
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		if (!pm.isCanceled()) {
			if(!updateReferences() && object == null) return null;
			String newName = getArguments().getNewName();
			if (field != null) {
				JSFRenameFieldChange c2 = new JSFRenameFieldChange(field, newName);
				XModel model = c2.getModel();
				if(c2.getChildren() == null || c2.getChildren().length == 0 || !updateReferences()) c2 = null;

				XModelObject[] os = JSFRenameFieldHelper.getBeanList(model, field);
				os = getProperties(os, field.getElementName());
				RenameModelObjectChange c1 = RenameModelObjectChange.createChange(os, newName, "property-name"); //$NON-NLS-1$
				
					c2 = null;

				if(c1 == null) return c2;
				if(c2 == null) return c1;
				CompositeChange change = new CompositeChange(JSFUIMessages.REFERENCES);
				if(c1 != null) change.add(c1);
				if(c2 != null) change.add(c2);
				return change;
			} else if(method != null) {
				XModel model = JSFRenameFieldChange.getModel(method);
				if(model == null) return null;
				XModelObject[] os = JSFRenameFieldHelper.getBeanList(model, method);
				String propertyName = BeanUtil.getPropertyName(method.getElementName());
				if(propertyName == null) return null;
				String newPropertyName = BeanUtil.getPropertyName(newName);
				os = getProperties(os, propertyName);
				RenameModelObjectChange c1 = RenameModelObjectChange.createChange(os, newPropertyName, "property-name"); //$NON-NLS-1$
				return c1;
			} else if(object != null) {
				RenameModelObjectChange c1 = RenameModelObjectChange.createChange(new XModelObject[]{object}, getArguments().getNewName(), "property-name"); //$NON-NLS-1$
				
				JSFRenameFieldChange c2 = null;
				if(updateReferences()) {
					c2 = new JSFRenameFieldChange(object, getArguments().getNewName());
					if(c2.getChildren() == null || c2.getChildren().length == 0) c2 = null;
				}
				
				if(c1 == null) return c2;
				if(c2 == null) return c1;
				CompositeChange change = new CompositeChange(JSFUIMessages.REFERENCES);
				change.add(c1);
				change.add(c2);
				return change;
			}
		}
		return null;
	}
	
	private XModelObject[] getProperties(XModelObject[] beans, String name) {
		if(beans == null) return null;
		ArrayList<XModelObject> list = new ArrayList<XModelObject>();
		for (int i = 0; i < beans.length; i++) {
			XModelObject o = beans[i].getChildByPath(name);
			if(o != null) list.add(o);
		}
		return list.toArray(new XModelObject[0]);
	}
	
	protected boolean updateReferences() {
		return RenameProcessorRunner.updateReferences(getProcessor());
	}

}
