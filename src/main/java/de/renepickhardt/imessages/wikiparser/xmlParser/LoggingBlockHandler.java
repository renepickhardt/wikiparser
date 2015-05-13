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
 * Default handler for SAXparsing XML files from Wikipedia page dumps.
 * <p>
 * @author rpickhardt
 * @author mruster
 */
public class LoggingBlockHandler extends DefaultHandler {

	private boolean isTimestamp;
	/**
	 * In block logs, the contributor is an administrator.
	 */
	private boolean isContributor;
	private boolean isUserId;
	private boolean isUserName;
	private boolean isAction;
	private boolean isComment;
	/**
	 * The logtitle identifies the blocked object. In the case of the block log,
	 * this is our blocked user.
	 */
	private boolean isLogTitle;
	private LogItem logItem;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		switch (qName) {
			case ("logitem"):
				logItem = new LogItem();
				isTimestamp = false;
				isUserName = false;
				isAction = false;
				isComment = false;
				isLogTitle = false;
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
	 * @param uri       The Namespace URI, or the empty string if the element has
	 *                  no Namespace URI or if Namespace processing is not being
	 *                  performed.
	 * @param localName The local name (without prefix), or the empty string if
	 *                  Namespace processing is not being performed.
	 * @param qName     The qualified name (with prefix), or the empty string if
	 *                  qualified names are not available.
	 * <p>
	 * @throws org.xml.sax.SAXException - Any SAX exception, possibly wrapping
	 *                                  another exception.
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
						if (!logItem.isTitleAnIpAddress()) {
							try (FileWriter fw = new FileWriter("logItems.csv", true); CSVWriter writer = new CSVWriter(fw, '\t')) {
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
			logItem.setTimestamp(text);
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
		}
	}
}
