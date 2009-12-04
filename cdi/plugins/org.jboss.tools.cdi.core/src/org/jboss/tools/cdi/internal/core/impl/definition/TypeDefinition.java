package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;

public class TypeDefinition extends AbstractTypeDefinition {
	List<FieldDefinition> fields = new ArrayList<FieldDefinition>();
	List<MethodDefinition> methods = new ArrayList<MethodDefinition>();

	public TypeDefinition() {
	}

	@Override
	protected void init(IType contextType, DefinitionContext context) throws CoreException {
		super.init(contextType, context);
		for (AnnotationDeclaration d: annotations) {
			int kind = context.getAnnotationKind(d.getType());
			//TODO do we need to create members for specific annotations?
		}
		IField[] fs = getType().getFields();
		for (int i = 0; i < fs.length; i++) {
			FieldDefinition f = new FieldDefinition();
			f.setField(fs[i], context);
			//TODO check if it is annotated
			fields.add(f);
		}
		IMethod[] ms = getType().getMethods();
		for (int i = 0; i < ms.length; i++) {
			MethodDefinition m = new MethodDefinition();
			m.setMethod(ms[i], context);
			//TODO check if it is annotated
			methods.add(m);
		}
	}

}
