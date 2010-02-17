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
public class EditingSPecialSymbolsVPE_JBIDE3810 extends VpeTest {

	private final static int clientX = 5;
	private final static int clientY = 12;
	private final static long delay  = 2000;
	
	public EditingSPecialSymbolsVPE_JBIDE3810(String name) {
		super(name);
	}

	public void testEditingSPecialSymbolsVPE_JBIDE3810() throws Exception {
		setException(null);
		IFile file = (IFile) TestUtil.getComponentPath(
				"JBIDE/3810/JBIDE3810.jsp", //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);
		IEditorInput editorInput = new FileEditorInput(file);
		JSPMultiPageEditor part = openEditor(editorInput);
		TestUtil.waitForIdle();
		TestUtil.delay(3000);
		VpeController controller = TestUtil.getVpeController(part);
		Mouse mouse = new Mouse(controller, delay);
		mouse.click(clientX, clientY);
		Keybord keybord = new Keybord(controller, delay);
		keybord.pressBackSP();
		mouse.click(clientX+2, clientY);
		keybord.pressDel().pressDel();
		compareVisualAndSourceTargets(part);
	}

	private class Keybord {

		private nsIDOMDocumentEvent documentEvent;
		private nsIDOMEventTarget eventTarget;
		private nsIDOMAbstractView abstractView;
		private long delay;

		public Keybord(VpeController controller, long delay) {
			this.delay = delay;
			nsIDOMDocument idomDocument = controller.getXulRunnerEditor()
					.getDOMDocument();
			nsIDOMDocumentView documentView = (nsIDOMDocumentView) idomDocument
					.queryInterface(nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID);
			abstractView = documentView.getDefaultView();
			documentEvent = (nsIDOMDocumentEvent) idomDocument
					.queryInterface(nsIDOMDocumentEvent.NS_IDOMDOCUMENTEVENT_IID);
			eventTarget = (nsIDOMEventTarget) idomDocument
					.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
		}

		public Keybord pressDel() {
			nsIDOMEvent delEvent = documentEvent.createEvent("KeyboardEvent"); //$NON-NLS-1$
			nsIDOMKeyEvent delKeyEvent = (nsIDOMKeyEvent) delEvent
					.queryInterface(nsIDOMKeyEvent.NS_IDOMKEYEVENT_IID);
			delKeyEvent
					.initKeyEvent("keypress", true, true, abstractView, false, //$NON-NLS-1$
							false, false, false, nsIDOMKeyEvent.DOM_VK_DELETE,
							0);
			eventTarget.dispatchEvent(delKeyEvent);
			TestUtil.waitForIdle();
			TestUtil.delay(this.delay);
			return this;
		}

		public Keybord pressBackSP() {
			nsIDOMEvent bsEvent = documentEvent.createEvent("KeyboardEvent"); //$NON-NLS-1$
			nsIDOMKeyEvent bsKeyEvent = (nsIDOMKeyEvent) bsEvent
					.queryInterface(nsIDOMKeyEvent.NS_IDOMKEYEVENT_IID);
			bsKeyEvent.initKeyEvent("keypress", true, true, abstractView, //$NON-NLS-1$
					false, false, false, false,
					nsIDOMKeyEvent.DOM_VK_BACK_SPACE, 0);
			eventTarget.dispatchEvent(bsKeyEvent);
			TestUtil.waitForIdle();
			TestUtil.delay(this.delay);
			return this;
		}

		public Keybord pressLeft() {
			nsIDOMEvent leftArrowEvent = documentEvent
					.createEvent("KeyboardEvent"); //$NON-NLS-1$
			nsIDOMKeyEvent leftArrowKeyEvent = (nsIDOMKeyEvent) leftArrowEvent
					.queryInterface(nsIDOMKeyEvent.NS_IDOMKEYEVENT_IID);
			leftArrowKeyEvent.initKeyEvent("keypress", true, true, //$NON-NLS-1$
					abstractView, false, false, false, false,
					nsIDOMKeyEvent.DOM_VK_LEFT, 0);
			eventTarget.dispatchEvent(leftArrowKeyEvent);
			TestUtil.waitForIdle();
			TestUtil.delay(this.delay);
			return this;
		}
	}

	private class Mouse {

		private long delay;
		private nsIDOMWindowUtils windowUtils;

		public Mouse(VpeController controller, long delay) {
			this.delay = delay;
			nsIDOMDocument idomDocument = controller.getXulRunnerEditor()
					.getDOMDocument();
			nsIDOMDocumentView documentView = (nsIDOMDocumentView) idomDocument
					.queryInterface(nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID);
			nsIDOMAbstractView abstractView = documentView.getDefaultView();
			nsIInterfaceRequestor requestor = (nsIInterfaceRequestor) abstractView
					.queryInterface(nsIInterfaceRequestor.NS_IINTERFACEREQUESTOR_IID);
			nsIDOMWindowUtils windowUtils = (nsIDOMWindowUtils) requestor
					.getInterface(nsIDOMWindowUtils.NS_IDOMWINDOWUTILS_IID);
			this.windowUtils = windowUtils;
		}

		public Mouse click(int clientX, int clientY) {
			windowUtils.sendMouseEvent("mousedown", clientX, clientY, 0, 1, //$NON-NLS-1$
					nsIDOMNSEvent.MOUSEDOWN, true);
			windowUtils.sendMouseEvent("mouseup", clientX, clientY, 0, 1, //$NON-NLS-1$
					nsIDOMNSEvent.MOUSEUP, true);
			TestUtil.delay(this.delay);
			return this;
		}

	}

	@SuppressWarnings("deprecation")
	private void compareVisualAndSourceTargets(JSPMultiPageEditor part) {
		nsIDOMElement headElement = ((VpeEditorPart) part.getVisualEditor())
				.getVisualEditor().getContentArea();
		nsIDOMNodeList nodeList = headElement.getElementsByTagName("SPAN"); //$NON-NLS-1$
		nsIDOMNode node = nodeList.item(0).getChildNodes().item(0);
		String valueToCompare = node.getNodeValue();
		assertEquals("\na\n", valueToCompare); //$NON-NLS-1$
		IStructuredModel model = part.getSourceEditor().getModel();
		IDOMModel sourceModel = (IDOMModel) model;
		IDOMDocument sourceDocument = sourceModel.getDocument();
		Node domNode = sourceDocument.getElementsByTagName("body").item(0); //$NON-NLS-1$
		String sourceValueToCompare = ((Text) domNode.getChildNodes().item(0))
				.getNodeValue();
		assertEquals("\r\na\r\n", sourceValueToCompare); //$NON-NLS-1$
	}

}
