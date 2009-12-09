package org.jboss.tools.cdi.internal.core.impl;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.jboss.tools.cdi.core.IBeanField;
import org.jboss.tools.cdi.internal.core.impl.definition.FieldDefinition;

public class BeanField extends BeanMember implements IBeanField {
	protected IField field;

	public BeanField() {}

	public void setDefinition(FieldDefinition definition) {
		super.setDefinition(definition);
		setField(definition.getField());
	}

	public IField getField() {
		return field;
	}

	public void setField(IField field) {
		this.field = field;
		setMember(field);
	}

	public IMember getSourceMember() {
		return getField();
	}

	public FieldDefinition getDefinition() {
		return (FieldDefinition)definition;
	}
}
