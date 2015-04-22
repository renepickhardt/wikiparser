/*
 * Copyright (C) 2015 rpickhardt, mruster
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.renepickhardt.imessages.wikiparser.xmlParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author rpickhardt
 * @author mruster
 */
public class SAXParserBufferedReader extends BufferedReader {

	/**
	 * <p>
	 * Creates a new reader instance and starts SAXparsing.
	 * <p>
	 * @param in       a Reader.
	 * @param handler  The SAX handler to use for parsing.
	 * @param encoding of the input stream.
	 * <p>
	 * @throws java.io.IOException                            for any given
	 *                                                        SAXException.
	 * @throws javax.xml.parsers.ParserConfigurationException if a parser cannot
	 *                                                        be created which
	 *                                                        satisfies the
	 *                                                        requested
	 *                                                        configuration
	 */
	public SAXParserBufferedReader(Reader in, DefaultHandler handler, String encoding) throws IOException, ParserConfigurationException {
		super(in);
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			SAXParser parser = factory.newSAXParser();

			InputSource is = new InputSource(in);
			is.setEncoding(encoding);

			parser.parse(is, handler);
		} catch (SAXException e) {
			System.err.println(e.getMessage());
		}
	}
}
