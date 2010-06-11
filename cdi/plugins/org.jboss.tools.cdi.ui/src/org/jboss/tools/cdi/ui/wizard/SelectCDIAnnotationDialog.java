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
import org.eclipse.ui.dialogs.SearchPattern;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.ui.widget.editor.ListFieldEditor.ListFieldEditorProvider;
/**
 * @author Viacheslav Kabanovich
 * 
 */
public class SelectCDIAnnotationDialog extends FilteredItemsSelectionDialog {
	private static final String FILE_NAME = "SelectCDIAnnotationHistory.xml"; //$NON-NLS-1$
	private static final String ROOT_NODE = "historyRootNode"; //$NON-NLS-1$
	private static final String INFO_NODE = "infoNode"; //$NON-NLS-1$
	private static final String PROJECT_NAME = "ProjectName"; //$NON-NLS-1$
	private static final String COMPONENT_NAME = "ComponentName"; //$NON-NLS-1$
	private static final String DELETED = "Deleted"; //$NON-NLS-1$
	private static final String SEPARATOR = " - "; //$NON-NLS-1$
	private static final String YES = "yes"; //$NON-NLS-1$
	private static final String NO = "no"; //$NON-NLS-1$
	
	ListFieldEditorProvider<ICDIAnnotation> provider = null;

	public SelectCDIAnnotationDialog(Shell shell) {
		super(shell);

		setSelectionHistory(new SeamComponentSelectionHistory());

		setListLabelProvider(new CDIAnnotationLabelProvider());
		setDetailsLabelProvider(new CDIAnnotationLabelProvider());

		XMLMemento memento = loadMemento();
		if (memento != null)
			getSelectionHistory().load(memento);
	}

	public void setProvider(ListFieldEditorProvider<ICDIAnnotation> provider) {
		this.provider = provider;
	}

	public void startSearch() {
		applyFilter();
	}

	@Override
	public void okPressed() {
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
		return new CDIAnnotionFilter();
	}

	protected void fillContentProvider(AbstractContentProvider contentProvider,
			ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
			throws CoreException {
		
		progressMonitor.subTask("Wait");
		
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		} catch (InterruptedException e) {
			// do nothing
		}
		if(provider != null) {

			ICDIAnnotation[] as = provider.getSelectableObjects();
				for (ICDIAnnotation component: as) {
					IProject project = component.getCDIProject().getNature().getProject();
					contentProvider.add(new CDIAnnotationWrapper(
						component.getSourceType().getFullyQualifiedName(), project.getName(), component),
						itemsFilter);
				}
			progressMonitor.worked(1);
		}
		progressMonitor.done();
	}

	private static XMLMemento loadMemento() {
		XMLMemento memento = null;
		IPath stateLocation = CDIUIPlugin.getDefault().getStateLocation()
				.append(FILE_NAME);
		File file = new File(stateLocation.toOSString());
		if (file.exists()) {
			FileReader reader = null;
			try {
				reader = new FileReader(file);

				memento = XMLMemento.createReadRoot(reader);
			} catch (IOException ex) {
				CDIUIPlugin.getDefault().logError(ex);
			} catch (WorkbenchException ex) {
				CDIUIPlugin.getDefault().logError(ex);
			} finally {
				try {
					reader.close();
				} catch (IOException ex) {
					CDIUIPlugin.getDefault().logError(ex);
				}
			}
		}
		return memento;
	}

	private static void saveMemento(XMLMemento xmlMemento) {
		IPath stateLocation = CDIUIPlugin.getDefault().getStateLocation()
				.append(FILE_NAME);
		File file = new File(stateLocation.toOSString());
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);

			xmlMemento.save(writer);
		} catch (IOException ex) {
			CDIUIPlugin.getDefault().logError(ex);
		} finally {
			try {
				writer.close();
			} catch (IOException ex) {
				CDIUIPlugin.getDefault().logError(ex);
			}
		}
	}

	protected IDialogSettings getDialogSettings() {
		return new DialogSettings("OPEN_CDI_COMPONENT_DIALOG_NAME"); //TODO
	}

	public String getElementName(Object item) {
		if (item instanceof CDIAnnotationWrapper) {
			CDIAnnotationWrapper componentWrapper = (CDIAnnotationWrapper) item;
			return componentWrapper.getComponentName();
		}
		return null;
	}

	protected Comparator<CDIAnnotationWrapper> getItemsComparator() {
		return new CDIAnnotationComparator();
	}

	protected IStatus validateItem(Object item) {
		return new Status(getSelectedItems().size() >= 0?IStatus.OK:IStatus.ERROR,CDIUIPlugin.PLUGIN_ID,null);
	}

	public class CDIAnnotationComparator implements Comparator<CDIAnnotationWrapper> {
		public int compare(CDIAnnotationWrapper left, CDIAnnotationWrapper right) {
			return left.getComponentName().compareTo(right.getComponentName());
		}

	}

	public class CDIAnnotionFilter extends ItemsFilter {

		public CDIAnnotionFilter() {
			if(patternMatcher != null && patternMatcher.getPattern() == null || patternMatcher.getPattern().length() == 0) {
				patternMatcher.setPattern("*");
			}
		}

		public CDIAnnotionFilter(SearchPattern searchPattern) {
			super(searchPattern);
		}
		public boolean isConsistentItem(Object item) {
			return true;
		}

		public boolean matchItem(Object item) {
			if (item instanceof CDIAnnotationWrapper) {
				CDIAnnotationWrapper componentWrapper = (CDIAnnotationWrapper) item;

				String qName = componentWrapper.getComponentName();
				boolean result = patternMatcher.matches(qName);
				if (!result) {
					String pattern = patternMatcher.getPattern();
					if (pattern.indexOf(".") < 0) {
						int lastIndex = qName.lastIndexOf(".");
						if (lastIndex >= 0 && (lastIndex + 1) < qName.length())
							return patternMatcher.matches(qName.substring(lastIndex + 1));
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
			return new CDIAnnotationWrapper(componentName, projectName);
		}

		@Override
		protected void storeItemToMemento(Object item, IMemento memento) {
//			CDIAnnotationWrapper a = (CDIAnnotationWrapper) item;
//			XMLMemento mem = (XMLMemento) memento;
//			mem.putString(PROJECT_NAME, a.getProjectName());
//			mem.putString(COMPONENT_NAME, a.getComponentName());
		}

		public void save(IMemento memento) {
			super.save(memento);
			if (!(memento instanceof XMLMemento))
				return;
			XMLMemento xmlMemento = (XMLMemento) memento;
			saveMemento(xmlMemento);
		}
	}

	public class CDIAnnotationLabelProvider implements ILabelProvider {

		public Image getImage(Object element) {
			return null; //TODO
		}

		public String getText(Object element) {
			if (element instanceof CDIAnnotationWrapper) {
				CDIAnnotationWrapper a = (CDIAnnotationWrapper) element;
				return a.getComponentName() + SEPARATOR
						+ a.getProjectName();
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

	public class CDIAnnotationWrapper {
		private String componentName;
		private String projectName;
		private ICDIAnnotation component;

		public CDIAnnotationWrapper(String componentName, String projectName) {
			this.componentName = componentName;
			this.projectName = projectName;
		}

		public CDIAnnotationWrapper(String componentName, String projectName,
				ICDIAnnotation component) {
			this(componentName, projectName);
			this.component = component;
		}

		public String getComponentName() {
			return componentName;
		}

		public String getProjectName() {
			return projectName;
		}

		public ICDIAnnotation getComponent() {
			if (component == null) {
				IProject project = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(projectName);
				if (project != null) {
					ICDIProject cdiProject = CDICorePlugin.getCDIProject(project, true);
					if (cdiProject != null) {
						ICDIAnnotation component = 
							cdiProject.getInterceptorBinding(componentName);
						if(component == null) cdiProject.getStereotype(componentName);
						//TODO
						return component;
					}
				}
				return null;
			}
			return component;
		}

		public boolean equals(Object o) {
			if(o instanceof CDIAnnotationWrapper) {
				CDIAnnotationWrapper other = (CDIAnnotationWrapper)o;
				return componentName.equals(other.componentName) && projectName.equals(other.projectName);
			}			
			return false;
		}

	}

}
