/**
 * This feature corresponds to runtime feature
 * javax.enterprise.inject.spi.ProcessAnnotatedType.setAnnotatedType()
 *
 * This feature is invoked by bean when it computes its name.
 * The first non-null value is accepted.
 * 
 * @author Viacheslav Kabanovich
 *
 */
package org.jboss.tools.cdi.core.extension.feature;

import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;

/**
 * This feature corresponds to ProcessAnnotatedTypeEvent in CDI runtime.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IProcessAnnotatedTypeFeature {

	/**
	 * Method is called after CDI builder loaded type definitions and before they are 
	 * used to build beans. Client may change type definitions and there members or veto them.
	 * 
	 * @param typeDefinition
	 * @param context
	 */
	public void processAnnotatedType(TypeDefinition typeDefinition, DefinitionContext context);

}
