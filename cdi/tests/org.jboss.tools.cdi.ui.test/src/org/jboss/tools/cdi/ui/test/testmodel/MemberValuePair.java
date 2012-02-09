/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.test.testmodel;

import org.eclipse.jdt.core.IMemberValuePair;

public class MemberValuePair implements IMemberValuePair {
	private String memberName;
	private Object value;
	private int valueKind;
	
	public MemberValuePair(String memberName, Object value, int valueKind){
		this.memberName = memberName;
		this.value = value;
		this.valueKind = valueKind;
	}

	@Override
	public String getMemberName() {
		return memberName;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public int getValueKind() {
		return valueKind;
	}

}
