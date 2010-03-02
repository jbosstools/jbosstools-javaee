package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.internal.core.impl.ParametedType;
import org.jboss.tools.common.model.util.EclipseJavaUtil;

public class ParametedTypeFactory {
	Map<String, ParametedType> cache = new HashMap<String, ParametedType>();

	public ParametedType getParametedType(IType context, String typeSignature) throws JavaModelException {
		if(typeSignature == null) return null;
		String key = context == null || context.isBinary() || "QObject;".equals(typeSignature) ? typeSignature : context.getFullyQualifiedName() + "+" + typeSignature;
		if(cache.containsKey(key)) return cache.get(key);
		ParametedType result = new ParametedType();
		result.setFactory(this);
		result.setSignature(typeSignature);

		typeSignature = typeSignature.substring(result.getArrayPrefix().length());

		int startToken = typeSignature.indexOf('<');
		if(startToken < 0) {
			String resovedTypeName = EclipseJavaUtil.resolveTypeAsString(context, typeSignature);
			if(resovedTypeName == null) return null;
			if(!context.isBinary()) {
				result.setSignature(result.getArrayPrefix() + "Q" + resovedTypeName + ";");
			}
			IType type = EclipseJavaUtil.findType(context.getJavaProject(), resovedTypeName);
			if(type != null) {
				result.setType(type);
				cache.put(key, result);
				return result;
			}
		} else {
			int endToken = typeSignature.lastIndexOf('>');
			if(endToken < startToken) return null;
			String typeName = typeSignature.substring(0, startToken) + typeSignature.substring(endToken + 1);
			String params = typeSignature.substring(startToken + 1, endToken);
			String resovedTypeName = EclipseJavaUtil.resolveTypeAsString(context, typeName);
			if(resovedTypeName == null) return null;
			IType type = EclipseJavaUtil.findType(context.getJavaProject(), resovedTypeName);
			if(type != null) {
				result.setType(type);
				StringBuffer newParams = new StringBuffer();
				StringTokenizer st = new StringTokenizer(params, ",");
				while(st.hasMoreTokens()) {
					String paramSignature = st.nextToken();
					ParametedType param = getParametedType(context, paramSignature);
					if(param == null) {
						param = new ParametedType();
						param.setSignature(paramSignature);
					}
					result.addParameter(param);
					if(newParams.length() > 0) newParams.append(',');
					newParams.append(param.getSignature());
				}
				if(!context.isBinary()) {
					result.setSignature(result.getArrayPrefix() + "Q" + resovedTypeName + '<' + newParams + '>' + ';');
				}
				cache.put(key, result);
				return result;
			}
		}
		return null;
	}

	public void clean() {
		cache.clear();
	}

}
