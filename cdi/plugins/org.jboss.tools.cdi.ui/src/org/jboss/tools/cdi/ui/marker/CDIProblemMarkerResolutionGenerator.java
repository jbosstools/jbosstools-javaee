/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.marker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInterceptor;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotyped;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.refactoring.CDIMarkerResolutionUtils;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationErrorManager;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.quickfix.IQuickFix;
import org.jboss.tools.common.quickfix.IQuickFixGenerator;
import org.jboss.tools.common.refactoring.MarkerResolutionUtils;
import org.jboss.tools.common.validation.java.TempJavaProblem;
import org.jboss.tools.common.validation.java.TempJavaProblemAnnotation;

/**
 * @author Daniel Azarov
 */
public class CDIProblemMarkerResolutionGenerator implements
		IMarkerResolutionGenerator2, IQuickFixGenerator {
	private static final String JAVA_EXTENSION = "java"; //$NON-NLS-1$
	private static final String XML_EXTENSION = "xml"; //$NON-NLS-1$
	private static final int MARKER_RESULUTION_NUMBER_LIMIT = 7;

	@Override
	public IMarkerResolution[] getResolutions(IMarker marker) {
		try {
			return findResolutions(marker);
		} catch (CoreException ex) {
			CDIUIPlugin.getDefault().logError(ex);
		}
		return new IMarkerResolution[] {};
	}
	
	/**
	 * return message id or -1 if impossible to find
	 * @param marker
	 * @return
	 */
	private int getMessageID(IMarker marker)throws CoreException{
		Integer attribute = ((Integer) marker.getAttribute(CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME));
		if (attribute != null)
			return attribute.intValue();

		return -1; 
	}
	
	private int getMessageID(TemporaryAnnotation annotation){
		if(annotation.getAttributes() != null){
			Integer attribute = ((Integer) annotation.getAttributes().get(CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME));
			if (attribute != null)
				return attribute.intValue();
		}
		return -1; 
	}
	
	private IQuickFix[] findXMLResolutions(IFile file, int messageId, int start, int end, boolean asYouType) throws JavaModelException{
		IJavaProject javaProject = EclipseUtil.getJavaProject(file.getProject());
		
		FileEditorInput input = new FileEditorInput(file);
		IDocumentProvider provider = DocumentProviderRegistry.getDefault().getDocumentProvider(input);
		try {
			provider.connect(input);
		} catch (CoreException e) {
			CDIUIPlugin.getDefault().logError(e);
		}
		
		IDocument document = provider.getDocument(input);
		
		String text="";
		try {
			text = document.get(start, end-start);
		} catch (BadLocationException e) {
			CDIUIPlugin.getDefault().logError(e);
		} finally {
			provider.disconnect(input);
		}
		boolean correctTypeName = JavaConventionsUtil.validatePackageName(text, javaProject).isOK();
		
		if(messageId == CDIValidationErrorManager.UNKNOWN_ALTERNATIVE_BEAN_CLASS_NAME_ID){
			IJavaElement element = findJavaElementByQualifiedName(javaProject, text);
			if(element == null && correctTypeName){
				return new IQuickFix[] {
					new CreateCDIElementMarkerResolution(file.getProject(), text, CreateCDIElementMarkerResolution.CREATE_BEAN_CLASS)
				};
			}
		}else if(messageId == CDIValidationErrorManager.UNKNOWN_ALTERNATIVE_ANNOTATION_NAME_ID){
			IJavaElement element = findJavaElementByQualifiedName(javaProject, text);
			if(element == null && correctTypeName){
				return new IQuickFix[] {
					new CreateCDIElementMarkerResolution(file.getProject(), text, CreateCDIElementMarkerResolution.CREATE_STEREOTYPE)
				};
			}
		}else if(messageId == CDIValidationErrorManager.ILLEGAL_ALTERNATIVE_BEAN_CLASS_ID){
			IJavaElement element = getTypeToAddAlternativeToBean(javaProject, text, asYouType);
			if(element != null){
				return new IQuickFix[] {
					new AddAnnotationMarkerResolution(element, CDIConstants.ALTERNATIVE_ANNOTATION_TYPE_NAME)
				};
			}
		}else if(messageId == CDIValidationErrorManager.ILLEGAL_ALTERNATIVE_ANNOTATION_ID){
			IJavaElement element = getTypeToAddAlternativeToStereotype(javaProject, text, asYouType);
			if(element != null){
				return new IQuickFix[] {
					new AddAnnotationMarkerResolution(element, CDIConstants.ALTERNATIVE_ANNOTATION_TYPE_NAME)
				};
			}
		}else if(messageId == CDIValidationErrorManager.UNKNOWN_DECORATOR_BEAN_CLASS_NAME_ID){
			IJavaElement element = findJavaElementByQualifiedName(javaProject, text);
			if(element == null && correctTypeName){
				return new IQuickFix[] {
					new CreateCDIElementMarkerResolution(file.getProject(), text, CreateCDIElementMarkerResolution.CREATE_DECORATOR)
				};
			}
		}else if(messageId == CDIValidationErrorManager.UNKNOWN_INTERCEPTOR_CLASS_NAME_ID){
			IJavaElement element = findJavaElementByQualifiedName(javaProject, text);
			if(element == null && correctTypeName){
				return new IQuickFix[] {
					new CreateCDIElementMarkerResolution(file.getProject(), text, CreateCDIElementMarkerResolution.CREATE_INTERCEPTOR)
				};
			}
		}
		return new IQuickFix[]{};
	}
	
	private IQuickFix[] findJavaResolutions(ICompilationUnit compilationUnit, int problemId, int offset, ICDIMarkerResolutionGeneratorExtension[] extensions, boolean asYouType) throws JavaModelException{
		if (problemId == CDIValidationErrorManager.ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN_ID) {
			IField field = findNonStaticField(compilationUnit, offset);
			if(field != null){
				return new IQuickFix[] {
					new MakeFieldStaticMarkerResolution(field)
				};
			}
		}else if (problemId == CDIValidationErrorManager.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN_ID || 
				problemId == CDIValidationErrorManager.ILLEGAL_DISPOSER_IN_SESSION_BEAN_ID ||
						problemId == CDIValidationErrorManager.ILLEGAL_OBSERVER_IN_SESSION_BEAN_ID) {
			IMethod method = findMethod(compilationUnit, offset);
			if(method != null){
				List<IType> types = findLocalAnnotattedInterfaces(method);
				if(types.size() == 0 && !Flags.isPublic(method.getFlags())){
					return new IQuickFix[] {
						new MakeMethodPublicMarkerResolution(method)
					};
				}else{
					IQuickFix[] resolutions = new IQuickFix[types.size()+1];
					for(int i = 0; i < types.size(); i++){
						resolutions[i] = new MakeMethodBusinessMarkerResolution(method, types.get(i));
					}
					resolutions[types.size()] = new AddLocalBeanMarkerResolution(method);
					return resolutions;
				}
			}
		}else if (problemId == CDIValidationErrorManager.MULTIPLE_DISPOSERS_FOR_PRODUCER_ID) {
			IMethod method = findMethod(compilationUnit, offset);
			if(method != null){
				return new IQuickFix[] {
						new DeleteAllDisposerDuplicantMarkerResolution(method)
					};
			}
		}else if (problemId == CDIValidationErrorManager.MULTIPLE_INJECTION_CONSTRUCTORS_ID) {
			IMethod method = findMethod(compilationUnit, offset);
			if(method != null){
				return new IQuickFix[] {
						new DeleteAllInjectedConstructorsMarkerResolution(method)
					};
			}
		}else if(problemId == CDIValidationErrorManager.AMBIGUOUS_INJECTION_POINTS_ID ||
				problemId == CDIValidationErrorManager.UNSATISFIED_INJECTION_POINTS_ID){
			
			List<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();
			
			IInjectionPoint injectionPoint = findInjectionPoint(compilationUnit, offset, asYouType);
			if(injectionPoint != null){
				List<IBean> beans;
				if(problemId == CDIValidationErrorManager.AMBIGUOUS_INJECTION_POINTS_ID){
					beans = findBeans(injectionPoint);
				}else{
					beans = findLegalBeans(injectionPoint);
				}
				
				for(int i = beans.size()-1; i >= 0; i--){
					IBean bean = beans.get(i);
					for(ICDIMarkerResolutionGeneratorExtension extension : extensions){
						if(extension.shouldBeExtended(problemId, bean)){
							List<IQuickFix> addings = extension.getResolutions(problemId, bean);
							resolutions.addAll(addings);
							beans.remove(bean);
							break;
						}
					}
				}
				
				if(beans.size() < MARKER_RESULUTION_NUMBER_LIMIT){
					for(int i = 0; i < beans.size(); i++){
						resolutions.add(new MakeInjectedPointUnambiguousMarkerResolution(injectionPoint, beans, i));
					}
				}else{
					resolutions.add(new SelectBeanMarkerResolution(injectionPoint, beans));
				}
			}
			return resolutions.toArray(new IQuickFix[]{});
		}else if(problemId == CDIValidationErrorManager.NOT_PASSIVATION_CAPABLE_BEAN_ID){
			IType type = findTypeWithNoSerializable(compilationUnit, offset);
			
			if(type != null){
				return new IQuickFix[] {
						new AddSerializableInterfaceMarkerResolution(type)
					};
			}
		}else if(problemId == CDIValidationErrorManager.ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_PUBLIC_FIELD_ID){
			IField field = findPublicField(compilationUnit, offset);
			CDICoreNature cdiNature = CDIUtil.getCDINatureWithProgress(field.getUnderlyingResource().getProject());
			if(cdiNature != null){
				ICDIProject cdiProject = CDIUtil.getCDIProject((IFile)compilationUnit.getUnderlyingResource(), cdiNature, asYouType);
				
				if(cdiProject != null){
					Collection<IBean> beans = cdiProject.getBeans(field.getUnderlyingResource().getFullPath());
					if(!beans.isEmpty()){
						IBean bean = beans.iterator().next();
						if(field != null){
							return new IQuickFix[] {
									new MakeFieldProtectedMarkerResolution(field),
									new MakeBeanScopedDependentMarkerResolution(compilationUnit, bean)
								};
						}
					}
				}
			}
		}else if(problemId == CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE_ID ||
				problemId == CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_SCOPE_TYPE_ID ||
				problemId == CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_STEREOTYPE_TYPE_ID){
			
			TypeAndAnnotation ta = findTypeAndAnnotation(compilationUnit, offset, CDIConstants.RETENTION_ANNOTATION_TYPE_NAME);
			if(ta != null){
				if(ta.annotation == null){
					return new IQuickFix[] {
							new AddRetentionAnnotationMarkerResolution(ta.type)
						};
				}else{
					return new IQuickFix[] {
							new ChangeAnnotationMarkerResolution(ta.annotation, CDIConstants.RETENTION_POLICY_RUNTIME_TYPE_NAME)
						};
					
				}
			}
		}else if(problemId == CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE_ID){
			TypeAndAnnotation ta = findTypeAndAnnotation(compilationUnit, offset, CDIConstants.TARGET_ANNOTATION_TYPE_NAME);
			if(ta != null){
				if(ta.annotation == null){
					return new IQuickFix[] {
							new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME, CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME, CDIConstants.ELEMENT_TYPE_PARAMETER_NAME}),
							new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_FIELD_NAME, CDIConstants.ELEMENT_TYPE_PARAMETER_NAME})
						};
				}else{
					return new IQuickFix[] {
							new ChangeAnnotationMarkerResolution(ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME, CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME, CDIConstants.ELEMENT_TYPE_PARAMETER_NAME}),
							new ChangeAnnotationMarkerResolution(ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_FIELD_NAME, CDIConstants.ELEMENT_TYPE_PARAMETER_NAME})
						};
					
				}
			}
		}else if(problemId == CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE_ID){
			TypeAndAnnotation ta = findTypeAndAnnotation(compilationUnit, offset, CDIConstants.TARGET_ANNOTATION_TYPE_NAME);
			if(ta != null){
				if(ta.annotation == null){
					return new IQuickFix[] {
						new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME, CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME}),
						new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME}),
						new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME}),
						new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_METHOD_NAME}),
						new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_FIELD_NAME})
					};
				}else{
					return new IQuickFix[] {
						new ChangeAnnotationMarkerResolution(ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME, CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME}),
						new ChangeAnnotationMarkerResolution(ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME}),
						new ChangeAnnotationMarkerResolution(ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME}),
						new ChangeAnnotationMarkerResolution(ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_METHOD_NAME}),
						new ChangeAnnotationMarkerResolution(ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_FIELD_NAME})
					};
				
				}
			}
		}else if(problemId == CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE_ID){
			TypeAndAnnotation ta = findTypeAndAnnotation(compilationUnit, offset, CDIConstants.TARGET_ANNOTATION_TYPE_NAME);
			if(ta != null){
				if(ta.annotation == null){
					return new IQuickFix[] {
						new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME, CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME})
					};
				}else{
					return new IQuickFix[] {
						new ChangeAnnotationMarkerResolution(ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME, CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME})
					};
				
				}
			}
		}else if(problemId == CDIValidationErrorManager.MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER_ID ||
				problemId == CDIValidationErrorManager.MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_QUALIFIER_TYPE_MEMBER_ID ||
				problemId == CDIValidationErrorManager.MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER_ID ||
				problemId == CDIValidationErrorManager.MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_QUALIFIER_TYPE_MEMBER_ID){
			IJavaElement element = findJavaElement(compilationUnit, offset);
			if(element != null){
				IAnnotation annotation = getAnnotation(element, CDIConstants.NON_BINDING_ANNOTATION_TYPE_NAME);
				if(element instanceof IMember && annotation == null){
					return new IQuickFix[] {
						new AddAnnotationMarkerResolution((IMember)element, CDIConstants.NON_BINDING_ANNOTATION_TYPE_NAME)
					};
				}
			}
		}else if(problemId == CDIValidationErrorManager.DISPOSER_ANNOTATED_INJECT_ID){
			IJavaElement element = findJavaElement(compilationUnit, offset);
			if(element != null){
				IJavaElement injectElement = findJavaElementByAnnotation(element, CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
				IJavaElement disposesElement = findJavaElementByAnnotation(element, CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME);
				if(injectElement != null && disposesElement != null){
					return new IQuickFix[] {
						new DeleteAnnotationMarkerResolution(injectElement, CDIConstants.INJECT_ANNOTATION_TYPE_NAME),
						new DeleteAnnotationMarkerResolution(disposesElement, CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME)
					};
				}
			}
		}else if(problemId == CDIValidationErrorManager.PRODUCER_ANNOTATED_INJECT_ID){
			IJavaElement element = findJavaElement(compilationUnit, offset);
			if(element != null){
				IJavaElement injectElement = findJavaElementByAnnotation(element, CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
				IJavaElement produsesElement = findJavaElementByAnnotation(element, CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME);
				if(injectElement != null && produsesElement != null){
					return new IQuickFix[] {
						new DeleteAnnotationMarkerResolution(injectElement, CDIConstants.INJECT_ANNOTATION_TYPE_NAME),
						new DeleteAnnotationMarkerResolution(produsesElement, CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME)
					};
				}
			}
		}else if(problemId == CDIValidationErrorManager.OBSERVER_ANNOTATED_INJECT_ID){
			IJavaElement element = findJavaElement(compilationUnit, offset);
			if(element != null){
				IJavaElement injectElement = findJavaElementByAnnotation(element, CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
				IJavaElement observerElement = findJavaElementByAnnotation(element, CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME);
				if(injectElement != null && observerElement != null){
					return new IQuickFix[] {
						new DeleteAnnotationMarkerResolution(injectElement, CDIConstants.INJECT_ANNOTATION_TYPE_NAME),
						new DeleteAnnotationMarkerResolution(observerElement, CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME)
					};
				}
			}
		}else if(problemId == CDIValidationErrorManager.CONSTRUCTOR_PARAMETER_ANNOTATED_OBSERVES_ID){
			IJavaElement element = findJavaElement(compilationUnit, offset);
			if(element != null){
				IJavaElement observerElement = findJavaElementByAnnotation(element, CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME);
				if(observerElement != null){
					return new IQuickFix[] {
						new DeleteAnnotationMarkerResolution(observerElement, CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME)
					};
				}
			}
		}else if(problemId == CDIValidationErrorManager.DISPOSER_IN_INTERCEPTOR_ID ||
				problemId == CDIValidationErrorManager.DISPOSER_IN_DECORATOR_ID ||
				problemId == CDIValidationErrorManager.CONSTRUCTOR_PARAMETER_ANNOTATED_DISPOSES_ID){
			IJavaElement element = findJavaElement(compilationUnit, offset);
			if(element != null){
				IJavaElement disposerElement = findJavaElementByAnnotation(element, CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME);
				if(disposerElement != null){
					return new IQuickFix[] {
						new DeleteAnnotationMarkerResolution(disposerElement, CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME)
					};
				}
			}
		}else if(problemId == CDIValidationErrorManager.PRODUCER_IN_INTERCEPTOR_ID ||
				problemId == CDIValidationErrorManager.PRODUCER_IN_DECORATOR_ID){
			IJavaElement element = findJavaElement(compilationUnit, offset);
			if(element != null){
				IJavaElement producerElement = findJavaElementByAnnotation(element, CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME);
				if(producerElement != null){
					return new IQuickFix[] {
						new DeleteAnnotationMarkerResolution(producerElement, CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME)
					};
				}
			}
		}else if(problemId == CDIValidationErrorManager.STEREOTYPE_DECLARES_NON_EMPTY_NAME_ID){
			TypeAndAnnotation ta = findTypeAndAnnotation(compilationUnit, offset, CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
			if(ta != null && ta.annotation != null && ta.type != null){
				return new IQuickFix[] {
					new ChangeAnnotationMarkerResolution(ta.annotation),
					new DeleteAnnotationMarkerResolution(ta.type, CDIConstants.NAMED_QUALIFIER_TYPE_NAME)
				};
			}
		}else if(problemId == CDIValidationErrorManager.INTERCEPTOR_HAS_NAME_ID ||
				problemId == CDIValidationErrorManager.DECORATOR_HAS_NAME_ID){
			TypeAndAnnotation ta = findTypeAndAnnotation(compilationUnit, offset, CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
			if(ta != null && ta.type != null){
				CDICoreNature cdiNature = CDIUtil.getCDINatureWithProgress(ta.type.getUnderlyingResource().getProject());
				if(cdiNature != null){
					ICDIProject cdiProject = CDIUtil.getCDIProject((IFile)compilationUnit.getUnderlyingResource(), cdiNature, asYouType);;
					IType declarationType = findNamedDeclarationType(cdiProject, ta.type, problemId == CDIValidationErrorManager.DECORATOR_HAS_NAME_ID);
					ArrayList<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();
					if(declarationType != null){
						IAnnotation annotation = getAnnotation(declarationType, CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
						if(annotation != null){
							resolutions.add(new DeleteAnnotationMarkerResolution(declarationType, CDIConstants.NAMED_QUALIFIER_TYPE_NAME));
						}
						
						if(!declarationType.equals(ta.type)){
							annotation = getAnnotation(ta.type, declarationType.getFullyQualifiedName());
							if(annotation != null){
								resolutions.add(new DeleteAnnotationMarkerResolution(ta.type, declarationType.getFullyQualifiedName()));
							}
						}
					}
					if(!resolutions.isEmpty()){
						return resolutions.toArray(new IQuickFix[]{});
					}
				}
			}
		}else if(problemId == CDIValidationErrorManager.STEREOTYPE_IS_ANNOTATED_TYPED_ID){
			TypeAndAnnotation ta = findTypeAndAnnotation(compilationUnit, offset, CDIConstants.TYPED_ANNOTATION_TYPE_NAME);
			if(ta != null && ta.annotation != null && ta.type != null){
				return new IQuickFix[] {
					new DeleteAnnotationMarkerResolution(ta.type, CDIConstants.TYPED_ANNOTATION_TYPE_NAME)
				};
			}
		}else if(problemId == CDIValidationErrorManager.INTERCEPTOR_ANNOTATED_SPECIALIZES_ID ||
				problemId == CDIValidationErrorManager.DECORATOR_ANNOTATED_SPECIALIZES_ID){
			TypeAndAnnotation ta = findTypeAndAnnotation(compilationUnit, offset, CDIConstants.SPECIALIZES_ANNOTATION_TYPE_NAME);
			if(ta != null && ta.annotation != null && ta.type != null){
				return new IQuickFix[] {
					new DeleteAnnotationMarkerResolution(ta.type, CDIConstants.SPECIALIZES_ANNOTATION_TYPE_NAME)
				};
			}
		}else if(problemId == CDIValidationErrorManager.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_DISPOSES_ID){
			IJavaElement element = findJavaElement(compilationUnit, offset);
			if(element != null){
				IJavaElement producerElement = findJavaElementByAnnotation(element, CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME);
				IJavaElement disposerElement = findJavaElementByAnnotation(element, CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME);
				if(producerElement != null && disposerElement != null){
					return new IQuickFix[] {
						new DeleteAnnotationMarkerResolution(producerElement, CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME),
						new DeleteAnnotationMarkerResolution(disposerElement, CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME)
					};
				}
			}
		}else if(problemId == CDIValidationErrorManager.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_OBSERVES_ID){
			IJavaElement element = findJavaElement(compilationUnit, offset);
			if(element != null){
				IJavaElement producerElement = findJavaElementByAnnotation(element, CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME);
				IJavaElement observerElement = findJavaElementByAnnotation(element, CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME);
				if(producerElement != null && observerElement != null){
					return new IQuickFix[] {
						new DeleteAnnotationMarkerResolution(producerElement, CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME),
						new DeleteAnnotationMarkerResolution(observerElement, CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME)
					};
				}
			}
		}else if(problemId == CDIValidationErrorManager.OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED_ID){
			IJavaElement element = findJavaElement(compilationUnit, offset);
			if(element != null){
				IJavaElement disposerElement = findJavaElementByAnnotation(element, CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME);
				IJavaElement observerElement = findJavaElementByAnnotation(element, CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME);
				if(disposerElement != null && observerElement != null){
					return new IQuickFix[] {
						new DeleteAnnotationMarkerResolution(disposerElement, CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME),
						new DeleteAnnotationMarkerResolution(observerElement, CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME)
					};
				}
			}
		}else if(problemId == CDIValidationErrorManager.OBSERVER_IN_DECORATOR_ID ||
				problemId == CDIValidationErrorManager.OBSERVER_IN_INTERCEPTOR_ID){
			IJavaElement element = findJavaElement(compilationUnit, offset);
			if(element != null){
				IJavaElement observerElement = findJavaElementByAnnotation(element, CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME);
				if(observerElement != null){
					return new IQuickFix[] {
						new DeleteAnnotationMarkerResolution(observerElement, CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME)
					};
				}
			}
		}else if(problemId == CDIValidationErrorManager.SESSION_BEAN_ANNOTATED_INTERCEPTOR_ID){
			IJavaElement element = findJavaElement(compilationUnit, offset);
			if(element != null){
				IJavaElement interceptorElement = findJavaElementByAnnotation(element, CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME);
				if(interceptorElement != null){
					return new IQuickFix[] {
						new DeleteAnnotationMarkerResolution(interceptorElement, CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME)
					};
				}
			}
		}else if(problemId == CDIValidationErrorManager.SESSION_BEAN_ANNOTATED_DECORATOR_ID){
			IJavaElement element = findJavaElement(compilationUnit, offset);
			if(element != null){
				IJavaElement decoratorElement = findJavaElementByAnnotation(element, CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME);
				if(decoratorElement != null){
					return new IQuickFix[] {
						new DeleteAnnotationMarkerResolution(decoratorElement, CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME)
					};
				}
			}
		}else if(problemId == CDIValidationErrorManager.PARAM_INJECTION_DECLARES_EMPTY_NAME_ID){
			List<IMarkerResolution> resolutions = getAddNameResolutions(compilationUnit, offset, asYouType);
			return resolutions.toArray(new IQuickFix[]{});
		}else if(problemId == CDIValidationErrorManager.MULTIPLE_DISPOSING_PARAMETERS_ID){
			ILocalVariable parameter = findParameter(compilationUnit, offset);
			if(parameter != null){
				return new IQuickFix[] {
					new DeleteAllOtherAnnotationsFromParametersMarkerResolution(CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME, parameter, (IFile)parameter.getUnderlyingResource())
				};
			}
		}else if(problemId == CDIValidationErrorManager.MULTIPLE_OBSERVING_PARAMETERS_ID){
			ILocalVariable parameter = findParameter(compilationUnit, offset);
			if(parameter != null){
				return new IQuickFix[] {
					new DeleteAllOtherAnnotationsFromParametersMarkerResolution(CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME, parameter, (IFile)parameter.getUnderlyingResource())
				};
			}
		}
		return new IQuickFix[] {};
	}

	private IMarkerResolution[] findResolutions(IMarker marker)
			throws CoreException {

		int messageId = getMessageID(marker);
		if (messageId == -1)
			return new IMarkerResolution[] {};

		final IFile file = (IFile) marker.getResource();
		
		

		Integer attribute = ((Integer) marker.getAttribute(IMarker.CHAR_START));
		if (attribute == null)
			return new IMarkerResolution[] {};
		final int start = attribute.intValue();
		
		attribute = ((Integer) marker.getAttribute(IMarker.CHAR_END));
		if (attribute == null)
			return new IMarkerResolution[] {};
		final int end = attribute.intValue();
		
		ICDIMarkerResolutionGeneratorExtension[] extensions = CDIQuickFixExtensionManager.getInstances();

		if (JAVA_EXTENSION.equals(file.getFileExtension())) {
			ICompilationUnit compilationUnit = EclipseUtil.getCompilationUnit(file);
			
			return findJavaResolutions(compilationUnit, messageId, start, extensions, false);
			
		}else if (XML_EXTENSION.equals(file.getFileExtension())){
			return findXMLResolutions(file, messageId, start, end, false);
		}
		return new IMarkerResolution[] {};
	}
	
	private List<IMarkerResolution> getAddNameResolutions(ICompilationUnit compilationUnit, int start, boolean asYouType) throws JavaModelException{
		List<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();
		ILocalVariable parameter = findParameter(compilationUnit, start);
		if(parameter != null){
			IAnnotation namedAnnotation = getAnnotation(parameter, CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
			if(namedAnnotation != null){
				CDICoreNature cdiNature = CDIUtil.getCDINatureWithProgress(compilationUnit.getUnderlyingResource().getProject());
				if(cdiNature == null)
					return resolutions;
				
				ICDIProject cdiProject = CDIUtil.getCDIProject((IFile)compilationUnit.getUnderlyingResource(), cdiNature, asYouType);
				
				if(cdiProject == null){
					return resolutions;
				}
				
				Collection<IBean> allBeans = CDIUtil.getFilteredBeans(cdiProject, compilationUnit.getUnderlyingResource().getFullPath());
				
				IInjectionPoint ip = CDIUtil.findInjectionPoint(allBeans, parameter, start);
				if(ip != null){
					List<IBean> beans;
					beans = findLegalBeans(ip);
					if(beans.size() == 0){
						beans = findBeans(ip);
					}
					if(beans.size() < MARKER_RESULUTION_NUMBER_LIMIT){
						for(IBean bean : beans){
							if(bean.getName() != null && !bean.getName().isEmpty()){
								resolutions.add(new AddNameMarkerResolution(namedAnnotation, bean.getName()));
							}
						}
					}
				}
			}
			if(resolutions.size() == 0){
				resolutions.add(new AddNameMarkerResolution(namedAnnotation, parameter.getElementName()));
			}
		}
		return resolutions;
	}
	
	private IType getTypeToAddAlternativeToBean(IJavaProject javaProject, String qualifiedName, boolean asYouType) throws JavaModelException{
		IType type = null;
		try {
			type =  javaProject.findType(qualifiedName);
		} catch (JavaModelException ex) {
			CDIUIPlugin.getDefault().logError(ex);
		}
		
		if(type != null){
			CDICoreNature cdiNature = CDIUtil.getCDINatureWithProgress(javaProject.getProject());
			if(cdiNature != null){
				ICDIProject cdiProject = CDIUtil.getCDIProject((IFile)type.getUnderlyingResource(), cdiNature, asYouType);
				
				if(cdiProject != null){
					IClassBean classBean = cdiProject.getBeanClass(type);
					if(classBean != null && !classBean.isAlternative()){
						return type;
					}
				}
			}
		}
		return null;
	}

	private IType getTypeToAddAlternativeToStereotype(IJavaProject javaProject, String qualifiedName, boolean asYouType) throws JavaModelException{
		IType type = null;
		try {
			type =  javaProject.findType(qualifiedName);
		} catch (JavaModelException ex) {
			CDIUIPlugin.getDefault().logError(ex);
		}
		
		if(type != null){
			CDICoreNature cdiNature = CDIUtil.getCDINatureWithProgress(javaProject.getProject());
			if(cdiNature != null){
				ICDIProject cdiProject = CDIUtil.getCDIProject((IFile)type.getUnderlyingResource(), cdiNature, asYouType);
				
				if(cdiProject != null){
					IStereotype stereotype = cdiProject.getStereotype(qualifiedName);
					if(stereotype != null && !stereotype.isAlternative()){
						return type;
					}
				}
			}
		}
		return null;
	}
	
	private IJavaElement findJavaElementByQualifiedName(IJavaProject javaProject, String qualifiedName){
		try {
			return javaProject.findType(qualifiedName);
		} catch (JavaModelException ex) {
			CDIUIPlugin.getDefault().logError(ex);
		}
		
		return null;
	}
	
	private IType findNamedDeclarationType(ICDIProject cdiProject, IType type, boolean isItDecorator){
		IType declarationType = null;
		IBean bean = null;
		IAnnotationDeclaration declaration = null;
		if(isItDecorator){
			bean = findDecoratorByType(cdiProject.getDecorators(), type);
		}else{
			bean = findInterceptorByType(cdiProject.getInterceptors(), type);
		}
		if(bean != null){
			declaration = findNamedDeclaration(bean);
			if(declaration != null){
				declarationType = declaration.getType(); 
			}
		}
		return declarationType;
	}
	
	private IDecorator findDecoratorByType(IDecorator[] decorators, IType type){
		for(IDecorator decorator : decorators){
			if(decorator.getBeanClass().equals(type))
				return decorator;
		}
		return null;
	}
	
	private IInterceptor findInterceptorByType(IInterceptor[] interceptors, IType type){
		for(IInterceptor interceptor : interceptors){
			if(interceptor.getBeanClass().equals(type))
				return interceptor;
		}
		return null;
	}
	
	private IAnnotationDeclaration findNamedDeclaration(IStereotyped stereotyped){
		IAnnotationDeclaration declaration = stereotyped.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
//		if (declaration == null) {
//			declaration = stereotyped.getAnnotation(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME);
//		}
		if (declaration == null) {
			declaration = CDIUtil.getNamedStereotypeDeclaration(stereotyped);
		}
		return declaration;
	}
	
	private List<IBean> findLegalBeans(IInjectionPoint injectionPoint){
		IBean[] bs = injectionPoint.getCDIProject().getBeans();
		
		String injectionPointTypeName = injectionPoint.getClassBean().getBeanClass().getFullyQualifiedName();
		String injectionPointPackage = null;
		
		int dotLastIndex = injectionPointTypeName.lastIndexOf(CDIMarkerResolutionUtils.DOT);
		
		if(dotLastIndex < 0)
			injectionPointPackage = "";
		else
			injectionPointPackage = injectionPointTypeName.substring(0, dotLastIndex);

		ArrayList<IBean> beans = new ArrayList<IBean>();
    	for(IBean bean : bs){
    		if(CDIProject.containsType(bean.getLegalTypes(), injectionPoint.getType())){
    			boolean isPublic = true;
				try{
					isPublic = Flags.isPublic(bean.getBeanClass().getFlags());
				}catch(JavaModelException ex){
					CDIUIPlugin.getDefault().logError(ex);
				}
    			String beanTypeName = bean.getBeanClass().getFullyQualifiedName();
    			String beanPackage = null;
    			
    			dotLastIndex = beanTypeName.lastIndexOf(CDIMarkerResolutionUtils.DOT);
    			
    			if(dotLastIndex < 0)
    				beanPackage = "";
    			else
    				beanPackage = beanTypeName.substring(0,dotLastIndex);
    			
    			if((isPublic || injectionPointPackage.equals(beanPackage)) && bean.getBeanClass().getResource() != null)
    				beans.add(bean);
    		}
    	}
    	return beans;
	}
	
	
	private IInjectionPoint findInjectionPoint(ICompilationUnit compilationUnit, int start, boolean asYouType) throws JavaModelException{
		IJavaElement element = findJavaElement(compilationUnit, start);
		if(element == null)
			return null;
		
		CDICoreNature cdiNature = CDIUtil.getCDINatureWithProgress(compilationUnit.getUnderlyingResource().getProject());
		if(cdiNature == null)
			return null;
		
		ICDIProject cdiProject = CDIUtil.getCDIProject((IFile)compilationUnit.getUnderlyingResource(), cdiNature, asYouType);
		
		if(cdiProject == null){
			return null;
		}
		
		Collection<IBean> allBeans = CDIUtil.getFilteredBeans(cdiProject, compilationUnit.getUnderlyingResource().getFullPath());
		
		IInjectionPoint ip = CDIUtil.findInjectionPoint(allBeans, element, start);
		
		return ip;
	}
	
	private List<IBean> findBeans(IInjectionPoint injectionPoint){
		ICDIProject cdiProject = injectionPoint.getCDIProject();
		List<IBean> beans = CDIUtil.getSortedBeans(cdiProject, false, injectionPoint);
		for(int i = beans.size()-1;i>= 0;i--){
			if(beans.get(i).getBeanClass().getResource() == null)
				beans.remove(i);
		}
		
		return beans;
	}
	
	private IMethod findMethod(ICompilationUnit compilationUnit, int start){
		IJavaElement javaElement = findJavaElement(compilationUnit, start);
		if(javaElement instanceof IMethod){
			IMethod method = (IMethod)javaElement;
			if(!method.isBinary())
				return method;
		}
		return null;
	}

	private IType findTypeWithNoSerializable(ICompilationUnit compilationUnit, int start) throws JavaModelException{
		IJavaElement javaElement = findJavaElement(compilationUnit, start);
		if(javaElement instanceof IType){
			IType type = (IType)javaElement;
			if(!type.isBinary()){
				String shortName = CDIMarkerResolutionUtils.getShortName(AddSerializableInterfaceMarkerResolution.SERIALIZABLE);
				String[] interfaces = type.getSuperInterfaceNames();
				for(String name : interfaces){
					if(name.equals(shortName))
						return null;
				}
				return type;
			}
		}
		return null;
	}
	
	class TypeAndAnnotation{
		IType type;
		IAnnotation annotation;
		
		public TypeAndAnnotation(IType type){
			this.type = type;
		}
		
		public TypeAndAnnotation(IType type, IAnnotation annotation){
			this(type);
			this.annotation = annotation;
		}
	}
	
	private TypeAndAnnotation findTypeAndAnnotation(ICompilationUnit compilationUnit, int start, String annotationQualifiedName) throws JavaModelException{
		IJavaElement javaElement = findJavaElement(compilationUnit, start);
		if(javaElement instanceof IType){
			IType type = (IType)javaElement;
			if(!type.isBinary()){
				IAnnotation annotation = getAnnotation(type, annotationQualifiedName);
				if(annotation != null){
					return new TypeAndAnnotation(type, annotation);
				}
				return new TypeAndAnnotation(type);
			}
		}
		return null;
	}
	
	private IJavaElement findJavaElement(ICompilationUnit compilationUnit, int start){
		try{
			return compilationUnit == null ? null : compilationUnit.getElementAt(start);
		}catch(JavaModelException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
		return null;
		
	}
	
	private ILocalVariable findParameter(ICompilationUnit compilationUnit, int start){
		IJavaElement element = findJavaElement(compilationUnit, start);
		if(element instanceof IMethod){
			try {
				for(ILocalVariable parameter : ((IMethod) element).getParameters()){
					if(parameter.getSourceRange().getOffset() <= start && parameter.getSourceRange().getOffset()+parameter.getSourceRange().getLength() > start){
						return parameter;
					}
				}
			} catch (JavaModelException ex) {
				CDIUIPlugin.getDefault().logError(ex);
			}
		}
		return null;
	}
	
	private List<IType> findLocalAnnotattedInterfaces(IMethod method) throws JavaModelException{
		ArrayList<IType> types = new ArrayList<IType>();
		
		if(method.getTypeParameters().length > 0)
			return types;
		
		IType type = method.getDeclaringType();
		String[] is = type.getSuperInterfaceNames();
		for(int i = 0; i < is.length; i++){
			String f = EclipseJavaUtil.resolveType(type, is[i]);
			IType t = EclipseResourceUtil.getValidType(type.getJavaProject().getProject(), f);
			if(t != null && t.isInterface()){
				IAnnotation localAnnotation = EclipseJavaUtil.findAnnotation(t, t, CDIConstants.LOCAL_ANNOTATION_TYPE_NAME);
				if(localAnnotation != null){
					if(isMethodExists(t, method)){
						types.clear();
						return types;
					}
					types.add(t);
				}
			}
		}
		return types;
	}
	
	private boolean isMethodExists(IType interfaceType, IMethod method){
		IMethod existingMethod = interfaceType.getMethod(method.getElementName(), method.getParameterTypes());
		if(existingMethod.exists())
			return true;
		return false;
	}
	
	private IField findNonStaticField(ICompilationUnit compilationUnit, int start){
		try{
			IJavaElement javaElement = findJavaElement(compilationUnit, start);
			
			if(javaElement instanceof IField){
				IField field = (IField)javaElement;
				if(!Flags.isStatic(field.getFlags()) && !field.isBinary())
					return field;
			}
		}catch(JavaModelException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
		return null;
	}

	private IField findPublicField(ICompilationUnit compilationUnit, int start){
		try{
			IJavaElement javaElement = findJavaElement(compilationUnit, start);
			
			if(javaElement instanceof IField){
				IField field = (IField)javaElement;
				if(Flags.isPublic(field.getFlags()) && !field.isBinary())
					return field;
			}
		}catch(JavaModelException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
		return null;
	}
	
	@Override
	public boolean hasResolutions(IMarker marker) {
		try {
			return getMessageID(marker) >= 0;
		} catch (CoreException ex) {
			CDIUIPlugin.getDefault().logError(ex);
		}
		return false;
	}
	
	private IAnnotation getAnnotation(IJavaElement element, String annotationQualifiedName){
		if(element instanceof IAnnotatable){
			String shortName = CDIMarkerResolutionUtils.getShortName(annotationQualifiedName);
			IAnnotation[] annotations;
			try {
				annotations = ((IAnnotatable)element).getAnnotations();
				for(IAnnotation annotation : annotations){
					if(annotation.getElementName().equals(annotationQualifiedName) ||
							annotation.getElementName().equals(shortName))
						return annotation;
						
				}
			} catch (JavaModelException e) {
				CDIUIPlugin.getDefault().logError(e);
			}
		}
		return null;
	}
	
	private IJavaElement findJavaElementByAnnotation(IJavaElement element, String qualifiedName) throws JavaModelException{
		IAnnotation annotation = getAnnotation(element, qualifiedName);
		if(annotation != null)
			return element;
		
		if(element instanceof IMethod){
			for(ILocalVariable parameter : ((IMethod)element).getParameters()){
				annotation = getAnnotation(parameter, qualifiedName);
				if(annotation != null)
					return parameter;
			}
		}else if(element instanceof ILocalVariable){
			IJavaElement parent = element.getParent();
			if(parent != null){
				annotation = getAnnotation(parent, qualifiedName);
				if(annotation != null)
					return parent;
			}
		}else if(element instanceof IType){
			for(IField field : ((IType)element).getFields()){
				annotation = getAnnotation(field, qualifiedName);
				if(annotation != null)
					return field;
			}
			
			for(IMethod method : ((IType)element).getMethods()){
				annotation = getAnnotation(method, qualifiedName);
				if(annotation != null)
					return method;
				for(ILocalVariable parameter : method.getParameters()){
					annotation = getAnnotation(parameter, qualifiedName);
					if(annotation != null)
						return parameter;
				}
			}
		}
		
		if(element instanceof IMember){
			annotation = getAnnotation(((IMember)element).getDeclaringType(), qualifiedName);
			if(annotation != null)
				return ((IMember)element).getDeclaringType();
		}
		
		return null;
	}
	
	@Override
	public boolean hasProposals(Annotation annotation) {
		if(annotation instanceof TempJavaProblemAnnotation || annotation instanceof TemporaryAnnotation){
			return true;
		}
		return false;
	}

	@Override
	public IJavaCompletionProposal[] getProposals(Annotation annotation) {
		if(annotation instanceof TempJavaProblemAnnotation){
			TempJavaProblemAnnotation javaAnnotation = (TempJavaProblemAnnotation) annotation;
			
			int messageId = javaAnnotation.getId() - TempJavaProblem.TEMP_PROBLEM_ID;
			ICompilationUnit compilationUnit = javaAnnotation.getCompilationUnit();
			if(compilationUnit != null){
				int start = javaAnnotation.getPosition();
				
				ICDIMarkerResolutionGeneratorExtension[] extensions = CDIQuickFixExtensionManager.getInstances();
				
				try {
					return findJavaResolutions(compilationUnit, messageId, start, extensions, true);
				} catch (JavaModelException e) {
					CDIUIPlugin.getDefault().logError(e);
				}
			}
		}else if(annotation instanceof TemporaryAnnotation){
			TemporaryAnnotation tempAnnotation = (TemporaryAnnotation)annotation;
			
			IFile file = MarkerResolutionUtils.getFile();
			int messageId = getMessageID(tempAnnotation);
			int start = tempAnnotation.getPosition().getOffset();
			int end = start + tempAnnotation.getPosition().getLength();
			
			try {
				return findXMLResolutions(file, messageId, start, end, true);
			} catch (JavaModelException e) {
				CDIUIPlugin.getDefault().logError(e);
			}
		}
		return null;
	}
	
	
}
