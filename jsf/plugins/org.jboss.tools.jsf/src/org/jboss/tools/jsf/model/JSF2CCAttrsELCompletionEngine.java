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
package org.jboss.tools.jsf.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine;
import org.jboss.tools.common.el.core.model.ELArgumentInvocation;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELObjectType;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.el.core.parser.ELParser;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.parser.LexicalToken;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolutionImpl;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.el.core.resolver.ELSegmentImpl;
import org.jboss.tools.common.el.core.resolver.IOpenableReference;
import org.jboss.tools.common.el.core.resolver.IRelevanceCheck;
import org.jboss.tools.common.el.core.resolver.IVariable;
import org.jboss.tools.common.el.core.resolver.JavaMemberELSegmentImpl;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.MemberInfo;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.model.JSFELCompletionEngine.IJSFVariable;
import org.jboss.tools.jst.web.kb.IXmlContext;
import org.jboss.tools.jst.web.kb.PageContextFactory;

/**
 * 
 * @author V. Kabanovich
 *
 */
public class JSF2CCAttrsELCompletionEngine extends AbstractELCompletionEngine<IVariable> {
	private static final Image JSF2_EL_CC_ATTRS_PROPOSAL_IMAGE = JSFModelPlugin.getDefault().getImage(JSFModelPlugin.CA_JSF_MESSAGES_IMAGE_PATH);

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine#getELProposalImage()
	 */
	public Image getELProposalImage() {
		return JSF2_EL_CC_ATTRS_PROPOSAL_IMAGE;
	}

	private static ELParserFactory factory = ELParserUtil.getDefaultFactory();

	public JSF2CCAttrsELCompletionEngine() {}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.resolver.ELResolver#getParserFactory()
	 */
	public ELParserFactory getParserFactory() {
		return factory;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine#log(java.lang.Exception)
	 */
	protected void log(Exception e) {
		JSFModelPlugin.getPluginLog().logError(e);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.resolver.ELResolver2#getProposals(org.jboss.tools.common.el.core.resolver.ELContext, java.lang.String)
	 */
	public List<TextProposal> getProposals(ELContext context, String el, int offset) {
		return getCompletions(el, false, 0, context);
	}

	public List<TextProposal> getCompletions(String elString,
			boolean returnEqualedVariablesOnly, int position, ELContext context) {
		IDocument document = null;

		List<TextProposal> proposals = null;
		try {
			 proposals = getCompletions(context.getResource(), document, elString.subSequence(0, elString.length()), position, returnEqualedVariablesOnly);
		} catch (StringIndexOutOfBoundsException e) {
			log(e);
		} catch (BadLocationException e) {
			log(e);
		}
		return proposals;
	}

	static String COMPOSITE_URI = "http://java.sun.com/jsf/composite";

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.resolver.ELResolver2#resolve(org.jboss.tools.common.el.core.resolver.ELContext, org.jboss.tools.common.el.core.model.ELExpression)
	 */
	public ELResolution resolve(ELContext context, ELExpression operand, int offset) {
		if(context instanceof IXmlContext) {
			if(((IXmlContext)context).getURIs().contains(COMPOSITE_URI)) {
				ELResolutionImpl resolution = resolveELOperand(operand, context, true, offset);
				if(resolution != null)
					resolution.setContext(context);
				return resolution;
			}
		}
		return null;
	}

	public ELResolutionImpl resolveELOperand(ELExpression operand,
			ELContext context, boolean returnEqualedVariablesOnly, int offset) {
		try {
			return resolveELOperand(context.getResource(), operand, returnEqualedVariablesOnly, offset);
		} catch (StringIndexOutOfBoundsException e) {
			log(e);
		} catch (BadLocationException e) {
			log(e);
		}
		return null;
	}

	public List<TextProposal> getCompletions(IFile file, IDocument document, CharSequence prefix, 
			int position, boolean returnEqualedVariablesOnly) throws BadLocationException, StringIndexOutOfBoundsException {
		List<TextProposal> completions = new ArrayList<TextProposal>();

		ELResolutionImpl status = resolveELOperand(file, parseOperand("" + prefix), returnEqualedVariablesOnly, position); //$NON-NLS-1$
		if(status!=null) {
			completions.addAll(status.getProposals());
		}

		return completions;
	}

	public ELResolutionImpl resolveELOperand(IFile file,
			ELExpression operand, boolean returnEqualedVariablesOnly, int offset)
			throws BadLocationException, StringIndexOutOfBoundsException {
		if(!(operand instanceof ELInvocationExpression) || file == null) {
			return null;
		}

		ELInvocationExpression expr = (ELInvocationExpression)operand;
		boolean isIncomplete = expr.getType() == ELObjectType.EL_PROPERTY_INVOCATION 
			&& ((ELPropertyInvocation)expr).getName() == null;
		boolean isArgument = expr.getType() == ELObjectType.EL_ARGUMENT_INVOCATION;

		ELResolutionImpl resolution = new ELResolutionImpl(expr);
		ELInvocationExpression left = expr;

		List<IVariable> resolvedVariables = new ArrayList<IVariable>();

		if (expr.getLeft() != null && isArgument) {
			left = expr.getLeft();
			resolvedVariables = resolveVariablesInternal(file, left, false, 
					true); 	// is Final and equal names are because of 
							// we have no more to resolve the parts of expression, 
							// but we have to resolve arguments of probably a message component
		} else if (expr.getLeft() == null && isIncomplete) {
			resolvedVariables = resolveVariablesInternal(file, expr, true, 
					returnEqualedVariablesOnly);
		} else {
			while(left != null) {
				List<IVariable>resolvedVars = new ArrayList<IVariable>();
				resolvedVars = resolveVariablesInternal(file, 
						left, left == expr, 
						returnEqualedVariablesOnly);
				if (resolvedVars != null && !resolvedVars.isEmpty()) {
					resolvedVariables = resolvedVars;
					resolution.setLastResolvedToken(left);

					JSF2CCAttrELSegmentImpl segment = new JSF2CCAttrELSegmentImpl(combineLexicalTokensForExpression(left));
					segment.setVarName(left.toString());
					segment.setResource(this.currentFile);
					segment.setResolved(true);
					resolution.addSegment(segment);

					break;
				}
				left = (ELInvocationExpression)left.getLeft();
			} 
		}

		if (resolution.getLastResolvedToken() == null && 
				!returnEqualedVariablesOnly && 
				expr != null && 
				isIncomplete) {
			// no vars are resolved 
			// the tokens are the part of var name ended with a separator (.)
			resolvedVariables = resolveVariablesInternal(file, expr, true, returnEqualedVariablesOnly);			
			Set<TextProposal> proposals = new TreeSet<TextProposal>(TextProposal.KB_PROPOSAL_ORDER);

			ELSegmentImpl segment = new ELSegmentImpl(expr.getFirstToken());
			segment.setResolved(false);
			resolution.addSegment(segment);

			for (IVariable var : resolvedVariables) {
				String varName = var.getName();
				if(varName.startsWith(operand.getText())) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(varName.substring(operand.getLength()));
					setImage(proposal);
					proposals.add(proposal);
				}
			}
			resolution.setProposals(proposals);
			segment.setResolved(!proposals.isEmpty());
			return resolution;
		}

		// Here we have a list of vars for some part of expression
		// OK. we'll proceed with members of these vars
		if (resolution.getLastResolvedToken() == operand) {
			// First segment is the last one
			Set<TextProposal> proposals = new TreeSet<TextProposal>(TextProposal.KB_PROPOSAL_ORDER);
			ELSegmentImpl segment = new ELSegmentImpl(operand.getFirstToken());
			segment.setResolved(true);
			resolution.addSegment(segment);

			for (IVariable var : resolvedVariables) {
				String varName = var.getName();
				if(operand.getLength()<=varName.length()) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(varName.substring(operand.getLength()));
					proposal.setLabel(varName);
					setImage(proposal);
					proposals.add(proposal);
				} else if(returnEqualedVariablesOnly) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(varName);
					proposal.setLabel(varName);
					setImage(proposal);
					proposals.add(proposal);
				}
				segment.getVariables().add(var);
			}
			resolution.setLastResolvedToken(expr);
			resolution.setProposals(proposals);
			return resolution;
		}
		
		if(!resolvedVariables.isEmpty() && resolvedVariables.iterator().next() instanceof IJSFVariable) {
			return buildJavaResolution(resolution, left, expr, operand, resolvedVariables, returnEqualedVariablesOnly, offset);
		}

		//process segments one by one
		if(left != null) {
			while(left != expr) {
				left = (ELInvocationExpression)left.getParent();
				if (left != expr) { // inside expression
					ELSegmentImpl segment = new ELSegmentImpl(left.getLastToken());
					segment.setResolved(true);
					resolution.addSegment(segment);
					resolution.setLastResolvedToken(left);
					return resolution;
				} else { // Last segment
					resolveLastSegment((ELInvocationExpression)operand, resolvedVariables, resolution, returnEqualedVariablesOnly);
					break;
				}
			}
		} else {
			ELSegmentImpl segment = new ELSegmentImpl(expr.getFirstToken());
			resolution.addSegment(segment);
		}

		return resolution;
	}

	//Method content copies code from the end AbstractELCompletionEngine.resolveELOperand
	ELResolutionImpl buildJavaResolution(ELResolutionImpl resolution, ELInvocationExpression left, ELInvocationExpression expr,
			ELExpression operand, List<IVariable> resolvedVariables, boolean returnEqualedVariablesOnly, int offset) {
		boolean varIsUsed = false;
		// First segment is found - proceed with next tokens 
		List<TypeInfoCollector.MemberInfo> members = new ArrayList<TypeInfoCollector.MemberInfo>();
		JavaMemberELSegmentImpl segment = new JavaMemberELSegmentImpl(expr.getFirstToken());
		for (IVariable var : resolvedVariables) {
			TypeInfoCollector.MemberInfo member = getMemberInfoByVariable(var, returnEqualedVariablesOnly, offset);
			if (member != null && !members.contains(member)) { 
				members.add(member);
				segment.setMemberInfo(member);
				segment.getVariables().add(var);
				segment.setResolved(true);
			}
		}
		resolution.addSegment(segment);
		//process segments one by one
		if(left != null) {
			while(left != expr) {
				left = (ELInvocationExpression)left.getParent();
				if (left != expr) { // inside expression
					segment = new JavaMemberELSegmentImpl(left.getLastToken());
					if(left instanceof ELArgumentInvocation) {
						String s = "#{" + left.getLeft().toString() + collectionAdditionForCollectionDataModel + "}"; //$NON-NLS-1$ //$NON-NLS-2$
						ELParser p = getParserFactory().createParser();
						ELInvocationExpression expr1 = (ELInvocationExpression)p.parse(s).getInstances().get(0).getExpression();
						members = resolveSegment(expr1.getLeft(), members, resolution, returnEqualedVariablesOnly, varIsUsed, segment);
						members = resolveSegment(expr1, members, resolution, returnEqualedVariablesOnly, varIsUsed, segment);
						if(resolution.getLastResolvedToken() == expr1) {
							resolution.setLastResolvedToken(left);
						}
					} else {
						members = resolveSegment(left, members, resolution, returnEqualedVariablesOnly, varIsUsed, segment);
					}
					if(!members.isEmpty()) {
						segment.setResolved(true);
						segment.setMemberInfo(members.get(0));	// TODO: This is a buggy way to select a member to setup in a segment
					}
					resolution.addSegment(segment);
				} else { // Last segment
					resolveLastSegment((ELInvocationExpression)operand, members, resolution, returnEqualedVariablesOnly, varIsUsed);
					break;
				}
			}
		}

		if(resolution.getProposals().isEmpty() && !resolution.getSegments().isEmpty()) {
//			&& status.getUnpairedGettersOrSetters()!=null) {
			ELSegment lastSegment = resolution.getSegments().get(resolution.getSegments().size()-1);
			if(lastSegment instanceof JavaMemberELSegmentImpl) {
				((JavaMemberELSegmentImpl)lastSegment).clearUnpairedGettersOrSetters();
			}
		}
		return resolution;
	}

	static String[] vs = {"cc.attrs", "compositeComponent.attrs"};
	private IFile currentFile;
	private ELContext currentContext;
	private XModelObject currentXModelObject;

	public List<IVariable> resolveVariablesInternal(IFile file, ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames) {
		ELContext context = null;
		if(currentXModelObject==null || currentFile!=file || (context = PageContextFactory.createPageContext(file))!=currentContext) {
			currentXModelObject = EclipseResourceUtil.createObjectForResource(file);
			currentFile = file;
			if(currentContext!=context) {
				if(currentXModelObject == null) return Collections.emptyList();
				if(!"FileJSF2Component".equals(currentXModelObject.getModelEntity().getName())) return Collections.emptyList();;
			}
			currentContext = context;
		}

		List<IVariable> result = new ArrayList<IVariable>();

		String varName = expr.toString();

		for (int i = 0; i < vs.length; i++) {
			String name = vs[i];
			if(!isFinal || onlyEqualNames) {
				if(!name.equals(varName)) continue;
			}
			if(!name.startsWith(varName)) continue;
			if(varName.lastIndexOf('.') > name.length()) continue; //It is the java variable case
			Variable v = new Variable(name, file);
			result.add(v);
			break;
		}

		if(currentXModelObject != null && result.isEmpty()) {
			IJavaProject javaProject = EclipseResourceUtil.getJavaProject(file.getProject());
			XModelObject is = currentXModelObject.getChildByPath("Interface");
			if(is != null && javaProject != null) {			
				XModelObject[] cs = is.getChildren("JSF2ComponentAttribute");

				for (int i = 0; i < cs.length; i++) {
					String name = cs[i].getAttributeValue("name");
					String type = cs[i].getAttributeValue("type");
					if(type == null || type.length() == 0) continue;
					String[] names = {vs[0] + "." + name, vs[1] + "." + name};
					for (String n: names) {
						boolean match = (!isFinal || onlyEqualNames) ? n.equals(varName) : false;
						if(!match) continue;
						IType javaType = null;
						try {
							javaType = EclipseJavaUtil.findType(javaProject, type);
						} catch (JavaModelException e) {
							
						}
						if(javaType == null) continue;
						IVariable v = new JSFELCompletionEngine.Variable(n, javaType);
						result.add(v);
					}
				}
			}
		}

		return result;
	}

	protected void setImage(TextProposal kbProposal) {
		kbProposal.setImage(getELProposalImage());
	}

	protected void resolveLastSegment(ELInvocationExpression expr, 
			List<IVariable> members,
			ELResolutionImpl resolution,
			boolean returnEqualedVariablesOnly) {
		Set<TextProposal> kbProposals = new TreeSet<TextProposal>(TextProposal.KB_PROPOSAL_ORDER);
		
		JSF2CCAttrELSegmentImpl segment = new JSF2CCAttrELSegmentImpl(expr.getFirstToken());
		segment.setResource(this.currentFile);
		segment.setVarName(expr.toString());
		resolution.setProposals(kbProposals);
		if(expr instanceof ELPropertyInvocation) {
			segment.setToken(((ELPropertyInvocation)expr).getName());			
		}

		if(segment.getToken()!=null) {
			resolution.addSegment(segment);
		}

		if (expr.getType() == ELObjectType.EL_PROPERTY_INVOCATION && ((ELPropertyInvocation)expr).getName() == null) {
			// return all the methods + properties
			for (IVariable mbr : members) {
				processSingularMember(mbr, kbProposals);
			}
		} else
			if(expr.getType() != ELObjectType.EL_ARGUMENT_INVOCATION) {
			Set<String> proposalsToFilter = new TreeSet<String>(); 
			for (IVariable mbr : members) {
					filterSingularMember(mbr, proposalsToFilter);
			}
			for (String proposal : proposalsToFilter) {
				// We do expect nothing but name for method tokens (No round brackets)
				String filter = expr.getMemberName();
				if(filter == null) filter = ""; //$NON-NLS-1$
				if(returnEqualedVariablesOnly) {
					// This is used for validation.
					if (proposal.equals(filter)) {
						TextProposal kbProposal = new TextProposal();
						kbProposal.setReplacementString(proposal);
						kbProposal.setLabel(proposal);
						setImage(kbProposal);

						kbProposals.add(kbProposal);

						break;
					}
				} else if (proposal.startsWith(filter)) {
					// This is used for CA.
					TextProposal kbProposal = new TextProposal();
					kbProposal.setReplacementString(proposal.substring(filter.length()));
					kbProposal.setLabel(proposal);
					kbProposal.setImage(getELProposalImage());
					
					kbProposals.add(kbProposal);
				}
			}
		} else if(expr.getType() == ELObjectType.EL_ARGUMENT_INVOCATION) {
			Set<String> proposalsToFilter = new TreeSet<String>();
			boolean isMessages = false;
			for (IVariable mbr : members) {
				isMessages = true;
				filterSingularMember(mbr, proposalsToFilter);
			}

			String filter = expr.getMemberName();
			boolean bSurroundWithQuotes = false;
			if(filter == null) {
				filter = ""; //$NON-NLS-1$
				bSurroundWithQuotes = true;
			} else {
				boolean b = filter.startsWith("'") || filter.startsWith("\""); //$NON-NLS-1$ //$NON-NLS-2$
				boolean e = filter.endsWith("'") || filter.endsWith("\""); //$NON-NLS-1$ //$NON-NLS-2$
				if((b) && (e)) {
					filter = filter.length() == 1 ? "" : filter.substring(1, filter.length() - 1); //$NON-NLS-1$
				} else if(b && !returnEqualedVariablesOnly) {
					filter = filter.substring(1);
				} else {
					//Value is set as expression itself, we cannot compute it
					if(isMessages) {
						resolution.setMapOrCollectionOrBundleAmoungTheTokens(true);
					}
					return;
				}
			}

			for (String proposal : proposalsToFilter) {
				if(returnEqualedVariablesOnly) {
					// This is used for validation.
					if (proposal.equals(filter)) {
						TextProposal kbProposal = new TextProposal();
						kbProposal.setReplacementString(proposal);
						kbProposal.setLabel(proposal);
						setImage(kbProposal);

						kbProposals.add(kbProposal);

						break;
					}
				} else if (proposal.startsWith(filter)) {
					// This is used for CA.
					TextProposal kbProposal = new TextProposal();

					String replacementString = proposal.substring(filter.length());
					if (bSurroundWithQuotes) {
						replacementString = "'" + replacementString + "']"; //$NON-NLS-1$ //$NON-NLS-2$
					}

					kbProposal.setReplacementString(replacementString);
					kbProposal.setLabel(proposal);
					kbProposal.setImage(getELProposalImage());

					kbProposals.add(kbProposal);
				}
			}
		}
		segment.setResolved(!kbProposals.isEmpty());
		if (resolution.isResolved()){
			resolution.setLastResolvedToken(expr);
		}
	}

	protected void processSingularMember(IVariable mbr, Set<TextProposal> kbProposals) {
		// Surround the "long" keys containing the dots with [' '] 
		TreeSet<String> keys = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		keys.addAll(((Variable)mbr).getKeys());
		Iterator<String> sortedKeys = keys.iterator();
		while(sortedKeys.hasNext()) {
			String key = sortedKeys.next();
			if (key == null || key.length() == 0)
				continue;
			if (key.indexOf('.') != -1) {
				TextProposal proposal = new TextProposal();
				proposal.setReplacementString("['" + key + "']"); //$NON-NLS-1$ //$NON-NLS-2$
				proposal.setLabel("['" + key + "']");
				setImage(proposal);
				
				kbProposals.add(proposal);
			} else {
				TextProposal proposal = new TextProposal();
				proposal.setReplacementString(key);
				proposal.setLabel(key);
				setImage(proposal);
				
				kbProposals.add(proposal);
			}
		}
	}

	protected void filterSingularMember(IVariable mbr, Set<String> proposalsToFilter) {
		Collection<String> keys = ((Variable)mbr).getKeys();
		for (String key : keys) {
			proposalsToFilter.add(key);
		}
	}

	static String[] COMMON_ATTRS = {"onclick", "ondblclick", "onkeydown", "onkeypress", "onkeyup", "onmousedown", "onmousemove", "onmouseout", "onmouseover", "onmouseup"};

	static class Variable implements IVariable {
		IFile f;
		String name;

		public Variable(String name, IFile f) {
			this.name = name;
			this.f = f;
		}

		public String getName() {
			return name;
		}

		public Collection<String> getKeys() {
			TreeSet<String> result = new TreeSet<String>();
			
			XModelObject o = EclipseResourceUtil.createObjectForResource(f);
			if(o == null) return result;
			if(!"FileJSF2Component".equals(o.getModelEntity().getName())) return result;
			
			for (int i = 0; i < COMMON_ATTRS.length; i++) {
				result.add(COMMON_ATTRS[i]);
			}

			XModelObject is = o.getChildByPath("Interface");
			if(is == null) return result;
			
			XModelObject[] cs = is.getChildren("JSF2ComponentAttribute");

			for (int i = 0; i < cs.length; i++) {
				result.add(cs[i].getAttributeValue("name"));
			}
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine#getMemberInfoByVariable(org.jboss.tools.common.el.core.resolver.IVariable, boolean)
	 */
	@Override
	protected MemberInfo getMemberInfoByVariable(IVariable var,
			boolean onlyEqualNames, int offset) {
		if(var instanceof IJSFVariable) {
			return TypeInfoCollector.createMemberInfo(((IJSFVariable)var).getSourceMember());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine#resolveVariables(org.eclipse.core.resources.IFile, org.jboss.tools.common.el.core.model.ELInvocationExpression, boolean, boolean)
	 */
	@Override
	public List<IVariable> resolveVariables(IFile file,
			ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames, int offset) {
		return resolveVariablesInternal(file, expr, isFinal, onlyEqualNames);
	}

	@Override
	protected boolean isStaticMethodsCollectingEnabled() {
		return false;
	}


	public IRelevanceCheck createRelevanceCheck(IJavaElement element) {
		return IRRELEVANT;
	}
}

class JSF2CCAttrELSegmentImpl extends ELSegmentImpl {
	String varName;
	IFile file;

	public JSF2CCAttrELSegmentImpl(LexicalToken token) {
		super(token);
	}
	
	public void setVarName(String s) {
		varName = s;
	}

	public void setResource(IFile f) {
		file = f;
		super.setResource(f);
	}
	
	public IOpenableReference[] getOpenable() {
		final XModelObject o = findJSF2CCAttributeXModelObject(varName, file);
		if(o != null) {
			IOpenableReference openable = new IOpenableReference() {
				@Override
				public boolean open() {
					int q = FindObjectHelper.findModelObject(o, FindObjectHelper.IN_EDITOR_ONLY);
					return q == 0;
				}				
				@Override
				public String getLabel() {
					return Messages.OpenJsf2CCAttribute;
				}				
				@Override
				public Image getImage() {
					return null;
				}
			};
			return new IOpenableReference[]{openable};
		}
		return new IOpenableReference[0];
	}

	static String[] vs = {"cc.attrs", "compositeComponent.attrs"}; //$NON-NLS-1$ //$NON-NLS-2$

	public static XModelObject findJSF2CCAttributeXModelObject(String varName, IFile file) {
		XModelObject xModelObject = EclipseResourceUtil.createObjectForResource(file);
		if(xModelObject == null) return null;
		if(!"FileJSF2Component".equals(xModelObject.getModelEntity().getName())) return null;

		IJavaProject javaProject = EclipseResourceUtil.getJavaProject(file.getProject());
		XModelObject is = xModelObject.getChildByPath("Interface");
		if(is != null && javaProject != null) {	
			for (int i = 0; i < vs.length; i++) {
				if (vs[i].equals(varName)) return is;
			}
			XModelObject[] cs = is.getChildren("JSF2ComponentAttribute");

			for (int i = 0; i < cs.length; i++) {
				String name = cs[i].getAttributeValue("name");
				String[] names = {vs[0] + "." + name, vs[1] + "." + name};
				for (String n: names) {
					if (n.equals(varName)) return cs[i];
				}
			}
		}
		return null;
	}
}
