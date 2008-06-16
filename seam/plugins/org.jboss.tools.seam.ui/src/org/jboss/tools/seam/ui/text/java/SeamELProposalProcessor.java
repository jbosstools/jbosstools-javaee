/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.ui.text.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.AbstractContentAssistProcessor;
import org.eclipse.wst.xml.ui.internal.util.SharedXMLEditorPluginImageHelper;
import org.jboss.tools.common.model.ui.texteditors.xmleditor.XMLTextEditor;
import org.jboss.tools.common.text.ext.IEditorWrapper;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.el.ElVarSearcher;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.w3c.dom.Node;
import org.w3c.tidy.AttrCheckImpl.CheckBool;

/**
 * Content assist proposal processor.
 * Computes Seam EL proposals.
 * 
 * @author Jeremy
 */
public class SeamELProposalProcessor extends AbstractContentAssistProcessor {

    private static final ICompletionProposal[] NO_PROPOSALS= new ICompletionProposal[0];
    private static final IContextInformation[] NO_CONTEXTS= new IContextInformation[0];
    public static final String EL_END_EXPRESSION = "}";
    public static final String EL_START_EXPRESSION = "#{";
    
    public static final class Proposal implements ICompletionProposal, ICompletionProposalExtension, ICompletionProposalExtension2, ICompletionProposalExtension3, ICompletionProposalExtension4,ICompletionProposalExtension5{

        private final String fString;
        private final String fPrefix;
        private final String fNewPrefix;
        private final int fOffset;
        private int fNewPosition;
        private final String fDisplaySuffix;
        private final String fDisplayPrefix;

//      public Proposal(String string, String prefix, int offset) {
//          this(string, prefix, offset, offset + string.length());
//      }
//      public Proposal(String string, String prefix, int offset, String fDisplayPrefix,String fDisplaySuffix) {
//            this(string, prefix, offset, offset + string.length());
//        }
//      
    
        public Proposal(String string, String prefix, int offset, int newPosition,String fDisplayPrefix,String fDisplaySuffix) {
            this(string, prefix, prefix, offset, offset + string.length(),fDisplayPrefix,fDisplaySuffix);
        }

        public Proposal(String string, String prefix, String newPrefix, int offset, int newPosition,String fDisplayPrefix,String fDisplaySuffix) {
            fString = string;
            fPrefix = prefix;
            fNewPrefix = newPrefix;
            fOffset = offset;
            fNewPosition = newPosition;
            this.fDisplayPrefix = fDisplayPrefix;
            this.fDisplaySuffix = fDisplaySuffix;
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(IDocument)
         */
        public void apply(IDocument document) {
            apply(null, '\0', 0, fOffset);
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(IDocument)
         */
        public Point getSelection(IDocument document) {
            int position = fNewPosition;
            //added by estherbin 
            //fix http://jira.jboss.com/jira/browse/JBIDE-2277
            if(isStartsWithEl()){
                position+=EL_START_EXPRESSION.length();
            }
            return new Point(position, 0);
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
         */
        public String getAdditionalProposalInfo() {
            return null;
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
         */
        public String getDisplayString() {
            final StringBuffer sb = new StringBuffer();
            
            //added by estherbin 
            //fix http://jira.jboss.com/jira/browse/JBIDE-2277
            if((this.fDisplayPrefix.length() > 0) && (this.fDisplaySuffix.length() > 0)){
                sb.append(fDisplayPrefix+fPrefix+fString+fDisplaySuffix);
            }else{
                sb.append((fNewPrefix == null ? fPrefix : fNewPrefix) + fString);
            }
            return sb.toString();
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
         */
        public Image getImage() {
            return SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_ATTRIBUTE);
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
         */
        public IContextInformation getContextInformation() {
            return null;
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension#apply(IDocument, char, int)
         */
        public void apply(IDocument document, char trigger, int offset) {
            try {
                int docCharsToReplace = (fNewPrefix == null || fPrefix == null) ? 0 : fPrefix.length() - fNewPrefix.length();
                String replacement = fString.substring(offset - fOffset);
                //added by estherbin 
                //fix http://jira.jboss.com/jira/browse/JBIDE-2277
                if (isStartsWithEl()) {
                    replacement = EL_START_EXPRESSION + fPrefix + fString + EL_END_EXPRESSION;
                    if (document instanceof IDocumentExtension4) {
                        ((IDocumentExtension4) document).replace(offset - fPrefix.length(), getRemovedLength(offset,document,fPrefix.length()), "",
                                ((IDocumentExtension4) document).getModificationStamp());
                    }
                    document.replace(offset - fPrefix.length(), 0, replacement);

                } else {
                    document.replace(offset - docCharsToReplace, docCharsToReplace, replacement);
                }
            } catch (BadLocationException x) {
                SeamGuiPlugin.getPluginLog().logError(x);
            }
        }
        
        /**
         * @param document
         * @param length
         * @return
         */
        private int getRemovedLength(int offset,IDocument document, int length) {
            int len = length;
//            int _offset = offset;
//            while(document.getChar(offset))
            return len;
        }

        protected boolean isStartsWithEl(){
            return ((this.fDisplayPrefix.length() > 0) && (this.fDisplaySuffix.length() > 0));
        }

        /**
         * @param document
         * @param offset
         * @return
         */
        private int getBeginOffset(IDocument document, int offset) {
            int _offset = offset;
            while(_offset-- > 0){
                try {
                    if ('"' == document.getChar(offset) || '\'' == document.getChar(offset)) {
                        return _offset;
                    }
                } catch (BadLocationException e) {
                    SeamGuiPlugin.getPluginLog().logError(e);
                }
            }
            return 0;
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension#isValidFor(IDocument, int)
         */
        public boolean isValidFor(IDocument document, int offset) {
            return validate(document, offset, null);
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension#getTriggerCharacters()
         */
        public char[] getTriggerCharacters() {
            return null;
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension#getContextInformationPosition()
         */
        public int getContextInformationPosition() {
            return 0;
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#apply(ITextViewer, char, int, int)
         */
        public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
            apply(viewer.getDocument(), trigger, offset);
            if (fString != null && fString.endsWith(EL_END_EXPRESSION)) {
                fNewPosition -= 1;
            }
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#selected(ITextViewer, boolean)
         */
        public void selected(ITextViewer viewer, boolean smartToggle) {
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#unselected(ITextViewer)
         */
        public void unselected(ITextViewer viewer) {
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#validate(IDocument document, int offset, DocumentEvent event)
         */
        public boolean validate(IDocument document, int offset, DocumentEvent event) {
            try {
                int prefixStart= fOffset - fPrefix.length();
                return offset >= fOffset && offset < fOffset + fString.length() && document.get(prefixStart, offset - (prefixStart)).equals((fPrefix + fString).substring(0, offset - prefixStart));
            } catch (BadLocationException x) {
                return false;
            } 
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension3#getInformationControlCreator()
         */
        public IInformationControlCreator getInformationControlCreator() {
            return null;
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension3#getPrefixCompletionText(IDocument, int)
         */
        public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
            return fPrefix + fString;
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension3#getPrefixCompletionStart(IDocument, int)
         */
        public int getPrefixCompletionStart(IDocument document, int completionOffset) {
            return fOffset - fPrefix.length();
        }

        /*
         * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension4#isAutoInsertable()
         */
        public boolean isAutoInsertable() {
            return false;
        }
        
        /**
         * Return cursor position of proposal replacement string.
         */
        public int getCursorPosition() {
            return fNewPosition;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension5#getAdditionalProposalInfo(org.eclipse.core.runtime.IProgressMonitor)
         */
        public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    private final SeamELCompletionEngine fEngine= new SeamELCompletionEngine();

    /**
     * Creates a new Seam EL completion proposal computer.
     */
    public SeamELProposalProcessor() {
    }

    /*
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
     */
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        try {
            ITextEditor part = getActiveEditor();
            if (part == null) {
                return NO_PROPOSALS;
            }

            IEditorInput editorInput = part.getEditorInput();
            if (!(editorInput instanceof IFileEditorInput)) {
                return NO_PROPOSALS;
            }

            IFile file = ((IFileEditorInput)editorInput).getFile();
            IProject project = (file == null ? null : file.getProject());

            ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
            if (seamProject == null) {
                return NO_PROPOSALS;
            }

            String prefix= SeamELCompletionEngine.getPrefix(viewer, offset);
            prefix = (prefix == null ? "" : prefix); //$NON-NLS-1$

            String proposalPrefix = "";
            String proposalSufix = "";
            String documentContent = null;
            IDocument document = viewer.getDocument();
            if (!checkStartPositionInEL(viewer, offset,true)) {
                // Work only with attribute value for JSP/HTML
                if((part instanceof XMLTextEditor) || (!isAttributeValue(viewer, offset))) {
                    return NO_PROPOSALS;
                }
                prefix = ""; // Clear prefix because it's not the part of EL 
                if(isCharSharp(viewer, offset-1)) {
                    proposalPrefix = "{";  //$NON-NLS-1$
                } else {
                    proposalPrefix = "#{";  //$NON-NLS-1$
                }
                
                if(document != null) {
                    documentContent = document.get(0,offset) +  proposalPrefix + document.get(offset, document.getLength() - offset);
                }
                
                proposalSufix = "}";  //$NON-NLS-1$
            } else {
                if(viewer.getDocument() != null) {
                    documentContent = document.get();
                }
            }

            List<ElVarSearcher.Var> vars = ElVarSearcher.findAllVars(viewer, offset);
            List<String> suggestions = fEngine.getCompletions(seamProject, file, documentContent, prefix, offset + proposalPrefix.length() - prefix.length(), false, vars);
            List<String> uniqueSuggestions = fEngine.makeUnique(suggestions);

            List<ICompletionProposal> result= new ArrayList<ICompletionProposal>();
            for (String string : uniqueSuggestions) {
                
                if (string.length() >= 0) {
                    //added by estherbin 
                    //fix http://jira.jboss.com/jira/browse/JBIDE-2277
                    string = proposalPrefix + string + proposalSufix;
                    String elDisplaySuffix ="";
                    String elDisplayPrefix ="";
                    if(!checkStartPositionInEL(viewer, offset,false) && !string.endsWith(".")){
                        elDisplaySuffix="}";
                        elDisplayPrefix="#{";
                    }
                    if (string.startsWith("['") && string.endsWith("']") && prefix != null && prefix.endsWith(".")) {
                        String newPrefix = prefix.substring(0, prefix.length() - 1);
                        result.add(new Proposal(string, prefix, newPrefix, offset, (offset - 1 + string.length() - proposalSufix.length()),elDisplayPrefix,elDisplaySuffix));
                    } else {
                        result.add(new Proposal(string, prefix, offset, offset + string.length() - proposalSufix.length(),elDisplayPrefix,elDisplaySuffix));
                    }
                }
            }

            if (result == null || result.size() == 0) {
                return NO_PROPOSALS;
            }
            ICompletionProposal[] resultArray = result.toArray(new ICompletionProposal[result.size()]);
            Arrays.sort(resultArray, new Comparator<ICompletionProposal>() {
                public int compare(ICompletionProposal arg0,
                        ICompletionProposal arg1) {
                    String str0 = (arg0 == null ? "" : arg0.getDisplayString()); //$NON-NLS-1$
                    String str1 = (arg1 == null ? "" : arg1.getDisplayString()); //$NON-NLS-1$
                    return str0.compareTo(str1);
                }});
            return resultArray;
        } catch (BadLocationException x) {
            SeamGuiPlugin.getPluginLog().logError(x);
            return NO_PROPOSALS;
        } catch (StringIndexOutOfBoundsException e) {
            SeamGuiPlugin.getPluginLog().logError(e);
            return NO_PROPOSALS;
        }
    }

    private boolean isAttributeValue(ITextViewer viewer, int offset) {
        IndexedRegion treeNode = ContentAssistUtils.getNodeAt(viewer, offset);

        if(treeNode instanceof Node) {
            Node node = (Node) treeNode;
            while ((node != null) && (node.getNodeType() == Node.TEXT_NODE) && (node.getParentNode() != null)) {
                node = node.getParentNode();
            }
            if(node instanceof IDOMNode) {
                IDOMNode xmlnode = (IDOMNode) node;
                ITextRegion completionRegion = getCompletionRegion(offset, node);
                if (DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE != completionRegion.getType())
                    return false;
                ITextRegion nextRegion = getCompletionRegion(offset + 1, node);
                return DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE == nextRegion.getType();
            }
        }
        return false;
    }

    /*
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
     */
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        // no context informations for Seam EL completions
        return NO_CONTEXTS;
    }
    
    private char[] autoActivChars;

    /*
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
     */
    public char[] getCompletionProposalAutoActivationCharacters() {
        if(autoActivChars==null) {
            IPreferenceStore store= EditorsUI.getPreferenceStore();
            String superDefaultChars = store.getDefaultString(PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS_JAVA);
            StringBuffer redhatDefaultChars = new StringBuffer(superDefaultChars);
            if(superDefaultChars.indexOf("{")<0) { //$NON-NLS-1$
                redhatDefaultChars.append('{');
            }
            if(superDefaultChars.indexOf(".")<0) { //$NON-NLS-1$
                redhatDefaultChars.append('.');
            }
            autoActivChars = new char[redhatDefaultChars.length()];
            redhatDefaultChars.getChars(0, redhatDefaultChars.length(), autoActivChars, 0);
            store.setDefault(PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS_JAVA, redhatDefaultChars.toString());
            store.setValue(PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS_JAVA, redhatDefaultChars.toString());
        }
        return autoActivChars;
    }

    /*
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
     */
    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }
    
    /*
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
     */
    public IContextInformationValidator getContextInformationValidator() {
        return null;
    }

    /*
     * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#getErrorMessage()
     */
    public String getErrorMessage() {
        return null; // no custom error message
    }

    /*
     * Returns active text editor
     * @return
     */
    private ITextEditor getActiveEditor() {
        IWorkbenchWindow window= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            IWorkbenchPage page= window.getActivePage();
            if (page != null) {
                IEditorPart editor= page.getActiveEditor();
                if (editor instanceof IEditorWrapper)
                    editor = ((IEditorWrapper) editor).getEditor();
                
                if (editor instanceof ITextEditor)
                    return (ITextEditor) editor;
                else 
                    return (ITextEditor)editor.getAdapter(ITextEditor.class);
            }
        }
        return null;
    }

    private boolean isCharSharp(ITextViewer viewer, int offset) throws BadLocationException {
        IDocument doc= viewer.getDocument();
        if (doc == null || offset > doc.getLength() || offset < 0) {
            return false;
        }

        return '#' == doc.getChar(offset);
    }

    /*
     * Checks if the EL start starting characters are present
     * @param viewer
     * @param offset
     * @return
     * @throws BadLocationException
     */
    private boolean checkStartPositionInEL(ITextViewer viewer, int offset,boolean isCheckBackslashes) throws BadLocationException {
        
        // JBIDE-1676: Do not even try to get IStructuredDocument in case of not-a-structured document
        // This will prevent class cast exceptions while using the editors other than StructuredEditor 
        // (CompilationUnitEditor, for example). 
        IStructuredDocumentRegion sdRegion = 
            (viewer.getDocument() instanceof IStructuredDocument ? 
                    ContentAssistUtils.getStructuredDocumentRegion(viewer, offset) : 
                        null);
        ITextRegion region = (sdRegion == null ? null : sdRegion.getRegionAtCharacterOffset(offset));
        
        int startIndex = (region == null ? 0 : sdRegion.getStartOffset() + region.getStart());
        
        IDocument doc= viewer.getDocument();
        if (doc == null || offset > doc.getLength())
            return false;

        while (--offset >= startIndex) {
            if ('}' == doc.getChar(offset))
                return false;

            if (isCheckBackslashes) {
                if ('"' == doc.getChar(offset) || '\'' == doc.getChar(offset)) {
                    int backslashCount = 0;
                    while (doc.getChar(offset - 1 - backslashCount) == '\\') {
                        backslashCount++;
                    }
                    // Added by estherbin
                    // JBIDE-2227
                    if (backslashCount > 0 && (backslashCount % 2 == 0)) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }

            if ('{' == doc.getChar(offset) &&
                    (offset - 1) >= 0 && 
                    ('#' == doc.getChar(offset - 1) || 
                            '$' == doc.getChar(offset - 1))) {
                return true;
            }

        }
        return false;
    }
}