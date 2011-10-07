package org.jboss.tools.cdi.seam.config.core.test;

import java.io.ByteArrayInputStream;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.jboss.tools.cdi.seam.config.core.xml.SAXElement;
import org.jboss.tools.cdi.seam.config.core.xml.SAXParser;

import junit.framework.TestCase;

public class ParserTest extends TestCase {

	public void testIncorrectXML() throws Exception {
		String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n</";			
		IDocument document = new Document();
		document.set(text);
		
		SAXParser parser = new SAXParser();
		parser.setSupperssedFatalErrorLimit(11);
		ByteArrayInputStream s = new ByteArrayInputStream(text.getBytes());
		SAXElement root = parser.parse(s, document);
		assertEquals(12, parser.getErrors().size());

		parser = new SAXParser();
		parser.setSupperssedFatalErrorLimit(0);
		s = new ByteArrayInputStream(text.getBytes());
		root = parser.parse(s, document);
		assertEquals(1, parser.getErrors().size());

	
	}

}
