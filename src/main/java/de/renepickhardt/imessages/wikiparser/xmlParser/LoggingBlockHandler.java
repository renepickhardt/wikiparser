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

import com.opencsv.CSVWriter;
import de.renepickhardt.imessages.wikiparser.dataTypes.LogItem;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 * Default handler for SAXparsing Wikipedia XML logging dumps. The results are
 * written to a CSV file and contain only blocks.
 * <p>
 * @author rpickhardt
 * @author mruster
 */
public class LoggingBlockHandler extends DefaultHandler {

	private final static Logger logger = Logger.getLogger(LoggingBlockHandler.class.getCanonicalName());

	private final String FILE_NAME_TEMP = "blockItems_temp.csv";
	private final String FILE_NAME = "blockItems.csv";

	private LogItem logItem;
	private boolean isTimestamp = false;
	/**
	 * In block logs, the contributor is an administrator.
	 */
	private boolean isContributor = false;
	private boolean isUserId = false;
	private boolean isUserName = false;
	private boolean isAction = false;
	private boolean isComment = false;
	/**
	 * The logtitle identifies the blocked object. In the case of the block log,
	 * this is our blocked user.
	 */
	private boolean isLogTitle = false;
	private boolean isLogItemId = false;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		switch (qName) {
			case ("logitem"):
				logItem = new LogItem();
				break;
			case ("timestamp"):
				isTimestamp = true;
				break;
			case ("contributor"):
				isContributor = true;
				break;
			case ("id"):
				if (isContributor) {
					isUserId = true;
				} else {
					isLogItemId = true;
				}
				break;
			case ("username"):
				if (isContributor) {
					isUserName = true;
				}
				break;
			case ("action"):
				isAction = true;
				break;
			case ("logtitle"):
				isLogTitle = true;
				break;
			case ("comment"):
				isComment = true;
				break;
			default:
				break;
		}
	}

	/**
	 * <p>
	 * This method writes logItems to disk when they are of action type block and
	 * the blocked user is a registered user (not an IP address).
	 * <p>
	 * @param uri The Namespace URI, or the empty string if the element has no
	 * Namespace URI or if Namespace processing is not being performed.
	 * @param localName The local name (without prefix), or the empty string if
	 * Namespace processing is not being performed.
	 * @param qName The qualified name (with prefix), or the empty string if
	 * qualified names are not available.
	 * <p>
	 * @throws org.xml.sax.SAXException - Any SAX exception, possibly wrapping
	 * another exception.
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (qName) {
			case "contributor":
				isContributor = false;
				break;
			case "logitem":
				try {
					if ("block".equals(logItem.getAction().toLowerCase(Locale.ENGLISH))) {
						if (!logItem.wasBlockedUserAnonymous()) {
							try (FileWriter fw = new FileWriter(FILE_NAME_TEMP, true); CSVWriter writer = new CSVWriter(fw, '\t')) {
								writer.writeNext(logItem.toStringArray());
							}
						}
					}
				} catch (IOException ex) {
					Logger.getLogger(LoggingBlockHandler.class.getName()).log(Level.SEVERE, null, ex);
				}
				break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String text = new String(ch, start, length);
		if (isTimestamp) {
			// Timestamps are encoded as yyyy-MM-ddTHH-mm-ssZ (e.g. "2004-12-23T23:44:47Z")
			String timestamp = text.replace("T", " ").replace("Z", "");
			logItem.setTimestamp(timestamp);
			isTimestamp = false;
		} else if (isContributor) {
			if (isUserName) {
				logItem.setUserName(text);
				isUserName = false;
			} else if (isUserId) {
				logItem.setUserId(text);
				isUserId = false;
			}
		} else if (isAction) {
			logItem.setAction(text);
			isAction = false;
		} else if (isComment) {
			logItem.setComment(text);
			isComment = false;
		} else if (isLogTitle) {
			logItem.setTitle(text);
			isLogTitle = false;
		} else if (isLogItemId) {
			logItem.setId(text);
			isLogItemId = false;
		}
	}

	/**
	 * Post processes the intermediate block log by calling a python script. This
	 * one will also remove any WikiCode markup from the comments and filter the
	 * relevant block entries by their comment.
	 *
	 * @throws SAXException
	 */
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		logger.log(Level.INFO, "Now starting post processing the intermediate block log results.");
		boolean wasPostProcessingSuccessful = WikiCodeCleaner.postProcess("../../../" + FILE_NAME_TEMP, "../../../" + FILE_NAME);
		if (wasPostProcessingSuccessful) {
			File tempFile = new File(FILE_NAME_TEMP);
			tempFile.deleteOnExit();
			logger.log(Level.INFO, "Post processing was successful and the temporary file will be deleted when this program terminates.");
		} else {
			logger.log(Level.SEVERE, "Post processing was not successful.");
		}
	}
}
