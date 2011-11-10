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
package org.jboss.tools.jsf.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine;
import org.jboss.tools.common.el.core.ca.MessagesELTextProposal;
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
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolutionImpl;
import org.jboss.tools.common.el.core.resolver.ELSegmentImpl;
import org.jboss.tools.common.el.core.resolver.IRelevanceCheck;
import org.jboss.tools.common.el.core.resolver.IVariable;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.MemberInfo;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.PositionHolder;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.util.StringUtil;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.model.helpers.converter.OpenKeyHelper;
import org.jboss.tools.jst.web.kb.IPageContext;
import org.jboss.tools.jst.web.kb.IResourceBundle;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.el.MessagePropertyELSegmentImpl;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JSFMessageELCompletionEngine extends AbstractELCompletionEngine<IVariable> {
	private static final Image JSF_EL_MESSAGES_PROPOSAL_IMAGE = JSFModelPlugin.getDefault().getImage(JSFModelPlugin.CA_JSF_MESSAGES_IMAGE_PATH);

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine#getELProposalImageForMember(org.jboss.tools.common.el.core.resolver.TypeInfoCollector.MemberInfo)
	 */
	@Override
	public Image getELProposalImageForMember(MemberInfo memberInfo) {
		return JSF_EL_MESSAGES_PROPOSAL_IMAGE;
	}

	private static ELParserFactory factory = ELParserUtil.getDefaultFactory();

	public JSFMessageELCompletionEngine() {}

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
		currentOffset = offset;
		return getCompletions(el, false, 0, context);
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

	int currentOffset = 0;

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.resolver.ELResolver2#resolve(org.jboss.tools.common.el.core.resolver.ELContext, org.jboss.tools.common.el.core.model.ELExpression)
	 */
	public ELResolution resolve(ELContext context, ELExpression operand, int offset) {
		currentOffset = offset;
		ELResolutionImpl resolution = resolveELOperand(operand, context, true);
		if(resolution != null)
			resolution.setContext(context);
		return resolution;
	}

	public ELResolutionImpl resolveELOperand(ELExpression operand,
			ELContext context, boolean returnEqualedVariablesOnly) {
		IResourceBundle[] bundles = new IResourceBundle[0];
		if(context instanceof IPageContext) {
			IPageContext pageContext = (IPageContext)context;
			bundles = pageContext.getResourceBundles();
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
	
				ELSegmentImpl segment = new MessagePropertyELSegmentImpl(left.getFirstToken());
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
						MessagesELTextProposal proposal = new MessagesELTextProposal();
						proposal.setReplacementString(varName.substring(operand.getLength()));
						proposal.setImage(getELProposalImageForMember(null));

						List<XModelObject> objects = new ArrayList<XModelObject>();

						IModelNature n = EclipseResourceUtil.getModelNature(var.f.getProject());
						XModel model = n != null ? n.getModel() : null;
						if(model != null) {
							OpenKeyHelper keyHelper = new OpenKeyHelper();
							XModelObject[] properties = keyHelper.findBundles(model, var.basename, null);
							if(properties == null) continue;
							for (XModelObject p : properties) {
								objects.add(p);
	//							IFile propFile = (IFile)p.getAdapter(IFile.class);
	//							if(propFile == null)
	//								continue;
							}
						}
						
						proposal.setBaseName(var.basename);
						proposal.setObjects(objects);
//						if (!lastSegment.isBundle()) {
//							proposal.setPropertyName(lastSegment.getToken().getText());
//						}

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

			for (Variable var : resolvedVariables) {
				String varName = var.getName();
				if(operand.getLength()<=varName.length()) {
					MessagesELTextProposal proposal = new MessagesELTextProposal();
					proposal.setReplacementString(varName.substring(operand.getLength()));
					proposal.setLabel(varName);
					proposal.setImage(getELProposalImageForMember(null));
					List<XModelObject> objects = new ArrayList<XModelObject>();
					IModelNature n = EclipseResourceUtil.getModelNature(var.f.getProject());
					XModel model = n != null ? n.getModel() : null;
					if(model != null) {
						OpenKeyHelper keyHelper = new OpenKeyHelper();
						XModelObject[] properties = keyHelper.findBundles(model, var.basename, null);
						if(properties == null) continue;
						for (XModelObject p : properties) {
							objects.add(p);
//							IFile propFile = (IFile)p.getAdapter(IFile.class);
//							if(propFile == null)
//								continue;
						}
					}
					
					proposal.setBaseName(var.basename);
					proposal.setObjects(objects);

//						if (!lastSegment.isBundle()) {
//							proposal.setPropertyName(lastSegment.getToken().getText());
//						}
					
					proposals.add(proposal);
				} else if(returnEqualedVariablesOnly) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(varName);
					proposal.setLabel(varName);
					proposal.setImage(getELProposalImageForMember(null));
					proposals.add(proposal);
				}
				resolution.getLastSegment().getVariables().add(var);
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
				if(!name.startsWith(varName)) continue;
				Variable v = new Variable(name, basename, file);
				result.add(v);
			}
		}

		return result;
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
		} else if(expr.getType() != ELObjectType.EL_ARGUMENT_INVOCATION) {
			String filter = (expr.getMemberName() == null ? "" : expr.getMemberName());

			for (Variable mbr : members) {
				Collection<String> keys = mbr.getKeys();
				for (String key : keys) {
					// We do expect nothing but name for method tokens (No round brackets)
					if(returnEqualedVariablesOnly) {
						// This is used for validation.
						if (key.equals(filter)) {
							MessagesELTextProposal kbProposal = createProposal(mbr, key);
							kbProposals.add(kbProposal);
							break;
						}
					} else if (key.startsWith(filter)) {
						// This is used for CA.
						MessagesELTextProposal kbProposal = createProposal(mbr, key);
						if (key.indexOf('.') == -1)	kbProposal.setReplacementString(key.substring(filter.length()));
						else kbProposal.setReplacementString('[' + kbProposal.getReplacementString());
						kbProposals.add(kbProposal);
					}
				}
			}
		} else if(expr.getType() == ELObjectType.EL_ARGUMENT_INVOCATION) {
			String filter = expr.getMemberName() == null ? "" : expr.getMemberName();
			boolean b = filter.startsWith("'") || filter.startsWith("\""); //$NON-NLS-1$ //$NON-NLS-2$
			filter = StringUtil.trimQuotes(filter);
			
			for (Variable mbr : members) {
				if (!b && filter.length() > 0) {
					//Value is set as expression itself, we cannot compute it
					resolution.setMapOrCollectionOrBundleAmoungTheTokens(true);
					return;
				}

				Collection<String> keys = mbr.getKeys();
				for (String key : keys) {
					if(returnEqualedVariablesOnly) {
						// This is used for validation.
						if (key.equals(filter)) {
							MessagesELTextProposal kbProposal = createProposal(mbr, key);
							kbProposals.add(kbProposal);
							break;
						}
					} else if (key.startsWith(filter)) {
						// This is used for CA.
						MessagesELTextProposal kbProposal = createProposal(mbr, key);
						
						String existingString = expr.getMemberName() == null ? "" : expr.getMemberName();
						// Because we're in argument invocation we should fix the proposal by surrounding it with quotes as needed
						String replacement = kbProposal.getReplacementString();
						String label = kbProposal.getLabel();
						if (!replacement.startsWith("'")) {
							replacement = '\'' + key + '\'';
							label = "['" + key + "']";
						}
						replacement = replacement.substring(existingString.length());
						
						kbProposal.setReplacementString(replacement);
						kbProposal.setLabel(label);

						kbProposals.add(kbProposal);
					}
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
			if(expr.getFirstToken().getText().equals(variable.name)){
				
				IModelNature n = EclipseResourceUtil.getModelNature(variable.f.getProject());
				if(n == null)
					return;
				XModel model = n.getModel();
				if(model == null)
					return;
				
				OpenKeyHelper keyHelper = new OpenKeyHelper();
				XModelObject[] properties = keyHelper.findBundles(model, variable.basename, null);
				if(properties == null)
					return;
				
				for (XModelObject p : properties) {
					segment.addObject(p);
					IFile propFile = (IFile)p.getAdapter(IFile.class);
					if(propFile == null)
						continue;
					segment.setMessageBundleResource(propFile);
					
				}
				
				segment.setBaseName(variable.basename);
				segment.setBundleOnlySegment(true);
			}
		}
	}
	
	private void processMessagePropertySegment(ELInvocationExpression expr, MessagePropertyELSegmentImpl segment, List<Variable> variables){
		if(segment.getToken() == null)
			return;
		for(Variable variable : variables){
			if(expr.getFirstToken().getText().equals(variable.name)){
				int offset = currentOffset;
				String locale = getPageLocale(variable.f, offset);
				
				IModelNature n = EclipseResourceUtil.getModelNature(variable.f.getProject());
				if(n == null)
					return;
				XModel model = n.getModel();
				if(model == null)
					return;
				
				OpenKeyHelper keyHelper = new OpenKeyHelper();
				XModelObject[] properties = keyHelper.findBundles(model, variable.basename, locale);
				if(properties == null)
					return;
				
				for (XModelObject p : properties) {
					String name = segment.getToken().getText();
					XModelObject property = p.getChildByPath(StringUtil.trimQuotes(name));
					if(property == null) continue;
					segment.addObject(property);

					PositionHolder h = PositionHolder.getPosition(p, null);
					h.update();
					segment.setMessagePropertySourceReference(h.getStart(), name.length());

					IFile propFile = (IFile)p.getAdapter(IFile.class);
					if(propFile != null)
						segment.setMessageBundleResource(propFile);
				}
				segment.setBaseName(variable.basename);
				segment.setBundleOnlySegment(false);
			}
		}
	}
	
	private static final String VIEW_TAGNAME = "view"; //$NON-NLS-1$
	private static final String LOCALE_ATTRNAME = "locale"; //$NON-NLS-1$
	private static final String PREFIX_SEPARATOR = ":"; //$NON-NLS-1$

	private String getPageLocale(IFile file, int offset) {
		ELContext c = PageContextFactory.createPageContext(file);
		if(!(c instanceof IPageContext)) return "";
		IPageContext context = (IPageContext)c;
		IDocument document = context.getDocument();
		if(document == null) return "";

		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);
			if (!(n instanceof Attr) ) return null; 

			Element el = ((Attr)n).getOwnerElement();
			
			Element jsfCoreViewTag = null;
			String nodeToFind = PREFIX_SEPARATOR + VIEW_TAGNAME; 
	
			while (el != null) {
				if (el.getNodeName() != null && el.getNodeName().endsWith(nodeToFind)) {
					jsfCoreViewTag = el;
					break;
				}
				Node parent = el.getParentNode();
				el = (parent instanceof Element ? (Element)parent : null); 
			}
			
			if (jsfCoreViewTag == null || !jsfCoreViewTag.hasAttribute(LOCALE_ATTRNAME)) return null;
			
			String locale = Utils.trimQuotes((jsfCoreViewTag.getAttributeNode(LOCALE_ATTRNAME)).getValue());
			if (locale == null || locale.length() == 0) return null;
			return locale;
		} finally {
			smw.dispose();
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
	
	private MessagesELTextProposal createProposal(Variable mbr, String proposal) {
		MessagesELTextProposal kbProposal = new MessagesELTextProposal();
		if (proposal.indexOf('.') != -1) {
			kbProposal.setReplacementString('\'' + proposal + '\'');
			kbProposal.setLabel("['" + proposal + "']");
		} else {
			kbProposal.setReplacementString(proposal);
			kbProposal.setLabel(proposal);
		}
		kbProposal.setAlternateMatch(proposal);
		kbProposal.setImage(getELProposalImageForMember(null));
			
		List<XModelObject> objects = new ArrayList<XModelObject>();
		String locale = getPageLocale(mbr.f, currentOffset);
		IModelNature n = EclipseResourceUtil.getModelNature(mbr.f.getProject());
		XModel model = n != null ? n.getModel() : null;
		if(model != null) {
			OpenKeyHelper keyHelper = new OpenKeyHelper();
			XModelObject[] properties = keyHelper.findBundles(model, mbr.basename, locale);
			
			if(properties != null) {
				for (XModelObject p : properties) {
					XModelObject property = p.getChildByPath(proposal);
					if(property != null) objects.add(property);
				}
			}
		}
		
		kbProposal.setBaseName(mbr.basename);
		kbProposal.setPropertyName(proposal);
		kbProposal.setObjects(objects);

		return kbProposal;
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

			MessagesELTextProposal proposal = createProposal(mbr, key); 
			if (key.indexOf('.') != -1) {
				proposal.setReplacementString("['" + key + "']"); //$NON-NLS-1$ //$NON-NLS-2$
				proposal.setLabel("['" + key + "']");
			} else {
				proposal.setReplacementString(key);
				proposal.setLabel(key);
			}
			kbProposals.add(proposal);
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
		return IRRELEVANT;
	}

}