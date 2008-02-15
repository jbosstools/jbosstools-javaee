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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.internal.core.el.ElVarSearcher.Var;

/**
 * Utility class used to find Seam Project content assist proposals
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
		SeamELOperandResolveStatus status = resolveSeamELOperand(project, file, documentContent, prefix, position, returnEqualedVariablesOnly, vars);
		if (status.isOK()) {
			completions.addAll(status.getProposals());
		}
		return completions;
	}

	/**
	 * Status of EL resolving.
	 * @author Jeremy
	 */
	public static class SeamELOperandResolveStatus {
		private List<ELOperandToken> tokens;
		public List<ISeamContextVariable> usedVariables;
		Map<String, TypeInfoCollector.MethodInfo> unpairedGettersOrSetters;
		Set<String> proposals;
		private ELOperandToken lastResolvedToken;
		private boolean isMapOrCollectionOrBundleAmoungTheTokens = false;

		public SeamELOperandResolveStatus(List<ELOperandToken> tokens) {
			this.tokens = tokens;
		}

		public boolean isMapOrCollectionOrBundleAmoungTheTokens() {
			return this.isMapOrCollectionOrBundleAmoungTheTokens;
		}

		public void setMapOrCollectionOrBundleAmoungTheTokens() {
			this.isMapOrCollectionOrBundleAmoungTheTokens = true;
		}

		public boolean isOK() {
			return !getProposals().isEmpty() || isMapOrCollectionOrBundleAmoungTheTokens(); 
		}

		public boolean isError() {
			return !isOK();
		}

		public List<ELOperandToken> getResolvedTokens() {
			List<ELOperandToken> resolvedTokens = new ArrayList<ELOperandToken>();
			int index = tokens.indexOf(lastResolvedToken); // index == -1 means that no tokens are resolved
			for (int i = 0; i < tokens.size() && i <= index; i++) {
				resolvedTokens.add(tokens.get(i));
			}
			return resolvedTokens;
		}

		public List<ELOperandToken> getUnresolvedTokens() {
			List<ELOperandToken> unresolvedTokens = new ArrayList<ELOperandToken>();
			int index = tokens.indexOf(lastResolvedToken); // index == -1 means that no tokens are resolved
			for (int i = index + 1; i < tokens.size(); i++) {
				unresolvedTokens.add(tokens.get(i));
			}
			return unresolvedTokens;
		}

		public ELOperandToken getLastResolvedToken() {
			return lastResolvedToken;
		}

		public void setLastResolvedToken(ELOperandToken lastResolvedToken) {
			this.lastResolvedToken = lastResolvedToken;
		}

		public List<ELOperandToken> getTokens() {
			return tokens;
		}

		public void setTokens(List<ELOperandToken> tokens) {
			this.tokens = tokens;
		}

		public Set<String> getProposals() {
			return proposals == null ? new TreeSet<String>() : proposals;
		}

		public void setProposals(Set<String> proposals) {
			this.proposals = proposals;
		}

		public List<ISeamContextVariable> getUsedVariables() {
			return (usedVariables == null ? new ArrayList<ISeamContextVariable>() : usedVariables);
		}

		public void setUsedVariables(List<ISeamContextVariable> usedVariables) {
			this.usedVariables = usedVariables;
		}

		public Map<String, TypeInfoCollector.MethodInfo> getUnpairedGettersOrSetters() {
			if (unpairedGettersOrSetters == null) {
				unpairedGettersOrSetters = new HashMap<String, TypeInfoCollector.MethodInfo>();
			}
			return unpairedGettersOrSetters;
		}

		public void clearUnpairedGettersOrSetters() {
			getUnpairedGettersOrSetters().clear();
		}
	}

	public SeamELOperandResolveStatus resolveSeamELOperand(ISeamProject project, IFile file, String documentContent, CharSequence prefix, 
			int position, boolean returnEqualedVariablesOnly, List<Var> vars) throws BadLocationException, StringIndexOutOfBoundsException {
		String oldEl = prefix.toString();
		Var var = findVarForEl(oldEl, vars);
		String newEl = replacePrefixByVar(oldEl, var);
		String newDocumentContent = documentContent;
		boolean prefixWasChanged = newEl!=oldEl;
		if(prefixWasChanged) {
			newDocumentContent = documentContent.substring(0, position) + newEl;
		}

		SeamELOperandResolveStatus status = resolveSeamELOperand(project, file, newDocumentContent, newEl, position, returnEqualedVariablesOnly);

		if(prefixWasChanged) {
			// Replace new EL by original one in result status.
			ELOperandToken newLastResolvedToken = status.getLastResolvedToken();
			SeamELOperandTokenizer tokenizer = new SeamELOperandTokenizer(documentContent, position + prefix.length());
			List<ELOperandToken> oldTokens = tokenizer.getTokens();
			status.setTokens(oldTokens);
			if(newLastResolvedToken != null) {
				if(newLastResolvedToken.getStart() < var.getElToken().getLength()) {
					// Last resolved token is token from "var". Set first token of original EL as last resolved one.
					status.setLastResolvedToken(oldTokens.get(0));
				} else {
					// Last resolved token is token outside "var" prefix. Correct last resolved token.
					int oldLastResolvedTokenStart = newLastResolvedToken.getStart() - var.getElToken().getText().length() + var.getName().length();
					for (ELOperandToken oldToken : oldTokens) {
						if(oldToken.getStart() == oldLastResolvedTokenStart) {
							status.setLastResolvedToken(oldToken);
							break;
						}
					}
				}
			}
		}

		return status;
	}

	private Var findVarForEl(String el, List<Var> vars) {
		if(vars!=null) {
			for (Var var : vars) {
				ELToken token = var.getElToken();
				if(token!=null && !token.getText().endsWith(".")) {
					String varName = var.getName();
					if(el.equals(varName) || el.startsWith(varName + ".")) {
						return var;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Replace all el variables by variables from found "var" attributes.
	 * @param el
	 * @param vars
	 * @return
	 */
	private String replacePrefixByVar(String el, Var var) {
		if(var!=null) {
			ELToken token = var.getElToken();
			if(token!=null) {
				return token.getText() + ".iterator().next()" + el.substring(var.getName().length());
			}
		}
		return el;
	}

	/**
	 * Resolves Seam EL
	 * @param project
	 * @param file
	 * @param documentContent
	 * @param prefix
	 * @param position
	 * @param returnEqualedVariablesOnly
	 * @return
	 * @throws BadLocationException
	 * @throws StringIndexOutOfBoundsException
	 */
	public SeamELOperandResolveStatus resolveSeamELOperand(ISeamProject project, IFile file, String documentContent, String prefix, 
			int position, boolean returnEqualedVariablesOnly) throws BadLocationException, StringIndexOutOfBoundsException {

		SeamELOperandTokenizer tokenizer = new SeamELOperandTokenizer(documentContent, position + prefix.length());
		SeamELOperandResolveStatus status = new SeamELOperandResolveStatus(tokenizer.getTokens());

		List<ISeamContextVariable> resolvedVariables = new ArrayList<ISeamContextVariable>();
		ScopeType scope = getScope(project, file);
		List<List<ELOperandToken>> variations = getPossibleVarsFromPrefix(status.getTokens());

		if (variations.isEmpty()) {
			resolvedVariables = resolveVariables(project, scope, 
					status.getTokens(), status.getTokens(), 
					returnEqualedVariablesOnly);
		} else {
			for (List<ELOperandToken> variation : variations) {
				List<ISeamContextVariable>resolvedVars = new ArrayList<ISeamContextVariable>();
				resolvedVars = resolveVariables(project, scope, 
						variation, status.getTokens(), 
						returnEqualedVariablesOnly);
				if (resolvedVars != null && !resolvedVars.isEmpty()) {
					resolvedVariables = resolvedVars;
					status.setLastResolvedToken(variation.get(variation.size() - 1));
					break;
				}
			}
		}

		// Save all resolved variables. It's useful for incremental validation.
		if(resolvedVariables!=null && resolvedVariables.size()>0) {
			status.setUsedVariables(resolvedVariables);
		}

		if (status.getResolvedTokens().isEmpty() && 
				!returnEqualedVariablesOnly && 
				status.getTokens().size() > 0 && 
				status.getTokens().get(status.getTokens().size() - 1).getType() == ELOperandToken.EL_SEPARATOR_TOKEN) {
			// no vars are resolved 
			// the tokens are the part of var name ended with a separator (.)
			resolvedVariables = resolveVariables(project, scope, status.getTokens(), status.getTokens(), returnEqualedVariablesOnly);			
			Set<String> proposals = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			for (ISeamContextVariable var : resolvedVariables) {
				String varName = var.getName();
				if(varName.startsWith(prefix)) {
					proposals.add(varName.substring(prefix.length()));
				}
			}
			status.setProposals(proposals);
			return status;
		}

		// Here we have a list of vars for some part of expression
		// OK. we'll proceed with members of these vars
		if (areEqualExpressions(status.getResolvedTokens(), status.getTokens())) {
			// First segment is the last one
			Set<String> proposals = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			for (ISeamContextVariable var : resolvedVariables) {
				String varName = var.getName();
				if(prefix.length()<=varName.length()) {
					proposals.add(varName.substring(prefix.length()));
				} else if(returnEqualedVariablesOnly) {
					proposals.add(varName);
				}
			}
			status.setLastResolvedToken(status.getTokens().isEmpty() ? null : status.getTokens().get(status.getTokens().size() - 1));
			status.setProposals(proposals);
			return status;
		}

		// First segment is found - proceed with next tokens 
		int startTokenIndex = status.getResolvedTokens().size();
		List<TypeInfoCollector.MemberInfo> members = new ArrayList<TypeInfoCollector.MemberInfo>();
		for (ISeamContextVariable var : resolvedVariables) {
			TypeInfoCollector.MemberInfo member = SeamExpressionResolver.getMemberInfoByVariable(var, returnEqualedVariablesOnly);
			if (member != null && !members.contains(member)) 
				members.add(member);
		}
		for (int i = startTokenIndex; i < status.getTokens().size() && members.size() > 0; i++) {
			ELOperandToken token = status.getTokens().get(i);
			if (i < status.getTokens().size() - 1) { // inside expression
				if (token.getType() == ELOperandToken.EL_SEPARATOR_TOKEN) {
					// proceed with next token
					status.setLastResolvedToken(token);
					continue;
				}
				if (token.isNameToken()) {
					// Find properties for the token
					String name = token.getText();
					List<TypeInfoCollector.MemberInfo> newMembers = new ArrayList<TypeInfoCollector.MemberInfo>();
					for (TypeInfoCollector.MemberInfo mbr : members) {
						if (mbr.getMemberType() == null) continue;
						TypeInfoCollector infos = SeamExpressionResolver.collectTypeInfo(mbr);
						if (TypeInfoCollector.isMapOrNotParameterizedCollection(mbr.getMemberType()) || TypeInfoCollector.isResourceBundle(mbr.getMemberType())) {
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
						status.setLastResolvedToken(token);
				}
				if (token.getType() == ELOperandToken.EL_METHOD_TOKEN) {
					// Find methods for the token
					String name = token.getText();
					if (name.indexOf('(') != -1) {
						name = name.substring(0, name.indexOf('('));
					}
					List<TypeInfoCollector.MemberInfo> newMembers = new ArrayList<TypeInfoCollector.MemberInfo>();
					for (TypeInfoCollector.MemberInfo mbr : members) {
						if (mbr.getMemberType() == null) continue;
						TypeInfoCollector infos = SeamExpressionResolver.collectTypeInfo(mbr);
						if (TypeInfoCollector.isMapOrNotParameterizedCollection(mbr.getMemberType()) || TypeInfoCollector.isResourceBundle(mbr.getMemberType())) {
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
						status.setLastResolvedToken(token);
				}
			} else { // Last segment
				Set<String> proposals = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
				if (token.getType() == ELOperandToken.EL_SEPARATOR_TOKEN) {
					// return all the methods + properties
					for (TypeInfoCollector.MemberInfo mbr : members) {
						if (mbr.getMemberType() == null) continue;
						TypeInfoCollector infos = SeamExpressionResolver.collectTypeInfo(mbr);
						if (TypeInfoCollector.isMapOrNotParameterizedCollection(mbr.getMemberType()) || TypeInfoCollector.isResourceBundle(mbr.getMemberType())) {
							status.setMapOrCollectionOrBundleAmoungTheTokens();
						}
						proposals.addAll(infos.getMethodPresentations());
						proposals.addAll(infos.getPropertyPresentations(status.getUnpairedGettersOrSetters()));
					}
				} else if (token.getType() == ELOperandToken.EL_VARIABLE_NAME_TOKEN ||
						token.getType() == ELOperandToken.EL_PROPERTY_NAME_TOKEN ||
						token.getType() == ELOperandToken.EL_METHOD_TOKEN) {
					// return filtered methods + properties 
					Set<String> proposalsToFilter = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER); 
					for (TypeInfoCollector.MemberInfo mbr : members) {
						if (mbr.getMemberType() == null) continue;
						TypeInfoCollector infos = SeamExpressionResolver.collectTypeInfo(mbr);
						if (TypeInfoCollector.isMapOrNotParameterizedCollection(mbr.getMemberType()) || TypeInfoCollector.isResourceBundle(mbr.getMemberType())) {
							status.setMapOrCollectionOrBundleAmoungTheTokens();
						}
						proposalsToFilter.addAll(infos.getMethodPresentations());
						proposalsToFilter.addAll(infos.getPropertyPresentations(status.getUnpairedGettersOrSetters()));
					}
					for (String proposal : proposalsToFilter) {
						// We do expect nothing but name for method tokens (No round brackets)
						String filter = token.getText();
						if (filter.indexOf('(') != -1) {
							filter = filter.substring(0, filter.indexOf('('));
						}
						if(returnEqualedVariablesOnly) {
							// This is used for validation.
							if (proposal.equals(filter)) {
								proposals.add(proposal);
								if(status.getUnpairedGettersOrSetters()!=null) {
									TypeInfoCollector.MethodInfo unpirMethod = status.getUnpairedGettersOrSetters().get(filter);
									status.clearUnpairedGettersOrSetters();
									if(unpirMethod!=null) {
										status.getUnpairedGettersOrSetters().put(filter, unpirMethod);
									}
								}
								break;
							}
						} else {
							// This is used for CA.
							if (proposal.startsWith(filter)) {
								proposals.add(proposal.substring(filter.length()));
							}
						}
					}
				}
				status.setProposals(proposals);
				if (status.isOK()){
					status.setLastResolvedToken(token);
				}
			}
		}

		if(status.getProposals().isEmpty() && status.getUnpairedGettersOrSetters()!=null) {
			status.clearUnpairedGettersOrSetters();
		}
		return status;
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

	/*
	 * Creates and returns list of possible variable name combinations from expression starting from the longest name
	 *  
	 * 
	 * @param prefix
	 * @return
	 */
	private List<List<ELOperandToken>> getPossibleVarsFromPrefix(List<ELOperandToken>prefix) {
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
	 * Create the array of suggestions. 
	 * @param project Seam project 
	 * @param file File 
	 * @param document 
	 * @param prefix the prefix to search for
	 * @param position Offset of the prefix 
	 */
	public List<IJavaElement> getJavaElementsForExpression(ISeamProject project, IFile file, String expression) throws BadLocationException, StringIndexOutOfBoundsException {
		List<IJavaElement> res= new ArrayList<IJavaElement>();
		SeamELOperandTokenizer tokenizer = new SeamELOperandTokenizer(expression, expression.length());
		List<ELOperandToken> tokens = tokenizer.getTokens();

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
						TypeInfoCollector infos = SeamExpressionResolver.collectTypeInfo(mbr);
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
						TypeInfoCollector infos = SeamExpressionResolver.collectTypeInfo(mbr);
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
				Set<IJavaElement> javaElements = new HashSet<IJavaElement>();
				if (token.getType() == ELOperandToken.EL_VARIABLE_NAME_TOKEN ||
					token.getType() == ELOperandToken.EL_PROPERTY_NAME_TOKEN ||
					token.getType() == ELOperandToken.EL_METHOD_TOKEN) {
					// return filtered methods + properties 
					Set<TypeInfoCollector.MemberInfo> javaElementInfosToFilter = new HashSet<TypeInfoCollector.MemberInfo>(); 
					for (TypeInfoCollector.MemberInfo mbr : members) {
						TypeInfoCollector infos = SeamExpressionResolver.collectTypeInfo(mbr);
						javaElementInfosToFilter.addAll(infos.getMethods());
						javaElementInfosToFilter.addAll(infos.getProperties());
					}

					for (TypeInfoCollector.MemberInfo info : javaElementInfosToFilter) {
						// We do expect nothing but name for method tokens (No round brackets)
						String filter = token.getText();
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
}