package deltaspike.config;

import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigProperty;

public class SettingsBean {
    @Inject
    @ConfigProperty(name = "property1")
    String property1;

    @Inject
    @Property2 
    Long property2;

    @Inject
    Long property3;

}
