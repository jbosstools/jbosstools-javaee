package org.jboss.tools.cdi.internal.core.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.StereotypeElement;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.internal.KbProject;
import org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatingProjectSet;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;
import org.jboss.tools.jst.web.kb.validation.IValidatingProjectSet;
import org.jboss.tools.jst.web.kb.validation.IValidationContext;
import org.jboss.tools.jst.web.kb.validation.IValidator;

public class CDICoreValidator extends CDIValidationErrorManager implements IValidator {
	public static final String ID = "org.jboss.tools.cdi.core.CoreValidator";

	CDIProject cdiProject;
	String projectName;

	public String getId() {
		return ID;
	}

	public IValidatingProjectSet getValidatingProjects(IProject project) {
		IValidationContext rootContext = null;
		IProject war = null; //TODO get war ?
		if(war != null && war.isAccessible()) {
			IKbProject kbProject = KbProjectFactory.getKbProject(war, false);
			if(kbProject!=null) {
				rootContext = kbProject.getValidationContext();
			} else {
				KbProject.checkKBBuilderInstalled(war);
				CDICoreNature cdiProject = CDICorePlugin.getCDI(project, false);
				if(cdiProject != null) {
					rootContext = null; //cdiProject.getDelegate().getValidationContext();
				}
			}
		}
		if(rootContext == null) {
			CDICoreNature cdiProject = CDICorePlugin.getCDI(project, false);
			if(cdiProject != null) {
				rootContext = cdiProject.getValidationContext();
			}
		}

		List<IProject> projects = new ArrayList<IProject>();
		projects.add(project);
//		IProject[] array = set.getAllProjects();
//		for (int i = 0; i < array.length; i++) {
//			if(array[i].isAccessible()) {
//				projects.add(array[i]);
//			}
//		}
		return new ValidatingProjectSet(project, projects, rootContext);
	}

	public boolean shouldValidate(IProject project) {
		try {
			// TODO check preferences
			return project != null && project.isAccessible() && project.hasNature(CDICoreNature.NATURE_ID);
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#init(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter, org.jboss.tools.jst.web.kb.validation.IValidationContext)
	 */
	@Override
	public void init(IProject project, ContextValidationHelper validationHelper, org.eclipse.wst.validation.internal.provisional.core.IValidator manager, IReporter reporter) {
		super.init(project, validationHelper, manager, reporter);

//		SeamProjectsSet set = new SeamProjectsSet(project);
//		IProject warProject = set.getWarProject();
		CDICoreNature nature = CDICorePlugin.getCDI(project, false);
		cdiProject = nature != null ? (CDIProject)nature.getDelegate() : null;
		
		projectName = project.getName();
	}

	public IStatus validate(Set<IFile> changedFiles, IProject project,
			ContextValidationHelper validationHelper, ValidatorManager manager,
			IReporter reporter) throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public IStatus validateAll(IProject project,
			ContextValidationHelper validationHelper, ValidatorManager manager,
			IReporter reporter) throws ValidationException {
		init(project, validationHelper, manager, reporter);
		removeAllMessagesFromResource(cdiProject.getNature().getProject());
		if(cdiProject != null) {
			validateStereotypes();
			
		}
		// TODO 
		return OK_STATUS;
	}

	public void validateStereotypes() {
		Set<IType> ts = cdiProject.getStereotypes();
		for (IType t: ts) {
			StereotypeElement s = cdiProject.getStereotype(t.getFullyQualifiedName());
			if(s == null) continue;
			IResource resource = s.getResource();
			if(resource == null || !resource.getName().endsWith(".java")) {
				//validate sources only
				continue;
			}
			List<IAnnotationDeclaration> as = s.getAnnotationDeclarations();
			
//			1. non-empty name
			AnnotationDeclaration nameDeclaration = s.getNameDeclaration();
			if(nameDeclaration != null) {
				IMemberValuePair[] ps = null;
				try {
					ps = nameDeclaration.getDeclaration().getMemberValuePairs();
				} catch (JavaModelException e) {
					CDICorePlugin.getDefault().logError(e);
				}
				if(ps != null && ps.length > 0) {
					Object name = ps[0].getValue();
					if(name != null && name.toString().length() > 0) {
						ITextSourceReference location = nameDeclaration;
						addError(CDIValidationMessages.STEREOTYPE_DECLARES_NON_EMPTY_NAME, CDIPreferences.STEREOTYPE_DECLARES_NON_EMPTY_NAME, location, resource);
					}
				}
			}

//			2. typed annotation			
			IAnnotationDeclaration typedDeclaration = s.getAnnotationDeclaration(CDIConstants.TYPED_ANNOTATION_TYPE_NAME);
			if(typedDeclaration != null) {
				ITextSourceReference location = typedDeclaration;
				addError(CDIValidationMessages.STEREOTYPE_IS_ANNOTATED_TYPED, CDIPreferences.STEREOTYPE_IS_ANNOTATED_TYPED, location, resource);
			}

//			3. Qualifier other than @Named
			for (IAnnotationDeclaration a: as) {
				if(a instanceof IQualifierDeclaration && a != nameDeclaration) {
					ITextSourceReference location = a;
					addError(CDIValidationMessages.ILLEGAL_QUALIFIER_IN_STEREOTYPE, CDIPreferences.ILLEGAL_QUALIFIER_IN_STEREOTYPE, location, resource);
				}
			}
		}
	}

}
