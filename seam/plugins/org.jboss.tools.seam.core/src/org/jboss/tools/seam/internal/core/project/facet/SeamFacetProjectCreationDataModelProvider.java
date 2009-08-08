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

package org.jboss.tools.seam.internal.core.project.facet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jst.j2ee.internal.web.archive.operations.WebFacetProjectCreationDataModelProvider;
import org.eclipse.jst.jsf.ui.internal.JSFUiPlugin;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.core.SeamCorePlugin;

public class SeamFacetProjectCreationDataModelProvider extends WebFacetProjectCreationDataModelProvider  {
	
	@Override
	public Object getDefaultProperty(String propertyName) {
		if(IFacetProjectCreationDataModelProperties.FACET_PROJECT_NAME.equals(propertyName)) {
			// Any not empty string should be returned by default
			// to workaround https://bugs.eclipse.org/bugs/show_bug.cgi?id=206541
			return "__d_u_m_m_y__for__bug206541__";
		}
		return super.getDefaultProperty(propertyName);
	}

	public static IStatus OK_STATUS = new Status(IStatus.OK, SeamCorePlugin.PLUGIN_ID, 0, "OK", null); //$NON-NLS-1$ //$NON-NLS-2$

	public SeamFacetProjectCreationDataModelProvider() {
		super();
	}

	public void init() {
		super.init();
		FacetDataModelMap map = (FacetDataModelMap) getProperty(FACET_DM_MAP);
		IDataModel seamFacet = DataModelFactory.createDataModel(new SeamFacetInstallDataModelProvider());
		map.add(seamFacet);
		
		seamFacet.addListener(new IDataModelListener() {
			public void propertyChanged(DataModelEvent event) {
				if (ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME.equals(event.getPropertyName())) {
					setProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME, event.getProperty());
					model.notifyPropertyChange(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME, IDataModel.DEFAULT_CHG);
					model.notifyPropertyChange(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER, IDataModel.VALID_VALUES_CHG);
				} else if (IFacetDataModelProperties.FACET_PROJECT_NAME.equals(event.getPropertyName())) {
					setProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME, event.getProperty());
				}
			}
		});	

		Collection requiredFacets = (Collection)getProperty(REQUIRED_FACETS_COLLECTION);
		requiredFacets.add(ProjectFacetsManager.getProjectFacet(seamFacet.getStringProperty(IFacetDataModelProperties.FACET_ID)));
		setProperty(REQUIRED_FACETS_COLLECTION, requiredFacets);

		IDialogSettings s = JSFUiPlugin.getDefault().getDialogSettings();
		IDialogSettings r = s.getSection(JSFUiPlugin.PLUGIN_ID + ".jsfFacetInstall");
		if(r == null) {
			r = s.addNewSection(JSFUiPlugin.PLUGIN_ID + ".jsfFacetInstall");
		}
		String deployImplType = r.get("deployImplType");
		if(deployImplType == null || !deployImplType.endsWith("_SUPPLIED")) {
			r.put("deployImplType", "SERVER_SUPPLIED");
		}
		IDialogSettings u = r.getSection("urlMappings");
		if(u == null) {
			u = r.addNewSection("urlMappings");
		}
		u.put("pattern", new String[]{"*.seam"});
	}

	public boolean propertySet(String propertyName, Object propertyValue) {
		if( propertyName.equals( IFacetProjectCreationDataModelProperties.FACET_RUNTIME )){
			FacetDataModelMap map = (FacetDataModelMap) getProperty(FACET_DM_MAP);
			IDataModel seamFacet = map.getFacetDataModel(ISeamFacetDataModelProperties.SEAM_FACET_ID);	
			seamFacet.setProperty( ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME, propertyValue );

			if (propertyValue != null) {
				// Fixes the empty/wrong server 
				IServer server = (IServer)model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER);
				if (!validateServer(server).isOK()) {
					List<IServer> servers = getServers(getRuntimeName(propertyValue));
					if (servers != null && !servers.isEmpty()) {
						setProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER, servers.get(0));
					}
				}
			}
		} else if (propertyName.equals(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER)) {
			FacetDataModelMap map = (FacetDataModelMap) getProperty(FACET_DM_MAP);
			IDataModel seamFacet = map.getFacetDataModel(ISeamFacetDataModelProperties.SEAM_FACET_ID);
			seamFacet.setProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER, propertyValue);
		} else if (propertyName.equals(IFacetDataModelProperties.FACET_PROJECT_NAME)) {
			model.setProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME, propertyValue);
		}

		return super.propertySet(propertyName, propertyValue);
	}
	
	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER);
		names.add(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME);
		names.add(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME);
		return names;
	}

	public static DataModelPropertyDescriptor[] getServerPropertyDescriptors(String runtimeName) {
		List<IServer> list = getServers(runtimeName);

		DataModelPropertyDescriptor[] descriptors = new DataModelPropertyDescriptor[list.size() + 1];

		Iterator<IServer> iterator = list.iterator();
		for (int i = 0; i < descriptors.length - 1; i++) {
			IServer server = (IServer) iterator.next();
			descriptors[i] = new DataModelPropertyDescriptor(server, server.getName());
		}
		descriptors[descriptors.length - 1] = new DataModelPropertyDescriptor(null, "<None>");

		if(descriptors.length > 2){
			Arrays.sort(descriptors, 0, descriptors.length - 2, new Comparator() {
				public int compare(Object arg0, Object arg1) {
					DataModelPropertyDescriptor d1 = (DataModelPropertyDescriptor)arg0;
					DataModelPropertyDescriptor d2 = (DataModelPropertyDescriptor)arg1;
					return d1.getPropertyDescription().compareTo(d2.getPropertyDescription());
				}
			});
		}

		return descriptors;
	}

	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		if (ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER.equals(propertyName)) {
			Object rt = getProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME);
			String primaryName = getRuntimeName(rt); 
			return getServerPropertyDescriptors(primaryName);
		}
		return super.getValidPropertyDescriptors(propertyName);
	}

	private static List<IServer> getServers(String runtimeName) {
		ArrayList<IServer> list = new ArrayList<IServer>();
		if( runtimeName != null ) {
			if (runtimeName != null) {
				IServer[] servers = ServerCore.getServers(); 
				for (IServer server : servers) { 
					IRuntime runtime = server.getRuntime(); 
					if(runtime!=null) { 
						String serverRuntimeName = runtime.getName(); 
						if(runtimeName.equals(serverRuntimeName)) { 
							list.add(server); 
						} 
					} 
				} 
			}
		}
		return list;
	}

	/**
	 * Performs the property validation 
	 * 
	 * returns IStatus status of validation
	 */
	public IStatus validate(String propertyName) {
		IStatus status = super.validate(propertyName);
		if (status != null && !status.isOK())
			return status;
		
		if (ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER.equals(propertyName)) {
			IServer server = (IServer)model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER);
			status = validateServer(server);
			if (!status.isOK())
				return status;
		}
		if(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME.equals(propertyName)) {
			org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime = 
				(org.eclipse.wst.common.project.facet.core.runtime.IRuntime)model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME);
			status = validateRuntime(runtime);
			if (!status.isOK())
				return status;
		}
		return OK_STATUS;
	}

	public static IStatus validateUpperCaseInProjectName(String projectName) {
		if(projectName!=null && projectName.length()>0) {
			char firstLetter = projectName.charAt(0);
			if(Character.isUpperCase(firstLetter)) {
				return new Status(IStatus.WARNING, SeamCorePlugin.PLUGIN_ID, SeamCoreMessages.SEAM_INSTALL_WIZARD_PROJECT_NAME_WITH_UPPERCASE);
			}
		}
		return OK_STATUS;
	}

	private IStatus validateServer(Object serverObject) {
		if (serverObject == null) {
			return SeamCorePlugin.createErrorStatus(SeamCoreMessages.ERROR_JBOSS_AS_TARGET_SERVER_IS_EMPTY, null);
		}

		IServer s = (serverObject instanceof IServer ? (IServer)serverObject : null);

		if (s == null) {
			return SeamCorePlugin.createErrorStatus(SeamCoreMessages.ERROR_JBOSS_AS_TARGET_SERVER_UNKNOWN, null);
		}

		Object rt = getProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME);
		String primaryRuntimeName = getRuntimeName(rt);

		List<IServer> servers = getServers(primaryRuntimeName);
		if (servers.isEmpty()) {
			return SeamCorePlugin.createErrorStatus(SeamCoreMessages.ERROR_JBOSS_AS_TARGET_SERVER_NO_SERVERS_DEFINED, null);
		}

		for (IServer server : servers) {
			if (s.equals(server)) {
				return OK_STATUS;
			}
		}
		
		return SeamCorePlugin.createErrorStatus(SeamCoreMessages.ERROR_JBOSS_AS_TARGET_SERVER_INCOMPATIBLE, null);
	}
	
	private static String getRuntimeName(Object rt) {
		if( rt == null ) {
			return null;
		}
		String rtName = null;
		if (rt instanceof org.eclipse.wst.common.project.facet.core.runtime.IRuntime) {
			rtName = ((org.eclipse.wst.common.project.facet.core.runtime.IRuntime)rt).getName();
		} else if (rt instanceof IRuntime) {
			rtName = ((IRuntime)rt).getName();
		}
		return rtName;
	}

	IStatus validateRuntime(Object runtimeObject) {
		if (runtimeObject == null) {
			return SeamCorePlugin.createErrorStatus(SeamCoreMessages.ERROR_JBOSS_AS_TARGET_RUNTIME_IS_EMPTY, null);
		}
		if (getRuntimeName(runtimeObject) == null) {
			return SeamCorePlugin.createErrorStatus(SeamCoreMessages.ERROR_JBOSS_AS_TARGET_RUNTIME_UNKNOWN, null);
		}
		
		return OK_STATUS;
	}
	
	/**
	 * Returns The server name stored in the 
	 * 	ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER property value 
	 * 	of the model specified
	 *  
	 * @param model
	 * @return String Server name 
	 */
	public static String getServerName(IDataModel model) {
		Object serverObject = model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER);
		if (!(serverObject instanceof IServer))
			return "";
		
		IServer server = (IServer)serverObject;
		return (server.getName() == null ? "" : server.getName());
	}
	
	/**
	 * Sets the server by its name as 
	 * 	ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER property value 
	 * 	of the model specified
	 * 
	 * @param model
	 * @param serverName
	 */
	public static void setServerName(IDataModel model, String serverName) {
		if (serverName == null)
			return;
		
		IServer[] servers = ServerCore.getServers(); 
		for (IServer server : servers) { 
			if(serverName.equals(server.getName())) {
				model.setProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER, server);
				return;
			} 
		} 
	}
}