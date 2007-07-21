 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.validation;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.text.FastJavaPartitionScanner;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;

/**
 * EL Validator
 * @author Alexey Kazakov
 */
public class SeamELValidator extends SeamValidator {

	private SeamELCompletionEngine engine= new SeamELCompletionEngine();
	private IJavaProject javaProject = null;

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.validation.SeamValidator#validate(java.util.Set)
	 */
	@Override
	public IStatus validate(Set<IFile> changedFiles) throws ValidationException {
		// TODO Incremental validation
		validateAll();
		return OK_STATUS;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.validation.SeamValidator#validateAll()
	 */
	@Override
	public IStatus validateAll() throws ValidationException {
		reporter.removeAllMessages(this);
		SeamELValidationHelper vlh = (SeamELValidationHelper)coreHelper;
		Collection files = vlh.getAllFilesForValidation();
		for (Object file : files) {
			if(file instanceof IFile && !reporter.isCancelled()) {
				validateFile((IFile)file);
			}
		}
		return OK_STATUS;
	}

	private void validateFile(IFile file) {
		String ext = file.getFileExtension();
		String content = null;
		try {
			content = FileUtil.readStream(file.getContents());
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError("Error validating Seam EL", e);
			return;
		}
		if(ext.equalsIgnoreCase("java")) {
			validateJava(file, content);
		} else {
			validateDom(file, content);
		}
	}

	private void validateJava(IFile file, String content) {
		try {
			FastJavaPartitionScanner scaner = new FastJavaPartitionScanner();
			Document document = new Document(content);
			scaner.setRange(document, 0, document.getLength());
			IToken token = scaner.nextToken();
			while(token!=null && token!=Token.EOF && !reporter.isCancelled()) {
				if(IJavaPartitions.JAVA_STRING.equals(token.getData())) {
					int length = scaner.getTokenLength();
					int offset = scaner.getTokenOffset();
					String value = document.get(offset, length);
					if(value.indexOf('{')>-1) {
						validateString(file, value, offset);
					}
				}
				token = scaner.nextToken();
			}
		} catch (BadLocationException e) {
			SeamCorePlugin.getDefault().logError("Error validating Seam EL", e);
		}
	}

	private void validateDom(IFile file, String content) {
		IStructuredModel model = null;		
		try {
			model = StructuredModelManager.getModelManager().getModelForRead(file);
			if (model instanceof IDOMModel) {
				IDOMModel domModel = (IDOMModel) model;
    			IStructuredDocument structuredDoc = domModel.getStructuredDocument();
    			IStructuredDocumentRegion curNode = structuredDoc.getFirstStructuredDocumentRegion();
    			while (curNode !=null && !reporter.isCancelled()) {
    				if (curNode.getFirstRegion().getType() == DOMRegionContext.XML_TAG_OPEN) {
    					validateNodeContent(file, curNode, DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE);
    				}				
    				if (curNode.getFirstRegion().getType() == DOMRegionContext.XML_CONTENT) {
    					validateNodeContent(file, curNode, DOMRegionContext.XML_CONTENT);
    				}
    				curNode = curNode.getNext();
    			}
			}
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError("Error validating Seam EL", e);
        } catch (IOException e) {
        	SeamCorePlugin.getDefault().logError("Error validating Seam EL", e);
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}

		return;
	}

	private void validateNodeContent(IFile file, IStructuredDocumentRegion node, String regionType) {
		ITextRegionList regions = node.getRegions();
		for(int i=0; i<regions.size(); i++) {
			ITextRegion region = regions.get(i);
			if(region.getType() == regionType) {
				String text = node.getFullText(region);
				if(text.indexOf("{")>-1) {
					int offset = node.getStartOffset() + region.getStart();
					validateString(file, text, offset);
				}
			}
		}
	}

	/**
	 * @param offset - offset of string in file
	 * @param length - length of string in file
	 */
	private void validateString(IFile file, String string, int offset) {
		Set<EL> els = new HashSet<EL>();
		String localString = string;
		while(!reporter.isCancelled()) {
			int startEl = localString.indexOf("#{");
			int endEl = -1;
//			if(startEl==-1) {
//				startEl = localString.indexOf("${");
//			}
			if(startEl>-1) {
				endEl = localString.indexOf('}', startEl);
				if(endEl>-1) {
					String value = localString.substring(startEl+2, endEl);
					int os = offset + startEl + 2;
					int ln = value.length();
					els.add(new EL(value, ln, os));
					localString = localString.substring(endEl);
					offset = offset + endEl;
					continue;
				}
			}
			break;
		}

		for(EL el: els) {
			if(!validateEl(file, el)) {
				// Mark EL
				addError(INVALID_EXPRESSION_MESSAGE_ID, SeamPreferences.INVALID_EXPRESSION, new String[]{el.getValue()}, el.getLength(), el.getOffset(), file, MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
			}
		}
	}

	private boolean validateEl(IFile file, EL el) {
		try {
			String exp = el.value;
//			int offset = exp.length()-1;
//			String prefix= SeamELCompletionEngine.getPrefix(exp, offset);
//			prefix = (prefix == null ? "" : prefix);

			String prefix = el.value;
			int possition = prefix.length();

			// TODO ?
			List<String> suggestions = engine.getCompletions(project, file, exp, prefix, possition, true);

			if (suggestions != null && suggestions.size() > 0) {
				return true;
			}
		} catch (BadLocationException e) {
			SeamCorePlugin.getDefault().logError("Error validating Seam EL", e);
		} catch (StringIndexOutOfBoundsException e) {
			SeamCorePlugin.getDefault().logError("Error validating Seam EL", e);
		}
		return false;
	}

	private IJavaProject getJavaProject() {
		if(javaProject == null) {
			javaProject = coreHelper.getJavaProject();
		}
		return javaProject;
	}

	public static class EL {
		private String value;
		private int length;
		private int offset;

		public EL(String value, int length, int offset) {
			this.value = value;
			this.length = length;
			this.offset = offset;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}

		public int getOffset() {
			return offset;
		}

		public void setOffset(int offset) {
			this.offset = offset;
		}
	}
}