package org.jboss.requires;

import javax.inject.Named;

import org.jboss.solder.core.Requires;

@Requires({"org.jboss.requires.Fly", "org.jboss.requires.Flower"})
@Named("dragonfly")
public class Dragonfly {

}
