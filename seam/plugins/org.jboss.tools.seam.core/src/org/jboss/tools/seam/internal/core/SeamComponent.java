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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.tools.common.model.project.ext.event.Change;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamPackage;
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
	
	SeamPackage projectPackage = null;
	SeamPackage scopePackage = null;
	
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

	public ITextSourceReference getLocationFor(String path) {
		ISeamJavaComponentDeclaration javaDeclaration = getJavaDeclaration();
		if(javaDeclaration != null) return javaDeclaration.getLocationFor(path);
		Set<ISeamXmlComponentDeclaration> xml = getXmlDeclarations();
		for(ISeamXmlComponentDeclaration d: xml) {
			if(d.getLocationFor(path) != null) {
				return d.getLocationFor(path);
			}
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
		if(xml.size() > 0) return 20;
		ISeamJavaComponentDeclaration javaDeclaration = getJavaDeclaration();
		if(javaDeclaration != null) return javaDeclaration.getPrecedence();
		return 20;
	}
	
	static Set<ISeamComponentMethod> EMPTY = new HashSet<ISeamComponentMethod>();

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getMethods()
	 */
	public Set<ISeamComponentMethod> getMethods() {
		ISeamJavaComponentDeclaration javaDeclaration = getJavaDeclaration();
		return (javaDeclaration == null) ? EMPTY : javaDeclaration.getMethods();
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
	 * @see org.jboss.tools.seam.core.ISeamComponent#isStateless()
	 */
	public boolean isStateless() {
		ISeamJavaComponentDeclaration javaDeclaration = getJavaDeclaration();
		return (javaDeclaration != null) && javaDeclaration.isStateless();
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
		if(javaDeclarations.isEmpty()) return null;
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
		((SeamComponentDeclaration)declaration).bindToComponent(this);
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
		((SeamComponentDeclaration)declaration).unbindFromComponent(this);
	}

	@Override
	public String toString() {		
		return "SeamComponent: " + getName(); //$NON-NLS-1$
	}
	
	public SeamComponent clone() throws CloneNotSupportedException {
		return this;
	}
	
	public void setProjectPackage(SeamPackage p) {
		projectPackage = p;
	}
	
	public void setScopePackage(SeamPackage p) {
		scopePackage = p;
	}
	
	public List<Change> removeFromModel(List<Change> changes) {
		SeamScope pc = (SeamScope)getParent();
		if(pc != null) {
			pc.removeComponent(this);
			changes = Change.addChange(changes, new Change(pc, null, this, null));
		}
		if(scopePackage != null) {
			removeFrom(scopePackage);
			changes = Change.addChange(changes, new Change(scopePackage, null, this, null));
			scopePackage = null;
		}
		if(projectPackage != null) {
			removeFrom(projectPackage);
			changes = Change.addChange(changes, new Change(projectPackage, null, this, null));
			projectPackage = null;
		}
		
		return changes;
	}
	
	public List<Change> revalidate(List<Change> changes) {
		SeamScope pc = (SeamScope)getParent();
		SeamScope pn = (SeamScope)getSeamProject().getScope(getScope());
		if(pc != pn) {
			if(pc != null) {
				pc.removeComponent(this);
				changes = Change.addChange(changes, new Change(pc, null, this, null));
			}
			setParent(pn);
			pn.addComponent(this);
			changes = Change.addChange(changes, new Change(pn, null, null, this));
			if(scopePackage != null) {
				removeFrom(scopePackage);
				changes = Change.addChange(changes, new Change(scopePackage, null, this, null));
				scopePackage = null;
			}
		}
		if(scopePackage != null && !scopePackage.getQualifiedName().equals(SeamPackageUtil.getPackageName(this))) {
			removeFrom(scopePackage);
			changes = Change.addChange(changes, new Change(scopePackage, null, this, null));
			scopePackage = null;
		}
		if(scopePackage == null) {
			pn.validatePackage(this);
		}
		if(projectPackage != null && !projectPackage.getQualifiedName().equals(SeamPackageUtil.getPackageName(this))) {
			removeFrom(projectPackage);
			changes = Change.addChange(changes, new Change(projectPackage, null, this, null));
			projectPackage = null;
		}
		if(projectPackage == null) {
			((SeamProject)getSeamProject()).validatePackage(this);
		}
		return changes;
	}
	
	private void removeFrom(ISeamPackage p) {
		p.getComponents().remove(this);
		while(p != null && p.getComponents().size() + p.getPackages().size() == 0) {
			ISeamElement o = p.getParent();
			if(o instanceof ISeamPackage) {
				ISeamPackage q = (ISeamPackage)o;
				q.getPackages().remove(p);
				p = q;
			} else if(o instanceof SeamScope) {
				SeamScope s = (SeamScope)o;
				s.removePackage(p);
				p = null;
			} else if(o instanceof SeamProject) {
				SeamProject project = (SeamProject)o;
				project.removePackage(p);
				p = null;
			}
		}
	}
}