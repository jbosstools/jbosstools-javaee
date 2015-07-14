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

import java.util.Collection;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.services.ContentProposal;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.forms.PropertyEditorActionHandler;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.jboss.tools.batch.ui.editor.internal.services.contentproposal.RefProposalService;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BrowseRefActionHandler extends PropertyEditorActionHandler {

	@Override
	protected Object run(Presentation context) {
        Value<?> property = (Value<?>)property();
        RefProposalService service = property().service(RefProposalService.class);
        final SapphirePart part = context.part();

        Collection<ContentProposal> valuesList = service.session().proposals();
        ContentProposal[] valuesArray = valuesList.toArray(new ContentProposal[valuesList.size()]);
            
        ILabelProvider labelProvider = new ILabelProvider() {
				@Override
				public void addListener(ILabelProviderListener listener) {
				}

				@Override
				public void dispose() {
				}

				@Override
				public boolean isLabelProperty(Object element, String property) {
					return false;
				}

				@Override
				public void removeListener(ILabelProviderListener listener) {
				}

				@Override
				public Image getImage(Object element) {
			        Image image = null;			        
			        ImageData imageData = ((ContentProposal)element).image();
			        if(imageData != null) {
			            image = part.getSwtResourceCache().image(imageData);
			        }			        
			        return image;
				}

				@Override
				public String getText(Object element) {
					return ((ContentProposal)element).content();
				}            	
            };
            
        ElementListSelectionDialog dialog = new ElementListSelectionDialog(((FormComponentPresentation)context ).shell(), labelProvider);
            
        dialog.setElements( valuesArray );
        dialog.setMultipleSelection( false );
        dialog.setHelpAvailable( false );
        dialog.setTitle(property.definition().getLabel(false, CapitalizationType.TITLE_STYLE, false));
        dialog.setMessage("Select artifact");
            
        dialog.open();
            
        Object[] result = dialog.getResult();
            
        if(result != null && result.length == 1) {
        	property.write(((ContentProposal)result[0]).content(), true);
        }
        
        return null;
	}
}
