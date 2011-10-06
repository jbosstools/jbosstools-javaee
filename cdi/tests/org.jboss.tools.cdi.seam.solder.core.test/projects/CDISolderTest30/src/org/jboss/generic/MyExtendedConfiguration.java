package org.jboss.generic;

/**
 * Configuration created by bean extending config type.
 */
@MyGenericType("third")
@Qualifier2
public class MyExtendedConfiguration extends MyConfiguration {
	public MyExtendedConfiguration() {
		super("");
	}
}
