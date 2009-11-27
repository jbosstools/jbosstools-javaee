package org.jboss.tools.jsf.vpe.seam.template;

import org.jboss.tools.jsf.vpe.seam.template.util.SeamUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SeamPdfPieChartTemplate extends SeamPdfAbstractTemplate {

	private static String PIE_CHART = "/pieChart/pieChart.png"; //$NON-NLS-1$
	private static String PIE_CHART_SERIES_NESTED = "/pieChart/pieChartSeriesNested.png"; //$NON-NLS-1$

	@Override
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		nsIDOMElement visualElement = visualDocument
				.createElement(HTML.TAG_IMG);
		Node[] seriesNodes = SeamUtil.getChildsByName(pageContext, sourceNode,
				"p:series");
		if (seriesNodes != null && seriesNodes.length != 0) {
			SeamUtil.setImg(visualElement, PIE_CHART_SERIES_NESTED);
		} else {
			SeamUtil.setImg(visualElement, PIE_CHART);
		}
		copySizeAttrs(visualElement, (Element)sourceNode);
		return new VpeCreationData(visualElement);
	}

}
