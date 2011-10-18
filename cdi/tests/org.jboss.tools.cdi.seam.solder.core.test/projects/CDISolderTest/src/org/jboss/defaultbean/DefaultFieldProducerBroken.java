package org.jboss.defaultbean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.jboss.solder.bean.defaultbean.DefaultBean;

@SessionScoped
public class DefaultFieldProducerBroken implements Serializable {
	private static final long serialVersionUID = 1L;

	@Produces
	@DefaultBean(String.class)
	@Named("defaultbean172817")
	String s;

}
