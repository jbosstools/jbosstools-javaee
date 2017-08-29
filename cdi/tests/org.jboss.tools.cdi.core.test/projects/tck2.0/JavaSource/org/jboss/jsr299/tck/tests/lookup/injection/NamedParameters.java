package org.jboss.jsr299.tck.tests.lookup.injection;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

public class NamedParameters {
    @Produces
    @Named("uniqueName16728")
    String abc;

    @Inject
    void setA(@Named("anotherName63916") String s) {
    }

    @Inject
    void setABC(@Named("uniqueName16728") String s) {
    }
}
