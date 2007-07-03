package org.jboss.tools.seam.internal.core;

import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.ScopeType;

public class SeamXmlComponentDeclaration extends SeamPropertiesDeclaration
		implements ISeamXmlComponentDeclaration {
	
	String autoCreate = null;
	String className = null;
	String installed = null;
	String jndiName = null;
	String precedence = null;
	String scope = null;

	public String getAutoCreateAsString() {
		return autoCreate;
	}

	public String getClassName() {
		return className;
	}

	public boolean getInstalledAsString() {
		return "true".equals(installed);
	}

	public String getJndiName() {
		return jndiName;
	}

	public String getPrecedence() {
		return precedence;
	}

	public ScopeType getScope() {
		if(scope == null || scope.length() == 0) return ScopeType.UNSPECIFIED;
		return ScopeType.valueOf(scope);
	}

	public String getScopeAsString() {
		return scope;
	}

	public boolean isAutoCreate() {
		return "true".equals(autoCreate);
	}

	public boolean isInstalled() {
		return "true".equals(installed);
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public void setAutoCreate(String autoCreate) {
		this.autoCreate = autoCreate;
	}

	public void setInstalled(String installed) {
		this.installed = installed;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public void setPrecedence(String precedence) {
		this.precedence = precedence;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
