/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IExcluded;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class Excluded implements IExcluded {
	static List<String> EMPTY = new ArrayList<String>();

	private Filter filter = NoTypeFilter.INSTANCE;
	private List<String> typesAvailable = EMPTY;
	private List<String> typesNotAvailable = EMPTY;

	private IPath source = null;

	public Excluded(IPath source) {
		this.source = source;
	}

	@Override
	public IPath getSource() {
		return source;
	}

	@Override
	public boolean isExcluded(String typeName) {
		return filter.isExcluded(typeName);
	}

	public boolean isEnabled(ICDIProject project) {
		for (String typeName: typesAvailable) {
			IType type = project.getNature().getType(typeName);
			if(type == null || !type.exists()) {
				return false;
			}
		}
		for (String typeName: typesNotAvailable) {
			IType type = project.getNature().getType(typeName);
			if(type != null && type.exists()) {
				return false;
			}
		}
		return true;
	}

	public void setFilter(String filter) {
		if(filter.equals("**")) {
			this.filter = AnyTypeFilter.INSTANCE;
		} else if(filter.equals("*")) {
			this.filter = DefaultTypeFilter.INSTANCE;
		} else if(filter.endsWith("**")) {
			this.filter = new ParentPackageFilter();
			this.filter.setValue(filter.substring(0, filter.length() - 2));
		} else if(filter.endsWith("*")) {
			this.filter = new CurrentPackageFilter();
			this.filter.setValue(filter.substring(0, filter.length() - 1));
		} else {
			this.filter = new ExactTypeFilter();
			this.filter.setValue(filter);
		}		
	}

	public void addAvailableType(String typeName) {
		if(typesAvailable == EMPTY) {
			typesAvailable = new ArrayList<String>();
		}
		typesAvailable.add(typeName);
	}

	public void addNotAvailableType(String typeName) {
		if(typesNotAvailable == EMPTY) {
			typesNotAvailable = new ArrayList<String>();
		}
		typesNotAvailable.add(typeName);
	}

	static abstract class Filter {
		protected String value;

		public Filter() {}
	
		public void setValue(String value) {
			this.value = value;
		}
		public abstract boolean isExcluded(String typeName);

		public int hashCode() {
			return getClass().hashCode() + (value == null ? 0 : 10 * value.hashCode());
		}

		public boolean equals(Object o) {
			if(!(o instanceof Filter)) {
				return false;
			}
			Filter other = (Filter)o;
			if(getClass() != other.getClass()) {
				return false;
			}
			return value == null ? other.value == null : value.equals(other.value);
		}
	} 

	static class NoTypeFilter extends Filter {
		static Filter INSTANCE = new AnyTypeFilter();
		public boolean isExcluded(String typeName) {
			return false;
		}
	}

	static class AnyTypeFilter extends Filter {
		static Filter INSTANCE = new AnyTypeFilter();
		public boolean isExcluded(String typeName) {
			return true;
		}
	}

	static class DefaultTypeFilter extends Filter {
		static Filter INSTANCE = new DefaultTypeFilter();
		public boolean isExcluded(String typeName) {
			return typeName.indexOf('.') < 0;
		}
	}

	static class ExactTypeFilter extends Filter {
		public boolean isExcluded(String typeName) {
			return value.equals(typeName);
		}
	}

	static class CurrentPackageFilter extends Filter {
		public boolean isExcluded(String typeName) {
			return typeName.startsWith(value) && typeName.lastIndexOf('.') == value.length() - 1;
		}
	}

	static class ParentPackageFilter extends Filter {
		public boolean isExcluded(String typeName) {
			return typeName.startsWith(value);
		}
	}
}
