/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.services.contentproposal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.LoggingService;
import org.eclipse.sapphire.Sapphire;
import org.eclipse.sapphire.services.ContentProposal;
import org.eclipse.sapphire.services.ContentProposalService;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.ui.editor.internal.model.Job;
import org.jboss.tools.batch.ui.editor.internal.model.RefAttributeElement;
import org.jboss.tools.batch.ui.editor.internal.util.ModelToBatchArtifactsMapping;

/**
 * Abstract parent for all Sapphire content proposal services for the Batch
 * editor. Handlers the technical stuff so that implementations can just declare
 * desired type of content and its presentation.
 * 
 * @author Tomas Milata
 */
public class RefProposalService extends ContentProposalService {

	private IBatchProject batchProject;
	private Class<? extends RefAttributeElement> elementClass;
	private ImageData image;

	/**
	 * Prepares the instance of the project with batch nature to be used for
	 * queries and calls parent.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void init() {
		super.init();

		IProject project = context(Job.class).adapt(IProject.class);
		batchProject = BatchCorePlugin.getBatchProject(project, true);

		ElementType type = context(Element.class).type();
		image = type.image();

		Class<?> c = type.getModelElementClass();
		if (!RefAttributeElement.class.isAssignableFrom(c)) {
			String msg = String.format("Incorrect model element type %s. %s is only applicable to subtypes of %s.",
					c.getName(), RefProposalService.class.getSimpleName(), RefAttributeElement.class.getName());
			Sapphire.service(LoggingService.class).logError(msg);
		}
		elementClass = (Class<? extends RefAttributeElement>) c;

	}

	/**
	 * The list of content proposals returned by the session object is a list of
	 * artifacts specified by the
	 * {@link ModelToBatchArtifactsMapping#getBatchArtifactTypes(Class)} method
	 * that contain a value of the current filter in their name as a substring.
	 * 
	 * Image is taken from the context element.
	 * 
	 * @return a new session object
	 */
	@Override
	public Session session() {
		return new Session() {

			@Override
			protected List<ContentProposal> compute() {
				List<ContentProposal> proposals = new ArrayList<>();
				if(batchProject == null) {
					return proposals;
				}
				for (BatchArtifactType type : ModelToBatchArtifactsMapping.getBatchArtifactTypes(elementClass)) {
					Collection<IBatchArtifact> artifacts = batchProject.getArtifacts(type);
					for (IBatchArtifact artifact : artifacts) {
						if (artifact.getName().contains(filter())) {
							proposals.add(new ContentProposal(artifact.getName(), null, null, image));
						}
					}
				}
				return proposals;
			}
		};
	}
}
