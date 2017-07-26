package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;

public class StackOverflowExceptionTest {

	@Test
	public void testRepetitionOfGroups() {
		Pattern p = Pattern.compile("\"(\\\\.|[^\"])*\"");
		StringBuilder input = new StringBuilder("\"");
		for(int i=0; i < 1; ++i) {
			for(int j=0; j < 1300; ++j) {
				input.append("a");
			}
			input.append("\\\"");
		}
		input.append("\"");
		assertTrue( p.matcher(input.toString()).matches() );
		
	}
	
	@Test
	public void testRepetitionOfGroupsJavaUtilRegex() {
		java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"(\\\\.|[^\"])*\"");
		StringBuilder input = new StringBuilder("\"");
		for(int i=0; i < 1; ++i) {
			for(int j=0; j < 1300; ++j) {
				input.append("a");
			}
			input.append("\\\"");
		}
		input.append("\"");
		assertTrue( p.matcher(input.toString()).matches() );
		
	}

}
