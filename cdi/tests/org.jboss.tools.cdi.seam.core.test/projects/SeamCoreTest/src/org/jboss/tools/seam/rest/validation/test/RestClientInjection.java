package org.jboss.tools.seam.rest.validation.test;

import javax.inject.Inject;

import org.jboss.seam.rest.client.RestClient;

public class RestClientInjection {

    @Inject
    @RestClient("http://localhost:8080/rest-tasks")
    RestPath ok;

    @Inject
    void foo(@RestClient("http://localhost:8080/rest-tasks") RestPath ok) {
    }

    @Inject
    RestPath broken;
}