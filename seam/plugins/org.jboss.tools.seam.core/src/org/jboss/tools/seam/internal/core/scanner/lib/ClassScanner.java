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
package org.jboss.tools.seam.internal.core.scanner.lib;

import java.lang.annotation.Annotation;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.java.ASTVisitorImpl;

/**
 * Loads seam components from Class object.
 *  
 * @author Viacheslav Kabanovich
 */
public class ClassScanner {
	
	/**
	 * Checks if class may be a source of seam components. 
	 * @param f
	 * @return
	 */
	public boolean isLikelyComponentSource(Class<?> cls) {
		return cls != null && isSeamAnnotatedClass(cls);
	}
	
	/**
	 * Loads seam components from class.
	 * Returns object that contains loaded components or null;
	 * @param type
	 * @param cls
	 * @param path
	 * @return
	 */
	public LoadedDeclarations parse(IType type, Class<?> cls, IPath path) {
		if(!isLikelyComponentSource(cls)) return null;
		LoadedDeclarations ds = new LoadedDeclarations();
		
		return ds;		
	}
	
	/**
	 * Check if class has at least one seam annotation.
	 * @param cls
	 * @return
	 */
	boolean isSeamAnnotatedClass(Class<?> cls) {
		if(cls == null || cls.isInterface()) return false;
		Annotation[] as = cls.getAnnotations();
		for (int i = 0; i < as.length; i++) {
			Class<?> acls = as[i].annotationType();
			if(acls.getName().startsWith(ASTVisitorImpl.SEAM_ANNOTATION_TYPE_PREFIX)) {
				return true;
			}
		}
		return false;
	}

}
