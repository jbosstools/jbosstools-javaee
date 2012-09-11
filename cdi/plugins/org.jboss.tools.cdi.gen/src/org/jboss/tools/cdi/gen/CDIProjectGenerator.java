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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.gen.model.GenAnnotation;
import org.jboss.tools.cdi.gen.model.GenAnnotationReference;
import org.jboss.tools.cdi.gen.model.GenClass;
import org.jboss.tools.cdi.gen.model.GenField;
import org.jboss.tools.cdi.gen.model.GenInterface;
import org.jboss.tools.cdi.gen.model.GenMember;
import org.jboss.tools.cdi.gen.model.GenMethod;
import org.jboss.tools.cdi.gen.model.GenProject;
import org.jboss.tools.cdi.gen.model.GenQualifier;
import org.jboss.tools.cdi.gen.model.GenType;
import org.jboss.tools.cdi.gen.model.GenVariable;
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
	int classCount = 500;
	int producersPerClass = 3;
	int fieldInjectionsPerClassCount = 20;
	int initMethodsPerClass = 3;
	int paramsPerInitMethod = 2;
	int elInstancesPerClass = 20;
	
	public CDIProjectGenerator() {}

	public void setWorkspaceLocation(File workspaceLocation) {
		this.workspaceLocation = workspaceLocation;
	}

	public void generate(String projectName) {
		project.setName(projectName);
		createPackages();
		createTypes();
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
		// void type
		GenClass voidType = new GenClass();
		voidType.setFullyQualifiedName("void");
		// String type
		GenClass string = new GenClass();
		string.setFullyQualifiedName("java.lang.String");

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

		List<String> beanNames = new ArrayList<String>();

		//@Named type
		GenQualifier named = new GenQualifier();
		named.setName("Named");
		named.setPackageName("javax.inject");

		//Classes
		GenClass[] classes = new GenClass[classCount];
		for (int i = 0; i < classCount; i++) {
			String name = "MyBean" + i;
			GenClass type = new GenClass();
			type.setPackageName(getRandomPackage());
			type.setTypeName(name);
			GenQualifier q = qualifiers[seed.nextInt(qualifierCount)];
			type.addQualifierAnnotation(q, "qvalue" + i);

			String beanName = "bean" + (beanNames.size() + 1);
			type.addQualifierAnnotation(named, beanName);
			beanNames.add(beanName);

			classes[i] = type;
			project.addType(type);
		}
		//Mix classes array
		for (int i = classes.length - 1; i > 0; i--) {
			int j = seed.nextInt(i);
			GenClass c = classes[i];
			classes[i] = classes[j];
			classes[j] = c;
		}
		//Generate inheritance
		for (int i = 0; i < classes.length; i++) {
			int j = seed.nextInt(classes.length);
			if(i != j && (classes[j].getExtendedType() != null || !classes[j].getImplementedTypes().isEmpty())) {
				classes[i].setExtendedType(classes[j]);
			} else {
				j = seed.nextInt(interfaceCount);
				classes[i].addImplementedType(interfaces[j]);
			}
		}

		GenAnnotation disposeType = new GenAnnotation();
		disposeType.setFullyQualifiedName(CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME);
		GenAnnotationReference dispose = new GenAnnotationReference();
		dispose.setAnnotation(disposeType);

		//Producers
		List<GenMethod> producers = new ArrayList<GenMethod>();
		GenAnnotation producesType = new GenAnnotation();
		producesType.setFullyQualifiedName(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME);
		GenAnnotationReference produces = new GenAnnotationReference();
		produces.setAnnotation(producesType);
		for (int i = 0; i < classes.length; i++) {
			for (int j = 0; j < producersPerClass; j++) {
				GenMethod producer = new GenMethod();
				producer.addAnnotation(produces);
				GenClass c = classes[seed.nextInt(classes.length)];
				producer.setReturnType(c);
				producer.setName("produceC" + i + "M" + j);
				GenQualifier q = qualifiers[seed.nextInt(qualifierCount)];
				producer.addQualifierAnnotation(q, "qpvalue" + i + "_" + j);
			
				classes[i].addMethod(producer);
				producers.add(producer);
				
				//Disposer
				GenMethod disposer = new GenMethod();
				disposer.setReturnType(voidType);
				disposer.setName("disposeC" + i + "M" + j);
				GenVariable v = new GenVariable();
				v.setName("p0");
				v.setType(getRandomSuperType(c, 0.6f));
				v.addAnnotation(dispose);
				v.addQualifierAnnotation(q, "qpvalue" + i + "_" + j);
				disposer.addParameter(v);
				classes[i].addMethod(disposer);
			}
		}
		
		//Injections
		GenAnnotation injectType = new GenAnnotation();
		injectType.setFullyQualifiedName(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
		GenAnnotationReference inject = new GenAnnotationReference();
		inject.setAnnotation(injectType);

		for (int i = 0; i < classes.length; i++) {
			for (int j = 0; j < fieldInjectionsPerClassCount; j++) {
				GenField f = new GenField();
				f.setName("f" + j);
				f.addAnnotation(inject);
				GenMember beanMember = getRandomBeanMember(classes, producers);
				for (GenAnnotationReference q: beanMember.getQualifiers()) {
					f.addAnnotation(q);
				}
				GenType type = getRandomSuperType(beanMember.getType(), 0.6f);
				f.setType(type);
				classes[i].addField(f);
			}
			// getName();
			GenMethod nameProperty = new GenMethod();
			nameProperty.setReturnType(string);
			nameProperty.setName("getName");
			classes[i].addMethod(nameProperty);

			// getSelf();
			GenMethod selfProperty = new GenMethod();
			selfProperty.setReturnType(classes[i]);
			selfProperty.setName("getSelf");
			classes[i].addMethod(selfProperty);
			
			//initializers
			for (int j = 0; j < initMethodsPerClass; j++) {
				GenMethod m = new GenMethod();
				m.addAnnotation(inject);
				m.setReturnType(voidType);
				m.setName("initC" + i + "M" + j);
				for (int k = 0; k < paramsPerInitMethod; k++) {
					GenVariable v = new GenVariable();
					v.setName("p" + k);
					GenMember beanMember = getRandomBeanMember(classes, producers);
					for (GenAnnotationReference q: beanMember.getQualifiers()) {
						v.addAnnotation(q);
					}
					GenType type = getRandomSuperType(beanMember.getType(), 0.6f);
					v.setType(type);
					m.addParameter(v);
				}
				
				classes[i].addMethod(m);
			}
		}
	
		//EL
		for (int i = 0; i < classes.length; i++) {
			for (int k = 0; k < elInstancesPerClass; k++) {
				GenField f = new GenField();
				f.setName("el" + k);
				f.setType(string);
				String beanName = beanNames.get(seed.nextInt(beanNames.size()));
				f.setInitValue("\"#{" + beanName + ".self.self.name}\"");
				classes[i].addField(f);
			}
		}
	}

	private String getRandomPackage() {
		return project.getPackages().get(seed.nextInt(project.getPackages().size()));
	}

	private GenMember getRandomBeanMember(GenClass[] classes, List<GenMethod> producers) {
		if(seed.nextFloat() < 0.6f) {
			return classes[seed.nextInt(classes.length)];
		} else {
			return producers.get(seed.nextInt(producers.size()));
		}
	}

	private GenType getRandomSuperType(GenType type, float level) {
		if(type instanceof GenClass) {
			GenClass c = (GenClass)type;
			while(c.getExtendedType() != null) c = c.getExtendedType();
			type = c;
		}
		if(seed.nextFloat() < level) {
			Collection<GenInterface> is = type.getImplementedTypes();
			if(!is.isEmpty()) {
				type = is.iterator().next();
			}
		}		
		return type;
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
