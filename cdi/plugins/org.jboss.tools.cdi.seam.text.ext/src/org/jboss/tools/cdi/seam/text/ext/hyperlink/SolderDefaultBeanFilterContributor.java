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
package org.jboss.tools.cdi.seam.text.ext.hyperlink;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.seam.solder.core.CDISeamSolderDefaultBeanExtension;
import org.jboss.tools.cdi.text.ext.hyperlink.AssignableBeanFilters;
import org.jboss.tools.cdi.text.ext.hyperlink.AssignableBeanFilters.Filter;
import org.jboss.tools.cdi.text.ext.hyperlink.AssignableBeanFilters.FilterContributor;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SolderDefaultBeanFilterContributor implements FilterContributor {
	public static final int OPTION_DEFAULT_BEAN = 21;

	@Override
	public void contribute(AssignableBeanFilters filters, IInjectionPoint injectionPoint) {
		ICDIExtension ext = CDISeamSolderDefaultBeanExtension.getExtension(injectionPoint.getCDIProject().getNature());
		if(ext == null) {
			return;
		}
		CDISeamSolderDefaultBeanExtension defaultBeanExtension = (CDISeamSolderDefaultBeanExtension)ext;
		filters.add(0, OPTION_DEFAULT_BEAN, "Eliminated @DefaultBean", new DefaultBeanFilter(filters, injectionPoint, defaultBeanExtension));
	}
	
	class DefaultBeanFilter implements Filter {
		AssignableBeanFilters filters;
		IInjectionPoint injectionPoint;
		CDISeamSolderDefaultBeanExtension defaultBeanExtension;
		DefaultBeanFilter(AssignableBeanFilters filters, IInjectionPoint injectionPoint, CDISeamSolderDefaultBeanExtension defaultBeanExtension) {
			this.filters = filters;
			this.injectionPoint = injectionPoint;
			this.defaultBeanExtension = defaultBeanExtension;
		}

		@Override
		public void filter(Set<IBean> beans) {
			Set<IBean> eligible = new HashSet<IBean>(beans);
			for (int i = AssignableBeanFilters.OPTION_UNAVAILABLE_BEANS + 1; i < AssignableBeanFilters.OPTION_ELIMINATED_AMBIGUOUS; i++) {
				Filter f = filters.getFilter(i);
				if(f != null) {
					f.filter(eligible);
				}
			}
			boolean hasNonDefault = false;
			for (IBean b: eligible) {
				if(b.isEnabled() && !defaultBeanExtension.isBeanDefault(b)) {
					hasNonDefault = true;
				}
			}
			if(hasNonDefault) {
				Iterator<IBean> it = beans.iterator();
				while(it.hasNext()) {
					IBean bean = it.next();
					if(!eligible.contains(bean) || !defaultBeanExtension.isBeanDefault(bean)) continue;
					it.remove();
				}
			}
		}
		
	}

}
