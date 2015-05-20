/*
 * Copyright (C) 2015 mruster
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
import de.renepickhardt.imessages.wikiparser.dataTypes.Page;
import de.renepickhardt.imessages.wikiparser.dataTypes.Revision;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 * Default handler for SAXparsing Wikipedia XML page history dumps. The results
 * are written to a CSV file.
 * <p>
 * @author mruster
 */
public class PageHistoryHandler extends DefaultHandler {

	private final String PAGES_FILE_NAME = "pages.csv";
	private final String REVISIONS_FILE_NAME = "revisions.csv";

	private Page page;
	private boolean isPage = false;
	private boolean isPageId = false;
	private boolean isTimestamp = false;
	private boolean isTitle = false;
	private boolean isNamespace = false;

	private Revision revision;
	private boolean isRevision = false;
	private boolean isRevisionId = false;
	private boolean isRevisionParentId = false;
	private boolean isComment = false;
	private boolean isContributor = false;
	private boolean isUserId = false;
	private boolean isUserName = false;
	private boolean isText = false;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		switch (qName) {
			case ("page"):
				page = new Page();
				isPage = true;
				break;
			case ("timestamp"):
				isTimestamp = true;
				break;
			case ("contributor"):
				isContributor = true;
				break;
			case ("revision"):
				revision = new Revision();
				isRevision = true;
				break;
			case ("id"):
				if (isContributor) {
					isUserId = true;
				} else if (isRevision) {
					isRevisionId = true;
				} else if (isPage) {
					isPageId = true;
				}
				break;
			case ("parentid"):
				isRevisionParentId = true;
				break;
			case ("username"):
				if (isContributor) {
					isUserName = true;
				}
				break;
			case ("text"):
				if (isRevision) {
					isText = true;
				}
				break;
			case ("title"):
				isTitle = true;
				break;
			case ("comment"):
				isComment = true;
				break;
			case ("ns"):
				isNamespace = true;
				break;
			default:
				break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (qName) {
			case "contributor":
				isContributor = false;
				break;
			case "revision":
				page.addRevision(revision);
				isRevision = false;
				break;
			case "page":
				isPage = false;
				if (page.getNamespace() == 4) { // Wikipedia Namespace
					if (page.getTitle().startsWith("Wikipedia:Articles for deletion")) { // deletion discussions
						System.out.println("Processing a page with " + page.getRevisions().size() + " revisions.");
						try {
							FileWriter fw = new FileWriter(PAGES_FILE_NAME, true);
							CSVWriter writer = new CSVWriter(fw, '\t');
							writer.writeNext(page.toStringArray());
							writer.close();

							fw = new FileWriter(REVISIONS_FILE_NAME, true);
							writer = new CSVWriter(fw, '\t');
							for (Revision currentRevision : page.getRevisions()) {
								writer.writeNext(createPrintableRevision(currentRevision));
							}
							writer.close();
						} catch (IOException ex) {
							Logger.getLogger(PageHistoryHandler.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String text = new String(ch, start, length);
		if (isTimestamp) {
			page.setTimestamp(text);
			isTimestamp = false;
		} else if (isTitle) {
			page.setTitle(text);
			isTitle = false;
		} else if (isPageId) {
			page.setId(text);
			isPageId = false;
		} else if (isNamespace) {
			page.setNamespace(Integer.parseInt(text));
			isNamespace = false;
			// below there be revisions
		} else if (isContributor) {
			if (isUserName) {
				revision.setUserName(text);
				isUserName = false;
			} else if (isUserId) {
				revision.setUserId(text);
				isUserId = false;
			}
		} else if (isText) {
			revision.setText(text);
			isText = false;
		} else if (isComment) {
			revision.setComment(text);
			isComment = false;
		} else if (isRevisionId) {
			revision.setId(text);
			isRevisionId = false;
		} else if (isRevisionParentId) {
			revision.setParentId(text);
			isRevisionParentId = false;
		}
	}

	/**
	 * <p>
	 * Helper function to print a revision. Extracts the attribute {String[]} from
	 * the revision and prepends the associated {@code Page} id to the returned
	 * array.
	 *
	 * @param printRevision the revision to be prepared for printing.
	 * @return {page id, revision attributes}.
	 */
	private String[] createPrintableRevision(Revision printRevision) {
		ArrayList<String> tmpArrayList = new ArrayList<>();
		tmpArrayList.add(page.getId());
		String[] revisionStringArray = printRevision.toStringArray();
		tmpArrayList.addAll(Arrays.asList(revisionStringArray));
		return tmpArrayList.toArray(new String[tmpArrayList.size()]);
	}
}
