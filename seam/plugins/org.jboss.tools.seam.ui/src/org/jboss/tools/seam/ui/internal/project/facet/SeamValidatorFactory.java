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
package org.jboss.tools.seam.ui.internal.project.facet;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.common.ui.IValidator;
import org.jboss.tools.common.ui.ValidatorFactory;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.SeamUIMessages;

/**
 * 
 * @author eskimo
 * 
 */
@SuppressWarnings("restriction")
public class SeamValidatorFactory extends ValidatorFactory {

	/**
	 * 
	 */
	public static final IValidator JBOSS_SEAM_HOME_FOLDER_VALIDATOR = new IValidator() {
		public Map<String, IStatus> validate(Object value, Object context) {
			Map<String, IStatus> errors = FILE_SYSTEM_FOLDER_EXISTS.validate(
					value, context);
			if (!errors.isEmpty()) {
				errors = createErrorMap();
				errors.put(ISeamFacetDataModelProperties.JBOSS_SEAM_HOME, new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID,
					SeamUIMessages.VALIDATOR_FACTORY_SEAM_HOME_FOLDER_DOES_NOT_EXISTS));
				return errors;
			}
			String version = (String) context;

			if (version.startsWith("2.")) { //$NON-NLS-1$
				File seamJarFile = new File(value.toString(),
						"lib/jboss-seam.jar"); //$NON-NLS-1$
				if (!seamJarFile.isFile()) {
					errors = createErrorMap();
					errors.put(ISeamFacetDataModelProperties.JBOSS_SEAM_HOME,
							new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, 
									SeamUIMessages.VALIDATOR_FACTORY_HOME_FOLDER_POINTS_TO_LOCATION_THAT_DOES_NOT_LOOK_LIKE_SEAM_HOME_FOLDER));
				}
			} else {
				File seamJarFile = new File(value.toString(), "jboss-seam.jar"); //$NON-NLS-1$
				if (!seamJarFile.isFile()) {
					errors = createErrorMap();
					errors.put(ISeamFacetDataModelProperties.JBOSS_SEAM_HOME,
							new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, 
									SeamUIMessages.VALIDATOR_FACTORY_HOME_FOLDER_POINTS_TO_LOCATION_THAT_DOES_NOT_LOOK_LIKE_SEAM_HOME_FOLDER));
				}
			}

			return errors;
		}
	};

	/**
	 * 
	 */
	public static final IValidator JBOSS_AS_HOME_FOLDER_VALIDATOR = new IValidator() {
		public Map<String, IStatus> validate(Object value, Object context) {
			Map<String, IStatus> errors = FILE_SYSTEM_FOLDER_EXISTS.validate(
					value, context);
			if (!errors.isEmpty()) {
				errors = createErrorMap();
				errors.put(ISeamFacetDataModelProperties.JBOSS_AS_HOME,
						new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID,
								SeamUIMessages.VALIDATOR_FACTORY_JBOSS_AS_HOME_FOLDER_DOES_NOT_EXIST));
				return errors;
			}
			if (!new File(value.toString(), "bin/twiddle.jar").isFile()) { //$NON-NLS-1$
				errors.put(ISeamFacetDataModelProperties.JBOSS_AS_HOME,
					new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID,
						SeamUIMessages.VALIDATOR_FACTORY_JBOSS_AS_HOME_FOLDER_POINT_TO_LOCATION_THAT_DOES_NOT_LOOK_LIKE_JBOSS_AS_HOME_FOLDER));
			}
			return errors;
		}
	};

	/**
	 * 
	 */
	public static final IValidator CLASS_QNAME_VALIDATOR = new IValidator() {
		public Map<String, IStatus> validate(Object value, Object context) {
			String classDecl = "class " + value.toString() + " {}"; //$NON-NLS-1$ //$NON-NLS-2$
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(classDecl.toCharArray());
			parser.setProject((IJavaProject) context);
			CompilationUnit compilationUnit = (CompilationUnit)parser.createAST(null);
			IProblem[] problems = compilationUnit.getProblems();
			if (problems.length > 0) {
				return createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID,
					Messages.format(SeamUIMessages.VALIDATOR_FACTORY_COMPONENT_NAME_IS_NOT_VALID,
						problems[0].getMessage())));
			}
			return SeamValidatorFactory.NO_ERRORS;
		}
	};

	public static final IValidator PACKAGE_NAME_VALIDATOR = new IValidator() {
		public Map<String, IStatus> validate(Object value, Object context) {
			IStatus status = JavaConventions.validatePackageName(value.toString(),
					DEFAULT_SOURCE_LEVEL,
					DEFAULT_COMPLIANCE_LEVEL);
			if (status.getSeverity() == IStatus.ERROR) {
				return createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, Messages.format(NewWizardMessages.NewTypeWizardPage_error_InvalidPackageName, status.getMessage())));
			}

			return SeamValidatorFactory.NO_ERRORS;
		}
	};

	/**
	 * 
	 */
	public static final IValidator FILESYSTEM_FILE_EXISTS_VALIDATOR = new IValidator() {
		public java.util.Map<String, IStatus> validate(Object value,
				Object context) {
			return SeamValidatorFactory.NO_ERRORS;
		};
	};

	/**
	 * 
	 * @author eskimo
	 * 
	 */
	public static final IValidator SEAM_COMPONENT_NAME_VALIDATOR = new IValidator() {

		public Map<String, IStatus> validate(Object value, Object context) {
			String name = value.toString();
			if(context != null && context instanceof ISeamProject){
				ISeamProject seamProject = (ISeamProject)context;
				ISeamComponent component = seamProject.getComponent(name);
				if(component != null)
					return createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, NLS.bind(SeamUIMessages.VALIDATOR_FACTORY_COMPONENT_ALREADY_EXISTS, name)));
			}
			
			String[] segs = name.split("\\.");//$NON-NLS-1$
			for(String segm : segs){
				if(!segm.trim().equals(segm))
					return createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, SeamUIMessages.VALIDATOR_FACTORY_NAME_IS_NOT_VALID));
				
				IStatus status = JavaConventions.validateClassFileName(segm
						+ ".class", DEFAULT_SOURCE_LEVEL, DEFAULT_COMPLIANCE_LEVEL); //$NON-NLS-1$
				if (!status.isOK())
					return createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, SeamUIMessages.VALIDATOR_FACTORY_NAME_IS_NOT_VALID));
			}
			return NO_ERRORS;
		}
	};

	/**
	 * 
	 * @author eskimo
	 * 
	 */
	public static final IValidator SEAM_JAVA_INTEFACE_NAME_CONVENTION_VALIDATOR = new IValidator() {

		public Map<String, IStatus> validate(Object value, Object context) {
			if (!(context instanceof Object[])) {
				throw new IllegalArgumentException(
						"Context parameter should be instance of Object[]"); //$NON-NLS-1$
			}

			Object[] contextArray = ((Object[]) context);
			IProject project = (IProject) contextArray[1];
			IJavaProject jProject = JavaCore.create(project);

			IStatus status = JavaConventions.validateJavaTypeName(value.toString(),
					getCompilerSourceLevel(jProject), getCompilerComplianceLevel(jProject));			
			if (((IStatus.ERROR | IStatus.WARNING) & status.getSeverity()) != 0) {
				return createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, SeamUIMessages.VALIDATOR_FACTORY_LOCAL_INTERFACE_NAME_IS_NOT_VALID
						+ status.getMessage()));
			}
			return NO_ERRORS;
		}
	};

	public static final IValidator SEAM_METHOD_NAME_VALIDATOR = new IValidator() {

		public Map<String, IStatus> validate(Object value, Object context) {
			if (!(context instanceof Object[])) {
				throw new IllegalArgumentException(
						"Context parameter should be instance of Object[]"); //$NON-NLS-1$
			}

			Object[] contextArray = ((Object[]) context);
			String targetName = contextArray[0].toString();
			IProject project = (IProject) contextArray[1];
			IJavaProject jProject = JavaCore.create(project);
			
			IStatus status = JavaConventions.validateMethodName(value.toString(),
					getCompilerSourceLevel(jProject), getCompilerComplianceLevel(jProject));
			
			if (status.getSeverity() == IStatus.ERROR){
				return createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID,
						NLS.bind(SeamUIMessages.VALIDATOR_FACTORY_NAME_IS_NOT_VALID, targetName)));
			}

			return NO_ERRORS;
		}
	};

	public static final IValidator FILE_NAME_VALIDATOR = new IValidator() {

		public Map<String, IStatus> validate(Object value, Object context) {
			if (!(context instanceof Object[])) {
				throw new IllegalArgumentException(
						"Context parameter should be instance of Object[]"); //$NON-NLS-1$
			}

			Object[] contextArray = ((Object[]) context);
			String targetName = contextArray[0].toString();
			IProject project = (IProject) contextArray[1];

			if ("".equals(value) //$NON-NLS-1$
					|| !project.getLocation().isValidSegment(value.toString()))
				return createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID,
						NLS.bind(SeamUIMessages.VALIDATOR_FACTORY_NAME_IS_NOT_VALID, targetName)));

			return NO_ERRORS;
		}
	};

	public static final IValidator SEAM_PROJECT_NAME_VALIDATOR = new IValidator() {
		public Map<String, IStatus> validate(Object value, Object context) {

			if (value == null || "".equals(value)) { //$NON-NLS-1$
				return createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, SeamUIMessages.VALIDATOR_FACTORY_PRJ_NOT_SELECTED));
			}
			IResource project = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(value.toString());

			if (project == null || !(project instanceof IProject)
					|| !project.exists()) {
				return createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, 
						NLS.bind(SeamUIMessages.VALIDATOR_FACTORY_PROJECT_DOES_NOT_EXIST,
						value)));
			} else {
				IProject selection = (IProject) project;
				try {
					if (!selection.hasNature(ISeamProject.NATURE_ID)
							|| SeamCorePlugin.getSeamPreferences(selection) == null
							// ||
							// selection.getAdapter(IFacetedProject.class)==null
							// || !((IFacetedProject)selection.getAdapter(
							// IFacetedProject
							// .class)).hasProjectFacet(ProjectFacetsManager
							// .getProjectFacet("jst.web"))
							|| ""	.equals(SeamCorePlugin.getSeamPreferences(selection).get(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS, ""))) { //$NON-NLS-1$
						return createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, 
								NLS.bind(SeamUIMessages.VALIDATOR_FACTORY_SELECTED_PROJECT_IS_NOT_A_SEAM_WEB_PROJECT,
								project.getName())));
					} else {
						// TODO validate project(s) structure
					}
				} catch (CoreException e) {
					// it might happen only if project is closed and project
					// name typed by hand
					return createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, NLS
							.bind(
									SeamUIMessages.VALIDATOR_FACTORY_SELECTED_PRJ_IS_CLOSED,
									project.getName())));
				}
			}
			return NO_ERRORS;
		}
	};

	private static class ConnectionProfileValidator implements IValidator {

		private boolean allowEmptyConnection;

		public ConnectionProfileValidator(boolean allowEmptyConnection) {
			this.allowEmptyConnection = allowEmptyConnection;
		}

		public Map<String, IStatus> validate(Object value, Object context) {
			if (!allowEmptyConnection && (value == null || "".equals(value.toString().trim()))) { //$NON-NLS-1$
				return createErrormessage(
						ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE,
						new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, SeamUIMessages.VALIDATOR_FACTORY_CONNECTION_PROFILE_IS_NOT_SELECTED));
			} else {
				IConnectionProfile connProfile = ProfileManager.getInstance()
						.getProfileByName(value.toString());
				Properties props = connProfile.getBaseProperties();
				Object driverClass = props
						.get("org.eclipse.datatools.connectivity.db.driverClass"); //$NON-NLS-1$

				if (driverClass == null || "".equals(driverClass)) { //$NON-NLS-1$
					return createErrormessage(
							ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE,
							new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, 
									NLS.bind(SeamUIMessages.VALIDATOR_FACTORY_DRIVER_CLASS_PROPERTY_IS_EMPTY_FOR_SELECTED_CONNECTION_PROFILE,
									value)));
				}
			}
			return NO_ERRORS;
		}
	}

	public static final IValidator CONNECTION_PROFILE_VALIDATOR = new ConnectionProfileValidator(false);

	public static final IValidator CONNECTION_DRIVER_VALIDATOR = new ConnectionProfileValidator(true);

	public static final IValidator SEAM_RUNTIME_NAME_VALIDATOR = new IValidator() {
		public Map<String, IStatus> validate(Object value, Object context) {

			Map<String, IStatus> errors = NO_ERRORS;
			if (value == null || "".equals(value.toString().trim())) { //$NON-NLS-1$
				errors = createErrormessage(
						ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME,
						new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, SeamUIMessages.VALIDATOR_FACTORY_SEAM_RUNTIME_IS_NOT_SELECTED));
			} else {
				SeamRuntime rt = SeamRuntimeManager.getInstance()
						.findRuntimeByName(value.toString());
				if (rt == null) {
					errors = createErrormessage(
							ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME,
							new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, NLS
									.bind(
											SeamUIMessages.VALIDATOR_FACTORY_CANNOT_FIND_SEAM_RUNTIME,
											value)));
				} else if (!new File(rt.getHomeDir()).exists()) {
					errors = createErrormessage(
							ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME,
							new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, NLS
									.bind(
											SeamUIMessages.VALIDATOR_FACTORY_SEAM_RT_HOME_DIR_IS_MISSING,
											value)));
				} else if (!new File(rt.getSeamGenDir()).exists()) {
					errors = createErrormessage(
							ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME,
							new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, NLS
									.bind(
											SeamUIMessages.VALIDATOR_FACTORY_SEAM_RT_TEMPLATES_DIR_IS_MISSING,
											value)));
				} else if (!new File(rt.getSrcTemplatesDir()).exists()) {
					errors = createErrormessage(
							ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME,
							new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, NLS
									.bind(
											SeamUIMessages.VALIDATOR_FACTORY_SEAM_RT_SRC_DIR_IS_MISSING,
											value)));
				} else if (!new File(rt.getViewTemplatesDir()).exists()) {
					errors = createErrormessage(
							ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME,
							new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, NLS
									.bind(
											SeamUIMessages.VALIDATOR_FACTORY_SEAM_RT_VIEW_DIR_IS_MISSING,
											value)));
				} else if (!new File(rt.getResourceTemplatesDir()).exists()) {
					errors = createErrormessage(
							ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME,
							new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, NLS
									.bind(
											SeamUIMessages.VALIDATOR_FACTORY_SEAM_RT_RESOURCES_DIR_IS_MISSING,
											value)));
				}
			}
			return errors;
		}
	};

	public static final IValidator SEAM_RUNTIME_VALIDATOR = new IValidator() {
		public java.util.Map<String, IStatus> validate(Object value,
				Object context) {
			Map<String, IStatus> errors = NO_ERRORS;

			if (value == null || "".equals(value)) { //$NON-NLS-1$
				errors = createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, SeamUIMessages.VALIDATOR_FACTORY_SEAM_RT_NOT_CONFIGURED));
			} else {
				SeamRuntime rt = SeamRuntimeManager.getInstance()
						.findRuntimeByName(value.toString());
				if (rt == null) {
					errors = createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, NLS
							.bind(
									SeamUIMessages.VALIDATOR_FACTORY_SEAM_RT_CANNOT_BE_FOUND,
									value)));
				} else if (!new File(rt.getHomeDir()).exists()) {
					errors = createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, NLS
							.bind(
									SeamUIMessages.VALIDATOR_FACTORY_SEAM_RT_HOME_DIR_DOES_NOT_EXIST,
									value)));
				} else if (!new File(rt.getSeamGenDir()).exists()) {
					errors = createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, NLS
							.bind(
									SeamUIMessages.VALIDATOR_FACTORY_SEAM_RT_TEMPLATES_DIR_DOES_NOT_EXIST,
									value)));
				} else if (!new File(rt.getSrcTemplatesDir()).exists()) {
					errors = createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, NLS
							.bind(
									SeamUIMessages.VALIDATOR_FACTORY_SEAM_RT_SOURCE_TEMPLATES_DIR_DOES_NOT_EXIST,
									value)));
				} else if (!new File(rt.getViewTemplatesDir()).exists()) {
					errors = createErrormessage(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, NLS
							.bind(
									SeamUIMessages.VALIDATOR_FACTORY_SEAM_RT_VIE_TEMPLATE_DIR_DOES_NOT_EXIST,
									value)));
					// } else if(!new
					// File(rt.getResourceTemplatesDir()).exists()) {
					// errors = createErrormessage(NLS.bind(
					// "Seam Runtime '{0)' resource templates directory doesn't exist for selected Seam Web Project"
					// ,value));
				}
			}
			return errors;
		}
	};
}