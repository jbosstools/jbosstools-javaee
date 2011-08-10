/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.internal.core.el.CdiElResolver;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.wizard.OpenCDINamedBeanDialog;
import org.jboss.tools.cdi.ui.wizard.OpenCDINamedBeanDialog.CDINamedBeanWrapper;
import org.jboss.tools.common.el.core.resolver.ELContextImpl;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.el.core.resolver.IOpenableReference;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.common.text.ext.util.StructuredSelectionHelper;

/**
 * Open CDI Named Bean Dialog Handler
 * 
 * @author Victor V. Rubezhny
 */
@SuppressWarnings("restriction")
public class OpenCDINamedBeanHandler extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell parent= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		SelectionDialog dialog;
		
		dialog= new OpenCDINamedBeanDialog(parent);
		
		dialog.setTitle(CDIUIMessages.OPEN_CDI_NAMED_BEAN_ACTION_NAME);
		dialog.setMessage(CDIUIMessages.OPEN_CDI_NAMED_BEAN_ACTION_MESSAGE);

		int result= dialog.open();
		if (result != IDialogConstants.OK_ID)
			return null;

		Object[] resultObjects = dialog.getResult();
		for (Object resultObject : resultObjects) {
			CDINamedBeanWrapper wrapper= (CDINamedBeanWrapper) resultObject;
			IBean bean = wrapper.getBean();
			IProject project = bean.getCDIProject().getNature().getProject();
			ELContextImpl elContext = new ELContextImpl();
			elContext.setResource(project.getFile(".project"));
			
			CdiElResolver resolver = new CdiElResolver();
			ELResolution resolution = resolver.resolve(elContext, resolver.parseOperand("${" + bean.getName() + "}"), 0);
			if (resolution.isResolved() && resolution.getNumberOfResolvedSegments() == 1) {
				ELSegment segment = resolution.getLastSegment();
				if (segment.isResolved()) {
					IOpenableReference[] openables = segment.getOpenable();
					
					if(openables.length == 0 || !openables[0].open()) {
						ITextSourceReference ref = segment.getSourceReference();
						if (ref.getResource() instanceof IFile) {
							try {
								IWorkbenchPage page = PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow().getActivePage();
								IEditorPart part = IDE.openEditor(page, (IFile)ref.getResource(), true);
								if (ref.getStartPosition() >=0 && ref.getLength() > 0) {
									if (part instanceof JavaEditor) {
										EditorUtility.revealInEditor(part, 
												ref.getStartPosition(), ref.getLength());
									} else if (part != null) { 
										// We have not to pass null argument here, because the following call will 
										// perform select and reveal on active editor which is wrong in part == null
										//
										StructuredSelectionHelper.setSelectionAndRevealInActiveEditor(
												new Region(ref.getStartPosition(), ref.getLength()));
									}
								}

							} catch (PartInitException pie) {
								CDICorePlugin.getDefault().logError(pie);
							}
						}
					}
				}
			}
		}

		return null;
	}
}
