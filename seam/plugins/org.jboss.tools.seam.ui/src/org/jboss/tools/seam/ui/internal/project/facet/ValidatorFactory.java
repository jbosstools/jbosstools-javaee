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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.jboss.tools.seam.core.internal.project.facet.ISeamFacetDataModelProperties;



public class ValidatorFactory {
	
	static public Map<String,IValidator> validators = new HashMap<String, IValidator>();
	
	static public Map<String,String> NO_ERRORS = new HashMap<String,String>();
	
	static public IValidator NO_ERRORS_VALIDATOR = new IValidator() {
		public Map<String, String> validate(Object value, Object context) {
			// TODO Auto-generated method stub
			return NO_ERRORS;
		}
	};
	
	public static IValidator getValidator(String id) {
		IValidator validator = validators.get(id);
		return validator==null?NO_ERRORS_VALIDATOR:validator;
	}
	
	public static Map<String,String> createErrorMap() {
		return new HashMap<String,String>();
	}
	
	public static Map<String,String> createErrormessage(String text) {
		Map<String,String> map = createErrorMap();
		map.put("", text);
		return map;
	}
	
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
				errors.put(
						ISeamFacetDataModelProperties.JBOSS_SEAM_HOME,
						"Seam Home Folde field points to location that doesn't look like seam home folder");
				
			}
			return errors;
		}
	};
	
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
	
	public static IValidator CLASS_QNAME_VALIDATOR = new IValidator() {
		public Map<String, String> validate(Object value, Object context) {
			
			return ValidatorFactory.NO_ERRORS;
		}
	};
	
	public static IValidator FILESYSTEM_FILE_EXISTS_VALIDATOR = new IValidator() {
		public java.util.Map<String,String> validate(Object value, Object context) {
			return ValidatorFactory.NO_ERRORS;
		};
	};
	
}
