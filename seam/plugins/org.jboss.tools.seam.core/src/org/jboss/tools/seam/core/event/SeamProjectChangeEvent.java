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
package org.jboss.tools.seam.core.event;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProject;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamProjectChangeEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	/**
	 * Modified seam project. 
	 */
	ISeamProject project;
	
	/**
	 * List of structured changes.
	 */
	List<Change> changes;

	public SeamProjectChangeEvent(ISeamProject project, List<Change> changes) {
		super(project);
		this.changes = changes;
	}
	
	/**
	 * Returns modified seam project
	 * @return
	 */	
	public ISeamProject getProject() {
		return project;
	}
	
	/**
	 * Returns all changes
	 * @return
	 */
	public List<Change> getAllChanges() {
		return changes;
	}
	
	/**
	 * Invokes visitor for each change, which result in iteration over
	 * tree of changes when  
	 * @param visitor
	 */
	public void visit(IChangeVisitor visitor) {
		if(changes != null) for (Change c: changes) {
			c.visit(visitor);
		}
	}

	/**
	 * Utility method, returns all components that have been modified.
	 * The list does not include removed and added components.
	 * This method makes an example of using visitors to process 
	 * seam project events.
	 * 
	 * @return
	 */
	public List<ISeamComponent> getAllModifiedComponents() {
		final List<ISeamComponent> list = new ArrayList<ISeamComponent>();
		visit(new IChangeVisitor() {
			public boolean visit(Change change) {
				Object t = change.getTarget();
				if(t instanceof ISeamComponent) {
					list.add((ISeamComponent)t);
				} else if(t instanceof ISeamProject) {
					return true;
				}
				return false;
			}			
		});
		return list;
	}

}
