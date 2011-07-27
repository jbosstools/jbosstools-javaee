/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.seam.core.international.el;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.seam.core.CDISeamCorePlugin;
import org.jboss.tools.cdi.seam.core.international.BundleModelFactory;
import org.jboss.tools.cdi.seam.core.international.IBundle;
import org.jboss.tools.cdi.seam.core.international.IBundleModel;
import org.jboss.tools.cdi.seam.core.international.IProperty;
import org.jboss.tools.cdi.seam.core.international.impl.BundleImpl;
import org.jboss.tools.cdi.seam.core.international.impl.LocalizedValue;
import org.jboss.tools.cdi.seam.core.international.impl.PropertyImpl;
import org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine;
import org.jboss.tools.common.el.core.model.ELArgumentInvocation;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInstance;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELModel;
import org.jboss.tools.common.el.core.model.ELObjectType;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.el.core.parser.ELParser;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.parser.LexicalToken;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolutionImpl;
import org.jboss.tools.common.el.core.resolver.ELSegmentImpl;
import org.jboss.tools.common.el.core.resolver.IRelevanceCheck;
import org.jboss.tools.common.el.core.resolver.IVariable;
import org.jboss.tools.common.el.core.resolver.MessagePropertyELSegmentImpl;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.MemberInfo;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.PositionHolder;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.jst.web.kb.IPageContext;
import org.jboss.tools.jst.web.kb.IResourceBundle;
import org.jboss.tools.jst.web.kb.internal.ResourceBundle;

/**
 * 
 * @author Victor V. Rubezhny
 *
 */
public class CDIInternationalMessagesELResolver extends AbstractELCompletionEngine<IVariable> {
	private static final Image CDI_INTERNATIONAL_MESSAGE_PROPOSAL_IMAGE = 
			CDISeamCorePlugin.getDefault().getImage(CDISeamCorePlugin.CA_CDI_MESSAGE_IMAGE_PATH);

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine#getELProposalImage()
	 */
	public Image getELProposalImage() {
		return CDI_INTERNATIONAL_MESSAGE_PROPOSAL_IMAGE;
	}

	private static ELParserFactory factory = ELParserUtil.getDefaultFactory();

	public CDIInternationalMessagesELResolver() {}

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
		CDISeamCorePlugin.getDefault().logError(e);
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
		
		IProject project = context == null ? null :
			context.getResource() == null ? null :
				context.getResource().getProject();
		if (project == null)
			return null;
		
		if (!CDICorePlugin.getCDI(project, true).getExtensionManager().isCDIExtensionAvailable(CDISeamCorePlugin.CDI_INTERNATIONAL_RUNTIME_EXTENTION))
			return null;
		
		IBundleModel bundleModel = BundleModelFactory.getBundleModel(project);
		if (bundleModel == null)
			return null;
		IResourceBundle[] bundles = findResourceBundles(bundleModel);
		if (bundles == null) 
			return null;

		List<TextProposal> proposals = null;
		try {
			 proposals = getCompletions(context.getResource(), null, elString.subSequence(0, elString.length()), position, returnEqualedVariablesOnly, bundles);
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
		IResourceBundle[] bundles = new IResourceBundle[0];
		if(context instanceof IPageContext) {
			IBundleModel bundleModel = BundleModelFactory.getBundleModel(context.getResource().getProject());
			if (bundleModel != null) {
				bundles = findResourceBundles(bundleModel);
			}

		}
		try {
			return resolveELOperand(context.getResource(), operand, returnEqualedVariablesOnly, bundles);
		} catch (StringIndexOutOfBoundsException e) {
			log(e);
		} catch (BadLocationException e) {
			log(e);
		}
		return null;
	}

	public List<TextProposal> getCompletions(IFile file, IDocument document, CharSequence prefix, 
			int position, boolean returnEqualedVariablesOnly, IResourceBundle[] bundles) throws BadLocationException, StringIndexOutOfBoundsException {
		List<TextProposal> completions = new ArrayList<TextProposal>();

		ELResolutionImpl status = resolveELOperand(file, parseOperand("" + prefix), returnEqualedVariablesOnly, bundles); //$NON-NLS-1$
		if(status!=null) {
			completions.addAll(status.getProposals());
		}

		return completions;
	}

	public ELExpression parseOperand(String operand) {
		if(operand == null) return null;
		String el = (operand.indexOf("#{") < 0 && operand.indexOf("${") < 0) ? "#{" + operand + "}" : operand; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ELParser p = getParserFactory().createParser();
		ELModel model = p.parse(el);
		List<ELInstance> is = model.getInstances();
		if(is.isEmpty()) return null;
		return is.get(0).getExpression();
	}

	public ELResolutionImpl resolveELOperand(IFile file,
			ELExpression operand, boolean returnEqualedVariablesOnly, IResourceBundle[] bundles)
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

		List<Variable> resolvedVariables = new ArrayList<Variable>();

		if (expr.getLeft() != null && isArgument) {
			left = expr.getLeft();
			resolvedVariables = resolveVariables(file, left, bundles, false, 
					true); 	// is Final and equal names are because of 
							// we have no more to resolve the parts of expression, 
							// but we have to resolve arguments of probably a message component
			if (resolvedVariables != null && !resolvedVariables.isEmpty()) {
				resolution.setLastResolvedToken(left);
				// Create a combined lexical token to store all the variable name (not only the name before first dot, but all the name including all the words and dots)
				int variableTokenType = left.getFirstToken().getType();
				int variableTokenStart = left.getFirstToken().getStart();
				int variableTokenLength = 0;
				StringBuffer variableTokenText = new StringBuffer();
				LexicalToken current = left.getFirstToken();
				LexicalToken variableTokenNext = null;
				while (current != null && current != left.getLastToken()) {
					variableTokenText.append(current.getText());
					variableTokenLength += current.getLength();
					variableTokenNext = current.getNextToken();
					current = variableTokenNext;
				}
				if (current != null) {
					variableTokenText.append(current.getText());
					variableTokenLength += current.getLength();
					variableTokenNext = current.getNextToken();
				}				
				
				LexicalToken variableToken = new LexicalToken(variableTokenStart, variableTokenLength, variableTokenText, variableTokenType);
				variableToken.setNextToken(variableTokenNext);
				
				ELSegmentImpl segment = new MessagePropertyELSegmentImpl(variableToken);
				processMessageBundleSegment(expr, (MessagePropertyELSegmentImpl)segment, resolvedVariables);

				segment.setResolved(true);
				for (Variable variable : resolvedVariables) {
					segment.getVariables().add(variable);						
				}
				resolution.addSegment(segment);
			}
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
					resolution.setLastResolvedToken(left);

					ELSegmentImpl segment = new MessagePropertyELSegmentImpl(left.getFirstToken());
					processMessageBundleSegment(expr, (MessagePropertyELSegmentImpl)segment, resolvedVariables);
					
					segment.setResolved(true);
					for (Variable variable : resolvedVars) {
						segment.getVariables().add(variable);						
					}
					resolution.addSegment(segment);
					if(left.getLastToken() != left.getFirstToken()) {
						LexicalToken combined = left.getFirstToken().getNextToken().getCombinedToken(left.getLastToken());
						segment = new MessagePropertyELSegmentImpl(combined);
						processMessageBundleSegment(expr, (MessagePropertyELSegmentImpl)segment, resolvedVariables);
						segment.setResolved(true);
						for (Variable variable : resolvedVars) {
							segment.getVariables().add(variable);						
						}
						resolution.addSegment(segment);
					}

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
			resolvedVariables = resolveVariables(file, expr, bundles, true, returnEqualedVariablesOnly);			
			Set<TextProposal> proposals = new TreeSet<TextProposal>(TextProposal.KB_PROPOSAL_ORDER);

			if (left != null) {
				ELSegmentImpl segment = new MessagePropertyELSegmentImpl(left.getFirstToken());
				processMessageBundleSegment(expr, (MessagePropertyELSegmentImpl)segment, resolvedVariables);
				
				segment.setResolved(false);
				resolution.addSegment(segment);
	
				for (Variable var : resolvedVariables) {
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
			}
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

			for (Variable var : resolvedVariables) {
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

	public List<Variable> resolveVariables(IFile file, ELInvocationExpression expr, IResourceBundle[] bundles, boolean isFinal, boolean onlyEqualNames) {
		List<Variable> result = new ArrayList<Variable>();
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

		return result;
	}

	protected void setImage(TextProposal kbProposal) {
		kbProposal.setImage(getELProposalImage());
	}
	
	protected void resolveLastSegment(ELInvocationExpression expr, 
			List<Variable> members,
			ELResolutionImpl resolution,
			boolean returnEqualedVariablesOnly) {
		Set<TextProposal> kbProposals = new TreeSet<TextProposal>(TextProposal.KB_PROPOSAL_ORDER);

		ELSegmentImpl segment = new ELSegmentImpl(expr.getFirstToken());
		resolution.setProposals(kbProposals);
		if(expr instanceof ELPropertyInvocation) {
			segment = new MessagePropertyELSegmentImpl(((ELPropertyInvocation)expr).getName());
			processMessagePropertySegment(expr, (MessagePropertyELSegmentImpl)segment, members);
		} else if (expr instanceof ELArgumentInvocation) {
			segment = new MessagePropertyELSegmentImpl(((ELArgumentInvocation)expr).getArgument().getOpenArgumentToken().getNextToken());
			processMessagePropertySegment(expr, (MessagePropertyELSegmentImpl)segment, members);
		}

		if(segment.getToken()!=null) {
			resolution.addSegment(segment);
		}

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
			for (Variable mbr : members) {
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
	
	private void processMessageBundleSegment(ELInvocationExpression expr, MessagePropertyELSegmentImpl segment, List<Variable> variables) {
		if(segment.getToken() == null)
			return;
		for(Variable variable : variables){
			if(isRelevant(expr, variable)) {
				IBundleModel bundleModel = BundleModelFactory.getBundleModel(variable.f.getProject());
				if(bundleModel == null) return;
				if(bundleModel.getBundle(variable.basename) == null)
					return;
				segment.setBaseName(variable.basename);
				segment.setBundleOnlySegment(true);

				IBundle bundle = bundleModel.getBundle(variable.basename);
				if(bundle == null)
					continue;
				Map<String, XModelObject> os = ((BundleImpl)bundle).getObjects();
				for (XModelObject o: os.values()) {
					segment.addObject(o);
				}
				
			}
		}
	}

	/*
	 * Checks that name of variable is equal to the beginning of expression, which can take more than one token (like a.b.c)
	 */
	private boolean isRelevant(ELInvocationExpression expr, Variable variable) {
		LexicalToken t = expr.getFirstToken();
		StringBuffer sb = new StringBuffer();
		sb.append(t.getText());
		boolean ok = sb.toString().equals(variable.name);
		while(!ok && t != null && t != expr.getLastToken()) {
			t = t.getNextToken();
			sb.append(t.getText());
			ok = sb.toString().equals(variable.name);
		}
		return ok;
	}
	
	private void processMessagePropertySegment(ELInvocationExpression expr, MessagePropertyELSegmentImpl segment, List<Variable> variables){
		if(segment.getToken() == null)
			return;
		for(Variable variable : variables){
			if(isRelevant(expr, variable)) {
				IBundleModel bundleModel = BundleModelFactory.getBundleModel(variable.f.getProject());
				if(bundleModel == null) return;
				IBundle bundle = bundleModel.getBundle(variable.basename);
				if(bundle == null)
					return;
				
				String propertyName = segment.getToken().getText();
				
				IProperty prop = bundle.getProperty(trimQuotes(propertyName));
				if(prop == null) continue;
				Map<String, LocalizedValue> values = ((PropertyImpl)prop).getValues();
				for (LocalizedValue value: values.values()) {
					XModelObject p = value.getObject();
					segment.addObject(p);
					segment.setBaseName(variable.basename);

					PositionHolder h = PositionHolder.getPosition(p, null);
					h.update();
					segment.setMessagePropertySourceReference(h.getStart(), prop.getName().length());

					IFile propFile = (IFile)p.getAdapter(IFile.class);
					if(propFile == null)
						continue;
					segment.setMessageBundleResource(propFile);
				}
			}
		}
	}
	
	public boolean findPropertyLocation(XModelObject property, String content, MessagePropertyELSegmentImpl segment) {
		String name = property.getAttributeValue("name"); //$NON-NLS-1$
		String nvs = property.getAttributeValue("name-value-separator"); //$NON-NLS-1$
		int i = content.indexOf(name + nvs);
		if(i < 0) return false;
		segment.setMessagePropertySourceReference(i, name.length());
		return true;
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

	protected void filterSingularMember(Variable mbr, Set<String> proposalsToFilter) {
		Collection<String> keys = mbr.getKeys();
		for (String key : keys) {
			proposalsToFilter.add(key);
		}
	}

	static class Variable implements IVariable {
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
			IBundleModel bundleModel = BundleModelFactory.getBundleModel(f.getProject());
			if(bundleModel != null) {
				IBundle bundle = bundleModel.getBundle(basename);
				if(bundle != null) {
					result.addAll(bundle.getPropertyNames());
				}
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
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine#resolveVariables(org.eclipse.core.resources.IFile, org.jboss.tools.common.el.core.model.ELInvocationExpression, boolean, boolean)
	 */
	@Override
	public List<IVariable> resolveVariables(IFile file,
			ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames, int offset) {
		return null;
	}

	@Override
	protected boolean isStaticMethodsCollectingEnabled() {
		return false;
	}

	public IRelevanceCheck createRelevanceCheck(IJavaElement element) {
		return new IRelevanceCheck() {
			public boolean isRelevant(String content) {
				return false;
			}
		};
	}

	IResourceBundle[] findResourceBundles (IBundleModel model) {
		Map<String, IResourceBundle> result = new HashMap<String, IResourceBundle>();
		for (String basename : model.getAllAvailableBundles()) {
			String var = "bundles." + basename;
			IResourceBundle resourceBundle = new ResourceBundle(basename, var);
			result.put(var, resourceBundle);
		}
		return result.values().toArray(new IResourceBundle[0]);
	}

}
