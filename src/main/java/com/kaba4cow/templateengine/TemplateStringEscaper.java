package com.kaba4cow.templateengine;

/**
 * Interface for escaping template strings.
 */
public interface TemplateStringEscaper {

	/**
	 * Escapes the specified string.
	 * 
	 * @param string the string to escape
	 * 
	 * @return the escaped string
	 */
	public String escape(String string);

}
