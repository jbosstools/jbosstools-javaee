/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.jboss.tools.seam.internal.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.seam.core.BeanType;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.IValueInfo;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamComponentMethodType;
import org.jboss.tools.seam.core.SeamComponentPrecedenceType;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.event.Change;

public class SeamJavaComponentDeclaration extends SeamComponentDeclaration
		implements ISeamJavaComponentDeclaration {

	public static final String PATH_OF_STATEFUL = "stateful"; //$NON-NLS-1$

	protected String className = null;
	protected ScopeType scopeType = ScopeType.UNSPECIFIED;
	Map<BeanType, IValueInfo> types = null;
	protected int precedence = SeamComponentPrecedenceType.DEFAULT;
	
	protected IType type;

	protected Set<IBijectedAttribute> bijectedAttributes = new HashSet<IBijectedAttribute>();
	protected Set<ISeamComponentMethod> componentMethods = new HashSet<ISeamComponentMethod>();
	protected Set<IRole> roles = new HashSet<IRole>();
	
	public void setType(IType type) {
		this.type = type;
	}
	
	public ScopeType getScope() {
		if(scopeType != null && scopeType != ScopeType.UNSPECIFIED) {
			return scopeType;
		}
		if(isEntity() || isStateful()) {
			return ScopeType.CONVERSATION;
		}
		if(isOfType(BeanType.STATELESS) || isOfType(BeanType.MESSAGE_DRIVEN)) {
			return ScopeType.STATELESS;
		}
		return ScopeType.EVENT;
	}
	
	public void setScope(String scope) {
		if(scope == null || scope.length() == 0) {
			scopeType = ScopeType.UNSPECIFIED;
		} else {
			try {
				scopeType = ScopeType.valueOf(scope.toUpperCase());
			} catch (IllegalArgumentException e) {
				scopeType = ScopeType.UNSPECIFIED;
			}
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
		adopt(attribute);
	}

	public void addMethod(ISeamComponentMethod method) {
		componentMethods.add(method);
		adopt(method);
	}

	public void addRole(IRole role) {
		roles.add(role);
		adopt(role);
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
		Set<String> names = null;
		for(ISeamComponentMethod a: getMethods()) {
			if(a.isOfType(type)) {
				if(result == null) {
					result = new HashSet<ISeamComponentMethod>();
					names = new HashSet<String>();
				}
				result.add(a);
				if(a.getSourceMember() != null) {
					names.add(a.getSourceMember().getElementName());
				}
			}
		}
		
		ISeamJavaComponentDeclaration superDeclaration = getSuperDeclaration();
		if(superDeclaration != null) {
			Set<ISeamComponentMethod> s = superDeclaration.getMethodsByType(type);
			if(s != null) for(ISeamComponentMethod a: s) {
				if(a.getSourceMember() == null) continue;
				String n = a.getSourceMember().getElementName();
				if(names != null && names.contains(n)) continue;
				if(result == null) {
					result = new HashSet<ISeamComponentMethod>();
					names = new HashSet<String>();
				}
				result.add(a);
				if(a.getSourceMember() != null) {
					names.add(a.getSourceMember().getElementName());
				}
			}
		}
		return result;
	}
	
	public ISeamJavaComponentDeclaration getSuperDeclaration() {
		if(type == null) return null;
		String superclass = null;
		try {
			superclass = type.getSuperclassName();
		} catch (JavaModelException e) {
			return null;
		}
		if(superclass == null || "java.lang.Object".equals(superclass)) {
			return null;
		}
		if(superclass.indexOf('.') < 0) {
			superclass = EclipseJavaUtil.resolveType(type, superclass);
		}
		SeamProject p = (SeamProject)getSeamProject();
		return p == null ? null : p.getAllJavaComponentDeclarations().get(superclass);
	}

	public Set<IRole> getRoles() {
		return roles;
	}
	
	public boolean isOfType(BeanType type) {
		return types != null && types.containsKey(type);
	}
	
	public boolean isEntity() {
		return isOfType(BeanType.ENTITY);
	}

	public boolean isStateful() {
		return isOfType(BeanType.STATEFUL);
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

	public IMember getSourceMember() {
		return type;
	}

	/**
	 * Merges loaded data into currently used declaration.
	 * If changes were done returns a list of changes. 
	 * @param d
	 * @return list of changes
	 */
	public List<Change> merge(SeamObject s) {
		List<Change> changes = super.merge(s);
		SeamJavaComponentDeclaration jd = (SeamJavaComponentDeclaration)s;
		if(!stringsEqual(className, jd.className)) {
			changes = Change.addChange(changes, new Change(this, "class", className, jd.className)); //$NON-NLS-1$
			className = jd.className;
		}
		if(scopeType != jd.scopeType) {
			changes = Change.addChange(changes, new Change(this, "scope", scopeType, jd.scopeType)); //$NON-NLS-1$
			scopeType = jd.scopeType;
		}
		if(precedence != jd.precedence) {
			changes = Change.addChange(changes, new Change(this, "precedence", precedence, jd.precedence)); //$NON-NLS-1$
			precedence = jd.precedence;
		}

		if(type != jd.type) type = jd.type;
		if(!typesAreEqual(types, jd.types)) {
			changes = Change.addChange(changes, new Change(this, "types", types, jd.types)); //$NON-NLS-1$
		}
		this.types = jd.types;

		Change children = new Change(this, null, null, null);

		mergeComponentMethods(jd, children);
		mergeBijected(jd, children);
		mergeRoles(jd, children);

		changes = Change.addChange(changes, children);
		
		return changes;
	}
	
	public void mergeBijected(SeamJavaComponentDeclaration jd, Change children) {
		
		Map<Object, BijectedAttribute> bijectedMap = new HashMap<Object, BijectedAttribute>();
		for (IBijectedAttribute r: bijectedAttributes) bijectedMap.put(((SeamObject)r).getId(), (BijectedAttribute)r);
		
		for (IBijectedAttribute r: jd.bijectedAttributes) {
			BijectedAttribute loaded = (BijectedAttribute)r;
			BijectedAttribute current = (BijectedAttribute)bijectedMap.remove(loaded.getId());
			if(current == null) {
				adopt(loaded);
				bijectedAttributes.add(loaded);
				ISeamProject p = getSeamProject();
				if(p != null && loaded.isContextVariable()) {
					p.addVariable(loaded);
				}
				Change change = new Change(this, null, null, loaded);
				children.addChildren(Change.addChange(null, change));
			} else {
				boolean wasOut = current.isContextVariable();
				boolean isOut = loaded.isContextVariable();
				List<Change> rc = current.merge(loaded);
				if(rc != null) children.addChildren(rc);
				if(wasOut != isOut) {
					ISeamProject p = getSeamProject();
					if(p != null) {
						if(wasOut) p.removeVariable(current);
						if(isOut) p.addVariable(current);
					}
				}
			}
		}
		
		for (BijectedAttribute r: bijectedMap.values()) {
			bijectedAttributes.remove(r);
			ISeamProject p = getSeamProject();
			if(p != null) p.removeVariable(r);
			Change change = new Change(this, null, r, null);
			children.addChildren(Change.addChange(null, change));
		}

	}

	public void mergeRoles(SeamJavaComponentDeclaration jd, Change children) {
		
		Map<Object, Role> roleMap = new HashMap<Object, Role>();
		for (IRole r: roles) roleMap.put(((SeamObject)r).getId(), (Role)r);
		
		for (IRole r: jd.roles) {
			Role loaded = (Role)r;
			Role current = (Role)roleMap.remove(loaded.getId());
			if(current == null) {
				adopt(loaded);
				roles.add(loaded);
				ISeamProject p = getSeamProject();
				if(p != null) p.addVariable(loaded);
				Change change = new Change(this, null, null, loaded);
				children.addChildren(Change.addChange(null, change));
			} else {
				List<Change> rc = current.merge(loaded);
				if(rc != null) children.addChildren(rc);
			}
		}
		
		for (Role r: roleMap.values()) {
			roles.remove(r);
			ISeamProject p = getSeamProject();
			if(p != null) p.removeVariable(r);
			Change change = new Change(this, null, r, null);
			children.addChildren(Change.addChange(null, change));
		}

	}

	public void mergeComponentMethods(SeamJavaComponentDeclaration jd, Change children) {
		
		Map<Object, SeamComponentMethod> methodsMap = new HashMap<Object, SeamComponentMethod>();
		for (ISeamComponentMethod r: componentMethods) methodsMap.put(((SeamObject)r).getId(), (SeamComponentMethod)r);
		
		for (ISeamComponentMethod r: jd.componentMethods) {
			SeamComponentMethod loaded = (SeamComponentMethod)r;
			SeamComponentMethod current = (SeamComponentMethod)methodsMap.remove(loaded.getId());
			if(current == null) {
				adopt(loaded);
				componentMethods.add(loaded);
				Change change = new Change(this, null, null, loaded);
				children.addChildren(Change.addChange(null, change));
			} else {
				List<Change> rc = current.merge(loaded);
				if(rc != null) children.addChildren(rc);
			}
		}
		
		for (SeamComponentMethod r: methodsMap.values()) {
			componentMethods.remove(r);
			Change change = new Change(this, null, r, null);
			children.addChildren(Change.addChange(null, change));
		}

	}

	boolean typesAreEqual(Map<BeanType, IValueInfo> types1, Map<BeanType, IValueInfo> types2) {
		if(types1 == null || types2 == null) return types2 == types1;
		if(types1.size() != types2.size()) return false;
		for (BeanType t : types1.keySet()) {
			if(!types2.containsKey(t)) return false;
		}
		return true;
		
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

	public void setScope(IValueInfo value) {
		attributes.put(ISeamXmlComponentDeclaration.SCOPE, value);
		setScope(value == null ? null : value.getValue());
	}
	
	static final Map<String, Integer> NAMED_PRECEDENCES = new HashMap<String, Integer>();
	
	static {
		NAMED_PRECEDENCES.put("Install.BUILT_IN", Integer.valueOf(0)); //$NON-NLS-1$
		NAMED_PRECEDENCES.put("Install.FRAMEWORK", Integer.valueOf(10)); //$NON-NLS-1$
		NAMED_PRECEDENCES.put("Install.APPLICATION", Integer.valueOf(20)); //$NON-NLS-1$
		NAMED_PRECEDENCES.put("Install.DEPLOYMENT", Integer.valueOf(30)); //$NON-NLS-1$
		NAMED_PRECEDENCES.put("Install.MOCK", Integer.valueOf(40)); //$NON-NLS-1$
	}

	public void setPrecedence(IValueInfo value) {
		attributes.put(ISeamXmlComponentDeclaration.PRECEDENCE, value);
		String p = value.getValue();
		if(p == null) {
			setPrecedence(0);
			return;
		}
		Integer i = NAMED_PRECEDENCES.get(p);
		if(i == null) try {
			i = Integer.parseInt(p);
		} catch (NumberFormatException e) {
			//ignore - exact value is stored in ValueInfo
		}
		setPrecedence(i == null ? -1 : i.intValue());
	}
	
	public void setTypes(Map<BeanType, IValueInfo> types) {
		this.types = types;		
	}

	public Set<ISeamContextVariable> getDeclaredVariables() {
		Set<ISeamContextVariable> set = new HashSet<ISeamContextVariable>();
		set.addAll(roles);
		for (IBijectedAttribute a : bijectedAttributes) {
			if(a.isContextVariable()) set.add(a);
		}
		return set;
	}
	
	public void open() {
		if(type == null) return;
		try {
			JavaUI.openInEditor(type);
		} catch (Exception e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}

	public SeamJavaComponentDeclaration clone() throws CloneNotSupportedException {
		SeamJavaComponentDeclaration c = (SeamJavaComponentDeclaration)super.clone();
		if(types != null) {
			c.types = new HashMap<BeanType, IValueInfo>();
			c.types.putAll(types);
		}
		c.bijectedAttributes = new HashSet<IBijectedAttribute>();
		for (IBijectedAttribute a : bijectedAttributes) {
			c.addBijectedAttribute(a.clone());
		}
		c.componentMethods = new HashSet<ISeamComponentMethod>();
		for (ISeamComponentMethod m : componentMethods) {
			c.addMethod(m.clone());
		}
		c.roles = new HashSet<IRole>();
		for (IRole r : roles) {
			c.addRole(r.clone());
		}
		return c;
	}

}
