package org.jboss.tools.seam.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IMember;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.SeamComponentMethodType;

public class SeamComponentMethod implements ISeamComponentMethod {
	boolean create = false;
	boolean destroy = false;
	
	IMember javaSource = null;
	IResource resource = null;

	public boolean isCreate() {
		return create;
	}
	
	public void setCreate(boolean b) {
		create = b;
	}

	public boolean isOfType(SeamComponentMethodType type) {
		if(type == SeamComponentMethodType.CREATE) return isCreate();
		if(type == SeamComponentMethodType.DESTROY) return isDestroy();
		return false;
	}

	public boolean isDestroy() {
		return destroy;
	}
	
	public void setDestroy(boolean b) {
		destroy = b;
	}

	public IMember getSourceMember() {
		return javaSource;
	}
	
	public void setSourceMember(IMember javaSource) {
		this.javaSource = javaSource;
	}

	public int getLength() {
		return 0;
	}

	public IResource getResource() {
		if(resource == null && javaSource != null) {
			resource = javaSource.getResource();
		}
		return resource;
	}

	public int getStartPosition() {
		return 0;
	}

}
