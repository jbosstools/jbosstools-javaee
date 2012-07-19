package org.jboss.jsr299.tck.tests.jbt.quickfixes;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;


@SessionScoped
class AddSerializable implements Serializable
{
	private static final long serialVersionUID = 1L;
}
