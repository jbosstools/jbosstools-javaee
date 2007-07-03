package org.jboss.tools.seam.internal.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamComponentMethodType;

public class SeamJavaComponentDeclaration extends SeamComponentDeclaration
		implements ISeamJavaComponentDeclaration {

	protected String className = null;
	protected ScopeType scopeType = ScopeType.UNSPECIFIED;
	protected boolean stateful = false;
	protected boolean entity = false;
	
	protected IType type;

	protected Set<IBijectedAttribute> bijectedAttributes = new HashSet<IBijectedAttribute>();
	protected Set<ISeamComponentMethod> componentMethods = new HashSet<ISeamComponentMethod>();
	protected Set<IRole> roles = new HashSet<IRole>();
	
	public void setType(IType type) {
		this.type = type;
	}

	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}

	public void addBijectedAttribute(IBijectedAttribute attribute) {
		bijectedAttributes.add(attribute);		
	}

	public void addMethod(ISeamComponentMethod method) {
		componentMethods.add(method);
	}

	public void addRole(IRole role) {
		roles.add(role);		
	}

	public Set<IBijectedAttribute> getBijectedAttributes() {
		return bijectedAttributes;
	}

	public Set<IBijectedAttribute> getBijectedAttributesByName(String name) {
		Set<IBijectedAttribute> result = null;
		for(IBijectedAttribute a: getBijectedAttributes()) {
			if(name.equals(a.getName())) {
				if(result == null) result = new HashSet<IBijectedAttribute>();
				result.add(a);
			}
		}
		return result;
	}

	public Set<IBijectedAttribute> getBijectedAttributesByType(
			BijectedAttributeType type) {
		Set<IBijectedAttribute> result = null;
		for(IBijectedAttribute a: getBijectedAttributes()) {
			if(type.equals(a.getType())) {
				if(result == null) result = new HashSet<IBijectedAttribute>();
				result.add(a);
			}
		}
		return result;
	}

	public Set<ISeamComponentMethod> getMethods() {
		return componentMethods;
	}

	public Set<ISeamComponentMethod> getMethodsByType(
			SeamComponentMethodType type) {
		Set<ISeamComponentMethod> result = null;
		for(ISeamComponentMethod a: getMethods()) {
			if(type.equals(a.getType())) {
				if(result == null) result = new HashSet<ISeamComponentMethod>();
				result.add(a);
			}
		}
		return result;
	}

	public Set<IRole> getRoles() {
		return roles;
	}

	public boolean isEntity() {
		return entity;
	}

	public boolean isStateful() {
		return stateful;
	}

	public void removeBijectedAttribute(IBijectedAttribute attribute) {
		bijectedAttributes.remove(attribute);
	}

	public void removeMethod(ISeamComponentMethod method) {
		componentMethods.remove(method);
	}

	public void removeRole(IRole role) {
		roles.remove(role);
	}

	public void setEntity(boolean entity) {
		this.entity = entity;
	}

	public IMember getSourceMember() {
		return type;
	}

}
