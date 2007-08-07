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
package demo;

import javax.persistence.Entity;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;

/**
 * Created by Red Hat Developer Studio
 */

@Name("myUser")
@Scope(ScopeType.APPLICATION)
@Entity
@Install(precedence=Install.FRAMEWORK)

public class User {

	private String name;
	
	@Out
	private String address = "";
	
	@In
	private String payment = "";

	/**
	 * @return User Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param User Name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getPayment() {
		return payment;
	}
	
	@Unwrap
	public void unwrapMethod() {
	}
	
	@Create @Destroy
	public void createAndDestroyMethod() {
	}

}
