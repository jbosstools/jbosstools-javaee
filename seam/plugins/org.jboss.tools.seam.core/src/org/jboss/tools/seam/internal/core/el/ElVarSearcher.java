 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.el;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class helps to find var/value attributes in DOM tree.   
 * @author Alexey Kazakov
 */
public class ElVarSearcher {

	private final static String VAR_ATTRIBUTE_NAME = "var";
	private final static String VALUE_ATTRIBUTE_NAME = "value";

	private ISeamProject project;
	private IFile file;
	private SeamELCompletionEngine engine;

	/**
	 * Constructor.
	 * @param project Seam project where we will look for vars.
	 * @param file File where we will look for vars.
	 * @param engine Competion Engine that we will use for resolving vars.
	 */
	public ElVarSearcher(ISeamProject project, IFile file, SeamELCompletionEngine engine) {
		this.project = project;
		this.file = file;
		this.engine = engine;
	}

	/**
	 * Constructor.
	 * @param project Seam project where we will look for vars.
	 * @param engine Competion Engine that we will use for resolving vars.
	 */
	public ElVarSearcher(ISeamProject project, SeamELCompletionEngine engine) {
		this(project, null, engine);
	}

	/**
	 * @param file File where we will look for vars.
	 */
	public void setFile(IFile file) {
		this.file = file;
	}

	/**
	 * @param viewer
	 * @param offset
	 * @return
	 */
	public static Node getNode(ITextViewer viewer, int offset) {
		IndexedRegion treeNode = ContentAssistUtils.getNodeAt(viewer, offset);
		if(treeNode instanceof Node) {
			return (Node)treeNode;
		}
		return null;
	}

	/**
	 * @param node
	 * @return All var/value that can be used in this position and null if can't find anyone.
	 */
	public static List<Var> findAllVars(ITextViewer viewer, int offset) {
		Node node = getNode(viewer, offset);
		if(node!=null) {
			return findAllVars(node);
		}
		return null;
	}

	/**
	 * @param node
	 * @return All var/value that can be used in node and null if can't find anyone.
	 */
	public static List<Var> findAllVars(Node node) {
		ArrayList<Var> vars = null;
		Node parentNode = node.getParentNode();
		while(parentNode!=null) {
			Var var = findVar(parentNode);
			if(var!=null) {
				if(vars == null) {
					vars = new ArrayList<Var>();
				}
				vars.add(0, var);
			}
			parentNode = parentNode.getParentNode();
		}
		return vars;
	}

	/**
	 * Finds var/value attribute in node
	 * @param node
	 * @param vars
	 * @return found var/value or null
	 */
	public static Var findVar(Node node) {
		if(node!=null && Node.ELEMENT_NODE == node.getNodeType()) {
			Element element = (Element)node;
			String var = element.getAttribute(VAR_ATTRIBUTE_NAME);
			if(var!=null) {
				var = var.trim();
				if(!"".equals(var)) {
					String value = element.getAttribute(VALUE_ATTRIBUTE_NAME);
					if(value!=null) {
						value = value.trim();
						Var newVar = new Var(var, value);
						if(newVar.getElToken()!=null) {
							return newVar;
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Finds var in list of vars that is used in given EL.
	 * @param el EL without brackets.
	 * @param vars
	 * @param initializeNestedVars
	 * @return
	 */
	public Var findVarForEl(String el, List<Var> vars, boolean initializeNestedVars) {
		if(vars!=null) {
			ArrayList<Var> parentVars = new ArrayList<Var>();
			for (Var var : vars) {
				ELToken token = var.getElToken();
				if(token!=null && !token.getText().endsWith(".")) {
					String varName = var.getName();
					if(el.equals(varName) || el.startsWith(varName + ".")) {
						if(var.getElToken()!=null && initializeNestedVars) {
							Var parentVar = findVarForEl(var.getElToken().getText(), parentVars, true);
							if(parentVar!=null) {
								ELToken resolvedToken = parentVar.getResolvedElToken();
								if(resolvedToken==null && parentVar.getElToken()!=null) {
									try {
										// Initialize parent vars.
										engine.resolveSeamELOperand(project, file, var.getElToken().getText(), var.getElToken().getText(), 0, true, parentVars, this);
										resolvedToken = parentVar.getResolvedElToken();
									} catch (StringIndexOutOfBoundsException e) {
										SeamCorePlugin.getPluginLog().logError(e);
									} catch (BadLocationException e) {
										SeamCorePlugin.getPluginLog().logError(e);
									}
								}
								if(resolvedToken!=null) {
									String oldText = var.getElToken().getText();
									String newValue = "#{" + resolvedToken.getText() + oldText.substring(parentVar.getName().length()) + "}";
									var.value = newValue;
									var.elToken = var.parseEl(newValue);
								}
							}
						}
						return var;
					}
				}
				parentVars.add(var);
			}
		}
		return null;
	}

	/**
	 * Represents "var"/"value" attributes.
	 * @author Alexey Kazakov
	 */
	public static class Var {
		String name;
		String value;
		ELToken elToken;
		String resolvedValue;
		ELToken resolvedElToken;

		/**
		 * Constructor
		 * @param name - value of "var" attribute. 
		 * @param value - value of "value" attribute.
		 */
		public Var(String name, String value) {
			super();
			this.name = name;
			this.value = value;
			elToken = parseEl(value);
		}

		private ELToken parseEl(String el) {
			if(el.length()>3 && el.startsWith("#{") && el.endsWith("}")) {
				String elBody = el.substring(0, el.length()-1).substring(2);
				SeamELTokenizer elTokenizer = new SeamELTokenizer(elBody);
				List<ELToken> tokens = elTokenizer.getTokens();
				for (ELToken token : tokens) {
					if(token.getType()==ELToken.EL_VARIABLE_TOKEN) {
						return token;
					}
				}
			}
			return null;
		}

		/**
		 * Sets value to new resolved EL which we got as result of parsing value.
		 * For example:
		 * <h:datatable value="#{list}" var="item">
		 * 	<h:dataTable value="#{item.anotherList}" var="innerItem">
		 * 		...
		 * 	</h:dataTable>
		 * </h:dataTable>
		 * Original El is #{item.anotherList}
		 * Resolved El is #{list.iterator().next().anotherList}
		 * It's very useful for nested vars.
		 * @param newEl
		 */
		public void resolveValue(String newEl) {
			resolvedValue = newEl;
			resolvedElToken = parseEl(newEl);
		}

		/**
		 * @return parsed EL from "value" attribute. Returns null if EL is not valid.
		 */
		public ELToken getElToken() {
			return elToken;
		}

		/**
		 * @return parsed resolved EL from "value" attribute. May be null.
		 */
		public ELToken getResolvedElToken() {
			return resolvedElToken;
		}

		/**
		 * @return name of variable. 
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return value of variable. It's EL.
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @return resolved value of variable. It's EL. May be null.
		 */
		public String getResolvedValue() {
			return resolvedValue;
		}
	}
}