/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.services.diagram.layout.persistence;

import org.eclipse.sapphire.ui.diagram.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.layout.DiagramLayoutPersistenceService;

/**
 * Overrides Sapphire's standard diagram layout persistence so that no
 * persistence is used. Auto layout is used when file is opened.
 * 
 * @author Tomas Milata
 */
public class BatchDiagramLayoutPersistenceService extends DiagramLayoutPersistenceService {

	@Override
	public DiagramConnectionInfo read(DiagramConnectionPart connection) {
		return null;
	}

}
