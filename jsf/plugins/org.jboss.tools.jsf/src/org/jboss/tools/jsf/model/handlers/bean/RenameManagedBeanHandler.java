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

import java.text.MessageFormat;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.undo.*;
import org.jboss.tools.common.reporting.ProblemReportingHelper;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.helpers.bean.ManagedBeanHelper;

public class RenameManagedBeanHandler extends AbstractHandler {
	boolean isLight = false;

	public boolean isEnabled(XModelObject object) {
		if(isLight) return false;
		if(object == null || !object.isObjectEditable()) return false;
		if(ManagedBeanHelper.getType(object) == null) return false;
		return true;
	}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		if (!isEnabled(object)) return;
		XUndoManager undo = object.getModel().getUndoManager();
		XTransactionUndo u = new XTransactionUndo(MessageFormat.format(JSFUIMessages.RenameManagedBeanHandler_Rename, DefaultCreateHandler.title(object, false)), XTransactionUndo.EDIT);
		undo.addUndoable(u);
		try {
			transaction(object, p);
		} catch (XModelException e) {
			undo.rollbackTransactionInProgress();
			throw e;
		} finally {
			u.commit();
		}
	}

	protected void transaction(XModelObject object, Properties p) throws XModelException {
		IType type = ManagedBeanHelper.getType(object);
		if(type != null && !type.isBinary()) {
			RenameManagedBeanClassRunnable r = new RenameManagedBeanClassRunnable(object, type);
			Display.getDefault().syncExec(r);
		}
	}
	
	class RenameManagedBeanClassRunnable implements Runnable {
		XModelObject object;
		IType type;
		
		public RenameManagedBeanClassRunnable(XModelObject object, IType type) {
			this.object = object;
			this.type = type;
		}

		public void run() {
			try {
				runInternal();
			} catch (CoreException e) {
				ProblemReportingHelper.reportProblem("org.jboss.tools.jsf", e); //$NON-NLS-1$
			}
		}
		
		public void runInternal() throws CoreException {
			RenameSupport renameSupport = RenameSupport.create(type, null, RenameSupport.UPDATE_REFERENCES);
			if (!renameSupport.preCheck().isOK()) return;			
			IElementChangedListener listener = new JavaElementChangedListener(object, type);
			JavaCore.addElementChangedListener(listener);
			try {			
				renameSupport.openDialog(ModelPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
			} finally {
				JavaCore.removeElementChangedListener(listener);
			}
		}
	}
	
	
	private void renameClass(XModelObject object, String qualifiedName) throws XModelException {		
		object.getModel().changeObjectAttribute(object, "managed-bean-class", qualifiedName); //$NON-NLS-1$
	}
	
	private class JavaElementChangedListener implements IElementChangedListener {
		private IType type;
		private XModelObject object;
		
		public JavaElementChangedListener(XModelObject object, IType type) {
			this.type = type; 
			this.object = object;
		}
		
		public void elementChanged(ElementChangedEvent event) {
			IJavaElementDelta delta = event.getDelta();
			IJavaElement fromElement = getMovedFromElement(delta);
			if (type.getCompilationUnit().equals(fromElement)) {
				try {
					IJavaElement toElement = getMovedToElement(delta);
					if (toElement instanceof ICompilationUnit) {
						String packageName = toElement.getParent().getElementName();
						String className = toElement.getElementName();
						if(className.endsWith(".java")) className = className.substring(0, className.length() - 5); //$NON-NLS-1$
						if(packageName.length() > 0) className = packageName + "." + className; //$NON-NLS-1$
						try {
							renameClass(object, className);
						} catch (XModelException e) {
							throw new RuntimeException(e);
						}
					}
				} finally {
					JavaCore.removeElementChangedListener(this);
				}
			}
		} 
		
		private IJavaElement getMovedFromElement(IJavaElementDelta delta) {
			IJavaElement element = delta.getMovedFromElement();
			if (element == null) {
				IJavaElementDelta deltas[] = delta.getAffectedChildren();
				for (int i = 0; i < deltas.length && element == null; i++)
					element = getMovedFromElement(deltas[i]);
			}			
			return element;			
		}

		private IJavaElement getMovedToElement(IJavaElementDelta delta) {
			IJavaElement element = delta.getMovedToElement();
			if (element == null) {
				IJavaElementDelta deltas[] = delta.getAffectedChildren();
				for (int i = 0; i < deltas.length && element == null; i++)
					element = getMovedToElement(deltas[i]);
			}			
			return element;			
		}
	}
}
