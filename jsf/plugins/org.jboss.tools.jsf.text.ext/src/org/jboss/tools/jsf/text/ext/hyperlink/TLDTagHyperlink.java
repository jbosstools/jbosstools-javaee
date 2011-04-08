package org.jboss.tools.jsf.text.ext.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.jst.web.kb.internal.taglib.AbstractComponent;
import org.jboss.tools.jst.web.kb.taglib.ITagLibrary;

public class TLDTagHyperlink extends AbstractHyperlink {
	private AbstractComponent tag;
	private XModelObject xmodelObject;
	private String xmodelObjectName = null;
	private IFile file = null;
	private IRegion region;
	
	public TLDTagHyperlink(AbstractComponent tag, IRegion region){
		this.tag = tag;
		ITagLibrary tagLib = tag.getTagLib();
		this.region = region;
		IResource r = tagLib.getResource();
		if(r instanceof IFile) {
			IFile file = (IFile)r;
			if(file.getFullPath() != null && file.getFullPath().toString().endsWith(".jar")) {
				Object id = tag.getId();
				if(id instanceof XModelObject) {
					xmodelObject = (XModelObject)id;
					FileAnyImpl fai = null;
					XModelObject f = xmodelObject;
					while(f != null && f.getFileType() != XModelObject.FILE) f = f.getParent();
					if(f instanceof FileAnyImpl) fai = (FileAnyImpl)f;
					xmodelObjectName = FileAnyImpl.toFileName(fai);
				}
			}
		}
		
		
	}
	
	public AbstractComponent getComponent(){
		return tag;
	}
	
	protected IRegion doGetHyperlinkRegion(int offset) {
		return region;
	}

	@Override
	protected void doHyperlink(IRegion region) {
		if(xmodelObject != null){
			int q = FindObjectHelper.findModelObject(xmodelObject, FindObjectHelper.IN_EDITOR_ONLY);
			if(q == 1) {
				openFileFailed();
			}
		} else if(file != null){
			IEditorPart part = null;
			part = openFileInEditor(file);
			if(part == null) {
				openFileFailed();
			}
		}else
			openFileFailed();
	}

	@Override
	public String getHyperlinkText() {
		if(xmodelObjectName != null)
			return "Open "+xmodelObjectName;
		else if(file != null)
			return "Open "+file.getName();
		else
			return "Open...";
	}

}
