/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.internal.core.el;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine;
import org.jboss.tools.common.el.core.ca.MessagesELTextProposal;
import org.jboss.tools.common.el.core.model.ELArgumentInvocation;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInstance;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELModel;
import org.jboss.tools.common.el.core.model.ELObjectType;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.el.core.parser.ELParser;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolutionImpl;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.el.core.resolver.ElVarSearcher;
import org.jboss.tools.common.el.core.resolver.JavaMemberELSegment;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.MemberInfo;
import org.jboss.tools.common.el.core.resolver.Var;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.XModelObjectConstants;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.common.util.StringUtil;
import org.jboss.tools.jst.web.kb.el.MessagePropertyELSegmentImpl;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamContextShortVariable;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamMessages;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamXmlFactory;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.el.SeamExpressionResolver.MessagesInfo;

/**
 * Utility class used to collect info for EL
 * 
 * @author Jeremy
 */
public final class SeamELCompletionEngine extends AbstractELCompletionEngine<ISeamContextVariable> {

	private static final Image SEAM_EL_PROPOSAL_IMAGE = 
		SeamCorePlugin.getDefault().getImage(SeamCorePlugin.CA_SEAM_EL_IMAGE_PATH);
	private static final Image SEAM_MESSAGES_PROPOSAL_IMAGE = 
		SeamCorePlugin.getDefault().getImage(SeamCorePlugin.CA_SEAM_MESSAGES_IMAGE_PATH);
	
	private static ELParserFactory factory = ELParserUtil.getJbossFactory();

	/**
	 * Constructs SeamELCompletionEngine object
	 */
	public SeamELCompletionEngine() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine#getELProposalImageForMember(org.jboss.tools.common.el.core.resolver.TypeInfoCollector.MemberInfo)
	 */
	@Override
	public Image getELProposalImageForMember(MemberInfo memberInfo) {
		return SEAM_EL_PROPOSAL_IMAGE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.resolver.ELCompletionEngine#getParserFactory()
	 */
	public ELParserFactory getParserFactory() {
		return factory;
	}

	protected void log(Exception e) {
		SeamCorePlugin.getPluginLog().logError(e);
	}

	/**
	 * Returns a list of Seam Context Variables that is represented by EL. Null if El is not resolved.
	 * @param project
	 * @param file
	 * @param el
	 * @return
	 * @throws BadLocationException
	 * @throws StringIndexOutOfBoundsException
	 */
	public List<ISeamContextVariable> resolveSeamVariableFromEL(ISeamProject project, IFile file, String el) throws BadLocationException, StringIndexOutOfBoundsException {
		List<ISeamContextVariable> resolvedVariables = new ArrayList<ISeamContextVariable>();
		
		if(!el.startsWith("#{")) {
			el = "#{" + el + "}";
		}
		ELParser parser = factory.createParser();
		ELModel model = parser.parse(el);
		List<ELInstance> is = model.getInstances();
		if(is.size() < 1) return resolvedVariables;
		
		ELExpression ex = is.get(0).getExpression();
		if(!(ex instanceof ELInvocationExpression)) return resolvedVariables;
		
		ELInvocationExpression expr = (ELInvocationExpression)ex;
		
		boolean isIncomplete = expr.getType() == ELObjectType.EL_PROPERTY_INVOCATION
				&& ((ELPropertyInvocation) expr).getName() == null;

		ELInvocationExpression left = expr;

//		ScopeType scope = getScope(project, file);

		if (expr.getLeft() == null && isIncomplete) {
			resolvedVariables = resolveVariables(project, file, expr, true, true);
		} else {
			while (left != null) {
				List<ISeamContextVariable> resolvedVars = new ArrayList<ISeamContextVariable>();
				resolvedVars = resolveVariables(project, file, left,
						left == expr, true);
				if (resolvedVars != null && !resolvedVars.isEmpty()) {
					resolvedVariables = resolvedVars;
					break;
				}
				left = (ELInvocationExpression) left.getLeft();
			}
		}

		if (left != expr) {
			resolvedVariables.clear();
		}

		return resolvedVariables;
	}

	@Override
	public List<ISeamContextVariable> resolveVariables(IFile file, ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames, int offset) {
		ISeamProject project = SeamCorePlugin.getSeamProject(file.getProject(), true);
//		ScopeType scope = getScope(project, file);
		return resolveVariables(project, file, expr, isFinal, onlyEqualNames);
	}

	protected TypeInfoCollector.MemberInfo getMemberInfoByVariable(ISeamContextVariable var, boolean onlyEqualNames, int offset) {
		return SeamExpressionResolver.getMemberInfoByVariable(var, true, this, offset);
	}

	protected void setImage(TextProposal proposal, ISeamContextVariable var) {
		if (isSeamMessagesComponentVariable((ISeamContextVariable)var)) {
			proposal.setImage(SEAM_MESSAGES_PROPOSAL_IMAGE);
		} else {
			proposal.setImage(getELProposalImageForMember(null));
		}
	}

	protected boolean isSingularAttribute(ISeamContextVariable var) {
		return var instanceof IBijectedAttribute;
	}

	protected void setImage(TextProposal kbProposal, TypeInfoCollector.MemberPresentation proposal) {
		if (proposal.getMember() instanceof MessagesInfo) {
			kbProposal.setImage(SEAM_MESSAGES_PROPOSAL_IMAGE);
		} else {
			super.setImage(kbProposal, proposal);
		}
	}

	protected boolean isSingularMember(TypeInfoCollector.MemberInfo mbr) {
		return (mbr instanceof MessagesInfo);
	}

	protected void resolveLastSegment(ELInvocationExpression expr, 
			List<TypeInfoCollector.MemberInfo> members,
			ELResolutionImpl resolution,
			boolean returnEqualedVariablesOnly, boolean varIsUsed) {
		if(resolveLastSegmentInMessages(expr, members, resolution, returnEqualedVariablesOnly, varIsUsed)) {
			return;
		} else {
			super.resolveLastSegment(expr, members, resolution, returnEqualedVariablesOnly, varIsUsed);
		}
	}

	private boolean resolveLastSegmentInMessages(ELInvocationExpression expr, 
			List<TypeInfoCollector.MemberInfo> members,
			ELResolutionImpl resolution,
			boolean returnEqualedVariablesOnly, boolean varIsUsed) {
		if(members.isEmpty() || !(members.get(0) instanceof MessagesInfo)) {
			return false;
		}
		MessagesInfo messagesInfo = ((MessagesInfo)members.get(0));
		MessagePropertyELSegmentImpl segment = null;
		if(expr instanceof ELPropertyInvocation) {
			segment = new MessagePropertyELSegmentImpl(((ELPropertyInvocation)expr).getName());
		} else if (expr instanceof ELArgumentInvocation) {
			segment = new MessagePropertyELSegmentImpl(((ELArgumentInvocation)expr).getArgument().getOpenArgumentToken().getNextToken());
		}
		
		Set<TextProposal> kbProposals = new TreeSet<TextProposal>(TextProposal.KB_PROPOSAL_ORDER);

		if (segment.getToken() != null) {
			String propertyName = segment.getToken().getText();
			Map<String, List<XModelObject>> properties = messagesInfo.getPropertiesMap();
			List<XModelObject> os = properties.get(StringUtil.trimQuotes(propertyName));
			if(os != null) {
				for(XModelObject o: os) {
					segment.addObject(o);
				}
				
				// Using 'base name' in seam is not a good idea.
	//			if(!os.isEmpty()) {
	//				segment.setBaseName(getBundle(os.get(0)));
	//			} else {
	//				segment.setBaseName("messages");
	//			}
			}
		}		
		if(segment.getToken()!=null) {
			resolution.addSegment(segment);
		}
			resolution.setProposals(kbProposals);
			if (expr.getType() == ELObjectType.EL_PROPERTY_INVOCATION && ((ELPropertyInvocation)expr).getName() == null) {
			// return all the methods + properties
			for (TypeInfoCollector.MemberInfo mbr : members) {
				processSingularMember(mbr, kbProposals);
			}
		} else
			if(expr.getType() != ELObjectType.EL_ARGUMENT_INVOCATION) {
			String filter = (expr.getMemberName() == null ? "" : expr.getMemberName());

			for (TypeInfoCollector.MemberInfo mbr : members) {
				Collection<String> keys = ((MessagesInfo)mbr).getKeys();
				for (String key : keys) {
					if(returnEqualedVariablesOnly) {
						// This is used for validation.
						if (key.equals(filter)) {
							MessagesELTextProposal kbProposal = createProposal(messagesInfo, key);
							kbProposals.add(kbProposal);
							break;
						}
					} else if (key.startsWith(filter)) {
						// This is used for CA.
						MessagesELTextProposal kbProposal = createProposal(messagesInfo, key);
						if (key.indexOf('.') == -1)	kbProposal.setReplacementString(key.substring(filter.length()));
						else kbProposal.setReplacementString('[' + kbProposal.getReplacementString());
						kbProposals.add(kbProposal);
					}
				}
			}
		} else if(expr.getType() == ELObjectType.EL_ARGUMENT_INVOCATION) {
			String filter = expr.getMemberName() == null ? "" : expr.getMemberName();
			boolean b = filter.startsWith("'") || filter.startsWith("\""); //$NON-NLS-1$ //$NON-NLS-2$
			filter = StringUtil.trimQuotes(filter);

			for (TypeInfoCollector.MemberInfo mbr : members) {
				if (!b && filter.length() > 0) {
					//Value is set as expression itself, we cannot compute it
					resolution.setMapOrCollectionOrBundleAmoungTheTokens(true);
					return true;
				}

				Collection<String> keys = ((MessagesInfo)mbr).getKeys();
				for (String key : keys) {
					if(returnEqualedVariablesOnly) {
						// This is used for validation.
						if (key.equals(filter)) {
							MessagesELTextProposal kbProposal = createProposal(messagesInfo, key);
							kbProposals.add(kbProposal);
							break;
						}
					} else if (key.startsWith(filter)) {
						// This is used for CA.
						MessagesELTextProposal kbProposal = createProposal(messagesInfo, key);
						String existingString = expr.getMemberName() == null ? "" : expr.getMemberName();
						// Because we're in argument invocation we should fix the proposal by surrounding it with quotes as needed
						String replacement = kbProposal.getReplacementString();
						String label = kbProposal.getLabel();
						if (!replacement.startsWith("'")) {
							replacement = '\'' + key + '\'';
							label = "['" + key + "']";
						}
						replacement = replacement.substring(existingString.length());

						kbProposal.setReplacementString(replacement);
						kbProposal.setLabel(label);

						kbProposals.add(kbProposal);
					}
				}
			}
		}
		segment.setResolved(!kbProposals.isEmpty());
		if (resolution.isResolved()){
			resolution.setLastResolvedToken(expr);
		}			
		return true;
	}

	protected void processSingularMember(TypeInfoCollector.MemberInfo mbr, Set<TextProposal> kbProposals) {
		if (mbr instanceof MessagesInfo) {
			// Surround the "long" keys containing the dots with [' ']
			Map<String, List<XModelObject>> properties = ((MessagesInfo)mbr).getPropertiesMap();
			TreeSet<String> keys = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			keys.addAll(properties.keySet());
			Iterator<String> sortedKeys = keys.iterator();
			while(sortedKeys.hasNext()) {
				String key = sortedKeys.next();
				if (key == null || key.length() == 0)
					continue;

				MessagesELTextProposal proposal = createProposal((MessagesInfo)mbr, key);
				if (key.indexOf('.') != -1) {
					proposal.setReplacementString("['" + key + "']"); //$NON-NLS-1$ //$NON-NLS-2$
					proposal.setLabel("['" + key + "']");
				} else {
					proposal.setReplacementString(key);
					proposal.setLabel(key);
				}
				kbProposals.add(proposal);
			}
		}
	}

	private MessagesELTextProposal createProposal(MessagesInfo mbr, String proposal) {
		MessagesELTextProposal kbProposal = new MessagesELTextProposal();
		if (proposal.indexOf('.') != -1) {
			kbProposal.setReplacementString('\'' + proposal + '\'');
			kbProposal.setLabel("['" + proposal + "']");
		} else {
			kbProposal.setReplacementString(proposal);
			kbProposal.setLabel(proposal);
		}
		kbProposal.setAlternateMatch(proposal);
		kbProposal.setImage(SEAM_MESSAGES_PROPOSAL_IMAGE);

		Map<String, List<XModelObject>> properties = mbr.getPropertiesMap();
		List<XModelObject> ps = properties.get(proposal);
//		String bundle = ps.isEmpty() ? "messages" : getBundle(ps.get(0)); // Using 'base name' in seam is not a good idea.
		
//		kbProposal.setBaseName(bundle); // Using 'base name' in seam is not a good idea.
		kbProposal.setPropertyName(proposal);
		kbProposal.setObjects(ps);

		return kbProposal;
	}

	private String getBundle(XModelObject o) {
		StringBuilder sb = new StringBuilder();
		XModelObject f = FileSystemsHelper.getFile(o);
		if(f != null) {
			sb.append(f.getAttributeValue(XModelObjectConstants.ATTR_NAME));
			f = f.getParent();
			while(f != null && f.getFileType() == XModelObject.FOLDER) {
				sb.insert(0,  ".").insert(0, f.getAttributeValue(XModelObjectConstants.ATTR_NAME));
				f = f.getParent();
			}
		}
		return sb.toString();
	}

	protected void filterSingularMember(TypeInfoCollector.MemberInfo mbr, Set<TypeInfoCollector.MemberPresentation> proposalsToFilter) {
		Collection<String> keys = ((MessagesInfo)mbr).getKeys();
		for (String key : keys) {
			proposalsToFilter.add(new TypeInfoCollector.MemberPresentation(key, key, mbr));
		}
	}

	/**
	 *  Returns scope for the resource
	 * @param project
	 * @param resource
	 * @return
	 */
	public static ScopeType getScope(ISeamProject project, IResource resource) {
		if (project == null || resource == null)
			return null;
		
		if (!"java".equals(resource.getFileExtension())) //$NON-NLS-1$
			return null;
		Set<ISeamComponent> components = project.getComponentsByPath(resource.getFullPath());

		if (components.size() > 1) // Don't use scope in case of more than one component
			return null;
		for (ISeamComponent component : components) {
			return component.getScope();
		}
		return null;
	}

	public List<ISeamContextVariable> resolveVariables(ISeamProject project, IFile file, ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames) {
		List<ISeamContextVariable>resolvedVars = new ArrayList<ISeamContextVariable>();
		
		if (project == null)
			return new ArrayList<ISeamContextVariable>(); 
		
		String varName = expr.toString();

		if (varName != null) {
			resolvedVars = SeamExpressionResolver.resolveVariables(project, file, varName, onlyEqualNames);
		}
		if (resolvedVars != null && !resolvedVars.isEmpty()) {
			List<ISeamContextVariable> newResolvedVars = new ArrayList<ISeamContextVariable>();
			for (ISeamContextVariable var : resolvedVars) {
				if(!isFinal) {
					// Do filter by equals (name)
					// In case of the last pass - do not filter by startsWith(name) instead of equals
					if (varName.equals(var.getName())) {
						newResolvedVars.add(var);
					}
				} else {
					newResolvedVars.add(var);
				}
			}
			return newResolvedVars;
		}
		return new ArrayList<ISeamContextVariable>(); 
	}

	/**
	 * @param documentContent
	 * @param offset
	 * @param region
	 * @return 
	 * @throws StringIndexOutOfBoundsException
	 */
	public String getJavaElementExpression(IDocument document, int offset, IRegion region, int start, int end) throws StringIndexOutOfBoundsException {
		if (document == null || document.get() == null || offset > document.get().length())
			return null;

		ELInvocationExpression expr = findExpressionAtOffset(
				document, 
				region.getOffset() + region.getLength(), 
				start, end);

		if (expr == null) return null;

		ELInvocationExpression left = expr;
		while(left != null && left.getLeft() != null) left = left.getLeft();

		String prefixPart = document.get().substring(expr.getStartPosition(), offset);

		while(left != null) {
			String varText = left.getText(); 
			if (varText != null && varText.startsWith(prefixPart)) {
				return varText; 
			}
			if(left == expr) break;
			left = (ELInvocationExpression)left.getParent();
		}
		return null;
	}

	/**
	 * Create the array of suggestions from expression. 
	 * @param project Seam project 
	 * @param file File 
	 * @param offset TODO
	 * @param document 
	 * @param prefix the prefix to search for
	 * @param position Offset of the prefix 
	 */
	public List<IJavaElement> getJavaElementsForExpression(ISeamProject project, IFile file, String expression, int offset) throws BadLocationException, StringIndexOutOfBoundsException {
		ELExpression expr = parseOperand(expression);
		if(!(expr instanceof ELInvocationExpression)) {
			return new ArrayList<IJavaElement>();
		}
		return getJavaElementsForELOperandTokens(project, file, (ELInvocationExpression)expr);
	}

	/**
	 * Create the array of suggestions. 
	 * @param project Seam project 
	 * @param file File 
	 * @param document 
	 * @param prefix the prefix to search for
	 * @param position Offset of the prefix 
	 */
	public List<IJavaElement> getJavaElementsForELOperandTokens(
			ISeamProject project, IFile file, 
			ELInvocationExpression expr) throws BadLocationException, StringIndexOutOfBoundsException {
		List<IJavaElement> res = new ArrayList<IJavaElement>();

		ElVarSearcher varSearcher = new ElVarSearcher(file, this);
		List<Var> vars = varSearcher.findAllVars(file, expr.getStartPosition());

		ELResolution resolution = resolveELOperand(file, expr, true, vars, varSearcher, 0);
		if (resolution!=null && resolution.isResolved()) {
			ELSegment segment = resolution.getLastSegment();
			if(segment instanceof JavaMemberELSegment) {
				IJavaElement el = ((JavaMemberELSegment)segment).getJavaElement();
				if (el != null) {
					res.add(el);
					return res;
				}
			}
		}
		return res;
	}

	public static ISeamMessages getSeamMessagesComponentVariable(ISeamContextVariable variable) {
		if (variable instanceof ISeamMessages) {
			return (ISeamMessages)variable;
		} else if (variable instanceof ISeamXmlFactory) {
			ISeamXmlFactory factory = (ISeamXmlFactory)variable;
			String value = factory.getValue();
			if (value != null && value.length() > 0) {
				if (value.startsWith("#{") || value.startsWith("${")) //$NON-NLS-1$ //$NON-NLS-2$
					value = value.substring(2);
				if (value.endsWith("}")) //$NON-NLS-1$
					value = value.substring(0, value.length() - 1);
			}
			if (value != null && value.length() > 0) {
				ISeamProject p = ((ISeamElement)factory).getSeamProject();
				if (p != null) {
					List<ISeamContextVariable> resolvedValues = SeamExpressionResolver.resolveVariables(p, null, value, true);
					for (ISeamContextVariable var : resolvedValues) {
						if (var.getName().equals(value)) {
							if (var instanceof ISeamMessages) {
								return (ISeamMessages)var;
							}
						}
					}
				}
			}
		} else if(variable instanceof ISeamContextShortVariable) {
			ISeamContextShortVariable sv = (ISeamContextShortVariable)variable;
			return getSeamMessagesComponentVariable(sv.getOriginal());
		}
		return null;
	}

	public static boolean isSeamMessagesComponentVariable(ISeamContextVariable variable) {
		return (null != getSeamMessagesComponentVariable(variable));
	}

	@Override
	protected boolean isStaticMethodsCollectingEnabled() {
		return true; // Static methods are always enabled for Seam
	}
}