/*
 * Created by Roy
 *
 */
package cs.decision;

import java.util.*;

/**
 * One section of an INI file, holding all the key-value pairs.
 */
final public class IniSection {

	/**
	 * Hashtable holding all the key-value pairs
	 */
	Hashtable keyvals;
	
	/**
	 * In cases that we want to access every value, use vals.
	 */
	Vector vals;
	
	/**
	 * An array holds all the keys.
	 */
	Vector keys;
	
	/**
	 * Name of this section
	 */
	String name;
	
	public IniSection(String name){
		this.name = name;
		keyvals = new Hashtable();
	}
	
	/**
	 * Insert a key-value pair into this section
	 * @param key The key
	 * @param value The value
	 */
	public void addKey(String key, String value){
		keyvals.put(key, value);
	}
	
	/**
	 * Retrive a value by its key
	 * @param key The name of the key
	 * @return The value (String), null if key doesnot exist.
	 */
	public String getValue(String key){
		return (String)keyvals.get(key);
	}
	
	/**
	 * Return the name of this section.  For debugging purposes.
	 * @return The name of this section.
	 */
	public String getName() {
		return name;
	}
	
	public int size() {
		return keyvals.size();
	}
	
	public void vectorize() {
		// This is as stupid as it gets.  Goes a long way to get all the
		// keys into a collection/array.  Can't quite comprehend why
		// keys can only be turned into a set.
		int size = this.size();
		keys = new Vector(size);
		vals = new Vector(size);
		for (Enumeration e=keyvals.keys() ; e.hasMoreElements() ;) {
			Object ele = e.nextElement();
			keys.add(ele);
			vals.add(keyvals.get(ele));
	     }
	}
	
	public String getValue(int i) {
		return (String)vals.get(i);
	}
	
	public String getKey(int i) {
		return (String)keys.get(i);
	}
}
