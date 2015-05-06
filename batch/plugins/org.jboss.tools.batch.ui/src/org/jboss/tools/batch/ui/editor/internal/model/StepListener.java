/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.model;

import org.eclipse.sapphire.ElementType;

/**
 * Represents {@code <listener>} child tags of {@code <listeners>} the parent of
 * which is a {@code <step>}.
 * 
 * @author Tomas Milata
 *
 */
public interface StepListener extends Listener, RefAttributeElement {
	ElementType TYPE = new ElementType(StepListener.class);
}
