package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.ejb.Stateful;
import javax.enterprise.inject.Produces;

@Stateful
public class MakeMethodPublic
{
   @Produces public Foo createFoo() { return new Foo(); }
}
