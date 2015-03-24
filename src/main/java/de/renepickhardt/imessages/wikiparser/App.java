package de.renepickhardt.imessages.wikiparser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * Hello world!
 *
 */
public class App 
{
	private static boolean containsString( String s, String subString ) {
        return s.indexOf( subString ) > -1 ? true : false;
    }
	
    public static void main( String[] args )
    {     
        int cnt=0;
        try{ 
        BufferedReader br=getBufferedReaderForBZ2File("/media/anna/Daten/Wikipedia/dewiki-20150301-pages-articles2.xml.bz2");		//Dateipfad zur Wiki Bz2
        String line=" ";
        	while((line=br.readLine())!=null){
        		if(containsString(line, "<title>Wikipedia:Löschkandidaten/")){
        			System.out.println(line);
        		/*
        			while(cnt<100){
        				System.out.println(br.readLine());
        				cnt++;
        			}
        			break;
  */
        		}
        		
        		/*
        		System.out.println(line);		//Ausgabe der ersten 100 Zeilen
        		cnt++;
        		if(cnt>100)
        			break;
        			*/
        	}
        }
        catch(FileNotFoundException fnfe){
        	fnfe.printStackTrace();
        }
        catch(CompressorException ce){
        	ce.printStackTrace();
        }
        catch(IOException ioe) {
        	ioe.printStackTrace();
        }
    }
    
    
    public static BufferedReader getBufferedReaderForBZ2File(String fileIn) throws FileNotFoundException, CompressorException {
        FileInputStream fin = new FileInputStream(fileIn);
        BufferedInputStream bis = new BufferedInputStream(fin);
        CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(input));

        return br2;
    }
}
