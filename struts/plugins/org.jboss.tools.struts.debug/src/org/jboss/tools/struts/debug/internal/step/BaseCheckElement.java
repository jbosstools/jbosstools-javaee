/*
 * Created on 24.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.jboss.tools.struts.debug.internal.step;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.model.IStackFrame;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.struts.debug.internal.StrutsDebugPlugin;
import org.jboss.tools.struts.debug.internal.condition.ICondition;
import org.jboss.tools.struts.model.ReferenceObjectImpl;

abstract public class BaseCheckElement implements ICheckElement {

	private IResource resource;
	private String modelPath;

	protected BaseCheckElement(XModelObject xObject) {
		this(EclipseResourceUtil.getResource(xObject), xObject.getPath());	
	}

	protected BaseCheckElement(IResource resource, String modelPath) {
		this.resource = resource;
		this.modelPath = modelPath;
	}

	public IResource getResource() {
		return resource;
	}

	public String getModelPath() {
		return modelPath;
	}

	abstract public ICondition getCondition();

	public boolean canStop(IStackFrame[] stackFrames) {
		boolean result = false;
		try {
			result = getCondition().check(stackFrames);
		} catch(Exception e) {
			StrutsDebugPlugin.log(e);
		}
		StrutsDebugPlugin.log(this.getClass().getName() + ".canStop() = " + result);

		return result;		
	}
	
	protected final String getAttributeValue(XModelObject object, String attribute) {
		XModelObject reference = object;
		if(object instanceof ReferenceObjectImpl) {
			reference = ((ReferenceObjectImpl)object).getReference();
		}
		return reference == null ? null : reference.getAttributeValue(attribute);		
	}
}