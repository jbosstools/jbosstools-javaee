package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.ejb.Stateful;
import javax.enterprise.inject.Produces;

@Stateful
public class FooProducer implements FooProducerLocal
{
   @Produces Foo createFoo() { return new Foo(); }
}
