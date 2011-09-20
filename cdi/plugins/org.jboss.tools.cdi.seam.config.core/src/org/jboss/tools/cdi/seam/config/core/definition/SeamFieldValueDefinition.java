package org.jboss.tools.cdi.seam.config.core.definition;

import org.jboss.tools.common.java.IParametedType;

/**
 * Additional object for field definition, when an inline bean is injected to its value.
 * In case of collections and maps, there can be multiple value injections, for each one a 
 * separate injection point should be provided.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamFieldValueDefinition extends SeamFieldDefinition {
	SeamBeanDefinition inline;
	IParametedType requiredType;

	public void setInlineBean(SeamBeanDefinition inline) {
		this.inline = inline;
	}

	public SeamBeanDefinition getInlineBean() {
		return inline;
	}

	public void setRequiredType(IParametedType requiredType) {
		this.requiredType = requiredType;
	}

	public IParametedType getRequiredType() {
		return requiredType;
	}

}
