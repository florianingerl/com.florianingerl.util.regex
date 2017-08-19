package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;
import com.florianingerl.util.regex.PatternSyntaxException;

public class RecursiveGroupTest {

	@Test(expected = PatternSyntaxException.class)
	public void recursionToNamedGroupThatsNotDefinedShouldCausePatternSyntaxException() {
		Pattern.compile("(?'notdefined')group is not defined");
	}

	@Test(expected = PatternSyntaxException.class)
	public void recursionToGroupThatsNotDefinedShouldCausePatternSyntaxException() {
		Pattern.compile("(?1)group is not defined");
	}

	@Test
	public void recursionToNamedGroupWithoutAMaximumLengthInsideLookbehindShouldCausePatternSyntaxException() {
		try {
			Pattern.compile("(?<=(?'laterdefined'))something(?(DEFINE)(?<laterdefined>a(?'laterdefined')?b))");
		} catch (PatternSyntaxException pse) {
			assertTrue(pse.getMessage().contains("obvious maximum length"));
			return;
		}
		assertTrue(false);
	}

	@Test
	public void recursionToGroupWithoutAMaximumLengthInsideLookbehindShouldCausePatternSyntaxException() {
		try {
			Pattern.compile("(?<=(?1))something(?(DEFINE)(a(?1)?b))");
		} catch (PatternSyntaxException pse) {
			assertTrue(pse.getMessage().contains("obvious maximum length"));
			return;
		}
		assertTrue(false);
	}

	@Test
	public void whenEnteringRecursionAllBackreferencesShouldInitiallyFail() {
		Pattern p = Pattern.compile("(?<first>[a-z])(?<second>\\k<first>)");
		assertTrue(p.matcher("bb").matches());
		assertFalse(p.matcher("ab").matches());

		p = Pattern.compile("(?(DEFINE)(?<second>\\k<first>))(?<first>[a-z])(?'second')");
		assertFalse(p.matcher("bb").matches());
		assertFalse(p.matcher("ab").matches());
	}

	@Test
	public void whenLeavingRecursionACaptureShouldBeMadeForTheGroupThatWasRecursedTo() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<first>[a-z]))(?'first')(?<second>\\k<first>)");
		assertTrue(p.matcher("bb").matches());
		assertFalse(p.matcher("ab").matches());

		p = Pattern.compile("(?(DEFINE)(?<first>(?<letter>[a-z])))(?'first')\\k<first>");
		assertTrue(p.matcher("aa").matches());
	}

	@Test
	public void whenLeavingRecursionAllCapturesMadeInsideTheRecursionShouldAffectBackreferencesOutside() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<first>(?<letter>[a-z])))(?'first')\\k<letter>");
		assertFalse(p.matcher("aa").matches());
	}

	@Test
	public void anagramsShouldBeMatched() {
		Pattern p = Pattern.compile(
				"(?(DEFINE)(?<letter>[a-zA-Z]))\\b(?<anagram>(?'letter')(?'anagram')?\\k<letter>|(?'letter'))\\b");
		assertTrue(p.matcher("radar").matches());
		assertTrue(p.matcher("aa").matches());

		p = Pattern.compile(
				"(?(DEFINE)(?<wrapper>(?<letter>[a-zA-Z])))\\b(?<anagram>(?'wrapper')(?'anagram')?\\k<letter>|(?'letter'))\\b");
		assertFalse(p.matcher("aa").matches());
		assertFalse(p.matcher("radar").matches());
	}

}
