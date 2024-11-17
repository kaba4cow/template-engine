package com.kaba4cow.templateengine;

/**
 * Default implementation of {@code TemplateStringEscaper} that performs no escaping.
 */
public class DefaultTemplateStringEscaper implements TemplateStringEscaper {

	public DefaultTemplateStringEscaper() {}

	@Override
	public String escape(String string) {
		return string;
	}

}
