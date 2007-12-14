package org.jboss.seam.test.unit;

import java.io.InputStreamReader;
import java.io.Reader;

import org.jboss.seam.text.SeamTextLexer;
import org.jboss.seam.text.SeamTextParser;

public class SeamTextTest
{
    public static void main(String[] args) throws Exception {
        Reader r = new InputStreamReader( SeamTextTest.class.getResourceAsStream("SeamTextTest.txt") );
        SeamTextLexer lexer = new SeamTextLexer(r);
        SeamTextParser parser = new SeamTextParser(lexer);
        parser.startRule();
        System.out.println(parser);
    }
}
