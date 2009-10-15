package org.jboss.tools.seam.ui.bot.test.create;

import org.jboss.tools.seam.ui.bot.test.TestControl;

public class CreateSeamProjects extends TestControl{
	
	public void testCreateSeamProject12war(){
		createSeamProject(seam12Settings, jbossEAPRuntime, TYPE_WAR);
		waitForBlockingJobsAcomplished(180000, BUILDING_WS);
	}
	
//	public void testCreateSeamProject12ear(){
//		createSeamProject(seam12Settings, jbossEAPRuntime, TYPE_EAR);
//		try {
//			waitForBlockingJobsAcomplished(180000, BUILDING_WS);
//		} catch (InterruptedException e) {
//		}
//	}
//	
//	public void testCreateSeamProject2fpwar(){
//		createSeamProject(seam2fpSettings, jbossEAPRuntime, TYPE_WAR);
//		try {
//			waitForBlockingJobsAcomplished(180000, BUILDING_WS);
//		} catch (InterruptedException e) {
//		}
//	}
//	
//	public void testCreateSeamProject2fpear(){
//		createSeamProject(seam2fpSettings, jbossEAPRuntime, TYPE_EAR);
//		try {
//			waitForBlockingJobsAcomplished(180000, BUILDING_WS);
//		} catch (InterruptedException e) {
//		}
//	}
//	
//	public void testCreateSeamProject21war(){
//		createSeamProject(seam21Settings, jbossEAPRuntime, TYPE_WAR);
//		try {
//			waitForBlockingJobsAcomplished(180000, BUILDING_WS);
//		} catch (InterruptedException e) {
//		}
//	}
//	
//	public void testCreateSeamProject21ear(){
//		createSeamProject(seam21Settings, jbossEAPRuntime, TYPE_EAR);
//		try {
//			waitForBlockingJobsAcomplished(180000, BUILDING_WS);
//		} catch (InterruptedException e) {
//		}
//	}
	
}