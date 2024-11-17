# Template Engine Library

A lightweight and flexible **Java** template engine that supports value placeholders and list formatting with customizable escaping strategies.

## Features

- Simple placeholder syntax for values and lists
- Multiple list formatting options (`INLINE`, `BULLETED`, `ORDERED`, `UNORDERED`)
- Customizable string escaping
- Support for loading templates from strings, files, and resources
- Thread-safe placeholder value handling
- Fluent builder **API**

## Usage

### Basic Example

```java
String template = "Hello {{name}}! Your favorite colors are: [[colors::BULLETED]]";

String result = TemplateBuilder.forString(template)
    .value("name", "Grzegorz")
    .list("colors", Arrays.asList("Red", "Blue", "Green"))
    .build();
```

Output:

```
Hello Grzegorz! Your favorite colors are:
• Red
• Blue
• Green
```

### Loading Templates

```java
// From a file
TemplateBuilder builder = TemplateBuilder.forFile("templates/greeting.txt");

// From a resource
TemplateBuilder builder = TemplateBuilder.forResource("resources/templates/email.txt");
```

### Placeholder Syntax

- Value placeholders: `{{placeholder}}`
- List placeholders: `[[listName::formatter]]`

### List Formatting Options

- `INLINE`: comma-separated values

```
item1, item2, item3
```

- `BULLETED`: bullet points (•)

```
• item1
• item2
• item3
```

- `ORDERED`: numbered list (1. 2. 3. ...)

```
1. item1
2. item2
3. item3
```

- `UNORDERED`: dashed list (-)

```
- item1
- item2
- item3
```

### Custom String Escaping

Implement the `TemplateStringEscaper` interface to create custom escaping strategies:

```java
public class HtmlEscaper implements TemplateStringEscaper {
    @Override
    public String escape(String string) {
        return string.replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("&", "&amp;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}
```

Using the `HtmlEscaper`:

```java
TemplateBuilder.forString(template)
    .escaper(new HtmlEscaper())
    .value("content", "<script>alert('Hello')</script>")
    .build();
```

### Formatted Values

```java
TemplateBuilder.forString("Balance: {{amount}}")
    .value("amount", "%.2f USD", 42.5)
    .build();
// Output: Balance: 42.50 USD
```

## Thread Safety

The `TemplateBuilder` uses `ConcurrentHashMap` for storing values and lists, making it safe to use in multi-threaded environments.

## Error Handling

The `TemplateEngineException` is thrown in the following cases:
- Unclosed placeholders
- Missing placeholder values
- Invalid list formatter names
- File or resource loading errors