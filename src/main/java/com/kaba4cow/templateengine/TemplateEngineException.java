package com.kaba4cow.templateengine;

/**
 * Exception thrown when there is an error in the template engine.
 */
public class TemplateEngineException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	TemplateEngineException(String format, Object... args) {
		super(String.format(format, args));
	}

}
