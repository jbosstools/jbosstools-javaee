/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.uiutils;

import java.util.Collection;
import java.util.List;

public class CollectionsUtil {

	private CollectionsUtil() {
		throw new AssertionError();
	}
	
	/**
	 * 
	 * @param expectedAffectedFiles
	 * @param affectedFiles
	 * @return
	 */
	public static boolean compareTwoCollectionsEquality(
			Collection<String> expectedAffectedFiles,
			Collection<String> affectedFiles) {
		int counter = 0;
		for (String f1 : affectedFiles) {
			for (String f2 : expectedAffectedFiles) {
				if (f1.equals(f2)) {
					counter++;
				}
			}
		}
		return expectedAffectedFiles.size() == counter;
	}
	
	public static boolean checkNoMatch(List<String> proposalList, List<String> nonexpectedList) {
		boolean noMatch = true;
		for (String proposeValue : proposalList) {
			for (String nonexpectedValue : nonexpectedList) {
				if (proposeValue.equals(nonexpectedValue)) {
					noMatch = false;
					break;
				}
			}
		}
		return noMatch;
	}
	
	public static boolean checkMatch(List<String> proposalList, List<String> expectedList) {
		boolean match = false;
		for (String expectedValue : expectedList) {
			match = false;
			for (String proposeValue : proposalList) {
				if (expectedValue.equals(proposeValue)) {
					match = true;
				}
			}
			if (!match) return match;
		}
		return match;
	}
	
}
