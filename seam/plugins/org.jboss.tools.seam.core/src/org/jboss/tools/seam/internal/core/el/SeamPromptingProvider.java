package org.jboss.tools.seam.internal.core.el;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.project.IPromptingProvider;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;

public class SeamPromptingProvider implements IPromptingProvider {
	static String IS_SEAM_PROJECT = "seam.is_seam_project";
	static String VARIABLES = "seam.variables";
	static String MEMBERS = "seam.members";
	
	public SeamPromptingProvider() {}

	public List getList(XModel model, String id, String prefix,
			Properties properties) {
		IFile f = (IFile)properties.get("file");
		ISeamProject p = (f == null) ? null : SeamCorePlugin.getSeamProject(f.getProject(), false);
		if(p == null) return null;
		if(IS_SEAM_PROJECT.equals(id)) {
			ArrayList<Object> list = new ArrayList<Object>();
			list.add(p);
			return list;
		} else if(VARIABLES.equals(id)) {
			p.resolve();
			Set<ISeamContextVariable> vs = p.getVariables();
			Set<Object> set = new TreeSet<Object>();
			for (ISeamContextVariable v : vs) {
				set.add(v.getName());
			}
			ArrayList<Object> list = new ArrayList<Object>();
			list.addAll(set);
			return list;
		} else if(MEMBERS.equals(id)) {
			
		}
		return null;
	}

	public boolean isSupporting(String id) {
		return id != null && id.startsWith("seam.");
	}

}
