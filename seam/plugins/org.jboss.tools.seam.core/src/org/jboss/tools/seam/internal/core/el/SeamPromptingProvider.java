/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core.el;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.common.el.core.resolver.SimpleELContext;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.project.IPromptingProvider;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;

public class SeamPromptingProvider implements IPromptingProvider {
	static String IS_SEAM_PROJECT = "seam.is_seam_project"; //$NON-NLS-1$
	public static String VARIABLES = "seam.variables"; //$NON-NLS-1$
	public static String MEMBERS = "seam.members"; //$NON-NLS-1$

	SeamELCompletionEngine engine = new SeamELCompletionEngine();

	public SeamPromptingProvider() {}

	public List getList(XModel model, String id, String prefix,
			Properties properties) {
		IFile f = (IFile)properties.get("file"); //$NON-NLS-1$
		ISeamProject p = (f == null) ? null : SeamCorePlugin.getSeamProject(f.getProject(), false);
		if(f == null) {
			p = (ISeamProject)properties.get("seamProject"); //$NON-NLS-1$
		}
		if(p == null) return null;
		if(IS_SEAM_PROJECT.equals(id)) {
			ArrayList<Object> list = new ArrayList<Object>();
			list.add(p);
			return list;
		} else if(VARIABLES.equals(id)) {
			p.resolve();
			Set<ISeamContextVariable> vs = p.getVariables(true);
			Set<Object> set = new TreeSet<Object>();
			for (ISeamContextVariable v : vs) {
				set.add(v.getName());
			}
			ArrayList<Object> list = new ArrayList<Object>();
			list.addAll(set);
			return list;
		} else if(MEMBERS.equals(id)) {
			SimpleELContext context = new SimpleELContext();
			context.setResource(f);
			List<TextProposal> proposals = engine.getProposals(context, prefix);
			List<String> suggestions = new ArrayList<String>();
			if(proposals != null) for (TextProposal proposal: proposals) {
				suggestions.add(proposal.getReplacementString());
			}
			return suggestions;
		}
		return null;
	}

	public boolean isSupporting(String id) {
		return id != null && id.startsWith("seam."); //$NON-NLS-1$
	}
}