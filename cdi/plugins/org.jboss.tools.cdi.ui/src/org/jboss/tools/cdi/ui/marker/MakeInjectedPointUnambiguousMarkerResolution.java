/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.marker;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.internal.core.refactoring.AddQualifiersToBeanProcessor;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.wizard.AddQualifiersToBeanWizard;
import org.jboss.tools.common.quickfix.IQuickFix;
import org.jboss.tools.common.refactoring.TestableResolutionWithDialog;

/**
 * @author Daniel Azarov
 */
public class MakeInjectedPointUnambiguousMarkerResolution implements IQuickFix, TestableResolutionWithDialog {
	private String label;
	private IInjectionPoint injectionPoint;
	private List<IBean> beans;
	private IBean selectedBean;

	public MakeInjectedPointUnambiguousMarkerResolution(IInjectionPoint injectionPoint, List<IBean> beans, int index){
		this.injectionPoint = injectionPoint;
		this.beans = beans;
		this.selectedBean = beans.get(index);
		this.label = MessageFormat.format(CDIUIMessages.MAKE_INJECTED_POINT_UNAMBIGUOUS_TITLE, new Object[]{selectedBean.getElementName()});
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void runForTest(IMarker marker){
		internal_run(true);
	}

	@Override
	public void run(IMarker marker) {
		internal_run(false);
	}

	private void internal_run(boolean test) {
		AddQualifiersToBeanProcessor processor = new AddQualifiersToBeanProcessor(label, injectionPoint, beans, selectedBean);
		ProcessorBasedRefactoring refactoring = new ProcessorBasedRefactoring(processor);
		AddQualifiersToBeanWizard wizard = new AddQualifiersToBeanWizard(refactoring);
		wizard.showWizard();
	}

	@Override
	public String getDescription() {
		return label;
	}

	@Override
	public Image getImage() {
		return CDIImages.getImage(CDIImages.QUICKFIX_EDIT);
	}

	@Override
	public int getRelevance() {
		return 100;
	}

	@Override
	public void apply(IDocument document) {
		internal_run(false);
	}

	@Override
	public Point getSelection(IDocument document) {
		return null;
	}

	@Override
	public String getAdditionalProposalInfo() {
		return label;
	}

	@Override
	public String getDisplayString() {
		return label;
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}
}