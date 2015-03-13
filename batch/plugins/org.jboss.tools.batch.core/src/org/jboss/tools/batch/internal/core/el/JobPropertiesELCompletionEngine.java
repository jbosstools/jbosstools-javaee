/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.internal.core.el;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.internal.core.impl.BatchUtil;
import org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine;
import org.jboss.tools.common.el.core.ca.MessagesELTextProposal;
import org.jboss.tools.common.el.core.model.ELArgumentInvocation;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELObjectType;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolutionImpl;
import org.jboss.tools.common.el.core.resolver.ELResolverExtension;
import org.jboss.tools.common.el.core.resolver.ELSegmentImpl;
import org.jboss.tools.common.el.core.resolver.IOpenableReference;
import org.jboss.tools.common.el.core.resolver.IRelevanceCheck;
import org.jboss.tools.common.el.core.resolver.IVariable;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.MemberInfo;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.common.util.StringUtil;
import org.jboss.tools.common.validation.SkipValidation;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.jst.web.kb.IXmlContext;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.internal.XmlContextImpl;
import org.jboss.tools.jst.web.kb.taglib.INameSpace;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@SkipValidation
public class JobPropertiesELCompletionEngine extends AbstractELCompletionEngine<IVariable> implements ELResolverExtension {
	public static final String PROPERTY_IMAGE_NAME = "property.png"; //$NON-NLS-1$
	private static final ImageDescriptor JOB_PROPERTIES_PROPOSAL_IMAGE = ImageDescriptor.createFromFile(JobPropertiesELCompletionEngine.class, PROPERTY_IMAGE_NAME);

	@Override
	public ImageDescriptor getELProposalImageForMember(MemberInfo memberInfo) {
		return JOB_PROPERTIES_PROPOSAL_IMAGE;
	}

	public JobPropertiesELCompletionEngine() {}

	@Override
	public boolean isRelevant(ELContext context) {
		if(context instanceof IXmlContext) {
			Map<String, List<INameSpace>> namespaces = ((IXmlContext)context).getRootNameSpaces();
			if(namespaces.containsKey(BatchConstants.JAVAEE_NAMESPACE)) {
				for (INameSpace n: namespaces.get(BatchConstants.JAVAEE_NAMESPACE)) {
					if(BatchConstants.TAG_JOB.equals(n.getRoot())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public ELParserFactory getParserFactory() {
		return ELParserUtil.getDefaultFactory();
	}

	@Override
	protected void log(Exception e) {
		BatchCorePlugin.pluginLog().logError(e);
	}

	@Override
	public List<TextProposal> getProposals(ELContext context, String el, int offset) {
		if(!isRelevant(context)) {
			return null;
		}

		currentOffset = offset;
		List<TextProposal> proposals = null;
		try {
			 proposals = getCompletions(context.getResource(), el.subSequence(0, el.length()), false);
		} catch (StringIndexOutOfBoundsException e) {
			log(e);
		} catch (BadLocationException e) {
			log(e);
		}
		return proposals;
	}

	int currentOffset = 0;

	@Override
	public ELResolution resolve(ELContext context, ELExpression operand, int offset) {
		currentOffset = offset;
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

	private List<TextProposal> getCompletions(IFile file, CharSequence prefix, 
			boolean returnEqualedVariablesOnly) throws BadLocationException, StringIndexOutOfBoundsException {
		List<TextProposal> completions = new ArrayList<TextProposal>();

		ELResolutionImpl status = resolveELOperand(file, parseOperand("" + prefix), returnEqualedVariablesOnly); //$NON-NLS-1$
		if(status != null) {
			completions.addAll(status.getProposals());
		}

		return completions;
	}

	protected ELResolutionImpl resolveELOperand(IFile file,
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
			resolvedVariables = resolveVariables(file, left, false, 
					true); 	// is Final and equal names are because of 
							// we have no more to resolve the parts of expression, 
							// but we have to resolve arguments of probably a message component
			if (resolvedVariables != null && !resolvedVariables.isEmpty()) {
				resolution.setLastResolvedToken(left);
	
				ELSegmentImpl segment = new ELSegmentImpl(left.getFirstToken());
				segment.setResolved(true);
				for (IVariable variable : resolvedVariables) {
					segment.getVariables().add(variable);						
				}
				resolution.addSegment(segment);
			}
		} else if (expr.getLeft() == null && isIncomplete) {
			resolvedVariables = resolveVariables(file, expr, true, 
					returnEqualedVariablesOnly);
		} else {
			while(left != null) {
				List<IVariable>resolvedVars = new ArrayList<IVariable>();
				resolvedVars = resolveVariables(file, 
						left, left == expr, 
						returnEqualedVariablesOnly);
				if (resolvedVars != null && !resolvedVars.isEmpty()) {
					resolvedVariables = resolvedVars;
					resolution.setLastResolvedToken(left);

					ELSegmentImpl segment = new JobPropertyELSegmentImpl(left.getFirstToken());
					segment.setResolved(true);
					for (IVariable variable : resolvedVars) {
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
			resolvedVariables = resolveVariables(file, expr, true, returnEqualedVariablesOnly);                     
			Set<TextProposal> proposals = new TreeSet<TextProposal>(TextProposal.KB_PROPOSAL_ORDER);

			if (left != null) {
				ELSegmentImpl segment = new JobPropertyELSegmentImpl(left.getFirstToken());

				segment.setResolved(false);
				resolution.addSegment(segment);

				for (IVariable var : resolvedVariables) {
					String varName = var.getName();
					if(varName.startsWith(operand.getText())) {
						MessagesELTextProposal proposal = new MessagesELTextProposal();
						proposal.setReplacementString(varName.substring(operand.getLength()));
						proposal.setImageDescriptor(getELProposalImageForMember(null));

						List<?> objects = new ArrayList();
						proposal.setBaseName("");
						proposal.setObjects(objects);

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

			for (IVariable var : resolvedVariables) {
				String varName = var.getName();
				if(operand.getLength()<=varName.length()) {
					MessagesELTextProposal proposal = new MessagesELTextProposal();
					proposal.setReplacementString(varName.substring(operand.getLength()));
					proposal.setLabel(varName);
					proposal.setImageDescriptor(getELProposalImageForMember(null));
					List<?> objects = new ArrayList();
					
					proposal.setBaseName("");
					proposal.setObjects(objects);

					proposals.add(proposal);
				} else if(returnEqualedVariablesOnly) {
					TextProposal proposal = new TextProposal();
					proposal.setReplacementString(varName);
					proposal.setLabel(varName);
					proposal.setImageDescriptor(getELProposalImageForMember(null));
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
					resolveLastSegment(file, (ELInvocationExpression)operand, resolvedVariables, resolution, returnEqualedVariablesOnly);
					break;
				}
			}
		} else {
			ELSegmentImpl segment = new ELSegmentImpl(expr.getFirstToken());
			resolution.addSegment(segment);
		}

		return resolution;
	}

	protected List<IVariable> resolveVariables(IFile file, ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames) {
		List<IVariable> result = new ArrayList<IVariable>();
		if(expr.getLeft() != null) return result;
		String varName = expr.toString();
		for (IVariable v: getAllVariables()) {
			if(!isFinal || onlyEqualNames) {
				if(!v.getName().equals(varName)) {
					continue;
				}
			}
			if(!v.getName().startsWith(varName)) {
				continue;
			}
			result.add(v);
		}
		return result;
	}

	protected IVariable[] getAllVariables() {
		return new IVariable[]{JOB_PROPERTIES, JOB_PARAMETERS, SYSTEM_PROPERTIES, PARTITION_PLAN};
	}

	protected void resolveLastSegment(IFile file, ELInvocationExpression expr, 
			List<IVariable> members,
			ELResolutionImpl resolution,
			boolean returnEqualedVariablesOnly) {

		ELSegmentImpl segment = new ELSegmentImpl(expr.getFirstToken());
		if(expr instanceof ELPropertyInvocation) {
			segment = new JobPropertyELSegmentImpl(((ELPropertyInvocation)expr).getName());
			processJobPropertySegment(file, expr, (JobPropertyELSegmentImpl)segment, members);
		} else if (expr instanceof ELArgumentInvocation) {
			segment = new JobPropertyELSegmentImpl(((ELArgumentInvocation)expr).getArgument().getOpenArgumentToken().getNextToken());
			processJobPropertySegment(file, expr, (JobPropertyELSegmentImpl)segment, members);
		}

		if(segment.getToken()!=null) {
			resolution.addSegment(segment);
		}

		addTextProposals(file, expr, members, resolution, segment, returnEqualedVariablesOnly);

		if (resolution.isResolved()){
			resolution.setLastResolvedToken(expr);
		}
	}

	protected void addTextProposals(IFile file, ELInvocationExpression expr, List<IVariable> members, ELResolutionImpl resolution, ELSegmentImpl segment, boolean returnEqualedVariablesOnly) {
		Set<TextProposal> kbProposals = new TreeSet<TextProposal>(TextProposal.KB_PROPOSAL_ORDER);
		resolution.setProposals(kbProposals);

		if (expr.getType() == ELObjectType.EL_PROPERTY_INVOCATION && ((ELPropertyInvocation)expr).getName() == null) {
			//Batch job does not support property invocation with dot
		} else if(expr.getType() != ELObjectType.EL_ARGUMENT_INVOCATION) {
			//Batch job substitution does not property invocation with dot
		} else if(expr.getType() == ELObjectType.EL_ARGUMENT_INVOCATION) {
			String filter = expr.getMemberName() == null ? "" : expr.getMemberName();
			filter = StringUtil.trimQuotes(filter);

			for (IVariable mbr : members) {
				if(mbr != JOB_PROPERTIES) {
					continue;
				}
				List<OpenableReference> properties = getProperties(file, currentOffset);
				for (OpenableReference p : properties) {
					String key = p.getValue();
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
	}
	
	private void processJobPropertySegment(IFile file, ELInvocationExpression expr, JobPropertyELSegmentImpl segment, List<IVariable> variables){
		if(segment.getToken() == null)
			return;
		for(IVariable variable : variables){
			if(variable != JOB_PROPERTIES) {
				continue;
			}
			if(expr.getFirstToken().getText().equals(variable.getName())){
				List<OpenableReference> properties = getProperties(file, currentOffset);
				List<OpenableReference> result = new ArrayList<OpenableReference>();
				for (OpenableReference r: properties) {
					if(r.getValue().equals(StringUtil.trimQuotes(segment.getToken().getText()))) {
						result.add(r);
					}
				}				
				segment.setResource(file);
				segment.setAttrs(result);
			}
		}
	}
	
	private List<OpenableReference> getProperties(final IFile file, int offset) {
		final List<OpenableReference> result = new ArrayList<OpenableReference>();
		ELContext c = PageContextFactory.createPageContext(file);
		if(!(c instanceof XmlContextImpl)) return result;
		XmlContextImpl context = (XmlContextImpl)c;
		final IDocument idocument = context.getDocument();
		if(idocument == null) return result;
		
		BatchUtil.scanXMLFile(file, new BatchUtil.DocumentScanner() {			
			@Override
			public void scanDocument(Document document) {
				Node n = findNodeForOffset((IDOMDocument)document, currentOffset);
				if(n instanceof Attr) {
					fillProperties(idocument, file, result, (Element)n.getParentNode());
				} else if(n instanceof Element) {
					fillProperties(idocument, file, result, (Element)n);
				}
			}
		});
		return result;
	}

	void fillProperties(IDocument document, IFile file, List<OpenableReference> result, Element element) {
		Attr exclude = null;
		if(element.getNodeName().equals(BatchConstants.TAG_PROPERTY)) {
			exclude = element.getAttributeNode(BatchConstants.ATTR_NAME);
			element = (Element)element.getParentNode();
		}
		if(element.getNodeName().equals(BatchConstants.TAG_PROPERTIES)) {
			element = (Element)element.getParentNode();
		}
		while(element != null) {
			Element psn = element;
			if(!element.getNodeName().equals(BatchConstants.TAG_PROPERTIES)) {
				psn = XMLUtilities.getUniqueChild(element, BatchConstants.TAG_PROPERTIES);
			}
			if(psn != null) {
				Element[] ps =  XMLUtilities.getChildren(psn, BatchConstants.TAG_PROPERTY);
				for (Element p: ps) {
					Attr a = p.getAttributeNode(BatchConstants.ATTR_NAME);
					if(a != exclude && a instanceof IDOMAttr) {
						result.add(new OpenableReference(document, file, (IDOMAttr)a));
					}
				}
			}
			if(element.getParentNode() instanceof Element) {
				element = (Element)element.getParentNode();
			} else {
				element = null;
			}
		}
	}

	static public Node findNodeForOffset(IDOMNode node, int offset) {
		IndexedRegion region = node.getModel().getIndexedRegion(offset);
		if(region instanceof Node){
			return (Node)region;
		}
		return null;
	}	

	private MessagesELTextProposal createProposal(IVariable mbr, String proposal) {
		MessagesELTextProposal kbProposal = new MessagesELTextProposal();
		if (proposal.indexOf('.') != -1) {
			kbProposal.setReplacementString('\'' + proposal + '\'');
			kbProposal.setLabel("['" + proposal + "']");
		} else {
			kbProposal.setReplacementString(proposal);
			kbProposal.setLabel(proposal);
		}
		kbProposal.setAlternateMatch(proposal);
		kbProposal.setImageDescriptor(getELProposalImageForMember(null));
			
		List<?> objects = new ArrayList(); //TODO do another implementation
		
		kbProposal.setBaseName("");
		kbProposal.setPropertyName(proposal);
		kbProposal.setObjects(objects);

		return kbProposal;
	}

	static IVariable JOB_PROPERTIES = new Variable(BatchConstants.JOB_PROPERTIES_OPERATOR);
	static IVariable JOB_PARAMETERS = new Variable(BatchConstants.JOB_PARAMETERS_OPERATOR);
	static IVariable SYSTEM_PROPERTIES = new Variable(BatchConstants.SYSTEM_PROPERTIES_OPERATOR);
	static IVariable PARTITION_PLAN = new Variable(BatchConstants.PARTITION_PLAN_OPERATOR);

	static class Variable implements IVariable {
		String name;
		Variable(String name) {
			this.name = name;
		}
		@Override
		public String getName() {
			return name;
		}
	}

	public static final String GO_TO_PROPERTY_AT = "Property {0} at {1}:{2}";

	static class OpenableReference implements IOpenableReference {
		IFile file;
		int start;
		int length;
		String value;
		String label;
		
		OpenableReference(IDocument document, IFile file, IDOMAttr attr) {
			this.file = file;
			this.start = attr.getValueRegionStartOffset();
			this.length = attr.getValueRegionText().length();
			value = attr.getValue();
			int line = 0;
			int pos = 0;
			try {
				line = document.getLineOfOffset(start);
				pos = start - document.getLineOffset(line);
			} catch (BadLocationException e) {
				BatchCorePlugin.pluginLog().logError(e);
			}
			label = NLS.bind(GO_TO_PROPERTY_AT, new Object[]{value, "" + line, "" + pos});
		}

		@Override
		public boolean open() {
			try {
				IEditorPart part = IDE.openEditor(BatchCorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage(), file);
				StructuredTextEditor text = (StructuredTextEditor)part.getAdapter(ITextEditor.class);
				text.selectAndReveal(start, length);
			} catch (PartInitException e) {
				BatchCorePlugin.pluginLog().logError(e);
			}
			return false;
		}

		@Override
		public String getLabel() {
			return label;
		}

		public String getValue() {
			return value;
		}

		@Override
		public Image getImage() {
			return null;
		}
		
	}

	@Override
	protected MemberInfo getMemberInfoByVariable(IVariable var, ELContext context,
			boolean onlyEqualNames, int offset) {
		return null;
	}

	@Override
	public List<IVariable> resolveVariables(IFile file, ELContext context,
			ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames, int offset) {
		return null;
	}

	@Override
	protected boolean isStaticMethodsCollectingEnabled() {
		return false;
	}

	@Override
	public IRelevanceCheck createRelevanceCheck(IJavaElement element) {
		return IRRELEVANT;
	}

}