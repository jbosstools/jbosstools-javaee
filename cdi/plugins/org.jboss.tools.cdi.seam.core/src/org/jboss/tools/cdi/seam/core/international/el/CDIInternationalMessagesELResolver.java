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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.seam.core.CDISeamCorePlugin;
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
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolutionImpl;
import org.jboss.tools.common.el.core.resolver.ELSegmentImpl;
import org.jboss.tools.common.el.core.resolver.IRelevanceCheck;
import org.jboss.tools.common.el.core.resolver.IVariable;
import org.jboss.tools.common.el.core.resolver.MessagePropertyELSegmentImpl;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.MemberInfo;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.common.util.FileUtil;
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

	static final CDIOpenKeyHelper keyHelper = new CDIOpenKeyHelper();
	
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
		
		XModelObject modelObject = EclipseResourceUtil.createObjectForResource(project);
		if (modelObject == null)
			return null;
		XModel model = modelObject.getModel();

		IResourceBundle[] bundles = keyHelper.findResourceBundles(model);

		IDocument document = null;

		if (bundles == null) 
			bundles = new IResourceBundle[0];

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
			if(expr.getFirstToken().getText().equals(variable.name)){
				IModelNature n = EclipseResourceUtil.getModelNature(variable.f.getProject());

				XModel model = n == null ? null : n.getModel();
				if(model == null)
					return;
				
				XModelObject[] properties = keyHelper.findBundles(model, variable.basename, null);
				if(properties == null)
					return;

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
				
				IModelNature n = EclipseResourceUtil.getModelNature(variable.f.getProject());
				if(n == null)
					return;
				XModel model = n.getModel();
				if(model == null)
					return;

				XModelObject[] properties = keyHelper.findBundles(model, variable.basename, null);
				if(properties == null)
					return;
				
				for (XModelObject p : properties) {
					IFile propFile = (IFile)p.getAdapter(IFile.class);
					if(propFile == null)
						continue;
					segment.setMessageBundleResource(propFile);
					XModelObject property = p.getChildByPath(trimQuotes(segment.getToken().getText()));
					if(property != null){
						try {
							String content = FileUtil.readStream(propFile);
							if(findPropertyLocation(property, content, segment)){
								segment.setBaseName(variable.basename);
							}
						} catch (CoreException e) {
							log(e);
						}
					}
				}
			}
		}
	}
	
	private String trimQuotes(String value) {
		if(value == null)
			return null;

		if(value.startsWith("'") || value.startsWith("\"")) {  //$NON-NLS-1$ //$NON-NLS-2$
			value = value.substring(1);
		} 
		
		if(value.endsWith("'") || value.endsWith("\"")) { //$NON-NLS-1$ //$NON-NLS-2$
			value = value.substring(0, value.length() - 1);
		}
		return value;
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
			IModelNature n = EclipseResourceUtil.getModelNature(f.getProject());
			if(n == null) return result;
			XModel model = n.getModel();

			List<Object> l = keyHelper.getBundleProperties(model, basename);
			if (l == null) return result;
			
			for (Object o : l) {
				result.add(o.toString());
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

	static class CDIOpenKeyHelper {
		static List<Object> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<Object>());

		IResourceBundle[] findResourceBundles (XModel model) {
			XModelObject[] fs = FileSystemsHelper.getFileSystems(model).getChildren();
			if (fs == null)
				return null;

			Collection<XModelObject> bundles = new HashSet<XModelObject>(); 
			for (XModelObject f : fs) {
				String name = f.getAttributeValue("name");
				if (name == null)
					continue;
				if (name.equals("src") || name.endsWith("lib") ||
						name.startsWith("src-") || name.startsWith("lib-")) {
					bundles.addAll(collectXModelBundleObjects(f));
				}
			}

			if (bundles.isEmpty())
				return null;
			
			 Map<String, IResourceBundle> result = new HashMap<String, IResourceBundle>();
			 for (XModelObject b : bundles) {
				 String path = b.getPath();
				 IPath p = new Path(path);
				 IPath containerPath = p.removeLastSegments(p.segmentCount() - 2);
				 IPath varPath = p.removeFirstSegments(2);
				 
				 String var = varPath.toString();
				 
				 var = var.substring(0, var.lastIndexOf('.'));
				 if (var.indexOf('_') != -1)
					var = var.substring(0, var.indexOf('_'));
				
				String basename = containerPath.append(new Path(var)).toString();
				var = "bundles." + var.replace('/', '.');
				
				if (!result.containsKey(var)) {
					IResourceBundle resourceBundle = new ResourceBundle(basename, var);
					result.put(var, resourceBundle);
				}
			 }

			return result.values().toArray(new IResourceBundle[0]);
		}

		public XModelObject[] findBundles(XModel model, String bundle, String locale) {
			ArrayList<XModelObject> l = new ArrayList<XModelObject>();
			if(locale == null || locale.length() == 0) locale = getDeafultLocale(model);
			while(locale != null && locale.length() > 0) {
				String path = bundle.replace('.', '/') + "_" + locale + ".properties"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				XModelObject o = model.getByPath(path);
				if(o != null) l.add(o);
				int i = locale.lastIndexOf('_');
				if(i < 0) break;
				locale = locale.substring(0, i);
			}
			String path = bundle.replace('.', '/') + ".properties"; //$NON-NLS-1$ //$NON-NLS-2$
			XModelObject o = model.getByPath(path);
			if(o != null) l.add(o);
			return l.toArray(new XModelObject[0]);
		}

		public List<Object> getBundleProperties(XModel model, String bundle) {
			if(bundle == null || bundle.length() == 0) return EMPTY_LIST;
			
			XModelObject[] bundleObjects = findBundles(model, bundle, null);
			if (bundleObjects == null) 
				return EMPTY_LIST;
			Set<String> properties = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			for (XModelObject b : bundleObjects) {
				if(b == null) continue;
				XModelObject[] os = b.getChildren();
				for (int i = 0; i < os.length; i++) {
					properties.add(os[i].getAttributeValue("name"));
				}
			}
			List<Object> list = new ArrayList<Object>();
			list.addAll(properties);
			
			return list;
		}

		public String getDeafultLocale(XModel model) {
			String facesConfigLocale = getDeafultLocaleFromFacesConfig(model);
			if (facesConfigLocale.length() == 0) {
				Locale locale = Locale.getDefault();
				facesConfigLocale = locale == null || locale.toString().length() == 0 ? null : locale.toString();
			}
			return facesConfigLocale;
		}
		
		/**
		 * Gets the default locale from faces config file.
		 * 
		 * TODO: Need Get the Default Locale (From Web Project Root?)
		 * 
		 * @param model XModel
		 * @return locale string or empty string if no locale was found
		 */
		public String getDeafultLocaleFromFacesConfig(XModel model) {
			String facesConfigLocale = ""; //$NON-NLS-1$
			/*
			JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
			WebProjectNode conf = root == null ? null : (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.CONFIGURATION);
			XModelObject[] fs = conf == null ? new XModelObject[0] : conf.getTreeChildren();
			for (int i = 0; i < fs.length; i++) {
				XModelObject o = fs[i].getChildByPath("application/Locale Config"); //$NON-NLS-1$
				String res = (o == null) ? "" : o.getAttributeValue("default-locale"); //$NON-NLS-1$ //$NON-NLS-2$
				if(res != null && res.length() > 0) {
					facesConfigLocale = res;
				}
			}
			*/
			return facesConfigLocale;
		}

		private Collection<XModelObject> collectXModelBundleObjects(XModelObject o) {
			Set<XModelObject> objects = new HashSet<XModelObject>();
			if (o == null) return objects;
			
			String path = o.getPath();
			if (path == null || "META-INF".equalsIgnoreCase(o.getAttributeValue("name")))
				return objects;
			
			if (path.endsWith(".properties")) {
				objects.add(o);
			}

			if (o.hasChildren()) {
				XModelObject[] children = o.getChildren();
				if (children != null) {
					for (XModelObject c : children) {
						objects.addAll(collectXModelBundleObjects(c));
					}
				}
			}
			return objects;
		}
	}
}