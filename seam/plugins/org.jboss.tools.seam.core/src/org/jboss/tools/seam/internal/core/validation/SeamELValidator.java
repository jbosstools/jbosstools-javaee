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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.internal.ui.text.FastJavaPartitionScanner;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;
import org.jboss.tools.seam.internal.core.el.ELOperandToken;
import org.jboss.tools.seam.internal.core.el.ELToken;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;
import org.jboss.tools.seam.internal.core.el.SeamELOperandTokenizer;
import org.jboss.tools.seam.internal.core.el.SeamELTokenizer;
import org.jboss.tools.seam.internal.core.el.TypeInfoCollector;

/**
 * EL Validator
 * @author Alexey Kazakov
 */
public class SeamELValidator extends SeamValidator {

	protected static final String INVALID_EXPRESSION_MESSAGE_ID = "INVALID_EXPRESSION"; //$NON-NLS-1$
	protected static final String UNPAIRED_GETTER_OR_SETTER_MESSAGE_ID = "UNPAIRED_GETTER_OR_SETTER"; //$NON-NLS-1$

	protected static final String VALIDATING_EL_FILE_MESSAGE_ID = "VALIDATING_EL_FILE";

	private SeamELCompletionEngine engine= new SeamELCompletionEngine();
	private IJavaProject javaProject;

	public SeamELValidator(SeamValidatorManager validatorManager,
			SeamValidationHelper coreHelper, IReporter reporter,
			SeamValidationContext validationContext, ISeamProject project) {
		super(validatorManager, coreHelper, reporter, validationContext, project);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.validation.ISeamValidator#isEnabled()
	 */
	public boolean isEnabled() {
		return SeamPreferences.isValidateEL(project);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.validation.ISeamValidator#validate(java.util.Set)
	 */
	public IStatus validate(Set<IFile> changedFiles) throws ValidationException {
		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
		Set<IPath> files = validationContext.getElResourcesForValidation(changedFiles);
		validationContext.removeLinkedElResources(files);
		for (IPath path : files) {
			if(!reporter.isCancelled()) {
				validationContext.removeUnnamedElResource(path);
				IFile file = wsRoot.getFile(path);
				if(file.exists()) {
					reporter.removeMessageSubset(validationManager, file, ISeamValidator.MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
					validateFile(file);
				}
			}
		}

		validationContext.clearOldVariableNameForElValidation();
		return OK_STATUS;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.validation.ISeamValidator#validateAll()
	 */
	public IStatus validateAll() throws ValidationException {
		validationContext.clearElResourceLinks();
		Set<IFile> files = validationContext.getRegisteredFiles();
		for (IFile file : files) {
			if(!reporter.isCancelled()) {
				validateFile(file);
			}
		}
		return OK_STATUS;
	}

	private void validateFile(IFile file) {
		displaySubtask(VALIDATING_EL_FILE_MESSAGE_ID, new String[]{projectName, file.getName()});
		String ext = file.getFileExtension();
		String content = null;
		try {
			if(!file.isSynchronized(IResource.DEPTH_ZERO)) {
				// The resource is out of sync with the file system
				// Just ignore this resource.
				return;
			}
			content = FileUtil.readStream(file.getContents());
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
			return;
		}
		if(ext.equalsIgnoreCase("java")) { //$NON-NLS-1$
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
			SeamCorePlugin.getDefault().logError(SeamCoreMessages.SEAM_EL_VALIDATOR_ERROR_VALIDATING_SEAM_EL, e);
		}
	}

	private void validateDom(IFile file, String content) {
		IModelManager manager = StructuredModelManager.getModelManager();
		if(manager == null) {
			// this can happen if plugin org.eclipse.wst.sse.core 
			// is stopping or uninstalled, that is Eclipse is shutting down.
			// there is no need to report it, just stop validation.
			return;
		}
		IStructuredModel model = null;		
		try {
			model = manager.getModelForRead(file);
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
			SeamCorePlugin.getDefault().logError(SeamCoreMessages.SEAM_EL_VALIDATOR_ERROR_VALIDATING_SEAM_EL, e);
        } catch (IOException e) {
        	SeamCorePlugin.getDefault().logError(SeamCoreMessages.SEAM_EL_VALIDATOR_ERROR_VALIDATING_SEAM_EL, e);
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
				if(text.indexOf("{")>-1) { //$NON-NLS-1$
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
			int startEl = localString.indexOf("#{"); //$NON-NLS-1$
			int endEl = -1;
//			if(startEl==-1) {
//				startEl = localString.indexOf("${");
//			}
			if(startEl>-1) {
				endEl = localString.lastIndexOf('}');
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
			validateEl(file, el);
		}
	}

	private void validateEl(IFile file, EL el) {
		String exp = el.value;
//		String test = "hotelBooking.bookHotel(hotel.id, user.username) not null av.test[] ! = var2 <> var3.test3";
		SeamELTokenizer elTokenizer = new SeamELTokenizer(exp);
		List<ELToken> tokens = elTokenizer.getTokens();
		for (ELToken token : tokens) {
			if(token.getType()==ELToken.EL_OPERAND_TOKEN) {
				validateElOperand(file, token, el.getOffset());
			}
		}
	}

	private void validateElOperand(IFile file, ELToken operandToken, int documnetOffset) {
		String operand = operandToken.getText();
		String varName = operand;
		int offsetOfVarName = documnetOffset + operandToken.getStart();
		int lengthOfVarName = varName.length();
		try {
			int offset = operand.length();
			if (!operand.endsWith(".")) { //$NON-NLS-1$
				String prefix = SeamELCompletionEngine.getPrefix(operand, offset);
				if(prefix!=null) {
					int position = operand.indexOf(prefix);
					if (position == -1) {
						position = 0;
					}

					Set<ISeamContextVariable> usedVariables = new HashSet<ISeamContextVariable>();
					Map<String, TypeInfoCollector.MethodInfo> unpairedGettersOrSetters = new HashMap<String, TypeInfoCollector.MethodInfo>();

					List<String> suggestions = engine.getCompletions(project, file, operand, prefix, position, true, usedVariables, unpairedGettersOrSetters);

					if(usedVariables.size()==0 && suggestions.size()==0) {
						// Save resources with unknown variables names
						validationContext.addUnnamedElResource(file.getFullPath());
					} else {
						// Save links between resource and used variables names
						for(ISeamContextVariable variable: usedVariables) {
							validationContext.addLinkedElResource(variable.getName(), file.getFullPath());
						}
					}

					// Check pair for getter/setter
					if(unpairedGettersOrSetters.size()>0) {
						TypeInfoCollector.MethodInfo unpairedMethod = unpairedGettersOrSetters.values().iterator().next();
						String methodName = unpairedMethod.getName();
						String propertyName = unpairedGettersOrSetters.keySet().iterator().next();
						String missingMethodName = SeamCoreMessages.SEAM_EL_VALIDATOR_SETTER;
						String existedMethodName = SeamCoreMessages.SEAM_EL_VALIDATOR_GETTER;
						if(methodName.startsWith("s")) { //$NON-NLS-1$
							missingMethodName = existedMethodName;
							existedMethodName = SeamCoreMessages.SEAM_EL_VALIDATOR_SETTER;
						}
						addError(UNPAIRED_GETTER_OR_SETTER_MESSAGE_ID, SeamPreferences.UNPAIRED_GETTER_OR_SETTER, new String[]{propertyName, existedMethodName, missingMethodName}, operand.length(), documnetOffset, file);
					}

					if (suggestions != null && suggestions.size() > 0) {
						// It's valid EL.
						return;
					}

					SeamELOperandTokenizer tokenizer = new SeamELOperandTokenizer(operand, position + prefix.length());
					List<ELOperandToken> tokens = tokenizer.getTokens();
					for (ELOperandToken token : tokens) {
						if((token.getType()==ELOperandToken.EL_NAME_TOKEN) || (token.getType()==ELOperandToken.EL_METHOD_TOKEN)) {
							if(!isResolvedVar(token.getText(), usedVariables)) {
								varName = token.getText();
								offsetOfVarName = documnetOffset + operandToken.getStart() + token.getStart();
								lengthOfVarName = varName.length();
								break;
							}
						}
					}
				}
			}
		} catch (BadLocationException e) {
			SeamCorePlugin.getDefault().logError(SeamCoreMessages.SEAM_EL_VALIDATOR_ERROR_VALIDATING_SEAM_EL, e);
		} catch (StringIndexOutOfBoundsException e) {
			SeamCorePlugin.getDefault().logError(SeamCoreMessages.SEAM_EL_VALIDATOR_ERROR_VALIDATING_SEAM_EL, e);
		}
		// Mark invalid EL
		addError(INVALID_EXPRESSION_MESSAGE_ID, SeamPreferences.INVALID_EXPRESSION, new String[]{varName}, lengthOfVarName, offsetOfVarName, file);
	}

	private boolean isResolvedVar(String varName, Set<ISeamContextVariable> usedVariables) {
		for (ISeamContextVariable seamContextVariable : usedVariables) {
			if(varName.equals(seamContextVariable.getName())) {
				return true;
			}
		}
		return false;
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