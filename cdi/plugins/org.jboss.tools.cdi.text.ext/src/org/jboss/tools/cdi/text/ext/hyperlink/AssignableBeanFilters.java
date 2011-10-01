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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IProducer;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AssignableBeanFilters {

	public static interface Filter {
		public void filter(Set<IBean> beans);
	}

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
			ALL_OPTIONS[id] = this;
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
			if(id == OPTION_ELIMINATED_AMBIGUOUS) filter = new EliminatedAmbiguousFilter();
			if(!isEnabled() && filter != null) {
				filter.filter(beans);
			}
			for (Checkbox c: children) {
				c.filter(beans);
			}
		}
	}

	public static Checkbox[] ALL_OPTIONS = new Checkbox[8];

	public static Checkbox ROOT = new Checkbox(0, "", null);
	
	static {
		Checkbox unavailable = ROOT.add(OPTION_UNAVAILABLE_BEANS, "Unavailable Beans", null);
		unavailable.add(OPTION_DECORATOR, "@Decorator", new DecoratorFilter());
		unavailable.add(OPTION_INTERCEPTOR, "@Interceptor", new InterceptorFilter());
		unavailable.add(OPTION_UNSELECTED_ALTERNATIVE, "Unselected @Alternative", new UnselectedAlternativeFilter());
		unavailable.add(OPTION_PRODUCER_IN_UNAVAILABLE_BEAN, "@Produces in unavailable bean", new ProducerFilter());
		unavailable.add(OPTION_SPECIALIZED_BEAN, "Specialized beans", new SpecializedBeanFilter());
		ROOT.add(OPTION_ELIMINATED_AMBIGUOUS, "Eliminated ambiguous", new EliminatedAmbiguousFilter());
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

	public static class EliminatedAmbiguousFilter implements Filter {
		public void filter(Set<IBean> beans) {
			Set<IBean> eligible = new HashSet<IBean>(beans);
			for (int i = OPTION_UNAVAILABLE_BEANS + 1; i < OPTION_ELIMINATED_AMBIGUOUS; i++) {
				Filter f = ALL_OPTIONS[i].filter;
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
