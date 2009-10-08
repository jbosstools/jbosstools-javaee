/*******************************************************************************
 * Copyright (c) 2008 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.text.ext.hyperlink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jboss.tools.common.el.core.model.ELArgumentInvocation;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.resolver.ElVarSearcher;
import org.jboss.tools.common.el.core.resolver.Var;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.IHyperLinkPartitionPriority;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkPartitionRecognizer;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamMessages;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;
import org.jboss.tools.seam.text.ext.SeamExtPlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * @author Jeremy
 */
public class SeamBeanHyperlinkPartitioner extends AbstractHyperlinkPartitioner implements IHyperlinkPartitionRecognizer, IHyperLinkPartitionPriority { 
	public static final String SEAM_BEAN_PARTITION = "org.jboss.tools.seam.text.ext.SEAM_BEAN";
	public static final String SEAM_MESSAGES_BEAN_PARTITION = "org.jboss.tools.seam.text.ext.SEAM_MESSAGES_BEAN";

	private ELParserFactory factory = ELParserUtil.getJbossFactory();

	/**
	 * @see com.ibm.sse.editor.hyperlink.AbstractHyperlinkPartitioner#parse(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	protected IHyperlinkRegion parse(IDocument document, IHyperlinkRegion superRegion) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Utils.findNodeForOffset(xmlDocument, superRegion.getOffset());
			if (!recognize(document, superRegion)) return null;
			IHyperlinkRegion r = getRegion(document, superRegion.getOffset());
			if (r == null) return null;
			r = getWordRegion(document, superRegion.getOffset());
			if (r == null) return null;
			
			Map<String, ISeamMessages> messages = findMessagesComponents(document, superRegion);

			if (messages != null && !messages.isEmpty()) {
				String axis = getAxis(document, superRegion);
				String contentType = superRegion.getContentType();
				String type = SEAM_MESSAGES_BEAN_PARTITION;
				
				int length = r.getLength() - (superRegion.getOffset() - r.getOffset());
				int offset = superRegion.getOffset();
				
				IHyperlinkRegion region = new HyperlinkRegion(offset, length, axis, contentType, type);
				return region;
			}
			
			List<IJavaElement> javaElements = findJavaElements(document, superRegion);
			if (javaElements != null && !javaElements.isEmpty()) {///
				String axis = getAxis(document, superRegion);
				String contentType = superRegion.getContentType();
				String type = SEAM_BEAN_PARTITION;
				int length = r.getLength() - (superRegion.getOffset() - r.getOffset());
				int offset = superRegion.getOffset();
				
				IHyperlinkRegion region = new HyperlinkRegion(offset, length, axis, contentType, type);
				return region;
			}
		
			return null;
		} finally {
			smw.dispose();
		}
	}
	
	

	protected String getAxis(IDocument document, IHyperlinkRegion superRegion) {
		if (superRegion.getAxis() == null || superRegion.getAxis().length() == 0) {
			return JSPRootHyperlinkPartitioner.computeAxis(document, superRegion.getOffset()) + "/";
		}
		return superRegion.getAxis();
	}
	
	public static IHyperlinkRegion getWordRegion (IDocument document, final int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof Attr || n instanceof Text)) return null;

			int start = Utils.getValueStart(n);
			int end = Utils.getValueEnd(n);
			if(start < 0 || start > end || start > offset) return null;
			String attrText = document.get(start, end - start);

			StringBuffer sb = new StringBuffer(attrText);
			//find start of bean property
			int bStart = offset - start;
			while (bStart >= 0) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bStart))) {
					bStart++;
					break;
				}
			
				if (bStart == 0) break;
				bStart--;
			}
			// find end of bean property
			int bEnd = offset - start;
			while (bEnd < sb.length()) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bEnd)))
					break;
				bEnd++;
			}
			
			int propStart = bStart + start;
			int propLength = bEnd - bStart;
			
			if (propStart > offset || propStart + propLength < offset) return null;
			
			IHyperlinkRegion region = new HyperlinkRegion(propStart, propLength, null, null, null);
			return region;
		} catch (BadLocationException x) {
			SeamExtPlugin.getPluginLog().logError(x);
			return null;
		} finally {
			smw.dispose();
		}
	}

	public static IHyperlinkRegion getRegionPart(IDocument document, final int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof Attr || n instanceof Text)) return null;

			int start = Utils.getValueStart(n);
			int end = Utils.getValueEnd(n);
			if(start < 0 || start > end || start > offset) return null;
			String attrText = document.get(start, end - start);

			StringBuffer sb = new StringBuffer(attrText);
			//find start of bean property
			int bStart = offset - start;
			while (bStart >= 0) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bStart)) &&
						sb.charAt(bStart) != '.' && sb.charAt(bStart) != '[' && sb.charAt(bStart) != ']') {
					bStart++;
					break;
				}
			
				if (bStart == 0) break;
				bStart--;
			}
			// find end of bean property
			int bEnd = offset - start;
			while (bEnd < sb.length()) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bEnd)))
					break;
				bEnd++;
			}
			
			int propStart = bStart + start;
			int propLength = bEnd - bStart;
			
			if (propStart > offset || propStart + propLength < offset) return null;
			
			IHyperlinkRegion region = new HyperlinkRegion(propStart, propLength, null, null, null);
			return region;
		} catch (BadLocationException x) {
			SeamExtPlugin.getPluginLog().logError(x);
			return null;
		} finally {
			smw.dispose();
		}
	}
	public static IHyperlinkRegion getRegion(IDocument document, final int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof Attr || n instanceof Text)) return null;

			int start = 0;
			int end = document.getLength();
			if(n instanceof IDOMNode) {
				start = ((IDOMNode)n).getStartOffset();
				end = ((IDOMNode)n).getEndOffset();
			}

			//TODO do we have and need seam project here?
			SeamELCompletionEngine engine = new SeamELCompletionEngine();
			ELInvocationExpression tokens = engine.findExpressionAtOffset(document, offset, start, end);
			if (tokens == null /*|| tokens.size() == 0*/)
				return null; // No EL Operand found

			int propStart = tokens.getStartPosition();
			int propLength = tokens.getEndPosition() - propStart; 
			
			if (propStart > offset || propStart + propLength < offset) return null;
			
			IHyperlinkRegion region = new HyperlinkRegion(propStart, propLength);
			return region;
		} finally {
			smw.dispose();
		}
	}
	
	/**
	 * @see com.ibm.sse.editor.extensions.hyperlink.IHyperlinkPartitionRecognizer#recognize(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	public boolean recognize(IDocument document, IHyperlinkRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return false;
			
			Utils.findNodeForOffset(xmlDocument, region.getOffset());

			Map<String, ISeamMessages> messages = findMessagesComponents(document, region);
			if (messages != null && !messages.isEmpty()) {
				return true;
			}

			List<IJavaElement> javaElements = findJavaElements(document, region);
			if (javaElements != null && !javaElements.isEmpty()) {
				return true;
			}
			
			return false;
		} finally {
			smw.dispose();
		}
	}

	public static List<IJavaElement> findJavaElements(IDocument document, IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			IHyperlinkRegion r = getRegion(document, region.getOffset());
			if (r == null) return null;

			
			String propText = document.get(r.getOffset(), r.getLength());
			
			
			IFile file = smw.getFile();
			IProject project = (file == null ? null : file.getProject());

			ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
			if (seamProject == null)
				return null;

			SeamELCompletionEngine engine = new SeamELCompletionEngine();

			String prefix = propText;
			ELExpression expr = engine.parseOperand(prefix);
			if (!(expr instanceof ELInvocationExpression))
				return null; // No EL Operand found
			expr.getModel().shift(r.getOffset() - expr.getFirstToken().getStart());
			
			List<IJavaElement> javaElements = null;
			try {
				javaElements = engine.getJavaElementsForELOperandTokens(seamProject, file, (ELInvocationExpression)expr);
			} catch (StringIndexOutOfBoundsException e) {
				SeamExtPlugin.getPluginLog().logError(e);
				return null;
			} catch (BadLocationException e) {
				SeamExtPlugin.getPluginLog().logError(e);
				return null;
			}

			//Do not need it, vars handled in getJavaElementsForELOperandTokens
			if (javaElements == null || javaElements.isEmpty()) {
				// Try to find a local Var (a pair of variable-value attributes)
				ElVarSearcher varSearcher = new ElVarSearcher(file, engine);
				// Find a Var in the EL 
				int start = expr.getStartPosition();
				int end = expr.getEndPosition();
				
				if (expr.getText().length() == 0)
					return null;
				
				List<Var> allVars= varSearcher.findAllVars(file, start);
				Var var = varSearcher.findVarForEl(expr.getText(), allVars, true);
				if (var == null) {
					// Find a Var in the current offset assuming that it's a node with var/value attribute pair
					var = varSearcher.findVar(file, start);
				}
				if (var == null)
					return null;

				String resolvedValue = var.getValue();
				if (resolvedValue == null || resolvedValue.length() == 0) 
						return null;
				if (resolvedValue.startsWith("#{") || resolvedValue.startsWith("${"))
					resolvedValue = resolvedValue.substring(2);
				if (resolvedValue.endsWith("}"))
					resolvedValue = resolvedValue.substring(0, resolvedValue.lastIndexOf("}"));
				
				// Replace the Var with its resolved value in tokens (Var is always the first token)
				StringBuffer elText = new StringBuffer();
				elText.append(resolvedValue);
				String app = expr.getText();
				int i = app.indexOf('.');
				app = app.substring(i + 1);
				elText.append('.').append(app);
				
				javaElements = engine.getJavaElementsForExpression(
						seamProject, file, elText.toString());
			}
			return javaElements;
		} catch (BadLocationException x) {
			SeamExtPlugin.getPluginLog().logError(x);
			return null;
		} finally {
			smw.dispose();
		}
	}
	
	public static Map<String, ISeamMessages> findMessagesComponents(IDocument document, IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			IHyperlinkRegion r = getRegion(document, region.getOffset());
			if (r == null) return null;
			
			String propText = document.get(r.getOffset(), r.getLength());
			
			
			IFile file = smw.getFile();
			IProject project = (file == null ? null : file.getProject());

			ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
			if (seamProject == null)
				return null;

			SeamELCompletionEngine engine= new SeamELCompletionEngine();

			String prefix = propText;
			ELExpression exp = engine.parseOperand(prefix);
			if (exp == null)
				return null; // No EL Operand found
			
			Map<ELInvocationExpression, List<ISeamContextVariable>> map = new HashMap<ELInvocationExpression, List<ISeamContextVariable>>();

			exp.getModel().shift(r.getOffset() - exp.getFirstToken().getStart());

			if (	!(exp instanceof ELInvocationExpression) &&
					!(exp instanceof ELPropertyInvocation) && 
					!(exp instanceof ELArgumentInvocation))
				return null;
			
			String propertyName = null;
			if (exp instanceof ELPropertyInvocation) {
				propertyName = ((ELPropertyInvocation)exp).getMemberName();
			} else if (exp instanceof ELArgumentInvocation) {
				propertyName = Utils.trimQuotes(
						((ELArgumentInvocation)exp).getArgument().getArgument().getText());
			}
			
			if (propertyName == null)
				return null;
			
			ScopeType scope = SeamELCompletionEngine.getScope(seamProject, file);

			ELInvocationExpression expr = (ELInvocationExpression)exp;
			
			ELInvocationExpression left = expr;

			if (expr.getLeft() != null) {
				while (left != null) {
					List<ISeamContextVariable> resolvedVars = new ArrayList<ISeamContextVariable>();
					resolvedVars = engine.resolveVariables(seamProject, scope, left,
							left == expr, true);
					if (resolvedVars != null && !resolvedVars.isEmpty()) {
						map.put(left, resolvedVars);
						break;
					}
					left = (ELInvocationExpression) left.getLeft();
				}
			}

			// At the moment map contains the resolved EL parts mapped to their vars
			// And now we need to extract SeamXmlFactory vars to the real ones 
			// and filter out all non-SeamMessagesComponent vars
			// Next we need to map the SeamMessagesComponent vars to properties
			Map<String, ISeamMessages> messages = new HashMap<String, ISeamMessages>();
			if (map != null && !map.isEmpty()) {
				for (ELInvocationExpression l : map.keySet()) {
					List<ISeamContextVariable> variables = map.get(l);
					for (ISeamContextVariable variable : variables) {
						ISeamMessages messagesVariable = SeamELCompletionEngine.getSeamMessagesComponentVariable(variable);
						if (messagesVariable != null) {
							messages.put(propertyName, messagesVariable);
						}
					}
				}
			}

			return messages;
		} catch (BadLocationException x) {
			SeamExtPlugin.getPluginLog().logError(x);
			return null;
		} finally {
			smw.dispose();
		}
	}

	public static IHyperlinkRegion getMessagesPropertyRegion(IDocument document, int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			IHyperlinkRegion r = getRegion(document, offset);
			if (r == null) return null;
			
			String propText = document.get(r.getOffset(), r.getLength());
			
			IFile file = smw.getFile();
			IProject project = (file == null ? null : file.getProject());

			ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
			if (seamProject == null)
				return null;

			SeamELCompletionEngine engine= new SeamELCompletionEngine();

			String prefix = propText;
			ELExpression exp = engine.parseOperand(prefix);
			if (exp == null)
				return null; // No EL Operand found
			
			Map<ELInvocationExpression, List<ISeamContextVariable>> map = new HashMap<ELInvocationExpression, List<ISeamContextVariable>>();

			exp.getModel().shift(r.getOffset() - exp.getFirstToken().getStart());

			if (	!(exp instanceof ELInvocationExpression) &&
					!(exp instanceof ELPropertyInvocation) && 
					!(exp instanceof ELArgumentInvocation))
				return null;
			
			String propertyName = null;
			if (exp instanceof ELPropertyInvocation) {
				propertyName = ((ELPropertyInvocation)exp).getMemberName();
				
				int start = ((ELPropertyInvocation)exp).getLastToken().getStart();
				int length = ((ELPropertyInvocation)exp).getLastToken().getLength();
				
				return new HyperlinkRegion(start, length); 
			} else if (exp instanceof ELArgumentInvocation) {
				propertyName = Utils.trimQuotes(
						((ELArgumentInvocation)exp).getArgument().getArgument().getText());
				int start = ((ELArgumentInvocation)exp).getArgument().getArgument().getStartPosition();
				int length = ((ELArgumentInvocation)exp).getArgument().getArgument().getEndPosition() - 
						((ELArgumentInvocation)exp).getArgument().getArgument().getStartPosition();
				
				return new HyperlinkRegion(start, length); 
			}
			
			return null;
		} catch (BadLocationException x) {
			SeamExtPlugin.getPluginLog().logError(x);
			return null;
		} finally {
			smw.dispose();
		}
	}

	public int getPriority() {
		return 0; // to be first
	}
	
}