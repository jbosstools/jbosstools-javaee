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
package org.jboss.tools.cdi.text.ext.hyperlink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.text.ext.CDIExtensionsPlugin;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AssignableBeanFilters {

	public static interface Filter {
		public void filter(Set<IBean> beans);
	}

	public static interface FilterContributor {
		public void contribute(AssignableBeanFilters filters, IInjectionPoint injectionPoint);
	}
	
	static FilterContributor[] contributors = null;

	public static int OPTION_UNAVAILABLE_BEANS = 1;
	public static int OPTION_DECORATOR = 2;
	public static int OPTION_INTERCEPTOR = 3;
	public static int OPTION_UNSELECTED_ALTERNATIVE = 4;
	public static int OPTION_PRODUCER_IN_UNAVAILABLE_BEAN = 5;
	public static int OPTION_SPECIALIZED_BEAN = 6;
	public static int OPTION_ELIMINATED_AMBIGUOUS = 7;

	public static class Checkbox {
		int id;
		String label;
		Filter filter;
		boolean state = true;
		Checkbox(int id, String label, Filter filter) {
			this.id = id;
			this.label = label;
			this.filter = filter;
		}
		Checkbox parent = null;
		List<Checkbox> children = new ArrayList<Checkbox>();

		Checkbox add(int id, String label, Filter f) {
			Checkbox c = new Checkbox(id, label, f);
			c.parent = this;
			children.add(c);			
			return c;
		}
	
		public String toString() {
			return label;
		}
	
		public boolean isEnabled() {
			return state && (parent == null || parent.isEnabled());
		}
	
		public void filter(Set<IBean> beans) {
			if(!isEnabled() && filter != null) {
				filter.filter(beans);
			}
			for (Checkbox c: children) {
				c.filter(beans);
			}
		}
	}

	private Map<Integer, Checkbox> allOptions = new HashMap<Integer, Checkbox>();
	private Checkbox root = new Checkbox(0, "", null);

	public AssignableBeanFilters() {
		initContributors();
	}

	void initContributors() {
		if(contributors == null) {
			IConfigurationElement[] cs = Platform.getExtensionRegistry().getExtensionPoint("org.jboss.tools.cdi.text.ext.assignableBeanFilterContributors").getConfigurationElements();
			ArrayList<FilterContributor> s = new ArrayList<FilterContributor>();
			for (IConfigurationElement c: cs) {
				try {
					FilterContributor f = (FilterContributor)c.createExecutableExtension("class");
					s.add(f);
				} catch (CoreException e) {
					CDIExtensionsPlugin.getDefault().logError(e);
				}
			}
			contributors = s.toArray(new FilterContributor[s.size()]);
		}
	}
	
	public void init(IInjectionPoint point) {
		allOptions.clear();
		allOptions.put(root.id, root);
		add(0, OPTION_UNAVAILABLE_BEANS, "Unavailable Beans", null);
		add(OPTION_UNAVAILABLE_BEANS, OPTION_DECORATOR, "@Decorator", new DecoratorFilter());
		add(OPTION_UNAVAILABLE_BEANS, OPTION_INTERCEPTOR, "@Interceptor", new InterceptorFilter());
		add(OPTION_UNAVAILABLE_BEANS, OPTION_UNSELECTED_ALTERNATIVE, "Unselected @Alternative", new UnselectedAlternativeFilter());
		add(OPTION_UNAVAILABLE_BEANS, OPTION_PRODUCER_IN_UNAVAILABLE_BEAN, "@Produces in unavailable bean", new ProducerFilter());
		add(OPTION_UNAVAILABLE_BEANS, OPTION_SPECIALIZED_BEAN, "Specialized beans", new SpecializedBeanFilter());
		add(0, OPTION_ELIMINATED_AMBIGUOUS, "Eliminated ambiguous", new EliminatedAmbiguousFilter());
		for (FilterContributor c: contributors) {
			c.contribute(this, point);
		}
	}

	public void add(int parentId, int id, String label, Filter f) {
		Checkbox parent = getOption(parentId);
		if(parent == null) {
			parent = root;
		}
		Checkbox c = parent.add(id, label, f);
		allOptions.put(id, c);
	}

	public Checkbox getRoot() {
		return root;
	}

	public Map<Integer, Checkbox> getAllOptions() {
		return allOptions;
	}

	public Checkbox getOption(int id) {
		return allOptions.get(id);
	}

	public Filter getFilter(int id) {
		Checkbox c = getOption(id);
		return c == null ? null : c.filter;
	}

	public static class DecoratorFilter implements Filter {
		public void filter(Set<IBean> beans) {
			Iterator<IBean> it = beans.iterator();
			while(it.hasNext()) {
				IBean b = it.next();
				if(b.isAnnotationPresent(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME)) {
					it.remove();
				}
			}
		}
	}

	public static class InterceptorFilter implements Filter {
		public void filter(Set<IBean> beans) {
			Iterator<IBean> it = beans.iterator();
			while(it.hasNext()) {
				IBean b = it.next();
				if(b.isAnnotationPresent(CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME)) {
					it.remove();
				}
			}
		}
	}

	public static class UnselectedAlternativeFilter implements Filter {
		public void filter(Set<IBean> beans) {
			Iterator<IBean> it = beans.iterator();
			while(it.hasNext()) {
				IBean b = it.next();
				if(b.isAlternative() && !b.isSelectedAlternative()) {
					it.remove();
				}
			}
		}
	}

	public static class SpecializedBeanFilter implements Filter {
		public void filter(Set<IBean> beans) {
			Iterator<IBean> it = beans.iterator();
			Set<IBean> specialized = new HashSet<IBean>();
			while(it.hasNext()) {
				IBean b = it.next();
				IBean bean = b.getSpecializedBean();
				if(bean != null) {
					specialized.add(bean);
				}
			}
			beans.removeAll(specialized);
		}
	}

	public static class ProducerFilter implements Filter {
		public void filter(Set<IBean> beans) {
			Iterator<IBean> it = beans.iterator();
			while(it.hasNext()) {
				IBean b = it.next();
				if(b instanceof IProducer && ((IProducer) b).getClassBean() != null && !((IProducer) b).getClassBean().isEnabled()) {
					it.remove();
				}
			}
		}
	}

	public class EliminatedAmbiguousFilter implements Filter {

		public void filter(Set<IBean> beans) {
			Set<IBean> eligible = new HashSet<IBean>(beans);
			for (int i = OPTION_UNAVAILABLE_BEANS + 1; i < OPTION_ELIMINATED_AMBIGUOUS; i++) {
				Filter f = getFilter(i);
				if(f != null) {
					f.filter(eligible);
				}
			}
			boolean hasAlternative = false;
			for (IBean b: eligible) {
				if(b.isEnabled() && b.isAlternative() && b.isSelectedAlternative()) {
					hasAlternative = true;
				}
			}
			if(hasAlternative) {
				Iterator<IBean> it = beans.iterator();
				while(it.hasNext()) {
					IBean bean = it.next();
					if(!eligible.contains(bean) || bean.isAlternative()) continue;
					if(bean instanceof IProducer && bean instanceof IBeanMember) {
						IBeanMember p = (IBeanMember)bean;
						if(p.getClassBean() != null && p.getClassBean().isAlternative()) continue;
					}
					it.remove();
				}
			}
		}
		
	}
}
