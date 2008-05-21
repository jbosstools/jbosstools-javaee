package org.jboss.ide.seam.gen.actions;

public class NewFormAction extends NewActionAction {
	/**
	 * The constructor.
	 */
	public NewFormAction() {
	}
	
	protected String getTarget() {
		return "new-form";
	}
	
	public String getTitle() {
		return "Create new form";
	}
	
	public String getDescription() {
		return "Create a form with a single input field and related\n" +
				"Java interface, SLSB with key Seam/EJB3 annotations.";
	}


}