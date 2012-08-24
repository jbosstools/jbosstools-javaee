package org.jboss.tools.cdi.gen.model;

public class GenImportsCollector {
	GenType type;

	public GenImportsCollector(GenType type) {
		this.type = type;
	}

	public void addImports(GenAnnotationReference a) {
		type.addImport(a.getFullyQualifiedName());
	}

	public void addImports(GenVariable v) {
		type.addImport(v.getType().getFullyQualifiedName());
		for (GenAnnotationReference a: v.getAnnotations()) {
			addImports(a);
		}
	}

	public void addImports(GenMethod m) {
		type.addImport(m.getType().getFullyQualifiedName());
		for (GenAnnotationReference a: m.getAnnotations()) {
			addImports(a);
		}
		for (GenVariable p: m.getParameters()) {
			addImports(p);
		}
	}
}
