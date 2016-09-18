package com.ingerlflori.util.regex;

import static org.junit.Assert.*;

import java.util.Stack;
import java.util.Vector;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class LearningTests {

	private static int failCount;

	@Before
	public void beforeEachTest() {
		failCount = 0;
	}

	@Test
	public void test() {
		Pattern p = Pattern.compile("1(\\s\\S+?){1,3}?[\\s,]2");
		check(p, "1 wor wo 2", true);

		assertEquals(0, failCount);
	}

	@Test
	public void test2() {
		Pattern p = Pattern.compile("([a-f])+ef");
		Matcher matcher = p.matcher("abcdef");

		assertTrue(matcher.find());

		Stack<Capture> captures = matcher.captures(1);
		assertEquals(4, captures.size());

		for (int i = 0; i < captures.size(); ++i) {
			assertEquals(new String(new byte[] { (byte) ('a' + i) }), captures.get(i).getValue());
		}
	}

	@Ignore
	@Test
	public void test3() {
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("1((r)++)?rrr2");
		java.util.regex.Matcher matcher = pattern.matcher("1rrr2");

		assertTrue(matcher.find());
		assertEquals(-1, matcher.start(2));
		assertNull(matcher.group(2));

	}

	@Ignore
	@Test
	public void test4() {
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("1((?>(r)+))?rrr2");
		java.util.regex.Matcher matcher = pattern.matcher("1rrr2");

		assertTrue(matcher.find());
		assertEquals(-1, matcher.start(2));
		assertNull(matcher.group(2));
		assertEquals(-1, matcher.start(3));
		assertNull(matcher.group(3));

	}

	@Test
	public void test5() {
		Stack<Capture> captures = new Stack<Capture>();
		captures.push(new Capture("abc", 0, 1));
		captures.push(new Capture("abc", 1, 2));
		captures.push(new Capture("abc", 2, 3));

		Stack<Capture> copy = (Stack<Capture>) captures.clone();
		assertEquals(3, copy.size());
		Capture capture = copy.pop();
		assertEquals("c", capture.getValue());
		capture = copy.pop();
		assertEquals("b", capture.getValue());
		capture = copy.pop();
		assertEquals("a", capture.getValue());
	}

	@Test
	public void test6() {
		Vector<Stack<Capture>> vecCaptures = new Vector<Stack<Capture>>(10);
		vecCaptures.setSize(10);

		Stack<Capture> captures = new Stack<Capture>();
		captures.push(new Capture("abc", 0, 1));
		captures.push(new Capture("abc", 1, 2));
		captures.push(new Capture("abc", 2, 3));

		vecCaptures.set(2, captures);

		Vector<Stack<Capture>> copy = (Vector<Stack<Capture>>) vecCaptures.clone();
		assertEquals(10, copy.size());
		for (int i = 0; i < 10; ++i) {
			if (i != 2) {
				assertNull(copy.get(i));
				continue;
			}

			Stack<Capture> capt = copy.get(i);
			assertEquals(3, capt.size());
			Capture capture = capt.pop();
			assertEquals("c", capture.getValue());
			capture = capt.pop();
			assertEquals("b", capture.getValue());
			capture = capt.pop();
			assertEquals("a", capture.getValue());
		}
	}

	@Test
	public void test7() {
		Pattern pattern = Pattern.compile("1((?>(r)+))?r2");
		Matcher matcher = pattern.matcher("1r2");

		assertTrue(matcher.find());
		assertEquals("1r2", matcher.group());
		assertEquals(-1, matcher.start(1));
		assertNull(matcher.group(1));
		assertEquals(0, matcher.captures(1).size());
		assertEquals(0, matcher.captures(2).size());
		assertNull(matcher.group(2));
		assertEquals(-1, matcher.start(2));

	}

	private static void check(Pattern p, String s, boolean expected) {
		if (p.matcher(s).find() != expected)
			failCount++;
	}

}
