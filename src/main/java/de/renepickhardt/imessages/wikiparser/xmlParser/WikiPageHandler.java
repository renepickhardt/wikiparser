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

import de.renepickhardt.imessages.wikiparser.dataTypes.LogItem;
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
public class WikiPageHandler extends DefaultHandler {

	private boolean timestamp;
	private boolean userName;
	private boolean action;
	private boolean comment;
	private boolean logTitle;
	private LogItem logItem;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		switch (qName) {
			case ("logitem"):
				logItem = new LogItem();
				timestamp = false;
				userName = false;
				action = false;
				comment = false;
				logTitle = false;
				break;
			case ("timestamp"):
				timestamp = true;
				break;
			case ("username"):
				userName = true;
				break;
			case ("action"):
				action = true;
				break;
			case ("logtitle"):
				logTitle = true;
				break;
			case ("comment"):
				comment = true;
				break;
			default:
				break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("logitem")) {
			if (logItem.getAction().equals("block") && !logItem.isTitleAnIpAddress()) {
				System.err.println(logItem);
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (timestamp) {
			logItem.setTimestamp(new String(ch, start, length));
			timestamp = false;
		} else if (userName) {
			logItem.setUser(new String(ch, start, length));
			userName = false;
		} else if (action) {
			logItem.setAction(new String(ch, start, length));
			action = false;
		} else if (comment) {
			logItem.setComment(new String(ch, start, length));
			comment = false;
		} else if (logTitle) {
			logItem.setTitle(new String(ch, start, length));
			logTitle = false;
		}
	}
}
