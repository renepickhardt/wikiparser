package de.renepickhardt.imessages.wikiparser;

import de.renepickhardt.imessages.wikiparser.xmlParser.SAXParserBufferedReader;
import de.renepickhardt.imessages.wikiparser.xmlParser.WikiPageHandler;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

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

	public static final String ENCODING = "UTF-8";
	private final static Logger logger = Logger.getLogger(App.class.getCanonicalName());

	public static void main(String[] args) {
		String userHomeDir = System.getProperty("user.home");
		String absoluteFilePath = userHomeDir + File.separator + "Downloads" + File.separator + "dewiki-20150301-pages-logging.xml.gz";
		//dewiki-20150301-pages-articles1.xml.bz2
		//dewiki-20150301-pages-logging.xml.gz
		//dewiki-20150301-pages-meta-current3.xml.bz2

		try {
			SAXParserBufferedReader br = createSAXParserBufferedReader(absoluteFilePath);
			if (!br.parse(new WikiPageHandler())) {
				logger.log(Level.SEVERE, "Error while SAXparsing.");
			}

			String line;
			int cnt = 0;
			int disc = 1;
			HashSet<String> set = new HashSet<>();
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

//				if (line.startsWith("</page>")) {
//				}
				cnt++;
				if (cnt % 5000000 == 0) {
					System.out.println("\t" + cnt);
				}
//                System.out.println(line);
//                if (cnt >100) break;
			}
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Could not access {0}", absoluteFilePath);
		} catch (IOException | CompressorException e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * Returns a buffered reader which is able to SAXparse on the given file if
	 * given a {@code org.xml.sax.helpers.DefaultHandler}.
	 * <p>
	 * @param absoluteFilePath full path to readable file.
	 * <p>
	 * @return @throws FileNotFoundException if {@code absoluteFilePath} does not
	 *         refer to a readable file.
	 * @throws CompressorException          if the compressor name is not known.
	 * @throws UnsupportedEncodingException if the encoding is not supported.
	 */
	public static SAXParserBufferedReader createSAXParserBufferedReader(String absoluteFilePath) throws FileNotFoundException, CompressorException, UnsupportedEncodingException {
		FileInputStream fin = new FileInputStream(absoluteFilePath);
		BufferedInputStream bis = new BufferedInputStream(fin);
		CompressorInputStream cis = new CompressorStreamFactory().createCompressorInputStream(bis);
		InputStreamReader isr = new InputStreamReader(cis, ENCODING);

		return new SAXParserBufferedReader(isr, ENCODING);
	}
}
