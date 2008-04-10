/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.text.ext.hyperlink;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.IHyperLinkPartitionPriority;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkPartitionRecognizer;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.el.ELOperandToken;
import org.jboss.tools.seam.internal.core.el.ELToken;
import org.jboss.tools.seam.internal.core.el.ElVarSearcher;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;
import org.jboss.tools.seam.internal.core.el.ElVarSearcher.Var;
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

	private final SeamELCompletionEngine fEngine= new SeamELCompletionEngine();

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

			String axis = getAxis(document, superRegion);
			String contentType = superRegion.getContentType();
			String type = SEAM_BEAN_PARTITION;
			int length = r.getLength() - (superRegion.getOffset() - r.getOffset());
			int offset = superRegion.getOffset();
			
			IHyperlinkRegion region = new HyperlinkRegion(offset, length, axis, contentType, type);
			return region;
		} catch (Exception x) {
			SeamExtPlugin.getPluginLog().logError(x);
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
		} catch (Exception x) {
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
		} catch (Exception x) {
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
			
			List<ELOperandToken> tokens = SeamELCompletionEngine.findTokensAtOffset(document, offset);
			if (tokens == null || tokens.size() == 0)
				return null; // No EL Operand found

			int propStart = tokens.get(0).getStart();
			int propLength = tokens.get(tokens.size() - 1).getStart() + tokens.get(tokens.size() - 1).getLength() - propStart; 
			
			if (propStart > offset || propStart + propLength < offset) return null;
			
			IHyperlinkRegion region = new HyperlinkRegion(propStart, propLength);
			return region;
		} catch (Exception x) {
			SeamExtPlugin.getPluginLog().logError(x);
			return null;
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

			List<IJavaElement> javaElements = findJavaElements(document, region);

			return (javaElements != null && javaElements.size() > 0);
		} catch (Exception x) {
			SeamExtPlugin.getPluginLog().logError(x);
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

			SeamELCompletionEngine engine= new SeamELCompletionEngine();

			List<ELOperandToken> tokens = SeamELCompletionEngine.findTokensAtOffset(document, r.getOffset() + r.getLength());
			if (tokens == null)
				return null; // No EL Operand found

			List<IJavaElement> javaElements = null;
			try {
				javaElements = engine.getJavaElementsForELOperandTokens(seamProject, file, tokens);
			} catch (StringIndexOutOfBoundsException e) {
				SeamExtPlugin.getPluginLog().logError(e);
				return null;
			} catch (BadLocationException e) {
				SeamExtPlugin.getPluginLog().logError(e);
				return null;
			}

			if (javaElements == null || javaElements.size() == 0) {
				// Try to find a local Var (a pair of variable-value attributes)
				ElVarSearcher varSearcher = new ElVarSearcher(seamProject, file, new SeamELCompletionEngine());
				// Find a Var in the EL 
				int start = tokens.get(0).getStart();
				int end = tokens.get(tokens.size() - 1).getStart() + 
								tokens.get(tokens.size() - 1).getLength();
				
				StringBuffer elText = new StringBuffer();
				for (ELOperandToken token : tokens) {
					if (token.getType() == ELOperandToken.EL_VARIABLE_NAME_TOKEN ||
							token.getType() == ELOperandToken.EL_PROPERTY_NAME_TOKEN ||
							token.getType() == ELOperandToken.EL_METHOD_TOKEN ||
							token.getType() == ELOperandToken.EL_SEPARATOR_TOKEN) {
						elText.append(token.getText());
					}
				}

				if (elText.length() == 0)
					return null;
				
				List<Var> allVars= ElVarSearcher.findAllVars(file, start);
				Var var = varSearcher.findVarForEl(elText.toString(), allVars, true);
				if (var == null) {
					// Find a Var in the current offset assuming that it's a node with var/value attribute pair
					var = ElVarSearcher.findVar(file, tokens.get(0).getStart());
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
				elText = new StringBuffer();
				elText.append(resolvedValue);
				for (int i = 1; i < tokens.size(); i++) {
					ELOperandToken token = tokens.get(i);
					if (token.getType() == ELOperandToken.EL_VARIABLE_NAME_TOKEN ||
							token.getType() == ELOperandToken.EL_PROPERTY_NAME_TOKEN ||
							token.getType() == ELOperandToken.EL_METHOD_TOKEN ||
							token.getType() == ELOperandToken.EL_SEPARATOR_TOKEN) {
						elText.append(token.getText());
					}
				}
				
				javaElements = engine.getJavaElementsForExpression(
						seamProject, file, elText.toString());
			}
			return javaElements;
		} catch (Exception x) {
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