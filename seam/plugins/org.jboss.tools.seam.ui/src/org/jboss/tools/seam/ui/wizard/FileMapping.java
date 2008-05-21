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
package org.jboss.tools.seam.ui.wizard;

/**
 * @author Alexey Kazakov
 */
public class FileMapping {

	private String source;
	private String destination;
	private TYPE deployType;
	private boolean test;

	public static enum TYPE {
		WAR("war"),
		EAR("ear");

		private String stringType;

		private TYPE(String type) {
			stringType = type;
		}

		@Override
		public String toString() {
			return stringType;
		}

		public boolean equalsString(String type) {
			if(type==null) {
				return false;
			}
			return stringType.equals(type);
		}
	}

	public FileMapping(String source, String destination, TYPE deployType, boolean test) {
		super();
		this.source = source;
		this.destination = destination;
		this.deployType = deployType;
		this.test = test;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public boolean isWar() {
		return deployType == TYPE.WAR;
	}

	public boolean isEar() {
		return deployType == TYPE.EAR;
	}

	public TYPE getDeployType() {
		return deployType;
	}

	public void setDeployType(TYPE deployType) {
		this.deployType = deployType;
	}

	public boolean isTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}
}