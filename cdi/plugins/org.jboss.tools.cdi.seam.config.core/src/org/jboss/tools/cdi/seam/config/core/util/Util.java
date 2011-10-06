/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.config.core.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigConstants;
import org.jboss.tools.cdi.seam.config.core.definition.SeamMethodDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamParameterDefinition;
import org.jboss.tools.cdi.seam.config.core.xml.SAXElement;
import org.jboss.tools.cdi.seam.config.core.xml.SAXText;
import org.jboss.tools.common.java.IParametedType;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class Util implements CDISeamConfigConstants {
	public static Map<String, String> EE_TYPES = new HashMap<String, String>();
	public static Map<String, String> EE_TYPES_30 = new HashMap<String, String>();
	
	static {
		EE_TYPES.put("int", "java.lang.Integer");
		EE_TYPES.put("short", "java.lang.Short");
		EE_TYPES.put("long", "java.lang.Long");
		EE_TYPES.put("char", "java.lang.Character");
		EE_TYPES.put("byte", "java.lang.Byte");
		EE_TYPES.put("boolean", "java.lang.Boolean");
		EE_TYPES.put("double", "java.lang.Double");
		EE_TYPES.put("float", "java.lang.Float");
		
		String[] JAVA_LANG = {"Integer", "Short", "Long", "String", "Character", "Byte", "Boolean", "Double", "Float"};
		for (String s: JAVA_LANG) EE_TYPES.put(s, "java.lang." + s);

		String[] JAVA_UTIL = {"List", "Map", "Set"};
		for (String s: JAVA_UTIL) EE_TYPES.put(s, "java.util." + s);
		
		String[] JAVAX_ANNOTATION = {"Generated", "PostConstruct", "PreDestroy", "Resource", "Resources"};
		for (String s: JAVAX_ANNOTATION) EE_TYPES.put(s, "javax.annotation." + s);
		
		String[] JAVAX_INJECT = {"Inject", "Named", "Provider", "Qualifier", "Scope", "Singleton"};
		for (String s: JAVAX_INJECT) EE_TYPES.put(s, "javax.inject." + s);
		
		String[] JAVAX_ENTERPRISE_INJECT = {"Alternative", "AmbiguousResolutionException", "Any", "CreationException", 
				"Default", "Disposes", "IllegalProductException", "InjectionException", "Instance", "Model", "New", 
				"Produces", "ResolutionException", "Specializes", "Stereotype", "Typed",
				"UnproxyableResolutionException", "UnsatisfiedResolutionException"};
		for (String s: JAVAX_ENTERPRISE_INJECT) EE_TYPES.put(s, "javax.enterprise.inject." + s);

		String[] JAVAX_ENTERPRISE_CONTEXT = {"ApplicationScoped", "BusyConversationException", "ContextException", 
				"ContextNotActiveException", "Conversation", "ConversationScoped", "Dependent",
				"NonexistentConversationException", "NormalScope", "RequestScoped", "SessionScoped"};
		for (String s: JAVAX_ENTERPRISE_CONTEXT) EE_TYPES.put(s, "javax.enterprise.context." + s);
		
		String[] JAVAX_ENTERPRISE_EVENT = {"Event", "ObserverException", "Observes", "Reception", "TransactionPhase"};
		for (String s: JAVAX_ENTERPRISE_EVENT) EE_TYPES.put(s, "javax.enterprise.event." + s);
		
		String[] JAVAX_DECORAROR = {"Decorator", "Delegate"};
		for (String s: JAVAX_DECORAROR) EE_TYPES.put(s, "javax.decorator." + s);
		
		String[] JAVAX_INTERCEPTOR = {"AroundInvoke", "AroundTimeout", "ExcludeClassInterceptors", "ExcludeDefaultInterceptors",
				"Interceptor", "InterceptorBinding", "Interceptors", "InvocationContext"};
		for (String s: JAVAX_INTERCEPTOR) EE_TYPES.put(s, "javax.interceptor." + s);

		//It is not clear.
		// In Seam3 doc, item 6.7. Overriding the type of an injection point is devoted to @Exact,
		// but item 6.1 does not mentions its package in namespace urn:java:ee.
		String[] SEAM_SOLDER = {"Exact"};
		for (String s: SEAM_SOLDER) {
			EE_TYPES.put(s, "org.jboss.solder.core." + s);
			EE_TYPES_30.put(s, "org.jboss.seam.solder.core." + s);
		}
		
	}

	public static boolean isConfigRelevant(SAXElement element) {
		String uri = element.getURI();
		return (uri != null && uri.startsWith(CDISeamConfigConstants.URI_PREFIX));
	}

	public static IType resolveType(SAXElement element, CDICoreNature project) {
		return resolveType(element.getLocalName(), element.getURI(), project);
	}

	public static IType resolveType(String name, String uri, CDICoreNature project) {
		if(uri == null || !uri.startsWith(CDISeamConfigConstants.URI_PREFIX)) {
			return null;
		}
		String[] packages = getPackages(uri);
		for (String pkg: packages) {
			if(pkg.length() == 0) continue;
			String typeName = null;
			if(pkg.equals(PACKAGE_EE)) {
				typeName = Util.EE_TYPES.get(name);
				if(typeName == null) {
					typeName = Util.EE_TYPES_30.get(name);
				}
			} else {
				typeName = pkg + "." + name;
			}
			IType type = project.getType(typeName);
			if(type != null) {
				return type;
			}
		}
		return null;
	}

	/**
	 * Computes possible type names that could resolve type for the element, 
	 * if one of these types existed.
	 * Returns empty set if a) element has no prefix, b) uri is not urn:java:,
	 * c) package is 'ee'.
	 * 
	 * @param element
	 * @return
	 */
	public static Set<String> getPossibleTypeNames(SAXElement element) {
		Set<String> result = new HashSet<String>();
		String name = element.getLocalName();
		String uri = element.getURI();
		if(uri != null && uri.startsWith(CDISeamConfigConstants.URI_PREFIX)) {
			String[] packages = getPackages(uri);
			for (String pkg: packages) {
				if(pkg.length() > 0 && !pkg.equals(PACKAGE_EE)) {
					result.add(pkg + "." + name);
				}
			}
		}
		return result;
	}

	public static IMember resolveMember(IType type, SAXElement element) throws JavaModelException {
		String name = element.getLocalName();
		IField f = type.getField(name);
		if(f != null && f.exists() && !hasParametersOrArrayChild(element)) {
			return f;
		}
		IMethod[] ms = type.getMethods();
		for (IMethod m: ms) {
			if(name.equals(m.getElementName())) {
				//that is only a preliminary resolving. Exact method will be found on loading parameters.
				return m;
			}
		}
		
		return null;
	}

	public static String[] getPackages(String uri) {
		if(uri == null || !uri.startsWith(CDISeamConfigConstants.URI_PREFIX)) {
			return new String[0];
		}
		uri = uri.substring(CDISeamConfigConstants.URI_PREFIX.length());
		return uri.split(":");
	}

	public static boolean containsEEPackage(SAXElement element) {
		return containsEEPackage(element.getURI());
	}

	public static boolean containsEEPackage(String uri) {
		String[] ps = getPackages(uri);
		for (String p: ps) if(CDISeamConfigConstants.PACKAGE_EE.equals(p)) {
			return true;
		}
		return false;
	}

	public static boolean isArray(SAXElement element) {
		return isKeyword(element, KEYWORD_ARRAY);
	}

	public static boolean isEntry(SAXElement element) {
		return isKeyword(element, KEYWORD_ENTRY, KEYWORD_E);
	}

	public static boolean isKey(SAXElement element) {
		return isKeyword(element, KEYWORD_KEY, KEYWORD_K);
	}

	public static boolean isValue(SAXElement element) {
		return isKeyword(element, KEYWORD_VALUE, KEYWORD_V);
	}

	public static boolean isParameters(SAXElement element) {
		return isKeyword(element, KEYWORD_PARAMETERS);
	}

	public static boolean isKeyword(SAXElement element, String keyword) {
		return keyword.equals(element.getLocalName()) && containsEEPackage(element);
	}

	public static boolean isKeyword(SAXElement element, String keyword1, String keyword2) {
		String n = element.getLocalName();
		return (keyword1.equals(n) || keyword2.equals(n)) && containsEEPackage(element);
	}

	public static boolean hasText(SAXElement element) {
		SAXText t = element.getTextNode();
		return t != null && t.getValue() != null && t.getValue().trim().length() > 0;
	}

	public static boolean hasProducesChild(SAXElement element) {
		List<SAXElement> cs = element.getChildElements();
		for (SAXElement c: cs) {
			if(containsEEPackage(c) && "Produces".equals(c.getLocalName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasParametersOrArrayChild(SAXElement element) {
		List<SAXElement> cs = element.getChildElements();
		for (SAXElement c: cs) {
			if(isParameters(c) || isArray(c)) return true;
		}
		return false;
	}

	/**
	 * 
	 * @param def
	 * @param type
	 * @param name method name or null for constructor
	 * @return
	 */
	public static IMethod findMethod(SeamMethodDefinition def, IType type, String name, IRootDefinitionContext context) throws JavaModelException {
		IMethod[] ms = type.getMethods();
		for (IMethod m: ms) {
			if((name == null && m.isConstructor()) || (name != null && name.equals(m.getElementName()))) {
				if(sameParameterTypes(def, m, context)) return m;
			}
		}
		return null;
	}

	static boolean sameParameterTypes(SeamMethodDefinition def, IMethod m, IRootDefinitionContext context) throws JavaModelException {
		String[] paramTypes = m.getParameterTypes();
		if(paramTypes.length != def.getParameters().size()) return false;
		if(paramTypes.length == 0) return true;
		for (int i = 0; i < paramTypes.length; i++) {
			String paramType = paramTypes[i];
			SeamParameterDefinition p = def.getParameters().get(i);
			if(p.getDimensions() != Signature.getArrayCount(paramType)) {
				return false;
			}
			IParametedType pt = context.getProject().getTypeFactory().getParametedType(m, paramType);
			if(pt == null || p.getType() == null) return false;
			if(!pt.getType().getFullyQualifiedName().equals(p.getType().getFullyQualifiedName())) return false; 
		}
		return true;
	}

}
