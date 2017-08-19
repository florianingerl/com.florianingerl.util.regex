package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;

public class StackOverflowExceptionTest {
	
	@Test
	@Ignore
	public void testRepetitionOfGroupsJavaUtilRegex() {
		java.util.regex.Pattern p = java.util.regex.Pattern.compile("(\\\\.|[^\"])*");
		StringBuilder input = new StringBuilder();
		for(int i=0; i < 8000; ++i) {
			input.append("a");
		}
		assertTrue( p.matcher(input.toString()).matches() );
	}
	
	@Test
	@Ignore
	public void testRepetitionOfGroups() {
		Pattern p1 = Pattern.compile("(\\\\.|[^\"])*");
		java.util.regex.Pattern p2 = java.util.regex.Pattern.compile(p1.pattern());
		StringBuilder input = new StringBuilder();
		boolean failed = false;
		for(int i=0; i < 5000; ++i) {
			input.append("a");
			if(i <= 1000) continue;
			System.out.println(i);
			try {
				assertTrue( p1.matcher(input.toString()).matches() );
			} catch(StackOverflowError soe) {
				soe.printStackTrace();
				failed = true;
			}
			if(!failed) continue;
			
			if( p2.matcher(input.toString()).matches() )
				assertTrue("com.florianingerl.util.regex needs more stacks!",false);
			return;
		}
		
	}

}
