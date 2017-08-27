package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;
import com.florianingerl.util.regex.PatternSyntaxException;

public class BackReferenceTest {

	@Test(expected = PatternSyntaxException.class)
	public void backReferenceToNonExistentGroupShouldCausePatternSyntaxException() {
		Pattern.compile("\\2");
	}

	@Test(expected = PatternSyntaxException.class)
	public void backReferenceToNonExistentNamedGroupShouldCausePatternSyntaxException() {
		Pattern.compile("\\k<letter>");
	}

	@Test
	public void backReferenceToGroupThatsDefinedLaterInTheRegexShouldWork() {
		//Case sensitive
		Pattern p = Pattern.compile("(?'letter')\\k<letter>(?(DEFINE)(?<letter>[a-z]))");
		Matcher m = p.matcher("bb");
		assertTrue(m.matches());

		m = p.matcher("ab");
		assertFalse(m.matches());

		p = Pattern.compile("(?1)\\1(?(DEFINE)([a-z]))");
		m = p.matcher("bb");
		assertTrue(m.matches());

		m = p.matcher("ab");
		assertFalse(m.matches());
		//Case insensitive
		p = Pattern.compile("(?'letter')\\k<letter>(?(DEFINE)(?<letter>[a-z]))", Pattern.CASE_INSENSITIVE);
		m = p.matcher("bB");
		assertTrue(m.matches());
		
		m = p.matcher("Bb");
		assertTrue(m.matches());
		
		m = p.matcher("ab");
		assertFalse(m.matches());
		
		p = Pattern.compile("(?1)\\1(?(DEFINE)([a-z]))", Pattern.CASE_INSENSITIVE);
		m = p.matcher("bB");
		assertTrue(m.matches());
		
		m = p.matcher("Bb");
		assertTrue(m.matches());

		m = p.matcher("ab");
		assertFalse(m.matches());
		
		p = Pattern.compile("(?<letter>[a-z])\\k<letter>", Pattern.CASE_INSENSITIVE);
		assertTrue(p.matcher("bB").matches() );
		assertTrue(p.matcher("Bb").matches() );
		assertFalse(p.matcher("ab").matches() );
	}
	


}
