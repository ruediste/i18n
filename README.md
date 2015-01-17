
# i18n

This project provides utility classes for internationalization especially useful if you are using Domain Driven Design. The main idea is to associate all localized strings with some java element and use references to the java elements to retrieve the strings. More specifically, a label can be bound to each

* Type
* Property
* Enum Member

In addition, message patterns are bound to message interfaces to allow parameterized string generation.

Care has been taken to allow for static extraction of all label keys, which allows to check if the keys present in some resource files match the keys used by the application.

In addition, the fallback translation can be specified directly in code via annotations. This allows the quick addition of labels and serves documentation purposes.

## Translated Strings
Translated strings are are represented by **LString**s. Their only capability is to resolve themselves against a **Locale**, resulting in a string representation.

### Labeling Classes, Properties, Enums and Enum members
To be labeled, a class or enum has to be annotated with the **Labeled** annotation. To label Properties or enum members, the containing type has to be annotated with **PropertiesLabeled** or **MembersLabeled**, respectively. It is not possible to select the properties or enum members to be labeled individually. 

By default, the fallback is generated from the name in the source code. The names are interpreted in upper camel case for class and enum names, lower camel case for property names and upper underscore case for enum members. They are converted to upper case, separated with spaces. The generated fallback can be overridden using the **Label** annotation. 

Multiple variants of a label can be specified, by repeating the **Label** annotation, specifying the optional **variant** attribute. For properties, the getter, setter or the backing field can be annotated, but only one of the three per property. For frequently used variants, an annotation can be defined using the **LabelVariant** meta-annotation. The annotation has to have exactly the **value** attribute of string type.

To specify a variant of a label, the variant has to be included in the **variants** attribute of the respective **PropertiesLabeled** or
**MembersLabeled** annotation. For classes or enums, the allowed variants are specified implicitely by the present **Label** annotations and additionally by the **variants** attribute of the **Labeled** annotation.

In class hierarchies, the first class to define a property defines the label. Derived classes can not change the label and may not use the label annotation. They cannot label an inherited unlabeled property.

During initialization, a resource bundle can be specified. When looking up a label, a locale has to be specified. A key is derived from the fully qualified class or enum name, the property, method or enum member name and the variant and looked up in the resource bundle. If no resource can be found, the label as defined by the label annotation is used if available, otherwise the default label. In class hierarchies, the defining class of a property is used to generate the key.

### Message Interfaces
Messages can be accessed by creating an interface annotated with **TMessages**. Each interface method has to return a **PString** and can have multiple parameters. Methods may not override each other. The interface may not inherit from other interfaces.

An interface implementation can be generated which constructs the **PString** result using the fully qualified interface name with the method name as key. The parameter values are copied into the result.

The default message can be specified using the **TMessage** annotation. Otherwise it is generated from the method name. It is interpreted as lower camel. The first character is converted to upper case, the words are separated with spaces and the message is terminated with a period.

## Workflow
A maven plugin is used to generate a properties file containing the default translation of all labels and message interfaces in all allowed variants. Using a resource bundle editor, the different languages can be synchronized (for example using the [Eclipse ResourceBundle Editor](http://essiembre.github.io/eclipse-rbe)). 

When performing a move or rename refactoring, the "Update fully qualified names in non-java text files" option should always be selected. This automatically keeps the properties files in sync with the code.

When sending properties files to translators, only commited versions should be used. The file should be renamed to include the commit hash. When file is returned, the original original commit should be checked out and the file replaced. Then the changes can be merged/rebased into the development branch. This results in a good merge tooling.## Message Format
The **MessageFormat** is a replacement for the class of the same name from the standard library with the following features

* familiar syntax
* new format types can be defined
* existing format types can be replaced
* use a PEG and a parser library to keep parsing code manageable
* support for the plural concept from ICU

See javadoc for more information.

