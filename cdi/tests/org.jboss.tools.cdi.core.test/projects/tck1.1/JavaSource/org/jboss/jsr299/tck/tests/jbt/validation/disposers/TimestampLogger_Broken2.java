package org.jboss.jsr299.tck.tests.jbt.validation.disposers;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

public class TimestampLogger_Broken2 {

	@Produces
	public static Spider spider = new Spider();

	public static void destorySpider(@Disposes Spider spider) {
	}

	public static void destorySpiderAgain(@Disposes Spider spider) {
	}
}