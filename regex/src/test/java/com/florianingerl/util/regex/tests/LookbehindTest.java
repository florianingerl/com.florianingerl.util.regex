package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.florianingerl.util.regex.Pattern;
import com.florianingerl.util.regex.PatternSyntaxException;

public class LookbehindTest {

	@Test
	public void test() {
		Pattern.compile("(?<=(?'digits'))(?(DEFINE)(?<digits>\\d))");
	}

	@Test(expected = PatternSyntaxException.class)
	public void test3() {
		Pattern.compile("(?<=(?'digits'))(?(DEFINE)(?<digits>a\\d+))");
	}

}
