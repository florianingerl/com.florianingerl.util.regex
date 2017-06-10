package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;

public class ConditionalTest {

	@Test
	public void test() {
		Pattern p = Pattern.compile("(?:(?<open>\\{)|\\[)(?<close>(?(open)\\}|\\]))");
		assertTrue(p.matcher("{}").matches());
		assertTrue(p.matcher("[]").matches());
		assertFalse(p.matcher("{]").matches());
	}

	@Test
	public void test2() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<close>(?(open)\\}|\\])))(?:(?<open>\\{)|\\[)(?'close')");
		assertTrue(p.matcher("{]").matches());
		assertTrue(p.matcher("[]").matches());
		assertFalse(p.matcher("{}").matches());
		assertFalse(p.matcher("[}").matches());
	}

	@Test
	public void test3() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<open>\\{))(?:(?'open')|\\[)(?<close>(?(open)\\}|\\]))");
		assertTrue(p.matcher("{}").matches());
		assertTrue(p.matcher("[]").matches());
		assertFalse(p.matcher("{]").matches());
	}

	@Test
	public void test4() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<first>(?<letter>[a-z])))(?'first')(?(letter)|(?!))");
		assertFalse(p.matcher("a").matches());

		p = Pattern.compile("(?(DEFINE)(?<first>(?<letter>[a-z])))(?'first')(?(first)|(?!))");
		assertTrue(p.matcher("a").matches());
	}

	@Test
	public void test5() {
		Pattern p = Pattern.compile("(\\()?[^()]+(?(1)\\))", 0 | Pattern.MULTILINE | Pattern.DOTALL);
		Matcher m = p.matcher("the quick (abcd) fox");
		assertTrue(m.find());
		System.out.println(m.groupTree().print());
		assertEquals("the quick ", m.group());
	}

}
