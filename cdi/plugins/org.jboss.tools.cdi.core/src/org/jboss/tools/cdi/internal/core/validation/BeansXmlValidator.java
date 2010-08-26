/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.validation;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Alexey Kazakov
 */
public class BeansXmlValidator extends CDICoreValidator {

	public String ENT_CDI_BEANS = "FileCDIBeans"; //$NON-NLS-1$
	public static final String ID = "org.jboss.tools.cdi.core.BeansXmlValidator"; //$NON-NLS-1$

	private IJavaProject javaProject;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#getId()
	 */
	public String getId() {
		return ID;
	}

	@Override
	public void init(IProject project, ContextValidationHelper validationHelper, org.eclipse.wst.validation.internal.provisional.core.IValidator manager,
			IReporter reporter) {
		super.init(project, validationHelper, manager, reporter);
		javaProject = EclipseUtil.getJavaProject(project);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.internal.core.validation.CDICoreValidator#shouldValidate(org.eclipse.core.resources.IProject)
	 */
	@Override
	public boolean shouldValidate(IProject project) {
		return super.shouldValidate(project) && CDIPreferences.shouldValidateBeansXml(project);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.tools.jst.web.kb.validation.IValidator#validate(java.util.Set,
	 * org.eclipse.core.resources.IProject,
	 * org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper,
	 * org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager,
	 * org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validate(Set<IFile> changedFiles, IProject project, ContextValidationHelper validationHelper, ValidatorManager manager, IReporter reporter)
			throws ValidationException {
		init(project, validationHelper, manager, reporter);
		displaySubtask(CDIValidationMessages.SEARCHING_RESOURCES);

		if (cdiProject == null) {
			return OK_STATUS;
		}
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (IFile currentFile : changedFiles) {
			if (reporter.isCancelled()) {
				break;
			}
			// Check if it's a beans.xml
			XModelObject xmo = EclipseResourceUtil.createObjectForResource(currentFile);
			if(xmo != null && xmo.getModelEntity().getName().startsWith(ENT_CDI_BEANS)) {
				validateBeansXml(currentFile);
			}
		}

		return OK_STATUS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.tools.jst.web.kb.validation.IValidator#validateAll(org.eclipse
	 * .core.resources.IProject,
	 * org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper,
	 * org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager,
	 * org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validateAll(IProject project, ContextValidationHelper validationHelper, ValidatorManager manager, IReporter reporter)
			throws ValidationException {
		init(project, validationHelper, manager, reporter);
		if (cdiProject == null) {
			return OK_STATUS;
		}
		displaySubtask(CDIValidationMessages.VALIDATING_PROJECT, new String[] { projectName });
		removeAllMessagesFromResource(cdiProject.getNature().getProject());

		// TODO get all the beans.xml and validate them
		
		return OK_STATUS;
	}

	private void validateBeansXml(IFile beansXml) {
		IModelManager manager = StructuredModelManager.getModelManager();
		if(manager == null) {
			// this may happen if plug-in org.eclipse.wst.sse.core 
			// is stopping or un-installed, that is Eclipse is shutting down.
			// there is no need to report it, just stop validation.
			return;
		}

		IStructuredModel model = null;
		try {
			model = manager.getModelForRead(beansXml);
			if (model instanceof IDOMModel) {
				IDOMModel domModel = (IDOMModel) model;
				IDOMDocument document = domModel.getDocument();

				NodeList list = document.getElementsByTagName("class");
				for (int i = 0; i < list.getLength(); i++) {
					Node classNode = list.item(i);
					NodeList children = classNode.getChildNodes();

					boolean empty = true;
					for (int j = 0; j < children.getLength(); j++) {
						Node node = children.item(j);
						if(node.getNodeType() == Node.TEXT_NODE) {
							String value = node.getNodeValue();
							if(value!=null) {
								String className = value.trim();
								if(className.length()==0) {
									continue;
								}
								empty = false;
								if(node instanceof IndexedRegion) {
									int start = ((IndexedRegion)node).getStartOffset() + value.indexOf(className);
									int length = className.length();
									IType type = EclipseJavaUtil.findType(javaProject, className);
									if(type==null) {
										if(node instanceof IndexedRegion) {
											addError(CDIValidationMessages.CONFLICTING_INTERCEPTOR_BINDINGS, CDIPreferences.CONFLICTING_INTERCEPTOR_BINDINGS,
													new String[]{}, length, start, beansXml);
											break;
										}
									}
								}
							}
						}
					}

					if(empty) {
						if(classNode instanceof IndexedRegion) {
							int start = ((IndexedRegion)classNode).getStartOffset();
							int end = ((IndexedRegion)classNode).getEndOffset();
							int length = end - start;
							addError(CDIValidationMessages.CONFLICTING_INTERCEPTOR_BINDINGS, CDIPreferences.CONFLICTING_INTERCEPTOR_BINDINGS,
									new String[]{}, length, start, beansXml);
						}
					}
				}
			}
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
        } catch (IOException e) {
        	CDICorePlugin.getDefault().logError(e);
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
	}
}