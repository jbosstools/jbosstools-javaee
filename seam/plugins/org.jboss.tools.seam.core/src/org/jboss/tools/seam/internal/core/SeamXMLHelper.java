package org.jboss.tools.seam.internal.core;

import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.seam.core.IValueInfo;
import org.jboss.tools.seam.internal.core.scanner.java.ValueInfo;
import org.jboss.tools.seam.internal.core.scanner.xml.XMLValueInfo;
import org.w3c.dom.Element;

public class SeamXMLHelper implements SeamXMLConstants {
	
	public static void saveValueInfo(Element parent, IValueInfo value, Properties context) {
		value.toXML(parent, context);
	}
	
	public static IValueInfo loadValueInfo(Element parent, Properties context) {
		Element c = XMLUtilities.getUniqueChild(parent, TAG_VALUE_INFO);
		if(c == null) return null;
		IValueInfo v = null;
		if(CLS_XML.equals(c.getAttribute(ATTR_CLASS))) {
			v = new XMLValueInfo();
			v.loadXML(c, context);
			if(((XMLValueInfo)v).getObject() == null) {
				v = new ValueInfo();
				//that may be a problem
				((ValueInfo)v).setValue("");
			}
		} else {
			v = new ValueInfo();
			v.loadXML(c, context);
		}
		
		return v;
	}
	
	public static void saveModelObject(Element element, XModelObject object, Properties context) {
		if(object == null) return;
		String path = object.getPath();
		if(path == null) return;
		XModelObject base = (XModelObject)context.get(SeamXMLConstants.KEY_MODEL_OBJECT);
		if(base != null && base.getModel() == object.getModel()) {
			String basePath = base.getPath();
			if(path.startsWith("" + basePath + "/")) {
				path = path.substring(basePath.length());
			} else if(path.equals(basePath)) {
				path = ".";
			}
		}
		
		if(path != null) element.setAttribute(ATTR_PATH, path);
		IProject p = EclipseResourceUtil.getProject(object);
		if(p == null) return;
		element.setAttribute(ATTR_PROJECT, p.getName());
	}

	public static void saveModelObject(Element parent, XModelObject object, String child, Properties context) {
		Element element = XMLUtilities.createElement(parent, child);
		element.setAttribute(ATTR_CLASS, CLS_MODEL_OBJECT);
		saveModelObject(element, object, context);
	}

	public static XModelObject loadModelObject(Element element, Properties context) {
		String path = element.getAttribute(ATTR_PATH);
		XModelObject base = (XModelObject)context.get(SeamXMLConstants.KEY_MODEL_OBJECT);
		if(path == null || path.length() == 0) {
			//TODO reporting
			return null;
		} else if(path.equals(".")) {
			if(base == null) {
				//TODO reporting
			}
			return base;
		}
		if(path.startsWith("/")) {
			if(base != null) {
				if(base.getChildByPath(path.substring(1)) == null) {
					//TODO reporting
				}
				return base.getChildByPath(path.substring(1));
			}
			//TODO reporting
			return null;
		}
		IProject project = loadProject(element, context);
		if(project == null || !project.isAccessible()) return null;
		XModel model = InnerModelHelper.createXModel(project);
		if(model == null) return null;
		return model.getByPath(path);
	}

	public static XModelObject loadModelObject(Element parent, String child, Properties context) {
		Element element = XMLUtilities.getUniqueChild(parent, child);
		if(element == null) return null;
		return loadModelObject(element, context);
	}
	
	public static void saveType(Element element, IType type, Properties context) {
		if(type == null) return;
		if(context != null && type == context.get(ATTR_TYPE)) return;
		element.setAttribute(ATTR_PROJECT, type.getJavaProject().getProject().getName());
		element.setAttribute(ATTR_TYPE, type.getFullyQualifiedName());
	}

	public static void saveType(Element parent, IType type, String child, Properties context) {
		if(type == null) return;
		if(context != null && type == context.get(ATTR_TYPE)) return;
		Element element = XMLUtilities.createElement(parent, child);
		element.setAttribute(ATTR_CLASS, CLS_TYPE);
		saveType(element, type, context);
	}
	
	public static IProject loadProject(Element element, Properties context) {
		String project = element.getAttribute(ATTR_PROJECT);
		if(project == null || project.length() == 0) return null;
		return ResourcesPlugin.getWorkspace().getRoot().getProject(project);
	}
	
	public static IType loadType(Element element, Properties context) {
		String name = element.getAttribute(ATTR_TYPE);
		if(name == null || name.length() == 0) {
			if(context != null && context.containsKey(ATTR_TYPE)) {
				return (IType)context.get(ATTR_TYPE);
			}
			//TODO reporting
			return null;
		}
		IProject project = loadProject(element, context);
		if(project == null || !project.isAccessible()) return null;
		IJavaProject jp = JavaCore.create(project);
		if(jp != null) {
			try {
				IType type = jp.findType(name.replace('$', '.'));
				if(type == null && name.indexOf('$') >= 0) {
					int ii = name.lastIndexOf('.');
					String pack = (ii < 0) ? "" : name.substring(0, ii);
					String cls = name.substring(ii + 1);
					type = jp.findType(pack, cls.replace('$', '.'), new NullProgressMonitor());
				}
				return type;
			} catch (JavaModelException e) {
				//ignore
			}
		}		
		return null;
	}

	public static void saveField(Element element, IField field, Properties context) {
		if(field == null) return;
		saveType(element, field.getDeclaringType(), context);
		element.setAttribute(ATTR_NAME, field.getElementName());
	}

	public static void saveField(Element parent, IField field, String child, Properties context) {
		if(field == null) return;
		Element element = XMLUtilities.createElement(parent, child);
		element.setAttribute(ATTR_CLASS, CLS_FIELD);
		saveField(element, field, context);
	}
	
	public static IField loadField(Element element, Properties context) {
		IType type = loadType(element, context);
		if(type == null) return null;
		String name = element.getAttribute(ATTR_NAME);
		if(name == null || name.length() == 0) return null;
		return type.getField(name);
	}

	public static void saveMethod(Element element, IMethod method, Properties context) {
		if(method == null) return;
		saveType(element, method.getDeclaringType(), context);
		element.setAttribute(ATTR_NAME, method.getElementName());
		String[] s = method.getParameterTypes();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length; i++) sb.append(s[i]).append(',');
		element.setAttribute(ATTR_PARAMS, sb.toString());
	}

	public static void saveMethod(Element parent, IMethod method, String child, Properties context) {
		if(method == null) return;
		Element element = XMLUtilities.createElement(parent, child);
		element.setAttribute(ATTR_CLASS, CLS_METHOD);
		saveMethod(element, method, context);
	}
	
	public static IMethod loadMethod(Element element, Properties context) {
		IType type = loadType(element, context);
		if(type == null) return null;
		String name = element.getAttribute(ATTR_NAME);
		if(name == null || name.length() == 0) return null;
		String params = element.getAttribute(ATTR_PARAMS);
		String[] ps = new String[0];
		if(params != null && params.length() > 0) {
			ps = params.split(",");
		}
		return type.getMethod(name, ps);
	}

	public static void saveMap(Element parent, Map<String, IValueInfo> map, String child, Properties context) {
		if(map == null || map.isEmpty()) return;
		Element element = XMLUtilities.createElement(parent, child);
		for (String name: map.keySet()) {
			IValueInfo value = map.get(name);
			Element c =  XMLUtilities.createElement(element, TAG_ENTRY);
			c.setAttribute(ATTR_NAME, name);
			if(value == null) continue;
			value.toXML(c, context);
		}
	}
	
	public static void loadMap(Element parent, Map<String, IValueInfo> map, String child, Properties context) {
		Element element = XMLUtilities.getUniqueChild(parent, child);
		if(element == null) return;
		Element[] cs = XMLUtilities.getChildren(element, TAG_ENTRY);
		for (int i = 0; i < cs.length; i++) {
			String name = cs[i].getAttribute(ATTR_NAME);
			IValueInfo value = loadValueInfo(cs[i], context);
			if(name != null && value != null) {
				map.put(name, value);
			}
		}
	}
}
