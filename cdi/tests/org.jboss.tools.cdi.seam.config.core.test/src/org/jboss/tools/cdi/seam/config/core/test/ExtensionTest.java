package org.jboss.tools.cdi.seam.config.core.test;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class ExtensionTest extends SeamConfigTest {
	public ExtensionTest() {}

	public void testExtension() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		
		Set<IBuildParticipantFeature> bp = cdi.getNature().getExtensionManager().getBuildParticipantFeature();
		System.out.println(bp.size());
	}

}
