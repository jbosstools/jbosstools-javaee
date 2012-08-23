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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.core.runtime.Platform;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenEngine {

	static GenEngine instance = new GenEngine();

	public static GenEngine getInstance() {
		return instance;
	}

	public void executeTemplate(File sourceDir, File sourceFile, File targetFile, Map<String, Object> parameters) throws Exception {
		if(!sourceFile.exists()) return;
		
		ClassLoader c = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

		try {

		Properties properties = new Properties();
		properties.put("file.resource.loader.path", sourceDir.getCanonicalPath()); //$NON-NLS-1$
		String logFileName = Platform.getLocation().append(".metadata").append(".plugins").append(CDIGenPlugin.PLUGIN_ID).append("velocity.log").toFile().getAbsolutePath(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		properties.put("runtime.log", logFileName); //$NON-NLS-1$

		Velocity.init(properties);

		VelocityContext context = new VelocityContext(parameters);

		File file = targetFile;
		if (file.exists()) {
			if (!file.delete()) {
				throw new RuntimeException("Unable to delete file "+file.getAbsolutePath()); //$NON-NLS-1$
			}
		}
		File folder = file.getParentFile();
		folder.mkdirs();
		if (!folder.exists() || !folder.isDirectory()) {
			throw new RuntimeException("Unable to create folder "+folder.getAbsolutePath()); //$NON-NLS-1$
		}
		Writer writer = new BufferedWriter(new FileWriter(file));
		Reader reader = new BufferedReader(new FileReader(sourceFile));

		Velocity.evaluate(context, writer, "", reader); //$NON-NLS-1$

		writer.flush();
		writer.close();

		} finally {
			Thread.currentThread().setContextClassLoader(c);
		}
	}
}
