package org.jboss.tools.batch.ui.hyperlink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.core.IBatchProperty;
import org.jboss.tools.batch.internal.core.impl.BatchProjectFactory;
import org.jboss.tools.batch.ui.JSTJobUiPlugin;
import org.jboss.tools.common.refactoring.MarkerResolutionUtils;
import org.jboss.tools.common.text.ext.hyperlink.OpenJavaElementHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xml.XMLJumpToHyperlink;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.text.ext.util.Utils.AttrNodePair;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BatchHyperlinkDetector extends AbstractHyperlinkDetector {
	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region,
			boolean canShowMultipleHyperlinks) {
		List<IHyperlink> links = new ArrayList<IHyperlink>();

		IBatchProject batchProject = getBatchProject(MarkerResolutionUtils.getFile());
		if (batchProject == null) {
			return null;
		}

		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(textViewer.getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null)
				return null;

			AttrNodePair pair = Utils.findAttrNodePairForOffset(xmlDocument, region.getOffset());

			if (pair != null && pair.getNode() != null && pair.getAttribute() != null) {
				if (BatchConstants.ATTR_REF.equalsIgnoreCase(pair.getAttribute().getNodeName())) { // Open Java Class for attributre @ref
					IRegion sourceRegion = getAttributeValueSourceRegion(textViewer, region.getOffset(),
							(Attr) pair.getAttribute());

					if (sourceRegion != null) {
						Collection<IBatchArtifact> artifacts = batchProject.getArtifacts(pair.getAttribute()
								.getNodeValue());
						for (IBatchArtifact artifact : artifacts) {
							IType type = artifact.getType();
							links.add(new OpenJavaElementHyperlink(NLS.bind(
									BatchHyperlinkMessages.OPEN_JAVA_CLASS, type.getFullyQualifiedName()),
									textViewer.getDocument(), sourceRegion, type));
						}
					}

				} else if (BatchConstants.ATTR_NEXT.equalsIgnoreCase(pair.getAttribute().getNodeName()) // Show node with given id
						|| BatchConstants.ATTR_TO.equalsIgnoreCase(pair.getAttribute().getNodeName())
						|| BatchConstants.ATTR_RESTART.equalsIgnoreCase(pair.getAttribute().getNodeName())) {
					IRegion sourceRegion = getAttributeValueSourceRegion(textViewer, region.getOffset(),
							(Attr) pair.getAttribute());

					if (sourceRegion != null) {
						IndexedRegion node = findNodeWithId(xmlDocument, null, pair.getAttribute()
								.getNodeValue());

						if (node != null) {
							Region targetRegion = new Region(node.getStartOffset(),
									((IDOMElement) node).getStartEndOffset() - node.getStartOffset());
							links.add(new XMLJumpToHyperlink(NLS.bind(BatchHyperlinkMessages.GO_TO_NODE,
									((Node) node).getNodeName(), pair.getAttribute().getNodeValue()), textViewer
									.getDocument(), sourceRegion, targetRegion));
						}
					}
				} else if (BatchConstants.TAG_PROPERTY.equalsIgnoreCase(pair.getNode().getNodeName()) // Open Java Field
						&& BatchConstants.ATTR_NAME.equalsIgnoreCase(pair.getAttribute().getNodeName())) {
					IRegion sourceRegion = getAttributeValueSourceRegion(textViewer, region.getOffset(),
							(Attr) pair.getAttribute());

					if (sourceRegion != null) {
						String ref = findAttributeInAncestors(pair.getNode(), BatchConstants.ATTR_REF);
						if (ref != null) {
							Collection<IBatchArtifact> artifacts = batchProject.getArtifacts(ref);
							for (IBatchArtifact artifact : artifacts) {
								IBatchProperty property = artifact.getProperty(pair.getAttribute()
										.getNodeValue());
								if (property != null) {
									IField field = property.getField();
									links.add(new OpenJavaElementHyperlink(NLS.bind(
											BatchHyperlinkMessages.OPEN_JAVA_FIELD, field.getDeclaringType()
													.getFullyQualifiedName() + "." + field.getElementName()),
											textViewer.getDocument(), sourceRegion, field));
								}
							}
						}
					}
				}
			}
		} finally {
			smw.dispose();
		}
		if (links.size() == 0)
			return null;
		return (IHyperlink[]) links.toArray(new IHyperlink[links.size()]);

	}

	private IRegion getAttributeValueSourceRegion(ITextViewer textViewer, int offset, Attr attribute) {
		IRegion sourceRegion = null;
		try {
			sourceRegion = Utils.getAttributeValueRegion(textViewer.getDocument(), attribute);
		} catch (BadLocationException e) {
			JSTJobUiPlugin.getDefault().logError(e);
		}

		if (sourceRegion != null && offset >= sourceRegion.getOffset()
				&& offset <= (sourceRegion.getOffset() + sourceRegion.getLength())) {
			return sourceRegion;
		}
		return null;
	}

	private IBatchProject getBatchProject(IFile file) {
		IBatchProject batchProject = BatchProjectFactory.getBatchProjectWithProgress(file.getProject());

		if (batchProject != null) {
			for (IFile f : batchProject.getDeclaredBatchJobs()) {
				if (f.equals(file)) {
					return batchProject;
				}
			}
		}
		return null;
	}

	private String findAttributeInAncestors(Node node, String attrName) {
		if (node == null) {
			return null;
		}
		if (node.hasAttributes()) {
			NamedNodeMap attributes = node.getAttributes();
			Node attr = attributes.getNamedItem(attrName);
			if (attr != null) {
				return attr.getNodeValue();
			}
		}
		return findAttributeInAncestors(node.getParentNode(), attrName);
	}

	private IndexedRegion findNodeWithId(Node node, String nodeName, String id) {
		if (node instanceof IndexedRegion && (nodeName == null || nodeName.equalsIgnoreCase(node.getNodeName()))) {
			if (node.hasAttributes()) {
				NamedNodeMap attributes = node.getAttributes();
				Node attr = attributes.getNamedItem(BatchConstants.ATTR_ID);
				if (attr != null && id.equalsIgnoreCase(attr.getNodeValue())) {
					return (IndexedRegion) node;
				}
			}
		}
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			IndexedRegion result = findNodeWithId(child, nodeName, id);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
}
