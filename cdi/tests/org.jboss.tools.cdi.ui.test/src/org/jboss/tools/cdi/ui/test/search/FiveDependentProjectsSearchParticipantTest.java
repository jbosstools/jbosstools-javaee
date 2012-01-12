package org.jboss.tools.cdi.ui.test.search;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.ui.search.JavaSearchScopeFactory;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.search.ui.text.Match;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.ICDIElement;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
import org.jboss.tools.cdi.internal.core.impl.EventBean;
import org.jboss.tools.cdi.internal.core.impl.InjectionPointField;
import org.jboss.tools.cdi.internal.core.impl.InjectionPointParameter;
import org.jboss.tools.cdi.internal.core.impl.ObserverMethod;
import org.jboss.tools.cdi.ui.search.CDIBeanQueryParticipant;
import org.jboss.tools.cdi.ui.search.CDIMatch;
import org.jboss.tools.cdi.ui.search.InjectionPointQueryParticipant;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.base.test.kb.QueryParticipantTestUtils;
import org.jboss.tools.common.base.test.kb.QueryParticipantTestUtils.MatchStructure;
import org.jboss.tools.jst.web.kb.refactoring.ELReferencesQueryParticipant;

public class FiveDependentProjectsSearchParticipantTest extends TestCase {
	IProject project1 = null;
	IProject project2 = null;
	IProject project3 = null;
	IProject project4 = null;
	IProject project5 = null;

	@Override
	protected void setUp() throws Exception {
		project1 = ResourcesPlugin.getWorkspace().getRoot().getProject("CDITest1");
		project2 = ResourcesPlugin.getWorkspace().getRoot().getProject("CDITest2");
		project3 = ResourcesPlugin.getWorkspace().getRoot().getProject("CDITest3");
		project4 = ResourcesPlugin.getWorkspace().getRoot().getProject("CDITest4");
		project5 = ResourcesPlugin.getWorkspace().getRoot().getProject("CDITest5");
	}
	private static final int FIELD_SEARCH = 1;
	private static final int METHOD_SEARCH = 2;
	private static final int TYPE_SEARCH = 3;
	private static final int PARAMETER_SEARCH = 4;
	
	private void testSearchParticipant(IProject project, String fileName, int searchType, String elementName, String parameterName, IQueryParticipant participant, List<MStructure> matches){
		IFile file = project.getFile(fileName);
		assertNotNull("File - "+fileName+" not found", file);
		try{
			ICompilationUnit compilationUnit = EclipseUtil.getCompilationUnit(file);

			assertNotNull("CompilationUnit not found", compilationUnit);

			IJavaElement element = null;
			
			IType type = compilationUnit.findPrimaryType();
			
			if(searchType == FIELD_SEARCH){
				element = type.getField(elementName);
			}else if(searchType == METHOD_SEARCH){
				element = getMethod(type, elementName);
			}else if(searchType == TYPE_SEARCH){
				element = type;
			}else if(searchType == PARAMETER_SEARCH){
				IMethod method = getMethod(type, elementName);
				element = CDIUtil.getParameter(method, parameterName);
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
	
	private IMethod getMethod(IType type, String name) throws JavaModelException{
		IMethod[] methods = type.getMethods();
		for(IMethod method : methods){
			if(method.getElementName().equals(name))
				return method;
		}
		return null;
	}
	
	private void checkMatches(List<Match> matchesForCheck, List<MStructure> matchList) throws CoreException {
//		for(Match match : matchesForCheck){
//			System.out.println(("Match found (class - "+((CDIMatch)match).getCDIElement().getClass()+" name - "+((CDIMatch)match).getLabel()+")"));
//		}
		
		for(Match match : matchesForCheck){
			assertTrue("Match must be CDIMatch", match instanceof CDIMatch);
			MStructure ms = findMatch(matchList, (CDIMatch)match);
			assertNotNull("Unexpected match found (class - "+((CDIMatch)match).getCDIElement().getClass()+" name - "+((CDIMatch)match).getLabel()+")", ms);
			ms.checked = true;
		}
		
		for(MStructure ms : matchList){
			assertTrue("Match not found (class - "+ms.type+" name - "+ms.name, ms.checked);
		}
	}
	
	protected MStructure findMatch(List<MStructure> matchList, CDIMatch match){
		for(MStructure ms : matchList){
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
	
	class MStructure{
		Class<? extends ICDIElement> type;
		String name; // label
		boolean checked;
		
		public MStructure(Class<? extends ICDIElement> type, String name){
			this.type = type;
			this.name = name;
			checked = false;
		}
	}
	
	public void testInjectionPointQueryParticipantInProject1(){
		ArrayList<MStructure> matches = new ArrayList<MStructure>();
		
		matches.add(new MStructure(ClassBean.class, "Base1"));
		
		testSearchParticipant(project1, "src/cdi/test/search1/Bean1.java", FIELD_SEARCH, "field1", "", new InjectionPointQueryParticipant(), matches);
	}

	public void testInjectionPointQueryParticipantInProject2(){
		ArrayList<MStructure> matches = new ArrayList<MStructure>();
		
		matches.add(new MStructure(ClassBean.class, "Base1"));
		matches.add(new MStructure(ClassBean.class, "Base2"));
		
		testSearchParticipant(project2, "src/cdi/test/search2/Bean2.java", FIELD_SEARCH, "field2", "", new InjectionPointQueryParticipant(), matches);
	}
	
	public void testInjectionPointQueryParticipantInProject3(){
		ArrayList<MStructure> matches = new ArrayList<MStructure>();
		
		matches.add(new MStructure(ClassBean.class, "Base1"));
		matches.add(new MStructure(ClassBean.class, "Base2"));
		matches.add(new MStructure(ClassBean.class, "Base3"));
		
		testSearchParticipant(project3, "src/cdi/test/search3/Bean3.java", FIELD_SEARCH, "field3", "", new InjectionPointQueryParticipant(), matches);
	}
	
	public void testInjectionPointQueryParticipantInProject4(){
		ArrayList<MStructure> matches = new ArrayList<MStructure>();
		
		matches.add(new MStructure(ClassBean.class, "Base1"));
		matches.add(new MStructure(ClassBean.class, "Base2"));
		matches.add(new MStructure(ClassBean.class, "Base4"));
		
		testSearchParticipant(project4, "src/cdi/test/search4/Bean4.java", FIELD_SEARCH, "field4", "", new InjectionPointQueryParticipant(), matches);
	}
	
	public void testInjectionPointQueryParticipantInProject5(){
		ArrayList<MStructure> matches = new ArrayList<MStructure>();
		
		matches.add(new MStructure(ClassBean.class, "Base1"));
		matches.add(new MStructure(ClassBean.class, "Base2"));
		matches.add(new MStructure(ClassBean.class, "Base4"));
		matches.add(new MStructure(ClassBean.class, "Base5"));
		
		testSearchParticipant(project5, "src/cdi/test/search5/Bean5.java", FIELD_SEARCH, "field5", "", new InjectionPointQueryParticipant(), matches);
	}

	public void testCDIBeanQueryParticipantInProject1(){
		ArrayList<MStructure> matches = new ArrayList<MStructure>();
		
		matches.add(new MStructure(InjectionPointField.class, "Bean1.field1"));
		matches.add(new MStructure(InjectionPointField.class, "Bean2.field2"));
		matches.add(new MStructure(InjectionPointField.class, "Bean3.field3"));
		matches.add(new MStructure(InjectionPointField.class, "Bean4.field4"));
		matches.add(new MStructure(InjectionPointField.class, "Bean5.field5"));
		
		matches.add(new MStructure(InjectionPointParameter.class, "Bean1.method_1(BaseDecoratedInterface param1)"));
		matches.add(new MStructure(InjectionPointParameter.class, "Bean2.method_2(BaseDecoratedInterface param2)"));
		matches.add(new MStructure(InjectionPointParameter.class, "Bean3.method_3(BaseDecoratedInterface param3)"));
		matches.add(new MStructure(InjectionPointParameter.class, "Bean4.method_4(BaseDecoratedInterface param4)"));
		matches.add(new MStructure(InjectionPointParameter.class, "Bean5.method_5(BaseDecoratedInterface param5)"));
		
		testSearchParticipant(project1, "src/cdi/test/search1/Base1.java", TYPE_SEARCH, "Base1", "", new CDIBeanQueryParticipant(), matches);
	}
	
	public void testCDIBeanQueryParticipantInProject2(){
		ArrayList<MStructure> matches = new ArrayList<MStructure>();
		
		matches.add(new MStructure(InjectionPointField.class, "Bean2.field2"));
		matches.add(new MStructure(InjectionPointField.class, "Bean3.field3"));
		matches.add(new MStructure(InjectionPointField.class, "Bean4.field4"));
		matches.add(new MStructure(InjectionPointField.class, "Bean5.field5"));
		
		matches.add(new MStructure(InjectionPointParameter.class, "Bean2.method_2(BaseDecoratedInterface param2)"));
		matches.add(new MStructure(InjectionPointParameter.class, "Bean3.method_3(BaseDecoratedInterface param3)"));
		matches.add(new MStructure(InjectionPointParameter.class, "Bean4.method_4(BaseDecoratedInterface param4)"));
		matches.add(new MStructure(InjectionPointParameter.class, "Bean5.method_5(BaseDecoratedInterface param5)"));
		
		testSearchParticipant(project2, "src/cdi/test/search2/Base2.java", TYPE_SEARCH, "Base2", "", new CDIBeanQueryParticipant(), matches);
	}
	
	public void testCDIBeanQueryParticipantInProject3(){
		ArrayList<MStructure> matches = new ArrayList<MStructure>();
		
		matches.add(new MStructure(InjectionPointField.class, "Bean3.field3"));
		
		matches.add(new MStructure(InjectionPointParameter.class, "Bean3.method_3(BaseDecoratedInterface param3)"));
		
		testSearchParticipant(project3, "src/cdi/test/search3/Base3.java", TYPE_SEARCH, "Base3", "", new CDIBeanQueryParticipant(), matches);
	}
	
	public void testCDIBeanQueryParticipantInProject4(){
		ArrayList<MStructure> matches = new ArrayList<MStructure>();
		
		matches.add(new MStructure(InjectionPointField.class, "Bean4.field4"));
		matches.add(new MStructure(InjectionPointField.class, "Bean5.field5"));
		
		matches.add(new MStructure(InjectionPointParameter.class, "Bean4.method_4(BaseDecoratedInterface param4)"));
		matches.add(new MStructure(InjectionPointParameter.class, "Bean5.method_5(BaseDecoratedInterface param5)"));
		
		testSearchParticipant(project4, "src/cdi/test/search4/Base4.java", TYPE_SEARCH, "Base4", "", new CDIBeanQueryParticipant(), matches);
	}
	
	public void testCDIBeanQueryParticipantInProject5(){
		ArrayList<MStructure> matches = new ArrayList<MStructure>();
		
		matches.add(new MStructure(InjectionPointField.class, "Bean5.field5"));
		
		matches.add(new MStructure(InjectionPointParameter.class, "Bean5.method_5(BaseDecoratedInterface param5)"));
		
		testSearchParticipant(project5, "src/cdi/test/search5/Base5.java", TYPE_SEARCH, "Base5", "", new CDIBeanQueryParticipant(), matches);
	}
	
	public void testELReferencesQueryParticipantForNamedBean() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/CDITest2/src/cdi/test/search2/Check2.java", "customer"));
		matches.add(new MatchStructure("/CDITest3/src/cdi/test/search3/Check3.java", "customer"));
		matches.add(new MatchStructure("/CDITest4/src/cdi/test/search4/Check4.java", "customer"));
		matches.add(new MatchStructure("/CDITest5/src/cdi/test/search5/Check5.java", "customer"));
		matches.add(new MatchStructure("/CDITest2/WebContent/test.jsp", "customer"));
		matches.add(new MatchStructure("/CDITest3/WebContent/test.jsp", "customer"));
		matches.add(new MatchStructure("/CDITest4/WebContent/test.jsp", "customer"));
		matches.add(new MatchStructure("/CDITest5/WebContent/test.jsp", "customer"));
		
		QueryParticipantTestUtils.testSearchParticipant(project2,
				"src/cdi/test/search2/Bean2.java",
				QueryParticipantTestUtils.TYPE_SEARCH,
				"customer",
				"",
				new ELReferencesQueryParticipant(),
				matches);
	}

}
