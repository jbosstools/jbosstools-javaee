/*******************************************************************************
 * Copyright (c) 2009 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.text.ext.richfaces.hyperlink;

import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.jst.text.ext.hyperlink.CSSClassHyperlinkPartitioner;

/**
 * 
 * @author Victor Rubezhny
 *
 */
public class RichfacesCSSClassHyperlinkPartitioner extends CSSClassHyperlinkPartitioner {
	public static final String RICHFACES_CSS_CLASS_PARTITION = "org.jboss.tools.common.text.ext.RICHFACES_CSS_CLASS"; //$NON-NLS-1$
	
	private static final String[] tokens = new String[]{
		"/columnClasses/",
		"/footerClass/",
		"/headerClass/",
		"/rowClasses/",
		"/captionClass/",
		"/styleClass/",
		"/errorClass/",
		"/errorMarkerClass/",
		"/fatalClass/",
		"/fatalLabelClass/",
		"/fatalMarkerClass/",
		"/infoClass/",
		"/infoLabelClass/",
		"/infoMarkerClass/",
		"/disabledClass/",
		"/enabledClass/",
		"/buttonClass/",
		"/buttonDisabledClass/",
		"/buttonInactiveClass/",
		"/bodyClass/",
		"/addButtonClass/",
		"/cleanButtonClass/",
		"/stopButtonClass/",
		"/uploadButtonClass/",
		"/uploadListClass/",
		"/fileEntryClass/",
		"/fileEntryControlClass/",
		"/dayStyleClass/",
		"/inputClass/",
		"/inputDisabledClass/",
		"/inputInactiveClass/",
		"/itemClass/",
		"/itemSelectedClass/",
		"/selectItemClass/",
		"/selectClass/",
		"/disabledItemClass/",
		"/listClass/",
		"/changedClass/",
		"/changedHoverClass/",
		"/controlClass/",
		"/controlsClass/",
		"/controlHoverClass/",
		"/controlPressedClass/",
		"/editClass/",
		"/viewClass/",
		"/viewHoverClass/",
		"/barClass/",
		"/decreaseClass/",
		"/decreaseSelectedClass/",
		"/handleClass/",
		"/handleSelectedClass/",
		"/increaseClass/",
		"/increaseSelectedClass/",
		"/tipClass/",
		"/bottomControlClass/",
		"/copyAllControlClass/",
		"/copyControlClass/",
		"/disabledControlClass/",
		"/downControlClass/",
		"/removeAllControlClass/",
		"/removeControlClass/",
		"/topControlClass/",
		"/upControlClass/",
		"/iconClass/",
		"/labelClass/",
		"/markerClass/",
		"/warnClass/",
		"/warnLabelClass/",
		"/warnMarkerClass/",
		"/sidebarClass/",
		"/contentClass/",
		"/disabledGroupClass/",
		"/disabledItemClass/",
		"/groupClass/",
		"/hoveredGroupClass/",
		"/hoveredItemClass/",
		"/topGroupClass/",
		"/topItemClass/",
		"/completeClass/",
		"/finishClass/",
		"/initialClass/",
		"/remainClass/",
		"/activeClass/",
		"/selectedClass/",
		"/entryClass/",
		"/popupClass/",
		"/selectValueClass/",
		"/activeTabClass/",
		"/disabledTabClass/",
		"/inactiveTabClass/",
		"/tabClass/",
		"/separatorClass/",
		"/highlightedClass/",
		"/nodeClass/",
		"/controlPressClass/",
		"/errorLabelClass/"
	};

	@Override
	public boolean recognize(IDocument document, IHyperlinkRegion region) {
		if (region.getAxis() != null){
			for(String token : tokens)
				if(region.getAxis().endsWith(token))
					return true;
		}
		return false;
	}

	@Override
	protected String getPartitionType(String axis) {
		return RICHFACES_CSS_CLASS_PARTITION;
	}
	
}
