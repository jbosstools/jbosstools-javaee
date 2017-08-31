/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.project.facet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.cdi.core.CDICoreMessages;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.CDIVersion;

/**
 * @author Alexey Kazakov
 */
public class CDIFacetInstallDelegate implements ILogListener, IDelegate,
		ICDIFacetDataModelProperties {

	public static IProjectFacet CDI_FACET = ProjectFacetsManager.getProjectFacet(ICDIFacetDataModelProperties.CDI_FACET_ID);
	public static final IProjectFacetVersion CDI_10 = CDI_FACET.getVersion(CDIVersion.CDI_1_0.toString()); //$NON-NLS-1$
	public static final IProjectFacetVersion CDI_11 = CDI_FACET.getVersion(CDIVersion.CDI_1_1.toString()); //$NON-NLS-1$
	public static final IProjectFacetVersion CDI_12 = CDI_FACET.getVersion(CDIVersion.CDI_1_2.toString()); //$NON-NLS-1$
	public static final IProjectFacetVersion CDI_20 = CDI_FACET.getVersion(CDIVersion.CDI_2_0.toString()); //$NON-NLS-1$

	private boolean errorOccurs = false;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.ILogListener#logging(org.eclipse.core.runtime.IStatus, java.lang.String)
	 */
	public void logging(IStatus status, String plugin) {
		if(status.getPlugin().equals(CDICorePlugin.PLUGIN_ID)) {
			errorOccurs = true; 
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.common.project.facet.core.IDelegate#execute(org.eclipse.core.resources.IProject, org.eclipse.wst.common.project.facet.core.IProjectFacetVersion, java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void execute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		IDataModel model = (IDataModel) config;
		boolean generateBeansXml = model.getBooleanProperty(GENERATE_BEANS_XML);
		CDIVersion beansXmlVersion = null;
		if(generateBeansXml) {
			if(fv.compareTo(CDI_10) == 0 ) {
				beansXmlVersion = CDIVersion.CDI_1_0;
			} else if (fv.compareTo(CDI_20) == 0) {
				beansXmlVersion =  CDIVersion.CDI_2_0;
			} else {
				beansXmlVersion = CDIVersion.CDI_1_1;
			}
		}
		CDIUtil.enableCDI(project, beansXmlVersion, monitor);
		if(errorOccurs) {
			errorOccurs = false;
			Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
						ErrorDialog.openError(Display.getCurrent().getActiveShell(), 
								CDICoreMessages.CDI_FACET_INSTALL_ABSTRACT_DELEGATE_ERROR,
								CDICoreMessages.CDI_FACET_INSTALL_ABSTRACT_DELEGATE_CHECK_ERROR_LOG_VIEW,
								new Status(IStatus.ERROR,CDICorePlugin.PLUGIN_ID,
										CDICoreMessages.CDI_FACET_INSTALL_ABSTRACT_DELEGATE_ERRORS_OCCURED));
					}
				});
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.common.project.facet.core.IActionConfigFactory#create()
	 */
	public Object create() throws CoreException {
		return null;
	}
}