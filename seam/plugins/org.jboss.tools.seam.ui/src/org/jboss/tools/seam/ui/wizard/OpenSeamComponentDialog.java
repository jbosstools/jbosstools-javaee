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
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
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
	private static final String ROOT_NODE = "historyRootNode"; //$NON-NLS-1$
	private static final String INFO_NODE = "infoNode"; //$NON-NLS-1$
	private static final String PROJECT_NAME = "ProjectName"; //$NON-NLS-1$
	private static final String COMPONENT_NAME = "ComponentName"; //$NON-NLS-1$
	private static final String DELETED = "Deleted"; //$NON-NLS-1$
	private static final String SEPARATOR = " - "; //$NON-NLS-1$
	private static final String YES = "yes"; //$NON-NLS-1$
	private static final String NO = "no"; //$NON-NLS-1$

	public OpenSeamComponentDialog(Shell shell) {
		super(shell);

		setSelectionHistory(new SeamComponentSelectionHistory());

		setListLabelProvider(new SeamComponentLabelProvider());
		setDetailsLabelProvider(new SeamComponentLabelProvider());

		XMLMemento memento = loadMemento();
		if (memento != null)
			getSelectionHistory().load(memento);
	}

	public void startSearch() {
		applyFilter();
	}

	@Override
	public void okPressed() {
		// TODO Auto-generated method stub
		super.okPressed();
	}
	
	public void stopSearchAndShowResults() {
		refresh();
		List list = getSelectedItems().toList();
		Collections.sort(list,getItemsComparator());
		setResult(list);
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
		
		progressMonitor.subTask(SeamUIMessages.OPEN_SEAM_COMPONENT_DIALOG_WAIT);
		
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		} catch (InterruptedException e) {
			// do nothing
		}

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
				ISeamComponent[] iter = seamProject.getComponents();
				for (ISeamComponent component: iter) {
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

	private static XMLMemento loadMemento() {
		XMLMemento memento = null;
		IPath stateLocation = SeamGuiPlugin.getDefault().getStateLocation()
				.append(FILE_NAME);
		File file = new File(stateLocation.toOSString());
		if (file.exists()) {
			FileReader reader = null;
			try {
				reader = new FileReader(file);

				memento = XMLMemento.createReadRoot(reader);
			} catch (IOException ex) {
				SeamCorePlugin.getPluginLog().logError(ex);
			} catch (WorkbenchException ex) {
				SeamCorePlugin.getPluginLog().logError(ex);
			} finally {
				try {
					reader.close();
				} catch (IOException ex) {
					SeamCorePlugin.getPluginLog().logError(ex);
				}
			}
		}
		return memento;
	}

	private static void saveMemento(XMLMemento xmlMemento) {
		IPath stateLocation = SeamGuiPlugin.getDefault().getStateLocation()
				.append(FILE_NAME);
		File file = new File(stateLocation.toOSString());
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);

			xmlMemento.save(writer);
		} catch (IOException ex) {
			SeamCorePlugin.getPluginLog().logError(ex);
		} finally {
			try {
				writer.close();
			} catch (IOException ex) {
				SeamCorePlugin.getPluginLog().logError(ex);
			}
		}
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
		return new Status(getSelectedItems().size() >= 0?IStatus.OK:IStatus.ERROR,SeamGuiPlugin.PLUGIN_ID,null);
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

				boolean result = patternMatcher.matches(componentWrapper
						.getComponentName());
				if (!result) {
					String pattern = patternMatcher.getPattern();
					if (pattern.indexOf(".") < 0) {
						int lastIndex = componentWrapper.getComponentName()
								.lastIndexOf(".");
						if (lastIndex >= 0
								&& (lastIndex + 1) < componentWrapper
										.getComponentName().length())
							return patternMatcher.matches(componentWrapper
									.getComponentName()
									.substring(lastIndex + 1));
					}
				}
				return result;
			}
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
			String componentDeleted = mem.getString(DELETED);
			if (componentDeleted != null && YES.equals(componentDeleted))
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
			saveMemento(xmlMemento);
		}
	}

	public class SeamComponentLabelProvider implements ILabelProvider {

		public Image getImage(Object element) {
			return SeamUiImages.COMPONENT_IMAGE;
		}

		public String getText(Object element) {
			if (element instanceof SeamComponentWrapper) {
				SeamComponentWrapper componentWrapper = (SeamComponentWrapper) element;
				return componentWrapper.getComponentName() + SEPARATOR
						+ componentWrapper.getProjectName(); //$NON-NLS-1$
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

		public String getProjectName() {
			return projectName;
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

	}

	public static void validateHistory(ISeamProject seamProject) {
		String seamProjectName = seamProject.getProject().getName();

		XMLMemento memento = loadMemento();
		if (memento != null) {
			XMLMemento historyMemento = (XMLMemento) memento
					.getChild(ROOT_NODE);

			if (historyMemento == null) {
				return;
			}

			IMemento[] mementoElements = historyMemento.getChildren(INFO_NODE);
			for (int i = 0; i < mementoElements.length; ++i) {
				IMemento mem = mementoElements[i];
				String projectName = mem.getString(PROJECT_NAME);
				if (projectName == null) {
					mem.putString(DELETED, YES);
					continue;
				}
				if (projectName.equals(seamProjectName)) {
					String componentName = mem.getString(COMPONENT_NAME);
					if (componentName == null) {
						mem.putString(DELETED, YES);
						continue;
					}
					IProject project = ResourcesPlugin.getWorkspace().getRoot()
							.getProject(projectName);
					if (project != null) {
						ISeamProject cSeamProject = SeamCorePlugin
								.getSeamProject(project, true);
						if (cSeamProject != null) {
							ISeamComponent component = cSeamProject
									.getComponent(componentName);
							if (component == null)
								mem.putString(DELETED, YES);
							else
								mem.putString(DELETED, NO);
						} else
							mem.putString(DELETED, YES);
					} else
						mem.putString(DELETED, YES);
				}
			}
			saveMemento(memento);
		}
	}
}
