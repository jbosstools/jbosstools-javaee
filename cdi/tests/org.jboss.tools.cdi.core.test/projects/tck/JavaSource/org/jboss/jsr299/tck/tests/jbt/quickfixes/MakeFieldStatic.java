package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;

@Stateless
public class MakeFieldStatic {

	@Produces public static FunnelWeaver<String> anotherFunnelWeaver;
}