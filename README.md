![com.github.florianingerl.util.regex](media/logo.png)

### Introduction
This is a Java Regular Expressions library. Compared to the Regular Expression library shipped with the Java JDK, it provides support for Recursive and Conditional Regular Expressions, adopts the concept of Captures from .Net and allows the user to install plugins into the regex engine.

In the following screenshot, all the new features are summarized.
![com.github.florianingerl.util.regex.newfeatures](media/newfeatures.png)


### What's new :star:

### Version 1.1.1

### Version 1.0.3
- (?(DEFINE)never-executed-pattern)
- Plugins into the regex engine

### Version 1.0.2
- Recursive Regular Expressions
- Conditional Regular Expressions
- Captures

### Usage
The API is exactly the same as in java.util.regex. The only difference is that the required import statement is import com.florianingerl.util.regex.\*; instead of import java.util.regex.\*;

To illustrate the functionality, we will use the following utility functions
```
import com.florianingerl.util.regex.*;
...
private static void check(String p, String s, boolean expected) 
{
	Matcher matcher = Pattern.compile(p).matcher(s);
	if (matcher.find() != expected)
		failCount++;
}
static void check(String regex, String input, String[] expected) 
{
	List<String> result = new ArrayList<String>();
	Pattern p = Pattern.compile(regex);
	Matcher m = p.matcher(input);
	while (m.find()) {
		result.add(m.group());
	}
	if (!Arrays.asList(expected).equals(result))
		failCount++;
}
```
The following tests illustrate what you can do with Recursive Regular Expressions. Be aware that the syntax (?R) or (?0) as in Perl is not supported, only (?n) where n is greater than 0 or (?'groupName') is supported.
```
String pattern = "1(jT(\\<((?1)(,|(?=\\>)))+\\>)?)2";
check(pattern, "1jT2", true);
check(pattern, "1jT<jT>2", true);
check(pattern, "1jT<jT,jT>2", true);
check(pattern, "1jT<jT<jT>>2", true);
check(pattern, "1jT<jT<jT>,jT<jT,jT>>2", true);

check("(\\(([^()]+|(?1))*+\\))", "(go away (here (everything) is fine) afterwards",
				new String[] { "(here (everything) is fine)" });
```

Since version 1.0.3, you can install plugins into the regex engine. The method of the Pattern class seen in the screenshot below is used for that purpose.
![com.florianingerl.util.regex.plugins](media/plugins.png)

Good examples of what possibilites plugins offer you, are given in the PluginTest class, see [PluginTest.java](regex/src/test/java/com/florianingerl/util/regex/tests/PluginTest.java). You might also want to read the [JavaDoc](https://florianingerl.github.io/com.florianingerl.util.regex/).


### Maven Dependency
In order to use this library, add the following dependency to your pom.xml.
```
<dependency>
	<groupId>com.github.florianingerl.util</groupId>
	<artifactId>regex</artifactId>
	<version>1.1.1</version>
</dependency>
```

### Known Issues
Unfortunately this library needs more stacks than java.util.regex which can lead to a StackOverflowException more quickly in rare cases.
E.g. suppose you wanted to match Java strings with the regex 
```
"(\\.|[^"])*"
```
then this would only work for string lengths up to 3890, whereas with java.util.regex it would work with string lengths up to 6930. The problem is the
*-repetition that has to keep track of group captures and the backtracking options of the alternation. However a simple character class can be repeated nearly an unlimited number of times,
so the regex above could be improved to
```
"(?:\\.|[^"\\]+)*"
```
