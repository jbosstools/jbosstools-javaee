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
package org.jboss.tools.jsf.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.el.core.model.ELArgumentInvocation;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInstance;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELMethodInvocation;
import org.jboss.tools.common.el.core.model.ELModel;
import org.jboss.tools.common.el.core.model.ELObjectType;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
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
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.model.pv.JSFPromptingProvider;
import org.jboss.tools.jst.web.kb.IPageContext;

/**
 * Utility class used to collect info for EL
 * 
 * @author Viacheslav Kabanovich
 */
public class JSFELCompletionEngine implements ELResolver, ELCompletionEngine {
	private static final Image JSF_EL_PROPOSAL_IMAGE = JSFModelPlugin.getDefault().getImage(JSFModelPlugin.CA_JSF_EL_IMAGE_PATH);

	public Image getELProposalImage() {
		return JSF_EL_PROPOSAL_IMAGE;
	}

	private static ELParserFactory factory = ELParserUtil.getDefaultFactory();

	//copied
	public List<TextProposal> getCompletions(String elString,
			boolean returnEqualedVariablesOnly, int position, ELContext context) {
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
			JSFModelPlugin.getPluginLog().logError(e);
		} catch (BadLocationException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
		return proposals;
	}

	//copied
	public List<TextProposal> getCompletions(IFile file, IDocument document, CharSequence prefix, 
			int position, boolean returnEqualedVariablesOnly, List<Var> vars) throws BadLocationException, StringIndexOutOfBoundsException {
		List<TextProposal> completions = new ArrayList<TextProposal>();
		
		ELOperandResolveStatus status = resolveELOperand(file, parseOperand("" + prefix), returnEqualedVariablesOnly, vars, new ElVarSearcher(file, this));
		if (status.isOK()) {
			completions.addAll(status.getProposals());
		}

		return completions;
	}

	//copied
	public ELExpression parseOperand(String operand) {
		if(operand == null) return null;
		String el = (operand.indexOf("#{") < 0 && operand.indexOf("${") < 0) ? "#{" + operand + "}" : operand;
		ELParser p = factory.createParser();
		ELModel model = p.parse(el);
		List<ELInstance> is = model.getInstances();
		if(is.isEmpty()) return null;
		return is.get(0).getExpression();
	}

	public ELOperandResolveStatus resolveELOperand(ELExpression operand,
			ELContext context, boolean returnEqualedVariablesOnly) {
		return null;
	}

	public ELParserFactory getParserFactory() {
		return factory;
	}

	//copied
	private static final String collectionAdditionForCollectionDataModel = ".iterator().next()";
	private static final String collectionAdditionForMapDataModel = ".entrySet().iterator().next()";

	//copied
	public ELOperandResolveStatus resolveELOperand(IFile file,
			ELExpression operand, boolean returnEqualedVariablesOnly,
			List<Var> vars, ElVarSearcher varSearcher)
			throws BadLocationException, StringIndexOutOfBoundsException {
		if(operand == null) {
			//TODO
			return new ELOperandResolveStatus(null);
		}
		String oldEl = operand.getText();
		Var var = varSearcher.findVarForEl(oldEl, vars, true);
		String suffix = "";
		String newEl = oldEl;
		TypeInfoCollector.MemberInfo member = null;
		boolean isArray = false;
		if(var!=null) {
			member = resolveJSFEL(file, var.getElToken(), true);
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
							JSFModelPlugin.getPluginLog().logError(e);
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

		ELOperandResolveStatus status = resolveELOperand(file, newOperand, returnEqualedVariablesOnly, prefixWasChanged);

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
					proposal.setImage(getELProposalImage());
					status.getProposals().add(proposal);
				}
			}
		}
		return status;
	}

	//copied - JSF
	public TypeInfoCollector.MemberInfo resolveJSFEL(IFile file, ELExpression operand, boolean varIsUsed) throws BadLocationException, StringIndexOutOfBoundsException {
		if(!(operand instanceof ELInvocationExpression)) return null;
		ELOperandResolveStatus status = resolveELOperand(file, operand, true, varIsUsed);
		return status.getMemberOfResolvedOperand();
	}

	//copied
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





	public ELOperandResolveStatus resolveELOperand(IFile file, ELExpression operand,  
			boolean returnEqualedVariablesOnly, boolean varIsUsed) throws BadLocationException, StringIndexOutOfBoundsException {
		if(!(operand instanceof ELInvocationExpression) || file == null) {
			return new ELOperandResolveStatus(null);
		}

		ELInvocationExpression expr = (ELInvocationExpression)operand;
		boolean isIncomplete = expr.getType() == ELObjectType.EL_PROPERTY_INVOCATION 
			&& ((ELPropertyInvocation)expr).getName() == null;
		boolean isArgument = expr.getType() == ELObjectType.EL_ARGUMENT_INVOCATION;

		ELOperandResolveStatus status = new ELOperandResolveStatus(expr);
		ELInvocationExpression left = expr;

		List<IJSFVariable> resolvedVariables = new ArrayList<IJSFVariable>();
		IModelNature project = EclipseResourceUtil.getModelNature(file.getProject());

		if (expr.getLeft() != null && isArgument) {
			left = expr.getLeft();
			resolvedVariables = resolveVariables(project, left, false, 
					true); 	// is Final and equal names are because of 
							// we have no more to resolve the parts of expression, 
							// but we have to resolve arguments of probably a message component
		} else if (expr.getLeft() == null && isIncomplete) {
			resolvedVariables = resolveVariables(project, expr, true, 
					returnEqualedVariablesOnly);
		} else {
			while(left != null) {
				List<IJSFVariable>resolvedVars = new ArrayList<IJSFVariable>();
				resolvedVars = resolveVariables(project, 
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
//			status.setUsedVariables(resolvedVariables);
		}

		if (status.getResolvedTokens() == null && 
				!returnEqualedVariablesOnly && 
				expr != null && 
				isIncomplete) {
			// no vars are resolved 
			// the tokens are the part of var name ended with a separator (.)
			resolvedVariables = resolveVariables(project, expr, true, returnEqualedVariablesOnly);			
			Set<TextProposal> proposals = new TreeSet<TextProposal>(TextProposal.KB_PROPOSAL_ORDER);
			for (IJSFVariable var : resolvedVariables) {
				String varName = var.getName();
				if(varName.startsWith(operand.getText())) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(varName.substring(operand.getLength()));
					{
						proposal.setImage(getELProposalImage());
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
			for (IJSFVariable var : resolvedVariables) {
				String varName = var.getName();
				if(operand.getLength()<=varName.length()) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(varName.substring(operand.getLength()));
					{
						proposal.setImage(getELProposalImage());
					}
					proposals.add(proposal);
				} else if(returnEqualedVariablesOnly) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(varName);
					proposal.setImage(getELProposalImage());
					proposals.add(proposal);
				}
				status.setMemberOfResolvedOperand(getMemberInfoByVariable(var, true));
			}
			status.setLastResolvedToken(expr);
			status.setProposals(proposals);
			return status;
		}

		// First segment is found - proceed with next tokens 
		List<TypeInfoCollector.MemberInfo> members = new ArrayList<TypeInfoCollector.MemberInfo>();
		for (IJSFVariable var : resolvedVariables) {
			TypeInfoCollector.MemberInfo member = getMemberInfoByVariable(var, returnEqualedVariablesOnly);
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












//resolving segments
	//copied , removed MessagesInfo case
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
				//removed MessagesInfo case
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
						proposal.setImage(getELProposalImage());
						
						kbProposals.add(proposal);
					}
				}
				Set<String> propertyPresentations = 
					infos.getPropertyPresentationStrings(status.getUnpairedGettersOrSetters());
				if (propertyPresentations != null) {
					for (String presentation : propertyPresentations) {
						TextProposal proposal = new TextProposal();
						proposal.setReplacementString(presentation);
						proposal.setImage(getELProposalImage());
						
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
				//removed MessagesInfo case
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
						
						//removed MessagesInfo case
						{
							kbProposal.setImage(getELProposalImage());
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
					kbProposal.setImage(getELProposalImage());
					
					kbProposals.add(kbProposal);
				}
			}
		} else if(expr.getType() == ELObjectType.EL_ARGUMENT_INVOCATION) {
			Set<TypeInfoCollector.MemberPresentation> proposalsToFilter = new TreeSet<TypeInfoCollector.MemberPresentation>(TypeInfoCollector.MEMBER_PRESENTATION_COMPARATOR);
			boolean isMessages = false;
			for (TypeInfoCollector.MemberInfo mbr : members) {
				//removed MessagesInfo case
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
						
						//removed MessagesInfo case
						{
							kbProposal.setImage(getELProposalImage());
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
					kbProposal.setImage(getELProposalImage());
					
					kbProposals.add(kbProposal);
				}
			}
		}
		status.setProposals(kbProposals);
		if (status.isOK()){
			status.setLastResolvedToken(expr);
		}
	}

	public List<IJSFVariable> resolveVariables(IModelNature project, ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames) {
		List<IJSFVariable>resolvedVars = new ArrayList<IJSFVariable>();
		
		if (project == null)
			return new ArrayList<IJSFVariable>(); 
		
		String varName = expr.toString();

		if (varName != null) {
			resolvedVars = resolveVariables(project, varName, onlyEqualNames);
		}
		if (resolvedVars != null && !resolvedVars.isEmpty()) {
			List<IJSFVariable> newResolvedVars = new ArrayList<IJSFVariable>();
			for (IJSFVariable var : resolvedVars) {
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
		return new ArrayList<IJSFVariable>(); 
	}

	List<IJSFVariable> resolveVariables(IModelNature project, String varName, boolean onlyEqualNames) {
		if(project == null) return null;
		List<IJSFVariable> beans = new JSFPromptingProvider().getVariables(project.getModel());
		List<IJSFVariable> resolvedVariables = new ArrayList<IJSFVariable>();
		for (IJSFVariable variable: beans) {
			String n = variable.getName();
			if(onlyEqualNames) {
				if (n.equals(varName)) {
					resolvedVariables.add(variable);
				}
			} else {
				if (n.startsWith(varName)) {
					resolvedVariables.add(variable);
				}
			}
		}
		return resolvedVariables;
	}

	public static interface IJSFVariable {
		public String getName();
		public IMember getSourceMember();
	}

	TypeInfoCollector.MemberInfo getMemberInfoByVariable(IJSFVariable var, boolean onlyEqualNames) {
		return TypeInfoCollector.createMemberInfo(var.getSourceMember());		
	}

}
