/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.ui.wizard;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.util.BeanPresentationUtil;
import org.jboss.tools.cdi.internal.core.event.CDIProjectChangeEvent;
import org.jboss.tools.cdi.internal.core.event.ICDIProjectChangeListener;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;

/**
 * Open CDI Named Bean Dialog
 * 
 * @author Victor V. Rubezhny
 */
public class OpenCDINamedBeanDialog extends FilteredItemsSelectionDialog {

	private static final String FILE_NAME = "OpenCDINamedBeanHistory.xml"; //$NON-NLS-1$
	private static final String ROOT_NODE = "historyRootNode"; //$NON-NLS-1$
	private static final String INFO_NODE = "infoNode"; //$NON-NLS-1$
	private static final String PROJECT_NAME = "ProjectName"; //$NON-NLS-1$
	private static final String BEAN_NAME = "BeanName"; //$NON-NLS-1$
	private static final String DELETED = "Deleted"; //$NON-NLS-1$
	private static final String YES = "yes"; //$NON-NLS-1$
	private static final String NO = "no"; //$NON-NLS-1$

	public OpenCDINamedBeanDialog(Shell shell) {
		super(shell);

		setSelectionHistory(new CDINamedBeanSelectionHistory());

		setListLabelProvider(new CDINamedBeanLabelProvider());
		setDetailsLabelProvider(new CDINamedBeanLabelProvider());
	}

	private final static ICDIProjectChangeListener cdiProjectListener =
			new ICDIProjectChangeListener() {
				public void projectChanged(CDIProjectChangeEvent event) {
					validateHistory(event.getProject());
				}
			};
			

	@Override
	public boolean close() {
		CDICorePlugin.removeCDIProjectListener(cdiProjectListener);
		return super.close();
	}

	@Override
	public int open() {
		XMLMemento memento = loadMemento();
		if (memento != null) {
			getSelectionHistory().load(memento);
			updateHistory(memento);
			saveMemento(memento);
		}
		CDICorePlugin.addCDIProjectListener(cdiProjectListener);
		return super.open();
	}

	private static void updateHistory(XMLMemento memento) {
		XMLMemento historyMemento = (XMLMemento) memento
				.getChild(ROOT_NODE);

		if (historyMemento == null)
			return;

		IMemento[] mementoElements = historyMemento.getChildren(INFO_NODE);
		for (int i = 0; i < mementoElements.length; ++i) {
			IMemento mem = mementoElements[i];
			String projectName = mem.getString(PROJECT_NAME);
			if (projectName == null) {
				mem.putString(DELETED, YES);
				continue;
			}
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			ICDIProject cdiProject = CDICorePlugin.getCDIProject(project, true);
			if (cdiProject == null) {
				mem.putString(DELETED, YES);
				continue;
			}
			validateHistory(cdiProject, memento);
		}
	}


	public void startSearch() {
		applyFilter();
	}

	@Override
	public void okPressed() {
		// TODO Auto-generated method stub
		super.okPressed();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
		return new CDINamedBeanFilter();
	}

	protected void fillContentProvider(AbstractContentProvider contentProvider,
			ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
			throws CoreException {
		
		progressMonitor.subTask(CDIUIMessages.OPEN_CDI_NAMED_BEAN_DIALOG_WAIT);
		
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		} catch (InterruptedException e) {
			// do nothing
		}

		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();

		progressMonitor.beginTask(
				CDIUIMessages.OPEN_CDI_NAMED_BEAN_DIALOG_LOADING,
				projects.length);

		for (IProject project : projects) {
			progressMonitor.subTask(project.getName());

			ICDIProject cdiProject = CDICorePlugin.getCDIProject(project, true);
			if (cdiProject != null) {
				Set<IBean> iter = cdiProject.getNamedBeans(false);
				for (IBean bean: iter) {
					if (cdiProject == bean.getDeclaringProject()) {
						contentProvider.add(new CDINamedBeanWrapper(bean
								.getName(), project.getName(), bean),
								itemsFilter);
					}
				}
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
				CDICorePlugin.getDefault().logError(ex);
			} catch (WorkbenchException ex) {
				CDICorePlugin.getDefault().logError(ex);
			} finally {
				try {
					reader.close();
				} catch (IOException ex) {
					CDICorePlugin.getDefault().logError(ex);
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
			CDICorePlugin.getDefault().logError(ex);
		} finally {
			try {
				writer.close();
			} catch (IOException ex) {
				CDICorePlugin.getDefault().logError(ex);
			}
		}
	}

	protected IDialogSettings getDialogSettings() {
		return new DialogSettings(
				CDIUIMessages.OPEN_CDI_NAMED_BEAN_DIALOG_NAME);
	}

	public String getElementName(Object item) {
		if (item instanceof CDINamedBeanWrapper) {
			CDINamedBeanWrapper beanWrapper = (CDINamedBeanWrapper) item;
			return beanWrapper.getBeanName();
		}
		return null;
	}

	protected Comparator<CDINamedBeanWrapper> getItemsComparator() {
		return new CDINamedBeanComparator();
	}

	protected IStatus validateItem(Object item) {
		return new Status(getSelectedItems().size() >= 0?IStatus.OK:IStatus.ERROR,CDIUIPlugin.PLUGIN_ID,null);
	}

	public class CDINamedBeanComparator implements
			Comparator<CDINamedBeanWrapper> {
		public int compare(CDINamedBeanWrapper left, CDINamedBeanWrapper right) {
			return left.getBeanName().compareTo(right.getBeanName());
		}

	}

	public class CDINamedBeanFilter extends ItemsFilter {
		public boolean isConsistentItem(Object item) {
			if (item instanceof CDINamedBeanWrapper) {
				CDINamedBeanWrapper beanWrapper = (CDINamedBeanWrapper) item;
				String projectName = beanWrapper.getProjectName();
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
				ICDIProject cdiProject = CDICorePlugin.getCDIProject(project, true);
				if (cdiProject == null) {
					return false;
				}

				String beanName = beanWrapper.getBeanName();
				Set<IBean> beans = cdiProject
						.getBeans(beanName, true);
				IBean bean = (beans == null || beans.isEmpty() ? null : beans.iterator().next());
				return (bean != null);
			}
			return false;
		}

		public boolean matchItem(Object item) {
			if (item instanceof CDINamedBeanWrapper) {
				CDINamedBeanWrapper beanWrapper = (CDINamedBeanWrapper) item;

				boolean result = patternMatcher.matches(beanWrapper
						.getBeanName());
				if (!result) {
					String pattern = patternMatcher.getPattern();
					if (pattern.indexOf(".") < 0) {
						int lastIndex = beanWrapper.getBeanName()
								.lastIndexOf(".");
						if (lastIndex >= 0
								&& (lastIndex + 1) < beanWrapper
										.getBeanName().length())
							return patternMatcher.matches(beanWrapper
									.getBeanName()
									.substring(lastIndex + 1));
					}
				}
				return result;
			}
			return false;
		}
	}

	public class CDINamedBeanSelectionHistory extends SelectionHistory {

		public CDINamedBeanSelectionHistory() {
			super();
		}

		protected Object restoreItemFromMemento(IMemento memento) {
			XMLMemento mem = (XMLMemento) memento;
			String projectName = mem.getString(PROJECT_NAME);
			if (projectName == null)
				return null;
			String beanName = mem.getString(BEAN_NAME);
			if (beanName == null)
				return null;
			String beanDeleted = mem.getString(DELETED);
			if (beanDeleted != null && YES.equals(beanDeleted))
				return null;

			return new CDINamedBeanWrapper(beanName, projectName);
		}

		@Override
		protected void storeItemToMemento(Object item, IMemento memento) {
			CDINamedBeanWrapper beanWrapper = (CDINamedBeanWrapper) item;
			XMLMemento mem = (XMLMemento) memento;
			mem.putString(PROJECT_NAME, beanWrapper.getProjectName());
			mem.putString(BEAN_NAME, beanWrapper.getBeanName());
		}

		public void save(IMemento memento) {
			super.save(memento);
			if (!(memento instanceof XMLMemento))
				return;
			XMLMemento xmlMemento = (XMLMemento) memento;
			saveMemento(xmlMemento);
		}
	}

	private static class CDIBeanStyler extends Styler {
		private final Color foreground;

		public CDIBeanStyler(Color foreground) {
			this.foreground = foreground;
		}

		public void applyStyles(TextStyle textStyle) {
			if (foreground != null) {
				textStyle.foreground = foreground;
			}
		}
	}

	public class CDINamedBeanLabelProvider implements IStyledLabelProvider, ILabelProvider {
		final Color gray = new Color(null, 128, 128, 128);
		final Color black = new Color(null, 0, 0, 0);

		final Styler NAME_STYLE = new CDIBeanStyler(black);
		final Styler QUALIFIED_NAME_STYLE = new CDIBeanStyler(gray);
		final Styler BEAN_PATH_STYLE = new CDIBeanStyler(gray);

		public Image getImage(Object element) {
			if (element instanceof CDINamedBeanWrapper) {
				return CDIImages.getImageByElement(((CDINamedBeanWrapper)element).getBean());
			}
			return null;
		}
		
		public String getText(Object element) {
			return getStyledText(element).getString();
		}

		public StyledString getStyledText(Object element) {
			StyledString styledString = new StyledString();			
			if (element instanceof CDINamedBeanWrapper) {
				CDINamedBeanWrapper beanWrapper = (CDINamedBeanWrapper) element;
				//1. bean name
				styledString.append(beanWrapper.getBeanName(), NAME_STYLE);
				//2. bean location
				IBean b = beanWrapper.getBean();
				if (b != null) {
					String beanLocation = BeanPresentationUtil.getBeanLocation(b, true);
					styledString.append(beanLocation, BEAN_PATH_STYLE);
				}
			}
			return styledString;
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
	
	public class CDINamedBeanWrapper {
		private String beanName;
		private String projectName;
		private IBean bean;

		public CDINamedBeanWrapper(String beanName, String projectName) {
			this.beanName = beanName;
			this.projectName = projectName;
		}

		public CDINamedBeanWrapper(String beanName, String projectName,
				IBean bean) {
			this(beanName, projectName);
			this.bean = bean;
		}

		public String getBeanName() {
			return beanName;
		}

		public String getProjectName() {
			return projectName;
		}

		public IBean getBean() {
			if (bean == null) {
				IProject project = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(projectName);
				if (project.isAccessible()) {
					ICDIProject cdiProject = CDICorePlugin.getCDIProject(
							project, true);
					if (cdiProject != null) {
						Set<IBean> beans = cdiProject
								.getBeans(beanName, true);
						if (!beans.isEmpty())
							return beans.iterator().next();
					}
				}
				return null;
			}
			return bean;
		}
	
		public boolean equals(Object obj) {
			if(!(obj instanceof CDINamedBeanWrapper)) {
				return false;
			}
			String s1 = new CDINamedBeanLabelProvider().getText(this);
			String s2 = new CDINamedBeanLabelProvider().getText(obj);
			return s1.equals(s2);
		}
	
		public int hashCode() {
			return new CDINamedBeanLabelProvider().getText(this).hashCode();
		}
	}
	
	public static void validateHistory(ICDIProject cdiProject) {
		XMLMemento memento = loadMemento();
		if (memento != null) {
			validateHistory(cdiProject, memento);
			saveMemento(memento);
		}
	}
	
	public static void validateHistory(ICDIProject cdiProject, XMLMemento memento) {
		String cdiProjectName = cdiProject.getNature().getProject().getName(); 
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
			if (projectName.equals(cdiProjectName)) {
				if (!cdiProject.getNature().getProject().exists()) {
					mem.putString(DELETED, YES);
					continue;
				}
				
				String beanName = mem.getString(BEAN_NAME);
				if (beanName == null) {
					mem.putString(DELETED, YES);
					continue;
				}
				Set<IBean> beans = cdiProject
						.getBeans(beanName, true);
				IBean bean = (beans == null || beans.isEmpty() ? null : beans.iterator().next()); 
				if (bean == null)
					mem.putString(DELETED, YES);
				else
					mem.putString(DELETED, NO);
			}
		}
	}
}
