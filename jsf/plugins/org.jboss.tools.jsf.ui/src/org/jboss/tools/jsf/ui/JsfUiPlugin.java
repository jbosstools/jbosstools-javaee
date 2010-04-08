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
package org.jboss.tools.jsf.ui;

import java.io.IOException;


import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.jboss.tools.common.log.BaseUIPlugin;
import org.jboss.tools.common.log.IPluginLog;
import org.jboss.tools.jsf.ui.editor.check.ProjectNaturesChecker;
import org.jboss.tools.jsf.ui.editor.pref.template.TemplateContextTypeIdsXHTML;
import org.osgi.framework.BundleContext;

public class JsfUiPlugin extends BaseUIPlugin {
	
	public static String PLUGIN_ID = "org.jboss.tools.jsf.ui"; //$NON-NLS-1$
	
	/**
	 * The template store for the html editor.
	 */
	private TemplateStore fTemplateStore;
	/**
	 * The template context type registry for the html editor.
	 */
	private ContextTypeRegistry fContextTypeRegistry;

	public JsfUiPlugin() {
	}

	public void start(BundleContext context) throws Exception {
	    super.start(context);
		ProjectNaturesChecker.getInstance();
	}

	public static JsfUiPlugin getDefault() {
		return PluginHolder.INSTANCE;
	}

	public static boolean isDebugEnabled() {
		return PluginHolder.INSTANCE.isDebugging();
	}

	static class PluginHolder {
		static JsfUiPlugin INSTANCE = (JsfUiPlugin)Platform.getPlugin(PLUGIN_ID); 
	}

	public static Shell getShell() {
		return PluginHolder.INSTANCE.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	/**
	 * @return IPluginLog object
	 */
	public static IPluginLog getPluginLog() {
		return getDefault();
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	public TemplateStore getTemplateStore() {
		if (this.fTemplateStore == null) {
			this.fTemplateStore = new ContributionTemplateStore(getTemplateContextRegistry(), getPreferenceStore(), "org.jboss.tools.jsf.ui.custom_templates");

			try {
				this.fTemplateStore.load();
			} catch (IOException e) {
				this.logError(e);
			}
		}
		return this.fTemplateStore;
	}
	
	/**
	 * Returns the template context type registry for the html plugin.
	 * 
	 * @return the template context type registry for the html plugin
	 */
	public ContextTypeRegistry getTemplateContextRegistry() {
		if (this.fContextTypeRegistry == null) {
			ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();
			registry.addContextType(TemplateContextTypeIdsXHTML.ALL);
			registry.addContextType(TemplateContextTypeIdsXHTML.NEW);
			registry.addContextType(TemplateContextTypeIdsXHTML.TAG);
			registry.addContextType(TemplateContextTypeIdsXHTML.ATTRIBUTE);
			registry.addContextType(TemplateContextTypeIdsXHTML.ATTRIBUTE_VALUE);

			this.fContextTypeRegistry = registry;
		}

		return this.fContextTypeRegistry;
	}
}