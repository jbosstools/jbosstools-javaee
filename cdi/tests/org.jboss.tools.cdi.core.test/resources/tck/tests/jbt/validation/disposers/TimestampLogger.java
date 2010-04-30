package org.jboss.jsr299.tck.tests.jbt.validation.disposers;

import javax.decorator.Decorator;
import javax.enterprise.inject.Disposes;

@Decorator
public class TimestampLogger {

	public static void destorySpider(@Disposes Spider spider) {
	}
}