/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.ui.BatchUIPlugin;
import org.jboss.tools.batch.ui.editor.internal.model.Batchlet;
import org.jboss.tools.batch.ui.editor.internal.model.BatchletOrChunk;
import org.jboss.tools.batch.ui.editor.internal.model.RefAttributeElement;
import org.jboss.tools.batch.ui.editor.internal.model.Step;
import org.jboss.tools.batch.ui.editor.internal.util.ModelToBatchArtifactsMapping;
import org.jboss.tools.batch.ui.internal.wizard.BatchFieldEditorFactory;
import org.jboss.tools.batch.ui.internal.wizard.NewBatchArtifactDialog;
import org.jboss.tools.batch.ui.internal.wizard.WizardMessages;
import org.jboss.tools.common.EclipseUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class OpenOrCreateArtifactActionDelegate implements Runnable {
	private SapphirePart part;
	private IBatchProject batchProject;
	private RefAttributeElement refElement;
	private String ref;
	private List<BatchArtifactType> types;
	private String artifactTypeName = null;

	public OpenOrCreateArtifactActionDelegate(SapphirePart part) {
		this.part = part;
		init();
	}

	@SuppressWarnings("unchecked")
	public void init() {
		Element element = part.getLocalModelElement();
		IProject project = (IProject)element.resource().adapt(IProject.class);
		if(!(element instanceof RefAttributeElement) || project == null) {
			return;
		}
		batchProject = BatchCorePlugin.getBatchProject(project, true);
		if(batchProject == null) {
			return;
		}
		refElement = (RefAttributeElement)element;
		ref = refElement.getRef().content();

		types = ModelToBatchArtifactsMapping.getBatchArtifactTypes((Class<? extends RefAttributeElement>)((Element)refElement).type().getModelElementClass());
		if(types.size() > 1) {
			Step e = (Step)((Element)refElement).parent().element();
			ElementList<BatchletOrChunk> ch = e.getBatchletOrChunk();
			if(!ch.isEmpty() && ch.get(0) instanceof Batchlet) {
				types = new ArrayList<BatchArtifactType>();
				types.add(BatchArtifactType.STEP_LISTENER);
			}
		}

		if(types.size() == 1) {
			artifactTypeName = BatchFieldEditorFactory.getArtifactLabel(types.get(0));
		} else {
			artifactTypeName = WizardMessages.aChunkStepListenerLabel;
		}

		if(artifactTypeName != null) {
			artifactTypeName = artifactTypeName.toLowerCase();
		}
	}

	public SapphirePart getPart() {
		return part;
	}

	public IBatchProject getBatchProject() {
		return batchProject;
	}

	public String getActionLabel() {
		if(batchProject == null) {
			return "";
		}
		Collection<IBatchArtifact> artifacts = batchProject.getArtifacts(ref);
		return artifacts.isEmpty() ? NLS.bind(WizardMessages.actionCreateArtifact, artifactTypeName) :
									 NLS.bind(WizardMessages.actionOpenArtifact, artifactTypeName);
	}

	@SuppressWarnings("restriction")
	public void run() {
		Collection<IBatchArtifact> artifacts = batchProject.getArtifacts(ref);
		if(artifacts.isEmpty()) {
			IWorkbench workbench = BatchUIPlugin.getDefault().getWorkbench();
			NewBatchArtifactDialog dialog = new NewBatchArtifactDialog(workbench.getActiveWorkbenchWindow().getShell());
			if(types.size() > 0) {
				dialog.setTypes(types);
			}
			IResource[] rs = EclipseUtil.getJavaSourceRoots(batchProject.getProject());
			if(rs.length == 0) {
				return;
			}
			StructuredSelection selection = new StructuredSelection(rs[0]);
			
			dialog.init(workbench, selection);
			String typeName = null;
			if(ref != null) {
				if(ref.length() > 0 && Character.isJavaIdentifierStart(ref.charAt(0))) {
					typeName = ref.substring(0, 1).toUpperCase() + ref.substring(1);
				}
			}

			int result = dialog.open(batchProject, ref, ref.length() == 0, types, types.size() > 1, typeName);
			if(result == WizardDialog.OK) {
				String newRef = dialog.getBatchPage().getArtifactName();
				if(ref != null && !ref.equals(newRef)) {
					refElement.setRef(newRef);
				} else {
					refElement.setRef(""); //to refresh
					refElement.setRef(newRef);
				}
			}
		} else {
			IBatchArtifact a = artifacts.iterator().next();
			try {
				JavaUI.openInEditor(a.getType());
			} catch (PartInitException e) {
				BatchCorePlugin.pluginLog().logError(e);
			} catch (JavaModelException e) {
				BatchCorePlugin.pluginLog().logError(e);
			}
		}
	}

	public void dispose() {
		batchProject = null;
		part = null;
		refElement = null;
	}

}
