package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

public class TimestampLogger_Broken {

	@Produces
	public static Spider getSpider() {
		return new Spider();
	}

	public static void destorySpider(@Disposes Spider spider) {
	}

	public static void destorySpiderAgain(@Disposes Spider spider) {
	}
}