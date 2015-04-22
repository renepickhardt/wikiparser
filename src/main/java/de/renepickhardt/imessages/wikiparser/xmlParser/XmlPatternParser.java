package de.renepickhardt.imessages.wikiparser.xmlParser;

import java.io.BufferedReader;

/**
 * This class is supposed to parse an XML file and extract all nodes that follow
 * a certain schema. The result will be a smaller XML file which contains only
 * The topics of interest.
 * <p>
 * The Schema can also contain values and expressions within nodes that if
 * matched or positively evaluated will also lead to extraction of the content
 * <p>
 * It is designed to handle large files XML files which do not fit into main
 * memory.
 * <p>
 * @author rpickhardt
 * <p>
 */
public class XmlPatternParser {

	private BufferedReader xmlBufferedReader;

	public BufferedReader getXmlBufferedReader() {
		return xmlBufferedReader;
	}

	public void setXmlBufferedReader(BufferedReader xmlBufferedReader) {
		this.xmlBufferedReader = xmlBufferedReader;
	}
}
