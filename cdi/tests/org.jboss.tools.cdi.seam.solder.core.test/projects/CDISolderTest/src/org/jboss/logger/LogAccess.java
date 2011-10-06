package org.jboss.logger;

import javax.inject.Inject;

import org.jboss.solder.messages.MessageBundle;

public class LogAccess {
	@Inject MyLogger logger;

	@Inject @MessageBundle MyBundle bundle;
	
	String s = "#{logger1.message}";
}
