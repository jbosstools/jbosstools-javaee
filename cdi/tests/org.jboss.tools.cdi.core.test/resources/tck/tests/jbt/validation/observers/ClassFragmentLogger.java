package org.jboss.jsr299.tck.tests.jbt.validation.observers;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;

@Singleton
public class ClassFragmentLogger {

	private final List<Object> log;

	public ClassFragmentLogger() {
		this.log = new ArrayList<Object>();
	}

	@Lock(LockType.WRITE)
	public void addEntry(@Observes Object codeFragment) {
		this.log.add(codeFragment);
	}
}