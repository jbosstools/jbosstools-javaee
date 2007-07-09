package org.jboss.tools.seam.internal.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamComponentMethodType;
import org.jboss.tools.seam.core.SeamComponentPrecedenceType;
import org.jboss.tools.seam.core.event.Change;
import org.jboss.tools.seam.internal.core.scanner.java.ValueInfo;

public class SeamJavaComponentDeclaration extends SeamComponentDeclaration
		implements ISeamJavaComponentDeclaration {

	protected String className = null;
	protected ScopeType scopeType = ScopeType.UNSPECIFIED;
	protected boolean stateful = false;
	protected boolean entity = false;
	protected int precedence = SeamComponentPrecedenceType.DEFAULT;
	
	protected IType type;

	protected Set<IBijectedAttribute> bijectedAttributes = new HashSet<IBijectedAttribute>();
	protected Set<ISeamComponentMethod> componentMethods = new HashSet<ISeamComponentMethod>();
	protected Set<IRole> roles = new HashSet<IRole>();
	
	public void setType(IType type) {
		this.type = type;
	}
	
	public ScopeType getScope() {
		return scopeType;
	}
	
	public void setScope(String scope) {
		if(scope == null || scope.length() == 0) {
			scopeType = ScopeType.UNSPECIFIED;
		} else {
			scopeType = ScopeType.valueOf(scope.toUpperCase());
		}
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
			if(a.isOfType(type)) {
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
			if(a.isOfType(type)) {
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
	
	public void setStateful(boolean b) {
		stateful = b;
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

	/**
	 * Merges loaded data into currently used declaration.
	 * If changes were done returns a list of changes. 
	 * @param d
	 * @return list of changes
	 */
	public List<Change> merge(SeamComponentDeclaration d) {
		List<Change> changes = super.merge(d);
		SeamJavaComponentDeclaration jd = (SeamJavaComponentDeclaration)d;
		if(!stringsEqual(className, jd.className)) {
			changes = Change.addChange(changes, new Change(this, "class", className, jd.className));
			className = jd.className;
		}
		if(scopeType != jd.scopeType) {
			changes = Change.addChange(changes, new Change(this, "scope", scopeType, jd.scopeType));
			scopeType = jd.scopeType;
		}
		if(precedence != jd.precedence) {
			changes = Change.addChange(changes, new Change(this, "precedence", precedence, jd.precedence));
			precedence = jd.precedence;
		}

		if(type != jd.type) type = jd.type;
		if(stateful != jd.stateful) {
			changes = Change.addChange(changes, new Change(this, "stateful", stateful, jd.stateful));
			stateful = jd.stateful;
		}
		if(entity != jd.entity) {
			changes = Change.addChange(changes, new Change(this, "entity", entity, jd.entity));
			entity = jd.entity;
		}
		Change children = new Change(this, null, null, null);

		//TODO do real merge and add changes to children
		this.bijectedAttributes = jd.bijectedAttributes;
		this.componentMethods = jd.componentMethods;
		this.roles = jd.roles;		

		changes = Change.addChange(changes, children);
		
		return changes;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamJavaComponentDeclaration#getPrecedence()
	 */
	public int getPrecedence() {
		return precedence;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamJavaComponentDeclaration#setPrecedence(org.jboss.tools.seam.core.SeamComponentPrecedenceType)
	 */
	public void setPrecedence(int precedence) {
		this.precedence = precedence;
	}

	public void setScope(ValueInfo value) {
		attributes.put(ISeamXmlComponentDeclaration.SCOPE, value);
		setScope(value == null ? null : value.getValue());
	}

	public void setPrecedence(ValueInfo value) {
		attributes.put(ISeamXmlComponentDeclaration.PRECEDENCE, value);
		try {
			setPrecedence(value == null ? 0 : Integer.parseInt(value.getValue()));
		} catch (NumberFormatException e) {
			setPrecedence(-1); //error value
			//ignore - exact value is stored in ValueInfo
		}
	}

}
