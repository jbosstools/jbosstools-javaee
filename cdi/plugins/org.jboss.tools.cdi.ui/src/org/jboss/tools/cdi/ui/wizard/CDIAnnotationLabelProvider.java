package org.jboss.tools.cdi.ui.wizard;

import org.eclipse.jface.viewers.LabelProvider;
import org.jboss.tools.cdi.core.ICDIAnnotation;

public class CDIAnnotationLabelProvider extends LabelProvider {

	public String getText(Object element) {
		if(element instanceof ICDIAnnotation) {
			return ((ICDIAnnotation)element).getSourceType().getFullyQualifiedName();
		}
		return super.getText(element);
	}

}
