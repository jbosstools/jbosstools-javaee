package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;

public class Dependencies {
	protected Map<IPath, Set<IPath>> direct = new HashMap<IPath, Set<IPath>>();
	protected Map<IPath, Set<IPath>> reverse = new HashMap<IPath, Set<IPath>>();
	
	public Dependencies() {}

	public void addDependency(IPath source, IPath target) {
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

	public void clean() {
		direct.clear();
		reverse.clear();
	}

	public void clean(IPath path) {
		Set<IPath> ps = reverse.remove(path);
		if(ps != null) {
			for (IPath p: ps) {
				Set<IPath> ps1 = direct.get(p);
				if(ps1 != null) ps1.remove(path);
			}
		}
	}

	public Set<IPath> getDirectDependencies(IPath path) {
		return direct.get(path);
	}

}
