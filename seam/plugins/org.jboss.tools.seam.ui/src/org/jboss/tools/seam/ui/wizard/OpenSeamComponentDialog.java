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

package org.jboss.tools.seam.ui.wizard;

import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.SeamComponent;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.SeamUiImages;
import org.jboss.tools.seam.ui.views.SeamReferencedFilter;

/**
 * @author Daniel Azarov
 * 
 */
public class OpenSeamComponentDialog extends FilteredItemsSelectionDialog {

	public OpenSeamComponentDialog(Shell shell) {
		super(shell);
		setListLabelProvider(new SeamComponentLabelProvider());
		setDetailsLabelProvider(new SeamComponentLabelProvider());
		setSelectionHistory(new SeamComponentSelectionHistory());
	}
	
	protected void computeResult() {
		super.computeResult();
	}

	protected Control createExtendedContentArea(Composite parent) {
		return null;
	}

	protected ItemsFilter createFilter() {
		return new SeamComponentFilter();
	}

	protected void fillContentProvider(AbstractContentProvider contentProvider,
			ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
			throws CoreException {
		
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
		progressMonitor.beginTask(SeamUIMessages.OPEN_SEAM_COMPONENT_DIALOG_LOADING, projects.length);
		
		for(int i = 0; i < projects.length; i++){
			IProject project = projects[i];
			progressMonitor.subTask(project.getName());
			progressMonitor.worked(1);
			ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
			if(seamProject != null){
				Iterator<ISeamComponent> iter = seamProject.getComponents().iterator();
				while(iter.hasNext()){
					ISeamComponent component = iter.next();

					if(SeamReferencedFilter.isComponentDeclaredInThisProject(component))
						contentProvider.add(component, itemsFilter);
				}
			}
		}
		progressMonitor.done();
	}

	protected IDialogSettings getDialogSettings() {
		return new DialogSettings(SeamUIMessages.OPEN_SEAM_COMPONENT_DIALOG_NAME);
	}

	public String getElementName(Object item) {
		if(item instanceof ISeamComponent){
			ISeamComponent component = (ISeamComponent)item;
			return component.getName();
		}
		return null;
	}

	protected Comparator<SeamComponent> getItemsComparator() {
		return new SeamComponentComparator();
	}

	protected IStatus validateItem(Object item) {
		return new SeamComponentStatus();
	}
	
	public class SeamComponentComparator implements Comparator<SeamComponent>{
		public int compare(SeamComponent left, SeamComponent right) {
			return left.getName().compareTo(right.getName());
		}
		
	}
	
	public class SeamComponentFilter extends ItemsFilter{
		public boolean isConsistentItem(Object item) {
			return true;
		}

		public boolean matchItem(Object item) {
			if(item instanceof ISeamComponent){
				ISeamComponent component = (ISeamComponent)item;
				return patternMatcher.matches(component.getName());
			}
			return false;
		}
	}
	
	public class SeamComponentStatus implements IStatus{
		public IStatus[] getChildren() {
			return null;
		}

		public int getCode() {
			return 0;
		}

		public Throwable getException() {
			return null;
		}

		public String getMessage() {
			return ""; //$NON-NLS-1$
		}

		public String getPlugin() {
			return ""; //$NON-NLS-1$
		}

		public int getSeverity() {
			return 0;
		}

		public boolean isMultiStatus() {
			return false;
		}

		public boolean isOK() {
			return true;
		}

		public boolean matches(int severityMask) {
			return false;
		}
		
	}
	
	public class SeamComponentSelectionHistory extends SelectionHistory{
		protected Object restoreItemFromMemento(IMemento memento) {
			return null;
		}

		@Override
		protected void storeItemToMemento(Object item, IMemento memento) {
		}
		
	}
	
	public class SeamComponentLabelProvider implements ILabelProvider{

		public Image getImage(Object element) {
			return SeamUiImages.COMPONENT_IMAGE;
		}

		public String getText(Object element) {
			if(element instanceof ISeamComponent){
				ISeamComponent component = (ISeamComponent)element;
				return component.getName()+" - "+component.getSeamProject().getProject().getName(); //$NON-NLS-1$
			}
			return null;
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}
		
	}
}
