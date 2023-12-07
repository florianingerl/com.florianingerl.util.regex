package com.florianingerl.util.regex.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import org.junit.Test;

import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;

public class ToMatchResultTest {

	
	@Test
	public void testToMatchResult() {
		 Pattern pattern = Pattern.compile("a(\\w+)b");
		 Matcher matcher = pattern.matcher("ahellob");
		 assertTrue( matcher.find());
		 assertEquals("hello", matcher.toMatchResult().group(1));
	}
}
