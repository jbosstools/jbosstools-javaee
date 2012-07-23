/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Red Hat, Inc.
 *******************************************************************************/
package org.jboss.tools.cdi.text.ext.hyperlink.xpl;

import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SearchPattern;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.core.ICDIElement;
import org.jboss.tools.cdi.core.util.BeanPresentationUtil;
import org.jboss.tools.cdi.text.ext.hyperlink.IInformationItem;

/**
 * Show hierarchy in light-weight control.
 *
 * @since 3.0
 */
public class HierarchyInformationControl extends org.jboss.tools.common.text.ext.hyperlink.xpl.HierarchyInformationControl {
	public HierarchyInformationControl(Shell parent, String title, int shellStyle, int tableStyle, IHyperlink[] hyperlinks) {
		super(parent, title, shellStyle, tableStyle, hyperlinks);
	}

	@Override
	protected BeanTableLabelProvider createTableLableProvider() {
		return new BeanTableLabelProvider2();
	}

	@Override
	protected String getId() {
		return "org.jboss.tools.cdi.text.ext.InformationControl";
	}

	@Override
	protected boolean select2(SearchPattern patternMatcher, Object element) {
		if (element instanceof IInformationItem) {
			String name = ((IInformationItem)element).getCDIElement().getElementName();
			if(getFilterText().getText().isEmpty()){
				patternMatcher.setPattern("*");
			}else{
				patternMatcher.setPattern(getFilterText().getText());
			}
			return patternMatcher.matches(name);
		}else
			return true;
	}

	class BeanTableLabelProvider2 extends BeanTableLabelProvider {
		@Override
		public void update(ViewerCell cell) {
			Object element = cell.getElement();
			StyledString styledString = getStyledText(element);
			cell.setText(styledString.getString());
			cell.setStyleRanges(styledString.getStyleRanges());
			cell.setImage(getImage(element));

			super.update(cell);
		}

		@Override
		public String getText(Object element) {
			return getStyledText(element).getString();
		}

		@Override
		public StyledString getStyledText(Object element) {
			StyledString sb = new StyledString();
			if(element instanceof IHyperlink){
				if(element instanceof IInformationItem){
					ICDIElement cdiElement = ((IInformationItem)element).getCDIElement();
					String name = cdiElement.getElementName();
					String location = BeanPresentationUtil.getCDIElementLocation(cdiElement, false);
					sb.append(name, NAME_STYLE);
					sb.append(location, PACKAGE_STYLE);
				}else{
					sb.append(((IHyperlink)element).getHyperlinkText(), NAME_STYLE);
				}
			}
			return sb;
		}

		@Override
		public Image getImage(Object element) {
			if(element instanceof IInformationItem){
				ICDIElement cdiElement = ((IInformationItem)element).getCDIElement();
				return CDIImages.getImageByElement(cdiElement);
			}
			return null;
		}		
	}
}