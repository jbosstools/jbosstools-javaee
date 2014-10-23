/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.common.util.UniquePaths;

public class Dependencies {
	protected Map<IPath, Set<IPath>> direct = new HashMap<IPath, Set<IPath>>();
	protected Map<IPath, Set<IPath>> reverse = new HashMap<IPath, Set<IPath>>();
	
	public Dependencies() {}

	public synchronized void addDependency(IPath source, IPath target) {
		source = UniquePaths.getInstance().intern(source);
		target = UniquePaths.getInstance().intern(target);
		Set<IPath> ps = direct.get(source);
		if(ps == null) {
			ps = new HashSet<IPath>();
			direct.put(source, ps);
		}
		ps.add(target);
		
		ps = reverse.get(target);
		if(ps == null) {
			ps = new HashSet<IPath>();
			reverse.put(target, ps);
		}
		ps.add(source);
	}

	public synchronized void clean() {
		direct.clear();
		reverse.clear();
	}

	public synchronized void clean(IPath path) {
		Set<IPath> ps = reverse.remove(path);
		if(ps != null) {
			for (IPath p: ps) {
				Set<IPath> ps1 = direct.get(p);
				if(ps1 != null) ps1.remove(path);
			}
		}
	}

	public synchronized Set<IPath> getDirectDependencies(IPath path) {
		return direct.get(path);
	}

	synchronized void copyTo(Dependencies other) {
		other.direct.putAll(direct);
		other.reverse.putAll(reverse);
	}

}
