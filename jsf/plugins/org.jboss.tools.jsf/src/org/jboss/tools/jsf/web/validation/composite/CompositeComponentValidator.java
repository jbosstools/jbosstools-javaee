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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.web.validation.JSFSeverityPreferences;
import org.jboss.tools.jsf.web.validation.JSFValidationMessage;
import org.jboss.tools.jst.web.kb.IPageContext;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.internal.KbBuilder;
import org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper;
import org.jboss.tools.jst.web.kb.internal.validation.KBValidator;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;
import org.jboss.tools.jst.web.kb.taglib.IComponent;
import org.jboss.tools.jst.web.kb.taglib.ICompositeTagLibrary;
import org.jboss.tools.jst.web.kb.taglib.ITagLibrary;
import org.jboss.tools.jst.web.kb.taglib.TagLibraryManager;
import org.jboss.tools.jst.web.kb.validation.IProjectValidationContext;
import org.jboss.tools.jst.web.kb.validation.IValidatingProjectTree;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * JSF 2 composite component validator.
 * 
 * @author Alexey Kazakov
 */
public class CompositeComponentValidator extends KBValidator {

	public static final String ID = "org.jboss.tools.jsf.CompositeComponentValidator"; //$NON-NLS-1$
	public static final String PROBLEM_TYPE = "org.jboss.tools.jsf.compositeproblem"; //$NON-NLS-1$
	public static final String SHORT_ID = "jboss.jsf.core"; //$NON-NLS-1$

	private static final String COMPOSITE_COMPONENT_URI_PREFIX = "http://java.sun.com/jsf/composite/"; //$NON-NLS-1$

	private IProject currentProject;
	private IContainer webRootFolder;

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#init(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.eclipse.wst.validation.internal.provisional.core.IValidator, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	@Override
	public void init(IProject project, ContextValidationHelper validationHelper, IProjectValidationContext context, org.eclipse.wst.validation.internal.provisional.core.IValidator manager, IReporter reporter) {
		super.init(project, validationHelper, context, manager, reporter);
		currentProject = null;
		webRootFolder = null;
	}

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
			if(file.isAccessible() && notValidatedYet(file)) {
				filesToValidate.add(file);
				ITagLibrary[] libs = TagLibraryManager.getLibraries(file.getParent());
				for (ITagLibrary lib : libs) {
					if(lib instanceof ICompositeTagLibrary) {
						Set<IPath> pathes = getValidationContext().getCoreResourcesByVariableName(SHORT_ID, getComponentUri(lib, file), false);
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

	private String getComponentUri(ITagLibrary lib, IFile file) {
		String fullName = file.getName();
		String name = fullName;
		String ext = file.getFileExtension();
		if(ext!=null) {
			name = name.substring(0, name.lastIndexOf("." + ext));
		}
		return lib.getURI() + ":" + name;
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
		ITagLibrary[] libs = KbProjectFactory.getKbProject(file.getProject(), true).getTagLibraries(xmlComponent.getNamespaceURI());
		if(libs.length>0) {
			IComponent kbComponent = libs[0].getComponent(tagName);
			if(kbComponent!=null) {
				// Save the link between the composition component URI and the validating page 
				getValidationContext().addLinkedCoreResource(SHORT_ID, libs[0].getURI() + ":" + kbComponent.getName(), file.getFullPath(), false);

				NamedNodeMap map = xmlComponent.getAttributes();
				for (int i = 0; i < map.getLength(); i++) {
					Node xmlAttribute = map.item(i);
					String attributeName = xmlAttribute.getNodeName();
					if(!"id".equals(attributeName) && kbComponent.getAttribute(attributeName)==null && xmlAttribute instanceof IndexedRegion) {
						// Mark unknown attribute name
						IndexedRegion region = (IndexedRegion)xmlAttribute;
						int offset = region.getStartOffset();
						int length = attributeName.length();
						addError(JSFValidationMessage.UNKNOWN_COMPOSITE_COMPONENT_ATTRIBUTE, JSFSeverityPreferences.UNKNOWN_COMPOSITE_COMPONENT_ATTRIBUTE, new String[]{attributeName, tagName}, length, offset, file);
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
			addError(JSFValidationMessage.UNKNOWN_COMPOSITE_COMPONENT_NAME, JSFSeverityPreferences.UNKNOWN_COMPOSITE_COMPONENT_NAME, new String[]{tagName}, length, offset, file);
		}
	}

	private boolean enabled = true;

	private boolean shouldFileBeValidated(IFile file) {
		if(!file.isAccessible()) {
			return false;
		}
		IProject project = file.getProject();
		if(!file.isSynchronized(IResource.DEPTH_ZERO)) {
			// The resource is out of sync with the file system
			// Just ignore this resource.
			return false;
		}
		if(!project.equals(currentProject)) {
			currentProject = project;
			enabled = isEnabled(project);	
			if(!enabled) {
				return false;
			}
			if(webRootFolder!=null && !project.equals(webRootFolder.getProject())) {
				webRootFolder = null;
			}
			if(webRootFolder==null) {
				IFacetedProject facetedProject = null;
				try {
					facetedProject = ProjectFacetsManager.create(project);
				} catch (CoreException e) {
					JSFModelPlugin.getDefault().logError(e);
				}
				if(facetedProject!=null && facetedProject.getProjectFacetVersion(IJ2EEFacetConstants.DYNAMIC_WEB_FACET)!=null) {
					IVirtualComponent component = ComponentCore.createComponent(project);
					if(component!=null) {
						IVirtualFolder webRootVirtFolder = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$
						webRootFolder = webRootVirtFolder.getUnderlyingFolder();
					}
				}
			}
			currentProject = project;
		}

		// Validate files from Web-Content only (in case of WTP project)
		return enabled && webRootFolder!=null && webRootFolder.getLocation().isPrefixOf(file.getLocation()) && PageContextFactory.isPage(file);
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
		return ValidatorManager.validateBuilderOrder(project, getBuilderId(), getId(), JSFSeverityPreferences.getInstance());
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
}