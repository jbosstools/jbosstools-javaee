/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.verification.vrules;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.verification.vrules.*;
import org.jboss.tools.common.verification.vrules.layer.VObjectImpl;

public class JSFCheckClass extends JSFDefaultCheck {

	public VResult[] check(VObject object) {
		XModelObject o = ((VObjectImpl)object).getModelObject();
		String objectAttribute = rule.getProperty("objectAttribute");
		String attr = getAttribute();
		if(objectAttribute == null || objectAttribute.length() == 0) objectAttribute = attr;
		if(attr == null) return null;
		String value = o.getAttributeValue(objectAttribute);
		if(value.length() == 0 || isJavaLang(value)) return null;
		if(isPrimitive(value)) return allowsPrimitive() ? null : fire(object, attr, attr, value);
		IType type = object.getModel().getValidType(value);
		if(type != null) {
			String mustImpl = null;
			try { mustImpl = checkImplements(object, type); } catch (Exception e) {}
			if(mustImpl != null) return fireImplements(object, attr, attr, value, mustImpl);
			String mustExtend = null;
			try { mustExtend = checkExtends(object, type); } catch (Exception e) {}
			if(mustExtend != null) return fireExtends(object, attr, attr, value, mustExtend);
			return null;
		}
		return fire(object, attr, attr, value);
	}
	
	private String checkImplements(VObject object, IType type) throws Exception {
		if("java.lang.Class".equals(type.getFullyQualifiedName())) return null;
		String impl = rule.getProperty("implements");
		if(impl == null || impl.length() == 0) return null;
		String[] is = type.getSuperInterfaceNames();
		for (int i = 0; i < is.length; i++) {
			String f = EclipseJavaUtil.resolveType(type, is[i]);
			if(f != null && f.equals(impl)) return null; 
		}
		if(type.isInterface()) return impl;
		String f = type.getSuperclassName();
		if(f == null || f.length() == 0 || "java.lang.Object".equals(f)) return impl;
		f = EclipseJavaUtil.resolveType(type, f);
		if(f == null || f.length() == 0 || "java.lang.Object".equals(f)) return impl;
		type = object.getModel().getValidType(f);
		if(type == null) return impl;
		return checkImplements(object, type);
	}
	
	private String checkExtends(VObject object, IType type) throws Exception {
		if(type.isInterface()) return null;
		if("java.lang.Class".equals(type.getFullyQualifiedName())) return null;
		String ext = rule.getProperty("extends");
		if(ext == null || ext.length() == 0) return null;
		String f = type.getSuperclassName();
		if(f == null || f.length() == 0 || "java.lang.Object".equals(f)) return ext;
		if(f.equals(ext)) return null;
		f = EclipseJavaUtil.resolveType(type, f);
		if(f == null || f.length() == 0 || "java.lang.Object".equals(f)) return ext;
		if(f.equals(ext)) return null;
		type = object.getModel().getValidType(f);
		if(type == null) return ext;
		return checkExtends(object, type);
	}
	
	protected String getAttribute() {
		return rule.getProperty("attribute");
	}
	
	private boolean allowsPrimitive() {
		return "true".equals(rule.getProperty("allow-primitive"));
	}
	
	private boolean isPrimitive(String value) {
		return ".int.boolean.char.byte.double.float.long.short.".indexOf("." + value + ".") >= 0;
	}

	private boolean isJavaLang(String value) {
		if(value.indexOf('.') < 0) {
			return ".String.Integer.Boolean.Character.Byte.Double.Float.Long.Short.".indexOf("." + value + ".") >= 0;
		} else if(value.startsWith("java.lang.")) {
			return isJavaLang(value.substring(10));
		} else {
			return false;
		}
	}

	protected VResult[] fire(VObject object, String id, String attr, String value) {
		Object[] os = new Object[] {attr, value};
		VResult result = rule.getResultFactory().getResult(id, object, attr, object, attr, os);
		return new VResult[] {result};
	}

	protected VResult[] fireImplements(VObject object, String id, String attr, String value, String interfaceName) {
		Object[] os = new Object[] {attr, value, interfaceName};
		VResult result = rule.getResultFactory().getResult(id + ".implements", object, attr, object, attr, os);
		return new VResult[] {result};
	}

	protected VResult[] fireExtends(VObject object, String id, String attr, String value, String superName) {
		Object[] os = new Object[] {attr, value, superName};
		VResult result = rule.getResultFactory().getResult(id + ".extends", object, attr, object, attr, os);
		return new VResult[] {result};
	}
}
