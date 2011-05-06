package org.jboss.tools.cdi.seam.config.ui.contentassist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLModelQueryCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentModelGenerator;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImages;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigConstants;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigExtension;
import org.jboss.tools.cdi.seam.config.core.util.Util;
import org.jboss.tools.cdi.seam.config.core.xml.SAXElement;
import org.jboss.tools.cdi.seam.config.ui.CDISeamConfigUIPlugin;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.xml.XMLUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class SeamConfigXmlCompletionProposalComputer extends AbstractXMLModelQueryCompletionProposalComputer implements CDISeamConfigConstants {
	static int RELEVANCE_TAG = XMLRelevanceConstants.R_STRICTLY_VALID_TAG_NAME;
	static int RELEVANCE_TAG_KEYWORD = RELEVANCE_TAG - 1;
	static int RELEVANCE_TAG_ANNOTATION = RELEVANCE_TAG_KEYWORD - 1;
	static int RELEVANCE_TAG_MEMBER = RELEVANCE_TAG_ANNOTATION - 1;
	static int RELEVANCE_TAG_TYPE = RELEVANCE_TAG_MEMBER - 1;

	CompletionProposalInvocationContext context;
	Node currentNode;
	Element parentElement;
	SAXElement sax;

	IFile resource;
	IProject project;
	IJavaProject javaProject;
	CDICoreNature cdi;
	CDISeamConfigExtension extension;

	Map<String, String> uriByPrefix = new HashMap<String, String>();
	Map<String, String> prefixByUri = new HashMap<String, String>();
	String eePrefix;
	Map<String, String> prefixByPackage = new HashMap<String, String>();

	public List<?> computeCompletionProposals(
			CompletionProposalInvocationContext context,
			IProgressMonitor monitor) {
		this.context = context;
		currentNode = findNode();
		if(currentNode instanceof IDOMElement && context.getInvocationOffset() == ((IDOMElement)currentNode).getStartOffset()) {
			currentNode = currentNode.getPreviousSibling() != null ? currentNode.getPreviousSibling() : currentNode.getParentNode();
		}

		Node p = currentNode;
		while(p != null && !(p instanceof Element)) p = p.getParentNode();
		if(!(p instanceof Element)) {
			// can do nothing without context of parent element.
			return new ArrayList<Object>();
		}
		parentElement = (Element)p;
		sax = buildSAXElement(currentNode);
	
		fillNameSpaces(currentNode);
		resource = findResource(getDocument());
		if(resource != null) {
			javaProject = EclipseResourceUtil.getJavaProject(resource.getProject());
			cdi = CDICorePlugin.getCDI(resource.getProject(), true);
			if(cdi != null) extension = CDISeamConfigExtension.getExtension(cdi);
		}
		if(!isActive()) {
			return new ArrayList<Object>();
		}
		//compute the completion proposals
		return super.computeCompletionProposals(context, monitor);
	}

	protected boolean isActive() {
		//Cannot work without resource.
		//Have nothing to suggest without packages.
		//can only add in context of an element.
		return (resource != null && !prefixByPackage.isEmpty() && sax != null);
	}
	
	protected IDocument getDocument() {
		return context.getDocument();
	}

	private Node findNode() {
		return (Node)ContentAssistUtils.getNodeAt(context.getViewer(), context.getInvocationOffset());
	}

	private IFile findResource(IDocument document) {
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(document);
		try {
			if (sModel != null) {
				String baseLocation = sModel.getBaseLocation();
				IPath location = new Path(baseLocation).makeAbsolute();
				return FileBuffers.getWorkspaceFileAtLocation(location);
			}
		} finally {
			if (sModel != null) {
				sModel.releaseFromRead();
			}
		}
		return null;
	}

	private SAXElement buildSAXElement(Node node) {
		while(node != null && !(node instanceof Element)) {
			node = node.getParentNode();
		}
		if(node != null) {
			SAXElement sax = new SAXElement(node);
			SAXElement parent = buildSAXElement(node.getParentNode());
			sax.setParent(parent);
			return sax;
		}
		
		return null;
	}
	
	@Override
	protected void addTagInsertionProposals(
			ContentAssistRequest contentAssistRequest, int childPosition,
			CompletionProposalInvocationContext context) {
		Set<TagData> tagData = collectTagData();

		for (TagData tag: tagData) {
			if(tag.isUnique()) {
				if(XMLUtilities.getUniqueChild((Element)parentElement, tag.getName()) != null) continue;
			}
			String tagText = tag.getText();
			int positionAdjustment = (tagText.indexOf("><") > 0) ? tagText.indexOf("><") + 1 : tagText.length();
			String displayText = tag.getName();
			if(tag.getMember() instanceof IType) {
				displayText += " - " + ((IType)tag.getMember()).getPackageFragment().getElementName();
			}
			createProposal(contentAssistRequest, tagText, displayText, positionAdjustment, tag.getMember(), tag.getRelevance());
		}
	}

	private Set<TagData> collectTagData() {
		String parentElementName = sax.getLocalName();
		String parentElementPrefix = sax.getPrefix();
		
		Set<TagData> tagData = new HashSet<TagData>();

		if("beans".equals(parentElementName)) {
			//suggest all classes in all packages.
			addTypeNames(tagData, false);
		} else if(parentElementPrefix != null && prefixByPackage.containsValue(parentElementPrefix)) {
			// If we are not in <beans>, then we have to be in context of some seam package.
			IType contextType = null;
			if(Util.isEntry(sax)) {
				//Inside entry - only <key> and <value>
				if(eePrefix != null) {					
					addTagData(tagData, eePrefix, KEYWORD_KEY, true, true, RELEVANCE_TAG_KEYWORD);
					addTagData(tagData, eePrefix, KEYWORD_VALUE, true, true, RELEVANCE_TAG_KEYWORD);
				}
			} else if(Util.isValue(sax) || Util.isKey(sax)) {
				//Inside value or key we can define new bean.
				addTypeNames(tagData, false);
			} else if(Util.isParameters(sax)) {
				//TODO find parent method name and type, and suggest parameter types. 
			} else if((contextType = Util.resolveType(sax, cdi)) != null) {
				//We are inside bean. Suggest members and annotations.
				addTypeNames(tagData, true); //only annotations allowed here.
				addTagData(tagData, getTagNamesForMembers(parentElementPrefix, contextType, false), RELEVANCE_TAG_MEMBER);

				if(eePrefix != null
						&& XMLUtilities.getUniqueChild((Element)parentElement, eePrefix + ":" + KEYWORD_MODIFIES) == null
						&& XMLUtilities.getUniqueChild((Element)parentElement, eePrefix + ":" + KEYWORD_REPLACES) == null) {
					addTagData(tagData, eePrefix, KEYWORD_MODIFIES, false, true, RELEVANCE_TAG_KEYWORD);
					addTagData(tagData, eePrefix, KEYWORD_REPLACES, false, true, RELEVANCE_TAG_KEYWORD);
				}
			} else if(sax.getParent() != null && ((contextType = Util.resolveType(sax.getParent(), cdi)) != null)) {
				IMember member = null;
				try {
					member = Util.resolveMember(contextType, sax);
				} catch (JavaModelException e) {
					CDISeamConfigUIPlugin.log(e);
				}
				if(member != null) {
					//We are inside bean member. Suggest annotations and <value>.
					addTypeNames(tagData, true); //only annotations allowed here.
					if(eePrefix != null) {
						if(member instanceof IField) {
							addTagData(tagData, eePrefix, KEYWORD_VALUE, true, RELEVANCE_TAG_KEYWORD);
						} else if(member instanceof IMethod) {
							addTagData(tagData, eePrefix, KEYWORD_PARAMETERS, true, true, RELEVANCE_TAG_KEYWORD);
						}
					}

				}
			}
		}
		return tagData;
	}

	private void addTypeNames(Set<TagData> tagData, boolean annotationsOnly) {
		try {
			Map<String, IMember> tagNames = getAllTagNames(annotationsOnly);
			addTagData(tagData, tagNames, annotationsOnly ? RELEVANCE_TAG_ANNOTATION : RELEVANCE_TAG_TYPE);
		} catch (JavaModelException e) {
			CDISeamConfigUIPlugin.log(e);
		}
	}

	private void addTagData(Set<TagData> tagData, String prefix, String name, boolean hasClosingTag, int relevance) {
		tagData.add(new TagData(prefix, name, hasClosingTag, false, relevance));
	}

	private void addTagData(Set<TagData> tagData, String prefix, String name, boolean hasClosingTag, boolean isUnique, int relevance) {
		tagData.add(new TagData(prefix, name, hasClosingTag, isUnique, relevance));
	}

	private void addTagData(Set<TagData> tagData, Map<String, IMember> tagNames, int relevance) {
		for (String tagName: tagNames.keySet()) {
			TagData d = new TagData(tagName, relevance);
			IMember m = tagNames.get(tagName);
			if(m instanceof IType) {
				String qn = ((IType)m).getFullyQualifiedName();
				//TODO improve list of annotations that never need children.  
				if(QUALIFIER_ANNOTATION_TYPE_NAME.equals(qn)
					|| ANY_QUALIFIER_TYPE_NAME.equals(qn)
					|| DEFAULT_QUALIFIER_TYPE_NAME.equals(qn)
					|| SPECIALIZES_ANNOTATION_TYPE_NAME.equals(qn)
					|| DECORATOR_STEREOTYPE_TYPE_NAME.equals(qn)
					|| INJECT_ANNOTATION_TYPE_NAME.equals(qn)
					|| PRODUCES_ANNOTATION_TYPE_NAME.equals(qn)) {
					d.setHasClosingTag(false);
				}
			}
			d.setMember(m);
			tagData.add(d);
		}
	}

	protected void addTagNameProposals(ContentAssistRequest contentAssistRequest,
			int childPosition, CompletionProposalInvocationContext context) {
		IDOMNode node = (IDOMNode) contentAssistRequest.getNode();
		boolean isEnded = (node.getNodeType() != Node.TEXT_NODE) &&
				node.getFirstStructuredDocumentRegion().isEnded();
		if(currentNode == parentElement) {
			parentElement = (Element)parentElement.getParentNode();
			sax = sax.getParent();
		}

		String match = contentAssistRequest.getMatchString();

		Set<TagData> tagData = collectTagData();
		if(match != null && match.length() > 0) {
			Iterator<TagData> i = tagData.iterator();
			while(i.hasNext()) {
				TagData d = i.next();
				if(!d.getName().startsWith(match) && d.getName().indexOf(":" + match) < 0) {
					i.remove();
				}
			}
		}
		
		for (TagData tag: tagData) {
			if(tag.isUnique()) {
				if(XMLUtilities.getUniqueChild((Element)parentElement, tag.getName()) != null) continue;
			}
			String tagText = isEnded ? tag.getName() : tag.getText();
			if(tagText.startsWith("<")) tagText = tagText.substring(1);
			int positionAdjustment = (tagText.indexOf("><") > 0) ? tagText.indexOf("><") + 1 : tagText.length();
			String displayText = tag.getName();
			if(tag.getMember() instanceof IType) {
				displayText += " - " + ((IType)tag.getMember()).getPackageFragment().getElementName();
			}
			createProposal(contentAssistRequest, tagText, displayText, positionAdjustment, tag.getMember(), tag.getRelevance());
		}
	}

	private void createProposal(ContentAssistRequest contentAssistRequest, String tagText, String displayText, int positionAdjustment, IMember member, int relevance) {
		int begin = contentAssistRequest.getReplacementBeginPosition();;
		int length = contentAssistRequest.getReplacementLength();
		String proposedInfo = null;
		if(member != null) {
			try {
				proposedInfo = JavadocContentAccess2.getHTMLContent(member, true);
			} catch (JavaModelException e) {
				CDISeamConfigUIPlugin.log(e);
			}
		}
		String imagePath = XMLEditorPluginImages.IMG_OBJ_TAG_GENERIC;
		if(tagText.endsWith("\"")) { //improve that dirty hack
			imagePath = XMLEditorPluginImages.IMG_OBJ_ATTRIBUTE;
		}
		CustomCompletionProposal textProposal = new CustomCompletionProposal(
				tagText, begin, length, positionAdjustment,
				XMLEditorPluginImageHelper.getInstance().getImage(imagePath),
				displayText, null, proposedInfo, relevance);
		contentAssistRequest.addProposal(textProposal);
	}

	protected void addAttributeNameProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		IType contextType = Util.resolveType(sax, cdi);
		if(contextType != null) {
			String match = contentAssistRequest.getMatchString();
			Set<TagData> tagData = new HashSet<TagData>();
			addTagData(tagData, getTagNamesForMembers(sax.getPrefix(), contextType, true), RELEVANCE_TAG_MEMBER);
			for (TagData tag: tagData) {
				String attrName = tag.getName().substring(tag.getName().indexOf(':') + 1);
				if(parentElement.hasAttribute(attrName)) continue;
				if(match != null && !attrName.startsWith(match)) continue;
				String tagText = attrName + "=\"\"";
				int positionAdjustment = tagText.length() - 1;
				createProposal(contentAssistRequest, tagText, attrName, positionAdjustment, tag.getMember(), tag.getRelevance());
			}
		}

	}

	@Override
	protected XMLContentModelGenerator getContentGenerator() {
		return new XMLContentModelGenerator();
	}

	@Override
	protected boolean validModelQueryNode(CMNode node) {
		return false;
	}

	static String XMLNS_PREFIX = "xmlns:";

	private void fillNameSpaces(Node node) {
		uriByPrefix.clear();
		prefixByUri.clear();
		prefixByPackage.clear();
		eePrefix = null;
		while(node != null) {
			if(node instanceof Element) {
				Element element = (Element)node;
				NamedNodeMap as = element.getAttributes();
				for (int i = 0; i < as.getLength(); i++) {
					Node a = as.item(i);
					String nm = a.getNodeName();
					if(nm.startsWith(XMLNS_PREFIX)) {
						String prefix = nm.substring(XMLNS_PREFIX.length());
						String uri = a.getNodeValue();
						if(uri != null) {
							uriByPrefix.put(prefix, uri);
							prefixByUri.put(uri, prefix);
							String[] packages = Util.getPackages(uri);
							for (String pkg: packages) {
								prefixByPackage.put(pkg, prefix);
							}
						}
					}
				}
			}
			eePrefix = prefixByPackage.get(PACKAGE_EE);
			node = node.getParentNode();
		}
	}

	Map<String, IMember> getAllTagNames(boolean annotationsOnly) throws JavaModelException {
		Map<String, IMember> result = new HashMap<String, IMember>();
		for (String packageName: prefixByPackage.keySet()) {
			String prefix = prefixByPackage.get(packageName);
			Map<String, IMember> typeNames = findTypeNamesByPackage(javaProject, packageName, annotationsOnly);
			for (String typeName: typeNames.keySet()) {
				result.put(prefix + ":" + typeName, typeNames.get(typeName));
			}
		}
		return result;
	}

	Map<String, IMember> getTypeNamesByPrefix(String prefix, boolean annotationsOnly) throws JavaModelException {
		Map<String, IMember> result = new HashMap<String, IMember>();
		String uri = uriByPrefix.get(prefix);
		for (String packageName: Util.getPackages(uri)) {
			result.putAll(findTypeNamesByPackage(javaProject, packageName, annotationsOnly));
		}
		return result;
	}

	public static Map<String, IMember> findTypeNamesByPackage(IJavaProject javaProject, String packageName, boolean annotationsOnly) throws JavaModelException {
		Map<String, IMember> result = new HashMap<String, IMember>();
		if(PACKAGE_EE.equals(packageName)) {
			for (String name: Util.EE_TYPES.keySet()) {
				String cls = Util.EE_TYPES.get(name);
				IType t = EclipseJavaUtil.findType(javaProject, cls);
				if(accept(t, annotationsOnly)) result.put(name, t);
			}
		} else if(javaProject != null) {
			IPackageFragmentRoot[] rs = javaProject.getAllPackageFragmentRoots();
			for (IPackageFragmentRoot r: rs) {
				IPackageFragment pkg = r.getPackageFragment(packageName);
				if(pkg != null && pkg.exists()) {
					ICompilationUnit[] units = pkg.getCompilationUnits();
					for (ICompilationUnit u: units) {
						IType[] ts = u.getTypes();
						for (IType t: ts) if(accept(t, annotationsOnly)) result.put(t.getElementName(), t);
					}
					IClassFile[] cs = pkg.getClassFiles();
					for (IClassFile cls: cs) {
						if(accept(cls.getType(), annotationsOnly)) result.put(cls.getType().getElementName(), cls.getType());
					}
				}
			}
			
		}
		return result;
	}

	private static boolean accept(IType type, boolean annotationOnly) throws JavaModelException {
		return (type != null) && (!annotationOnly || type.isAnnotation());
	}

	private Map<String, IMember> getTagNamesForMembers(String prefix, IType type, boolean fieldsOnly) {
		Map<String, IMember> result = new HashMap<String, IMember>();
		try {
			IField[] fs = type.getFields();
			for (IField f: fs) {
				result.put(prefix + ":" + f.getElementName(), f);
			}
			if(!fieldsOnly || type.isAnnotation()) {
				IMethod[] ms = type.getMethods();
				for (IMethod m: ms) {
					result.put(prefix + ":" + m.getElementName(), m);
				}
			}
		} catch (JavaModelException e) {
			CDISeamConfigUIPlugin.log(e);
		}
		return result;
	}

}

