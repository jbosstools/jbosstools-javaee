package org.jboss.tools.seam.pages.xml.model.refactoring;

import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.filesystems.impl.FolderImpl;
import org.jboss.tools.common.model.refactoring.RefactoringHelper;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.seam.pages.xml.SeamPagesXMLMessages;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.handlers.RenameViewSupport;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramHelper;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;

public class SeamPagesRenamePageConfigChange extends CompositeChange {
	XModelObject object;
	String newName;
	
	String oldText;
	String newText;
	XModelObject[] fs = new XModelObject[0];	
	
	Properties replacements = new Properties();

	public SeamPagesRenamePageConfigChange(XModelObject object, String newName) {
		super(SeamPagesXMLMessages.PAGES_CONFIG_CHANGES);
		this.object = object;
		this.newName = newName;
		replacements.clear();
		oldText = XModelObjectLoaderUtil.getResourcePath(object);
		if(!EclipseResourceUtil.hasNature(object.getModel(), "org.jboss.tools.jsf.jsfnature")) {
			IFile file = (IFile)object.getAdapter(IFile.class);
			if(file != null) {
				IPath root = getRootPath(file.getProject());
				if(root.isPrefixOf(file.getFullPath())) {
					oldText = file.getFullPath().removeFirstSegments(root.segmentCount()).toString();
					if(!oldText.startsWith("/")) oldText = "/" + oldText;
				}
				
			}
		}
		int i = oldText.lastIndexOf("/");
		newText = oldText.substring(0, i + 1) + newName;
		replacements.setProperty(oldText, newText);
		addChanges();
	}

	IPath getRootPath(IProject project) {		
		IVirtualComponent component = ComponentCore.createComponent(project);
		if(component == null) return null;
		return component.getRootFolder().getWorkspaceRelativePath();
	}

	private void addChanges() {
		if(object == null) return;
		XModelObject fso = FileSystemsHelper.getFileSystems(object.getModel());
		if(fso == null) return;
		ArrayList<XModelObject> list = new ArrayList<XModelObject>();
		collectFiles(list, fso);
		fs = list.toArray(new XModelObject[0]);
		addChanges(fs);
	}
	
	private void addChanges(XModelObject[] objects) {
		for (int i = 0; i < objects.length; i++) {
			int c = getChildren().length; 
			RefactoringHelper.addChanges(objects[i], replacements, this);
			if(c == getChildren().length) {
				final XModelObject gs = findGroup(objects[i]);
				if(gs != null) {
					add(new Change() {
						public String getName() {
							return SeamPagesXMLMessages.UPDATE_REFERENCE_TO_PAGE + gs.getAttributeValue(SeamPagesConstants.ATTR_PATH);
						}
						public void initializeValidationData(IProgressMonitor pm) {
						}
						public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
							return null;
						}
						public Change perform(IProgressMonitor pm) throws CoreException {
							return null;
						}
						public Object getModifiedElement() {
							return null;
						}
					});
				}				
			}
		}
	}
	
	XModelObject findGroup(XModelObject f) {
		XModelObject diagram = SeamPagesDiagramStructureHelper.instance.getDiagram(f);
		if(diagram == null) return null;
		XModelObject[] is = SeamPagesDiagramStructureHelper.instance.getItems(diagram);
		for (int i = 0; i < is.length; i++) {
			String path = is[i].getAttributeValue(SeamPagesConstants.ATTR_PATH);
			if(path != null && path.equals(oldText)) return is[i];
		}
		return null;
	}

	private void collectFiles(ArrayList<XModelObject> list, XModelObject object) {
		if(object.getFileType() == XModelObject.SYSTEM) {
			if(object.getModelEntity().getName().equals("FileSystemJar")) {
				return;
			}
		}
		if(object.getFileType() == XModelObject.FILE) {
			if(object.getModelEntity().getName().startsWith(SeamPagesConstants.ENT_FILE_SEAM_PAGE)) {
				if(!isOverlapped(object)) list.add(object);
			}
		} else {
			if("true".equals(object.get("overlapped"))) return;
			XModelObject[] cs = object.getChildren();
			for (XModelObject o: cs) {
				collectFiles(list, o);
			}
		}
	}
	protected boolean isOverlapped(XModelObject object) {
		XModelObject p = object.getParent();
		while(p != null && !"true".equals(p.get("overlapped"))) p = p.getParent();
		return (p != null);
	}

	public Change perform(IProgressMonitor pm) throws CoreException {
		XModelObject parent = object.getParent();
		if(parent instanceof FolderImpl) {
			((FolderImpl)parent).update();
		}
		for (int i = 0; i < fs.length; i++) {
			performChangeInFile(fs[i]);
		}
		for (int i = 0; i < fs.length; i++) {
			if(fs[i].isModified()) {
				XActionInvoker.invoke("SaveActions.Save", fs[i], null);
			}
		}
		return null;
	}

	private void performChangeInFile(XModelObject f) throws XModelException {
		replaceViewIDs(f, oldText, newText);
		XModelObject g = findGroup(f);
		if(g != null) {
			SeamPagesDiagramHelper h = SeamPagesDiagramHelper.getHelper(SeamPagesDiagramStructureHelper.instance.getDiagram(f));
			h.addUpdateLock(this);
			try {
				RenameViewSupport.replace((ReferenceObject)g, oldText, newText);
			} finally {
				h.removeUpdateLock(this);
				h.updateDiagram();
			}
		}
	}

	static String[] ATTRIBUTES = {"no conversation view id", "login view id"};

	static void replaceViewIDs(XModelObject f, String oldText, String newText) throws XModelException {
		if(oldText == null || oldText.length() == 0) return;
		for (int i = 0; i < ATTRIBUTES.length; i++) {
			if(f.getModelEntity().getAttribute(ATTRIBUTES[i]) != null) {
				String v = f.getAttributeValue(ATTRIBUTES[i]);
				if(oldText.equals(v)) f.getModel().editObjectAttribute(f, ATTRIBUTES[i], newText);
			}
		}
		if(f.getModelEntity().getName().startsWith(SeamPagesConstants.ENT_FILE_SEAM_PAGES)) {
			XModelObject[] cs = f.getChildrenForSave();
			for (int i = 0; i < cs.length; i++) replaceViewIDs(cs[i], oldText, newText);
		}
		
	}

}
