package org.jboss.tools.cdi.seam.config.core.scanner;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMemberValuePair;
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
import org.jboss.tools.cdi.seam.config.core.definition.SeamParameterDefinition;
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
			result.addUnresolvedNode(element, CDISeamConfigConstants.ERROR_UNRESOLVED_TYPE);
			return;
		}
		TypeCheck typeCheck = new TypeCheck(type, element);
		if(typeCheck.isCorrupted) return;
		if(typeCheck.isAnnotation) {
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
			if(a != null) def.addAnnotation(a, context.getRootContext());
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
			if(Util.containsEEPackage(c)) {
				if(CDISeamConfigConstants.KEYWORD_REPLACES.equals(c.getLocalName())) {
					def.setReplaces(c);
					continue;
				}
				if(CDISeamConfigConstants.KEYWORD_MODIFIES.equals(c.getLocalName())) {
					def.setModifies(c);
					continue;
				}
			}
			IType t = Util.resolveType(c, project);
			if(t != null) {
				IJavaAnnotation a = loadAnnotationDeclaration(c, IN_ANNOTATION_TYPE);
				if(a != null) def.addAnnotation(a);
				continue;
			}
			IMember m = null;
			if(c.getURI() != null && c.getURI().equals(element.getURI())) try {
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
		if(Util.hasText(element)) {
			def.addValue(element.getTextNode());
		}
		List<SAXElement> es = element.getChildElements();
		for (SAXElement c: es) {
			if(!Util.isConfigRelevant(c)) continue;
			if(Util.isValue(c)) {
				if(Util.hasText(c)) {
					def.addValue(c.getTextNode());
				} else {
					scanFieldValue(c);
				}
				continue;
			} else if(Util.isEntry(c)) {
				scanEntry(def, c);
				continue;
			}
			IType t = Util.resolveType(c, project);
			if(t != null) {
				IJavaAnnotation a = loadAnnotationDeclaration(c, IN_ANNOTATION_TYPE);
				if(a != null) def.addAnnotation(a);
				continue;
			}
		
		}		
		return def;
	}	

	/**
	 * Scan field value for inline bean declarations. 
	 * @param element
	 */
	private void scanFieldValue(SAXElement element) {
		if(!Util.isConfigRelevant(element)) return;
		List<SAXElement> es = element.getChildElements();
		for (SAXElement c: es) {
			if(!Util.isConfigRelevant(c)) continue;
			IType type = Util.resolveType(element, project);
			if(type == null) continue;
			TypeCheck typeCheck = new TypeCheck(type, element);
			if(typeCheck.isCorrupted) return;
			if(!typeCheck.isAnnotation) {
				scanBean(element, type);
			}
		}
	}

	private void scanEntry(SeamFieldDefinition def, SAXElement element) {
		List<SAXElement> es = element.getChildElements();
		SAXText key = null;
		SAXText value = null;
		for (SAXElement c: es) {
			if(!Util.isConfigRelevant(c)) continue;
			if(Util.isKey(c)) {
				if(Util.hasText(c)) {
					key = c.getTextNode();
				} else {
					scanFieldValue(c);
				}
			}
			if(Util.isValue(c)) {
				if(Util.hasText(c)) {
					value = c.getTextNode();
				} else {
					scanFieldValue(c);
				}
			}
		}
		if(key != null && value != null) {
			def.addValue(key, value);
		}
	}

	private SeamMethodDefinition scanMethod(SAXElement element, IMethod method) {
		SeamMethodDefinition def = new SeamMethodDefinition();
		def.setElement(element);
		def.setMethod(method);
		List<SAXElement> es = element.getChildElements();
		for (SAXElement c: es) {
			if(!Util.isConfigRelevant(c)) continue;
			if(Util.isParameters(c)) {
				List<SAXElement> ps = element.getChildElements();
				for (SAXElement p: ps) {
					SeamParameterDefinition pd = scanParameter(p);
					if(pd != null) def.addParameter(pd);
				}
				continue;
			} else if(Util.isArray(c)) {
				SeamParameterDefinition pd = scanParameter(c);
				if(pd != null) def.addParameter(pd);
				continue;
			}
			IType t = Util.resolveType(c, project);
			if(t != null) {
				IJavaAnnotation a = loadAnnotationDeclaration(c, IN_ANNOTATION_TYPE);
				if(a != null) def.addAnnotation(a);
				continue;
			}
		
		}		
		return def;
	}

	private SeamParameterDefinition scanParameter(SAXElement element) {
		if(!Util.isConfigRelevant(element)) return null;
		SeamParameterDefinition def = new SeamParameterDefinition();
		def.setElement(element);
		if(Util.isArray(element)) {
			if(element.hasAttribute(CDISeamConfigConstants.ATTR_DIMENSIONS)) {
				def.setDimensions(element.getAttribute(CDISeamConfigConstants.ATTR_DIMENSIONS).getValue());
			}
			List<SAXElement> es = element.getChildElements();
			for (SAXElement c: es) {
				if(!Util.isConfigRelevant(c)) continue;
				IType type = Util.resolveType(c, project);
				if(type == null) {
					result.addUnresolvedNode(element, CDISeamConfigConstants.ERROR_UNRESOLVED_TYPE);
					continue;
				}
				TypeCheck typeCheck = new TypeCheck(type, c);
				if(typeCheck.isCorrupted) continue;
				if(typeCheck.isAnnotation) {
					IJavaAnnotation a = loadAnnotationDeclaration(c, IN_ANNOTATION_TYPE);
					if(a != null) def.addAnnotation(a);
				} else {
					def.setType(type);
				}
			}
		} else {
			IType type = Util.resolveType(element, project);
			if(type == null) {
				result.addUnresolvedNode(element, CDISeamConfigConstants.ERROR_UNRESOLVED_TYPE);
				return null;
			}
			def.setType(type);
			List<SAXElement> es = element.getChildElements();
			for (SAXElement c: es) {
				if(!Util.isConfigRelevant(c)) {
					continue; //report?
				}
				if(Util.containsEEPackage(c)) continue; //we are not interested yet
				IType t = Util.resolveType(c, project);
				if(t != null) {
					IJavaAnnotation a = loadAnnotationDeclaration(c, IN_ANNOTATION_TYPE);
					if(a != null) def.addAnnotation(a);
					continue;
				}
			}
		}

		return def;
	}

	private IJavaAnnotation loadAnnotationDeclaration(SAXElement element, int contextKind) {
		if(!Util.isConfigRelevant(element)) return null;
		
		IType type = Util.resolveType(element, project);
		if(type == null) {
			if(contextKind == IN_ANNOTATION_TYPE) {
				result.addUnresolvedNode(element, CDISeamConfigConstants.ERROR_UNRESOLVED_TYPE);
			}
			return null;
		}
		TypeCheck typeCheck = new TypeCheck(type, element);
		if(typeCheck.isCorrupted) return null;
		if(typeCheck.isAnnotation) {
			context.getRootContext().getAnnotationKind(type); // kick it
			String value = null;
			SAXText text = element.getTextNode();
			if(text != null && text.getValue() != null && text.getValue().trim().length() > 0) {
				value = text.getValue();
			}
			AnnotationLiteral literal = new AnnotationLiteral(resource,  
					element.getLocation().getStartPosition(), element.getLocation().getLength(), 
					value, IMemberValuePair.K_STRING, type);
			Set<String> ns = element.getAttributeNames();
			for (String n: ns) {
				String v = element.getAttribute(n).getValue();
				literal.addMemberValuePair(n, v, IMemberValuePair.K_STRING);
			}
			return literal;
		} else if(contextKind == IN_ANNOTATION_TYPE) {
			result.addUnresolvedNode(element, CDISeamConfigConstants.ERROR_ANNOTATION_EXPECTED);
		}		
		return null;
	}

	class TypeCheck {
		boolean isCorrupted = false;
		boolean isAnnotation = false;
		TypeCheck(IType type, SAXElement element) {
			try {
				isAnnotation = type.isAnnotation();
			} catch (JavaModelException e) {
				CDISeamConfigCorePlugin.getDefault().logError(e);
				result.addUnresolvedNode(element, CDISeamConfigConstants.ERROR_UNRESOLVED_TYPE);
				isCorrupted = true;
			}
		}
	}

}
