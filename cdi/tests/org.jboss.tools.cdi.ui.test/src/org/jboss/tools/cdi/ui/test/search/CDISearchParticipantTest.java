package org.jboss.tools.cdi.ui.test.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.ui.search.JavaSearchScopeFactory;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.search.ui.text.Match;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.ui.marker.MarkerResolutionUtils;
import org.jboss.tools.cdi.ui.search.CDIMatch;
import org.jboss.tools.common.EclipseUtil;

public class CDISearchParticipantTest  extends TCKTest {
	private static final int FIELD_SEARCH = 1;
	private static final int METHOD_SEARCH = 2;
	private static final int TYPE_SEARCH = 3;
	private static final int PARAMETER_SEARCH = 4;
	
	private void testSearchParticipant(IFile file, int searchType, String elementName, String parameterName, IQueryParticipant participant, List<MatchStructure> matches){
		try{
			ICompilationUnit compilationUnit = EclipseUtil.getCompilationUnit(file);
			IJavaElement element = null;
			
			IType type = compilationUnit.findPrimaryType();
			
			if(searchType == FIELD_SEARCH){
				element = type.getField(elementName);
			}else if(searchType == METHOD_SEARCH){
				element = type.getMethod(elementName, new String[]{});
			}else if(searchType == TYPE_SEARCH){
				element = type;
			}else if(searchType == PARAMETER_SEARCH){
				IMethod method = type.getMethod(elementName, new String[]{});
				element = MarkerResolutionUtils.getParameter(method, parameterName);
			}
			
			if(element != null){
				CDISearchRequestor requestor = new CDISearchRequestor();
				
				JavaSearchScopeFactory factory= JavaSearchScopeFactory.getInstance();
				IJavaSearchScope scope= factory.createWorkspaceScope(true);
				String description= factory.getWorkspaceScopeDescription(true);
				QuerySpecification specification = new ElementQuerySpecification(element, IJavaSearchConstants.REFERENCES, scope, description);
				
				participant.search(requestor, specification, new NullProgressMonitor());
				
				List<Match> matchesForCheck = requestor.getMatches();
				
				checkMatches(matchesForCheck, matches);
			}else
				fail("Java Element not found");
		}catch(CoreException ex){
			fail("Core exception");
		}
	}
	
	private void checkMatches(List<Match> matchesForCheck, List<MatchStructure> matchList) throws CoreException {
		assertEquals("There is unexpected number of matches",matchList.size(), matchesForCheck.size());

		for(Match match : matchesForCheck){
			assertTrue("Match must be CDIMatch", match instanceof CDIMatch);
			MatchStructure ms = findMatch(matchList, (CDIMatch)match);
			assertNotNull("Match not found", ms);
			ms.checked = true;
		}
		
		for(MatchStructure ms : matchList){
			assertTrue("Not all matches found", ms.checked);
		}
	}
	
	protected MatchStructure findMatch(List<MatchStructure> matchList, CDIMatch match){
		for(MatchStructure ms : matchList){
			if(!ms.checked && ms.type.equals(match.getCDIElement().getClass()) && ms.name.equals(match.getLabel()))
				return ms;
		}
		return null;
	}

	
	class CDISearchRequestor implements ISearchRequestor{
		ArrayList<Match> matches = new ArrayList<Match>();
		
		public void reportMatch(Match match){
			matches.add(match);
		}
		
		public List<Match> getMatches(){
			return matches;
		}
	}
	
	class MatchStructure{
		String type; // CDIElement.getClass()
		String name; // label
		boolean checked;
		
		public MatchStructure(String type, String name){
			this.type = type;
			this.name = name;
			checked = false;
		}
	}
	
	public void testInjectionPointQueryParticipant(){
		
		//testSearchParticipant();
	}

	public void testCDIBeanQueryParticipant(){
		
	}
}
