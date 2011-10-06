package org.jboss.requires;

import org.jboss.solder.core.Requires;
import javax.inject.Named;

@Requires("org.jboss.requires.Beehive")
@Named("bear")
public class Bear {

}
