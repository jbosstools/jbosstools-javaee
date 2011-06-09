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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.internal.core.impl.AbstractBeanElement;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.IConfigDefinition;
import org.jboss.tools.common.text.ITextSourceReference;

public class SeamConfigInjectedPointHyperlinkDetector extends GenericInjectedPointHyperlinkDetector {

	protected void findInjectedBeans(CDICoreNature nature, IJavaElement element, int offset, IFile file, ArrayList<IHyperlink> hyperlinks){
		ICDIProject cdiProject = nature.getDelegate();
		
		if(cdiProject == null) {
			return;
		}
		
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		
		Set<IInjectionPoint> injectionPoints = GenericInjectedPointHyperlinkDetector.findInjectionPoints(beans, element, offset);
		if(injectionPoints.isEmpty()) {
			return;
		}
		
		Set<IBean> resultBeanSet2 = new HashSet<IBean>();

		for (IInjectionPoint injectionPoint: injectionPoints) {
			Set<IBean> resultBeanSet = cdiProject.getBeans(true, injectionPoint);
			for (IBean b: resultBeanSet) {
				if(b instanceof AbstractBeanElement) {
					AbstractMemberDefinition def = ((AbstractBeanElement)b).getDefinition();
					if(def instanceof IConfigDefinition) {
						ITextSourceReference ref = def.getOriginalDefinition();
						if(ref != null) {
							resultBeanSet2.add(b);
						}
					}
				}				
			}
		}

		List<IBean> resultBeanList = CDIUtil.sortBeans(resultBeanSet2);		
		if(resultBeanList.size() == 1) {
			hyperlinks.add(new SeamConfigInjectedPointHyperlink(region, resultBeanList.get(0), document, true));
		} else if(resultBeanList.size() > 0) {
			hyperlinks.add(new SeamConfigInjectedPointListHyperlink(region, resultBeanList, viewer, document));
		}
	}

}
