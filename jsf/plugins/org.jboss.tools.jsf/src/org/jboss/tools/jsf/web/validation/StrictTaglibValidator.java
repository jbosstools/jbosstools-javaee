/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.web.validation;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.validation.ContextValidationHelper;
import org.jboss.tools.common.validation.IProjectValidationContext;
import org.jboss.tools.common.validation.IValidatingProjectTree;
import org.jboss.tools.common.validation.PreferenceInfoManager;
import org.jboss.tools.common.validation.ValidatorManager;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.web.validation.composite.CompositeComponentValidator;
import org.jboss.tools.jst.web.kb.IPageContext;
import org.jboss.tools.jst.web.kb.KbQuery;
import org.jboss.tools.jst.web.kb.KbQuery.Type;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.PageProcessor;
import org.jboss.tools.jst.web.kb.internal.KbBuilder;
import org.jboss.tools.jst.web.kb.internal.validation.KBValidator;
import org.jboss.tools.jst.web.kb.internal.validation.WebValidator;
import org.jboss.tools.jst.web.kb.taglib.IAttribute;
import org.jboss.tools.jst.web.kb.taglib.IComponent;
import org.jboss.tools.jst.web.kb.taglib.INameSpace;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Alexey Kazakov
 */
public class StrictTaglibValidator extends WebValidator {

	public static final String ID = "org.jboss.tools.jsf.StrictTagLibValidator"; //$NON-NLS-1$

	// Project libraries cache: Map<String url, Map<String tagName, Set<String> attributes>>>
	private Map<String, Map<String, Set<String>>> cache;
	private boolean shouldValidateTagLibTags;
	private boolean shouldValidateTagLibTagAttributes;
	
	@Override
	public void init(IProject project,
			ContextValidationHelper validationHelper,
			IProjectValidationContext context, IValidator manager,
			IReporter reporter) {
		super.init(project, validationHelper, context, manager, reporter);

		cache = new HashMap<String, Map<String, Set<String>>>();
		shouldValidateTagLibTags = shouldValidateTagLibTags(project);
		shouldValidateTagLibTagAttributes = shouldValidateTagLibTagAttributes(project);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.validation.IValidator#validate(java.util.Set, org.eclipse.core.resources.IProject, org.jboss.tools.common.validation.ContextValidationHelper, org.jboss.tools.common.validation.IProjectValidationContext, org.jboss.tools.common.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	@Override
	public IStatus validate(Set<IFile> changedFiles, IProject project,
			ContextValidationHelper validationHelper,
			IProjectValidationContext validationContext,
			ValidatorManager manager, IReporter reporter)
			throws ValidationException {
		init(project, validationHelper, validationContext, manager, reporter);
		Set<IFile> filesToValidate = new HashSet<IFile>();
		for (IFile file : changedFiles) {
			if(notValidatedYet(file) && shouldBeValidated(file)) {
				filesToValidate.add(file);
			}
		}
		for (IFile file : filesToValidate) {
			validateFile(file);
		}
		cache = null;
		return OK_STATUS;

	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.validation.IValidator#validateAll(org.eclipse.core.resources.IProject, org.jboss.tools.common.validation.ContextValidationHelper, org.jboss.tools.common.validation.IProjectValidationContext, org.jboss.tools.common.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	@Override
	public IStatus validateAll(IProject project,
			ContextValidationHelper validationHelper,
			IProjectValidationContext validationContext,
			ValidatorManager manager, IReporter reporter)
			throws ValidationException {
		init(project, validationHelper, validationContext, manager, reporter);
		Set<IFile> files = validationHelper.getProjectSetRegisteredFiles();
		Set<IFile> filesToValidate = new HashSet<IFile>();
		for (IFile file : files) {
			if(notValidatedYet(file) && shouldBeValidated(file)) {
				filesToValidate.add(file);
			}
		}
		for (IFile file : filesToValidate) {
			validateFile(file);
		}
		cache = null;
		return OK_STATUS;
	}

	private void validateFile(IFile file) {
		if(reporter.isCancelled()) {
			return;
		}

		ELContext context = PageContextFactory.createPageContext(file);
		if (context instanceof IPageContext) {
			IModelManager manager = StructuredModelManager.getModelManager();
			if(manager != null) {
				IStructuredModel model = null;
				try {
					model = manager.getModelForRead(file);
					if (model instanceof IDOMModel) {
						displaySubtask(JSFValidationMessage.VALIDATING_RESOURCE, new String[]{file.getProject().getName(), file.getName()});
						coreHelper.getValidationContextManager().addValidatedProject(this, file.getProject());
						removeAllMessagesFromResource(file);

						IDOMDocument domDocument = ((IDOMModel) model).getDocument();
						validateChildNodes(file, domDocument.getStructuredDocument(), 
								domDocument, (IPageContext)context);
					}
				} catch (CoreException e) {
					JSFModelPlugin.getDefault().logError(e);
				} catch (IOException e) {
					JSFModelPlugin.getDefault().logError(e);
				} finally {
					if (model != null) {
						model.releaseFromRead();
					}
				}
			}
		}
	}
	
	private void validateChildNodes(IFile file, IDocument document, IDOMNode parent, IPageContext context) {
		NodeList children = parent.getChildNodes();

		for(int i = 0; children != null && i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof IDOMNode) {
				validateNode(file, document, (IDOMNode)child, context);
				validateChildNodes(file, document, (IDOMNode)child, context);
			}
		}
	}
	
	private void validateNode(IFile file, IDocument document, IDOMNode node, IPageContext context) {
		if (!(node instanceof IDOMElement))
			return;
		
		String nodeName = node.getNodeName();
		int offset = node.getStartOffset();
		
		int prefixDivider = nodeName.indexOf(':');
		if (prefixDivider == -1) 
			return;
		
		String prefix = nodeName.substring(0, prefixDivider);
		String name = nodeName.substring(prefixDivider + 1);
		String uri = getUri(context, prefix, offset);
	
		// Check that tag exists
		// If the tag isn't cached - get its attributes and cache them
		Map<String, Set<String>> tagCache = cache.get(uri);
		if (tagCache == null) {
			tagCache = new HashMap<String, Set<String>>();
			cache.put(uri, tagCache);
		}
		
		if (!tagCache.containsKey(nodeName)) {
			KbQuery kbQuery = new KbQuery();
			kbQuery.setPrefix(prefix);
			kbQuery.setUri(uri);
			kbQuery.setMask(false); 
			kbQuery.setType(Type.TAG_NAME);
			kbQuery.setOffset(offset);
			kbQuery.setValue(nodeName); 

			IComponent[] components = PageProcessor.getInstance().getComponents(kbQuery, context, false);
			if (components.length > 0) {
				Set<String> tagAttributes = new HashSet<String>();
				tagCache.put(name, tagAttributes); 

				for (IComponent comp : components) {
					IAttribute[] attributes = comp.getAttributes();
					for (IAttribute attribute : attributes) {
						tagAttributes.add(attribute.getName());
					}
				}
			}
		}
		
		if (!tagCache.containsKey(name)) {
			if(shouldValidateTagLibTags) {
				if (null == unknownTag(file, offset + 1, nodeName.length(), nodeName)) {
					return; // if unknownTag() returns null then no further validation is needed
				}
			}
		} else if (shouldValidateTagLibTagAttributes) {
			NamedNodeMap nodeAttributes = node.getAttributes();
			if (nodeAttributes != null && nodeAttributes.getLength() > 0) {
				Set<String> tagAttributes = tagCache.get(name);
				for (int i = 0; i < nodeAttributes.getLength(); i++) {
					Node attribute = nodeAttributes.item(i);
					String attributeName = attribute.getNodeName();
					int attributeOffset = (attribute instanceof IDOMNode ? ((IDOMNode)attribute).getStartOffset() : offset);

					if (tagAttributes == null || !tagAttributes.contains(attributeName)) {
						if (null == unknownAttribute(file, attributeOffset, attributeName.length(), nodeName, attributeName))
							return; // if unknownAttribute() returns null then no further validation is needed
					}
				}
			}
		}
	}

	private String getUri(IPageContext context, String prefix, int offset) {
		if (prefix == null)
			return null;
		
		Map<String, List<INameSpace>> nameSpaces = context.getNameSpaces(offset);
		if (nameSpaces == null || nameSpaces.isEmpty())
			return null;
		
		for (List<INameSpace> nameSpace : nameSpaces.values()) {
			for (INameSpace n : nameSpace) {
				if (prefix.equals(n.getPrefix())) {
					return n.getURI();
				}
			}
		}
		return null;
	}
	
	private IMarker unknownAttribute(IFile target, int offset, int length, String tagName, String attributeName) {
		return addProblem(JSFValidationMessage.UNKNOWN_TAGLIB_COMPONENT_ATTRIBUTE, JSFSeverityPreferences.UNKNOWN_TAGLIB_ATTRIBUTE, new String[]{attributeName, tagName}, length, offset, target);
	}

	private IMarker unknownTag(IFile target, int offset, int length, String tagName) {
		return addProblem(JSFValidationMessage.UNKNOWN_TAGLIB_COMPONENT_NAME, JSFSeverityPreferences.UNKNOWN_TAGLIB_COMPONENT, new String[]{tagName}, length, offset, target);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.validation.IValidator#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.validation.IValidator#getBuilderId()
	 */
	@Override
	public String getBuilderId() {
		return KbBuilder.BUILDER_ID;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.WebValidator#shouldValidateJavaSources()
	 */
	@Override
	protected boolean shouldValidateJavaSources() {
		return false;
	}

	private static final String BUNDLE_NAME = "org.jboss.tools.jsf.web.validation.messages";

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.validation.TempMarkerManager#getMessageBundleName()
	 */
	@Override
	protected String getMessageBundleName() {
		return BUNDLE_NAME;
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
		return JSFSeverityPreferences.isValidationEnabled(project) && JSFSeverityPreferences.shouldValidateTagLibs(project);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getPreference(org.eclipse.core.resources.IProject, java.lang.String)
	 */
	protected String getPreference(IProject project, String preferenceKey) {
		return JSFSeverityPreferences.getInstance().getProjectPreference(project, preferenceKey);
	}

	protected boolean shouldValidateTagLibTags(IProject project) {
		return JSFSeverityPreferences.shouldValidateTagLibTags(project);
	}

	protected boolean shouldValidateTagLibTagAttributes(IProject project) {
		return JSFSeverityPreferences.shouldValidateTagLibTagAttributes(project);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getMaxNumberOfMarkersPerFile(org.eclipse.core.resources.IProject)
	 */
	public int getMaxNumberOfMarkersPerFile(IProject project) {
		return JSFSeverityPreferences.getMaxNumberOfProblemMarkersPerFile(project);
	}

	@Override
	public void registerPreferenceInfo() {
		PreferenceInfoManager.register(getProblemType(), new CompositeComponentValidator.CompositeComponentPreferenceInfo());
	}
}