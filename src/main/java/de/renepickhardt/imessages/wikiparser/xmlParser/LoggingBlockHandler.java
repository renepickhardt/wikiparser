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

	private int incompleteLogItemsCount = 0;
	private LogItem logItem;
	/**
	 * In block logs, the contributor is an administrator.
	 */
	private boolean isContributor = false;
	private StringBuilder tmpCharacters;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		tmpCharacters = new StringBuilder();
		switch (qName) {
			case ("logitem"):
				logItem = new LogItem();
				break;
			case ("contributor"):
				isContributor = true;
				break;
			default:
				break;
		}
	}

	/**
	 * <p>
	 * This method writes all text of this element into the matching attribute (if
	 * of interest). Furthermore, if this logItem ends, it is written to disk when
	 * they are of action type block and the blocked user is a registered user
	 * (not an IP address).
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
		String text = tmpCharacters.toString();
		switch (qName) {
			case ("timestamp"):
				// Timestamps are encoded as yyyy-MM-ddTHH-mm-ssZ (e.g. "2004-12-23T23:44:47Z")
				String timestamp = text.replace("T", " ").replace("Z", "");
				logItem.setTimestamp(timestamp);
				break;
			case ("username"):
				if (isContributor) {
					logItem.setUserName(text);
				}
				break;
			case ("id"):
				if (isContributor) {
					logItem.setUserId(text);
				} else {
					logItem.setId(text);
				}
				break;
			case ("action"):
				logItem.setAction(text);
				break;
			case ("comment"):
				logItem.setComment(text);
				break;
			case ("logtitle"):
				logItem.setTitle(text);
				break;
			case "contributor":
				isContributor = false;
				break;
			case "logitem":
				try {
					if ("block".equals(logItem.getAction().toLowerCase(Locale.ENGLISH))) {
						String title = logItem.getTitle();
						try {
							/**
							 * Truncates "User:" from the title to turn it into the user name.
							 */
							String blockedUserName = title.substring(5);
							logItem.setTitle(blockedUserName);
							if (logItem.getComment().isEmpty() || logItem.getTimestamp().isEmpty()) {
								throw new NullPointerException();
							}
							if (!logItem.wasBlockedUserAnonymous()) {
								try (FileWriter fw = new FileWriter(FILE_NAME_TEMP, true); CSVWriter writer = new CSVWriter(fw, '\t')) {
									writer.writeNext(logItem.toStringArray());
								}
							}
						} catch (NullPointerException e) {
							incompleteLogItemsCount++;
							logger.log(Level.FINE, "Due to missing crucial data (blocked user name, timestamp or comment), this log item is useless for us:\n{0}", logItem.toString());
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
		tmpCharacters.append(text);
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
		logger.log(Level.INFO, "{0} block items have been skipped due to incomplete data.", incompleteLogItemsCount);
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
