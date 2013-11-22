package org.jboss.tools.seam.ui.test.search;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.ui.search.JavaSearchScopeFactory;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.PatternQuerySpecification;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search2.internal.ui.SearchView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.jboss.tools.jst.web.ui.internal.editor.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.jst.web.ui.internal.editor.jspeditor.JSPTextEditor;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.ui.actions.FindSeamAction;
import org.jboss.tools.seam.ui.actions.FindSeamDeclarationsAction;
import org.jboss.tools.seam.ui.actions.FindSeamReferencesAction;
import org.jboss.tools.seam.ui.search.SeamElementMatch;
import org.jboss.tools.seam.ui.search.SeamMatchPresentation;
import org.jboss.tools.seam.ui.search.SeamQueryParticipant;
import org.jboss.tools.seam.ui.search.SeamSearchResultPage;
import org.jboss.tools.seam.ui.search.SeamSearchViewLabelProvider;
import org.jboss.tools.test.util.WorkbenchUtils;

public class SeamSearchTest extends TestCase{
	private static final String SEAM_SEARCH_RESULT_PAGE_ID = "org.jboss.tools.seam.ui.search.SeamSearchResultPage";
	private IProject project;
	private ISeamProject seamProject;
	SearchView view;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
       
		project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember("TestComponentView");
		seamProject = SeamCorePlugin.getSeamProject(project, true);
		
	}
	
	@Override
	protected void tearDown() throws Exception {
	}
	
	public void testSeamElementMatch(){
		ISeamComponent[] components = seamProject.getComponents();
		for (ISeamComponent component: components) {
			SeamElementMatch match = new SeamElementMatch(component);
			assertEquals("Wrong time stamp for component - "+component.getName(), component.getResource().getModificationStamp(), match.getCreationTimeStamp());
		}
	}
	
	public void testSeamMatchPresentation(){
		SeamMatchPresentation presentation = new SeamMatchPresentation();
		
		assertTrue("Lable provider should be instance of SeamSearchViewLabelProvider", presentation.createLabelProvider() instanceof SeamSearchViewLabelProvider);
	}
	
	public void testSeamQueryParticipant(){
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		testSeamSearchParticipant("User", new SeamQueryParticipant(), matches);
	}
	
	public void testFindSeamDeclarationsActionTest(){
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure(0, 0));
		
		checkAction("WebContent/pages/seamSearchPage.jsp", "myUser", new FindSeamDeclarationsAction(), matches);
	}
	
	public void testFindSeamReferencesActionTest(){
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure(437, 6));
		
		checkAction("WebContent/pages/seamSearchPage.jsp", "myUser", new FindSeamReferencesAction(), matches);
	}
	
	public void checkAction(String fileName, String textToSelect, final FindSeamAction action, final List<MatchStructure> testMatches){
		IFile file = project.getFile(fileName);

		assertNotNull("The file \"" + fileName + "\" is not found", file);
		assertTrue("The file \"" + fileName + "\" is not found", file.isAccessible());

		FileEditorInput editorInput = new FileEditorInput(file);

		IDocumentProvider documentProvider = null;
		try {
			documentProvider = DocumentProviderRegistry.getDefault().getDocumentProvider(editorInput);
		} catch (Exception x) {
			x.printStackTrace();
			fail("An exception caught: " + x.getMessage());
		}

		assertNotNull("The document provider for the file \"" + fileName + "\" is not loaded", documentProvider);

		try {
			documentProvider.connect(editorInput);
		} catch (Exception x) {
			fail("The document provider is not able to be initialized with the editor input\nAn exception caught: "+x.getMessage());
		}

		IDocument document = documentProvider.getDocument(editorInput);
		
		assertNotNull("The document for the file \"" + fileName + "\" is not loaded", document);
		
		
		IEditorPart editorPart = WorkbenchUtils.openEditor(project.getName()+"/"+ fileName); //$NON-NLS-1$
		if(editorPart instanceof JSPMultiPageEditor){
			JSPTextEditor textEditor = ((JSPMultiPageEditor) editorPart).getJspEditor();
			int position = document.get().indexOf(textToSelect);
			if(position < 0){
				fail("Text \'"+textToSelect+"\' not found");
			}
			textEditor.getSelectionProvider().setSelection(new TextSelection(position+1,0));
		}
		
		view = null;
		
		try {
			view = (SearchView) Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.search.ui.views.SearchView");
		} catch (CoreException e) {
			fail("CoreException - "+e.getMessage());
		}
		
		Display.getDefault().syncExec(
			new Runnable() {
				public void run() {
					view.showEmptySearchPage(SEAM_SEARCH_RESULT_PAGE_ID);
					
					action.run();
					
					SeamSearchResultPage resultPage = (SeamSearchResultPage)view.getSearchPageRegistry().findPageForPageId(SEAM_SEARCH_RESULT_PAGE_ID, true);
					if(resultPage != null){
						AbstractTextSearchResult result = resultPage.getInput();
						
						checkMatches(testMatches, result);
					}else{
						fail("SeamSearchResultPage not found");
					}

				}
			}
		);
	}
	
	private void checkMatches(List<MatchStructure> testMatches, AbstractTextSearchResult result){
		Object[] elements = result.getElements();
		if(elements.length == 0){
			assertEquals("No matches found", testMatches.size(), 0);
		}else{
			Object element = elements[0];
			Match[] matches = result.getMatches(element);
			for(Match match : matches){
				MatchStructure ms = findMatch(testMatches, match);
				assertNotNull("Unexpected match found (offset - "+match.getOffset()+" length - "+match.getLength()+")", ms);
				ms.checked = true;
			}
			
			for(MatchStructure ms : testMatches){
				assertTrue("Match not found (offset - "+ms.offset+" length - "+ms.length, ms.checked);
			}
		}
	}
	
	protected MatchStructure findMatch(List<MatchStructure> matchList, Match match){
		for(MatchStructure ms : matchList){
			if(!ms.checked && ms.offset == match.getOffset() && ms.length == match.getLength())
				return ms;
		}
		return null;
	}
	
	private void testSeamSearchParticipant(String pattern, IQueryParticipant participant, List<MatchStructure> matches){
		try{
			SeamTestSearchRequestor requestor = new SeamTestSearchRequestor();
			
			JavaSearchScopeFactory factory = JavaSearchScopeFactory.getInstance();
			IJavaSearchScope scope = factory.createWorkspaceScope(true);
			String description = factory.getWorkspaceScopeDescription(true);
			QuerySpecification specification = new PatternQuerySpecification(pattern, IJavaElement.TYPE, false, IJavaSearchConstants.ALL_OCCURRENCES, scope, description);
			
			participant.search(requestor, specification, new NullProgressMonitor());
			
			List<Match> matchesForCheck = requestor.getMatches();
			
			checkMatches(matchesForCheck, matches);
		}catch(CoreException ex){
			fail("Core exception");
		}
	}

	class SeamTestSearchRequestor implements ISearchRequestor{
		ArrayList<Match> matches = new ArrayList<Match>();
		
		public void reportMatch(Match match){
			matches.add(match);
		}
		
		public List<Match> getMatches(){
			return matches;
		}
	}
	
	class MatchStructure{
		Class<? extends ISeamElement> type;
		String name; // label
		boolean checked = false;
		int offset, length;
		
		public MatchStructure(Class<? extends ISeamElement> type, String name){
			this.type = type;
			this.name = name;
		}
		
		public MatchStructure(int offset, int length){
			this.offset = offset;
			this.length = length;
		}
	}
	
	private void checkMatches(List<Match> matchesForCheck, List<MatchStructure> matchList) throws CoreException {
		
		for(Match match : matchesForCheck){
			assertTrue("Match must be SeamElementMatch", match instanceof SeamElementMatch);
			MatchStructure ms = findMatch(matchList, (SeamElementMatch)match);
			assertNotNull("Unexpected match found (class - "+((SeamElementMatch)match).getElement().getClass()+")", ms);
			ms.checked = true;
		}
		
		for(MatchStructure ms : matchList){
			assertTrue("Match not found (class - "+ms.type+" name - "+ms.name, ms.checked);
		}
	}
	
	protected MatchStructure findMatch(List<MatchStructure> matchList, SeamElementMatch match){
		for(MatchStructure ms : matchList){
			if(!ms.checked && ms.type.equals(match.getElement().getClass()))
				return ms;
		}
		return null;
	}

}
