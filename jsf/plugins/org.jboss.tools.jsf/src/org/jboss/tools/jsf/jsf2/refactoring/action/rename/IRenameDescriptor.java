package org.jboss.tools.jsf.jsf2.refactoring.action.rename;

import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;

public interface IRenameDescriptor {
	
	RenameRefactoring getRenameRefactoring();
	
	RenameUserInterfaceManager getInterfaceManager();
	
	String getCurrentName();
	
	RefactoringProcessor getRefactoringProcessor();

}
