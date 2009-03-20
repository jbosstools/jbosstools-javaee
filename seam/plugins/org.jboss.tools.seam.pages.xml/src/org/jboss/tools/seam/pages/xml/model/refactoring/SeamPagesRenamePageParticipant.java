package org.jboss.tools.seam.pages.xml.model.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

public class SeamPagesRenamePageParticipant extends RenameParticipant {
	public static final String PARTICIPANT_NAME="seampages-RenamePageParticipant";
	XModelObject object;

	public SeamPagesRenamePageParticipant() {}

	protected boolean initialize(Object element) {
		if(!(element instanceof IFile)) return false;
		IFile f = (IFile)element;
		object = EclipseResourceUtil.getObjectByResource(f);
		if(object == null) object = EclipseResourceUtil.createObjectForResource(f);
		if(object == null) return false;
		String entity = object.getModelEntity().getName();
		if(".FileJSP.FileHTML.FileXHTML.FileGIF.FileCSS.FileAny.FileAnyLong.".indexOf("." + entity + ".") < 0) return false;
		return true;
	}

	public String getName() {
		return PARTICIPANT_NAME;
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException {
		return null;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		if (!pm.isCanceled()) {
			String newName = getArguments().getNewName();
			SeamPagesRenamePageConfigChange change = new SeamPagesRenamePageConfigChange(object, newName);
			if(change.getChildren() == null || change.getChildren().length == 0) change = null;
			return change;
		}
		return null;
	}

}
