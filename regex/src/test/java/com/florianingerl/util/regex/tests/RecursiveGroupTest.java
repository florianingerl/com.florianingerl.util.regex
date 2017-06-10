package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;
import com.florianingerl.util.regex.PatternSyntaxException;

public class RecursiveGroupTest {

	@Test(expected = PatternSyntaxException.class)
	public void test1() {
		Pattern.compile("(?'notdefined')group is not defined!");
	}

	@Test(expected = PatternSyntaxException.class)
	public void test2() {
		Pattern.compile("(?<=(?'laterdefined'))something(?(DEFINE)(?<laterdefined>a(?'laterdefined')?b))");
	}

	@Test
	public void test3() {
		Pattern p = Pattern.compile("(?<first>[a-z])(?<second>\\k<first>)");
		Matcher m = p.matcher("bb");
		assertTrue(m.matches());
		assertFalse(p.matcher("ab").matches());

		p = Pattern.compile("(?(DEFINE)(?<second>\\k<first>))(?<first>[a-z])(?'second')");
		assertFalse(p.matcher("bb").matches());

		p = Pattern.compile("(?(DEFINE)(?<first>[a-z]))(?'first')(?<second>\\k<first>)");
		assertTrue(p.matcher("bb").matches());
		assertFalse(p.matcher("ab").matches());
	}

	@Test
	public void test4() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<first>(?<letter>[a-z])))(?'first')\\k<letter>");
		assertFalse(p.matcher("aa").matches());

		p = Pattern.compile("(?(DEFINE)(?<first>(?<letter>[a-z])))(?'first')\\k<first>");
		assertTrue(p.matcher("aa").matches());
	}

}
