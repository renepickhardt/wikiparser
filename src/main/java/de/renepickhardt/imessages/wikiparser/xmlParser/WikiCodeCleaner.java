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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mruster
 */
public class WikiCodeCleaner {

	private final static Logger logger = Logger.getLogger(WikiCodeCleaner.class.getCanonicalName());

	/**
	 * <p>
	 * Calls an external Python 3 script to process the input String and clean it
	 * from any WikiCode syntax and other symbols that we do not care about.
	 *
	 * @param in any String that should be cleansed of markup and symbols unused
	 * by our later processing.
	 * @return input text without markup and other symbols that are of no use for
	 * us.
	 */
	public static String clean(String in) {
		StringBuilder out = new StringBuilder();
		try {
			Process p = Runtime.getRuntime().exec("python src/main/python/WikiCodeCleaner/clean.py \"" + in + "\"");

			BufferedReader processedIn = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line;
			while ((line = processedIn.readLine()) != null) {
				out.append(line);
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "The Python script to clean the WikiCode syntax raised an unrecoverable error. It must be fixed and this parsing must be re-run for proper results. To allow the inspection of all errors that might occur with the current dataset, the process is continued and the input string of this method is being returned as-is. The detailed error was:{0}", e.getLocalizedMessage());
		}

		return out.toString();
	}
}
