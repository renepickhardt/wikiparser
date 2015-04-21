package de.renepickhardt.imessages.wikiparser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * <mediawiki xmlns="http://www.mediawiki.org/xml/export-0.10/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.mediawiki.org/xml/export-0.10/ http://www.mediawiki.org/xml/export-0.10.xsd" version="0.10" xml:lang="de">
 * <siteinfo>
 * <sitename>Wikipedia</sitename>
 * <dbname>dewiki</dbname>
 * <base>http://de.wikipedia.org/wiki/Wikipedia:Hauptseite</base>
 * <generator>MediaWiki 1.25wmf18</generator>
 * <case>first-letter</case>
 * <namespaces>
 * <namespace key="-2" case="first-letter">Medium</namespace>
 * <namespace key="-1" case="first-letter">Spezial</namespace>
 * <namespace key="0" case="first-letter" />
 * <namespace key="1" case="first-letter">Diskussion</namespace>
 * <namespace key="2" case="first-letter">Benutzer</namespace>
 * <namespace key="3" case="first-letter">Benutzer Diskussion</namespace>
 * <namespace key="4" case="first-letter">Wikipedia</namespace>
 * <namespace key="5" case="first-letter">Wikipedia Diskussion</namespace>
 * <namespace key="6" case="first-letter">Datei</namespace>
 * <namespace key="7" case="first-letter">Datei Diskussion</namespace>
 * <namespace key="8" case="first-letter">MediaWiki</namespace>
 * <namespace key="9" case="first-letter">MediaWiki Diskussion</namespace>
 * <namespace key="10" case="first-letter">Vorlage</namespace>
 * <namespace key="11" case="first-letter">Vorlage Diskussion</namespace>
 * <namespace key="12" case="first-letter">Hilfe</namespace>
 * <namespace key="13" case="first-letter">Hilfe Diskussion</namespace>
 * <namespace key="14" case="first-letter">Kategorie</namespace>
 * <namespace key="15" case="first-letter">Kategorie Diskussion</namespace>
 * <namespace key="100" case="first-letter">Portal</namespace>
 * <namespace key="101" case="first-letter">Portal Diskussion</namespace>
 * <namespace key="828" case="first-letter">Modul</namespace>
 * <namespace key="829" case="first-letter">Modul Diskussion</namespace>
 * </namespaces>
 * </siteinfo>
 */
public class App {

	public static void main(String[] args) throws CompressorException, IOException {
		String userHomeDir = System.getProperty("user.home");
		BufferedReader br = getBufferedReaderForBZ2File(userHomeDir + File.separator + "Downloads" + File.separator + "dewiki-20150301-pages-logging.xml.gz");
		//dewiki-20150301-pages-articles1.xml.bz2
		//dewiki-20150301-pages-logging.xml.gz
		//dewiki-20150301-pages-meta-current3.xml.bz2

		String line;
		int cnt = 0;
		int disc = 1;
		HashSet<String> set = new HashSet<>();
		try {
			while ((line = br.readLine()) != null) {
				line = line.trim();
//				if (line.startsWith("<page>")) {
//				}

				if (line.startsWith("<ns>")) {
					line = line.replace("<ns>", "");
					line = line.replace("</ns>", "");
					int ns = Integer.parseInt(line);
					if (ns % 2 != 0)//found discussion page
					{
						if (disc++ % 100 == 0) {
							System.out.println(disc);
						}
					}
				}

				if (line.startsWith("<title>Lösch")) {
					System.out.println(line);
				}

				if (line.startsWith("<action>")) {
					if (!set.contains(line)) {
						set.add(line);
						System.out.println(line);
					}
				}

				if (line.startsWith("</page>")) {

				}
				cnt++;
				if (cnt % 5000000 == 0) {
					System.out.println("\t" + cnt);
				}
//                System.out.println(line);
//                if (cnt >100) break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BufferedReader getBufferedReaderForBZ2File(String fileIn) throws CompressorException, IOException {
		FileInputStream fin = new FileInputStream(fileIn);
		BufferedInputStream bis = new BufferedInputStream(fin);
		CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(input, "UTF-8"));

		try {
			XMLReader myReader = XMLReaderFactory.createXMLReader();
			InputSource is = new InputSource(input);

			myReader.setContentHandler(new DefaultHandler() {
				private boolean beginItem = false;
				private boolean timestamp;
				private boolean userName;
				private boolean action;
				private boolean comment;
				private boolean logTitle;

				private LogItem logItem;

				@Override
				public void startElement(
								String uri,
								String localName,
								String qName,
								Attributes atts) throws SAXException {
					if (localName.equals("logitem")) {
						beginItem = true;
						logItem = new LogItem();

						timestamp = false;
						userName = false;
						action = false;
						comment = false;
						logTitle = false;

					}
					if (beginItem) {
						if (localName.equals("timestamp")) {
							timestamp = true;
						}
						if (localName.equals("username")) {
							userName = true;
						}
						if (localName.equals("action")) {
							action = true;
						}
						if (localName.equals("logtitle")) {
							logTitle = true;
						}
						if (localName.equals("comment")) {
							comment = true;
						}
					}
				}

				@Override
				public void endElement(String uri, String localName, String qName)
								throws SAXException {
					if (localName.equals("logitem")) {
						beginItem = false;
						if (logItem.getAction().equals("block") && !logItem.isTitleAnIpAddress()) {
							System.err.println(logItem);
						}
					}
				}

				@Override
				public void characters(char[] ch, int start, int length)
								throws SAXException {
					if (timestamp) {
						logItem.setTimestamp(new String(ch, start, length));
						timestamp = false;
					}
					if (userName) {
						logItem.setUser(new String(ch, start, length));
						userName = false;
					}
					if (action) {
						logItem.setAction(new String(ch, start, length));
						action = false;
					}
					if (comment) {
						logItem.setComment(new String(ch, start, length));
						comment = false;
					}
					if (logTitle) {
						logItem.setTitle(new String(ch, start, length));
						logTitle = false;
					}
				}
			});

			myReader.parse(is);

		} catch (SAXException e) {
			System.err.println(e.getMessage());
		}

		return br2;
	}
}
