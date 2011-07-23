/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.core.international.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.cdi.seam.core.international.IBundleModel;
import org.jboss.tools.common.model.XModelObject;

public class BundleModel implements IBundleModel {
	long timeStamp = 0;
	Map<String, BundleImpl> bundles = new HashMap<String, BundleImpl>();

	public BundleImpl getBundle(String name) {
		return bundles.get(name);
	}

	public void rebuild(Set<XModelObject> objects) {
		long t = computeTimeStamp(objects);
		if(t == timeStamp) return;
		timeStamp = t;
		bundles.clear();
		for (XModelObject o: objects) {
			addToBundle(getBundleName(o), o);
		}
	}

	private BundleImpl addToBundle(String name, XModelObject o) {
		BundleImpl b = getBundle(name);
		if(b == null) {
			b = new BundleImpl();
			b.setName(name);
		}
		b.addObject(o);
		return b;		
	}

	private String getBundleName(XModelObject o) {
		 IPath p = new Path(o.getPath());
		 p = p.removeFirstSegments(2);
		 String n = p.toString();		 
		 n = n.substring(0, n.lastIndexOf('.'));
		 if (n.indexOf('_') != -1)
			n = n.substring(0, n.indexOf('_'));
		 return n.replace('/', '.');
	}

	long computeTimeStamp(Set<XModelObject> objects) {
		long t = 0;
		for (XModelObject o: objects) {
			t += o.hashCode() + 713 * o.getTimeStamp();
		}
		return t;
	}

}
