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
package org.jboss.tools.seam.internal.core.validation;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.WorkbenchContext;
import org.eclipse.wst.validation.internal.operations.WorkbenchReporter;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This validator is workaround for bug of WTP 2.0.2
 * See http://jira.jboss.com/jira/browse/JBIDE-2117
 * @author Alexey Kazakov
 */
public class SeamEarProjectValidator implements IValidatorJob {

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidatorJob#getSchedulingRule(org.eclipse.wst.validation.internal.provisional.core.IValidationContext)
	 */
	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidatorJob#validateInJob(org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validateInJob(IValidationContext helper, IReporter reporter)	throws ValidationException {
		IProject project = ((WorkbenchContext)helper).getProject();
		if(!project.isAccessible()) {
			return OK_STATUS;
		}
		WorkbenchReporter.removeAllMessages(project, new String[]{this.getClass().getName()}, null);

		IVirtualComponent component = ComponentCore.createComponent(project);
		IVirtualReference[] rs = component.getReferences();
		for (int i = 0; i < rs.length; i++) {
			IVirtualComponent c = rs[i].getReferencedComponent();
			if(c == null) {
				continue;
			}
			IVirtualFolder folder = c.getRootFolder();
			if(folder==null) {
				continue;
			}
			ISeamProject seamProject = SeamCorePlugin.getSeamProject(folder.getProject(), false);
			if(seamProject!=null) {
				IVirtualFolder earRootFolder = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$
				if(earRootFolder!=null) {
					IFolder f = (IFolder)earRootFolder.getUnderlyingFolder();
					if(f!=null ) {
						validateApplicationXml(f.findMember(new Path("META-INF/application.xml")));
					}
				}
				break;
			}
		}

		return OK_STATUS;
	}

	private static final String MODULE_NODE_NAME = "module";
	private static final String JAVA_NODE_NAME = "java";
	private static final String SEAM_JAR_NAME = "jboss-seam.jar";
	private static final String[] JARS = new String[]{
		"el-ri",
		"jbpm",
		"drools-core",
		"drools-compiler",
		"janino",
		"antlr",
		"commons-jci-core",
		"commons-jci-janino",
		"stringtemplate",
		"jboss-el",
		"jbpm-jpdl",
		"mvel14",
		"richfaces-api"};

	private void validateApplicationXml(IResource applicationXml) {
		if(applicationXml==null || !(applicationXml instanceof IFile) || !applicationXml.exists()) {
			return;
		}
		
		IModelManager manager = StructuredModelManager.getModelManager();
		if(manager == null) {
			return;
		}
		IStructuredModel model = null;		
		try {
			model = manager.getModelForRead((IFile)applicationXml);
			if (model instanceof IDOMModel) {
				IDOMModel domModel = (IDOMModel) model;
				IDOMDocument document = domModel.getDocument();
				Element root = document.getDocumentElement();
				if(root==null) {
					return;
				}
				NodeList children = root.getChildNodes();
				for(int i=0; i<children.getLength(); i++) {
					Node curentValidatedNode = children.item(i);
					if(Node.ELEMENT_NODE == curentValidatedNode.getNodeType() && MODULE_NODE_NAME.equals(curentValidatedNode.getNodeName())) {
						NodeList moduleChildren = curentValidatedNode.getChildNodes();
						for(int j=0; j<moduleChildren.getLength(); j++) {
							Node child = moduleChildren.item(j);
							if(Node.ELEMENT_NODE == child.getNodeType() && JAVA_NODE_NAME.equals(child.getNodeName())) {
								validateJavaModule(applicationXml, child);								
							}
						}
					}
				}
			}
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError(e);
        } catch (IOException e) {
        	SeamCorePlugin.getDefault().logError(e);
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
	}

	private void validateJavaModule(IResource file, Node node) {
		NodeList children = node.getChildNodes();
		for(int i=0; i<children.getLength(); i++) {
			Node child = children.item(i);
			if(Node.TEXT_NODE == child.getNodeType()) {
				IStructuredDocumentRegion strRegion = ((IDOMNode)child).getFirstStructuredDocumentRegion();
				ITextRegionList regions = strRegion.getRegions();
				for(int j=0; j<regions.size(); j++) {
					ITextRegion region = regions.get(j);
					if(region.getType() == DOMRegionContext.XML_CONTENT) {
						String text = strRegion.getFullText(region);
						int offset = strRegion.getStartOffset() + region.getStart();
						validateJarName(file, text, offset);
					}
				}
			}
		}
	}

	private void validateJarName(IResource file, String text, int offset) {
		String jarName = text.trim();
		TextFileDocumentProvider documentProvider = new TextFileDocumentProvider();
		for(int jarIndex=0; jarIndex<JARS.length; jarIndex++) {
			int position = offset + text.indexOf(jarName);
			int length = jarName.length();
			if(SEAM_JAR_NAME.equals(jarName)) {
				ValidationErrorManager.addError(SeamValidationMessages.INVALID_JAR_MODULE_IN_APPLICATION_XML, IMessage.HIGH_SEVERITY, new String[]{jarName}, length, position, file, documentProvider, SeamValidationErrorManager.MARKED_SEAM_PROJECT_MESSAGE_GROUP, this.getClass());
				break;
			}
			if(jarName.startsWith(JARS[jarIndex])) {
				ValidationErrorManager.addError(SeamValidationMessages.INVALID_JAR_MODULE_IN_APPLICATION_XML, IMessage.NORMAL_SEVERITY, new String[]{jarName}, length, position, file, documentProvider, SeamValidationErrorManager.MARKED_SEAM_PROJECT_MESSAGE_GROUP, this.getClass());
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#cleanup(org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public void cleanup(IReporter reporter) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#validate(org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public void validate(IValidationContext helper, IReporter reporter) throws ValidationException {
		validateInJob(helper, reporter);
	}
}