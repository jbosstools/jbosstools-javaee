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
package org.jboss.tools.batch.ui.editor.internal.model;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.core.BatchProjectChangeEvent;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.core.IBatchProjectChangeListener;
import org.jboss.tools.batch.internal.core.validation.BatchValidator;
import org.jboss.tools.batch.ui.editor.internal.util.ModelToBatchArtifactsMapping;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class RefValidationService extends ValidationService {
	private Property property;
	private Listener listener;
	private IBatchProject batchProject;
	BatchArtifactType type = null;
	boolean isChunkListener = false;

	@Override
	@SuppressWarnings("unchecked")
	protected void initValidationService() {
		property = context(Property.class);
		IProject project = context(Job.class).adapt(IProject.class);
		batchProject = BatchCorePlugin.getBatchProject(project, true);
		listener = new Listener() {
			@Override
			public void handle( final Event event ) {
				refresh();
			}
		};
		attach(listener);
		if(batchProject != null) {
			Listeners.getListener(batchProject).add(this);
		}
		Class<? extends RefAttributeElement> cls = (Class<? extends RefAttributeElement>) property.element().type().getModelElementClass();
		List<BatchArtifactType> types = ModelToBatchArtifactsMapping.getBatchArtifactTypes(cls);
		if(types.size() == 1) {
			type = types.get(0);
		} else if(types.size() > 1) {
			type = BatchArtifactType.STEP_LISTENER;
			Step e = (Step)property.element().parent().element();
			ElementList<BatchletOrChunk> ch = e.getBatchletOrChunk();
			if(ch.isEmpty() || ch.get(0) instanceof Chunk) {
				isChunkListener = true;
			}			
		}
	}

	@Override
	protected Status compute() {
		if(batchProject == null) {
			return Status.createOkStatus();
		}
		String message = null;
		if(property instanceof Value<?>) {
			Object c = ((Value<?>)property).content();
			if(c != null) {
				String ref = c.toString();
				Collection<IBatchArtifact> as = batchProject.getArtifacts(ref);
				if(as.isEmpty()) {
					message = type == null ? "" : BatchValidator.TypeToValidationMessage.getNotFoundMessage(type);
					message = MessageFormat.format(message, ref);
				} else if(type != null) {
					IBatchArtifact a = as.iterator().next();
					if(isChunkListener) {
						if(!type.getTag().equals(a.getArtifactType().getTag())) {
							message = BatchValidator.TypeToValidationMessage.getWrongTypeMessage(type);
							message = MessageFormat.format(message, ref);
						}
					} else if(!type.equals(a.getArtifactType())) {
						message = BatchValidator.TypeToValidationMessage.getWrongTypeMessage(type);
					}
				}
			}
		}
		
		return message == null ? Status.createOkStatus() : Status.createErrorStatus(message);
	}

	@Override
	public void dispose() {
		super.dispose();
		if(this.listener != null) {
			detach(this.listener);
		}
		if(batchProject != null) {
			Listeners.getListener(batchProject).remove(this);
		}
	}

	static class Listeners {
		static Map<String, BatchChangeListener> listeners = new HashMap<String, BatchChangeListener>();
		
		static synchronized BatchChangeListener getListener(IBatchProject batchProject) {
			String name = batchProject.getProject().getName();
			BatchChangeListener listener = listeners.get(name);
			if(listener == null) {
				listener = new BatchChangeListener();
				listeners.put(name, listener);
				batchProject.addBatchProjectListener(listener);
			}
			return listener;
		}
		
	}

	static class BatchChangeListener implements IBatchProjectChangeListener {
		Set<RefValidationService> refs = new HashSet<RefValidationService>();

		synchronized void add(RefValidationService ref) {
			refs.add(ref);
		}

		synchronized void remove(RefValidationService ref) {
			refs.remove(ref);
		}

		synchronized RefValidationService[] getRefs() {
			return refs.toArray(new RefValidationService[refs.size()]);
		}

		@Override
		public void projectChanged(BatchProjectChangeEvent event) {
			for (RefValidationService ref: getRefs()) {
				ref.refresh();
			}			
		}
		
	}
}
