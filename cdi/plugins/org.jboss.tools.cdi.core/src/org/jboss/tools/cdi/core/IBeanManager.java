/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core;

import java.util.Set;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IType;

/**
 * @author Alexey Kazakov
 */
public interface IBeanManager {

	   /**
	    * Returns the set of beans which match the given EL name
	    * 
	    * @param name the name used to restrict the beans matched
	    * @return the matched beans
	    */
	   public Set<IBean> getBeans(String name);

	   /**
	    * Returns the set of beans which match the given required type and bindings.
	    * 
	    * @param beanType the type of the beans to be resolved
	    * @param bindings the bindings used to restrict the matched beans. If no
	    *           bindings are passed to getBeans(), the default binding @Current
	    *           is assumed.
	    * @return the matched beans
	    */
	   public Set<IBean> getBeans(IType beanType, IAnnotation... bindings);

}