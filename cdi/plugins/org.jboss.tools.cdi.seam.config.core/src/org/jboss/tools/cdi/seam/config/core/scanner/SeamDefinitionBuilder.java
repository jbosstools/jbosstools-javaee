package org.jboss.tools.cdi.seam.config.core.scanner;

import java.io.ByteArrayInputStream;

import org.eclipse.jface.text.IDocument;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeansDefinition;

public class SeamDefinitionBuilder {

	public SeamBeansDefinition createDefinition(IDocument document, CDICoreNature project) {
		SAXParser parser = new SAXParser();
		String text = document.get();
		ByteArrayInputStream s = new ByteArrayInputStream(text.getBytes());
		SAXElement element = parser.parse(s, document);
		SeamBeansDefinition result = new SeamBeansDefinition();
		//TODO
		return result;
	}

}
