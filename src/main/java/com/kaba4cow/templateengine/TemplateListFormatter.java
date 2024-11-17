package com.kaba4cow.templateengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Enumeration for formatting collections in templates.
 */
public enum TemplateListFormatter {

	INLINE {

		@Override
		public void format(StringBuilder builder, List<String> list) {
			for (int i = 0; i < list.size(); i++) {
				if (i > 0)
					builder.append(", ");
				builder.append(list.get(i));
			}
		}

	},
	BULLETED {

		@Override
		public void format(StringBuilder builder, List<String> list) {
			for (int i = 0; i < list.size(); i++) {
				if (i > 0)
					builder.append("\n");
				builder.append("• ");
				builder.append(list.get(i));
			}
		}

	},
	ORDERED {

		@Override
		public void format(StringBuilder builder, List<String> list) {
			for (int i = 0; i < list.size(); i++) {
				if (i > 0)
					builder.append("\n");
				builder.append(String.format("%s. ", i + 1));
				builder.append(list.get(i));
			}
		}

	},
	UNORDERED {

		@Override
		public void format(StringBuilder builder, List<String> list) {
			for (int i = 0; i < list.size(); i++) {
				if (i > 0)
					builder.append("\n");
				builder.append("- ");
				builder.append(list.get(i));
			}
		}

	};

	/**
	 * Returns a {@code TemplateListFormatter} based on its name.
	 * 
	 * @param name the name of the formatter
	 * 
	 * @return the corresponding {@code TemplateListFormatter}
	 * 
	 * @throws TemplateEngineException if no formatter with the specified name exists
	 */
	public static TemplateListFormatter forName(String name) {
		Objects.requireNonNull(name);
		for (TemplateListFormatter value : values())
			if (Objects.equals(value.toString().toLowerCase(), name.toLowerCase()))
				return value;
		throw new TemplateEngineException("List formatter with name \"%s\" does not exist", name);
	}

	protected abstract void format(StringBuilder builder, List<String> list);

	/**
	 * Formats the collection based on the formatter type.
	 * 
	 * @param list the collection to format
	 * @param <T>  the type of elements in the collection
	 * 
	 * @return the formatted string
	 */
	public <T> String format(Collection<T> list) {
		StringBuilder builder = new StringBuilder();
		List<String> strings = new ArrayList<>();
		for (T element : list)
			strings.add(Objects.toString(element));
		format(builder, strings);
		return builder.toString();
	}

}