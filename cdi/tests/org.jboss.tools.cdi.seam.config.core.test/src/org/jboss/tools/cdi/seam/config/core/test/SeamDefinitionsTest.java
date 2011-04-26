package org.jboss.tools.cdi.seam.config.core.test;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IJavaAnnotation;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigExtension;
import org.jboss.tools.cdi.seam.config.core.ConfigDefinitionContext;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeanDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeansDefinition;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class SeamDefinitionsTest extends SeamConfigTest {
	public SeamDefinitionsTest() {}

	public void testBeansWithReplacesAndModifies() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		ConfigDefinitionContext context = (ConfigDefinitionContext)getConfigExtension(cdi).getContext();
		SeamBeansDefinition d = getBeansDefinition(context, "src/META-INF/beans.xml");
		
		Set<SeamBeanDefinition> ds = findBeanDefinitionByTagName(d, "test602:Report");
		assertEquals(1, ds.size());
		SeamBeanDefinition report = ds.iterator().next();
		ITextSourceReference modifies = report.getModifiesLocation();
		assertNotNull(modifies);
		IJavaAnnotation annotation = report.getAnnotation("org.jboss.test602.report.NewQualifier");
		assertNotNull(annotation);
		
		ds = findBeanDefinitionByTagName(d, "test602:ReportDatasource");
		assertEquals(1, ds.size());
		SeamBeanDefinition reportDatasource = ds.iterator().next();
		ITextSourceReference replaces = reportDatasource.getReplacesLocation();
		assertNotNull(replaces);
		

	}

}
