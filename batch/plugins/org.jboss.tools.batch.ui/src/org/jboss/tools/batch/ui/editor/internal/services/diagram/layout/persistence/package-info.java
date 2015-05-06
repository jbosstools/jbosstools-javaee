/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/

/**
 * Provides implemenation classes for the Batch diagram layout persistence
 * service. Standard persistence is not applicable as it persists only content
 * which is currently displayed in the diagram, which does not always cover the
 * whole model (In case of nested flows).
 */
package org.jboss.tools.batch.ui.editor.internal.services.diagram.layout.persistence;