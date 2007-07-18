/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.internal.project.facet;

import java.io.File;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.wizard.IParameter;

/**
 * 
 * @author eskimo
 *
 */
public class ValidatorFactory {
	
	
	
	/**
	 * 
	 */
	static public Map<String,IValidator> validators = new HashMap<String, IValidator>();
	
	/**
	 * 
	 */
	static public Map<String,String> NO_ERRORS = Collections.unmodifiableMap(new HashMap<String,String>());
	
	/**
	 * 
	 */
	static public IValidator NO_ERRORS_VALIDATOR = new IValidator() {
		public Map<String, String> validate(Object value, Object context) {
			// TODO Auto-generated method stub
			return NO_ERRORS;
		}
	};
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static IValidator getValidator(String id) {
		IValidator validator = validators.get(id);
		return validator==null?NO_ERRORS_VALIDATOR:validator;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Map<String,String> createErrorMap() {
		return new HashMap<String,String>();
	}
	
	/**
	 * 
	 * @param text
	 * @return
	 */
	public static Map<String,String> createErrormessage(String text) {
		Map<String,String> map = createErrorMap();
		map.put(IValidator.DEFAULT_ERROR, text);
		return map;
	}
	
	/**
	 * 
	 */
	public static final IValidator FILE_SYSTEM_FOLDER_EXISTS = new IValidator() {

		public Map<String, String> validate(Object value, Object context) {
			if(value == null)
				throw new IllegalArgumentException("Path to a folder cannot be null");
			String folderPath = value.toString();
			File folder = new File(folderPath);

			if (!folder.exists())
				return createErrormessage("Folder '" + folderPath +"' doesn't exists");
			if(!folder.isDirectory())
				return createErrormessage("Path '" + folderPath +"' points to file");
			return NO_ERRORS;
		}
		
	};
	
	/**
	 * 
	 */
	public static IValidator JBOSS_SEAM_HOME_FOLDER_VALIDATOR = new IValidator() {
		public Map<String, String> validate(Object value, Object context) {
			Map<String,String> errors = FILE_SYSTEM_FOLDER_EXISTS.validate(value, context);
			if(errors.size()>0) {
				errors = createErrorMap();
				errors.put(
						ISeamFacetDataModelProperties.JBOSS_SEAM_HOME,
						"Seam Home folder doesn't exist"
				);
				return errors;
			}
			if(!new File(value.toString(),"seam").isFile()) {
				errors = createErrormessage(
						"Seam Home Folde field points to location that doesn't look like seam home folder");
				
			}
			return errors;
		}
	};
	
	/**
	 * 
	 */
	public static IValidator JBOSS_AS_HOME_FOLDER_VALIDATOR = new IValidator() {
		public Map<String, String> validate(Object value, Object context) {
			Map<String,String> errors = FILE_SYSTEM_FOLDER_EXISTS.validate(value, context);
			if(errors.size()>0) {
				errors = createErrorMap();
				errors.put(
						ISeamFacetDataModelProperties.JBOSS_AS_HOME,
						"JBoss AS Home folder doesn't exist"
				);
				return errors;
			}
			if(!new File(value.toString(),"bin/twiddle.jar").isFile()) {
				errors.put(
						ISeamFacetDataModelProperties.JBOSS_AS_HOME,
						"JBoss AS Home Folde field points to location that doesn't look like JBoss AS home folder");	
			}
			return errors;
		}
	};
	
	/**
	 * 
	 */
	public static IValidator CLASS_QNAME_VALIDATOR = new IValidator() {
		public Map<String, String> validate(Object value, Object context) {
			String classDecl = "class " + value.toString() + " {}";
			ASTParser parser= ASTParser.newParser(AST.JLS3);
			parser.setSource(classDecl.toCharArray());
			parser.setProject((IJavaProject)context);
			CompilationUnit compilationUnit= (CompilationUnit) parser.createAST(null);
			IProblem[] problems= compilationUnit.getProblems();
			if (problems.length > 0) {
				return createErrormessage(Messages.format("Component name is not invalid.", problems[0].getMessage()));
			}			
			return ValidatorFactory.NO_ERRORS;
		}
	};
	
	/**
	 * 
	 */
	public static IValidator FILESYSTEM_FILE_EXISTS_VALIDATOR = new IValidator() {
		public java.util.Map<String,String> validate(Object value, Object context) {
			return ValidatorFactory.NO_ERRORS;
		};
	};
	/**
	 * 
	 * @author eskimo
	 *
	 */
	public static IValidator SEAM_COMPONENT_NAME_VALIDATOR = new IValidator() {

		public Map<String, String> validate(Object value, Object context) {
			String targetName = null;
			IProject project = null;
			if(context instanceof Object[]) {
				Object[] contextArray = ((Object[])context);
				targetName = contextArray[0].toString();
				project = (IProject)contextArray[1];
			}
			
			String classDecl = "class " + value + " {}";
			ASTParser parser= ASTParser.newParser(AST.JLS3);
			parser.setSource(classDecl.toCharArray());
			parser.setProject(JavaCore.create(project));
			CompilationUnit compilationUnit= (CompilationUnit) parser.createAST(null);
			IProblem[] problems= compilationUnit.getProblems();
			
			if (problems.length > 0) {
				return createErrormessage(targetName + " name is not valid.");
			}			
		
			return NO_ERRORS;
		}
	};
	
	/**
	 * 
	 * @author eskimo
	 *
	 */
	public static IValidator SEAM_JAVA_INTEFACE_NAME_CONVENTION_VALIDATOR = new IValidator() {

		public Map<String, String> validate(Object value, Object context) {
			String targetName = null;
			IProject project = null;
			if(context instanceof Object[]) {
				Object[] contextArray = ((Object[])context);
				targetName = contextArray[0].toString();
				project = (IProject)contextArray[1];
			}
			IJavaProject jProject = JavaCore.create(project);
			
			String sourceLevel= jProject.getOption(JavaCore.COMPILER_SOURCE, true);
			String compliance= jProject.getOption(JavaCore.COMPILER_COMPLIANCE, true);
			IStatus status = JavaConventions.validateJavaTypeName(value.toString(), sourceLevel, compliance);
			if(status.getSeverity() == IStatus.WARNING) {
				return createErrormessage("Local Interface name is not valid.\n" + status.getMessage());
			}
			return NO_ERRORS;
		}
	};

	public static IValidator SEAM_METHOD_NAME_VALIDATOR = new IValidator() {

		public Map<String, String> validate(Object value, Object context) {
			String targetName = null;
			IProject project = null;
			
			if(context instanceof Object[]) {
				Object[] contextArray = ((Object[])context);
				targetName = contextArray[0].toString();
				project = (IProject)contextArray[1];
			}
			
			String classDecl = "class ClassName {public void " + value.toString() + "() {}}";
			ASTParser parser= ASTParser.newParser(AST.JLS3);
			parser.setSource(classDecl.toCharArray());
			parser.setProject(JavaCore.create(project));
			CompilationUnit compilationUnit= (CompilationUnit) parser.createAST(null);
			IProblem[] problems= compilationUnit.getProblems();
			
			if (problems.length > 0) {
				return createErrormessage(targetName + " name is not valid.");
			}			
		
			return NO_ERRORS;
		}
	};

	public static IValidator FILE_NAME_VALIDATOR = new IValidator() {

		public Map<String, String> validate(Object value, Object context) {
			String targetName = null;
			IProject project = null;
			
			if(context instanceof Object[]) {
				Object[] contextArray = ((Object[])context);
				targetName = contextArray[0].toString();
				project = (IProject)contextArray[1];
			}
			if("".equals(value) || !project.getLocation().isValidSegment(value.toString()))
				return createErrormessage(targetName + " name is not valid.");		
		
			return NO_ERRORS;
		}
	};

	public static IValidator SEAM_PROJECT_NAME_VALIDATOR = new IValidator() {
		public Map<String, String> validate(Object value, Object context) {

			IResource project = ResourcesPlugin.getWorkspace().getRoot().findMember(value.toString());
			
			if(project==null || !(project instanceof IProject) || !project.exists()) {
				return createErrormessage("Project '" + value + "' does'n exist.");
			} else {
				try {
					if(!((IProject)project).hasNature(ISeamProject.NATURE_ID)) {
						return createErrormessage("Project '" + project.getName()+ "' has no Seam nature.");
					}
				} catch (CoreException e) {
					SeamCorePlugin.getPluginLog().logError(e);
				}
			}
			return NO_ERRORS;
		}
	};
}
