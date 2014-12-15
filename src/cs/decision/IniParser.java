/*
 * Created by Roy
 *
 */

package cs.decision;
import java.util.*;
import java.io.*;
import java.util.regex.*;

/**
 * Parse a Windows style INI file.  A typical INI file contains
 * a few sections and a list of key-value pair in each section.
 * 
 * It's much more readable and editable than an xml file.
 * 
 */
public class IniParser {
	
	final static boolean DEBUG = false;
	
	/**
	 * For multiline key-value pairs, \ is used to continue the line.
	 */
	final char lineExt = '\\';
	
	/**
	 * A key or a value is  a string of non-white's.  Or can be
	 * anything enclosed in () or "".  The parenthasis and quotes
	 * are not striped, meaning they are part of the key or value.
	 */
	final String keyval = "[\\S]*|\\([^\\)]*\\)|\"[^\"]*\"";
	//final String keyval = "\\s*[^=]*\\s*";
	
	BufferedReader inBuffer;
	
	/**
	 * A section is of the form [section name]
	 */
	Pattern sectionPat = Pattern.compile("\\[\\s*([^]]*)\\s*\\]");
	
	/**
	 * @see #keyval
	 */
	Pattern keyvalPat = Pattern.compile("\\s*("+keyval+")\\s*=\\s*("+keyval+")\\s*");
	
	/**
	 * Hash table containing sections.  It's a table of hashes, keyed
	 * by the section title.
	 */
	Hashtable sections;
		
	public IniParser(String filename) throws FileNotFoundException {
		FileReader iniReader = new FileReader(new File(filename));
		inBuffer = new BufferedReader(iniReader);
		sections = new Hashtable();
	}
	
	/**
	 * Begin parsing the ini file.  It generates all the sections and stores
	 * the key-value pairs in each section.
	 * <p>
	 * Should call this function before accessing the sections.
	 */
	public void parseIniFile(){
		String line;
		IniSection currentSection=null;
		Matcher m;
		boolean lookingKeyval = false;
		try {
			while((line = inBuffer.readLine())!=null){
			
				// if it's an empty line or a comment
				if(line.length() == 0 || line.charAt(0) == '#')
					continue;
			
				// check if it's a section
				m = sectionPat.matcher(line);

				if(m.matches()) {
			
					// just found a new section, pull out the name
					String sectionName = m.group(1);
					currentSection = new IniSection(sectionName);
					sections.put(sectionName, currentSection);
					lookingKeyval = true;
					
					if(DEBUG)
						System.out.println("Found new section:"+sectionName);
					
				}else if(lookingKeyval){
					// it's not a section, but we are expecting this:
					// we are looking for key-value pairs in this section
					
					// combine separated lines
					String accumLine="";

					while(line.charAt(line.length()-1) == lineExt) {
						// remove the trailing '\'
						if(DEBUG)
							System.out.println("Found a \\");
						accumLine = accumLine +line.substring(0, line.length()-1);
						line = inBuffer.readLine();
					}

					accumLine = accumLine + line;
					
					m = keyvalPat.matcher(accumLine);
					if(DEBUG)
						System.out.println("matching "+accumLine);
					if(m.matches()){
						String key = m.group(1);
						String val = m.group(2);
						currentSection.addKey(key, val);
						if(DEBUG)
							System.out.println("add |"+key+ "| |"+ val+ "| to " + currentSection.getName());
					}else {
						System.out.println("Getting "+accumLine+ ": shouldn't happen");
					}
				}
			}
		}catch (IOException e) {
			System.out.println();
		}
	}
	
	/**
	 * Retrive a section by name.
	 * @param sectionName The name of the section.
	 * @return An IniSection object, null if the section doesnot exist.
	 */
	public IniSection getSection(String sectionName){
		return (IniSection)sections.get(sectionName);
	}
	
}

