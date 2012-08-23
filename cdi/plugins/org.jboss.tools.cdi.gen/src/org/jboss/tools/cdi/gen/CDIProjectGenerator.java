/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.gen;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.cdi.gen.model.GenAnnotation;
import org.jboss.tools.cdi.gen.model.GenAnnotationReference;
import org.jboss.tools.cdi.gen.model.GenClass;
import org.jboss.tools.cdi.gen.model.GenField;
import org.jboss.tools.cdi.gen.model.GenInterface;
import org.jboss.tools.cdi.gen.model.GenProject;
import org.jboss.tools.cdi.gen.model.GenQualifier;
import org.jboss.tools.cdi.gen.model.GenType;
import org.jboss.tools.common.zip.UnzipOperation;
import org.osgi.framework.Bundle;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDIProjectGenerator {
	GenProject project = new GenProject();
	File workspaceLocation;
	
	Random seed = new Random();

	int packageCount = 50;
	int interfaceCount = 50;
	int qualifierCount = 50;
	int classCount = 300;
	int injectionsPerClassCount = 20;
	
	public CDIProjectGenerator() {}

	public void setWorkspaceLocation(File workspaceLocation) {
		this.workspaceLocation = workspaceLocation;
	}

	public void generate() {
		project.setName("GeneratedProject");
		createPackages();
		createTypes();
		//TODO
		project.flush(workspaceLocation);
	}

	void createPackages() {
		Set<String> pkgs = new HashSet<String>();
		
		for (int i = 0; i < packageCount; i++) {
			StringBuffer sb = new StringBuffer("org.jboss.cdi.gen");
			int j = 1;
			for (; j < 5; j++) {
				String pn = "pack" + j + "" + (seed.nextInt(3) + 1);
				sb.append(".").append(pn);
			}
			while(pkgs.contains(sb.toString())) {
				String pn = "pack" + j + "" + (seed.nextInt(3) + 1);
				sb.append(".").append(pn);
				j++;
			}
			pkgs.add(sb.toString());
		}
		
		project.setPackages(pkgs);
	}

	void createTypes() {
		GenInterface[] interfaces = new GenInterface[interfaceCount];
		for (int i = 0; i < interfaceCount; i++) {
			String name = "MyInterface" + i;
			GenInterface type = new GenInterface();
			type.setPackageName(getRandomPackage());
			type.setTypeName(name);
			project.addType(type);
			interfaces[i] = type;
		}
		GenQualifier[] qualifiers = new GenQualifier[qualifierCount];
		for (int i = 0; i < qualifierCount; i++) {
			String name = "MyQualifier" + i;
			GenQualifier type = new GenQualifier();
			type.setPackageName(getRandomPackage());
			type.setTypeName(name);
			
			project.addType(type);
			qualifiers[i] = type;
		}
		GenClass[] classes = new GenClass[classCount];
		for (int i = 0; i < classCount; i++) {
			String name = "MyBean" + i;
			GenClass type = new GenClass();
			type.setPackageName(getRandomPackage());
			type.setTypeName(name);
			GenQualifier q = qualifiers[seed.nextInt(qualifierCount)];
			type.addQualifierAnnotation(q, "qvalue" + i);
			classes[i] = type;
			
			project.addType(type);
		}
		for (int i = classes.length - 1; i > 0; i--) {
			int j = seed.nextInt(i);
			GenClass c = classes[i];
			classes[i] = classes[j];
			classes[j] = c;
		}
		for (int i = 0; i < classes.length; i++) {
			int j = seed.nextInt(classes.length);
			if(i != j && (classes[j].getExtendedType() != null || !classes[j].getImplementedTypes().isEmpty())) {
				classes[i].setExtendedType(classes[j]);
			} else {
				j = seed.nextInt(interfaceCount);
				classes[i].addImplementedType(interfaces[j]);
			}
		}
		
		//Injections
		GenAnnotation injectType = new GenAnnotation();
		injectType.setPackageName("javax.inject");
		injectType.setTypeName("Inject");
		GenAnnotationReference inject = new GenAnnotationReference();
		inject.setAnnotation(injectType);
		for (int i = 0; i < classes.length; i++) {
			for (int j = 0; j < injectionsPerClassCount; j++) {
				GenField f = new GenField();
				f.setName("f" + j);
				f.addAnnotation(inject);
				GenClass c = classes[seed.nextInt(classes.length)];
				for (GenAnnotationReference q: c.getQualifiers()) {
					f.addAnnotation(q);
				}
				while(c.getExtendedType() != null) c = c.getExtendedType();
				GenType type = c;
				if(!c.getImplementedTypes().isEmpty() && seed.nextFloat() < 0.6f) type = c.getImplementedTypes().get(0);
				f.setType(type);
				classes[i].addField(f);
			}
		}
	}

	private String getRandomPackage() {
		return project.getPackages().get(seed.nextInt(project.getPackages().size()));
	}

	private static File TEMPLATE_FOLDER;

	public static File getTemplatesFolder() throws IOException {
		if(TEMPLATE_FOLDER==null) {
			File templatesDir = null;
			if(CDIGenPlugin.getDefault() != null) {
				Bundle bundle = CDIGenPlugin.getDefault().getBundle();
				String version = bundle.getVersion().toString();
				IPath stateLocation = Platform.getStateLocation(bundle);
				templatesDir = FileLocator.getBundleFile(bundle);
				if(templatesDir.isFile()) {
					File toCopy = new File(stateLocation.toFile(),version);
					if(!toCopy.exists()) {
						toCopy.mkdirs();
						UnzipOperation unZip = new UnzipOperation(templatesDir.getAbsolutePath());
						unZip.execute(toCopy,"templates.*");
					}
					templatesDir = toCopy;
				}
			} else {
				templatesDir = new File("").getAbsoluteFile();
			}
			TEMPLATE_FOLDER = new File(templatesDir,"templates");
		}
		return TEMPLATE_FOLDER;
	}

}
