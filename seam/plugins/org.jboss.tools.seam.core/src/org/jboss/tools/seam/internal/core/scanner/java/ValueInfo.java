package org.jboss.tools.seam.internal.core.scanner.java;

import java.util.List;

import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.jboss.tools.seam.core.IValueInfo;

public class ValueInfo implements IValueInfo {
	String value;
	int valueStartPosition;
	int valueLength;
	
	/**
	 * Factory method.
	 * @param node
	 * @param name
	 * @return
	 */
	public static ValueInfo getValueInfo(Annotation node, String name) {
		if(name == null) name = "value";
		if(node instanceof SingleMemberAnnotation) {
			if(name == null || "value".equals(name)) {
				SingleMemberAnnotation m = (SingleMemberAnnotation)node;
				ValueInfo result = new ValueInfo();
				Expression exp = m.getValue();
				result.valueLength = exp.getLength();
				result.valueStartPosition = exp.getStartPosition();
				result.value = checkExpression(exp);				
				return result;
			}
			return null;
		} else if(node instanceof NormalAnnotation) {
			NormalAnnotation n = (NormalAnnotation)node;
			List<?> vs = n.values();
			if(vs != null) for (int i = 0; i < vs.size(); i++) {
				MemberValuePair p = (MemberValuePair)vs.get(i);
				String pname = p.getName().getIdentifier();
				if(!name.equals(pname)) continue;
				ValueInfo result = new ValueInfo();
				Expression exp = p.getValue();
				result.valueLength = exp.getLength();
				result.valueStartPosition = exp.getStartPosition();
				result.value = checkExpression(exp);				
				return result;
			}
			return null;			
		}
		return null;		
	}
	
	public ValueInfo() {
	}
	
	public String getValue() {
		return value;
	}
	
	public int getStartPosition() {
		return valueStartPosition;
	}
	
	public int getLength() {
		return valueLength;
	}

	static String checkExpression(Expression exp) {
		if(exp == null) return null;
		if(exp instanceof StringLiteral) {
			return ((StringLiteral)exp).getLiteralValue();
		} else if(exp instanceof QualifiedName) {
			return exp.toString();
		}
		return exp.toString();
	}
	
	public void setValue(String value) {
		this.value = value;
	}

}
