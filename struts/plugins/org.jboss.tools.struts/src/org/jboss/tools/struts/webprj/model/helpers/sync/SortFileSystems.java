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
package org.jboss.tools.struts.webprj.model.helpers.sync;

import java.util.*;
import org.jboss.tools.common.meta.action.SpecialWizard;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.XFileObject;
import org.jboss.tools.common.model.filesystems.impl.FileSystemsImpl;
import org.jboss.tools.jst.web.project.WebModuleConstants;

public class SortFileSystems implements SpecialWizard, WebModuleConstants {
	static int WEB_INF_WEIGHT = 8192;
	static int CONFIG_WEIGHT = 4096;
	static int WEB_ROOT_WEIGHT = 2048;
	static int MODULE_ROOT_WEIGHT = 1024;
	static int SRC_WEIGHT = 512;
	static int CLASSES_WEIGHT = 256;
	static int FOLDER_WEIGHT = 128;
	
	protected XModel model;
	XModelObject[] fs;
	int[] weight;
	
	public static void sort(XModel model) {
		SortFileSystems sorter = new SortFileSystems();
		sorter.setModel(model);
		sorter.sort();
	}

	public void setObject(Object object) {
		setModel((XModel)object);
	}
	
	public int execute() {
		sort();		
		return 0;
	}
	
	public void setModel(XModel model) {
		this.model = model;
	}
	
	public void sort() {
		if(model.getByPath("FileSystems") == null) return;
		fs = model.getByPath("FileSystems").getChildren();
		weight = new int[fs.length];
		for (int i = 0; i < fs.length; i++) {
			weight[i] = 50 - i;
			if("FileSystemFolder".equals(fs[i].getModelEntity().getName())) {
				weight[i] += FOLDER_WEIGHT;
				String n = fs[i].getAttributeValue("name");
				if(n.toLowerCase().indexOf("class") >= 0) weight[i] += CLASSES_WEIGHT;
			}
			 
		} 
		XModelObject[] ws = model.getByPath("Web").getChildren(WebModuleConstants.ENTITY_WEB_MODULE);
		for (int i = 0; i < ws.length; i++) {
			String nm = ws[i].getAttributeValue(ATTR_NAME);
			boolean isDefault = nm.length() == 0;
			String root = ws[i].getAttributeValue(ATTR_ROOT_FS);
			int q = getFileSystemIndex(root);
			if(q >= 0) if(isDefault) weight[q] |= WEB_ROOT_WEIGHT; else weight[q] |= MODULE_ROOT_WEIGHT;			
			String cgpath = ws[i].getAttributeValue(ATTR_MODEL_PATH);
			q = getConfigFileSystemIndex(cgpath);
			if(q >= 0) {
				if(isDefault) weight[q] |= WEB_INF_WEIGHT; else weight[q] |= CONFIG_WEIGHT;			
			}
			String src = ws[i].getAttributeValue(ATTR_SRC_FS);
			q = getFileSystemIndex(src);
			if(q >= 0) weight[q] |= SRC_WEIGHT;			
		}
		if(isUpToDate()) return;
		FileSystemsImpl fsi = (FileSystemsImpl)model.getByPath("FileSystems");
		if(fsi != null) fsi.sort(new ComparatorImpl());
	}
	
	private boolean isUpToDate() {
		XModelObject[] fs2 = (XModelObject[])fs.clone();
		Comparator<XModelObject> c = new ComparatorImpl();
		Arrays.sort(fs2, c);
		for (int i = 0; i < fs2.length; i++) 
			if(fs2[i] != fs[i]) return false;		
		return true;		
	}
	
	private int getFileSystemIndex(String name) {
		for (int i = 0; i < fs.length; i++) 
			if(fs[i].getAttributeValue("name").equals(name)) return i;
		return -1;
	}
	
	private int getFileSystemIndex(XModelObject s) {
		for (int i = 0; i < fs.length; i++)	if(fs[i] == s) return i;
		return -1;
	}
	
	private int getConfigFileSystemIndex(String path) {
		XModelObject o = model.getByPath(path);
		while (o != null && o.getFileType() != XFileObject.SYSTEM) o = o.getParent();
		return (o == null) ? -1 : getFileSystemIndex(o);
	}
	
	class ComparatorImpl implements Comparator<XModelObject> {

		public int compare(XModelObject o1, XModelObject o2) {
			int i1 = getFileSystemIndex(o1);
			int i2 = getFileSystemIndex(o2);
			if(i1 < 0 || i2 < 0) return 0;
			return (weight[i2] - weight[i1]);
		}
		
	}
	
}
