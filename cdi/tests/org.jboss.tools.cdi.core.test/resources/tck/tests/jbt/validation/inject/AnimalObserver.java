package org.jboss.jsr299.tck.tests.jbt.validation.inject;

import javax.enterprise.event.Observes;

public class AnimalObserver {

	public void observeSomeEvent(@Observes Animal someEvent, Animal injectedAnimal) {
	}
}