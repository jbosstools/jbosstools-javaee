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
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInstance;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELMethodInvocation;
import org.jboss.tools.common.el.core.model.ELModel;
import org.jboss.tools.common.el.core.model.ELObjectType;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.el.core.parser.ELParser;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.LexicalToken;
import org.jboss.tools.common.model.util.TypeInfoCollector;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.el.SeamExpressionResolver.MessagesInfo;

/**
 * Utility class used to collect info for EL
 * 
 * @author Jeremy
 */
public final class SeamELCompletionEngine {

	/**
	 * Constructs SeamELCompletionEngine object
	 */
	public SeamELCompletionEngine() {
	}

	/**
	 * Create the array of suggestions. 
	 * 
	 * @param project Seam project 
	 * @param file File 
	 * @param document 
	 * @param prefix the prefix to search for
	 * @param position Offset of the prefix
	 * @param vars - 'var' attributes which can be used in this EL 
	 * @return the list of all possible suggestions
	 * @throws BadLocationException if accessing the current document fails
	 */
	public List<String> getCompletions(ISeamProject project, IFile file, IDocument document, CharSequence prefix, 
			int position, List<Var> vars) throws BadLocationException, StringIndexOutOfBoundsException {
		String documentContent = null;
		if(document!=null) {
			documentContent = document.get();
		}
		return getCompletions(project, file, documentContent, prefix, position, false, vars);
	}

	/**
	 * Create the array of suggestions. 
	 * @param project Seam project 
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
	public List<String> getCompletions(ISeamProject project, IFile file, String documentContent, CharSequence prefix, 
			int position, boolean returnEqualedVariablesOnly, List<Var> vars) throws BadLocationException, StringIndexOutOfBoundsException {
		List<String> completions = new ArrayList<String>();
		
		//TODO change algorithm for finding operand.
		String prefix2 = SeamELCompletionEngine.getPrefix(documentContent, position + prefix.length());

		SeamELOperandResolveStatus status = resolveSeamELOperand(project, file, parseOperand(prefix2), returnEqualedVariablesOnly, vars, new ElVarSearcher(project, file, this));
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

	public SeamELOperandResolveStatus resolveSeamELOperand(ISeamProject project, IFile file, ELExpression operand,  
			boolean returnEqualedVariablesOnly, List<Var> vars, ElVarSearcher varSearcher) throws BadLocationException, StringIndexOutOfBoundsException {
		if(operand == null) {
			//TODO
			return new SeamELOperandResolveStatus(null);
		}
		String oldEl = operand.getText();
		Var var = varSearcher.findVarForEl(oldEl, vars, true);
		String suffix = "";
		String newEl = oldEl;
		if(var!=null) {
			TypeInfoCollector.MemberInfo member = resolveSeamEL(project, file, var.getElToken());
			if(member!=null && !member.getType().isArray()) {
				IType type = member.getMemberType();
				if(type!=null) {
					try {
						if(TypeInfoCollector.isInstanceofType(type, "java.util.Map")) {
							suffix = collectionAdditionForMapDataModel;
						} else {
							suffix = collectionAdditionForCollectionDataModel;
						}
					} catch (JavaModelException e) {
						SeamCorePlugin.getPluginLog().logError(e);
					}
				}
			}
			if(var.getElToken() != null) {
				newEl = var.getElToken().getText() + suffix + oldEl.substring(var.getName().length());
			}
		}
		boolean prefixWasChanged = newEl != oldEl;
		ELExpression newOperand = (prefixWasChanged) ? parseOperand(newEl) : operand;

		SeamELOperandResolveStatus status = resolveSeamELOperand(project, file, newOperand, returnEqualedVariablesOnly);

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
			status.getProposals().addAll(getVarNameProposals(vars, operand.toString()));
		}
		return status;
	}

	public static ELExpression parseOperand(String operand) {
		if(operand == null) return null;
		String el = (operand.indexOf("#{") < 0) ? "#{" + operand + "}" : operand;
		ELParser p = ELParserFactory.createJbossParser();
		ELModel model = p.parse(el);
		List<ELInstance> is = model.getInstances();
		if(is.size() == 0) return null;
		return is.get(0).getExpression();
	}

	/**
	 * Returns MemberInfo for last segment of EL. Null if El is not resolved.
	 * @param project
	 * @param file
	 * @param operand EL without #{}
	 * @return MemberInfo for last segment of EL. Null if El is not resolved.
	 * @throws BadLocationException
	 * @throws StringIndexOutOfBoundsException
	 */
	public TypeInfoCollector.MemberInfo resolveSeamEL(ISeamProject project, IFile file, ELExpression operand) throws BadLocationException, StringIndexOutOfBoundsException {
		if(!(operand instanceof ELInvocationExpression)) return null;
		SeamELOperandResolveStatus status = resolveSeamELOperand(project, file, operand, true);
		
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
		ELParser parser = ELParserFactory.createJbossParser();
		ELModel model = parser.parse(el);
		List<ELInstance> is = model.getInstances();
		if(is.size() < 1) return resolvedVariables;
		
		ELExpression ex = is.get(0).getExpression();
		if(!(ex instanceof ELInvocationExpression)) return resolvedVariables;
		
		ELInvocationExpression expr = (ELInvocationExpression)ex;
		
		boolean isIncomplete = expr.getType() == ELObjectType.EL_PROPERTY_INVOCATION
				&& ((ELPropertyInvocation) expr).getName() == null;

		SeamELOperandResolveStatus status = new SeamELOperandResolveStatus(expr);
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

	public SeamELOperandResolveStatus resolveSeamELOperand(ISeamProject project, IFile file, ELExpression operand,  
			boolean returnEqualedVariablesOnly) throws BadLocationException, StringIndexOutOfBoundsException {
		if(!(operand instanceof ELInvocationExpression)) {
			return new SeamELOperandResolveStatus(null);
		}
		
		ELInvocationExpression expr = (ELInvocationExpression)operand;
		boolean isIncomplete = expr.getType() == ELObjectType.EL_PROPERTY_INVOCATION 
			&& ((ELPropertyInvocation)expr).getName() == null;

		SeamELOperandResolveStatus status = new SeamELOperandResolveStatus(expr);
		ELInvocationExpression left = expr;

		List<ISeamContextVariable> resolvedVariables = new ArrayList<ISeamContextVariable>();
		ScopeType scope = getScope(project, file);

		if (expr.getLeft() == null && isIncomplete) {
			resolvedVariables = resolveVariables(project, scope, 
					expr, true, 
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
		if(resolvedVariables != null && resolvedVariables.size() > 0) {
			status.setUsedVariables(resolvedVariables);
		}

		if (status.getResolvedTokens() == null && 
				!returnEqualedVariablesOnly && 
				expr != null && 
				isIncomplete) {
			// no vars are resolved 
			// the tokens are the part of var name ended with a separator (.)
			resolvedVariables = resolveVariables(project, scope, expr, true, returnEqualedVariablesOnly);			
			Set<String> proposals = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			for (ISeamContextVariable var : resolvedVariables) {
				String varName = var.getName();
				if(varName.startsWith(operand.getText())) {
					proposals.add(varName.substring(operand.getLength()));
				}
			}
			status.setProposals(proposals);
			return status;
		}

		// Here we have a list of vars for some part of expression
		// OK. we'll proceed with members of these vars
		if (status.getResolvedTokens() == status.getTokens()) {
			// First segment is the last one
			Set<String> proposals = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			for (ISeamContextVariable var : resolvedVariables) {
				String varName = var.getName();
				if(operand.getLength()<=varName.length()) {
					proposals.add(varName.substring(operand.getLength()));
				} else if(returnEqualedVariablesOnly) {
					proposals.add(varName);
				}
				status.setMemberOfResolvedOperand(SeamExpressionResolver.getMemberInfoByVariable(var, true));
			}
			status.setLastResolvedToken(expr);
			status.setProposals(proposals);
			return status;
		}

		// First segment is found - proceed with next tokens 
		List<TypeInfoCollector.MemberInfo> members = new ArrayList<TypeInfoCollector.MemberInfo>();
		for (ISeamContextVariable var : resolvedVariables) {
			TypeInfoCollector.MemberInfo member = SeamExpressionResolver.getMemberInfoByVariable(var, returnEqualedVariablesOnly);
			if (member != null && !members.contains(member)) 
				members.add(member);
		}
		if(left != null) while(left != expr) {
			left = (ELInvocationExpression)left.getParent();
			if (left != expr) { // inside expression
				members = resolveSegment(left, members, status, returnEqualedVariablesOnly);
			} else { // Last segment
				resolveLastSegment((ELInvocationExpression)operand, members, status, returnEqualedVariablesOnly);
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
			SeamELOperandResolveStatus status,
			boolean returnEqualedVariablesOnly) {
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
				TypeInfoCollector infos = mbr.getTypeCollector();
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
			if (members != null && members.size() > 0)
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
			if (members != null && members.size() > 0)
				status.setLastResolvedToken(expr);
		}
		return members;
	}

	private void resolveLastSegment(ELInvocationExpression expr, 
			List<TypeInfoCollector.MemberInfo> members,
			SeamELOperandResolveStatus status,
			boolean returnEqualedVariablesOnly) {
		Set<String> proposals = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		if (expr.getType() == ELObjectType.EL_PROPERTY_INVOCATION && ((ELPropertyInvocation)expr).getName() == null) {
//		if (token.getType() == ELOperandToken.EL_SEPARATOR_TOKEN) {
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
							proposals.add("['" + key + "']");
						} else {
							proposals.add(key);
						}
					}
					continue;
				}
				if (mbr.getMemberType() == null) {
					continue;
				}
				TypeInfoCollector infos = mbr.getTypeCollector();
				if (TypeInfoCollector.isNotParameterizedCollection(mbr) || TypeInfoCollector.isResourceBundle(mbr.getMemberType())) {
					status.setMapOrCollectionOrBundleAmoungTheTokens();
				}
				proposals.addAll(infos.getMethodPresentationStrings());
				proposals.addAll(infos.getPropertyPresentationStrings(status.getUnpairedGettersOrSetters()));
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
				LexicalToken lt = (expr instanceof ELPropertyInvocation) 
					? ((ELPropertyInvocation)expr).getName()
					: (expr instanceof ELMethodInvocation) 
					? ((ELMethodInvocation)expr).getName()
					: null;
				String filter = lt != null ? lt.getText() : ""; //token.getText();
//				if (filter.indexOf('(') != -1) {
//					filter = filter.substring(0, filter.indexOf('('));
//				}
				if(returnEqualedVariablesOnly) {
					// This is used for validation.
					if (proposal.getPresentation().equals(filter)) {
						proposals.add(proposal.getPresentation());
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
					proposals.add(proposal.getPresentation().substring(filter.length()));
				}
			}
		}
		status.setProposals(proposals);
		if (status.isOK()){
			status.setLastResolvedToken(expr);
		}
	}
	
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

	/*
	 * Compares to tokenized expressions.
	 * 
	 * @param first
	 * @param second
	 * @return boolean true if two expressions are equal
	 */
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

	/* Returns scope for the resource
	 * 
	 * @param project
	 * @param resource
	 * @return
	 */
	private ScopeType getScope(ISeamProject project, IResource resource) {
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

	/*
	 * Tries to resolve variables by part of expression
	 *  
	 * @param project
	 * @param scope
	 * @param part
	 * @param tokens
	 * @return
	 */
	private List<ISeamContextVariable> resolveVariables(ISeamProject project, ScopeType scope, List<ELOperandToken>part, List<ELOperandToken> tokens, boolean onlyEqualNames) {
		List<ISeamContextVariable>resolvedVars = new ArrayList<ISeamContextVariable>();
		String varName = computeVariableName(part);
		if (varName != null) {
			resolvedVars = SeamExpressionResolver.resolveVariables(project, scope, varName, onlyEqualNames);
		}
		if (resolvedVars != null && resolvedVars.size() > 0) {
			List<ISeamContextVariable> newResolvedVars = new ArrayList<ISeamContextVariable>();
			for (ISeamContextVariable var : resolvedVars) {
				if(!areEqualExpressions(part, tokens)) {
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

	private List<ISeamContextVariable> resolveVariables(ISeamProject project, ScopeType scope, ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames) {
		List<ISeamContextVariable>resolvedVars = new ArrayList<ISeamContextVariable>();
		String varName = expr.toString();
		if (varName != null) {
			resolvedVars = SeamExpressionResolver.resolveVariables(project, scope, varName, onlyEqualNames);
		}
		if (resolvedVars != null && resolvedVars.size() > 0) {
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
	 * Creates and returns list of possible variable name combinations from expression starting from the longest name
	 * 
	 * @param prefix
	 * @return
	 */
	public static List<List<ELOperandToken>> getPossibleVarsFromPrefix(List<ELOperandToken>prefix) {
		ArrayList<List<ELOperandToken>> result = new ArrayList<List<ELOperandToken>>();
		for (int i = 0; prefix != null && i < prefix.size(); i++) {
			ELOperandToken lastToken = prefix.get(i);
			if (lastToken.getType() != ELOperandToken.EL_SEPARATOR_TOKEN) {
				ArrayList<ELOperandToken> prefixPart = new ArrayList<ELOperandToken>();
				for (int j = 0; j <= i; j++) {
					prefixPart.add(prefix.get(j));
				}
				result.add(0, prefixPart);
			}
		}
		return result;
	}

	/**
	 * Removes duplicates of completion strings
	 *
	 * @param suggestions a list of suggestions ({@link String}).
	 * @return a list of unique completion suggestions.
	 */
	public List<String> makeUnique(List<String> suggestions) {
		HashSet<String> present = new HashSet<String>();
		ArrayList<String> unique= new ArrayList<String>();

		if (suggestions == null)
			return unique;

		for (String item : suggestions) {
			if (!present.contains(item)) {
				present.add(item);
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
	 * @return
	 * @throws BadLocationException
	 */
	public static String getPrefix(ITextViewer viewer, int offset) throws StringIndexOutOfBoundsException {
		IDocument doc= viewer.getDocument();
		if (doc == null || offset > doc.getLength())
			return null;
		return getPrefix(doc.get(), offset);
	}

	/**
	 * Calculates the EX expression operand string
	 * 
	 * @param viewer
	 * @param offset
	 * @return
	 * @throws StringIndexOutOfBoundsException
	 */
	public static String getPrefix(String documentContent, int offset) throws StringIndexOutOfBoundsException {
		if (documentContent == null || offset > documentContent.length())
			return null;

		SeamELOperandTokenizer tokenizer = new SeamELOperandTokenizer(documentContent, offset);
		List<ELOperandToken> tokens = tokenizer.getTokens();

		if (tokens == null || tokens.size() == 0)
			return null;

		return documentContent.substring(tokens.get(0).start, offset);
	}

	/**
	 * @param documentContent
	 * @param offset
	 * @param region
	 * @return 
	 * @throws StringIndexOutOfBoundsException
	 */
	public String getJavaElementExpression(String documentContent, int offset, IRegion region) throws StringIndexOutOfBoundsException {
		if (documentContent == null || offset > documentContent.length())
			return null;

		SeamELOperandTokenizer tokenizer = new SeamELOperandTokenizer(documentContent, region.getOffset() + region.getLength());
		List<ELOperandToken> tokens = tokenizer.getTokens();

		if (tokens == null || tokens.size() == 0)
			return null;

		List<List<ELOperandToken>> vars = getPossibleVarsFromPrefix(tokens);
		if (vars == null) 
			return null;

		String prefixPart = documentContent.substring(tokens.get(0).start, offset);

		// Search from the shortest variation to the longest one
		for (int i = vars.size() - 1; i >= 0; i--) { 
			List<ELOperandToken>var = vars.get(i);
			String varText = computeVariableName(var); 
			if (varText != null && varText.startsWith(prefixPart)) {
				return varText; 
			}
		}
		return null;
	}

	/**
	 * Create the array of suggestions from {@link ELOperandToken} list. 
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
			List<ELOperandToken> tokens) throws BadLocationException, StringIndexOutOfBoundsException {
		List<IJavaElement> res= new ArrayList<IJavaElement>();
		if (tokens == null || tokens.size() == 0 || tokens.get(tokens.size() - 1).getType() == ELOperandToken.EL_SEPARATOR_TOKEN)
			return res;

		List<ELOperandToken> resolvedExpressionPart = new ArrayList<ELOperandToken>();
		List<ISeamContextVariable> resolvedVariables = new ArrayList<ISeamContextVariable>();
		ScopeType scope = getScope(project, file);
		List<List<ELOperandToken>> variations = getPossibleVarsFromPrefix(tokens);

		if (variations.isEmpty()) {
			resolvedVariables = resolveVariables(project, scope, tokens, tokens, true);
		} else {
			for (List<ELOperandToken> variation : variations) {
				List<ISeamContextVariable>resolvedVars = new ArrayList<ISeamContextVariable>();
				resolvedVars = resolveVariables(project, scope, variation, tokens, true);
				if (resolvedVars != null && !resolvedVars.isEmpty()) {
					resolvedVariables = resolvedVars;
					resolvedExpressionPart = variation;
					break;
				}
			}
		}

		// Here we have a list of vars for some part of expression
		// OK. we'll proceed with members of these vars
		if (areEqualExpressions(resolvedExpressionPart, tokens)) {
			// First segment is the last one
			for (ISeamContextVariable var : resolvedVariables) {
				IMember member = SeamExpressionResolver.getMemberByVariable(var, true);
				if (member instanceof IJavaElement){
					res.add((IJavaElement)member);
				}
			}
			return res;
		}

		// First segment is found - proceed with next tokens 
		int startTokenIndex = (resolvedExpressionPart == null ? 0 : resolvedExpressionPart.size());
		List<TypeInfoCollector.MemberInfo> members = new ArrayList<TypeInfoCollector.MemberInfo>();
		for (ISeamContextVariable var : resolvedVariables) {
			TypeInfoCollector.MemberInfo member = SeamExpressionResolver.getMemberInfoByVariable(var, true);
			if (member != null && !members.contains(member)) 
				members.add(member);
		}
		for (int i = startTokenIndex; 
				tokens != null && i < tokens.size() && 
				members != null && members.size() > 0; 
				i++) {
			ELOperandToken token = tokens.get(i);

			if (i < tokens.size() - 1) { // inside expression
				if (token.getType() == ELOperandToken.EL_SEPARATOR_TOKEN)
					// proceed with next token
					continue;

				if (token.isNameToken()) {
					// Find properties for the token
					String name = token.getText();
					List<TypeInfoCollector.MemberInfo> newMembers = new ArrayList<TypeInfoCollector.MemberInfo>();
					for (TypeInfoCollector.MemberInfo mbr : members) {
						TypeInfoCollector infos = mbr.getTypeCollector();
						List<TypeInfoCollector.MemberInfo> properties = infos.getProperties();
						for (TypeInfoCollector.MemberInfo property : properties) {
							StringBuffer propertyName = new StringBuffer(property.getName());
							if (property instanceof TypeInfoCollector.MethodInfo) { // Setter or getter
								propertyName.delete(0, 3);
								propertyName.setCharAt(0, Character.toLowerCase(propertyName.charAt(0)));
							}
							if (name.equals(propertyName.toString())) {
								newMembers.add(property);
							}
						}
					}
					members = newMembers;
				}
				if (token.getType() == ELOperandToken.EL_METHOD_TOKEN) {
					// Find methods for the token
					String name = token.getText();
					if (name.indexOf('(') != -1) {
						name = name.substring(0, name.indexOf('('));
					}
					List<TypeInfoCollector.MemberInfo> newMembers = new ArrayList<TypeInfoCollector.MemberInfo>();
					for (TypeInfoCollector.MemberInfo mbr : members) {
						TypeInfoCollector infos = mbr.getTypeCollector();
						List<TypeInfoCollector.MemberInfo> methods = infos.getMethods();
						for (TypeInfoCollector.MemberInfo method : methods) {
							if (method instanceof TypeInfoCollector.MethodInfo 
									&& name.equals(method.getName())) {
								newMembers.add(method);
							}
						}
					}
					members = newMembers;
				}
			} else { // Last segment
				List<IJavaElement> javaElements = new ArrayList<IJavaElement>();
				if (token.getType() == ELOperandToken.EL_VARIABLE_NAME_TOKEN ||
					token.getType() == ELOperandToken.EL_PROPERTY_NAME_TOKEN ||
					token.getType() == ELOperandToken.EL_METHOD_TOKEN) {
					// return filtered methods + properties 
					List<TypeInfoCollector.MemberInfo> javaElementInfosToFilter = new ArrayList<TypeInfoCollector.MemberInfo>(); 
					for (TypeInfoCollector.MemberInfo mbr : members) {
						TypeInfoCollector infos = mbr.getTypeCollector();
						javaElementInfosToFilter.addAll(infos.getMethods());
						javaElementInfosToFilter.addAll(infos.getProperties());
					}

					for (TypeInfoCollector.MemberInfo info : javaElementInfosToFilter) {
						// We do expect nothing but name for method tokens (No round brackets)
						String filter = token.getText();
						if (token.getType() == ELOperandToken.EL_METHOD_TOKEN) {
							if (filter.indexOf('(') >=0)
								filter = filter.substring(0, filter.indexOf('('));
						}
						// This is used for validation.
						if (info.getName().equals(filter)) {
							javaElements.add(info.getJavaElement());
						} else {
							if (info instanceof TypeInfoCollector.MethodInfo) {
								TypeInfoCollector.MethodInfo methodInfo = (TypeInfoCollector.MethodInfo)info;
								if(methodInfo.isGetter() || methodInfo.isSetter()) {
									StringBuffer name = new StringBuffer(methodInfo.getName());
									if(methodInfo.getName().startsWith("i")) { //$NON-NLS-1$
										name.delete(0, 2);
									} else {
										name.delete(0, 3);
									}
									name.setCharAt(0, Character.toLowerCase(name.charAt(0)));
									String propertyName = name.toString();
									if (propertyName.equals(filter)) {
										javaElements.add(methodInfo.getJavaElement());
									}
								}
							}
						}
					}
				}
				res.addAll(javaElements);
			}
		}
		return res;
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
		if (expr == null 
			|| (expr.getType() == ELObjectType.EL_PROPERTY_INVOCATION 
					&& expr.getMemberName() == null)) {
			return res;
		}

		List<ELOperandToken> resolvedExpressionPart = new ArrayList<ELOperandToken>();
		List<ISeamContextVariable> resolvedVariables = new ArrayList<ISeamContextVariable>();
		ScopeType scope = getScope(project, file);
		ELInvocationExpression left = expr;

		while(left != null) {
			List<ISeamContextVariable>resolvedVars = new ArrayList<ISeamContextVariable>();
			resolvedVars = resolveVariables(project, scope, 
					left, left == expr, 
					true);
			if (resolvedVars != null && !resolvedVars.isEmpty()) {
				resolvedVariables = resolvedVars;
				break;
			}
			left = (ELInvocationExpression)left.getLeft();
		} 

		// Here we have a list of vars for some part of expression
		// OK. we'll proceed with members of these vars
		if (left == expr) {
			// First segment is the last one
			for (ISeamContextVariable var : resolvedVariables) {
				IMember member = SeamExpressionResolver.getMemberByVariable(var, true);
				if (member instanceof IJavaElement){
					res.add((IJavaElement)member);
				}
			}
			return res;
		}

		// First segment is found - proceed with next tokens 
		int startTokenIndex = (resolvedExpressionPart == null ? 0 : resolvedExpressionPart.size());
		List<TypeInfoCollector.MemberInfo> members = new ArrayList<TypeInfoCollector.MemberInfo>();
		for (ISeamContextVariable var : resolvedVariables) {
			TypeInfoCollector.MemberInfo member = SeamExpressionResolver.getMemberInfoByVariable(var, true);
			if (member != null && !members.contains(member)) 
				members.add(member);
		}

		if(left != null) while(left != expr) {
			left = (ELInvocationExpression)left.getParent();
			if (left != expr) { // inside expression
				members = resolveSegment(left, members);
			} else { // Last segment
				resolveLastSegment(expr, members, res);
				break;
			}
		}

		return res;
	}
	
	private List<TypeInfoCollector.MemberInfo> resolveSegment(ELInvocationExpression expr, 
			List<TypeInfoCollector.MemberInfo> members) {

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
				TypeInfoCollector infos = mbr.getTypeCollector();
				List<TypeInfoCollector.MemberInfo> properties = infos.getProperties();
				for (TypeInfoCollector.MemberInfo property : properties) {
					StringBuffer propertyName = new StringBuffer(property.getName());
					if (property instanceof TypeInfoCollector.MethodInfo) { // Setter or getter
						propertyName.delete(0, 3);
						propertyName.setCharAt(0, Character.toLowerCase(propertyName.charAt(0)));
					}
					if (name.equals(propertyName.toString())) {
						newMembers.add(property);
					}
				}
			}
			members = newMembers;
		}
		if (expr.getType() == ELObjectType.EL_METHOD_INVOCATION) {
			// Find methods for the token
			List<TypeInfoCollector.MemberInfo> newMembers = new ArrayList<TypeInfoCollector.MemberInfo>();
			for (TypeInfoCollector.MemberInfo mbr : members) {
				TypeInfoCollector infos = mbr.getTypeCollector();
				List<TypeInfoCollector.MemberInfo> methods = infos.getMethods();
				for (TypeInfoCollector.MemberInfo method : methods) {
					if (method instanceof TypeInfoCollector.MethodInfo 
							&& name.equals(method.getName())) {
						newMembers.add(method);
					}
				}
			}
			members = newMembers;
		}

		return members;
	}
			

	private void resolveLastSegment(ELInvocationExpression expr, 
			List<TypeInfoCollector.MemberInfo> members,
			List<IJavaElement> res
			) {
		List<IJavaElement> javaElements = new ArrayList<IJavaElement>();
		if(expr.getType() != ELObjectType.EL_ARGUMENT_INVOCATION) {
			// return filtered methods + properties 
			List<TypeInfoCollector.MemberInfo> javaElementInfosToFilter = new ArrayList<TypeInfoCollector.MemberInfo>(); 
			for (TypeInfoCollector.MemberInfo mbr : members) {
				TypeInfoCollector infos = mbr.getTypeCollector();
				javaElementInfosToFilter.addAll(infos.getMethods());
				javaElementInfosToFilter.addAll(infos.getProperties());
			}

			for (TypeInfoCollector.MemberInfo info : javaElementInfosToFilter) {
				// We do expect nothing but name for method tokens (No round brackets)
				LexicalToken lt = (expr instanceof ELPropertyInvocation) 
					? ((ELPropertyInvocation)expr).getName()
					: (expr instanceof ELMethodInvocation) 
					? ((ELMethodInvocation)expr).getName()
					: null;
				String filter = lt != null ? lt.getText() : ""; //token.getText();

				// This is used for validation.
				if (info.getName().equals(filter)) {
					javaElements.add(info.getJavaElement());
				} else {
					if (info instanceof TypeInfoCollector.MethodInfo) {
						TypeInfoCollector.MethodInfo methodInfo = (TypeInfoCollector.MethodInfo)info;
						if(methodInfo.isGetter() || methodInfo.isSetter()) {
							StringBuffer name = new StringBuffer(methodInfo.getName());
							if(methodInfo.getName().startsWith("i")) { //$NON-NLS-1$
								name.delete(0, 2);
							} else {
								name.delete(0, 3);
							}
							name.setCharAt(0, Character.toLowerCase(name.charAt(0)));
							String propertyName = name.toString();
							if (propertyName.equals(filter)) {
								javaElements.add(methodInfo.getJavaElement());
							}
						}
					}
				}
			}
		}
		res.addAll(javaElements);
	}

	/**
	  * Returns list of Seam ELOperandToken which are placed under the cursor position
	  * 
	  * @param document
	  * @param offset
	  * @return
	  */
	public static List<ELOperandToken> findTokensAtOffset(IDocument document, int offset) {
		List<ELOperandToken> result = new ArrayList<ELOperandToken>();
		
		int elStart = getELStart(document, offset);
		
		if (elStart == -1) 
			elStart = offset;
	
		SeamELOperandTokenizerForward tokenizer = new SeamELOperandTokenizerForward(document, elStart);
		List<ELOperandToken> tokens = tokenizer.getTokens();
	
		ELOperandToken lastSeparator = null;
		for (int i = 0; tokens != null && i < tokens.size(); i++) {
			ELOperandToken token = tokens.get(i);
			if (token.getType() == ELOperandToken.EL_SEPARATOR_TOKEN) {
				lastSeparator = token;
				continue;
			}
			if (token.getType() == ELOperandToken.EL_VARIABLE_NAME_TOKEN ||
					token.getType() == ELOperandToken.EL_METHOD_TOKEN ||
					token.getType() == ELOperandToken.EL_PROPERTY_NAME_TOKEN) {
				if (token.getStart() <= offset) {
					if (lastSeparator != null) 
						result.add(lastSeparator);
					result.add(token);
				} else {
					// Stop processing. We're not interrested of the rest of tokens
					break;
				}
			}
		}
		
		return result;
	}

	/* 
	 * Scans the document from the offset to the beginning to find start of Seam EL operand
	 * Returns the start position of first Seam EL operand token 
	 */
	private static int getELStart(IDocument document, int offset) {
		SeamELOperandTokenizer tokenizer = new SeamELOperandTokenizer(document, offset);
		List<ELOperandToken> tokens = tokenizer.getTokens();

		if (tokens == null || tokens.size() == 0)
			return -1;
		
		ELOperandToken firstToken = tokens.get(0);
		return firstToken.getStart();
	}
	
}
