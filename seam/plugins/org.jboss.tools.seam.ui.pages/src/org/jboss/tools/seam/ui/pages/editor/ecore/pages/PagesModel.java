package org.jboss.tools.seam.ui.pages.editor.ecore.pages;


/** 
 * @author daniel
 * 
 * Pages.xml model interface
 * 
 * @model
 */
public interface PagesModel extends PagesElement {

	public PagesElement findElement(Object data);

	public void bindElement(Object data, PagesElement element);

	public Link findLink(Object data);

	public void bindLink(Object data, Link link);

	public void load();

	public void dispose();

}
