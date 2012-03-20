package cdi.seam;

import java.net.URL;

import javax.inject.Inject;

import org.jboss.seam.solder.resourceLoader.Resource;

public class MyBean {

	@Inject
	@Resource("WEB-INF/beans.xml")
	URL beansXml;
	
}
