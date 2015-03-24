package de.renepickhardt.imessages.wikiparser;

import java.io.BufferedReader;

import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class is supposed to parse an XML file and extract all nodes that follow
 * a certain schema. The result will be a smaller XML file which contains only
 * The topics of interest.
 * 
 * The Schema can also contain values and expressions within nodes that if
 * matched or positively evaluated will also lead to extractin of the content
 * 
 * It is dsigned to handle large files XML files which do not fit into main
 * memory.
 * 
 * @author rpickhardt
 * 
 */
public class XmlPatternParser {

    private BufferedReader xmlBufferedReader;

    private String pattern;
    
    XMLReaderFactory xmlrf;
    
    public BufferedReader getXmlBufferedReader() {
        return xmlBufferedReader;
    }

    
    public void setXmlBufferedReader(BufferedReader xmlBufferedReader) {
        this.xmlBufferedReader = xmlBufferedReader;
    }
    
}
