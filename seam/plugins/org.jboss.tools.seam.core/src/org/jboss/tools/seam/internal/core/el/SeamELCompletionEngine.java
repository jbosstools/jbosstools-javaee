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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.rules.IToken;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamCorePlugin;

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
	 * @return the list of all possible suggestions
	 * @throws BadLocationException if accessing the current document fails
	 */
	public List<String> getCompletions(ISeamProject project, IFile file, IDocument document, CharSequence prefix, 
			int position) throws BadLocationException, StringIndexOutOfBoundsException {
		String documentContent = null;
		if(document!=null) {
			documentContent = document.get();
		}
		return getCompletions(project, file, documentContent, prefix, position, false, null, null);
	}

	/**
	 * Create the array of suggestions. 
	 * @param project Seam project 
	 * @param file File 
	 * @param document 
	 * @param prefix the prefix to search for
	 * @param position Offset of the prefix 
	 * @return the list of all possible suggestions
	 * @param usedVariables - Set of variables which are used in this Expression. It's useful in incremental validation.
	 * @param unpairedGettersOrSetters - map of unpaired getters or setters of property which is used in last segment of Expression. 'key' is property name.
	 * @param returnCompletedVariablesOnly - if 'true' then returns only variables that equals prefix. It's useful for validation.
	 *  for example:
	 *   we have 'variableName.variableProperty', 'variableName.variableProperty1', 'variableName.variableProperty2'  
	 *   prefix is 'variableName.variableProperty'
	 *   Result is {'variableProperty'}
	 * if 'false' then returns ends of variables that starts with prefix. It's useful for CA.
	 *  for example:
	 *   we have 'variableName.variableProperty', 'variableName.variableProperty1', 'variableName.variableProperty2'
	 *   prefix is 'variableName.variableProperty'
	 *   Result is {'1','2'}
	 */
	public List<String> getCompletions(ISeamProject project, IFile file, String documentContent, CharSequence prefix, 
			int position, boolean returnEqualedVariablesOnly, Set<ISeamContextVariable> usedVariables, Map<String, IMethod> unpairedGettersOrSetters) throws BadLocationException, StringIndexOutOfBoundsException {

		List<String> res= new ArrayList<String>();
		SeamELOperandTokenizer tokenizer = new SeamELOperandTokenizer(documentContent, position + prefix.length());
		List<ELOperandToken> tokens = tokenizer.getTokens();
		
		List<ELOperandToken> resolvedExpressionPart = new ArrayList<ELOperandToken>();
		List<ISeamContextVariable> resolvedVariables = new ArrayList<ISeamContextVariable>();
		ScopeType scope = getScope(project, file);
		List<List<ELOperandToken>> variations = getPossibleVarsFromPrefix(tokens);
		
		if (variations.isEmpty()) {
			resolvedVariables = resolveVariables(project, scope, tokens, tokens, returnEqualedVariablesOnly);
		} else {
			for (List<ELOperandToken> variation : variations) {
				List<ISeamContextVariable>resolvedVars = new ArrayList<ISeamContextVariable>();
				resolvedVars = resolveVariables(project, scope, variation, tokens, returnEqualedVariablesOnly);
				if (resolvedVars != null && !resolvedVars.isEmpty()) {
					resolvedVariables = resolvedVars;
					resolvedExpressionPart = variation;
					break;
				}
			}
		}

		// Save all resolved variables. It's useful for incremental validation.
		if(resolvedVariables!=null && resolvedVariables.size()>0 && usedVariables!=null) {
			usedVariables.addAll(resolvedVariables);
		}

		// Here we have a list of vars for some part of expression
		// OK. we'll proceed with members of these vars
		if (areEqualExpressions(resolvedExpressionPart, tokens)) {
			// First segment is the last one
			for (ISeamContextVariable var : resolvedVariables) {
				String varName = var.getName();
				String prefixString = prefix.toString();
				if(prefixString.length()<varName.length()) {
					res.add(varName.substring(prefixString.length()));
				} else if(returnEqualedVariablesOnly) {
					res.add(varName);
				}
			}
			return res;
		}

		// First segment is found - proceed with next tokens 
		int startTokenIndex = (resolvedExpressionPart == null ? 0 : resolvedExpressionPart.size());
		Set<IMember> members = new HashSet<IMember>();
		for (ISeamContextVariable var : resolvedVariables) {
			IMember member = SeamExpressionResolver.getMemberByVariable(var, returnEqualedVariablesOnly);
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

				if (token.getType() == ELOperandToken.EL_NAME_TOKEN) {
					// Find properties for the token
					String name = token.getText();
					Set<IMember> newMembers = new HashSet<IMember>();
					for (IMember mbr : members) {
						try {
							IType type = (mbr instanceof IType ? (IType)mbr : EclipseJavaUtil.findType(mbr.getJavaProject(), EclipseJavaUtil.getMemberTypeAsString(mbr)));
							Set<IMember> properties = SeamExpressionResolver.getProperties(type);
							for (IMember property : properties) {
								StringBuffer propertyName = new StringBuffer(property.getElementName());
								if (property instanceof IMethod) { // Setter or getter
									propertyName.delete(0, 3);
									propertyName.setCharAt(0, Character.toLowerCase(propertyName.charAt(0)));
								}
								if (name.equals(propertyName.toString())) {
									newMembers.add(property);
								}
							}
						} catch (JavaModelException ex) {
							SeamCorePlugin.getPluginLog().logError(ex);
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
					Set<IMember> newMembers = new HashSet<IMember>();
					for (IMember mbr : members) {
						try {
							IType type = (mbr instanceof IType ? (IType)mbr : EclipseJavaUtil.findType(mbr.getJavaProject(), EclipseJavaUtil.getMemberTypeAsString(mbr)));
							Set<IMember> methods = SeamExpressionResolver.getMethods(type);
							for (IMember method : methods) {
								if (name.equals(method.getElementName())) {
									newMembers.add(method);
								}
							}
						} catch (JavaModelException ex) {
							SeamCorePlugin.getPluginLog().logError(ex);
						}
					}
					members = newMembers;
				}
			} else { // Last segment
				Set<String> proposals = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
				if (token.getType() == ELOperandToken.EL_SEPARATOR_TOKEN) {
					// return all the methods + properties
					for (IMember mbr : members) {
						try {
							IType type = (mbr instanceof IType ? (IType)mbr : EclipseJavaUtil.findType(mbr.getJavaProject(), EclipseJavaUtil.getMemberTypeAsString(mbr)));
							proposals.addAll(SeamExpressionResolver.getMethodPresentations(type));
							proposals.addAll(SeamExpressionResolver.getPropertyPresentations(type, unpairedGettersOrSetters));
						} catch (JavaModelException ex) {
							SeamCorePlugin.getPluginLog().logError(ex);
						}
					}
				} else if (token.getType() == ELOperandToken.EL_NAME_TOKEN ||
					token.getType() == ELOperandToken.EL_METHOD_TOKEN) {
					// return filtered methods + properties 
					Set<String> proposalsToFilter = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER); 
					for (IMember mbr : members) {
						try {
							IType type = null;
							if(mbr instanceof IType) {
								type = (IType)mbr;
							} else {
								type = EclipseJavaUtil.findType(mbr.getJavaProject(), EclipseJavaUtil.getMemberTypeAsString(mbr));
							}
							proposalsToFilter.addAll(SeamExpressionResolver.getMethodPresentations(type));
							proposalsToFilter.addAll(SeamExpressionResolver.getPropertyPresentations(type, unpairedGettersOrSetters));
						} catch (JavaModelException ex) {
							SeamCorePlugin.getPluginLog().logError(ex);
						}
					}
					for (String proposal : proposalsToFilter) {
						// We do expect nothing but name for method tokens (No round brackets)
						String filter = token.getText();
						if(returnEqualedVariablesOnly) {
							// This is used for validation.
							if (proposal.equals(filter)) {
								proposals.add(proposal);
								if(unpairedGettersOrSetters!=null) {
									IMethod unpirMethod = unpairedGettersOrSetters.get(filter);
									unpairedGettersOrSetters.clear();
									if(unpirMethod!=null) {
										unpairedGettersOrSetters.put(filter, unpirMethod);
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
				res.addAll(proposals);
			}
		}

		if(res.isEmpty() && unpairedGettersOrSetters!=null) {
			unpairedGettersOrSetters.clear();
		}
		return res;
	}
	
	private String computeVariableName(List<ELOperandToken> tokens){
		if (tokens == null)
			tokens = new ArrayList<ELOperandToken>();
		StringBuffer sb = new StringBuffer();
		for (ELOperandToken token : tokens) {
			if (token.getType() == ELOperandToken.EL_NAME_TOKEN ||
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
	 * EL string parser.
	 * Creates list of tokens for the name, method and separator parts 
	 *
	 * @author Jeremy
	 */
	public static class SeamELOperandTokenizer {
		static final int STATE_INITIAL = 0;
		static final int STATE_VAR = 1;
		static final int STATE_METHOD = 2;
		static final int STATE_SEPARATOR = 3;

		String documentContent;
		List<ELOperandToken> fTokens;
		int index;

		/**
		 * Constructs SeamELTokenizer object.
		 * Parse expression from offset to first operator or space.
		 * Tokenizer parses document from offset to beginning.
		 * For example: documentContetn is '<tag attr="#{var1.pr!=var2.pr}"/>'
		 *              offset =  29 ("...var2.pr|}")
		 *              then tokens are {"pr",".","var2"}
		 * @param documentContent
		 * @param offset
		 */
		public SeamELOperandTokenizer(String documentContent, int offset) {
			this.documentContent = documentContent;
			index = (documentContent == null || documentContent.length() < offset? -1 : offset);
			fTokens = new ArrayList<ELOperandToken>();
			parseBackward();
		}

		/**
		 * Constructs SeamELTokenizer object.
		 * @param document
		 * @param offset
		 */
		public SeamELOperandTokenizer(IDocument document, int offset) {
			this(document.get(), offset);
		}

		/**
		 * Returns list of tokens for the expression parsed
		 * 
		 * @return
		 */
		public List<ELOperandToken> getTokens() {
			return fTokens;
		}

		/*
		 * Performs backward parsing of document text for expression
		 */
		private void parseBackward() {
			ELOperandToken token;
			fState = STATE_INITIAL;
			while ((token = getNextToken()) != ELOperandToken.EOF) {

				if (token.type == ELOperandToken.EL_NAME_TOKEN ||
						token.type == ELOperandToken.EL_METHOD_TOKEN ||
						token.type == ELOperandToken.EL_SEPARATOR_TOKEN) {

					fTokens.add(0, token);
				}
			}
		}

		int fState;
		int fEndOfToken;

		/*
		 * Calculates and returns next token for expression
		 * 
		 * @return
		 */
		private ELOperandToken getNextToken() {
			switch (fState) {
			case STATE_INITIAL: // Just started
			{
				int ch = readCharBackward();
				if (ch == -1) {
					return ELOperandToken.EOF;
				}
				if (Character.isJavaIdentifierPart((char)ch)) {
					releaseChar();
					return readVarToken();
				}
				if (ch == '.') {
					releaseChar();
					return readSeparatorToken();
				}
				if (ch == ')') {
					releaseChar();
					return readMethodToken();
				}
				releaseChar();
				return ELOperandToken.EOF;
			}
			case STATE_VAR: // Variable name is read - expecting a separator 
			{
				int ch = readCharBackward();
				if (ch == -1) {
					return ELOperandToken.EOF;
				}
				if (ch == '.') {
					releaseChar();
					return readSeparatorToken();
				}
				releaseChar();
				return ELOperandToken.EOF;
			}
			case STATE_METHOD: // Method name and parameters are read - expecting a separator
			{
				int ch = readCharBackward();
				if (ch == -1) {
					return ELOperandToken.EOF;
				}
				if (ch == '.') {
					releaseChar();
					return readSeparatorToken();
				}
				releaseChar();
				return ELOperandToken.EOF;
			}
			case STATE_SEPARATOR: // Separator is read - expecting a var or method
			{
				int ch = readCharBackward();
				if (ch == -1) {
					return ELOperandToken.EOF;
				}
				if (Character.isJavaIdentifierPart((char)ch)) {
					releaseChar();
					return readVarToken();
				}
				if (ch == ')') {
					releaseChar();
					return readMethodToken();
				}
				releaseChar();
				return ELOperandToken.EOF;
			}
			}
			return ELOperandToken.EOF;
		}
		
		/* Reads and returns the method token from the expression
		 * 
		 * @return
		 */
		ELOperandToken readMethodToken() {
			fState = STATE_METHOD;
			int endOfToken = index;
			
			// read the method parameters
			if (!skipMethodParameters()) 
				return ELOperandToken.EOF;
			
			// skip spaces between the method's name and it's parameters
			if (!skipSpaceChars())
				return ELOperandToken.EOF;
			// read the method name
			if (!skipMethodName())
				return ELOperandToken.EOF;
			
			return (endOfToken - index > 0 ? new ELOperandToken(index, endOfToken - index, getCharSequence(index, endOfToken - index), ELOperandToken.EL_METHOD_TOKEN) : ELOperandToken.EOF);
		}

		/*
		 * Returns the CharSequence object
		 *  
		 * @param start
		 * @param length
		 * @return
		 */
		private CharSequence getCharSequence(int start, int length) {
			String text = ""; //$NON-NLS-1$
			try {
				text = documentContent.substring(start, start + length);
			} catch (StringIndexOutOfBoundsException e) {
				SeamCorePlugin.getDefault().logError(e);
				text = ""; // For sure //$NON-NLS-1$
			}
			return text.subSequence(0, text.length());
		}

		
		/*
		 * Skips the space characters in the document
		 */
		boolean skipSpaceChars() {
			int ch;
			while ((ch = readCharBackward()) != -1) {
				if (!Character.isSpaceChar(ch)) {
					releaseChar();
					break;
				}
			}
			return true;
		}
		
		/* 
		 * Skips the method name characters in the document
		 * 
		 * @return boolean true if at least 1 character had been read
		 */
		boolean skipMethodName() {
			int endOfToken = index;
			int ch;
			while((ch = readCharBackward()) != -1) {
				if (!Character.isJavaIdentifierPart(ch)) {
					releaseChar();
					return (endOfToken - index > 0);
				}
			}
			return false;
		}

		/* 
		 * Skips the method parameters characters in the document
		 * 
		 * @return boolean true if complete parameters set had been read
		 */
		boolean skipMethodParameters() {
			int ch = readCharBackward(); 
			if (ch != ')')
				return false;
			int pCount = 1;
			while (pCount > 0) {
				ch = readCharBackward();
				if (ch == -1)
					return false;
				
				if (ch == '"' || ch == '\'') {
					skipQuotedChars((char)ch);
					continue;
				}
				if (ch == ')') {
					pCount++;
					continue;
				}
				if (ch == '(') {
					pCount--;
					continue;
				}
			}
			return true;
		}
	
		/* 
		 * Skips the quoted characters 
		 * 
		 */
		void skipQuotedChars(char pair) {
			int ch = readCharBackward();
			
			while (ch != -1) {
				if (ch == pair) {
					ch = readCharBackward();
					if (ch == '\\') {
						int backSlashCount = 0;
						while (ch == '\\') {
							backSlashCount++;
							ch = readCharBackward();
						}
						releaseChar(); // Return the last non-slash char to the buffer
						if ((backSlashCount/2)*2 == backSlashCount) {
							return;
						}
					}
				}
				ch = readCharBackward();
			}
		}
	
		/* Reads and returns the separator token from the expression
		 * 
		 * @return
		 */
		ELOperandToken readSeparatorToken() {
			fState = STATE_SEPARATOR;
			int ch = readCharBackward();
			
			return (ch == '.' ? new ELOperandToken(index, 1, getCharSequence(index, 1), ELOperandToken.EL_SEPARATOR_TOKEN) :
				ELOperandToken.EOF);
		}
		
		/* Reads and returns the variable token from the expression
		 * 
		 * @return
		 */
		ELOperandToken readVarToken() {
			fState = STATE_VAR;
			int endOfToken = index;
			int ch;
			while((ch = readCharBackward()) != -1) {
				if (!Character.isJavaIdentifierPart(ch)) {
					releaseChar();
					return (endOfToken - index > 0 ? new ELOperandToken(index, endOfToken - index, getCharSequence(index, endOfToken - index), ELOperandToken.EL_NAME_TOKEN) : ELOperandToken.EOF);
				}
			}
			releaseChar();
			return (endOfToken - index > 0 ? new ELOperandToken(index, endOfToken - index, getCharSequence(index, endOfToken - index), ELOperandToken.EL_NAME_TOKEN) : ELOperandToken.EOF);
		}
		
		/* Reads the next character in the document
		 * 
		 * @return
		 */
		int readCharBackward() {
			if (--index < 0 || 
					documentContent == null ||
					documentContent.length() <= index)
				return -1;

			try {
				return documentContent.charAt(index);
			} catch (StringIndexOutOfBoundsException e) {
				return -1;
			}
		}
		
		/* 
		 * returns the character to the document
		 */
		void releaseChar() {
			if (index < documentContent.length())
				index++;
		}
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
/*				String varName = var.getName();
				if(expression.length()<varName.length()) {
					res.add(varName.substring(prefixString.length()));
				} else if(returnEqualedVariablesOnly) {
					res.add(varName);
				}
*/
				IMember member = SeamExpressionResolver.getMemberByVariable(var, true);
				if (member instanceof IJavaElement){
					res.add((IJavaElement)member);
				}
			}
			return res;
		}

		// First segment is found - proceed with next tokens 
		int startTokenIndex = (resolvedExpressionPart == null ? 0 : resolvedExpressionPart.size());
		Set<IMember> members = new HashSet<IMember>();
		for (ISeamContextVariable var : resolvedVariables) {
			IMember member = SeamExpressionResolver.getMemberByVariable(var, true);
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

				if (token.getType() == ELOperandToken.EL_NAME_TOKEN) {
					// Find properties for the token
					String name = token.getText();
					Set<IMember> newMembers = new HashSet<IMember>();
					for (IMember mbr : members) {
						try {
							IType type = (mbr instanceof IType ? (IType)mbr : EclipseJavaUtil.findType(mbr.getJavaProject(), EclipseJavaUtil.getMemberTypeAsString(mbr)));
							Set<IMember> properties = SeamExpressionResolver.getProperties(type);
							for (IMember property : properties) {
								StringBuffer propertyName = new StringBuffer(property.getElementName());
								if (property instanceof IMethod) { // Setter or getter
									propertyName.delete(0, 3);
									propertyName.setCharAt(0, Character.toLowerCase(propertyName.charAt(0)));
								}
								if (name.equals(propertyName.toString())) {
									newMembers.add(property);
								}
							}
						} catch (JavaModelException ex) {
							SeamCorePlugin.getPluginLog().logError(ex);
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
					Set<IMember> newMembers = new HashSet<IMember>();
					for (IMember mbr : members) {
						try {
							IType type = (mbr instanceof IType ? (IType)mbr : EclipseJavaUtil.findType(mbr.getJavaProject(), EclipseJavaUtil.getMemberTypeAsString(mbr)));
							Set<IMember> methods = SeamExpressionResolver.getMethods(type);
							for (IMember method : methods) {
								if (name.equals(method.getElementName())) {
									newMembers.add(method);
								}
							}
						} catch (JavaModelException ex) {
							SeamCorePlugin.getPluginLog().logError(ex);
						}
					}
					members = newMembers;
				}
			} else { // Last segment
				Set<IJavaElement> javaElements = new HashSet<IJavaElement>();
				if (token.getType() == ELOperandToken.EL_NAME_TOKEN ||
					token.getType() == ELOperandToken.EL_METHOD_TOKEN) {
					// return filtered methods + properties 
					Set<IJavaElement> javaElementsToFilter = new HashSet<IJavaElement>(); 
					for (IMember mbr : members) {
						try {
							IType type = null;
							if(mbr instanceof IType) {
								type = (IType)mbr;
							} else {
								type = EclipseJavaUtil.findType(mbr.getJavaProject(), EclipseJavaUtil.getMemberTypeAsString(mbr));
							}
							javaElementsToFilter.addAll(SeamExpressionResolver.getMethods(type));
							javaElementsToFilter.addAll(SeamExpressionResolver.getProperties(type));
						} catch (JavaModelException ex) {
							SeamCorePlugin.getPluginLog().logError(ex);
						}
					}
					for (IJavaElement javaElement : javaElementsToFilter) {
						// We do expect nothing but name for method tokens (No round brackets)
						String filter = token.getText();
						String elementName = javaElement.getElementName();
						// This is used for validation.
						if (javaElement.getElementName().equals(filter)) {
							javaElements.add(javaElement);
						} else {
							if (javaElement instanceof IMethod) {
								boolean getter = (elementName.startsWith("get") && !"get".equals(elementName)) || //$NON-NLS-1$ //$NON-NLS-2$
								 (elementName.startsWith("is") && !"is".equals(elementName)); //$NON-NLS-1$ //$NON-NLS-2$
								boolean setter = elementName.startsWith("set") && !"set".equals(elementName); //$NON-NLS-1$ //$NON-NLS-2$
								if(getter || setter) {
									StringBuffer name = new StringBuffer(elementName);
									if(elementName.startsWith("i")) { //$NON-NLS-1$
										name.delete(0, 2);
									} else {
										name.delete(0, 3);
									}
									name.setCharAt(0, Character.toLowerCase(name.charAt(0)));
									String propertyName = name.toString();
									if (propertyName.equals(filter)) {
										javaElements.add(javaElement);
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

/**
 * Token for the EX expression operand parts
 *
 * @author Jeremy
 */
class ELOperandToken implements IToken {
	static final ELOperandToken EOF = new ELOperandToken(-1, -1, null, -1);
	static final int EL_NAME_TOKEN = 1;
	static final int EL_METHOD_TOKEN = 2;
	static final int EL_SEPARATOR_TOKEN = 3;

	int start;
	int length;
	CharSequence chars;
	int type;
	
	/**
	 * Constructs the ELToken object
	 * 
	 * @param start
	 * @param length
	 * @param chars
	 * @param type
	 */
	public ELOperandToken(int start, int length, CharSequence chars, int type) {
		this.start = start;
		this.length = length;
		this.chars = chars;
		this.type = type;
	}
	
	/**
	 * Returns string representation for the token
	 */
	public String toString() {
		return "ELToken(" + start + ", " + length + ", " + type + ") [" + (chars == null ? "<Empty>" : chars.toString()) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	}

	/*
	 * @see org.eclipse.jface.text.rules.IToken#getData()
	 */
	public Object getData() {
		return (chars == null ? null : chars.subSequence(start, start+length).toString());
	}

	/*
	 * @see org.eclipse.jface.text.rules.IToken#isEOF()
	 */
	public boolean isEOF() {
		return (start == -1 && length == -1 && chars == null);
	}
	
	/*
	 * @see org.eclipse.jface.text.rules.IToken#isOther()
	 */
	public boolean isOther() {
		return false;
	}

	/*
	 * @see org.eclipse.jface.text.rules.IToken#isUndefined()
	 */
	public boolean isUndefined() {
		return false;
	}

	/*
	 * @see org.eclipse.jface.text.rules.IToken#isWhitespace()
	 */
	public boolean isWhitespace() {
		return false;
	}

	/*
	 * Returns the token type
	 */
	public int getType(){
		return type;
	}

	/*
	 * Returns the token text
	 */
	public String getText() {
		return chars.toString();
	}
}