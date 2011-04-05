package org.jboss.tools.cdi.core.test.extension;

import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedTypeFeature;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;

public class CDIExtensionImpl implements ICDIExtension, IProcessAnnotatedTypeFeature {

	public Object getAdapter(Class adapter) {
		if(adapter == IProcessAnnotatedTypeFeature.class) {
			return this;
		}
		return null;
	}

	public void processAnnotatedType(TypeDefinition typeDefinition,
			DefinitionContext context) {
		
	}

}
