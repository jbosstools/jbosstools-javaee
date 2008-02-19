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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.SeamUiImages;
import org.jboss.tools.seam.ui.views.SeamReferencedFilter;

/**
 * @author Daniel Azarov
 * 
 */
public class OpenSeamComponentDialog extends FilteredItemsSelectionDialog {
	private static final String FILE_NAME = "OpenSeamComponentHistory.xml"; //$NON-NLS-1$
	private static final String PROJECT_NAME = "ProjectName"; //$NON-NLS-1$
	private static final String COMPONENT_NAME = "ComponentName"; //$NON-NLS-1$

	public OpenSeamComponentDialog(Shell shell) {
		super(shell);

		setSelectionHistory(new SeamComponentSelectionHistory());

		setListLabelProvider(new SeamComponentLabelProvider());
		setDetailsLabelProvider(new SeamComponentLabelProvider());

		XMLMemento memento = load();
		if (memento != null)
			getSelectionHistory().load(memento);
	}
	
	public void beginTest(){
		create();
		applyFilter();
	}
	
	public void endTest(){
		refresh();
		setResult(getSelectedItems().toList());
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
		
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();

		progressMonitor.beginTask(
				SeamUIMessages.OPEN_SEAM_COMPONENT_DIALOG_LOADING,
				projects.length);

		for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];
			progressMonitor.subTask(project.getName());

			ISeamProject seamProject = SeamCorePlugin.getSeamProject(project,
					true);
			if (seamProject != null) {
				Iterator<ISeamComponent> iter = seamProject.getComponents()
						.iterator();
				while (iter.hasNext()) {
					ISeamComponent component = iter.next();

					if (SeamReferencedFilter
							.isComponentDeclaredInThisProject(component))
						contentProvider.add(new SeamComponentWrapper(component
								.getName(), project.getName(), component),
								itemsFilter);
				}
			}
			progressMonitor.worked(1);
		}
		progressMonitor.done();
	}

	private XMLMemento load() {
		XMLMemento memento = null;
		IPath stateLocation = SeamGuiPlugin.getDefault().getStateLocation()
				.append(FILE_NAME);
		File file = new File(stateLocation.toOSString());
		if (file.exists()) {
			FileReader reader = null;
			try {
				reader = new FileReader(file);

				memento = XMLMemento.createReadRoot(reader);
				reader.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return memento;
	}

	protected IDialogSettings getDialogSettings() {
		return new DialogSettings(
				SeamUIMessages.OPEN_SEAM_COMPONENT_DIALOG_NAME);
	}

	public String getElementName(Object item) {
		if (item instanceof SeamComponentWrapper) {
			SeamComponentWrapper componentWrapper = (SeamComponentWrapper) item;
			return componentWrapper.getComponentName();
		}
		return null;
	}

	protected Comparator<SeamComponentWrapper> getItemsComparator() {
		return new SeamComponentComparator();
	}

	protected IStatus validateItem(Object item) {
		return new SeamComponentStatus();
	}

	public class SeamComponentComparator implements
			Comparator<SeamComponentWrapper> {
		public int compare(SeamComponentWrapper left, SeamComponentWrapper right) {
			return left.getComponentName().compareTo(right.getComponentName());
		}

	}

	public class SeamComponentFilter extends ItemsFilter {
		public boolean isConsistentItem(Object item) {
			return true;
		}

		public boolean matchItem(Object item) {
			if (item instanceof SeamComponentWrapper) {
				SeamComponentWrapper componentWrapper = (SeamComponentWrapper) item;
				return patternMatcher.matches(componentWrapper
						.getComponentName());
			}
			return false;
		}
	}

	public class SeamComponentStatus implements IStatus {
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
			if (getSelectedItems().size() < 0)
				return false;

			return true;
		}

		public boolean matches(int severityMask) {
			return false;
		}

	}

	public class SeamComponentSelectionHistory extends SelectionHistory {

		public SeamComponentSelectionHistory() {
			super();
		}

		protected Object restoreItemFromMemento(IMemento memento) {
			XMLMemento mem = (XMLMemento) memento;
			String projectName = mem.getString(PROJECT_NAME);
			if (projectName == null)
				return null;
			String componentName = mem.getString(COMPONENT_NAME);
			if (componentName == null)
				return null;
			return new SeamComponentWrapper(componentName, projectName);
		}

		@Override
		protected void storeItemToMemento(Object item, IMemento memento) {
			SeamComponentWrapper componentWrapper = (SeamComponentWrapper) item;
			XMLMemento mem = (XMLMemento) memento;
			mem.putString(PROJECT_NAME, componentWrapper.getProjectName());
			mem.putString(COMPONENT_NAME, componentWrapper.getComponentName());
		}

		public void save(IMemento memento) {
			super.save(memento);
			if (!(memento instanceof XMLMemento))
				return;
			XMLMemento xmlMemento = (XMLMemento) memento;
			IPath stateLocation = SeamGuiPlugin.getDefault().getStateLocation()
					.append(FILE_NAME);
			File file = new File(stateLocation.toOSString());
			FileWriter writer = null;
			try {
				writer = new FileWriter(file);

				xmlMemento.save(writer);
				writer.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public class SeamComponentLabelProvider implements ILabelProvider {

		public Image getImage(Object element) {
			return SeamUiImages.COMPONENT_IMAGE;
		}

		public String getText(Object element) {
			if (element instanceof SeamComponentWrapper) {
				SeamComponentWrapper componentWrapper = (SeamComponentWrapper) element;
				return componentWrapper.getComponentName()
						+ " - " + componentWrapper.getProjectName(); //$NON-NLS-1$
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

	public class SeamComponentWrapper {
		private String componentName;
		private String projectName;
		private ISeamComponent component;

		public SeamComponentWrapper(String componentName, String projectName) {
			this.componentName = componentName;
			this.projectName = projectName;
		}

		public SeamComponentWrapper(String componentName, String projectName,
				ISeamComponent component) {
			this(componentName, projectName);
			this.component = component;
		}

		public String getComponentName() {
			return componentName;
		}

		public void setComponentName(String componentName) {
			this.componentName = componentName;
		}

		public String getProjectName() {
			return projectName;
		}

		public void setProjectName(String projectName) {
			this.projectName = projectName;
		}

		public ISeamComponent getComponent() {
			if (component == null) {
				IProject project = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(projectName);
				if (project != null) {
					ISeamProject seamProject = SeamCorePlugin.getSeamProject(
							project, true);
					if (seamProject != null) {
						ISeamComponent component = seamProject
								.getComponent(componentName);
						return component;
					}
				}
				return null;
			}
			return component;
		}

		public void setComponent(ISeamComponent component) {
			this.component = component;
		}

	}
}
