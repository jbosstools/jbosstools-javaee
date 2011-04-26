package org.jboss.tools.cdi.seam.config.core.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigConstants;
import org.jboss.tools.cdi.seam.config.core.scanner.SAXElement;

public class Util {
	public static Map<String, String> EE_TYPES = new HashMap<String, String>();
	
	static {
		String[] JAVA_LANG = {"Integer", "Short", "Long", "String", "Character", "Byte", "Boolean"};
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
		
	}

	public static boolean isConfigRelevant(SAXElement element) {
		String uri = element.getURI();
		return (uri != null && uri.startsWith(CDISeamConfigConstants.URI_PREFIX));
	}

	public static IType resolveType(SAXElement element, CDICoreNature project) {
		String uri = element.getURI();
		if(uri == null || !uri.startsWith(CDISeamConfigConstants.URI_PREFIX)) {
			return null;
		}
		String name = element.getLocalName();
		String[] packages = getPackages(uri);
		for (String pkg: packages) {
			if(pkg.length() == 0) continue;
			String typeName = null;
			if(pkg.equals("ee")) {
				typeName = Util.EE_TYPES.get(name);
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

	public static IMember resolveMember(IType type, SAXElement element) throws JavaModelException {
		String name = element.getLocalName();
		IField f = type.getField(name);
		if(f != null && f.exists()) {
			return f;
		}
		IMethod[] ms = type.getMethods();
		for (IMethod m: ms) {
			if(name.equals(m.getElementName())) {
				//do more checks
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

	public static boolean containsEEPackage(String uri) {
		String[] ps = getPackages(uri);
		for (String p: ps) if("ee".equals(p)) {
			return true;
		}
		return false;
	}

}
