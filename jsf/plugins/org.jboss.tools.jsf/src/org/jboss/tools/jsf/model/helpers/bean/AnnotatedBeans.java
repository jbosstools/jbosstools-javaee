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
package org.jboss.tools.jsf.model.helpers.bean;

import java.util.*;
import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.JSFModelPlugin;

public class AnnotatedBeans {
	
	public static AnnotatedBeans getAnnotatedBeans(XModel model) {
		AnnotatedBeans bs = (AnnotatedBeans)model.getManager("AnnotatedBeans");
		if(bs == null) {
			bs = new AnnotatedBeans();
			bs.setModel(model);
			model.addManager("AnnotatedBeans", bs);
		}
		return bs;
	}
	
	XModel model;
	IProject project;
	Map<String,XModelObject> beans = new HashMap<String,XModelObject>();
	
	String annotationType = "org.jboss.seam.annotations.Name";
	
	private AnnotatedBeans() {}
	
	public void setModel(XModel model) {
		this.model = model;
		try {
			update();
		} catch (Exception e) {
			JSFModelPlugin.log(e);
		}
	}
	
	public XModelObject[] getBeans() {
		return (XModelObject[])beans.values().toArray(new XModelObject[0]);
	}
	
	public XModelObject findBean(String name) {
		return (XModelObject)beans.get(name);
	}
	
	public void update() throws Exception {
		// TODO: this method is highly ineffective. We should do this in a builder incrementally!
		project = EclipseResourceUtil.getProject(model.getRoot());
		IResource[] src = EclipseResourceUtil.getJavaSourceRoots(project);
		if(src == null) {
			return;
		}
		IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
		
		ASTRequestorImpl requestor = new ASTRequestorImpl();
		Set<ICompilationUnit> units = new HashSet<ICompilationUnit>();
		
		for (IResource resource : src) {
			IPackageFragmentRoot pfr = javaProject.getPackageFragmentRoot(resource);
			collectCompilationUnits(pfr, units);
		}
		
		ICompilationUnit[] us = units.toArray(new ICompilationUnit[0]);		
		// This triggers a full resolve of all types, even inside jars...NOT scalable at all!
		ASTParser.newParser(AST.JLS3).createASTs(us, new String[0], requestor, null);
		
		Map map = requestor.getBeans();
		Set<String> set = new HashSet<String>();
		Iterator it = map.keySet().iterator();
		while(it.hasNext()) {
			String name = it.next().toString();
			String cls = map.get(name).toString();
			XModelObject o = beans.get(name);
			if(o == null) {
				o = model.createModelObject("JSFManagedBean", null);
				o.setAttributeValue("managed-bean-name", name);
				o.setAttributeValue("managed-bean-class", cls);
				beans.put(name, o);
			} else {
				o.setAttributeValue("managed-bean-class", cls);
			}
			set.add(name);
		}
		// TODO: why does the beans set have to double checkked for duplicates ?
		it = beans.keySet().iterator();
		while(it.hasNext()) {
			if(!set.contains(it.next())) it.remove();
		}
	}
	
	void collectCompilationUnits(IParent parent, Set<ICompilationUnit> units) throws Exception {
		if(parent instanceof IPackageFragmentRoot 
			|| parent instanceof IPackageFragment) {
			IJavaElement[] cs = parent.getChildren();
			for (int i = 0; i < cs.length; i++) {
				if(cs[i] instanceof ICompilationUnit) {
					units.add((ICompilationUnit)cs[i]);
				} else if(cs[i] instanceof IParent) {
					collectCompilationUnits((IParent)cs[i], units);
				}
			}
		}
	}
	
	class ASTRequestorImpl extends ASTRequestor {
		private ASTVisitorImpl visitor = new ASTVisitorImpl();
		private Map<String, String> beans = new HashMap<String, String>();

		public Map<String, String> getBeans() {
			return beans;
		}
		
		public void acceptAST(ICompilationUnit source, CompilationUnit ast) {
			visitor.name = null;
			
			try {
				IType[] ts = source.getTypes();
				if(ts != null && ts.length > 0) {
					visitor.type = ts[0];
				}
			} catch (Exception e) {
				//ignore
			}
			ast.accept(visitor);
			if(visitor.name != null && visitor.type != null) {
				String n = visitor.type.getElementName();
				n = getResolvedType(visitor.type, n);
				beans.put(visitor.name, n);
			}			
		}
	}
	
	class ASTVisitorImpl extends ASTVisitor {
		IType type;
		String name = null;
		public boolean visit(SingleMemberAnnotation node) {
			if(!checkAnnotationType(node)) return false;
			checkExpression(node.getValue());
			return true;
		}
		public boolean visit(NormalAnnotation node) {
			if(!checkAnnotationType(node)) return false;
			List vs = node.values();
			if(vs != null) for (int i = 0; i < vs.size(); i++) {
				MemberValuePair p = (MemberValuePair)vs.get(i);
				if("value".equals(p.getName().getIdentifier())) {
					checkExpression(p.getValue());
				}
			}
			return true;
		}
		
		boolean checkAnnotationType(Annotation node) {
			Name nm = node.getTypeName();
			if(nm instanceof SimpleName) {
				SimpleName sn = (SimpleName)nm;
				String n = sn.getIdentifier();
				if(type != null) {
					n = getResolvedType(type, n);
				}
				if(!annotationType.equals(n)) return false;
			} else if(nm instanceof QualifiedName) {
				QualifiedName qn = (QualifiedName)nm;
				if(!qn.getFullyQualifiedName().equals(annotationType)) return false;
				//improve
			} else {
				return false;
			}
			return true;
		}
		
		void checkExpression(Expression exp) {
			if(exp instanceof StringLiteral) {
				name = ((StringLiteral)exp).getLiteralValue();
			}
		}

		public boolean visit(Block node) {
			return false;
		}
		public boolean visit(MethodDeclaration node) {
			return false;
		}
	}
	
	String getResolvedType(IType type, String n) {
		try {
			String[][] rs = type.resolveType(n);
			if(rs != null && rs.length > 0) {
				return (rs[0][0].length() == 0) ? rs[0][1] : rs[0][0] + "." + rs[0][1];
			}
		} catch (Exception e) {
			//ignore
		}
		return n;
	}

}
