package org.jboss.tools.jsf.ui.test;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.progress.UIJob;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.TestProjectProvider;
import org.jboss.tools.test.util.WorkbenchUtils;

public class CreateJSF2CompositeMenuTest extends ContentAssistantTestCase {
	TestProjectProvider provider = null;
	boolean makeCopy = true;
	private static final String PROJECT_NAME = "test_jsf_project";
	private static final String PAGE_NAME = "/WebContent/pages/test_page2.xhtml";
	
	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.ui.test", null, PROJECT_NAME, makeCopy); 
		project = provider.getProject();
	}

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}
	
	public void testMenu(){
		fileName = PAGE_NAME;
		IFile testfile = project.getFile(fileName);
		assertTrue("Test file doesn't exist: " + project.getName() + "/" + fileName, 
				(testfile.exists() && testfile.isAccessible()));

		editorPart = WorkbenchUtils.openEditor(project.getName()+"/"+ fileName); //$NON-NLS-1$
		
		obtainTextEditor(editorPart);
		
		UIJob registerJob = new UIJob(Display.getDefault(), "JBoss Central DND initialization") {
			{
				setPriority(Job.DECORATE);
			}

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				viewer = getTextViewer();
				Menu menu = viewer.getTextWidget().getMenu();
				
				Listener[] listeners = menu.getListeners(SWT.Show);
				
				assertTrue("No listeners found in context menu",listeners.length > 0);
				
				for(Listener listener : listeners){
					if(listener instanceof TypedListener){
						TypedListener tl = (TypedListener)listener;
						if(tl.getEventListener() instanceof MenuListener){
							MenuListener ml = (MenuListener)tl.getEventListener();
							Event event = new Event();
							event.widget = viewer.getTextWidget();
							event.display = viewer.getTextWidget().getDisplay();
							event.time = (int)System.currentTimeMillis();
							ml.menuShown(new MenuEvent(event));
						}else{
							fail("Event listener should be instance of MenuListener");
						}
					}else{
						fail("listener should be instance of TypedListener");
					}
				}
				
				menu.setVisible(true);
				
				
				MenuItem[] items = menu.getItems();
				
				assertTrue("No items found in context menu",items.length > 0);
				
				for(MenuItem item : items){
					if("Create JSF2 composite...".equals(item.getText())){
						// found
						return null;
					}
				}
				fail("Create JSF2 composite... menu item not found");
				return null;
			}
		};

		
	}
}
