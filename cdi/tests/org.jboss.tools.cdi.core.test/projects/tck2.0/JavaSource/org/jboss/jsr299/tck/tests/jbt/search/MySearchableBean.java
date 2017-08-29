package org.jboss.jsr299.tck.tests.jbt.search;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

@Named
public class MySearchableBean {

  @Produces @Named public String sFoo;

  @Produces @Named public String sFoo1() {
     return "";
  }

  public String sFoo2() {
     return "";
  }
}