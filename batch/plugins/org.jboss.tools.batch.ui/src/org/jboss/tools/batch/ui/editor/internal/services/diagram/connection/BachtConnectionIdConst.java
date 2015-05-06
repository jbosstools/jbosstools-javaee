/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.services.diagram.connection;

/**
 * Constants for Sapphire connection ids used in the Batch diagram.
 * 
 * @author Tomas Milata
 */
public interface BachtConnectionIdConst {

	/**
	 * Connection using the {@code next} atribute on a {@code <step>}, {@code 
	 * <split>} or a {@code <flow>} in the JSR-352 model.
	 */
	static final String NEXT_ATTRIBUTE_CONNECTION_ID = "NextAttributeConnection";
}
