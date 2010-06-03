package org.jboss.tools.jsf.jsf2.refactoring.action.rename;

import org.eclipse.jdt.internal.ui.refactoring.UserInterfaceManager;

@SuppressWarnings("restriction")
public class RenameUserInterfaceManager extends UserInterfaceManager {
	
	private static final RenameUserInterfaceManager fgInstance= new RenameUserInterfaceManager();

	public static RenameUserInterfaceManager getDefault() {
		return fgInstance;
	}

	private RenameUserInterfaceManager() {
		put(CompositeAttributeRenameProcessor.class, RenameUserInterfaceStarter.class, CompositeAttributeRenameRefactoringWizard.class);
	}
	
}
