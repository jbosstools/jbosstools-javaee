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
package org.jboss.tools.cdi.ui.test.search;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.common.base.test.kb.QueryParticipantTestUtils;
import org.jboss.tools.common.base.test.kb.QueryParticipantTestUtils.MatchStructure;
import org.jboss.tools.jst.web.kb.refactoring.ELReferencesQueryParticipant;

public class ELReferencesQueryParticipantTest extends TCKTest{
	
	public void testELReferencesQueryParticipantForType() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/tck/WebContent/test.jsp", "sheep"));
		matches.add(new MatchStructure("/tck/WebContent/tests/lookup/el/integration/JSPTestPage.jsp", "sheep"));
		matches.add(new MatchStructure("/tck/WebContent/tests/lookup/el/integration/JSFTestPage.jsp", "sheep"));
		matches.add(new MatchStructure("/tck/WebContent/elValidation1.xhtml", "sheep"));
		
		QueryParticipantTestUtils.testSearchParticipant(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/lookup/el/integration/Sheep.java",
				QueryParticipantTestUtils.TYPE_SEARCH,
				"Sheep",
				"",
				new ELReferencesQueryParticipant(),
				matches);
	}
	
	public void testELReferencesQueryParticipantForProducerField() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/tck/JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/Zoo.java", "aaaf"));
		
		QueryParticipantTestUtils.testSearchParticipant(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/Zoo.java",
				QueryParticipantTestUtils.FIELD_SEARCH,
				"petShop",
				"",
				new ELReferencesQueryParticipant(),
				matches);
	}
	
	public void testELReferencesQueryParticipantForProducerMethod() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/tck/JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/Zoo.java", "aaam"));

		QueryParticipantTestUtils.testSearchParticipant(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/Zoo.java",
				QueryParticipantTestUtils.METHOD_SEARCH,
				"getPetShop",
				"",
				new ELReferencesQueryParticipant(),
				matches);
	}
	
	public void testELReferencesQueryParticipantForMethod() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/tck/WebContent/test.jsp", "name"));
		matches.add(new MatchStructure("/tck/WebContent/tests/lookup/el/integration/JSPTestPage.jsp", "name"));
		matches.add(new MatchStructure("/tck/WebContent/tests/lookup/el/integration/JSFTestPage.jsp", "name"));
		matches.add(new MatchStructure("/tck/WebContent/elValidation1.xhtml", "name"));

		QueryParticipantTestUtils.testSearchParticipant(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/lookup/el/integration/Sheep.java",
				QueryParticipantTestUtils.METHOD_SEARCH,
				"getName",
				"",
				new ELReferencesQueryParticipant(),
				matches);
	}
	
	public void testELReferencesQueryParticipantForType2() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/tck/WebContent/search.jsp", "mySearchableBean"));
		matches.add(new MatchStructure("/tck/WebContent/search.jsp", "mySearchableBean"));
		
		QueryParticipantTestUtils.testSearchParticipant(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/search/MySearchableBean.java",
				QueryParticipantTestUtils.TYPE_SEARCH,
				"MySearchableBean",
				"",
				new ELReferencesQueryParticipant(),
				matches);
	}
	
	public void testELReferencesQueryParticipantForMethod2() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/tck/WebContent/search.jsp", "sFoo1"));
		
		QueryParticipantTestUtils.testSearchParticipant(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/search/MySearchableBean.java",
				QueryParticipantTestUtils.METHOD_SEARCH,
				"sFoo1",
				"",
				new ELReferencesQueryParticipant(),
				matches);
	}

	public void testELReferencesQueryParticipantForField() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/tck/WebContent/search.jsp", "sFoo"));
		matches.add(new MatchStructure("/tck/WebContent/search.jsp", "sFoo"));
		
		QueryParticipantTestUtils.testSearchParticipant(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/search/MySearchableBean.java",
				QueryParticipantTestUtils.FIELD_SEARCH,
				"sFoo",
				"",
				new ELReferencesQueryParticipant(),
				matches);
	}

}
