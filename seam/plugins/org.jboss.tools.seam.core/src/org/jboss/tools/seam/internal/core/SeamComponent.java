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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamPropertiesDeclaration;
import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamComponentMethodType;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamComponent extends SeamObject implements ISeamComponent {
	
	protected String name = null;

	Set<ISeamComponentDeclaration> allDeclarations = new HashSet<ISeamComponentDeclaration>();
	Set<ISeamJavaComponentDeclaration> javaDeclarations = new HashSet<ISeamJavaComponentDeclaration>();
	Set<ISeamXmlComponentDeclaration> xmlDeclarations = new HashSet<ISeamXmlComponentDeclaration>();
	Set<ISeamPropertiesDeclaration> propertyDeclarations = new HashSet<ISeamPropertiesDeclaration>();
	
	public SeamComponent () {
	}
	
	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getBijectedAttributes()
	 */
	public Set<IBijectedAttribute> getBijectedAttributes() {
		ISeamJavaComponentDeclaration javaDeclaration = getJavaDeclaration();
		return (javaDeclaration == null) ? null : javaDeclaration.getBijectedAttributes();
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getBijectedAttributesByName(java.lang.String)
	 */
	public Set<IBijectedAttribute> getBijectedAttributesByName(String name) {
		ISeamJavaComponentDeclaration javaDeclaration = getJavaDeclaration();
		return (javaDeclaration == null) ? null : javaDeclaration.getBijectedAttributesByName(name);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getBijectedAttributesByType(org.jboss.tools.seam.core.BijectedAttributeType)
	 */
	public Set<IBijectedAttribute> getBijectedAttributesByType(
			BijectedAttributeType type) {
		ISeamJavaComponentDeclaration javaDeclaration = getJavaDeclaration();
		return (javaDeclaration == null) ? null : javaDeclaration.getBijectedAttributesByType(type);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getClassName()
	 */
	public String getClassName() {
		ISeamJavaComponentDeclaration javaDeclaration = getJavaDeclaration();
		if(javaDeclaration != null) return javaDeclaration.getClassName();
		Set<ISeamXmlComponentDeclaration> xml = getXmlDeclarations();
		for(ISeamXmlComponentDeclaration d: xml) {
			if(d.getClassName() != null && d.getClassName().length() > 0) return d.getClassName();
		}
		return null;
	}

	/**
	 * 
	 */
	public int getPrecedence() {
		Set<ISeamXmlComponentDeclaration> xml = getXmlDeclarations();
		for(ISeamXmlComponentDeclaration d: xml) {
			String s = d.getPrecedence();
			if(s == null || s.length() == 0) continue;
			try {
				return Integer.valueOf(s);
			} catch (NumberFormatException e) {
				//ignore here
			}
		}
		ISeamJavaComponentDeclaration javaDeclaration = getJavaDeclaration();
		if(javaDeclaration != null) return javaDeclaration.getPrecedence();
		return 20;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getMethods()
	 */
	public Set<ISeamComponentMethod> getMethods() {
		ISeamJavaComponentDeclaration javaDeclaration = getJavaDeclaration();
		return (javaDeclaration == null) ? null : javaDeclaration.getMethods();
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getMethodsByType(org.jboss.tools.seam.core.SeamComponentMethodType)
	 */
	public Set<ISeamComponentMethod> getMethodsByType(
			SeamComponentMethodType type) {
		ISeamJavaComponentDeclaration javaDeclaration = getJavaDeclaration();
		return (javaDeclaration == null) ? null : javaDeclaration.getMethodsByType(type);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getRoles()
	 */
	public Set<IRole> getRoles() {
		ISeamJavaComponentDeclaration javaDeclaration = getJavaDeclaration();
		return (javaDeclaration == null) ? null : javaDeclaration.getRoles();
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#isEntity()
	 */
	public boolean isEntity() {
		ISeamJavaComponentDeclaration javaDeclaration = getJavaDeclaration();
		return (javaDeclaration != null) && javaDeclaration.isEntity();
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#isStateful()
	 */
	public boolean isStateful() {
		ISeamJavaComponentDeclaration javaDeclaration = getJavaDeclaration();
		return (javaDeclaration != null) && javaDeclaration.isStateful();
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamContextVariable#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamContextVariable#getScope()
	 */
	public ScopeType getScope() {
		ScopeType scopeType = null;
		for (ISeamXmlComponentDeclaration d: xmlDeclarations) {
			String s = d.getScopeAsString();
			if(s != null && s.length() > 0) {
				scopeType = d.getScope();
			}
			if(scopeType != null && scopeType != ScopeType.UNSPECIFIED) break;
		}
		if(scopeType == null || scopeType == ScopeType.UNSPECIFIED) {
			ISeamJavaComponentDeclaration java = getJavaDeclaration();
			if(java != null) scopeType = java.getScope();
		}
		if(scopeType == null) scopeType = ScopeType.UNSPECIFIED;
		
		return scopeType;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamContextVariable#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamContextVariable#setScope(org.jboss.tools.seam.core.ScopeType)
	 */
	public void setScope(ScopeType type) {
		//TODO
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getProperties(java.lang.String)
	 */
	public List<ISeamProperty> getProperties(String propertyName) {
		List<ISeamProperty> list = new ArrayList<ISeamProperty>();
		Collection<ISeamProperty> ps = getProperties();
		for (ISeamProperty p: ps) {
			if(propertyName.equals(p.getName())) list.add(p);
		}
		return list;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getProperties()
	 */
	public Collection<ISeamProperty> getProperties() {
		Set<ISeamProperty> ps = new HashSet<ISeamProperty>();
		Set<ISeamXmlComponentDeclaration> xml = getXmlDeclarations();
		for (ISeamXmlComponentDeclaration d: xml) {
			ps.addAll(d.getProperties());
		}
		Set<ISeamPropertiesDeclaration> pd = getPropertiesDeclarations();
		for (ISeamPropertiesDeclaration d: pd) {
			ps.addAll(d.getProperties());
		}
		return ps;
	}

	public Set<ISeamComponentDeclaration> getAllDeclarations() {
		return allDeclarations;
	}

	public ISeamJavaComponentDeclaration getJavaDeclaration() {
		if(javaDeclarations.size() == 0) return null;
		return javaDeclarations.iterator().next();
	}

	public Set<ISeamPropertiesDeclaration> getPropertiesDeclarations() {
		return propertyDeclarations;
	}

	public Set<ISeamXmlComponentDeclaration> getXmlDeclarations() {
		return xmlDeclarations;
	}
	
	public void addDeclaration(ISeamComponentDeclaration declaration) {
		if(allDeclarations.contains(declaration)) return;
		allDeclarations.add(declaration);
		if(name.equals(declaration.getName())) {
			adopt(declaration);
		}
		if(declaration instanceof ISeamJavaComponentDeclaration) {
			javaDeclarations.add((ISeamJavaComponentDeclaration)declaration);
		} else if(declaration instanceof ISeamXmlComponentDeclaration) {
			xmlDeclarations.add((ISeamXmlComponentDeclaration)declaration);
		} else if(declaration instanceof ISeamPropertiesDeclaration) {
			propertyDeclarations.add((ISeamPropertiesDeclaration)declaration);
		}
	}
	
	public void removeDeclaration(ISeamComponentDeclaration declaration) {
		if(!allDeclarations.contains(declaration)) return;
		allDeclarations.remove(declaration);
		if(declaration instanceof ISeamJavaComponentDeclaration) {
			javaDeclarations.remove(declaration);
		} else if(declaration instanceof ISeamXmlComponentDeclaration) {
			xmlDeclarations.remove(declaration);
		} else if(declaration instanceof ISeamPropertiesDeclaration) {
			propertyDeclarations.remove(declaration);
		}
	}

	@Override
	public String toString() {		
		return "SeamComponent: " + getName();
	}
}
