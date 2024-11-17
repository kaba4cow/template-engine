package com.kaba4cow.templateengine;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A utility class for building string templates with placeholders.
 * <p>
 * Supports value and list placeholders with customizable formatting and escape strategies.
 * <p>
 * Value placeholders: <code>{{placeholder}}</code> <br>
 * List placeholders: <code>[[listPlaceholder::formatter]]</code>
 * </p>
 * 
 * @see TemplateStringEscaper
 * @see TemplateListFormatter
 */
public class TemplateBuilder {

	private static final String VALUE_DELIMITER_OPEN = "{{";
	private static final String VALUE_DELIMITER_CLOSE = "}}";
	private static final String LIST_DELIMITER_OPEN = "[[";
	private static final String LIST_DELIMITER_CLOSE = "]]";
	private static final String LIST_DELIMITER_FORMAT = "::";

	private final String template;
	private final Map<String, Object> values;
	private final Map<String, Collection<?>> lists;
	private TemplateStringEscaper escaper;

	private TemplateBuilder(String template) {
		this.template = Objects.requireNonNull(template);
		this.values = new ConcurrentHashMap<>();
		this.lists = new ConcurrentHashMap<>();
		this.escaper = new DefaultTemplateStringEscaper();
	}

	/**
	 * Creates a new {@code TemplateBuilder} for the specified template string.
	 * 
	 * @param template the template string containing placeholders
	 * 
	 * @return a new instance of {@code TemplateBuilder}
	 * 
	 * @throws NullPointerException if {@code template} is {@code null}
	 */
	public static TemplateBuilder forString(String template) {
		return new TemplateBuilder(template);
	}

	/**
	 * Creates a new {@code TemplateBuilder} by reading a template from a file.
	 * 
	 * @param filePath the path to the file containing the template
	 * 
	 * @return a new instance of {@code TemplateBuilder}
	 * 
	 * @throws NullPointerException if {@code filePath} is {@code null}
	 * @throws RuntimeException     if the file cannot be read
	 */
	public static TemplateBuilder forFile(String filePath) {
		try {
			Objects.requireNonNull(filePath);
			InputStream input = new FileInputStream(filePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String string = reader.lines().collect(Collectors.joining("\n"));
			reader.close();
			return new TemplateBuilder(string);
		} catch (IOException exception) {
			throw new RuntimeException(String.format("Could not load template from file %s", filePath), exception);
		}
	}

	/**
	 * Creates a new {@code TemplateBuilder} by reading a template from a resource.
	 * 
	 * @param resourceName the resource name containing the template
	 * 
	 * @return a new instance of {@code TemplateBuilder}
	 * 
	 * @throws NullPointerException if {@code resourceName} is {@code null}
	 * @throws RuntimeException     if the resource cannot be read
	 */
	public static TemplateBuilder forResource(String resourceName) {
		try {
			Objects.requireNonNull(resourceName);
			InputStream input = TemplateBuilder.class.getClassLoader().getResourceAsStream(resourceName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String string = reader.lines().collect(Collectors.joining("\n"));
			reader.close();
			return new TemplateBuilder(string);
		} catch (IOException exception) {
			throw new RuntimeException(String.format("Could not load template from resource %s", resourceName), exception);
		}
	}

	/**
	 * Sets a value for a placeholder in the template.
	 * 
	 * @param placeholder the name of the placeholder
	 * @param value       the value to replace the placeholder with
	 * 
	 * @return the current {@code TemplateBuilder} instance
	 * 
	 * @throws NullPointerException if {@code placeholder} is {@code null}
	 */
	public TemplateBuilder value(String placeholder, Object value) {
		this.values.put(Objects.requireNonNull(placeholder), value);
		return this;
	}

	/**
	 * Sets a formatted value for a placeholder.
	 * 
	 * @param placeholder the name of the placeholder
	 * @param format      a format string
	 * @param args        arguments referenced by the format string
	 * 
	 * @return the current {@code TemplateBuilder} instance
	 * 
	 * @throws NullPointerException if {@code placeholder} or {@code format} is {@code null}
	 */
	public TemplateBuilder value(String placeholder, String format, Object... args) {
		return value(placeholder, String.format(Objects.requireNonNull(format), args));
	}

	/**
	 * Sets a list for a list placeholder in the template.
	 * 
	 * @param placeholder the name of the list placeholder
	 * @param list        the collection to replace the list placeholder with
	 * 
	 * @return the current {@code TemplateBuilder} instance
	 * 
	 * @throws NullPointerException if {@code placeholder} or {@code list} is {@code null}
	 */
	public TemplateBuilder list(String placeholder, Collection<?> list) {
		this.lists.put(Objects.requireNonNull(placeholder), Objects.requireNonNull(list));
		return this;
	}

	/**
	 * Sets the escaping strategy for placeholder values.
	 * 
	 * @param escaper the {@code TemplateStringEscaper} to use
	 * 
	 * @return the current {@code TemplateBuilder} instance
	 * 
	 * @throws NullPointerException if {@code escaper} is {@code null}
	 */
	public TemplateBuilder escaper(TemplateStringEscaper escaper) {
		this.escaper = Objects.requireNonNull(escaper);
		return this;
	}

	/**
	 * Gets the current escaping strategy.
	 * 
	 * @return the current {@code TemplateStringEscaper}
	 */
	public TemplateStringEscaper escaper() {
		return escaper;
	}

	/**
	 * Renders the template by replacing all placeholders with their corresponding values.
	 * 
	 * @return the rendered template as a {@code String}
	 * 
	 * @throws TemplateEngineException if a placeholder is not properly closed or not provided
	 */
	public String build() {
		String template = this.template;
		StringBuilder result = new StringBuilder();
		int startIndex = 0;
		while (startIndex < template.length()) {
			int openIndex = template.indexOf(VALUE_DELIMITER_OPEN, startIndex);
			if (openIndex == -1) {
				result.append(template.substring(startIndex));
				break;
			}
			result.append(template.substring(startIndex, openIndex));
			int closeIndex = template.indexOf(VALUE_DELIMITER_CLOSE, openIndex + VALUE_DELIMITER_OPEN.length());
			if (closeIndex == -1)
				throw new TemplateEngineException("Unclosed value placeholder");
			String placeholder = template.substring(openIndex + VALUE_DELIMITER_OPEN.length(), closeIndex);
			if (!values.containsKey(placeholder))
				throw new TemplateEngineException("Value %s not provided", placeholder);
			result.append(escaper.escape(Objects.toString(values.get(placeholder))));
			startIndex = closeIndex + VALUE_DELIMITER_CLOSE.length();
		}
		template = result.toString();
		result.setLength(0);
		startIndex = 0;
		while (startIndex < template.length()) {
			int openIndex = template.indexOf(LIST_DELIMITER_OPEN, startIndex);
			if (openIndex == -1) {
				result.append(template.substring(startIndex));
				break;
			}
			result.append(template.substring(startIndex, openIndex));
			int closeIndex = template.indexOf(LIST_DELIMITER_CLOSE, openIndex + LIST_DELIMITER_OPEN.length());
			if (closeIndex == -1)
				throw new TemplateEngineException("Unclosed list placeholder");
			String placeholder = template.substring(openIndex + LIST_DELIMITER_OPEN.length(), closeIndex);
			String placeholderName = placeholder.split(LIST_DELIMITER_FORMAT)[0];
			String placeholderFormat = placeholder.split(LIST_DELIMITER_FORMAT)[1];
			if (!lists.containsKey(placeholderName))
				throw new TemplateEngineException("List %s not provided", placeholderName);
			TemplateListFormatter formatter = TemplateListFormatter.forName(placeholderFormat);
			result.append(escaper.escape(formatter.format(lists.get(placeholderName))));
			startIndex = closeIndex + LIST_DELIMITER_CLOSE.length();
		}
		return result.toString();
	}

	/**
	 * Returns a string representation of this {@code TemplateBuilder}.
	 * 
	 * @return a string representation of the current state of the template
	 */
	@Override
	public String toString() {
		return String.format("TemplateBuilder [template=%s, values=%s, lists=%s]", template, values, lists);
	}

}
