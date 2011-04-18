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
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.ui.search.JavaSearchScopeFactory;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IMarkerResolution;
import org.jboss.tools.cdi.core.ICDIElement;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
import org.jboss.tools.cdi.internal.core.impl.EventBean;
import org.jboss.tools.cdi.internal.core.impl.InjectionPointField;
import org.jboss.tools.cdi.internal.core.impl.InjectionPointParameter;
import org.jboss.tools.cdi.internal.core.impl.ObserverMethod;
import org.jboss.tools.cdi.ui.marker.MarkerResolutionUtils;
import org.jboss.tools.cdi.ui.search.CDIBeanQueryParticipant;
import org.jboss.tools.cdi.ui.search.CDIMatch;
import org.jboss.tools.cdi.ui.search.InjectionPointQueryParticipant;
import org.jboss.tools.common.EclipseUtil;

public class CDISearchParticipantTest  extends TCKTest {
	private static final int FIELD_SEARCH = 1;
	private static final int METHOD_SEARCH = 2;
	private static final int TYPE_SEARCH = 3;
	private static final int PARAMETER_SEARCH = 4;
	
	private void testSearchParticipant(String fileName, int searchType, String elementName, String parameterName, IQueryParticipant participant, List<MatchStructure> matches){
		IFile file = tckProject.getFile(fileName);
		assertNotNull("File - "+fileName+" not found", file);
		try{
			ICompilationUnit compilationUnit = EclipseUtil.getCompilationUnit(file);
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
	
	private IMethod getMethod(IType type, String name) throws JavaModelException{
		IMethod[] methods = type.getMethods();
		for(IMethod method : methods){
			if(method.getElementName().equals(name))
				return method;
		}
		return null;
	}
	
	private void checkMatches(List<Match> matchesForCheck, List<MatchStructure> matchList) throws CoreException {
		for(Match match : matchesForCheck){
			assertTrue("Match must be CDIMatch", match instanceof CDIMatch);
			MatchStructure ms = findMatch(matchList, (CDIMatch)match);
			assertNotNull("Unexpected mutch found (class - "+((CDIMatch)match).getCDIElement().getClass()+" label - "+((CDIMatch)match).getLabel()+")", ms);
			ms.checked = true;
		}
		
		for(MatchStructure ms : matchList){
			assertTrue("Match not found (class - "+ms.type+" label - "+ms.name, ms.checked);
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
		Class<? extends ICDIElement> type;
		String name; // label
		boolean checked;
		
		public MatchStructure(Class<? extends ICDIElement> type, String name){
			this.type = type;
			this.name = name;
			checked = false;
		}
	}
	
	public void testInjectionPointQueryParticipant1(){
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure(ClassBean.class, "BeanWithInjectionPointMetadata"));
		
		testSearchParticipant("JavaSource/org/jboss/jsr299/tck/tests/lookup/injectionpoint/FieldInjectionPointBean.java", FIELD_SEARCH, "injectedBean", "", new InjectionPointQueryParticipant(), matches);
	}

	public void testInjectionPointQueryParticipant2(){
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure(ClassBean.class, "BeanWithInjectionPointMetadata"));
		
		testSearchParticipant("JavaSource/org/jboss/jsr299/tck/tests/lookup/injectionpoint/ConstructorInjectionPointBean.java", PARAMETER_SEARCH, "ConstructorInjectionPointBean", "injectedBean", new InjectionPointQueryParticipant(), matches);
	}

	public void testInjectionPointQueryParticipant3(){
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure(EventBean.class, "Event"));
		
		matches.add(new MatchStructure(ObserverMethod.class, "DiscerningObserver.observeAny()"));
		matches.add(new MatchStructure(ObserverMethod.class, "EventTypeFamilyObserver.observeObject()"));
		matches.add(new MatchStructure(ObserverMethod.class, "Fox.observeEvent()"));
		matches.add(new MatchStructure(ObserverMethod.class, "GoldenRetriever.anObserverMethod()"));
		matches.add(new MatchStructure(ObserverMethod.class, "Pomeranian.observeStringEvent()"));
		matches.add(new MatchStructure(ObserverMethod.class, "StringObserver.anotherObserver()"));
		matches.add(new MatchStructure(ObserverMethod.class, "TeaCupPomeranian.observeSimpleEvent()"));
		matches.add(new MatchStructure(ObserverMethod.class, "Terrier.observer()"));
		matches.add(new MatchStructure(ObserverMethod.class, "TibetanTerrier_Broken.observeSomeEvent()"));
		matches.add(new MatchStructure(ObserverMethod.class, "TibetanTerrier_BrokenNoInterface.observeSomeEvent()"));
		matches.add(new MatchStructure(ObserverMethod.class, "ClassFragmentLogger.addEntry()"));
		matches.add(new MatchStructure(ObserverMethod.class, "ObserverMethodInInterceptorBroken.observeSomeEvent()"));
		matches.add(new MatchStructure(ObserverMethod.class, "ObserverMethodInDecoratorBroken.observeSomeEvent()"));
		
		testSearchParticipant("JavaSource/org/jboss/jsr299/tck/tests/event/bindingTypes/EventEmitter.java", FIELD_SEARCH, "stringEvent", "", new InjectionPointQueryParticipant(), matches);
	}

	public void testCDIBeanQueryParticipant(){
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure(InjectionPointField.class, "FieldInjectionPointBean.injectedBean"));
		matches.add(new MatchStructure(InjectionPointField.class, "NamedDecoratorBroken.logger"));
		matches.add(new MatchStructure(InjectionPointField.class, "NamedStereotypedDecoratorBroken.logger"));
		matches.add(new MatchStructure(InjectionPointField.class, "TransientFieldInjectionPointBean.injectedBean"));
		matches.add(new MatchStructure(InjectionPointField.class, "SpecializingDecoratorBroken.logger"));
		matches.add(new MatchStructure(InjectionPointField.class, "ObserverMethodInDecoratorBroken.logger"));
		
		matches.add(new MatchStructure(InjectionPointParameter.class, "ConstructorInjectionPointBean.ConstructorInjectionPointBean(BeanWithInjectionPointMetadata injectedBean)"));
		matches.add(new MatchStructure(InjectionPointParameter.class, "MethodInjectionPointBean.methodWithInjectedMetadata(BeanWithInjectionPointMetadata injectedBean)"));
		
		testSearchParticipant("JavaSource/org/jboss/jsr299/tck/tests/lookup/injectionpoint/BeanWithInjectionPointMetadata.java", TYPE_SEARCH, "BeanWithInjectionPointMetadata", "", new CDIBeanQueryParticipant(), matches);
	}
}
