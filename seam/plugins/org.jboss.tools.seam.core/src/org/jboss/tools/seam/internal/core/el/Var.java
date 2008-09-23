package org.jboss.tools.seam.internal.core.el;

import java.util.List;

/**
 * Represents "var"/"value" attributes.
 * @author Alexey Kazakov
 */
public class Var {
	String name;
	String value;
	ELToken elToken;
	String resolvedValue;
	ELToken resolvedElToken;
	int declOffset;
	int declLength;
	
	/**
	 * Constructor
	 * @param name - value of "var" attribute. 
	 * @param value - value of "value" attribute.
	 */
	public Var(String name, String value, int declOffset, int declLength) {
		super();
		this.name = name;
		this.value = value;
		elToken = parseEl(value);
		this.declOffset = declOffset;
		this.declLength = declLength;
	}

	/**
	 * Constructor
	 * @param name - value of "var" attribute. 
	 * @param value - value of "value" attribute.
	 */
	public Var(String name, String value) {
		this(name, value, 0, 0);
	}		
	
	ELToken parseEl(String el) {
		if(el.length()>3 && el.startsWith("#{") && el.endsWith("}")) {
			String elBody = el.substring(0, el.length()-1).substring(2);
			SeamELTokenizer elTokenizer = new SeamELTokenizer(elBody);
			List<ELToken> tokens = elTokenizer.getTokens();
			for (ELToken token : tokens) {
				if(token.getType()==ELToken.EL_VARIABLE_TOKEN) {
					return token;
				}
			}
		}
		return null;
	}

	/**
	 * Sets value to new resolved EL which we got as result of parsing value.
	 * For example:
	 * <h:datatable value="#{list}" var="item">
	 * 	<h:dataTable value="#{item.anotherList}" var="innerItem">
	 * 		...
	 * 	</h:dataTable>
	 * </h:dataTable>
	 * Original El is #{item.anotherList}
	 * Resolved El is #{list.iterator().next().anotherList}
	 * It's very useful for nested vars.
	 * @param newEl
	 */
	public void resolveValue(String newEl) {
		resolvedValue = newEl;
		resolvedElToken = parseEl(newEl);
	}

	/**
	 * @return parsed EL from "value" attribute. Returns null if EL is not valid.
	 */
	public ELToken getElToken() {
		return elToken;
	}

	/**
	 * @return parsed resolved EL from "value" attribute. May be null.
	 */
	public ELToken getResolvedElToken() {
		return resolvedElToken;
	}

	/**
	 * @return name of variable. 
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return value of variable. It's EL.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return resolved value of variable. It's EL. May be null.
	 */
	public String getResolvedValue() {
		return resolvedValue;
	}
	
	/**
	 * @return offset of the var declaration
	 */
	public int getDeclarationOffset() {
		return declOffset;
	}
	
	/**
	 * @return length of the var declaration
	 */
	public int getDeclarationLength() {
		return declLength;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Var) {
			Var compare = (Var)obj;
			String str = getName();
			if (str != null) {
				if (!str.equals(compare.getName()))
					return false;
			} else {
				if (compare.getName() != null)
					return false;
			}
			str = getValue();
			return (str != null ?
				str.equals(compare.getValue()) :
				compare.getValue() == null);
		}
		return false;
	}

}
