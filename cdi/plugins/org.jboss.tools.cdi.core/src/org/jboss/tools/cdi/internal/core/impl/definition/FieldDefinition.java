package org.jboss.tools.cdi.internal.core.impl.definition;

import org.eclipse.jdt.core.IField;

public class FieldDefinition extends AbstractMemberDefinition {
	IField field;

	public FieldDefinition() {}

	public void setField(IField field, DefinitionContext context) {
		this.field = field;
		setAnnotatable(field, field.getDeclaringType(), context);
	}

	public IField getField() {
		return field;
	}

}
