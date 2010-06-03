package org.jboss.tools.jsf.jsf2.refactoring.action.rename;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.internal.corext.refactoring.changes.TextChangeCompatibility;
import org.eclipse.jdt.internal.corext.refactoring.tagging.INameUpdating;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.GroupCategorySet;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RenameProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.jboss.tools.jsf.jsf2.refactoring.core.StructuredChanges;
import org.jboss.tools.jsf.jsf2.refactoring.core.StructuredTextFileChange;
import org.jboss.tools.jsf.jsf2.util.JSF2ComponentUtil;
import org.w3c.dom.Attr;
import org.eclipse.ltk.core.refactoring.GroupCategory;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

@SuppressWarnings("restriction")
public class CompositeAttributeRenameProcessor extends RenameProcessor
		implements INameUpdating {

	private static final Pattern PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_.]*+"); //$NON-NLS-1$
	private static final GroupCategorySet CATEGORY_COMPOSITE_ATTR_RENAME = new GroupCategorySet(
			new GroupCategory(
					"org.jboss.tools.jsf.jsf2.refactoring.rename.composite.attr.type", "Rename composite attribute changes", "Changes to rename composite attribute")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	public static String IDENTIFIER = "org.jboss.tools.jsf.jsf2.refactor.compositeattrrenameprocessor"; //$NON-NLS-1$
	private String newAttrName;
	private String currentAttrName;
	private IProject project;
	private String URI;
	private IFile baseFile;
	private IDOMAttr attrToRename;

	public void setCurrentAttrName(String currentAttrName) {
		this.currentAttrName = currentAttrName;
	}

	public CompositeAttributeRenameProcessor(IProject project) {
		this.setProject(project);
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws CoreException,
			OperationCanceledException {
		return null;
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return null;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		StructuredChanges changes = new StructuredChanges(
				"Composite attribute name changes"); //$NON-NLS-1$
		StructuredTextFileChange baseFileChange = new StructuredTextFileChange(baseFile.getFullPath().toOSString(), baseFile);
		ReplaceEdit edit = new ReplaceEdit(attrToRename.getValueRegionStartOffset()+1, attrToRename.getValue().length(), getNewElementName());
		TextChangeCompatibility.addTextEdit(baseFileChange, "Rename composite attribute name", edit, CATEGORY_COMPOSITE_ATTR_RENAME); //$NON-NLS-1$
		changes.add(baseFileChange);
		Map<IFile, List<IDOMNode>> nodesMap = JSF2ComponentUtil
				.findCompositeComponentsWithURI(getProject(), getURI());
		Set<Entry<IFile, List<IDOMNode>>> entries = nodesMap.entrySet();
		for (Entry<IFile, List<IDOMNode>> entry : entries) {
			StructuredTextFileChange fileChange = createFileChange(entry
					.getKey(), entry.getValue());
			if (fileChange != null) {
				changes.add(fileChange);
			}
		}
		return changes;
	}

	private StructuredTextFileChange createFileChange(IFile file,
			List<IDOMNode> nodeList) {
		StructuredTextFileChange fileChange = null;
		for (IDOMNode domNode : nodeList) {
			if (domNode instanceof IDOMElement) {
				IDOMElement element = (IDOMElement) domNode;
				Attr attr = element.getAttributeNode(getCurrentElementName());
				if (attr instanceof IDOMAttr) {
					IDOMAttr domAttr = (IDOMAttr) attr;
					if (fileChange == null) {
						fileChange = new StructuredTextFileChange(file
								.getFullPath().toOSString(), file);
					}
					ReplaceEdit edit = new ReplaceEdit(
							domAttr.getStartOffset(), domAttr.getName()
									.length(), getNewElementName());
					TextChangeCompatibility
							.addTextEdit(
									fileChange,
									"Rename Compoiste Attribute", edit, CATEGORY_COMPOSITE_ATTR_RENAME); //$NON-NLS-1$
				}
			}
		}
		return fileChange;
	}

	@Override
	public Object[] getElements() {
		return null;
	}

	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public String getProcessorName() {
		return "Rename Composite Attribute"; //$NON-NLS-1$
	}

	@Override
	public boolean isApplicable() throws CoreException {
		return true;
	}

	@Override
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status,
			SharableParticipants sharedParticipants) throws CoreException {
		return null;
	}

	public RefactoringStatus checkNewElementName(String newName)
			throws CoreException {
		RefactoringStatus status = null;
		Matcher matcher = PATTERN.matcher(getNewElementName());
		int includings = 0;
		String matchString = null;
		while (matcher.find()) {
			includings++;
			matchString = matcher.group();
		}
		if (includings != 1 || !matchString.equals(getNewElementName())) {
			status = RefactoringStatus.createFatalErrorStatus("Attribute name is not valid"); //$NON-NLS-1$
		}
		return status;
	}

	public String getCurrentElementName() {
		return currentAttrName;
	}

	public Object getNewElement() throws CoreException {
		return null;
	}

	public String getNewElementName() {
		return newAttrName;
	}

	public void setNewElementName(String newName) {
		this.newAttrName = newName;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public IProject getProject() {
		return project;
	}

	public void setURI(String uRI) {
		URI = uRI;
	}

	public String getURI() {
		return URI;
	}

	public void setBaseFile(IFile baseFile) {
		this.baseFile = baseFile;
	}

	public IFile getBaseFile() {
		return baseFile;
	}

	public void setAttrToRename(IDOMAttr attrToRename) {
		this.attrToRename = attrToRename;
	}

	public IDOMAttr getAttrToRename() {
		return attrToRename;
	}

}
