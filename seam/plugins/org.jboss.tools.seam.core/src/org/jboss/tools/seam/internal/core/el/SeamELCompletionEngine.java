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
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInstance;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELModel;
import org.jboss.tools.common.el.core.model.ELObjectType;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.el.core.model.ELUtil;
import org.jboss.tools.common.el.core.parser.ELParser;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.el.core.resolver.ElVarSearcher;
import org.jboss.tools.common.el.core.resolver.JavaMemberELSegment;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector;
import org.jboss.tools.common.el.core.resolver.Var;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.common.model.project.ext.event.Change;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine;
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
import org.w3c.dom.Element;

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

	public Image getELProposalImage() {
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
	public List<ISeamContextVariable> resolveVariables(IFile file, ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames) {
		ISeamProject project = SeamCorePlugin.getSeamProject(file.getProject(), false);
		ScopeType scope = getScope(project, file);
		return resolveVariables(project, scope, expr, isFinal, onlyEqualNames);
	}

	protected TypeInfoCollector.MemberInfo getMemberInfoByVariable(ISeamContextVariable var, boolean onlyEqualNames) {
		return SeamExpressionResolver.getMemberInfoByVariable(var, true, this);
	}

	protected void setImage(TextProposal proposal, ISeamContextVariable var) {
		if (isSeamMessagesComponentVariable((ISeamContextVariable)var)) {
			proposal.setImage(SEAM_MESSAGES_PROPOSAL_IMAGE);
		} else {
			proposal.setImage(getELProposalImage());
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

	protected void processSingularMember(TypeInfoCollector.MemberInfo mbr, Set<TextProposal> kbProposals) {
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
		}
	}

	protected void filterSingularMember(TypeInfoCollector.MemberInfo mbr, Set<TypeInfoCollector.MemberPresentation> proposalsToFilter) {
		Collection<String> keys = ((MessagesInfo)mbr).getKeys();
		for (String key : keys) {
			proposalsToFilter.add(new TypeInfoCollector.MemberPresentation(key, mbr));
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
				SeamCorePlugin.getDefault().logError(e);
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

		ELResolution resolution = resolveELOperand(file, expr, true, vars, varSearcher);
		if (resolution.isResolved()) {
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

class StringVariable implements ISeamContextVariable, IJavaSourceReference {
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