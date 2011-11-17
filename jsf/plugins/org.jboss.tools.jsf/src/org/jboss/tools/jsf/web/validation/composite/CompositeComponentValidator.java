/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jsf.web.validation.composite;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.validation.ContextValidationHelper;
import org.jboss.tools.common.validation.IProjectValidationContext;
import org.jboss.tools.common.validation.IValidatingProjectTree;
import org.jboss.tools.common.validation.ValidatorManager;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.web.validation.JSFSeverityPreferences;
import org.jboss.tools.jsf.web.validation.JSFValidationMessage;
import org.jboss.tools.jst.web.kb.IPageContext;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.internal.KbBuilder;
import org.jboss.tools.jst.web.kb.internal.validation.KBValidator;
import org.jboss.tools.jst.web.kb.internal.validation.WebValidator;
import org.jboss.tools.jst.web.kb.taglib.IComponent;
import org.jboss.tools.jst.web.kb.taglib.ICompositeTagLibrary;
import org.jboss.tools.jst.web.kb.taglib.ITagLibrary;
import org.jboss.tools.jst.web.kb.taglib.TagLibraryManager;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * JSF 2 composite component validator.
 * 
 * @author Alexey Kazakov
 */
public class CompositeComponentValidator extends WebValidator {

	public static final String ID = "org.jboss.tools.jsf.CompositeComponentValidator"; //$NON-NLS-1$
	public static final String PROBLEM_TYPE = "org.jboss.tools.jsf.compositeproblem"; //$NON-NLS-1$
	public static final String SHORT_ID = "jboss.jsf.core"; //$NON-NLS-1$
	public static final String PREFERENCE_PAGE_ID = "org.jboss.tools.jsf.ui.preferences.JSFValidationPreferencePage"; //$NON-NLS-1$

	private static final String COMPOSITE_COMPONENT_URI_PREFIX = "http://java.sun.com/jsf/composite/"; //$NON-NLS-1$
	
	public static final String MESSAGE_ID_ATTRIBUTE_NAME = "JSF2_message_id"; //$NON-NLS-1$
	
	public static final int UNKNOWN_COMPOSITE_COMPONENT_NAME_ID = 1;
	public static final int UNKNOWN_COMPOSITE_COMPONENT_ATTRIBUTE_ID = 2;

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#validate(java.util.Set, org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validate(Set<IFile> changedFiles, IProject project, ContextValidationHelper validationHelper, IProjectValidationContext validationContext, ValidatorManager manager, IReporter reporter)	throws ValidationException {
		init(project, validationHelper, validationContext, manager, reporter);
		displaySubtask(JSFValidationMessage.SEARCHING_RESOURCES, new String[]{project.getName()});

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		Set<IFile> filesToValidate = new HashSet<IFile>();
		Set<IPath> pathesToClean = new HashSet<IPath>();
		for (IFile file : changedFiles) {
			pathesToClean.add(file.getFullPath());
			// If the changed file is a composition component then collect all the pages which use this component.
			if(notValidatedYet(file)) {
				if(file.exists()) {
					filesToValidate.add(file);
					ITagLibrary[] libs = TagLibraryManager.getLibraries(file.getParent());
					for (ITagLibrary lib : libs) {
						if(lib instanceof ICompositeTagLibrary) {
							collectRelatedPages(root, filesToValidate, pathesToClean, getComponentUri(lib.getURI(), file));
						}
					}
				} else {
					// In case of deleted resource file
					IContainer folder = file.getParent();
					if(folder!=null) {
						String[] segemnts = folder.getFullPath().segments();
						StringBuilder libUri = new StringBuilder();
						for (String segment : segemnts) {
							if(libUri.length()==0) {
								if(segment.equalsIgnoreCase("resources")) {
									libUri.append("http://java.sun.com/jsf/composite");
								}
							} else {
								libUri.append('/').append(segment);
							}
						}
						if(libUri.length()>"http://java.sun.com/jsf/composite".length()) {
							collectRelatedPages(root, filesToValidate, pathesToClean, getComponentUri(libUri.toString(), file));
						}
					}
				}
			}
		}

		// Remove all links between collected resources because they will be
		// linked again during validation.
		getValidationContext().removeLinkedCoreResources(SHORT_ID, pathesToClean);

		for (IFile file : filesToValidate) {
			validateResource(file);
		}

		return OK_STATUS;
	}

	private void collectRelatedPages(IWorkspaceRoot root, Set<IFile> filesToValidate, Set<IPath> pathesToClean, String uri) {
		Set<IPath> pathes = getValidationContext().getCoreResourcesByVariableName(SHORT_ID, uri, false);
		if(pathes!=null) {
			for (IPath path : pathes) {
				IFile page = root.getFile(path);
				if(page!=null && page.isAccessible()) {
					filesToValidate.add(page);
					pathesToClean.add(page.getFullPath());
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#validateAll(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validateAll(IProject project, ContextValidationHelper validationHelper, IProjectValidationContext validationContext, ValidatorManager manager, IReporter reporter) throws ValidationException {
		init(project, validationHelper, validationContext, manager, reporter);
		displaySubtask(JSFValidationMessage.VALIDATING_PROJECT, new String[]{project.getName()});

		Set<IFile> files = validationHelper.getProjectSetRegisteredFiles();
		Set<IFile> filesToValidate = new HashSet<IFile>();
		for (IFile file : files) {
			if(file.isAccessible()) {
				if(notValidatedYet(file)) {
					filesToValidate.add(file);
				}
			}
		}
		for (IFile file : filesToValidate) {
			validateResource(file);
		}

		return OK_STATUS;
	}

	private String getComponentUri(String libUri, IFile file) {
		String fullName = file.getName();
		String name = fullName;
		String ext = file.getFileExtension();
		if(ext!=null) {
			name = name.substring(0, name.lastIndexOf("." + ext));
		}
		return libUri + ":" + name;
	}

	private void validateResource(IFile file) {
		if(shouldFileBeValidated(file)) {
			displaySubtask(JSFValidationMessage.VALIDATING_RESOURCE, new String[]{file.getProject().getName(), file.getName()});
			removeAllMessagesFromResource(file);
			coreHelper.getValidationContextManager().addValidatedProject(this, file.getProject());
			ELContext context = PageContextFactory.createPageContext(file);
			if(context!=null && context instanceof IPageContext) {
				IPageContext pageContext = (IPageContext)context;
				Set<String> uris = pageContext.getURIs();
				for (String uri : uris) {
					// Validate pages which use http://java.sun.com/jsf/composite/* name spaces only.
					if(uri.startsWith(COMPOSITE_COMPONENT_URI_PREFIX)) {
						IModelManager manager = StructuredModelManager.getModelManager();
						if (manager != null) {
							IStructuredModel model = null;
							try {
								model = manager.getModelForRead(file);
								if (model instanceof IDOMModel) {
									IDOMModel domModel = (IDOMModel) model;
									IDOMDocument document = domModel.getDocument();
									validateNode(file, document.getDocumentElement());
								}
							} catch (CoreException e) {
								JSFModelPlugin.getPluginLog().logError(e);
							} catch (IOException e) {
								JSFModelPlugin.getPluginLog().logError(e);
							} finally {
								if (model != null) {
									model.releaseFromRead();
								}
							}
						}
						break;
					}
				}
			}
		}
	}

	private void validateNode(IFile file, Node node) {
		if (node instanceof Element) {
			String namespaceURI = node.getNamespaceURI();
			if (namespaceURI != null && namespaceURI.startsWith(COMPOSITE_COMPONENT_URI_PREFIX)) {
				validateComponent(file, node);
			}
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				validateNode(file, children.item(i));
			}
		}
	}

	private void validateComponent(IFile file, Node xmlComponent) {
		String tagName = xmlComponent.getLocalName();
		if(tagName==null) {
			return;
		}
		String tagLibUri = xmlComponent.getNamespaceURI();
		// Save the link between the composition component URI and the validating page 
		getValidationContext().addLinkedCoreResource(SHORT_ID, tagLibUri + ":" + tagName, file.getFullPath(), false);

		ITagLibrary[] libs = KbProjectFactory.getKbProject(file.getProject(), true).getTagLibraries(tagLibUri);
		if(libs.length>0) {
			IComponent kbComponent = libs[0].getComponent(tagName);
			if(kbComponent!=null) {
				NamedNodeMap map = xmlComponent.getAttributes();
				for (int i = 0; i < map.getLength(); i++) {
					Node xmlAttribute = map.item(i);
					String attributeName = xmlAttribute.getNodeName();
					if(!"id".equals(attributeName) && kbComponent.getAttribute(attributeName)==null && xmlAttribute instanceof IndexedRegion) {
						// Mark unknown attribute name
						IndexedRegion region = (IndexedRegion)xmlAttribute;
						int offset = region.getStartOffset();
						int length = attributeName.length();
						addError(JSFValidationMessage.UNKNOWN_COMPOSITE_COMPONENT_ATTRIBUTE, JSFSeverityPreferences.UNKNOWN_COMPOSITE_COMPONENT_ATTRIBUTE, new String[]{attributeName, tagName}, length, offset, file, UNKNOWN_COMPOSITE_COMPONENT_ATTRIBUTE_ID);
					}
				}
			} else {
				addError(file, xmlComponent, tagName);
			}
		} else {
			addError(file, xmlComponent, tagName);
		}
	}

	/**
	 * Mark unknown tag name
	 * @param file
	 * @param xmlComponent
	 * @param tagName
	 */
	private void addError(IFile file, Node xmlComponent, String tagName) {
		if(xmlComponent instanceof IndexedRegion) {
			IndexedRegion region = (IndexedRegion)xmlComponent;
			int offset = region.getStartOffset();
			int length = xmlComponent.getNodeName().length() + 1;
			addError(JSFValidationMessage.UNKNOWN_COMPOSITE_COMPONENT_NAME, JSFSeverityPreferences.UNKNOWN_COMPOSITE_COMPONENT_NAME, new String[]{tagName}, length, offset, file, UNKNOWN_COMPOSITE_COMPONENT_NAME_ID);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#getId()
	 */
	public String getId() {
		return ID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#getBuilderId()
	 */
	public String getBuilderId() {
		return KbBuilder.BUILDER_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#getValidatingProjects(org.eclipse.core.resources.IProject)
	 */
	public IValidatingProjectTree getValidatingProjects(IProject project) {
		return createSimpleValidatingProjectTree(project);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#shouldValidate(org.eclipse.core.resources.IProject)
	 */
	public boolean shouldValidate(IProject project) {
		try {
			return project != null 
					&& project.isAccessible() 
					&& project.hasNature(JSFNature.NATURE_ID) 
					&& validateBuilderOrder(project)
					&& isEnabled(project);
		} catch (CoreException e) {
			JSFModelPlugin.getDefault().logError(e);
		}
		return false;
	}

	private boolean validateBuilderOrder(IProject project) throws CoreException {
		return KBValidator.validateBuilderOrder(project, getBuilderId(), getId(), JSFSeverityPreferences.getInstance());
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#isEnabled(org.eclipse.core.resources.IProject)
	 */
	public boolean isEnabled(IProject project) {
		return JSFSeverityPreferences.isValidationEnabled(project);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getPreference(org.eclipse.core.resources.IProject, java.lang.String)
	 */
	protected String getPreference(IProject project, String preferenceKey) {
		return JSFSeverityPreferences.getInstance().getProjectPreference(project, preferenceKey);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getMaxNumberOfMarkersPerFile(org.eclipse.core.resources.IProject)
	 */
	public int getMaxNumberOfMarkersPerFile(IProject project) {
		return JSFSeverityPreferences.getMaxNumberOfProblemMarkersPerFile(project);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getMarkerType()
	 */
	public String getMarkerType() {
		return PROBLEM_TYPE;
	}
	
	public IMarker addError(String message, String preferenceKey,
			String[] messageArguments, int length, int offset, IResource target, int messageId) {
		IMarker marker = addError(message, preferenceKey, messageArguments, length, offset, target);
		try {
			if(marker!=null) {
				marker.setAttribute(MESSAGE_ID_ATTRIBUTE_NAME, new Integer(messageId));
			}
		} catch(CoreException e) {
			JSFModelPlugin.getDefault().logError(e);
		}
		return marker;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.WebValidator#shouldValidateJavaSources()
	 */
	@Override
	protected boolean shouldValidateJavaSources() {
		return false;
	}

	@Override
	protected String getPreferencePageId() {
		return PREFERENCE_PAGE_ID;
	}
}