package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.VpeEditorPart;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMAbstractView;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentEvent;
import org.mozilla.interfaces.nsIDOMDocumentView;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMKeyEvent;
import org.mozilla.interfaces.nsIDOMNSEvent;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMWindowUtils;
import org.mozilla.interfaces.nsIInterfaceRequestor;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

@SuppressWarnings("restriction")
public class JBIDE3810Test extends VpeTest {

	private final static int clientX = 20;
	private final static int clientY = 10;
	
	public JBIDE3810Test(String name) {
		super(name);
	}
	
	@SuppressWarnings("deprecation")
	public void testJBIDE3810() throws Exception {
		setException(null);
		IFile file = (IFile) TestUtil.getComponentPath("JBIDE/3810/JBIDE3810.jsp",  //$NON-NLS-1$
        		JsfAllTests.IMPORT_PROJECT_NAME);
		IEditorInput editorInput = new FileEditorInput(file);
		JSPMultiPageEditor part =  openEditor(editorInput);
		TestUtil.waitForIdle();
		TestUtil.delay(5000);
		VpeController controller = TestUtil.getVpeController(part);
		nsIDOMDocument idomDocument = controller.getXulRunnerEditor().getDOMDocument();
		nsIDOMDocumentView documentView = (nsIDOMDocumentView) idomDocument.queryInterface(nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID);
		nsIDOMAbstractView abstractView = documentView.getDefaultView();
		nsIInterfaceRequestor requestor = (nsIInterfaceRequestor) abstractView.queryInterface(nsIInterfaceRequestor.NS_IINTERFACEREQUESTOR_IID);
		nsIDOMWindowUtils windowUtils = (nsIDOMWindowUtils) requestor.getInterface(nsIDOMWindowUtils.NS_IDOMWINDOWUTILS_IID);
		windowUtils.sendMouseEvent("mousedown", clientX, clientY, 0, 1, nsIDOMNSEvent.MOUSEDOWN, true);
		windowUtils.sendMouseEvent("mouseup", clientX, clientY, 0, 1, nsIDOMNSEvent.MOUSEUP, true);
		TestUtil.delay(1000);
		nsIDOMDocumentEvent documentEvent = (nsIDOMDocumentEvent) idomDocument.queryInterface(nsIDOMDocumentEvent.NS_IDOMDOCUMENTEVENT_IID);
		nsIDOMEventTarget eventTarget = (nsIDOMEventTarget) idomDocument.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
		nsIDOMEvent delEvent = documentEvent.createEvent("KeyboardEvent");
		nsIDOMKeyEvent delKeyEvent = (nsIDOMKeyEvent) delEvent.queryInterface(nsIDOMKeyEvent.NS_IDOMKEYEVENT_IID);
		delKeyEvent.initKeyEvent("keypress", true, true, documentView.getDefaultView(), false, false, false, false, nsIDOMKeyEvent.DOM_VK_DELETE, 0);
		eventTarget.dispatchEvent(delKeyEvent);
		TestUtil.delay(1000);
		nsIDOMEvent bsEvent = documentEvent.createEvent("KeyboardEvent");
		nsIDOMKeyEvent bsKeyEvent = (nsIDOMKeyEvent) bsEvent.queryInterface(nsIDOMKeyEvent.NS_IDOMKEYEVENT_IID);
		bsKeyEvent.initKeyEvent("keypress", true, true, documentView.getDefaultView(), false, false, false, false, nsIDOMKeyEvent.DOM_VK_BACK_SPACE, 0);
		eventTarget.dispatchEvent(bsKeyEvent);
		TestUtil.delay(1000);
		nsIDOMEvent leftArrowEvent = documentEvent.createEvent("KeyboardEvent");
		nsIDOMKeyEvent leftArrowKeyEvent = (nsIDOMKeyEvent) leftArrowEvent.queryInterface(nsIDOMKeyEvent.NS_IDOMKEYEVENT_IID);
		leftArrowKeyEvent.initKeyEvent("keypress", true, true, documentView.getDefaultView(), false, false, false, false, nsIDOMKeyEvent.DOM_VK_LEFT, 0);
		eventTarget.dispatchEvent(leftArrowKeyEvent);
		TestUtil.delay(1000);
		bsEvent = documentEvent.createEvent("KeyboardEvent");
		bsKeyEvent = (nsIDOMKeyEvent) bsEvent.queryInterface(nsIDOMKeyEvent.NS_IDOMKEYEVENT_IID);
		bsKeyEvent.initKeyEvent("keypress", true, true, documentView.getDefaultView(), false, false, false, false, nsIDOMKeyEvent.DOM_VK_BACK_SPACE, 0);
		eventTarget.dispatchEvent(bsKeyEvent);
		TestUtil.delay(1000);
		nsIDOMElement headElement = ((VpeEditorPart) part.getVisualEditor()).getVisualEditor().getContentArea();
		nsIDOMNodeList nodeList = headElement.getElementsByTagName("SPAN");
		nsIDOMNode node = nodeList.item(0).getChildNodes().item(0);
		String valueToCompare = node.getNodeValue();
		assertEquals("\na\n", valueToCompare);
		IStructuredModel model = part.getSourceEditor().getModel();
		IDOMModel sourceModel = (IDOMModel) model;
		IDOMDocument sourceDocument = null;
		if (sourceModel != null) {
			sourceDocument = sourceModel.getDocument();
		}
		Node domNode = sourceDocument.getElementsByTagName("body").item(0);
		String sourceValueToCompare = ((Text)domNode.getChildNodes().item(0)).getNodeValue();
		assertEquals("\r\na\r\n", sourceValueToCompare);
	}
	
}
