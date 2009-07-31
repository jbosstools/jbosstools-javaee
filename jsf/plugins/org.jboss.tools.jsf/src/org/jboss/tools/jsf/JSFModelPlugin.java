/*
 * Created on 04.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.jboss.tools.jsf;

import java.util.HashMap;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jboss.tools.common.log.BaseUIPlugin;
import org.jboss.tools.common.log.IPluginLog;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelConstants;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.util.ClassLoaderUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

/**
 * @author Eskimo
 *
 */
public class JSFModelPlugin extends BaseUIPlugin {
	public static final String PLUGIN_ID = "org.jboss.tools.jsf"; //$NON-NLS-1$

	public static final String CA_JSF_EL_IMAGE_PATH = "images/ca/icons_JSF_EL.gif"; //$NON-NLS-1$
	public static final String CA_JSF_MESSAGES_IMAGE_PATH = "images/ca/icons_Message_Bundles.gif"; //$NON-NLS-1$

	// A Map to save a descriptor for each image
	private HashMap fImageDescRegistry = null;

	public JSFModelPlugin() {
		super();
		INSTANCE = this;
	}
	
	protected void initializeDefaultPluginPreferences()
	{
		super.initializeDefaultPluginPreferences();

		Properties p = new Properties();
		p.setProperty(XModelConstants.WORKSPACE, EclipseResourceUtil.getInstallPath(this));
		p.setProperty("initialModel", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		ClassLoaderUtil.init();
		XModel initialModel = PreferenceModelUtilities.createPreferenceModel(p);
		if (initialModel != null)
		{
			try {
			PreferenceModelUtilities.initPreferenceValue(initialModel, JSFPreference.USE_DEFAULT_JSF_PROJECT_ROOT);
			PreferenceModelUtilities.initPreferenceValue(initialModel, JSFPreference.DEFAULT_JSF_VERSION);
			PreferenceModelUtilities.initPreferenceValue(initialModel, JSFPreference.DEFAULT_JSF_PROJECT_TEMPLATE);
			PreferenceModelUtilities.initPreferenceValue(initialModel, JSFPreference.DEFAULT_JSF_PROJECT_ROOT_DIR);
			PreferenceModelUtilities.initPreferenceValue(initialModel, JSFPreference.DEFAULT_JSF_SERVLET_VERSION);
			PreferenceModelUtilities.initPreferenceValue(initialModel, JSFPreference.REGISTER_NEW_JSF_PROJECT_IN_SERVER);
			PreferenceModelUtilities.initPreferenceValue(initialModel, JSFPreference.REGISTER_IMPORTED_JSF_PROJECT_IN_SERVER);
			} catch (XModelException e) {
				ModelPlugin.getPluginLog().logError(e);
			}
			
			PreferenceModelUtilities.getPreferenceModel().save();
		}
	}
	
	public static void log(String msg) {
		if(isDebugEnabled()) INSTANCE.getLog().log(new Status(Status.INFO, PLUGIN_ID, Status.OK, msg, null));		
	}
	
	public static void log(IStatus status) {
		if(isDebugEnabled() || !status.isOK()) INSTANCE.getLog().log(status);
	}
	
	public static void log(String message, Throwable exception) {
		INSTANCE.getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.OK, message, exception));		
	}
	
	public static boolean isDebugEnabled() {
		return INSTANCE.isDebugging();
	}

	public static JSFModelPlugin getDefault() {
		return INSTANCE;
	}
	
	static JSFModelPlugin INSTANCE = null;
	
	/**
	 * @return IPluginLog object
	 */
	public static IPluginLog getPluginLog() {
		return getDefault();
	}

	/**
	 * Creates an image from the given resource and adds the image to the
	 * image registry.
	 * 
	 * @param resource
	 * @return Image
	 */
	private Image createImage(String resource) {
		ImageDescriptor desc = getImageDescriptorFromRegistry(resource);
		Image image = null;

		if (desc != null) {
			image = desc.createImage();
			// dont add the missing image descriptor image to the image
			// registry
			if (!desc.equals(ImageDescriptor.getMissingImageDescriptor())) {
				getImageRegistry().put(resource, image);
			}
		}
		return image;
	}

	/**
	 * Creates an image descriptor from the given imageFilePath and adds the
	 * image descriptor to the image descriptor registry. If an image
	 * descriptor could not be created, the default "missing" image descriptor
	 * is returned but not added to the image descriptor registry.
	 * 
	 * @param imageFilePath
	 * @return ImageDescriptor image descriptor for imageFilePath or default
	 *         "missing" image descriptor if resource could not be found
	 */
	private ImageDescriptor createImageDescriptor(String imageFilePath) {
		ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, imageFilePath);
		if (imageDescriptor != null) {
			getImageDescriptorRegistry().put(imageFilePath, imageDescriptor);
		}
		else {
			imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
		}

		return imageDescriptor;
	}

	/**
	 * Retrieves the image associated with resource from the image registry.
	 * If the image cannot be retrieved, attempt to find and load the image at
	 * the location specified in resource.
	 * 
	 * @param resource
	 *            the image to retrieve
	 * @return Image the image associated with resource or null if one could
	 *         not be found
	 */
	public Image getImage(String resource) {
		Image image = getImageRegistry().get(resource);
		if (image == null) {
			// create an image
			image = createImage(resource);
		}
		return image;
	}

	/**
	 * Retrieves the image descriptor associated with resource from the image
	 * descriptor registry. If the image descriptor cannot be retrieved,
	 * attempt to find and load the image descriptor at the location specified
	 * in resource.
	 * 
	 * @param resource
	 *            the image descriptor to retrieve
	 * @return ImageDescriptor the image descriptor assocated with resource or
	 *         the default "missing" image descriptor if one could not be
	 *         found
	 */
	public ImageDescriptor getImageDescriptorFromRegistry(String resource) {
		ImageDescriptor imageDescriptor = null;
		Object o = getImageDescriptorRegistry().get(resource);
		if (o == null) {
			// create a descriptor
			imageDescriptor = createImageDescriptor(resource);
		}
		else {
			imageDescriptor = (ImageDescriptor) o;
		}
		return imageDescriptor;
	}

	/**
	 * Returns the image descriptor registry for this plugin.
	 * 
	 * @return HashMap - image descriptor registry for this plugin
	 */
	private HashMap getImageDescriptorRegistry() {
		if (fImageDescRegistry == null) {
			fImageDescRegistry = new HashMap();
		}
		return fImageDescRegistry;
	}
	
}
