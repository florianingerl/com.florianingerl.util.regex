package com.ingerlflori.util.regex;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConditionalTest {

	@Test
	public void conditional_ifandelseandgroupreferencedbyitsnumber_works() {

		Pattern pattern = Pattern.compile("(a)?bbb(?(1)yy|zz)st");

		Matcher matcher = pattern
				.matcher("First match: abbbyyst Not the whole second match: abbbzzst Not the third match: bbbyyst");

		assertTrue(matcher.find());
		assertEquals("abbbyyst", matcher.group());

		assertTrue(matcher.find());
		assertEquals("bbbzzst", matcher.group());

		assertFalse(matcher.find());
	}

	@Test
	public void conditional_onlyifandgroupreferencedbyitsnumber_works() {
		Pattern pattern = Pattern.compile("(a)?bbb(?(1)yy)st");

		Matcher matcher = pattern.matcher(
				"First match: abbbyyst Second match: bbbst Not the third match: abbbst Also not the third match: bbbyyst");

		assertTrue(matcher.find());
		assertEquals("abbbyyst", matcher.group());

		assertTrue(matcher.find());
		assertEquals("bbbst", matcher.group());

		assertTrue(matcher.find());
		assertEquals("bbbst", matcher.group());

		assertFalse(matcher.find());
	}

	@Test
	public void conditional_ifandelseandgroupreferencedbyitsname_works() {
		Pattern pattern = Pattern.compile("(?<firstGroup>a)?bbb(?(firstGroup)yy|zz)st");

		Matcher matcher = pattern.matcher(
				"First match: abbbyyst Second match: bbbzzst Not the third match: abbbzzst Also not the third match: bbbyyst");

		assertTrue(matcher.find());
		assertEquals("abbbyyst", matcher.group());

		assertTrue(matcher.find());
		assertEquals("bbbzzst", matcher.group());

		assertTrue(matcher.find());
		assertEquals("bbbzzst", matcher.group());

		assertFalse(matcher.find());
	}

	@Test
	public void conditional_onlyifandgroupreferencedbyitsname_works() {
		Pattern pattern = Pattern.compile("(?<firstGroup>a)?bbb(?(firstGroup)yy)st");

		Matcher matcher = pattern.matcher(
				"First match: abbbyyst Second match: bbbst Not the third match: abbbst Also not the third match: bbbyyst");

		assertTrue(matcher.find());
		assertEquals("abbbyyst", matcher.group());

		assertTrue(matcher.find());
		assertEquals("bbbst", matcher.group());

		assertTrue(matcher.find());
		assertEquals("bbbst", matcher.group());

		assertFalse(matcher.find());
	}

}
