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
package org.jboss.tools.cdi.gen.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.cdi.gen.CDIProjectGenerator;
import org.jboss.tools.cdi.gen.GenEngine;
import org.jboss.tools.common.util.FileUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenProject {
	String name;
	List<String> packages = new ArrayList<String>();
	Map<String, GenType> allTypes = new HashMap<String, GenType>();
	

	public GenProject() {
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<String> getPackages() {
		return packages;
	}

	public void setPackages(Set<String> pkgs) {
		packages.addAll(pkgs);
	}

	public void addType(GenType type) {
		allTypes.put(type.getFullyQualifiedName(), type);
	}

	public void flush(File workspaceLocation) {		
		File templates;
		try {
			templates = CDIProjectGenerator.getTemplatesFolder();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		File projectLocation = new File(workspaceLocation, name);
		if(projectLocation.exists()) {
			FileUtil.clear(projectLocation);
		} else {
			projectLocation.mkdirs();
		}
		flushProjectFile(projectLocation);
		FileUtil.copyDir(new File(templates, "lib"), new File(projectLocation, "lib"), true);
		FileUtil.copyFile(new File(templates, ".classpath"), new File(projectLocation, ".classpath"));
		new File(projectLocation, "src").mkdirs();

		for (GenType type: allTypes.values()) {
			String path = "src/" + type.getPackageName().replace('.', '/') + "/" + type.getTypeName() + ".java";
			File f = new File(projectLocation, path);
			f.getParentFile().mkdirs();
			if(type instanceof GenInterface) {
				String content = "package " + type.getPackageName() + ";\n";
				content += "public interface " + type.getTypeName() + " {\n" + "}";
				FileUtil.writeFile(f, content);
			} else if(type instanceof GenQualifier) {
				flushQualifier(f, templates, (GenQualifier)type);
			} else if(type instanceof GenClass) {
				StringBuilder content = new StringBuilder();
				((GenClass)type).flush(content);
				FileUtil.writeFile(f, content.toString());
			}
		}
		//TODO
	}

	void flushProjectFile(File projectLocation) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("projectName", getName());
		try {
			GenEngine.getInstance().executeTemplate(CDIProjectGenerator.getTemplatesFolder(), new File(CDIProjectGenerator.getTemplatesFolder(), ".project"), new File(projectLocation, ".project"), parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void flushQualifier(File targetFile, File templates, GenQualifier type) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("type", type);
		try {
			GenEngine.getInstance().executeTemplate(templates, new File(templates, "src/Q.java"), targetFile, parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
