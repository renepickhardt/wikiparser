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
import java.util.logging.Level;
import java.util.logging.Logger;
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

	private final InputSource inputSource;
	private final static Logger logger = Logger.getLogger(SAXParserBufferedReader.class.getCanonicalName());

	/**
	 * <p>
	 * Creates a new reader instance.
	 * <p>
	 * @param in       a Reader.
	 * @param encoding of the input stream.
	 * <p>
	 */
	public SAXParserBufferedReader(Reader in, String encoding) {
		super(in);
		this.inputSource = new InputSource(in);
		inputSource.setEncoding(encoding);
	}

	/**
	 * <p>
	 * Parse the current input stream with the given handler.
	 * <p>
	 * @param handler The SAX handler to use for parsing.
	 * <p>
	 * @return {@code true} iff parsing was successful.
	 */
	public boolean parse(DefaultHandler handler) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			SAXParser parser = factory.newSAXParser();

			parser.parse(inputSource, handler);
			return true;
		} catch (SAXException | ParserConfigurationException | IOException e) {
			logger.log(Level.FINE, "SAX parsing was unsuccesful.");
			return false;
		}
	}
}
