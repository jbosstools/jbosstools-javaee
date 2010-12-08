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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IInterceptor;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * beans.xml validator
 * 
 * @author Alexey Kazakov
 */
public class BeansXmlValidationDelegate extends CDICoreValidationDelegate {

	private AlternativeClassValidator alternativeClassValidator;
	private AlternativeStereotypeValidator alternativeStereotypeValidator;
	private DecoratorTypeValidator decoratorTypeValidator;
	private InterceptorTypeValidator interceptorTypeValidator;

	public BeansXmlValidationDelegate(CDICoreValidator validator) {
		super(validator);
	}

	private AlternativeClassValidator getAlternativeClassValidator() {
		if(alternativeClassValidator==null) {
			alternativeClassValidator = new AlternativeClassValidator();
		}
		return alternativeClassValidator;
	}

	private AlternativeStereotypeValidator getAlternativeStereotypeValidator() {
		if(alternativeStereotypeValidator==null) {
			alternativeStereotypeValidator = new AlternativeStereotypeValidator();
		}
		return alternativeStereotypeValidator;
	}

	private DecoratorTypeValidator getDecoratorTypeValidator() {
		if(decoratorTypeValidator==null) {
			decoratorTypeValidator = new DecoratorTypeValidator();
		}
		return decoratorTypeValidator;
	}

	private InterceptorTypeValidator getInterceptorTypeValidator() {
		if(interceptorTypeValidator==null) {
			interceptorTypeValidator = new InterceptorTypeValidator();
		}
		return interceptorTypeValidator;
	}

	public void validateBeansXml(IFile beansXml) {
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

				/*
				 * 5.1.1. Declaring selected alternatives for a bean archive
				 *  - Each child <class> element must specify the name of an alternative bean class. If there is no class with the specified
				 *    name, or if the class with the specified name is not an alternative bean class, the container automatically detects the problem
				 *    and treats it as a deployment problem.
				 *  - If the same type is listed twice under the <alternatives> element, the container automatically detects the problem and
				 *    treats it as a deployment problem.
				 */
				validateTypeBeanForBeansXml(
						getAlternativeClassValidator(),
						document,
						beansXml);

				/*
				 * 5.1.1. Declaring selected alternatives for a bean archive
				 *  - Each child <stereotype> element must specify the name of an @Alternative stereotype annotation. If there is no annotation
				 *    with the specified name, or the annotation is not an @Alternative stereotype, the container automatically detects the
				 *    problem and treats it as a deployment problem.
				 *  - If the same type is listed twice under the <alternatives> element, the container automatically detects the problem and
				 *    treats it as a deployment problem. 
				 */
				validateTypeBeanForBeansXml(
						getAlternativeStereotypeValidator(),
						document,
						beansXml);

				/*
				 * 8.2. Decorator enablement and ordering
				 *  - Each child <class> element must specify the name of a decorator bean class. If there is no class with the specified name,
				 *    or if the class with the specified name is not a decorator bean class, the container automatically detects the problem and
				 *    treats it as a deployment problem.
				 *  - If the same class is listed twice under the <decorators> element, the container automatically detects the problem and
				 *    treats it as a deployment problem.
				 */
				validateTypeBeanForBeansXml(
						getDecoratorTypeValidator(),
						document,
						beansXml);

				/*
				 * 9.4. Interceptor enablement and ordering
				 * 	- Each child <class> element must specify the name of an interceptor class. If there is no class with the specified name, or if
				 * 	  the class with the specified name is not an interceptor class, the container automatically detects the problem and treats it as
				 * 	  a deployment problem.
				 *  - If the same class is listed twice under the <interceptors> element, the container automatically detects the problem and treats it as
				 *    a deployment problem.
				 */
				validateTypeBeanForBeansXml(
						getInterceptorTypeValidator(),
						document,
						beansXml);
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

	private void validateTypeBeanForBeansXml(TypeValidator typeValidator, IDOMDocument document, IFile beansXml) {
		try {
			NodeList parentNodeList = document.getElementsByTagName(typeValidator.getParrentElementname());
			for (int i = 0; i < parentNodeList.getLength(); i++) {
				Node parentNode = parentNodeList.item(i);
				if(parentNode instanceof Element) {
					List<TypeNode> typeNodes = getTypeElements((Element)parentNode, typeValidator.getTypeElementName());
					Map<String, TypeNode> uniqueTypes = new HashMap<String, TypeNode>();
					for (TypeNode typeNode : typeNodes) {
						IType type = getType(beansXml, typeNode, typeValidator.getUnknownTypeErrorMessage());
						if(type!=null) {
							if(!type.isBinary()) {
								validator.getValidationContext().addLinkedCoreResource(beansXml.getFullPath().toOSString(), type.getPath(), false);
							}
							if(!typeValidator.validateKindOfType(type)) {
								validator.addError(typeValidator.getIllegalTypeErrorMessage(), CDIPreferences.ILLEGAL_TYPE_NAME_IN_BEANS_XML,
										new String[]{}, typeNode.getLength(), typeNode.getStartOffset(), beansXml);
							} else if(type.isBinary()) {
								if(!typeValidator.validateBinaryType(type)) {
									validator.addError(typeValidator.getIllegalTypeErrorMessage(), CDIPreferences.ILLEGAL_TYPE_NAME_IN_BEANS_XML,
											new String[]{}, typeNode.getLength(), typeNode.getStartOffset(), beansXml);
								}
								continue;
							} else {
								if(!typeValidator.validateSourceType(type)) {
									validator.addError(typeValidator.getIllegalTypeErrorMessage(), CDIPreferences.ILLEGAL_TYPE_NAME_IN_BEANS_XML,
											new String[]{}, typeNode.getLength(), typeNode.getStartOffset(), beansXml);
								}
							}
							TypeNode node = uniqueTypes.get(typeNode.getTypeName());
							if(node!=null) {
								if(!node.isMarkedAsDuplicated()) {
									validator.addError(typeValidator.getDuplicateTypeErrorMessage(), CDIPreferences.DUPLICATE_TYPE_IN_BEANS_XML,
											new String[]{}, node.getLength(), node.getStartOffset(), beansXml);
								}
								node.setMarkedAsDuplicated(true);
								validator.addError(typeValidator.getDuplicateTypeErrorMessage(), CDIPreferences.DUPLICATE_TYPE_IN_BEANS_XML,
										new String[]{}, typeNode.getLength(), typeNode.getStartOffset(), beansXml);
							}
							uniqueTypes.put(typeNode.getTypeName(), typeNode);
						}
					}
				}
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
        }
	}

	private Map<IProject, IJavaProject> javaProjects;

	private IJavaProject getJavaProject(IResource resource) {
		if(javaProjects == null) {
			javaProjects = new HashMap<IProject, IJavaProject>();
		}
		IProject project = resource.getProject();
		if(project.isAccessible()) {
			IJavaProject javaProject = javaProjects.get(project);
			if(javaProject==null) {
				javaProject = EclipseUtil.getJavaProject(project);
				if(javaProject!=null) {
					javaProjects.put(project, javaProject);
				}
			}
			return javaProject;
		}
		return null;
	}

	private IType getType(IFile beansXml, TypeNode node, String errorMessage) {
		IType type = null;
		if(node.getTypeName()!=null) {
			try {
				IJavaProject javaProject = getJavaProject(beansXml);
				if(javaProject!=null) {
					type = EclipseJavaUtil.findType(javaProject, node.getTypeName());
				}
			} catch (JavaModelException e) {
				CDICorePlugin.getDefault().logError(e);
				return null;
			}
		}
		if(type==null) {
			addLinkedResourcesForUnknownType(beansXml, node.getTypeName());
			validator.addError(errorMessage, CDIPreferences.ILLEGAL_TYPE_NAME_IN_BEANS_XML,
					new String[]{}, node.getLength(), node.getStartOffset(), beansXml);
		}
		return type;
	}

	private void addLinkedResourcesForUnknownType(IFile beansXml, String typeName) {
		if(typeName!=null && typeName.trim().length()>0) {
			IStatus status = JavaConventions.validateJavaTypeName(typeName, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7);
			if(status.getSeverity()!=IStatus.ERROR) {
				String packagePath = typeName.replace('.', '/');
				Set<IFolder> sources = EclipseResourceUtil.getSourceFolders(beansXml.getProject());
				for (IFolder source : sources) {
					IPath path = source.getFullPath().append(packagePath + ".java"); //$NON-NLS-1$
					validator.getValidationContext().addLinkedCoreResource(beansXml.getFullPath().toOSString(), path, false);
				}
			}
		}
	}

	private List<TypeNode> getTypeElements(Element parentElement, String typeElementName) {
		List<TypeNode> result = new ArrayList<TypeNode>();
		NodeList list = parentElement.getElementsByTagName(typeElementName);
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
							result.add(new TypeNode(start, length, className));
							break;
						}
					}
				}
			}

			if(empty && classNode instanceof IndexedRegion) {
				int start = ((IndexedRegion)classNode).getStartOffset();
				int end = ((IndexedRegion)classNode).getEndOffset();
				int length = end - start;
				result.add(new TypeNode(start, length, null));
			}
		}
		return result;
	}

	private static class TypeNode {
		private int startOffset;
		private int length;
		private String typeName;
		private boolean markedAsDuplicated;

		public TypeNode(int startOffset, int length, String typeName) {
			this.startOffset = startOffset;
			this.length = length;
			this.typeName = typeName;
		}

		public int getStartOffset() {
			return startOffset;
		}

		public void setStartOffset(int startOffset) {
			this.startOffset = startOffset;
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}

		public String getTypeName() {
			return typeName;
		}

		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}

		public boolean isMarkedAsDuplicated() {
			return markedAsDuplicated;
		}

		public void setMarkedAsDuplicated(boolean markedAsDuplicated) {
			this.markedAsDuplicated = markedAsDuplicated;
		}
	}

	private static interface TypeValidator {

		boolean validateSourceType(IType type);

		boolean validateBinaryType(IType type) throws JavaModelException;

		/**
		 * Validates if the type represens class/annotation/...
		 * @param type
		 * @return
		 * @throws JavaModelException 
		 */
		boolean validateKindOfType(IType type) throws JavaModelException;

		String getTypeElementName();

		String getParrentElementname();

		String getUnknownTypeErrorMessage();

		String getIllegalTypeErrorMessage();

		String getDuplicateTypeErrorMessage();
	}

	private abstract class AbstractTypeValidator implements TypeValidator {

		public String getTypeElementName() {
			return "class"; //$NON-NLS-1$
		}

		public boolean validateKindOfType(IType type) throws JavaModelException {
			return type.isClass();
		}

		public boolean validateBinaryType(IType type) throws JavaModelException {
			IAnnotation[] annotations = type.getAnnotations();
			for (IAnnotation annotation : annotations) {
				if(annotation.getElementName().equals(getAnnotationName())) {
					return true;
				}
			}
			return false;
		}

		protected abstract String getAnnotationName();
	}

	private class AlternativeClassValidator extends AbstractTypeValidator {

		public boolean validateSourceType(IType type) {
			IClassBean classBean = validator.cdiProject.getBeanClass(type);
			return classBean!=null && classBean.isAlternative();
		}

		public String getParrentElementname() {
			return "alternatives"; //$NON-NLS-1$
		}

		public String getUnknownTypeErrorMessage() {
			return CDIValidationMessages.UNKNOWN_ALTERNATIVE_BEAN_CLASS_NAME;
		}

		public String getIllegalTypeErrorMessage() {
			return CDIValidationMessages.ILLEGAL_ALTERNATIVE_BEAN_CLASS;
		}

		public String getDuplicateTypeErrorMessage() {
			return CDIValidationMessages.DUPLICATE_ALTERNATIVE_TYPE;
		}

		@Override
		protected String getAnnotationName() {
			return CDIConstants.ALTERNATIVE_ANNOTATION_TYPE_NAME;
		}
	}

	private class AlternativeStereotypeValidator extends AbstractTypeValidator {

		public boolean validateSourceType(IType type) {
			IStereotype stereotype = validator.cdiProject.getStereotype(type);
			return stereotype!=null && stereotype.isAlternative();
		}

		@Override
		public boolean validateKindOfType(IType type) throws JavaModelException {
			return type.isAnnotation();
		}

		@Override
		public String getTypeElementName() {
			return "stereotype"; //$NON-NLS-1$
		}

		public String getParrentElementname() {
			return "alternatives"; //$NON-NLS-1$
		}

		public String getUnknownTypeErrorMessage() {
			return CDIValidationMessages.UNKNOWN_ALTERNATIVE_ANNOTATION_NAME;
		}

		public String getIllegalTypeErrorMessage() {
			return CDIValidationMessages.ILLEGAL_ALTERNATIVE_ANNOTATION;
		}

		public String getDuplicateTypeErrorMessage() {
			return CDIValidationMessages.DUPLICATE_ALTERNATIVE_TYPE;
		}

		@Override
		protected String getAnnotationName() {
			return CDIConstants.ALTERNATIVE_ANNOTATION_TYPE_NAME;
		}
	}

	private class DecoratorTypeValidator extends AbstractTypeValidator {

		public boolean validateSourceType(IType type) {
			IClassBean classBean = validator.cdiProject.getBeanClass(type);
			return classBean instanceof IDecorator;
		}

		public String getParrentElementname() {
			return "decorators"; //$NON-NLS-1$
		}

		public String getUnknownTypeErrorMessage() {
			return CDIValidationMessages.UNKNOWN_DECORATOR_BEAN_CLASS_NAME;
		}

		public String getIllegalTypeErrorMessage() {
			return CDIValidationMessages.ILLEGAL_DECORATOR_BEAN_CLASS;
		}

		public String getDuplicateTypeErrorMessage() {
			return CDIValidationMessages.DUPLICATE_DECORATOR_CLASS;
		}

		@Override
		protected String getAnnotationName() {
			return CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME;
		}
	}

	private class InterceptorTypeValidator extends AbstractTypeValidator {

		public boolean validateSourceType(IType type) {
			IClassBean classBean = validator.cdiProject.getBeanClass(type);
			return classBean instanceof IInterceptor;
		}

		public String getParrentElementname() {
			return "interceptors"; //$NON-NLS-1$
		}

		public String getUnknownTypeErrorMessage() {
			return CDIValidationMessages.UNKNOWN_INTERCEPTOR_CLASS_NAME;
		}

		public String getIllegalTypeErrorMessage() {
			return CDIValidationMessages.ILLEGAL_INTERCEPTOR_CLASS;
		}

		public String getDuplicateTypeErrorMessage() {
			return CDIValidationMessages.DUPLICATE_INTERCEPTOR_CLASS;
		}

		@Override
		protected String getAnnotationName() {
			return CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME;
		}
	}
}