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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInstance;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELModel;
import org.jboss.tools.common.el.core.model.ELObjectType;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.el.core.parser.ELParser;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELOperandResolveStatus;
import org.jboss.tools.common.el.core.resolver.ELResolver;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jst.web.kb.IPageContext;
import org.jboss.tools.jst.web.kb.IResourceBundle;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

public class JSFMessageELCompletionEngine implements ELResolver {
	private static final Image JSF_EL_MESSAGES_PROPOSAL_IMAGE = JSFModelPlugin.getDefault().getImage(JSFModelPlugin.CA_JSF_MESSAGES_IMAGE_PATH);

	public Image getELProposalImage() {
		return JSF_EL_MESSAGES_PROPOSAL_IMAGE;
	}

	private static ELParserFactory factory = ELParserUtil.getDefaultFactory();

	public JSFMessageELCompletionEngine() {}

	public ELParserFactory getParserFactory() {
		return factory;
	}

	protected void log(Exception e) {
		JSFModelPlugin.getPluginLog().logError(e);
	}

	protected ELOperandResolveStatus newELOperandResolveStatus(ELInvocationExpression tokens) {
		return new ELOperandResolveStatus(tokens);
	}

	public List<TextProposal> getCompletions(String elString,
			boolean returnEqualedVariablesOnly, int position, ELContext context) {
		IDocument document = null;
		IResourceBundle[] bundles = new IResourceBundle[0];
		if(context instanceof IPageContext) {
			IPageContext pageContext = (IPageContext)context;
			document = pageContext.getDocument();
			bundles = pageContext.getResourceBundles();
		}

		List<TextProposal> proposals = null;
		try {
			 proposals = getCompletions(context.getResource(), document, elString.subSequence(0, elString.length()), position, returnEqualedVariablesOnly, bundles);
		} catch (StringIndexOutOfBoundsException e) {
			log(e);
		} catch (BadLocationException e) {
			log(e);
		}
		return proposals;
	}

	public ELOperandResolveStatus resolveELOperand(ELExpression operand,
			ELContext context, boolean returnEqualedVariablesOnly) {
		ELOperandResolveStatus status = null;
		IResourceBundle[] bundles = new IResourceBundle[0];
		if(context instanceof IPageContext) {
			IPageContext pageContext = (IPageContext)context;
			bundles = pageContext.getResourceBundles();
		}
		try {
			status = resolveELOperand(context.getResource(), operand, returnEqualedVariablesOnly, bundles);
		} catch (StringIndexOutOfBoundsException e) {
			log(e);
		} catch (BadLocationException e) {
			log(e);
		}
		return status;
	}

	public List<TextProposal> getCompletions(IFile file, IDocument document, CharSequence prefix, 
			int position, boolean returnEqualedVariablesOnly, IResourceBundle[] bundles) throws BadLocationException, StringIndexOutOfBoundsException {
		List<TextProposal> completions = new ArrayList<TextProposal>();
		
		ELOperandResolveStatus status = resolveELOperand(file, parseOperand("" + prefix), returnEqualedVariablesOnly, bundles);
		if (status.isOK()) {
			completions.addAll(status.getProposals());
		}

		return completions;
	}

	public ELExpression parseOperand(String operand) {
		if(operand == null) return null;
		String el = (operand.indexOf("#{") < 0 && operand.indexOf("${") < 0) ? "#{" + operand + "}" : operand;
		ELParser p = getParserFactory().createParser();
		ELModel model = p.parse(el);
		List<ELInstance> is = model.getInstances();
		if(is.isEmpty()) return null;
		return is.get(0).getExpression();
	}

	public ELOperandResolveStatus resolveELOperand(IFile file,
			ELExpression operand, boolean returnEqualedVariablesOnly, IResourceBundle[] bundles)
			throws BadLocationException, StringIndexOutOfBoundsException {
		if(!(operand instanceof ELInvocationExpression) || file == null) {
			return newELOperandResolveStatus(null);
		}

		ELInvocationExpression expr = (ELInvocationExpression)operand;
		boolean isIncomplete = expr.getType() == ELObjectType.EL_PROPERTY_INVOCATION 
			&& ((ELPropertyInvocation)expr).getName() == null;
		boolean isArgument = expr.getType() == ELObjectType.EL_ARGUMENT_INVOCATION;

		ELOperandResolveStatus status = newELOperandResolveStatus(expr);
		ELInvocationExpression left = expr;

		List<Variable> resolvedVariables = new ArrayList<Variable>();

		if (expr.getLeft() != null && isArgument) {
			left = expr.getLeft();
			resolvedVariables = resolveVariables(file, left, bundles, false, 
					true); 	// is Final and equal names are because of 
							// we have no more to resolve the parts of expression, 
							// but we have to resolve arguments of probably a message component
		} else if (expr.getLeft() == null && isIncomplete) {
			resolvedVariables = resolveVariables(file, expr, bundles, true, 
					returnEqualedVariablesOnly);
		} else {
			while(left != null) {
				List<Variable>resolvedVars = new ArrayList<Variable>();
				resolvedVars = resolveVariables(file, 
						left, bundles, left == expr, 
						returnEqualedVariablesOnly);
				if (resolvedVars != null && !resolvedVars.isEmpty()) {
					resolvedVariables = resolvedVars;
					status.setLastResolvedToken(left);
					break;
				}
				left = (ELInvocationExpression)left.getLeft();
			} 
		}

		if (status.getResolvedTokens() == null && 
				!returnEqualedVariablesOnly && 
				expr != null && 
				isIncomplete) {
			// no vars are resolved 
			// the tokens are the part of var name ended with a separator (.)
			resolvedVariables = resolveVariables(file, expr, bundles, true, returnEqualedVariablesOnly);			
			Set<TextProposal> proposals = new TreeSet<TextProposal>(TextProposal.KB_PROPOSAL_ORDER);
			for (Variable var : resolvedVariables) {
				String varName = var.getName();
				if(varName.startsWith(operand.getText())) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(varName.substring(operand.getLength()));
					setImage(proposal);
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
			for (Variable var : resolvedVariables) {
				String varName = var.getName();
				if(operand.getLength()<=varName.length()) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(varName.substring(operand.getLength()));
					setImage(proposal);
					proposals.add(proposal);
				} else if(returnEqualedVariablesOnly) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(varName);
					setImage(proposal);
					proposals.add(proposal);
				}
			}
			status.setLastResolvedToken(expr);
			status.setProposals(proposals);
			return status;
		}

		//process segments one by one
		if(left != null) while(left != expr) {
			left = (ELInvocationExpression)left.getParent();
			if (left != expr) { // inside expression
				return status;
			} else { // Last segment
				resolveLastSegment((ELInvocationExpression)operand, resolvedVariables, status, returnEqualedVariablesOnly);
				break;
			}
		}

		return status;
	}

	public List<Variable> resolveVariables(IFile file, ELInvocationExpression expr, IResourceBundle[] bundles, boolean isFinal, boolean onlyEqualNames) {
		List<Variable> result = new ArrayList<Variable>();
		if(expr.getLeft() != null) return result;
		IModelNature n = EclipseResourceUtil.getModelNature(file.getProject());
		if(n == null) return result;
		XModel model = n.getModel();
		String varName = expr.toString();
		for (IResourceBundle b: bundles) {
			String name = b.getVar();
			if(!isFinal || onlyEqualNames) {
				if(!name.equals(varName)) continue;
			}
			if(!name.startsWith(varName)) continue;
			Variable v = new Variable(name, b.getBasename(), file);
			result.add(v);
		}
		List l = WebPromptingProvider.getInstance().getList(model, WebPromptingProvider.JSF_REGISTERED_BUNDLES, null, null);
		if(l != null && l.size() > 0 && (l.get(0) instanceof Map)) {
			Map map = (Map)l.get(0);
			Iterator it = map.keySet().iterator();
			while(it.hasNext()) {
				String name = it.next().toString();
				String basename = map.get(name).toString();
				if(!isFinal || onlyEqualNames) {
					if(!name.equals(varName)) continue;
				}
				Variable v = new Variable(name, basename, file);
				result.add(v);
			}
		}

		return result;
	}

	protected void setImage(TextProposal kbProposal) {
		kbProposal.setImage(getELProposalImage());
	}

	protected void resolveLastSegment(ELInvocationExpression expr, 
			List<Variable> members,
			ELOperandResolveStatus status,
			boolean returnEqualedVariablesOnly) {
		Set<TextProposal> kbProposals = new TreeSet<TextProposal>(TextProposal.KB_PROPOSAL_ORDER);
		
		if (expr.getType() == ELObjectType.EL_PROPERTY_INVOCATION && ((ELPropertyInvocation)expr).getName() == null) {
			// return all the methods + properties
			for (Variable mbr : members) {
				processSingularMember(mbr, kbProposals);
			}
		} else
			if(expr.getType() != ELObjectType.EL_ARGUMENT_INVOCATION) {
			Set<String> proposalsToFilter = new TreeSet<String>(); 
			for (Variable mbr : members) {
					filterSingularMember(mbr, proposalsToFilter);
			}
			for (String proposal : proposalsToFilter) {
				// We do expect nothing but name for method tokens (No round brackets)
				String filter = expr.getMemberName();
				if(filter == null) filter = "";
				if(returnEqualedVariablesOnly) {
					// This is used for validation.
					if (proposal.equals(filter)) {
						TextProposal kbProposal = new TextProposal();
						kbProposal.setReplacementString(proposal);

						setImage(kbProposal);
						
						kbProposals.add(kbProposal);

						break;
					}
				} else if (proposal.startsWith(filter)) {
					// This is used for CA.
					TextProposal kbProposal = new TextProposal();
					kbProposal.setReplacementString(proposal.substring(filter.length()));
					kbProposal.setImage(getELProposalImage());
					
					kbProposals.add(kbProposal);
				}
			}
		} else if(expr.getType() == ELObjectType.EL_ARGUMENT_INVOCATION) {
			Set<String> proposalsToFilter = new TreeSet<String>();
			boolean isMessages = false;
			for (Variable mbr : members) {
					isMessages = true;
					filterSingularMember(mbr, proposalsToFilter);
			}

			String filter = expr.getMemberName();
			boolean bSurroundWithQuotes = false;
			if(filter == null) {
				filter = "";
				bSurroundWithQuotes = true;
			} else {
				boolean b = filter.startsWith("'") || filter.startsWith("\"");
				boolean e = filter.endsWith("'") || filter.endsWith("\"");
				if((b) && (e)) {
					filter = filter.substring(1, filter.length() - 1);
				} else if(b && !returnEqualedVariablesOnly) {
					filter = filter.substring(1);
				} else {
					//Value is set as expression itself, we cannot compute it
					if(isMessages) status.setMapOrCollectionOrBundleAmoungTheTokens();
					return;
				}
			}
			
			for (String proposal : proposalsToFilter) {
				if(returnEqualedVariablesOnly) {
					// This is used for validation.
					if (proposal.equals(filter)) {
						TextProposal kbProposal = new TextProposal();
						kbProposal.setReplacementString(proposal);
						
						setImage(kbProposal);
						
						kbProposals.add(kbProposal);

						break;
					}
				} else if (proposal.startsWith(filter)) {
					// This is used for CA.
					TextProposal kbProposal = new TextProposal();
					
					String replacementString = proposal.substring(filter.length());
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

	protected void processSingularMember(Variable mbr, Set<TextProposal> kbProposals) {
			// Surround the "long" keys containing the dots with [' '] 
			TreeSet<String> keys = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			keys.addAll(mbr.getKeys());
			Iterator<String> sortedKeys = keys.iterator();
			while(sortedKeys.hasNext()) {
				String key = sortedKeys.next();
				if (key == null || key.length() == 0)
					continue;
				if (key.indexOf('.') != -1) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString("['" + key + "']");
					setImage(proposal);
					
					kbProposals.add(proposal);
				} else {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(key);
					setImage(proposal);
					
					kbProposals.add(proposal);
				}
			}
	}

	protected void filterSingularMember(Variable mbr, Set<String> proposalsToFilter) {
		Collection<String> keys = mbr.getKeys();
		for (String key : keys) {
			proposalsToFilter.add(key);
		}
	}

	static class Variable {
		IFile f;
		String name;
		String basename;
		
		public Variable(String name, String basename, IFile f) {
			this.name = name;
			this.basename = basename;
			this.f = f;
		}

		public String getName() {
			return name;
		}
	
		public String getBasename() {
			return basename;
		}
		
		public Collection<String> getKeys() {
			TreeSet<String> result = new TreeSet<String>();
			IModelNature n = EclipseResourceUtil.getModelNature(f.getProject());
			if(n == null) return result;
			XModel model = n.getModel();

			List l = WebPromptingProvider.getInstance().getList(model, WebPromptingProvider.JSF_BUNDLE_PROPERTIES, basename, null);
			for (int i = 0; i < l.size(); i++) {
				result.add(l.get(i).toString());
			}
			return result;
		}
	}

}
