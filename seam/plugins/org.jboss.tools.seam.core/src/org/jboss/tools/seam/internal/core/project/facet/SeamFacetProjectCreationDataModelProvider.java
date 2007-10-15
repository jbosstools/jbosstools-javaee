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
import org.eclipse.jst.j2ee.internal.web.archive.operations.WebFacetProjectCreationDataModelProvider;
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
//					if (isPropertySet(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME))
//						setProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME, event.getProperty());
//					else
//
					setProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME, event.getProperty());
					model.notifyPropertyChange(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME, IDataModel.DEFAULT_CHG);
					model.notifyPropertyChange(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER, IDataModel.VALID_VALUES_CHG);
				}
			}
		});	

/*		
		IDataModel webFacet = DataModelFactory.createDataModel(new WebFacetInstallDataModelProvider());
		map.add(webFacet);

		String webRoot = webFacet.getStringProperty(IWebFacetInstallDataModelProperties.CONFIG_FOLDER);
		String webSrc = webFacet.getStringProperty(IWebFacetInstallDataModelProperties.SOURCE_FOLDER);
		javaFacet.setProperty(IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME, webSrc);
		// If using optimized single root structure, set the output folder to "<content folder>/WEB-INF/classes"
		if (ProductManager.shouldUseSingleRootStructure())
			javaFacet.setProperty(IJavaFacetInstallDataModelProperties.DEFAULT_OUTPUT_FOLDER_NAME, webRoot+"/"+J2EEConstants.WEB_INF_CLASSES); //$NON-NLS-1$
		webFacet.addListener(new IDataModelListener() {
			public void propertyChanged(DataModelEvent event) {
				if (IJ2EEModuleFacetInstallDataModelProperties.EAR_PROJECT_NAME.equals(event.getPropertyName())) {
					if (isPropertySet(EAR_PROJECT_NAME))
						setProperty(EAR_PROJECT_NAME, event.getProperty());
					else
						model.notifyPropertyChange(EAR_PROJECT_NAME, IDataModel.DEFAULT_CHG);
				}else if (IJ2EEModuleFacetInstallDataModelProperties.ADD_TO_EAR.equals(event.getPropertyName())) {
					setProperty(ADD_TO_EAR, event.getProperty());
				}
			}
		});	
*/		
		Collection requiredFacets = (Collection)getProperty(REQUIRED_FACETS_COLLECTION);
		requiredFacets.add(ProjectFacetsManager.getProjectFacet(seamFacet.getStringProperty(IFacetDataModelProperties.FACET_ID)));
		setProperty(REQUIRED_FACETS_COLLECTION, requiredFacets);
	}

	public boolean propertySet(String propertyName, Object propertyValue) {
		if( propertyName.equals( IFacetProjectCreationDataModelProperties.FACET_RUNTIME )){
			FacetDataModelMap map = (FacetDataModelMap) getProperty(FACET_DM_MAP);
			IDataModel seamFacet = map.getFacetDataModel( ISeamCoreConstants.SEAM_CORE_FACET_ID );	
			seamFacet.setProperty( ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME, propertyValue );
		}

		return super.propertySet(propertyName, propertyValue);
	}
	
	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER);
		names.add(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME);
		return names;
	}

	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		if (ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER.equals(propertyName)) {
			Collection projectFacets = (Collection)getProperty(REQUIRED_FACETS_COLLECTION);
			Object rt = getProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME);
			String primaryName = getRuntimeName(rt); 
			
			List<IServer> list = getServers(primaryName);
			
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
		return super.getValidPropertyDescriptors(propertyName);
	}

	private List<IServer> getServers(String runtimeName) {
		ArrayList<IServer> list = new ArrayList<IServer>();
		
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
		return list;
	}
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

	 IStatus validateServer(Object serverObject) {
		if (serverObject == null) {
			return SeamCorePlugin.createErrorStatus(SeamCoreMessages.getString("ERROR_JBOSS_AS_TARGET_SERVER_IS_EMPTY"), null);
		}

		IServer s = (serverObject instanceof IServer ? (IServer)serverObject : null);

		if (s == null) {
			return SeamCorePlugin.createErrorStatus(SeamCoreMessages.getString("ERROR_JBOSS_AS_TARGET_SERVER_UNKNOWN"), null);
		}

		Object rt = getProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME);
		String primaryRuntimeName = getRuntimeName(rt);

		List<IServer> servers = getServers(primaryRuntimeName);
		if (servers.isEmpty()) {
			return SeamCorePlugin.createErrorStatus(SeamCoreMessages.getString("ERROR_JBOSS_AS_TARGET_SERVER_NO_SERVERS_DEFINED"), null);
		}

		for (IServer server : servers) {
			if (s.equals(server)) {
				return OK_STATUS;
			}
		}
		
		return SeamCorePlugin.createErrorStatus(SeamCoreMessages.getString("ERROR_JBOSS_AS_TARGET_SERVER_INCOMPATIBLE"), null);
	}
	
	private String getRuntimeName(Object rt) {
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
			return SeamCorePlugin.createErrorStatus(SeamCoreMessages.getString("ERROR_JBOSS_AS_TARGET_RUNTIME_IS_EMPTY"), null);
		}
		if (getRuntimeName(runtimeObject) == null) {
			return SeamCorePlugin.createErrorStatus(SeamCoreMessages.getString("ERROR_JBOSS_AS_TARGET_RUNTIME_UNKNOWN"), null);
		}
		
		return OK_STATUS;
	}
	
	public static String getServerName(IDataModel model) {
		Object serverObject = model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER);
		if (!(serverObject instanceof IServer))
			return "";
		
		IServer server = (IServer)serverObject;
		return (server.getName() == null ? "" : server.getName());
	}
	
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
