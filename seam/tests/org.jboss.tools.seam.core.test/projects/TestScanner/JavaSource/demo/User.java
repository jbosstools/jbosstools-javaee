/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package demo;

import javax.persistence.Entity;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.Component;

/**
 * Created by JBoss Developer Studio
 */

@Name("myUser")
@Scope(ScopeType.APPLICATION)
@Entity
@Install(precedence=Install.FRAMEWORK)

public class User {
	
	@Name("inner_JBIDE_1374")
	public static class Inner {
		private String innerName;
		
		public String getInnerName() {
			return innerName;
		}
		
		public void setInnerName(String s) {
			innerName = s;
		}
	}

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
	public User unwrapMethod() {
		return new User();
	}
	
	@Create @Destroy
	public void createAndDestroyMethod(Component c) {
	}
	
	@Factory(value="myFactory", scope=ScopeType.SESSION)
	public User testFactory() {
		return new User();
	}
	
	@Factory
	public User getMyFactory2() {
		return new User();
	}

}
