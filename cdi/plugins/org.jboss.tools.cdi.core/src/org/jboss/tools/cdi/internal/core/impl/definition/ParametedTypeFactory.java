package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.StringTokenizer;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.internal.core.impl.ParametedType;
import org.jboss.tools.common.model.util.EclipseJavaUtil;

public class ParametedTypeFactory {

	public static ParametedType getParametedType(IType context, String typeSignature) throws JavaModelException {
		if(typeSignature == null) return null;
		ParametedType result = new ParametedType();
		result.setSignature(typeSignature);
		int startToken = typeSignature.indexOf('<');
		if(startToken < 0) {
			String resovedTypeName = EclipseJavaUtil.resolveTypeAsString(context, typeSignature);
			if(resovedTypeName == null) return null;
			result.setSignature(resovedTypeName);
			IType type = EclipseJavaUtil.findType(context.getJavaProject(), resovedTypeName);
			if(type != null) {
				result.setType(type);
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
				result.setSignature(resovedTypeName + '<' + params + '>');
				StringTokenizer st = new StringTokenizer(params, ",");
				while(st.hasMoreTokens()) {
					String paramSignature = st.nextToken();
					ParametedType param = getParametedType(context, paramSignature);
					if(param == null) {
						param = new ParametedType();
						param.setSignature(paramSignature);
					}
					result.addParameter(param);
				}
				return result;
			}
		}
		return null;
	}
}
