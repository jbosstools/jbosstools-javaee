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
package org.jboss.tools.seam.internal.core;

import java.util.List;

import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.IValueInfo;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.event.Change;

/**
 * @author Viacheslav Kabanovich
 */
public class AbstractContextVariable extends AbstractSeamDeclaration implements ISeamContextVariable {

	protected ScopeType scopeType;
	protected String scope;

	public ScopeType getScope() {
		return scopeType;
	}

	public void setScope(ScopeType type) {
		scopeType = type;
		scope = scopeType == null ? null : scopeType.toString();
	}

	public void setScopeAsString(String scope) {
		if(scope != null && scope.indexOf('.') > 0) {
			scope = scope.substring(scope.lastIndexOf('.'));
		}
		try {
			this.scopeType = scope == null || scope.length() == 0 ? ScopeType.UNSPECIFIED : ScopeType.valueOf(scope.toUpperCase());
		} catch (IllegalArgumentException e) {
			//ignore
		}
	}

	/**
	 * Merges loaded data into currently used declaration.
	 * If changes were done returns a list of changes. 
	 * @param f
	 * @return list of changes
	 */
	public List<Change> merge(SeamObject s) {
		List<Change> changes = super.merge(s);
		AbstractContextVariable f = (AbstractContextVariable)s;

		if(!stringsEqual(name, f.name)) {
			changes = Change.addChange(changes, new Change(this, ISeamXmlComponentDeclaration.NAME, name, f.name));
			name = f.name;
		}
		if(!stringsEqual(scope, f.scope)) {
			changes = Change.addChange(changes, new Change(this, ISeamXmlComponentDeclaration.SCOPE, scope, f.scope));
			scope = f.scope;
			scopeType = f.scopeType;
		}
	
		return changes;
	}

	public void setScope(IValueInfo value) {
		attributes.put(ISeamXmlComponentDeclaration.SCOPE, value);
		setScopeAsString(value == null ? null : value.getValue());
	}

}
