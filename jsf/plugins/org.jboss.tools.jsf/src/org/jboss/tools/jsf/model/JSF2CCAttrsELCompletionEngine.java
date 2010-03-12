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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELObjectType;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolutionImpl;
import org.jboss.tools.common.el.core.resolver.ELSegmentImpl;
import org.jboss.tools.common.el.core.resolver.IVariable;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.MemberInfo;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.jsf.JSFModelPlugin;

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

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.resolver.ELResolver2#resolve(org.jboss.tools.common.el.core.resolver.ELContext, org.jboss.tools.common.el.core.model.ELExpression)
	 */
	public ELResolution resolve(ELContext context, ELExpression operand, int offset) {
		ELResolutionImpl resolution = resolveELOperand(operand, context, true);
		if(resolution != null)
			resolution.setContext(context);
		return resolution;
	}

	public ELResolutionImpl resolveELOperand(ELExpression operand,
			ELContext context, boolean returnEqualedVariablesOnly) {
		try {
			return resolveELOperand(context.getResource(), operand, returnEqualedVariablesOnly);
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

		ELResolutionImpl status = resolveELOperand(file, parseOperand("" + prefix), returnEqualedVariablesOnly); //$NON-NLS-1$
		completions.addAll(status.getProposals());

		return completions;
	}

	public ELResolutionImpl resolveELOperand(IFile file,
			ELExpression operand, boolean returnEqualedVariablesOnly)
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

					ELSegmentImpl segment = new ELSegmentImpl();
					segment.setToken(left.getFirstToken());
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

			ELSegmentImpl segment = new ELSegmentImpl();
			segment.setToken(expr.getFirstToken());
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
			ELSegmentImpl segment = new ELSegmentImpl();
			segment.setToken(operand.getFirstToken());
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

		//process segments one by one
		if(left != null) {
			while(left != expr) {
				left = (ELInvocationExpression)left.getParent();
				if (left != expr) { // inside expression
					ELSegmentImpl segment = new ELSegmentImpl();
					segment = new ELSegmentImpl();
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
			ELSegmentImpl segment = new ELSegmentImpl();
			segment.setToken(expr.getFirstToken());
			resolution.addSegment(segment);
		}

		return resolution;
	}

	public List<IVariable> resolveVariablesInternal(IFile file, ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames) {
		List<IVariable> result = new ArrayList<IVariable>();
		String varName = expr.toString();
		String[] vs = {"cc.attrs", "compositeComponent.attrs"};
		for (int i = 0; i < vs.length; i++) {
			String name = vs[i];
			if(!isFinal || onlyEqualNames) {
				if(!name.equals(varName)) continue;
			}
			if(!name.startsWith(varName)) continue;
			Variable v = new Variable(name, file);
			result.add(v);
			break;
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

		ELSegmentImpl segment = new ELSegmentImpl();
		resolution.setProposals(kbProposals);
		if(expr instanceof ELPropertyInvocation) {
			segment.setToken(((ELPropertyInvocation)expr).getName());			
		} else {
			segment.setToken(expr.getFirstToken());			
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
			boolean onlyEqualNames) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine#resolveVariables(org.eclipse.core.resources.IFile, org.jboss.tools.common.el.core.model.ELInvocationExpression, boolean, boolean)
	 */
	@Override
	public List<IVariable> resolveVariables(IFile file,
			ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames) {
		return null;
	}

	@Override
	protected boolean isStaticMethodsCollectingEnabled() {
		return false;
	}

}
