package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import com.florianingerl.util.regex.PatternSyntaxException;

import org.junit.Test;

import com.florianingerl.util.regex.Pattern;

public class PatternSyntaxExceptionTest {

	@Test
	public void test() {
		check("+", "Dangling meta character '+'", 0);
		check("?", "Dangling meta character '?'", 0);
		check("*", "Dangling meta character '*'", 0);
		check("Amen\\i", "Illegal/unsupported escape sequence", 5);
		check("(?<A<a)", "Named capturing group is missing trailing '>'", 4);
		check("(?<\\d)", "Unknown look-behind group", 3);
		check("(?3)", "Recursion to non-existent capturing group 3", 2);
		check("\\2", "Backreference to non-existent capturing group 2", 1);
		check("\\k<hello>","Backreference to non-existent named capturing group 'hello'", 8);
		check("(?'hello')", "Recursion to non-existent named capturing group 'hello'", 8);
		check("\\k{hello}","\\k is not followed by '<' for named capturing group", 2);
	}
	
	private static void check(String pattern, String description, int index) {
		try {
			Pattern.compile(pattern);
		} catch(PatternSyntaxException pse) {
			System.out.println(pse.getMessage() );
			assertEquals(description, pse.getDescription() );
			assertEquals(index, pse.getIndex() );
			
			return;
		}
		assertTrue(false);
	}

}
