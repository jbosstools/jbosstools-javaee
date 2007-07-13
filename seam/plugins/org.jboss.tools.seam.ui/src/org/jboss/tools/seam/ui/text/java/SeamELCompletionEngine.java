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
package org.jboss.tools.seam.ui.text.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import org.jboss.tools.seam.ui.SeamGuiPlugin;

public final class SeamELCompletionEngine {
	
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
			int position) throws BadLocationException {
		
		List<String> res= new ArrayList<String>();
		SeamELTokenizer tokenizer = new SeamELTokenizer(document, position + prefix.length());
		List<ELToken> tokens = tokenizer.getTokens();
		
		List<ELToken> resolvedExpressionPart = new ArrayList<ELToken>();
		List<ISeamContextVariable> resolvedVariables = null;
		ScopeType scope = getScope(project, file);
		List<List<ELToken>> variations = getPossibleVarsFromPrefix(tokens);
		
		if (variations.isEmpty()) {
			resolvedVariables = resolveVariables(project, scope, tokens, tokens);
		} else {
			for (List<ELToken> variation : variations) {
				List<ISeamContextVariable>resolvedVars = new ArrayList<ISeamContextVariable>();
				resolvedVars = resolveVariables(project, scope, variation, tokens);
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
				res.add(var.getName().substring(prefix.toString().length()));
			}
			return res;
		}

		// First segment is found - proceed with next tokens 
		int startTokenIndex = (resolvedExpressionPart == null ? 0 : resolvedExpressionPart.size());
		Set<IMember> members = new HashSet<IMember>();
		for (ISeamContextVariable var : resolvedVariables) {
			IMember member = SeamExpressionResolver.getMemberByVariable(var);
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
							SeamGuiPlugin.getPluginLog().logError(ex);
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
							SeamGuiPlugin.getPluginLog().logError(ex);
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
							proposals.addAll(SeamExpressionResolver.getPropertyPresentations(type));
						} catch (JavaModelException ex) {
							SeamGuiPlugin.getPluginLog().logError(ex);
						}
					}
				} else if (token.getType() == ELToken.EL_NAME_TOKEN ||
					token.getType() == ELToken.EL_METHOD_TOKEN) {
					// return filtered methods + properties 
					Set<String> proposalsToFilter = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER); 
					for (IMember mbr : members) {
						try {
							IType type = EclipseJavaUtil.findType(mbr.getJavaProject(), EclipseJavaUtil.getMemberTypeAsString(mbr));
							proposalsToFilter.addAll(SeamExpressionResolver.getMethodPresentations(type));
							proposalsToFilter.addAll(SeamExpressionResolver.getPropertyPresentations(type));
						} catch (JavaModelException ex) {
							SeamGuiPlugin.getPluginLog().logError(ex);
						}
					}
					for (String proposal : proposalsToFilter) {
						// We do expect nothing but name for method tokens (No round brackets)
						String filter = token.getText();
						if (proposal.startsWith(filter)) {
							proposals.add(proposal.substring(filter.length()));
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
	
	private ScopeType getScope(ISeamProject project, IResource resource) {
		if (project == null || resource == null)
			return null;
		
		Set<ISeamComponent> components = project.getComponentsByPath(resource.getFullPath());

		if (components.size() > 1) // Don't use scope in case of more than one component
			return null;
		for (ISeamComponent component : components) {
			return component.getScope();
		}
		return null;
	}
	
	List<ISeamContextVariable> resolveVariables(ISeamProject project, ScopeType scope, List<ELToken>part, List<ELToken> tokens) {
		List<ISeamContextVariable>resolvedVars = new ArrayList<ISeamContextVariable>();
		String varName = computeVariableName(part);
		if (varName != null) {
			resolvedVars = SeamExpressionResolver.resolveVariables(project, scope, varName);
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
	 * Removes duplicates
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
	
	public static class SeamELTokenizer {
		static final int STATE_INITIAL = 0;
		static final int STATE_VAR = 1;
		static final int STATE_METHOD = 2;
		static final int STATE_SEPARATOR = 3;
		
		IDocument fDocument;
		List<ELToken> fTokens;
		int index;
		
		public SeamELTokenizer(IDocument document, int offset) {
			fDocument = document;
			index = (fDocument == null || fDocument.getLength() < offset? -1 : offset);
			fTokens = new ArrayList<ELToken>();
			parseBackward();
		}
		
		public List<ELToken> getTokens() {
			return fTokens;
		}
		
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
		ELToken getNextToken() {
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
		
		private CharSequence getCharSequence(int start, int length) {
			String text = "";
			try {
				text = fDocument.get(start, length);
			} catch (BadLocationException e) {
				text = ""; // For sure
			}
			return text.subSequence(0, text.length());
		}

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
		
		ELToken readSeparatorToken() {
			fState = STATE_SEPARATOR;
			int ch = readCharBackward();
			
			return (ch == '.' ? new ELToken(index, 1, getCharSequence(index, 1), ELToken.EL_SEPARATOR_TOKEN) :
				ELToken.EOF);
		}
		
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
		
		int readCharBackward() {
			if (--index < 0 || 
					fDocument == null ||
					fDocument.getLength() <= index)
				return -1;

			try {
				return fDocument.getChar(index);
			} catch (BadLocationException e) {
				return -1;
			}
		}
		
		void releaseChar() {
			if (index < fDocument.getLength())
				index++;
		}
	}

	public static String getPrefix(ITextViewer viewer, int offset) throws BadLocationException {
		IDocument doc= viewer.getDocument();
		if (doc == null || offset > doc.getLength())
			return null;
		
		SeamELTokenizer tokenizer = new SeamELTokenizer(doc, offset);
		List<ELToken> tokens = tokenizer.getTokens();

		if (tokens == null || tokens.size() == 0)
			return null;
		
		return doc.get(tokens.get(0).start, offset - tokens.get(0).start);
	}

	
}
class ELToken implements IToken {
	static final ELToken EOF = new ELToken(-1, -1, null, -1);
	static final int EL_NAME_TOKEN = 1;
	static final int EL_METHOD_TOKEN = 2;
	static final int EL_SEPARATOR_TOKEN = 3;

	int start;
	int length;
	CharSequence chars;
	int type;
	
	public ELToken(int start, int length, CharSequence chars, int type) {
		this.start = start;
		this.length = length;
		this.chars = chars;
		this.type = type;
	}
	
	public String toString() {
		return "ELToken(" + start + ", " + length + ", " + type + ") [" + (chars == null ? "<Empty>" : chars.toString()) + "]";
	}

	public Object getData() {
		return (chars == null ? null : chars.subSequence(start, start+length).toString());
	}

	public boolean isEOF() {
		return (start == -1 && length == -1 && chars == null);
	}

	public boolean isOther() {
		return false;
	}

	public boolean isUndefined() {
		return false;
	}

	public boolean isWhitespace() {
		return false;
	}
	
	public int getType(){
		return type;
	}
	
	public String getText() {
		return chars.toString();
	}
}
