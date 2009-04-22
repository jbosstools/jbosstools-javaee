package org.jboss.tools.seam.xml.components.model;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.jst.web.project.list.IWebPromptingProvider;
import org.jboss.tools.seam.xml.components.model.helpers.OpenRuleHelper;

public class SeamPromptingProvider implements IWebPromptingProvider {

	static Set<String> SUPPORTED_IDS = new HashSet<String>();
	static {
		SUPPORTED_IDS.add(JSF_OPEN_ACTION);
	}
	public boolean isSupporting(String id) {
		return id != null && SUPPORTED_IDS.contains(id);
	}

	public List<Object> getList(XModel model, String id, String prefix,
			Properties properties) {
		try {
			return getListInternal(model, id, prefix, properties);
		} catch (CoreException e) {
			if(properties != null) {
				String message = e.getMessage();
				if(message==null) {
					message = e.getClass().getName();
				}
				properties.setProperty(ERROR, message);
			}
			return EMPTY_LIST;
		}
	}
	
	private List<Object> getListInternal(XModel model, String id, String prefix, Properties properties) throws CoreException {
		String error = null;
		if(JSF_OPEN_ACTION.equals(id)) {
			IFile file = (IFile)properties.get(FILE);
			String action = prefix;
			OpenRuleHelper helper = new OpenRuleHelper();
			error = helper.run(model, file, action);
		}
		if(error != null) throw new XModelException(error);
		return EMPTY_LIST;
	}

}
