/*
 * JBoss, Home of Professional Open Source
 * Copyright 2020, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.quickstarts.microprofile.faulttolerance;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

/**
 * A JAX-RS resource that provides information about kinds of coffees we have on store and numbers of packages available.
 * Demonstrates {@link Retry}, {@link CircuitBreaker}, {@link Timeout} and {@link Fallback} policies.
 *
 * @author Radoslav Husar
 */
@Path("/coffee")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CoffeeResource {

    private static final Logger LOGGER = Logger.getLogger(CoffeeResource.class);

    @Inject
    CoffeeRepositoryService coffeeRepository;

    private AtomicLong counter = new AtomicLong(0);

    private Float failRatio = 0.5f;

    /**
     * Provides list of all our coffees.
     * <p>
     * This method fails about 50% of time. However, in case of a failure, the method is automatically re-invoked again
     * (up to 4 times), thanks to the {@link Retry} annotation. That means that a user is rarely exposed to a
     * failure, since the probability of a failure occurring 4 times in row is fairly low.
     */
    @GET
    @Retry(maxRetries = 4, retryOn = RuntimeException.class)
    public List<Coffee> coffees() {
        final Long invocationNumber = counter.getAndIncrement();

        maybeFail(String.format("CoffeeResource#coffees() invocation #%d failed", invocationNumber));

        LOGGER.infof("CoffeeResource#coffees() invocation #%d returning successfully", invocationNumber);
        return coffeeRepository.getAllCoffees();
    }

    /**
     * Provides information about a coffee with given id.
     * <p>
     * Same as the {@link #coffees()} method, this method fails about 50% of time. However, because this method doesn't
     * have any {@link Retry} policy, a user is exposed to the failures every time they happen.
     */
    @Path("/{id}")
    @GET
    public Response coffeeDetail(@PathParam("id") int id) {
        final Long invocationNumber = counter.getAndIncrement();

        maybeFail(String.format("CoffeeResource#coffees() invocation #%d failed", invocationNumber));

        LOGGER.infof("CoffeeResource#coffees() invocation #%d returning successfully", invocationNumber);
        Coffee coffee = coffeeRepository.getCoffeeById(id);

        // if coffee with given id not found, return 404
        if (coffee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(coffee).build();
    }

    /**
     * Returns how many packages of given coffee we have on store.
     * <p>
     * In this case, the failures are introduced inside the CDI bean this method calls. The bean will repeat two
     * successful and two failed invocations. It also defines a {@link CircuitBreaker} policy, which will open a circuit
     * breaker after two failed invocations, and then stay open for 5 seconds.
     */
    @Path("/{id}/availability")
    @GET
    public Response availability(@PathParam("id") int id) {
        final Long invocationNumber = counter.getAndIncrement();

        Coffee coffee = coffeeRepository.getCoffeeById(id);

        // check that coffee with given id exists, return 404 if not
        if (coffee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            Integer availability = coffeeRepository.getAvailability(coffee);
            LOGGER.infof("CoffeeResource#availability() invocation #%d returning successfully", invocationNumber);
            return Response.ok(availability).build();
        } catch (RuntimeException e) {
            String message = e.getClass().getSimpleName() + ": " + e.getMessage();
            LOGGER.errorf("CoffeeResource#availability() invocation #%d failed: %s", invocationNumber, message);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(message)
                    .build();
        }
    }

    @GET
    @Path("/{id}/recommendations")
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackRecommendations")
    public List<Coffee> recommendations(@PathParam("id") int id) {
        long started = System.currentTimeMillis();
        final long invocationNumber = counter.getAndIncrement();

        try {
            randomDelay();
            LOGGER.infof("CoffeeResource#recommendations() invocation #%d returning successfully", invocationNumber);
            return coffeeRepository.getRecommendations(id);
        } catch (InterruptedException e) {
            LOGGER.errorf("CoffeeResource#recommendations() invocation #%d timed out after %d ms",
                    invocationNumber, System.currentTimeMillis() - started);
            return null;
        }
    }

    /**
     * A fallback method for recommendations.
     */
    public List<Coffee> fallbackRecommendations(int id) {
        LOGGER.info("Falling back to RecommendationResource#fallbackRecommendations()");
        // safe bet, return something that everybody likes
        return Collections.singletonList(coffeeRepository.getCoffeeById(1));
    }


    private void maybeFail(String failureLogMessage) {
        // introduce some artificial failures
        if (new Random().nextFloat() < failRatio) {
            LOGGER.error(failureLogMessage);
            throw new RuntimeException("Resource failure.");
        }
    }

    private void randomDelay() throws InterruptedException {
        // introduce some artificial delay
        Thread.sleep(new Random().nextInt(500));
    }

    void setFailRatio(Float failRatio) {
        this.failRatio = failRatio;
    }

    void resetCounter() {
        this.counter.set(0);
    }

    Long getCounter() {
        return counter.get();
    }
}
