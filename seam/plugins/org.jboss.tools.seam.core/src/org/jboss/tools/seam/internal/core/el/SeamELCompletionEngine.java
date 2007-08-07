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
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
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
		SeamELTokenizer tokenizer = new SeamELTokenizer(documentContent, position + prefix.length());
		List<ELToken> tokens = tokenizer.getTokens();
		
		List<ELToken> resolvedExpressionPart = new ArrayList<ELToken>();
		List<ISeamContextVariable> resolvedVariables = new ArrayList<ISeamContextVariable>();
		ScopeType scope = getScope(project, file);
		List<List<ELToken>> variations = getPossibleVarsFromPrefix(tokens);
		
		if (variations.isEmpty()) {
			resolvedVariables = resolveVariables(project, scope, tokens, tokens, returnEqualedVariablesOnly);
		} else {
			for (List<ELToken> variation : variations) {
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
			ELToken token = tokens.get(i);
			
			if (i < tokens.size() - 1) { // inside expression
				if (token.getType() == ELToken.EL_SEPARATOR_TOKEN)
					// proceed with next token
					continue;

				if (token.getType() == ELToken.EL_NAME_TOKEN) {
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
				if (token.getType() == ELToken.EL_METHOD_TOKEN) {
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
				if (token.getType() == ELToken.EL_SEPARATOR_TOKEN) {
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
				} else if (token.getType() == ELToken.EL_NAME_TOKEN ||
					token.getType() == ELToken.EL_METHOD_TOKEN) {
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

		return res;
	}
	
	private String computeVariableName(List<ELToken> tokens){
		if (tokens == null)
			tokens = new ArrayList<ELToken>();
		StringBuffer sb = new StringBuffer();
		for (ELToken token : tokens) {
			if (token.getType() == ELToken.EL_NAME_TOKEN ||
					token.getType() == ELToken.EL_METHOD_TOKEN ||
					token.getType() == ELToken.EL_SEPARATOR_TOKEN) {
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
	private boolean areEqualExpressions(List<ELToken>first, List<ELToken>second) {
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
		
		if (!"java".equals(resource.getFileExtension()))
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
	private List<ISeamContextVariable> resolveVariables(ISeamProject project, ScopeType scope, List<ELToken>part, List<ELToken> tokens, boolean onlyEqualNames) {
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
	private List<List<ELToken>> getPossibleVarsFromPrefix(List<ELToken>prefix) {
		ArrayList<List<ELToken>> result = new ArrayList<List<ELToken>>();
		for (int i = 0; prefix != null && i < prefix.size(); i++) {
			ELToken lastToken = prefix.get(i);
			if (lastToken.getType() != ELToken.EL_SEPARATOR_TOKEN) {
				ArrayList<ELToken> prefixPart = new ArrayList<ELToken>();
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
	public static class SeamELTokenizer {
		static final int STATE_INITIAL = 0;
		static final int STATE_VAR = 1;
		static final int STATE_METHOD = 2;
		static final int STATE_SEPARATOR = 3;
		
//		IDocument fDocument;
		String documentContent;
		List<ELToken> fTokens;
		int index;

		/**
		 * Constructs SeamELTokenizer object
		 * 
		 * @param document
		 * @param offset
		 */
		public SeamELTokenizer(IDocument document, int offset) {
			if(document!=null) {
				this.documentContent = document.get();
			}
			index = (documentContent == null || documentContent.length() < offset? -1 : offset);
			fTokens = new ArrayList<ELToken>();
			parseBackward();
		}

		/**
		 * Constructs SeamELTokenizer object
		 * 
		 * @param document
		 * @param offset
		 */
		public SeamELTokenizer(String documentContent, int offset) {
			this.documentContent = documentContent;
			index = (documentContent == null || documentContent.length() < offset? -1 : offset);
			fTokens = new ArrayList<ELToken>();
			parseBackward();
		}

		/**
		 * Returns list of tokens for the expression parsed
		 * 
		 * @return
		 */
		public List<ELToken> getTokens() {
			return fTokens;
		}
		
		/*
		 * Performs backward parsing of document text for expression
		 */
		private void parseBackward() {
			ELToken token;
			fState = STATE_INITIAL;
			while ((token = getNextToken()) != ELToken.EOF) {
				
				if (token.type == ELToken.EL_NAME_TOKEN ||
						token.type == ELToken.EL_METHOD_TOKEN ||
						token.type == ELToken.EL_SEPARATOR_TOKEN) {
					
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
		private ELToken getNextToken() {
			switch (fState) {
			case STATE_INITIAL: // Just started
			{
				int ch = readCharBackward();
				if (ch == -1) {
					return ELToken.EOF;
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
				return ELToken.EOF;
			}
			case STATE_VAR: // Variable name is read - expecting a separator 
			{
				int ch = readCharBackward();
				if (ch == -1) {
					return ELToken.EOF;
				}
				if (ch == '.') {
					releaseChar();
					return readSeparatorToken();
				}
				return ELToken.EOF;
			}
			case STATE_METHOD: // Method name and parameters are read - expecting a separator
			{
				int ch = readCharBackward();
				if (ch == -1) {
					return ELToken.EOF;
				}
				if (ch == '.') {
					releaseChar();
					return readSeparatorToken();
				}
				return ELToken.EOF;
			}
			case STATE_SEPARATOR: // Separator is read - expecting a var or method
			{
				int ch = readCharBackward();
				if (ch == -1) {
					return ELToken.EOF;
				}
				if (Character.isJavaIdentifierPart((char)ch)) {
					releaseChar();
					return readVarToken();
				}
				if (ch == ')') {
					releaseChar();
					return readMethodToken();
				}
				return ELToken.EOF;
			}
			}
			return ELToken.EOF;
		}
		
		/* Reads and returns the method token from the expression
		 * 
		 * @return
		 */
		ELToken readMethodToken() {
			fState = STATE_METHOD;
			int endOfToken = index;
			
			// read the method parameters
			if (!skipMethodParameters()) 
				return ELToken.EOF;
			
			// skip spaces between the method's name and it's parameters
			if (!skipSpaceChars())
				return ELToken.EOF;
			// read the method name
			if (!skipMethodName())
				return ELToken.EOF;
			
			return (endOfToken - index > 0 ? new ELToken(index, endOfToken - index, getCharSequence(index, endOfToken - index), ELToken.EL_METHOD_TOKEN) : ELToken.EOF);
		}

		/*
		 * Returns the CharSequence object
		 *  
		 * @param start
		 * @param length
		 * @return
		 */
		private CharSequence getCharSequence(int start, int length) {
			String text = "";
			try {
				text = documentContent.substring(start, start + length);
			} catch (StringIndexOutOfBoundsException e) {
				SeamCorePlugin.getDefault().logError(e);
				text = ""; // For sure
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
		ELToken readSeparatorToken() {
			fState = STATE_SEPARATOR;
			int ch = readCharBackward();
			
			return (ch == '.' ? new ELToken(index, 1, getCharSequence(index, 1), ELToken.EL_SEPARATOR_TOKEN) :
				ELToken.EOF);
		}
		
		/* Reads and returns the variable token from the expression
		 * 
		 * @return
		 */
		ELToken readVarToken() {
			fState = STATE_VAR;
			int endOfToken = index;
			int ch;
			while((ch = readCharBackward()) != -1) {
				if (!Character.isJavaIdentifierPart(ch)) {
					releaseChar();
					return (endOfToken - index > 0 ? new ELToken(index, endOfToken - index, getCharSequence(index, endOfToken - index), ELToken.EL_NAME_TOKEN) : ELToken.EOF);
				}
			}
			releaseChar();
			return (endOfToken - index > 0 ? new ELToken(index, endOfToken - index, getCharSequence(index, endOfToken - index), ELToken.EL_NAME_TOKEN) : ELToken.EOF);
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
	 * @throws BadLocationException
	 */
	public static String getPrefix(String documentContent, int offset) throws StringIndexOutOfBoundsException {
		if (documentContent == null || offset > documentContent.length())
			return null;

		SeamELTokenizer tokenizer = new SeamELTokenizer(documentContent, offset);
		List<ELToken> tokens = tokenizer.getTokens();

		if (tokens == null || tokens.size() == 0)
			return null;

		return documentContent.substring(tokens.get(0).start, offset);
//		return documentContent.substring(tokens.get(0).start, tokens.get(0).start + tokens.get(0).length);
	}
}

/**
 * Token for the EX expression
 *  
 * @author Jeremy
 */
class ELToken implements IToken {
	static final ELToken EOF = new ELToken(-1, -1, null, -1);
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
	public ELToken(int start, int length, CharSequence chars, int type) {
		this.start = start;
		this.length = length;
		this.chars = chars;
		this.type = type;
	}
	
	/**
	 * Returns string representation for the token
	 */
	public String toString() {
		return "ELToken(" + start + ", " + length + ", " + type + ") [" + (chars == null ? "<Empty>" : chars.toString()) + "]";
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