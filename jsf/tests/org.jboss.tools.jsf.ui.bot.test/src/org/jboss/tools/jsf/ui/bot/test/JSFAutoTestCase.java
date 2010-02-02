package org.jboss.tools.jsf.ui.bot.test;

import java.io.IOException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.vpe.ui.bot.test.VPEAutoTestCase;

public abstract class JSFAutoTestCase extends VPEAutoTestCase{
	
	@Override
	protected String getPathToResources(String testPage) throws IOException{
		return FileLocator.toFileURL(Platform.getBundle(Activator.PLUGIN_ID).getEntry("/")).getFile()+"resources/"+testPage ; //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected abstract void closeUnuseDialogs(); 
	
	protected abstract boolean isUnuseDialogOpened();
	
}
