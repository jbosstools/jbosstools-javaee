package org.jboss.jsr299.tck.tests.jbt.resolution.priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.annotation.Priority;

@ApplicationScoped
@Alternative
@Priority(300)
public class WoodenTable {

}
