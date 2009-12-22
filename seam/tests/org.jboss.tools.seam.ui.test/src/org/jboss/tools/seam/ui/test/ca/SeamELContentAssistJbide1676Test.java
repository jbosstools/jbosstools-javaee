package org.jboss.tools.seam.ui.test.ca;

import java.util.Set;
import java.util.TreeSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.common.el.ui.ca.ELProposalProcessor;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.jst.jsp.test.TestUtil;
import org.jboss.tools.test.util.JobUtils;

public class SeamELContentAssistJbide1676Test extends TestCase {
	TestProjectProvider provider = null;
	IProject project = null;
	boolean makeCopy = false;
	private static final String PROJECT_NAME = "TestSeamELContentAssist";
	private static final String JAVA_FILENAME = "org/domain/TestSeamELContentAssist/session/Authenticator.java";
	private static final String EL_START_TEMPLATE = "#{";

	private static final String[] VALID_SEAM_EL_PROPOSALS = new String[] {
			"TestSeamELContentAssistEntityManagerFactory",
			"fullPostList",
			"a4j", 
			"a4jSkin", 
			"actor",
			"ajaxContext",
			"applicationContext",
			"authenticator",
			"businessProcess",
			"businessProcessContext",
			"captcha",
			"conversation",
			"conversationContext",
			"conversationList",
			"conversationStack",
			"currentDate",
			"currentDatetime",
			"currentTime",
			"entityManager",
			"eventContext",
			"events",
			"expressions",
			"facesContext",
			"facesMessages",
			"httpError",
			"identity",
			"image",
			"interpolator",
			"isUserInRole",
			"jbpmContext",
			"locale",
			"localeSelector",
			"mailSession",
			"messages",
			"methodContext",
			"mockSecureEntity",
			"org.jboss.seam.captcha.captcha",
			"org.jboss.seam.captcha.captchaImage",
			"org.jboss.seam.core.actor",
			"org.jboss.seam.core.applicationContext",
			"org.jboss.seam.core.businessProcess",
			"org.jboss.seam.core.businessProcessContext",
			"org.jboss.seam.core.conversation",
			"org.jboss.seam.core.conversationContext",
			"org.jboss.seam.core.conversationEntries",
			"org.jboss.seam.core.conversationList",
			"org.jboss.seam.core.conversationStack",
			"org.jboss.seam.core.dispatcher",
			"org.jboss.seam.core.ejb",
			"org.jboss.seam.core.eventContext",
			"org.jboss.seam.core.events",
			"org.jboss.seam.core.exceptions",
			"org.jboss.seam.core.expressions",
			"org.jboss.seam.core.facesContext",
			"org.jboss.seam.core.facesMessages",
			"org.jboss.seam.core.facesPage",
			"org.jboss.seam.core.httpError",
			"org.jboss.seam.core.image",
			"org.jboss.seam.core.init",
			"org.jboss.seam.core.interpolator",
			"org.jboss.seam.core.isUserInRole",
			"org.jboss.seam.core.jbpm",
			"org.jboss.seam.core.jbpmContext",
			"org.jboss.seam.core.locale",
			"org.jboss.seam.core.localeSelector",
			"org.jboss.seam.core.manager",
			"org.jboss.seam.core.messages",
			"org.jboss.seam.core.methodContext",
			"org.jboss.seam.core.microcontainer",
			"org.jboss.seam.core.pageContext",
			"org.jboss.seam.core.pageflow",
			"org.jboss.seam.core.pages",
			"org.jboss.seam.core.persistenceContexts",
			"org.jboss.seam.core.pojoCache",
			"org.jboss.seam.core.pooledTask",
			"org.jboss.seam.core.pooledTaskInstanceList",
			"org.jboss.seam.core.processInstance",
			"org.jboss.seam.core.processInstanceFinder",
			"org.jboss.seam.core.processInstanceList",
			"org.jboss.seam.core.redirect",
			"org.jboss.seam.core.renderer",
			"org.jboss.seam.core.resourceBundle",
			"org.jboss.seam.core.safeActions",
			"org.jboss.seam.core.sessionContext",
			"org.jboss.seam.core.switcher",
			"org.jboss.seam.core.taskInstance",
			"org.jboss.seam.core.taskInstanceList",
			"org.jboss.seam.core.taskInstanceListForType",
			"org.jboss.seam.core.timeZone",
			"org.jboss.seam.core.timeZoneSelector",
			"org.jboss.seam.core.transactionListener",
			"org.jboss.seam.core.transition",
			"org.jboss.seam.core.uiComponent",
			"org.jboss.seam.core.userPrincipal",
			"org.jboss.seam.core.validation",
			"org.jboss.seam.core.validators",
			"org.jboss.seam.debug.contexts",
			"org.jboss.seam.debug.hotDeployFilter",
			"org.jboss.seam.debug.introspector",
			"org.jboss.seam.framework.currentDate",
			"org.jboss.seam.framework.currentDatetime",
			"org.jboss.seam.framework.currentTime",
			"org.jboss.seam.ioc.spring.contextLoader",
			"org.jboss.seam.ioc.spring.springELResolver",
			"org.jboss.seam.jms.queueConnection",
			"org.jboss.seam.jms.queueSession",
			"org.jboss.seam.jms.topicConnection",
			"org.jboss.seam.jms.topicSession",
			"org.jboss.seam.mail.mailSession",
			"org.jboss.seam.pdf.documentStore",
			"org.jboss.seam.persistence.persistenceProvider",
			"org.jboss.seam.remoting.messaging.subscriptionRegistry",
			"org.jboss.seam.remoting.remoting",
			"org.jboss.seam.security.configuration",
			"org.jboss.seam.security.identity",
			"org.jboss.seam.servlet.characterEncodingFilter",
			"org.jboss.seam.servlet.contextFilter",
			"org.jboss.seam.servlet.exceptionFilter",
			"org.jboss.seam.servlet.multipartFilter",
			"org.jboss.seam.servlet.redirectFilter",
			"org.jboss.seam.theme.theme",
			"org.jboss.seam.theme.themeSelector",
			"org.jboss.seam.ui.entityConverter",
			"org.jboss.seam.ui.entityConverterStore",
			"org.jboss.seam.ui.graphicImage.dynamicImageResource",
			"org.jboss.seam.ui.graphicImage.dynamicImageStore",
			"org.jboss.seam.ui.resource.webResource",
			"pageContext",
			"pageflow",
			"pdfKeyStore",
			"pojoCache",
			"pooledTask",
			"pooledTaskInstanceList",
			"processInstance",
			"processInstanceFinder",
			"processInstanceList",
			"queueSession",
			"redirect",
			"renderer",
			"resourceBundle",
			"securityRules",
			"sessionContext",
			"switcher",
			"taskInstance",
			"taskInstanceList",
			"taskInstanceListForType",
			"TestSeamELContentAssistEntityManagerFactory",
			"theme",
			"themeSelector",
			"timeZone",
			"timeZoneSelector",
			"topicSession",
			"transition",
			"uiComponent",
			"userPrincipal",
			"validation",
			"interfaceTest"
		};
	
	protected Set<String> getJavaStringValidELProposals() {
		TreeSet<String> pSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < VALID_SEAM_EL_PROPOSALS.length; i++) {
			pSet.add(VALID_SEAM_EL_PROPOSALS[i]);
		}

		return pSet;
	}

	public static Test suite() {
		return new TestSuite(SeamELContentAssistJbide1676Test.class);
	}

	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.seam.ui.test", null, PROJECT_NAME, makeCopy); 
		project = provider.getProject();
		Throwable exception = null;
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (Exception x) {
			exception = x;
			x.printStackTrace();
		}
		assertNull("An exception caught: " + (exception != null? exception.getMessage() : ""), exception);
	}

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}

	public void testSeamELContentAssistJbide1676() {
		try {
			JobUtils.waitForIdle();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		assertTrue("Test project \"" + PROJECT_NAME + "\" is not loaded", (project != null));
		 
		IFolder srcRoot = (IFolder)EclipseResourceUtil.getJavaSourceRoot(project);
		IFile javaFile = (srcRoot == null ? null : (IFile)srcRoot.findMember(JAVA_FILENAME));  
			

		assertTrue("The file \"" + JAVA_FILENAME + "\" is not found", (javaFile != null));
		assertTrue("The file \"" + JAVA_FILENAME + "\" is not found", (javaFile.exists()));

		FileEditorInput editorInput = new FileEditorInput(javaFile);
		Throwable exception = null;
		IEditorPart editorPart = null;
		try {
			editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, "org.eclipse.jdt.ui.CompilationUnitEditor");
		} catch (PartInitException ex) {
			exception = ex;
			ex.printStackTrace();
			assertTrue("The Java Editor couldn't be initialized.", false);
		}

		CompilationUnitEditor javaEditor = null;
		
		if (editorPart instanceof CompilationUnitEditor)
			javaEditor = (CompilationUnitEditor)editorPart;
		
		// Delay for 3 seconds so that
		// the Favorites view can be seen.
		try {
			JobUtils.waitForIdle();
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Waiting for the jobs to complete has failed.", false);
		} 
		TestUtil.delay(3000);
		
		ISourceViewer viewer = javaEditor.getViewer();
		IDocument document = viewer.getDocument();
		SourceViewerConfiguration config = TestUtil.getSourceViewerConfiguration(javaEditor);
		IContentAssistant contentAssistant = (config == null ? null : config.getContentAssistant(viewer));

		assertTrue("Cannot get the Content Assistant instance for the editor for file  \"" + JAVA_FILENAME + "\"", (contentAssistant != null));
		
		String documentContent = document.get();
		int start = (documentContent == null ? -1 : documentContent.indexOf(EL_START_TEMPLATE));
		int offsetToTest = start + EL_START_TEMPLATE.length();
		
		assertTrue("Cannot find the starting point in the test file  \"" + JAVA_FILENAME + "\"", (start != -1));

		ICompletionProposal[] result= null;
		String errorMessage = null;

		IContentAssistProcessor p= TestUtil.getProcessor(viewer, offsetToTest, contentAssistant);
		if (p != null) {
			try {
				result= p.computeCompletionProposals(viewer, offsetToTest);
			} catch (Throwable x) {
				x.printStackTrace();
			}
			errorMessage= p.getErrorMessage();
		}
		
//		if (errorMessage != null && errorMessage.trim().length() > 0) {
//			System.out.println("#" + offsetToTest + ": ERROR MESSAGE: " + errorMessage);
//		}

		assertTrue("Content Assistant peturned no proposals", (result != null && result.length > 0));
		
		// compare SeamELCompletionProposals in the result to the filtered valid proposals
		Set<String> existingProposals = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		Set<String> nonExistingProposals = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		Set<String> filteredValidProposals = getJavaStringValidELProposals();

		for (int j = 0; j < result.length; j++) {
			// Look only for SeamELProposalProcessor proposals
			if (result[j] instanceof ELProposalProcessor.Proposal) {
				ELProposalProcessor.Proposal proposal = (ELProposalProcessor.Proposal)result[j];
				String proposalString = proposal.getPrefixCompletionText(document, offsetToTest).toString();
				
				if (filteredValidProposals.contains(proposalString)) {
					existingProposals.add(proposalString);
					filteredValidProposals.remove(proposalString);
				} else {
					nonExistingProposals.add(proposalString);
				}
			}
		}
		assertTrue("Some Seam EL proposals werent\'t shown in the Content Assistant", filteredValidProposals.isEmpty());
		assertTrue("Some Seam EL proposals were shown in the Content Assistant but they shouldn\'t", nonExistingProposals.isEmpty());

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		.closeEditor(editorPart, false);
	}

}
