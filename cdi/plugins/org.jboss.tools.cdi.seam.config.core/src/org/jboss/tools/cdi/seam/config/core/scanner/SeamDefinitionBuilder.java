package org.jboss.tools.cdi.seam.config.core.scanner;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IDocument;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.IJavaAnnotation;
import org.jboss.tools.cdi.internal.core.impl.AnnotationLiteral;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigConstants;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigCorePlugin;
import org.jboss.tools.cdi.seam.config.core.ConfigDefinitionContext;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeanDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeansDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamFieldDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamMethodDefinition;
import org.jboss.tools.cdi.seam.config.core.util.Util;

public class SeamDefinitionBuilder {
	static int IN_ANNOTATION_TYPE = 1;

	CDICoreNature project;
	ConfigDefinitionContext context;
	IResource resource;
	SeamBeansDefinition result;
	SAXElement root;

	public SeamBeansDefinition createDefinition(IResource resource, IDocument document, CDICoreNature project, ConfigDefinitionContext context) {
		this.project = project;
		this.context = context;
		this.resource = resource;
		
		result = new SeamBeansDefinition();
		if(document.get().indexOf("<") >= 0) { // file can be empty
			SAXParser parser = new SAXParser();
			String text = document.get();
			ByteArrayInputStream s = new ByteArrayInputStream(text.getBytes());
			root = parser.parse(s, document);
			scanRoot();
		}

		return result;
	}

	private void scanRoot() {
		if(root == null) return;
		List<SAXElement> es = root.getChildElements();
		for (SAXElement element: es) {
			scanElement(element);
		}
	}

	private void scanElement(SAXElement element) {
		if(!Util.isConfigRelevant(element)) return;

		IType type = Util.resolveType(element, project);
		if(type == null) {
			result.addUnresolvedNode(element, CDISeamConfigConstants.UNRESOLVED_TYPE);
			return;
		}
		boolean isAnnnotation = false;
		try {
			isAnnnotation = type.isAnnotation();
		} catch (JavaModelException e) {
			CDISeamConfigCorePlugin.getDefault().logError(e);
			result.addUnresolvedNode(element, CDISeamConfigConstants.UNRESOLVED_TYPE);
			return;
		}
		if(isAnnnotation) {
			scanAnnotation(element, type);
		} else {
			scanBean(element, type);
		}
	}

	private void scanAnnotation(SAXElement element, IType type) {
		context.getRootContext().getAnnotationKind(type); // kick it
		AnnotationDefinition def = new AnnotationDefinition();
		def.setType(type, context.getRootContext());

		List<SAXElement> es = element.getChildElements();
		//children should be annotation declarations.
		for (SAXElement c: es) {
			IJavaAnnotation a = loadAnnotationDeclaration(c, IN_ANNOTATION_TYPE);
			if(a != null) {
				def.addAnnotation(a, context.getRootContext());
			}
		}
		
		def.revalidateKind(context.getRootContext());
	
		context.addAnnotation(type.getFullyQualifiedName(), def);

	}

	private void scanBean(SAXElement element, IType type) {
		SeamBeanDefinition def = new SeamBeanDefinition();
		def.setElement(element);
		def.setType(type);
		result.addBeanDefinition(def);
		List<SAXElement> es = element.getChildElements();
		for (SAXElement c: es) {
			if(!Util.isConfigRelevant(c)) continue;
			if(Util.containsEEPackage(c.getURI())) {
				if("replaces".equals(c.getLocalName())) {
					def.setReplaces(c);
					continue;
				}
				if("modifies".equals(c.getLocalName())) {
					def.setModifies(c);
					continue;
				}
			}
			IType t = Util.resolveType(c, project);
			if(t != null) {
				IJavaAnnotation a = loadAnnotationDeclaration(element, IN_ANNOTATION_TYPE);
				if(a != null) {
					def.addAnnotation(a);
				}
				continue;
			}
			IMember m = null;
			try {
				m = Util.resolveMember(type, c);
			} catch (JavaModelException e) {
				CDISeamConfigCorePlugin.getDefault().logError(e);
			}
			if(m instanceof IField) {
				def.addField(scanField(c, (IField)m));
			} else if(m instanceof IMethod) {
				def.addMethod(scanMethod(element, (IMethod)m));
			} else {
				result.addUnresolvedNode(c, "Cannot resolve member.");
			}
		}
	}

	private SeamFieldDefinition scanField(SAXElement element, IField field) {
		SeamFieldDefinition def = new SeamFieldDefinition();
		def.setElement(element);
		def.setField(field);
		List<SAXElement> es = element.getChildElements();
		for (SAXElement c: es) {
			if(!Util.isConfigRelevant(c)) continue;
			if(Util.containsEEPackage(c.getURI())) {
				if("value".equals(c.getLocalName())) {
					//TODO do not forget to look for Inline Bean Declarations inside field values.
					
				}
				
			}
			IType t = Util.resolveType(c, project);
			if(t != null) {
				IJavaAnnotation a = loadAnnotationDeclaration(element, IN_ANNOTATION_TYPE);
				if(a != null) {
					def.addAnnotation(a);
				}
				continue;
			}
		
		}		
		return def;
	}	

	private SeamMethodDefinition scanMethod(SAXElement element, IMethod method) {
		SeamMethodDefinition def = new SeamMethodDefinition();
		def.setElement(element);
		def.setMethod(method);
		List<SAXElement> es = element.getChildElements();
		for (SAXElement c: es) {
			if(!Util.isConfigRelevant(c)) continue;
			if(Util.containsEEPackage(c.getURI())) {
				if("parameters".equals(c.getLocalName())) {
					//TODO
					
				}
				
			}
			IType t = Util.resolveType(c, project);
			if(t != null) {
				IJavaAnnotation a = loadAnnotationDeclaration(element, IN_ANNOTATION_TYPE);
				if(a != null) {
					def.addAnnotation(a);
				}
				continue;
			}
		
		}		
		return def;
	}

	private IJavaAnnotation loadAnnotationDeclaration(SAXElement element, int contextKind) {
		if(!Util.isConfigRelevant(element)) return null;
		
		IType type = Util.resolveType(element, project);
		if(type == null) {
			if(contextKind == IN_ANNOTATION_TYPE) {
				result.addUnresolvedNode(element, CDISeamConfigConstants.UNRESOLVED_TYPE);
			}
			return null;
		}
		boolean isAnnnotation = false;
		try {
			isAnnnotation = type.isAnnotation();
		} catch (JavaModelException e) {
			CDISeamConfigCorePlugin.getDefault().logError(e);
			result.addUnresolvedNode(element, CDISeamConfigConstants.UNRESOLVED_TYPE);
			return null;
		}
		if(isAnnnotation) {
			context.getRootContext().getAnnotationKind(type); // kick it
			AnnotationLiteral literal = new AnnotationLiteral(resource,  
					element.getLocation().getStartPosition(), element.getLocation().getLength(), 
					null, 0, type);
			//TODO read and add member values.
			return literal;
		} else if(contextKind == IN_ANNOTATION_TYPE) {
			result.addUnresolvedNode(element, CDISeamConfigConstants.ANNOTATION_EXPECTED);
		}		
		return null;
	}

}
