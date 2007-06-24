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
package org.jboss.tools.jsf.project.capabilities;

public abstract class PerformerItem implements IPerformerItem {
	protected IPerformerItem parent;
	protected boolean selected = true;

	public String getDisplayName() {
		return null;
	}

	public IPerformerItem getParent() {
		return parent;
	}
	
	public void setParent(IPerformerItem p) {
		parent = p;
	}

	public IPerformerItem[] getChildren() {
		return new IPerformerItem[0];
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean b) {
		selected = b;		
	}

	public final boolean isEnabled() {
		IPerformerItem p = getParent();
		while(p != null) {
			if(!p.isSelected()) return false;
			p = p.getParent();
		}
		return true;
	}

	public boolean execute(PerformerContext context) throws Exception {
		throw new RuntimeException("Not implemented in " + getClass().getName());
	}

}
