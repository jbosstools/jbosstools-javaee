package org.jboss.tools.seam.ui.pages.editor.edit;

import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;

public class PageWrapper {
	private Page page;
	
	public PageWrapper(Page page){
		this.page = page;
	}
	
	public Page getPage(){
		return page;
	}

}
