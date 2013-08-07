package org.jboss.jsr299.tck.tests.jbt.validation.specialization;

import javax.enterprise.inject.Specializes;
import javax.enterprise.inject.Typed;

@Specializes
@Typed(MissingTypeBeanBroken.class)
public class MissingTypeBeanBroken extends Farmer {

}