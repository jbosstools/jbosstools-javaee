package org.jboss.jsr299.tck.tests.jbt.resolution.priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.annotation.Priority;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 100)
public class IronTable {

}
