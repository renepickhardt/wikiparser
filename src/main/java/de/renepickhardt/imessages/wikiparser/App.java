package de.renepickhardt.imessages.wikiparser;

import de.renepickhardt.imessages.wikiparser.xmlParser.SAXParserBufferedReader;
import de.renepickhardt.imessages.wikiparser.xmlParser.LoggingBlockHandler;
import de.renepickhardt.imessages.wikiparser.xmlParser.PageHistoryHandler;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class App {

	public static final String ENCODING = "UTF-8";
	public static final String[] FILE_PATH = new String[]{System.getProperty("user.home"), "Downloads", "delete", "wiki", "enwiki-20150429-pages-meta-hist-incr.xml.bz2"};
	private final static Logger logger = Logger.getLogger(App.class.getCanonicalName());

	public static void main(String[] args) {
		String absoluteFilePath = String.join(File.separator, FILE_PATH);

		try {
			SAXParserBufferedReader br = createSAXParserBufferedReader(absoluteFilePath);
			if (!br.parse(new PageHistoryHandler())) {
				logger.log(Level.SEVERE, "Error while SAXparsing.");
			}

			String line;
			Set<String> actionSet = new HashSet<>();
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("<action")) { // print all actions once
					if (!actionSet.contains(line)) {
						actionSet.add(line);
						System.out.println(line);
					}
				}
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
	 * refer to a readable file.
	 * @throws CompressorException if the compressor name is not known.
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
