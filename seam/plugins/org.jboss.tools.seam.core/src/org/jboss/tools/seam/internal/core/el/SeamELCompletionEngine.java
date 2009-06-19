/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.el.core.model.ELArgumentInvocation;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInstance;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELMethodInvocation;
import org.jboss.tools.common.el.core.model.ELModel;
import org.jboss.tools.common.el.core.model.ELObjectType;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.el.core.model.ELUtil;
import org.jboss.tools.common.el.core.parser.ELParser;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.parser.LexicalToken;
import org.jboss.tools.common.el.core.resolver.ELCompletionEngine;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELOperandResolveStatus;
import org.jboss.tools.common.el.core.resolver.ELResolver;
import org.jboss.tools.common.el.core.resolver.ElVarSearcher;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector;
import org.jboss.tools.common.el.core.resolver.Var;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.MemberInfo;
import org.jboss.tools.common.model.project.ext.ITextSourceReference;
import org.jboss.tools.common.model.project.ext.event.Change;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.jst.web.kb.IPageContext;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamContextShortVariable;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamJavaSourceReference;
import org.jboss.tools.seam.core.ISeamMessages;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamXmlFactory;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.el.SeamExpressionResolver.MessagesInfo;
import org.w3c.dom.Element;

/**
 * Utility class used to collect info for EL
 * 
 * @author Jeremy
 */
public final class SeamELCompletionEngine implements ELCompletionEngine, ELResolver {

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
	 * @see org.jboss.tools.common.el.core.resolver.ELCompletionEngine#getParserFactory()
	 */
	public ELParserFactory getParserFactory() {
		return factory;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.resolver.ELResolver#getCompletions(java.lang.String, boolean, int, org.jboss.tools.common.el.core.resolver.ELContext)
	 */
	public List<TextProposal> getCompletions(String elString, boolean returnEqualedVariablesOnly, int position, ELContext context) {
		IDocument document = null;
		if(context instanceof IPageContext) {
			IPageContext pageContext = (IPageContext)context;
			document = pageContext.getDocument();
		}
		List<Var> vars = new ArrayList<Var>();
		Var[] array = context.getVars();
		for (int i = 0; i < array.length; i++) {
			vars.add(array[i]);
		}
		List<TextProposal> proposals = null;
		try {
			 proposals = getCompletions(context.getResource(), document, elString.subSequence(0, elString.length()), position, returnEqualedVariablesOnly, vars);
		} catch (StringIndexOutOfBoundsException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		} catch (BadLocationException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		return proposals;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.resolver.ELResolver#resolveELOperand(org.jboss.tools.common.el.core.model.ELExpression, org.jboss.tools.common.el.core.resolver.ELContext, boolean)
	 */
	public ELOperandResolveStatus resolveELOperand(ELExpression operand, ELContext context, boolean returnEqualedVariablesOnly) {
		List<Var> vars = new ArrayList<Var>();
		Var[] array = context.getVars();
		for (int i = 0; i < array.length; i++) {
			vars.add(array[i]);
		}
		ELOperandResolveStatus status = null;
		try {
			status = resolveELOperand(context.getResource(), operand, returnEqualedVariablesOnly, vars, new ElVarSearcher(context.getResource(), this));
		} catch (StringIndexOutOfBoundsException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		} catch (BadLocationException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		return status;
	}

	/**
	 * Create the list of suggestions. 
	 * @param seamProject Seam project 
	 * @param file File 
	 * @param documentContent
	 * @param prefix the prefix to search for
	 * @param position Offset of the prefix 
	 * @param vars - 'var' attributes which can be used in this EL. Can be null.
	 * @param returnEqualedVariablesOnly 'false' if we get proposals for mask  
	 *  for example:
	 *   we have 'variableName.variableProperty', 'variableName.variableProperty1', 'variableName.variableProperty2'  
	 *   prefix is 'variableName.variableProperty'
	 *   Result is {'variableProperty'}
	 * if 'false' then returns ends of variables that starts with prefix. It's useful for CA.
	 *  for example:
	 *   we have 'variableName.variableProperty', 'variableName.variableProperty1', 'variableName.variableProperty2'
	 *   prefix is 'variableName.variableProperty'
	 *   Result is {'1','2'}
	 * @return the list of all possible suggestions
	 * @throws BadLocationException if accessing the current document fails
	 * @throws StringIndexOutOfBoundsException
	 */
	public List<TextProposal> getCompletions(IFile file, IDocument document, CharSequence prefix, 
			int position, boolean returnEqualedVariablesOnly, List<Var> vars) throws BadLocationException, StringIndexOutOfBoundsException {
		List<TextProposal> completions = new ArrayList<TextProposal>();
		
		ELOperandResolveStatus status = resolveELOperand(file, parseOperand("" + prefix), returnEqualedVariablesOnly, vars, new ElVarSearcher(file, this));
		if (status.isOK()) {
			completions.addAll(status.getProposals());
		}

		return completions;
	}
	private static final String collectionAdditionForCollectionDataModel = ".iterator().next()";
	private static final String collectionAdditionForMapDataModel = ".entrySet().iterator().next()";

	private List<String> getVarNameProposals(List <Var> vars, String prefix) {
		List<String> proposals = new ArrayList<String>();
		for (Var var : vars) {
			if(var.getName().startsWith(prefix)) {
				String proposal = var.getName().substring(prefix.length());
				proposals.add(proposal);
			}
		}
		return proposals;
	}

	public SeamELOperandResolveStatus resolveELOperand(IFile file, ELExpression operand,  
			boolean returnEqualedVariablesOnly, List<Var> vars, ElVarSearcher varSearcher) throws BadLocationException, StringIndexOutOfBoundsException {
		if(operand == null) {
			//TODO
			return new SeamELOperandResolveStatus(null);
		}
		String oldEl = operand.getText();
		Var var = varSearcher.findVarForEl(oldEl, vars, true);
		String suffix = "";
		String newEl = oldEl;
		TypeInfoCollector.MemberInfo member = null;
		boolean isArray = false;
		if(var!=null) {
			member = resolveSeamEL(file, var.getElToken(), true);
			if(member!=null) {
				if(!member.getType().isArray()) {
					IType type = member.getMemberType();
					if(type!=null) {
						try {
							if(TypeInfoCollector.isInstanceofType(type, "java.util.Map")) {
								suffix = collectionAdditionForMapDataModel;
							} else if(TypeInfoCollector.isInstanceofType(type, "java.util.Collection")) {
								suffix = collectionAdditionForCollectionDataModel;
							}
						} catch (JavaModelException e) {
							SeamCorePlugin.getPluginLog().logError(e);
						}
					}
				} else {
					isArray = true;
				}
			}
			if(var.getElToken() != null) {
				newEl = var.getElToken().getText() + suffix + oldEl.substring(var.getName().length());
			}
		}
		boolean prefixWasChanged = !oldEl.equals(newEl);
		if(prefixWasChanged && isArray) {
			member.setDataModel(true);
		}
		ELExpression newOperand = (prefixWasChanged) ? parseOperand(newEl) : operand;

		SeamELOperandResolveStatus status = resolveELOperand(file, newOperand, returnEqualedVariablesOnly, prefixWasChanged);

		if(prefixWasChanged) {
			ELInvocationExpression newLastResolvedToken = status.getLastResolvedToken();
			status.setTokens((ELInvocationExpression)operand);
			if(newLastResolvedToken != null) {
				if(status.getUnresolvedTokens() != null 
						&& status.getUnresolvedTokens().getInvocationStartPosition() - status.getUnresolvedTokens().getStartPosition() < var.getElToken().getLength() + suffix.length()) {
					// Last resolved token is token from "var". Set first token of original EL as last resolved one.
					status.setLastResolvedToken(null);
				} else {
					// Last resolved token is token outside "var" prefix. Correct last resolved token.
					int oldLastResolvedTokenStart = newLastResolvedToken.getInvocationStartPosition() - var.getElToken().getText().length() - suffix.length() + var.getName().length();
					if(newLastResolvedToken.getLeft() == null) {
						//In this case we do not need to take into account difference in length of var and its expression.
						oldLastResolvedTokenStart = newLastResolvedToken.getInvocationStartPosition();
					}
					ELInvocationExpression l = (ELInvocationExpression)operand;
					while(l != null) {
						if(l.getInvocationStartPosition() - l.getStartPosition() <= oldLastResolvedTokenStart) {
							status.setLastResolvedToken(l);
							break;
						}
						l = l.getLeft();
					}
				}
			}
		}

		if(prefixWasChanged) {
			var.resolveValue("#{" + var.getElToken().getText() + suffix + "}");
		}

		if(!returnEqualedVariablesOnly && vars!=null) {
			List<String> varNameProposals = getVarNameProposals(vars, operand.toString());
			if (varNameProposals != null) {
				for (String varNameProposal : varNameProposals) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(varNameProposal);
					proposal.setImage(SEAM_EL_PROPOSAL_IMAGE);
					status.getProposals().add(proposal);
				}
			}
		}
		return status;
	}

	public ELExpression parseOperand(String operand) {
		if(operand == null) return null;
		String el = (operand.indexOf("#{") < 0 && operand.indexOf("${") < 0) ? "#{" + operand + "}" : operand;
		ELParser p = factory.createParser();
		ELModel model = p.parse(el);
		List<ELInstance> is = model.getInstances();
		if(is.isEmpty()) return null;
		return is.get(0).getExpression();
	}

	/**
	 * Returns MemberInfo for last segment of EL. Null if El is not resolved.
	 * @param seamProject
	 * @param file
	 * @param operand EL without #{}
	 * @return MemberInfo for last segment of EL. Null if El is not resolved.
	 * @throws BadLocationException
	 * @throws StringIndexOutOfBoundsException
	 */
	public TypeInfoCollector.MemberInfo resolveSeamEL(IFile file, ELExpression operand, boolean varIsUsed) throws BadLocationException, StringIndexOutOfBoundsException {
		if(!(operand instanceof ELInvocationExpression)) return null;
		ELOperandResolveStatus status = resolveELOperand(file, operand, true, varIsUsed);
		return status.getMemberOfResolvedOperand();
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

		ELOperandResolveStatus status = new ELOperandResolveStatus(expr);
		ELInvocationExpression left = expr;

		ScopeType scope = getScope(project, file);

		if (expr.getLeft() == null && isIncomplete) {
			resolvedVariables = resolveVariables(project, scope, expr, true, true);
		} else {
			while (left != null) {
				List<ISeamContextVariable> resolvedVars = new ArrayList<ISeamContextVariable>();
				resolvedVars = resolveVariables(project, scope, left,
						left == expr, true);
				if (resolvedVars != null && !resolvedVars.isEmpty()) {
					resolvedVariables = resolvedVars;
					status.setLastResolvedToken(left);
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

	public SeamELOperandResolveStatus resolveELOperand(IFile file, ELExpression operand,  
			boolean returnEqualedVariablesOnly, boolean varIsUsed) throws BadLocationException, StringIndexOutOfBoundsException {
		if(!(operand instanceof ELInvocationExpression) || file == null) {
			return new SeamELOperandResolveStatus(null);
		}

		ELInvocationExpression expr = (ELInvocationExpression)operand;
		boolean isIncomplete = expr.getType() == ELObjectType.EL_PROPERTY_INVOCATION 
			&& ((ELPropertyInvocation)expr).getName() == null;
		boolean isArgument = expr.getType() == ELObjectType.EL_ARGUMENT_INVOCATION;

		SeamELOperandResolveStatus status = new SeamELOperandResolveStatus(expr);
		ELInvocationExpression left = expr;

		List<ISeamContextVariable> resolvedVariables = new ArrayList<ISeamContextVariable>();
		ISeamProject project = SeamCorePlugin.getSeamProject(file.getProject(), false);
		ScopeType scope = getScope(project, file);

		if (expr.getLeft() != null && isArgument) {
			left = expr.getLeft();
			resolvedVariables = resolveVariables(project, scope, left, false, 
					true); 	// is Final and equal names are because of 
							// we have no more to resolve the parts of expression, 
							// but we have to resolve arguments of probably a message component
		} else if (expr.getLeft() == null && isIncomplete) {
			resolvedVariables = resolveVariables(project, scope, expr, true, 
					returnEqualedVariablesOnly);
		} else {
			while(left != null) {
				List<ISeamContextVariable>resolvedVars = new ArrayList<ISeamContextVariable>();
				resolvedVars = resolveVariables(project, scope, 
						left, left == expr, 
						returnEqualedVariablesOnly);
				if (resolvedVars != null && !resolvedVars.isEmpty()) {
					resolvedVariables = resolvedVars;
					status.setLastResolvedToken(left);
					break;
				}
				left = (ELInvocationExpression)left.getLeft();
			} 
		}

		// Save all resolved variables. It's useful for incremental validation.
		if(resolvedVariables != null && !resolvedVariables.isEmpty()) {
			status.setUsedVariables(resolvedVariables);
		}

		if (status.getResolvedTokens() == null && 
				!returnEqualedVariablesOnly && 
				expr != null && 
				isIncomplete) {
			// no vars are resolved 
			// the tokens are the part of var name ended with a separator (.)
			resolvedVariables = resolveVariables(project, scope, expr, true, returnEqualedVariablesOnly);			
			Set<TextProposal> proposals = new TreeSet<TextProposal>(TextProposal.KB_PROPOSAL_ORDER);
			for (ISeamContextVariable var : resolvedVariables) {
				String varName = var.getName();
				if(varName.startsWith(operand.getText())) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(varName.substring(operand.getLength()));
					if (isSeamMessagesComponentVariable(var)) {
						proposal.setImage(SEAM_MESSAGES_PROPOSAL_IMAGE);
					} else {
						proposal.setImage(SEAM_EL_PROPOSAL_IMAGE);
					}
					proposals.add(proposal);
				}
			}
			status.setProposals(proposals);
			return status;
		}

		// Here we have a list of vars for some part of expression
		// OK. we'll proceed with members of these vars
		if (status.getResolvedTokens() == status.getTokens()) {
			// First segment is the last one
			Set<TextProposal> proposals = new TreeSet<TextProposal>(TextProposal.KB_PROPOSAL_ORDER);
			// In some cases there may be a few references to the same variable name.
			// For example @Factory and @DataModel. We should use @DataModel instead of @Factory
			// method which returns null.
			// See https://jira.jboss.org/jira/browse/JBIDE-3694
			TypeInfoCollector.MemberInfo bijectedAttribute = null;
			for (ISeamContextVariable var : resolvedVariables) {
				if(var instanceof IBijectedAttribute) {
					bijectedAttribute = SeamExpressionResolver.getMemberInfoByVariable(var, true, this);
				}
				String varName = var.getName();
				if(operand.getLength()<=varName.length()) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(varName.substring(operand.getLength()));
					if (isSeamMessagesComponentVariable(var)) {
						proposal.setImage(SEAM_MESSAGES_PROPOSAL_IMAGE);
					} else {
						proposal.setImage(SEAM_EL_PROPOSAL_IMAGE);
					}
					proposals.add(proposal);
				} else if(returnEqualedVariablesOnly) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(varName);
					if (isSeamMessagesComponentVariable(var)) {
						proposal.setImage(SEAM_MESSAGES_PROPOSAL_IMAGE);
					} else {
						proposal.setImage(SEAM_EL_PROPOSAL_IMAGE);
					}
					proposals.add(proposal);
				}
				status.setMemberOfResolvedOperand(bijectedAttribute!=null?bijectedAttribute:SeamExpressionResolver.getMemberInfoByVariable(var, true, this));
			}
			status.setLastResolvedToken(expr);
			status.setProposals(proposals);
			return status;
		}

		// First segment is found - proceed with next tokens 
		List<TypeInfoCollector.MemberInfo> members = new ArrayList<TypeInfoCollector.MemberInfo>();
		for (ISeamContextVariable var : resolvedVariables) {
			TypeInfoCollector.MemberInfo member = SeamExpressionResolver.getMemberInfoByVariable(var, returnEqualedVariablesOnly, this);
			if (member != null && !members.contains(member)) 
				members.add(member);
		}
		//process segments one by one
		if(left != null) while(left != expr) {
			left = (ELInvocationExpression)left.getParent();
			if (left != expr) { // inside expression
				if(left instanceof ELArgumentInvocation) {
					String s = "#{" + left.getLeft().toString() + collectionAdditionForCollectionDataModel + "}";
					ELParser p = factory.createParser();
					ELInvocationExpression expr1 = (ELInvocationExpression)p.parse(s).getInstances().get(0).getExpression();
					members = resolveSegment(expr1.getLeft(), members, status, returnEqualedVariablesOnly, varIsUsed);
					members = resolveSegment(expr1, members, status, returnEqualedVariablesOnly, varIsUsed);
					if(status.getLastResolvedToken() == expr1) {
						status.setLastResolvedToken(left);
					}
				} else {				
					members = resolveSegment(left, members, status, returnEqualedVariablesOnly, varIsUsed);
				}
			} else { // Last segment
				resolveLastSegment((ELInvocationExpression)operand, members, status, returnEqualedVariablesOnly, varIsUsed);
				break;
			}
		}

		if(status.getProposals().isEmpty() && status.getUnpairedGettersOrSetters()!=null) {
			status.clearUnpairedGettersOrSetters();
		}
		return status;
	}

	private List<TypeInfoCollector.MemberInfo> resolveSegment(ELInvocationExpression expr, 
			List<TypeInfoCollector.MemberInfo> members,
			ELOperandResolveStatus status,
			boolean returnEqualedVariablesOnly, boolean varIsUsed) {
		LexicalToken lt = (expr instanceof ELPropertyInvocation) 
			? ((ELPropertyInvocation)expr).getName()
					: (expr instanceof ELMethodInvocation) 
					? ((ELMethodInvocation)expr).getName()
							: null;
		String name = lt != null ? lt.getText() : ""; // token.getText();
		if (expr.getType() == ELObjectType.EL_PROPERTY_INVOCATION) {
			// Find properties for the token
			List<TypeInfoCollector.MemberInfo> newMembers = new ArrayList<TypeInfoCollector.MemberInfo>();
			for (TypeInfoCollector.MemberInfo mbr : members) {
				if (mbr.getMemberType() == null) continue;
				TypeInfoCollector infos = mbr.getTypeCollector(varIsUsed);
				if (TypeInfoCollector.isNotParameterizedCollection(mbr) || TypeInfoCollector.isResourceBundle(mbr.getMemberType())) {
					status.setMapOrCollectionOrBundleAmoungTheTokens();
				}
				List<TypeInfoCollector.MemberInfo> properties = infos.getProperties();
				for (TypeInfoCollector.MemberInfo property : properties) {
					StringBuffer propertyName = new StringBuffer(property.getName());
					if (property instanceof TypeInfoCollector.MethodInfo) { // Setter or getter
						propertyName.delete(0, (propertyName.charAt(0) == 'i' ? 2 : 3));
						propertyName.setCharAt(0, Character.toLowerCase(propertyName.charAt(0)));
					}
					if (name.equals(propertyName.toString())) {
						newMembers.add(property);
					}
				}
			}
			members = newMembers;
			if (members != null && !members.isEmpty())
				status.setLastResolvedToken(expr);
		}
		if (expr.getType() == ELObjectType.EL_METHOD_INVOCATION) {
			// Find methods for the token
			if (name.indexOf('(') != -1) {
				name = name.substring(0, name.indexOf('('));
			}
			List<TypeInfoCollector.MemberInfo> newMembers = new ArrayList<TypeInfoCollector.MemberInfo>();
			for (TypeInfoCollector.MemberInfo mbr : members) {
				if (mbr.getMemberType() == null) continue;
				TypeInfoCollector infos = mbr.getTypeCollector();
				if (TypeInfoCollector.isNotParameterizedCollection(mbr) || TypeInfoCollector.isResourceBundle(mbr.getMemberType())) {
					status.setMapOrCollectionOrBundleAmoungTheTokens();
				}
				List<TypeInfoCollector.MemberInfo> methods = infos.getMethods();
				for (TypeInfoCollector.MemberInfo method : methods) {
					if (name.equals(method.getName())) {
						newMembers.add(method);
					}
				}
			}
			members = newMembers;
			if (members != null && !members.isEmpty())
				status.setLastResolvedToken(expr);
		}
		return members;
	}

	private void resolveLastSegment(ELInvocationExpression expr, 
			List<TypeInfoCollector.MemberInfo> members,
			ELOperandResolveStatus status,
			boolean returnEqualedVariablesOnly, boolean varIsUsed) {
		Set<TextProposal> kbProposals = new TreeSet<TextProposal>(TextProposal.KB_PROPOSAL_ORDER);
		
		if (expr.getType() == ELObjectType.EL_PROPERTY_INVOCATION && ((ELPropertyInvocation)expr).getName() == null) {
			// return all the methods + properties
			for (TypeInfoCollector.MemberInfo mbr : members) {
				if (mbr instanceof MessagesInfo) {
					// Surround the "long" keys containing the dots with [' '] 
					TreeSet<String> keys = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
					keys.addAll(((MessagesInfo)mbr).getKeys());
					Iterator<String> sortedKeys = keys.iterator();
					while(sortedKeys.hasNext()) {
						String key = sortedKeys.next();
						if (key == null || key.length() == 0)
							continue;
						if (key.indexOf('.') != -1) {
							TextProposal proposal = new TextProposal();
							proposal.setReplacementString("['" + key + "']");
							proposal.setImage(SEAM_MESSAGES_PROPOSAL_IMAGE);
							
							kbProposals.add(proposal);
						} else {
							TextProposal proposal = new TextProposal();
							proposal.setReplacementString(key);
							proposal.setImage(SEAM_MESSAGES_PROPOSAL_IMAGE);
							
							kbProposals.add(proposal);
						}
					}
					continue;
				}
				if (mbr.getMemberType() == null) {
					continue;
				}
				TypeInfoCollector infos = mbr.getTypeCollector(varIsUsed);
				if (TypeInfoCollector.isNotParameterizedCollection(mbr) || TypeInfoCollector.isResourceBundle(mbr.getMemberType())) {
					status.setMapOrCollectionOrBundleAmoungTheTokens();
				}
				
				Set<String> methodPresentations = 
						infos.getMethodPresentationStrings();
				if (methodPresentations != null) {
					for (String presentation : methodPresentations) {
						TextProposal proposal = new TextProposal();
						proposal.setReplacementString(presentation);
						proposal.setImage(SEAM_EL_PROPOSAL_IMAGE);
						
						kbProposals.add(proposal);
					}
				}
				Set<String> propertyPresentations = 
					infos.getPropertyPresentationStrings(status.getUnpairedGettersOrSetters());
				if (propertyPresentations != null) {
					for (String presentation : propertyPresentations) {
						TextProposal proposal = new TextProposal();
						proposal.setReplacementString(presentation);
						proposal.setImage(SEAM_EL_PROPOSAL_IMAGE);
						
						kbProposals.add(proposal);
					}
				}
			}
		} else
			if(expr.getType() != ELObjectType.EL_ARGUMENT_INVOCATION)
			//actually any case
//			if (token.getType() == ELOperandToken.EL_VARIABLE_NAME_TOKEN ||
//				token.getType() == ELOperandToken.EL_PROPERTY_NAME_TOKEN ||
//				token.getType() == ELOperandToken.EL_METHOD_TOKEN) 
			{
			// return filtered methods + properties 
			Set<TypeInfoCollector.MemberPresentation> proposalsToFilter = new TreeSet<TypeInfoCollector.MemberPresentation>(TypeInfoCollector.MEMBER_PRESENTATION_COMPARATOR); 
			for (TypeInfoCollector.MemberInfo mbr : members) {
				if (mbr instanceof MessagesInfo) {
					Collection<String> keys = ((MessagesInfo)mbr).getKeys();
					for (String key : keys) {
						proposalsToFilter.add(new TypeInfoCollector.MemberPresentation(key, mbr));
					}
					continue;
				}
				if (mbr.getMemberType() == null) continue;
				TypeInfoCollector infos = mbr.getTypeCollector();
				if (TypeInfoCollector.isNotParameterizedCollection(mbr) || TypeInfoCollector.isResourceBundle(mbr.getMemberType())) {
					status.setMapOrCollectionOrBundleAmoungTheTokens();
				}
				proposalsToFilter.addAll(infos.getMethodPresentations());
				proposalsToFilter.addAll(infos.getPropertyPresentations(status.getUnpairedGettersOrSetters()));
				status.setMemberOfResolvedOperand(mbr);
			}
			for (TypeInfoCollector.MemberPresentation proposal : proposalsToFilter) {
				// We do expect nothing but name for method tokens (No round brackets)
				String filter = expr.getMemberName();
				if(filter == null) filter = "";
				if(returnEqualedVariablesOnly) {
					// This is used for validation.
					if (proposal.getPresentation().equals(filter)) {
						TextProposal kbProposal = new TextProposal();
						kbProposal.setReplacementString(proposal.getPresentation());
						
						if (proposal.getMember() instanceof MessagesInfo) {
							kbProposal.setImage(SEAM_MESSAGES_PROPOSAL_IMAGE);
						} else {
							kbProposal.setImage(SEAM_EL_PROPOSAL_IMAGE);
						}
						
						kbProposals.add(kbProposal);

						status.setMemberOfResolvedOperand(proposal.getMember());
						if(status.getUnpairedGettersOrSetters()!=null) {
							TypeInfoCollector.MethodInfo unpirMethod = status.getUnpairedGettersOrSetters().get(filter);
							status.clearUnpairedGettersOrSetters();
							if(unpirMethod!=null) {
								status.getUnpairedGettersOrSetters().put(filter, unpirMethod);
							}
						}
						break;
					}
				} else if (proposal.getPresentation().startsWith(filter)) {
					// This is used for CA.
					TextProposal kbProposal = new TextProposal();
					kbProposal.setReplacementString(proposal.getPresentation().substring(filter.length()));
					kbProposal.setImage(SEAM_EL_PROPOSAL_IMAGE);
					
					kbProposals.add(kbProposal);
				}
			}
		} else if(expr.getType() == ELObjectType.EL_ARGUMENT_INVOCATION) {
			Set<TypeInfoCollector.MemberPresentation> proposalsToFilter = new TreeSet<TypeInfoCollector.MemberPresentation>(TypeInfoCollector.MEMBER_PRESENTATION_COMPARATOR);
			boolean isMessages = false;
			for (TypeInfoCollector.MemberInfo mbr : members) {
				if (mbr instanceof MessagesInfo) {
					isMessages = true;
					Collection<String> keys = ((MessagesInfo)mbr).getKeys();
					for (String key : keys) {
						proposalsToFilter.add(new TypeInfoCollector.MemberPresentation(key, mbr));
					}
					continue;
				}
				if (mbr.getMemberType() == null) continue;
				try {
					if(TypeInfoCollector.isInstanceofType(mbr.getMemberType(), "java.util.Map")) {
						status.setMapOrCollectionOrBundleAmoungTheTokens();
						//if map/collection is parameterized, we might return member info for value type. 
						return;
					}
				} catch (JavaModelException jme) {
					//ignore
				}
				status.setMemberOfResolvedOperand(mbr);
			}

			String filter = expr.getMemberName();
			boolean bSurroundWithQuotes = false;
			if(filter == null) {
				filter = "";
				bSurroundWithQuotes = true;
			} else {
				if((filter.startsWith("'") || filter.startsWith("\""))
					&& (filter.endsWith("'") || filter.endsWith("\""))) {
					filter = filter.substring(1, filter.length() - 1);
				} else {
					//Value is set as expression itself, we cannot compute it
					if(isMessages) status.setMapOrCollectionOrBundleAmoungTheTokens();
					return;
				}
			}
			
			for (TypeInfoCollector.MemberPresentation proposal : proposalsToFilter) {
				if(returnEqualedVariablesOnly) {
					// This is used for validation.
					if (proposal.getPresentation().equals(filter)) {
						TextProposal kbProposal = new TextProposal();
						kbProposal.setReplacementString(proposal.getPresentation());
						
						if (proposal.getMember() instanceof MessagesInfo) {
							kbProposal.setImage(SEAM_MESSAGES_PROPOSAL_IMAGE);
						} else {
							kbProposal.setImage(SEAM_EL_PROPOSAL_IMAGE);
						}
						
						kbProposals.add(kbProposal);

						status.setMemberOfResolvedOperand(proposal.getMember());
						if(status.getUnpairedGettersOrSetters()!=null) {
							TypeInfoCollector.MethodInfo unpirMethod = status.getUnpairedGettersOrSetters().get(filter);
							status.clearUnpairedGettersOrSetters();
							if(unpirMethod!=null) {
								status.getUnpairedGettersOrSetters().put(filter, unpirMethod);
							}
						}
						break;
					}
				} else if (proposal.getPresentation().startsWith(filter)) {
					// This is used for CA.
					TextProposal kbProposal = new TextProposal();
					
					String replacementString = proposal.getPresentation().substring(filter.length());
					if (bSurroundWithQuotes) {
						replacementString = "'" + replacementString + "']";
					}
					
					kbProposal.setReplacementString(replacementString);
					kbProposal.setImage(SEAM_EL_PROPOSAL_IMAGE);
					
					kbProposals.add(kbProposal);
				}
			}
		}
		status.setProposals(kbProposals);
		if (status.isOK()){
			status.setLastResolvedToken(expr);
		}
	}

/**
	private String computeVariableName(List<ELOperandToken> tokens){
		if (tokens == null)
			tokens = new ArrayList<ELOperandToken>();
		StringBuffer sb = new StringBuffer();
		for (ELOperandToken token : tokens) {
			if (token.getType() == ELOperandToken.EL_VARIABLE_NAME_TOKEN ||
					token.getType() == ELOperandToken.EL_PROPERTY_NAME_TOKEN ||
					token.getType() == ELOperandToken.EL_METHOD_TOKEN ||
					token.getType() == ELOperandToken.EL_SEPARATOR_TOKEN) {
				sb.append(token.getText());
			}
		}
		return sb.toString();
	}
*/

/**
	private boolean areEqualExpressions(List<ELOperandToken>first, List<ELOperandToken>second) {
		if (first == null || second == null)
			return (first == second);

		if (first.size() != second.size())
			return false;

		for (int i = 0; i < first.size(); i++) {
			if (!first.get(i).equals(second.get(i)))
				return false;
		}
		return true;
	}
*/

	/* Returns scope for the resource
	 * 
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


	
	
	public List<ISeamContextVariable> resolveVariables(ISeamProject project, ScopeType scope, ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames) {
		List<ISeamContextVariable>resolvedVars = new ArrayList<ISeamContextVariable>();
		
		if (project == null)
			return new ArrayList<ISeamContextVariable>(); 
		
		String varName = expr.toString();

		if (varName != null) {
			resolvedVars = SeamExpressionResolver.resolveVariables(project, scope, varName, onlyEqualNames);
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
		else if(varName != null && (varName.startsWith("\"") || varName.startsWith("'"))
								&& (varName.endsWith("\"") || varName.endsWith("'"))) {
			IJavaProject jp = EclipseResourceUtil.getJavaProject(project.getProject());
			try {
				IType type = jp.findType("java.lang.String");
				if(type != null) {
					IMethod m = type.getMethod("toString", new String[0]);
					if(m != null) {
						ISeamContextVariable v = new StringVariable(m);
						List<ISeamContextVariable> newResolvedVars = new ArrayList<ISeamContextVariable>();
						newResolvedVars.add(v);
						return newResolvedVars;
					}
				}
			} catch (JavaModelException e) {
				//ignore
			}
			
		}
		return new ArrayList<ISeamContextVariable>(); 
	}

	/**
	 * Removes duplicates of completion strings
	 *
	 * @param suggestions a list of suggestions ({@link String}).
	 * @return a list of unique completion suggestions.
	 */
	public List<TextProposal> makeKbUnique(List<TextProposal> suggestions) {
		HashSet<String> present = new HashSet<String>();
		ArrayList<TextProposal> unique= new ArrayList<TextProposal>();

		if (suggestions == null)
			return unique;

		for (TextProposal item : suggestions) {
			if (!present.contains(item.getReplacementString())) {
				present.add(item.getReplacementString());
				unique.add(item);
			}
		}

		present.clear();
		return unique;
	}

	/**
	 * Calculates the EX expression operand string
	 * 
	 * @param viewer
	 * @param offset
	 * @param start  start of relevant region in document
	 * @param end    end of relevant region in document
	 * @return
	 * @throws BadLocationException
	 */
	public String getPrefix(ITextViewer viewer, int offset, int start, int end) throws StringIndexOutOfBoundsException {
		IDocument doc= viewer.getDocument();
		if (doc == null || offset > doc.getLength())
			return null;
		return getPrefix(doc, offset, start, end);
	}

	/**
	 * Calculates the EX expression operand string
	 * 
	 * @param viewer
	 * @param offset
	 * @param start  start of relevant region in document
	 * @param end    end of relevant region in document
	 * @return
	 * @throws StringIndexOutOfBoundsException
	 */
	public String getPrefix(IDocument document, int offset, int start, int end) throws StringIndexOutOfBoundsException {
		if (document == null || document.get() == null || offset > document.get().length())
			return null;
		ELInvocationExpression expr = findExpressionAtOffset(document, offset, start, end);
		if (expr == null)
			return null;
		return document.get().substring(expr.getStartPosition(), offset);
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
	 * @param document 
	 * @param prefix the prefix to search for
	 * @param position Offset of the prefix 
	 */
	public List<IJavaElement> getJavaElementsForExpression(ISeamProject project, IFile file, String expression) throws BadLocationException, StringIndexOutOfBoundsException {
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

		ELOperandResolveStatus status = resolveELOperand(file, expr, true, vars, varSearcher);
		if (status.isOK()) {
			MemberInfo member = status.getMemberOfResolvedOperand();
			if (member != null) {
				IJavaElement el = member.getJavaElement();
				if (el != null) {
					res.add(el);
					return res;
				}
			}
		}
		return res;
	}

	/**
	 * 
	 * @param document
	 * @param offset
	 * @param start  start of relevant region in document
	 * @param end    end of relevant region in document
	 * @return
	 */
	public static ELInvocationExpression findExpressionAtOffset(IDocument document, int offset, int start, int end) {
		return findExpressionAtOffset(document.get(), offset, start, end);
	}

	public static ELInvocationExpression findExpressionAtOffset(String content, int offset, int start, int end) {

		//TODO this naive calculations should be removed; 
		//	   this method should be called with reasonable start and end. 
		if(start <= 0) start = guessStart(content, offset);
		if(end >= content.length()) end = guessEnd(content, offset);
		
		ELParser parser = factory.createParser();
		ELModel model = parser.parse(content, start, end - start);
		
		return ELUtil.findExpression(model, offset);
	}

	static int guessStart(String content, int offset) {
		if(offset > content.length()) offset = content.length();
		if(offset < 2) return 0;
		int s = offset - 2;
		
		while(s >= 0) {
			if(content.charAt(s + 1) == '{') {
				char ch = content.charAt(s);
				if(ch == '#' || ch == '$') return s;
			}
			s--;
		}
		return 0;
	}

	static int guessEnd(String content, int offset) {
		if(offset >= content.length()) return content.length();
		while(offset < content.length()) {
			if(content.charAt(offset) == '}') return offset;
			offset++;
		}
		return content.length();
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
}

class StringVariable implements ISeamContextVariable, ISeamJavaSourceReference {
	IMember member;
	public StringVariable(IMember member) {
		this.member = member;
	}
	public ScopeType getScope() {
		return ScopeType.APPLICATION;
	}
	public void setName(String name) {
	}
	public void setScope(ScopeType type) {
	}
	public ITextSourceReference getLocationFor(String path) {
		return null;
	}
	public String getName() {
		return "String";
	}
	public ISeamElement getParent() {
		return null;
	}
	public IResource getResource() {
		return null;
	}
	public ISeamProject getSeamProject() {
		return null;
	}
	public IPath getSourcePath() {
		return null;
	}
	public void loadXML(Element element, Properties context) {
	}
	public List<Change> merge(ISeamElement s) {
		return null;
	}
	public Element toXML(Element parent, Properties context) {
		return null;
	}
	public Object getAdapter(Class adapter) {
		return null;
	}
	public IMember getSourceMember() {
		return member;
	}
	public int getLength() {
		return 0;
	}
	public int getStartPosition() {
		return 0;
	}
	public StringVariable clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}