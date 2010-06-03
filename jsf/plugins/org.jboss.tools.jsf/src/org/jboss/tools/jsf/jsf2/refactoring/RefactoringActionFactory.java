package org.jboss.tools.jsf.jsf2.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.jboss.tools.jsf.jsf2.refactoring.action.rename.CompositeAttributeRenameDescriptor;
import org.jboss.tools.jsf.jsf2.refactoring.action.rename.IRenameDescriptor;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;

@SuppressWarnings("restriction")
public class RefactoringActionFactory {

	public static IRenameDescriptor createRenameDescriptor(
			StructuredTextEditor editorPart) {
		ISelection selection = editorPart.getEditorSite()
				.getSelectionProvider().getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.size() > 1) {
				return null;
			}
			Object object = structuredSelection.getFirstElement();
			if (object instanceof IDOMAttr) {
				return createRenameDescriptor((IDOMAttr) object, editorPart
						.getTextViewer(), ((IFileEditorInput) editorPart
						.getEditorInput()).getFile());
			}
		}
		return null;
	}

	public static IRenameDescriptor createRenameDescriptor(IDOMAttr attr,
			ISourceViewer sourceViewer, IFile file) {
		IRenameDescriptor renameDescriptor = null;
		String uri = createJSF2URIFromPath(file.getParent().getFullPath());
		if (JSF2ResourceUtil.JSF2_URI_PREFIX.equals(uri)) {
			return renameDescriptor;
		}
		if (!attr.getOwnerElement().getNamespaceURI().trim().equalsIgnoreCase(
				JSF2ResourceUtil.JSF2_URI_PREFIX)) {
			return renameDescriptor;
		}
		if (!"attribute".equals(attr.getOwnerElement().getLocalName())) { //$NON-NLS-1$
			return renameDescriptor;
		}
		int offset = sourceViewer.getSelectedRange().x;
		if (offset > attr.getValueRegionStartOffset()
				&& offset < attr.getValueRegionStartOffset()
						+ attr.getValueRegionText().length()) {
			renameDescriptor = new CompositeAttributeRenameDescriptor(attr, file, uri);
		}
		return renameDescriptor;
	}
	
	private static String createJSF2URIFromPath(IPath path) {
		StringBuilder uri = new StringBuilder(""); //$NON-NLS-1$
		String[] segments = path.segments();
		if (segments.length > 3) {
			if (segments[2].equals("resources")) { //$NON-NLS-1$
				for (int i = 3; i < segments.length; i++) {
					uri.append("/" + segments[i]); //$NON-NLS-1$
				}
			}
		}
		uri.insert(0, JSF2ResourceUtil.JSF2_URI_PREFIX);
		return uri.toString();
	}

}
