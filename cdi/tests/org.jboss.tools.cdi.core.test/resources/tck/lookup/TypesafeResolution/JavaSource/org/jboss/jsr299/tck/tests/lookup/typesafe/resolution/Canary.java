package org.jboss.jsr299.tck.tests.lookup.typesafe.resolution;

import javax.enterprise.inject.Typed;

@Typed(Canary.class)
public class Canary implements Bird
{

}
