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
package org.jboss.tools.seam.core.test;

import java.io.File;
import java.io.InputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.common.util.FileUtil;

public class SeamBigProjectGenerator {
	int classesCount = 1000;
	int filesInFolderLimit = 6;
	int folderDepth = 6;
	
	public void generate(IResource resource, InputStream templateFile) {
		File root = resource.getLocation().toFile();
		String pack = root.getName();
		if(templateFile == null) {
			throw new IllegalArgumentException("No template file found.");
		}
		String text = FileUtil.readStream(templateFile);
		
		int depth = 0;
		int i = 0;
		while(i < classesCount) {
			while(getFolderCount(root) >= filesInFolderLimit || (getFileCount(root) >= filesInFolderLimit && depth == folderDepth) || depth > folderDepth) {
				root = root.getParentFile();
				int q = pack.lastIndexOf('.');
				if(q >= 0) pack = pack.substring(0, q);
				depth--;
			}
			i++;
			if(getFileCount(root) >= filesInFolderLimit) {
				root = new File(root, "p" + i);
				root.mkdirs();
				pack += "." + root.getName();
				depth++;
			}
			File f = new File(root, "T" + i + ".java");

			FileUtil.writeFile(f, replace(text, i, pack));
			
		}
		
		try {
			resource.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	String replace(String text, int n, String pack) {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while(true) {
			int jb = text.indexOf('%', i);
			if(jb < 0) break;
			int je = text.indexOf('%', jb + 1);
			if(je < 0) break;
			sb.append(text.substring(i, jb));
			String r = text.substring(jb + 1, je);
			if(r.equals("p")) {
				r = pack;
			} else if(r.endsWith("#")) {
				r = r.substring(0, r.length() - 1) + (int)(10 * Math.random());
			} else {
				r += "" + n;
			}
			sb.append(r);
			i = je + 1;
		}
		
		if(i < text.length() && i >= 0) sb.append(text.substring(i));
		
		return sb.toString();
	}
	
	int getFolderCount(File f) {
		File[] fs = f.listFiles();
		int c = 0;
		for (int i = 0; i < fs.length; i++) {
			if(fs[i].isDirectory()) c++;
		}
		return c;
	}

	int getFileCount(File f) {
		File[] fs = f.listFiles();
		int c = 0;
		for (int i = 0; i < fs.length; i++) {
			if(fs[i].isFile()) c++;
		}
		return c;
	}

}
