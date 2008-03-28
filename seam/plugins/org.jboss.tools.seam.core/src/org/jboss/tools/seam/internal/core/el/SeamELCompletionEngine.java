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
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.jboss.tools.common.model.util.TypeInfoCollector;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.el.ElVarSearcher.Var;
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
		SeamELOperandResolveStatus status = resolveSeamELOperand(project, file, documentContent, prefix, position, returnEqualedVariablesOnly, vars, new ElVarSearcher(project, file, this));
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
		private TypeInfoCollector.MemberInfo memberOfResolvedOperand;

		/**
		 * @return MemberInfo of last segment of EL operand. Null if El is not resolved.
		 */
		public TypeInfoCollector.MemberInfo getMemberOfResolvedOperand() {
			return memberOfResolvedOperand;
		}

		/**
		 * Sets MemberInfo for last segment of EL operand.
		 * @param lastResolvedMember
		 */
		public void setMemberOfResolvedOperand(
				TypeInfoCollector.MemberInfo lastResolvedMember) {
			this.memberOfResolvedOperand = lastResolvedMember;
		}

		/**
		 * Constructor
		 * @param tokens Tokens of EL
		 */
		public SeamELOperandResolveStatus(List<ELOperandToken> tokens) {
			this.tokens = tokens;
		}

		/**
		 * @return true if EL contains any not parametrized Collection or ResourceBundle.
		 */
		public boolean isMapOrCollectionOrBundleAmoungTheTokens() {
			return this.isMapOrCollectionOrBundleAmoungTheTokens;
		}

		public void setMapOrCollectionOrBundleAmoungTheTokens() {
			this.isMapOrCollectionOrBundleAmoungTheTokens = true;
		}

		/**
		 * @return true if EL is resolved.
		 */
		public boolean isOK() {
			return !getProposals().isEmpty() || isMapOrCollectionOrBundleAmoungTheTokens(); 
		}

		/**
		 * @return false if El is not resolved.
		 */
		public boolean isError() {
			return !isOK();
		}

		/**
		 * @return List of resolved tokens of EL. Includes separators "."
		 */
		public List<ELOperandToken> getResolvedTokens() {
			List<ELOperandToken> resolvedTokens = new ArrayList<ELOperandToken>();
			int index = tokens.indexOf(lastResolvedToken); // index == -1 means that no tokens are resolved
			for (int i = 0; i < tokens.size() && i <= index; i++) {
				resolvedTokens.add(tokens.get(i));
			}
			return resolvedTokens;
		}

		/**
		 * @return List of unresolved tokens of EL.
		 */
		public List<ELOperandToken> getUnresolvedTokens() {
			List<ELOperandToken> unresolvedTokens = new ArrayList<ELOperandToken>();
			int index = tokens.indexOf(lastResolvedToken); // index == -1 means that no tokens are resolved
			for (int i = index + 1; i < tokens.size(); i++) {
				unresolvedTokens.add(tokens.get(i));
			}
			return unresolvedTokens;
		}

		/**
		 * @return Last resolved token of EL. Can be separator "."
		 */
		public ELOperandToken getLastResolvedToken() {
			return lastResolvedToken;
		}

		/**
		 * @param lastResolvedToken Last resolved token of EL. Can be separator "."
		 */
		public void setLastResolvedToken(ELOperandToken lastResolvedToken) {
			this.lastResolvedToken = lastResolvedToken;
		}

		/**
		 * @return Tokens of EL.
		 */
		public List<ELOperandToken> getTokens() {
			return tokens;
		}

		/**
		 * @param tokens Tokens of EL.
		 */
		public void setTokens(List<ELOperandToken> tokens) {
			this.tokens = tokens;
		}

		/**
		 * @return Set of proposals for EL.
		 */
		public Set<String> getProposals() {
			return proposals == null ? new TreeSet<String>() : proposals;
		}

		/**
		 * @param proposals Set of proposals.
		 */
		public void setProposals(Set<String> proposals) {
			this.proposals = proposals;
		}

		/**
		 * @return List of Seam Context Variables used in EL.  
		 */
		public List<ISeamContextVariable> getUsedVariables() {
			return (usedVariables == null ? new ArrayList<ISeamContextVariable>() : usedVariables);
		}

		/**
		 * @param usedVariables List of Seam Context Variables used in EL.
		 */
		public void setUsedVariables(List<ISeamContextVariable> usedVariables) {
			this.usedVariables = usedVariables;
		}

		/**
		 * @return Map of unpaired getters and setters (getters/setters without proper setters/getters).
		 * of all properties used in EL.
		 * Key - name of property.
		 * Value - MethodInfo of existed getter/setter.
		 */
		public Map<String, TypeInfoCollector.MethodInfo> getUnpairedGettersOrSetters() {
			if (unpairedGettersOrSetters == null) {
				unpairedGettersOrSetters = new HashMap<String, TypeInfoCollector.MethodInfo>();
			}
			return unpairedGettersOrSetters;
		}

		/**
		 * Clear Map of unpaired getters and setters.
		 */
		public void clearUnpairedGettersOrSetters() {
			getUnpairedGettersOrSetters().clear();
		}
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

	/**
	 * Resolve EL.
	 * @param project Seam project.
	 * @param file
	 * @param documentContent
	 * @param prefix Text between #{ and cursor position in document. 
	 * @param position Cursor position in document
	 * @param returnEqualedVariablesOnly if "false" use prefix as mask. 
	 * @param vars All "var" attributes that can be used in the EL.
	 * @param varSearcher
	 * @return Status of resolving.
	 * @throws BadLocationException
	 * @throws StringIndexOutOfBoundsException
	 */
	public SeamELOperandResolveStatus resolveSeamELOperand(ISeamProject project, IFile file, String documentContent, CharSequence prefix, 
			int position, boolean returnEqualedVariablesOnly, List<Var> vars, ElVarSearcher varSearcher) throws BadLocationException, StringIndexOutOfBoundsException {
		String oldEl = prefix.toString();
		Var var = varSearcher.findVarForEl(oldEl, vars, true);
		String suffix = "";
		String newEl = oldEl;
		if(var!=null) {
			TypeInfoCollector.MemberInfo member = resolveSeamEL(project, file, var.getElToken().getText());
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
			if(var.getElToken()!=null) {
				newEl  = var.getElToken().getText() + suffix + oldEl.substring(var.getName().length());
			}
		}
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
				if(newLastResolvedToken.getStart() < var.getElToken().getLength() + suffix.length()) {
					// Last resolved token is token from "var". Set first token of original EL as last resolved one.
					status.setLastResolvedToken(null);
				} else {
					// Last resolved token is token outside "var" prefix. Correct last resolved token.
					int oldLastResolvedTokenStart = newLastResolvedToken.getStart() - var.getElToken().getText().length() - suffix.length() + var.getName().length();
					for (ELOperandToken oldToken : oldTokens) {
						if(oldToken.getStart() == oldLastResolvedTokenStart) {
							status.setLastResolvedToken(oldToken);
							break;
						}
					}
				}
			}
		}

		if(prefixWasChanged) {
			var.resolveValue("#{" + var.getElToken().getText() + suffix + "}");
		}

		if(!returnEqualedVariablesOnly && vars!=null) {
			status.getProposals().addAll(getVarNameProposals(vars, prefix.toString()));
		}
		return status;
	}

	/**
	 * Returns MemberInfo for last segment of EL. Null if El is not resolved.
	 * @param project
	 * @param file
	 * @param elBody EL without #{}
	 * @return MemberInfo for last segment of EL. Null if El is not resolved.
	 * @throws BadLocationException
	 * @throws StringIndexOutOfBoundsException
	 */
	public TypeInfoCollector.MemberInfo resolveSeamEL(ISeamProject project, IFile file, String elBody) throws BadLocationException, StringIndexOutOfBoundsException {
		SeamELOperandResolveStatus status = resolveSeamELOperand(project, file, elBody, elBody, 0, true);
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
		SeamELOperandTokenizer tokenizer = new SeamELOperandTokenizer(el, el.length());
		List<ELOperandToken> tokens = tokenizer.getTokens();

		SeamELOperandResolveStatus status = new SeamELOperandResolveStatus(tokenizer.getTokens());
		status.setTokens(tokens);
	
		List<ISeamContextVariable> resolvedVariables = new ArrayList<ISeamContextVariable>();
		ScopeType scope = getScope(project, file);
		List<List<ELOperandToken>> variations = getPossibleVarsFromPrefix(tokens);

		if (variations.isEmpty()) {
			resolvedVariables = resolveVariables(project, scope, 
					tokens, tokens, 
					true);
		} else {
			for (List<ELOperandToken> variation : variations) {
				List<ISeamContextVariable> resolvedVars = resolveVariables(project, scope, 
						variation, tokens, 
						true);
				if (resolvedVars != null && !resolvedVars.isEmpty()) {
					resolvedVariables = resolvedVars;
					status.setLastResolvedToken(variation.get(variation.size() - 1));
					break;
				}
			}
		}

		if (!areEqualExpressions(status.getResolvedTokens(), status.getTokens())) {
			resolvedVariables.clear();
		}

		return resolvedVariables;
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
				status.setMemberOfResolvedOperand(SeamExpressionResolver.getMemberInfoByVariable(var, true));
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
						status.setLastResolvedToken(token);
				}
			} else { // Last segment
				Set<String> proposals = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
				if (token.getType() == ELOperandToken.EL_SEPARATOR_TOKEN) {
					// return all the methods + properties
					for (TypeInfoCollector.MemberInfo mbr : members) {
						if (mbr instanceof MessagesInfo) {
							proposals.addAll(((MessagesInfo)mbr).getKeys());
							continue;
						}
						if (mbr.getMemberType() == null) {
							continue;
						}
						TypeInfoCollector infos = SeamExpressionResolver.collectTypeInfo(mbr);
						if (TypeInfoCollector.isNotParameterizedCollection(mbr) || TypeInfoCollector.isResourceBundle(mbr.getMemberType())) {
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
						if (mbr instanceof MessagesInfo) {
							proposalsToFilter.addAll(((MessagesInfo)mbr).getKeys());
							continue;
						}
						if (mbr.getMemberType() == null) continue;
						TypeInfoCollector infos = SeamExpressionResolver.collectTypeInfo(mbr);
						if (TypeInfoCollector.isNotParameterizedCollection(mbr) || TypeInfoCollector.isResourceBundle(mbr.getMemberType())) {
							status.setMapOrCollectionOrBundleAmoungTheTokens();
						}
						proposalsToFilter.addAll(infos.getMethodPresentations());
						proposalsToFilter.addAll(infos.getPropertyPresentations(status.getUnpairedGettersOrSetters()));
						status.setMemberOfResolvedOperand(mbr);
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