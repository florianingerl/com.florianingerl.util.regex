package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.florianingerl.util.regex.Pattern;
import com.florianingerl.util.regex.PatternSyntaxException;

public class LookbehindTest {

	@Test
	public void recursionWithMaximumLengthInsideLookbehindShouldWork() {
		Pattern.compile("(?<=(?'digits'))(?(DEFINE)(?<digits>\\d))");
	}

	@Test(expected = PatternSyntaxException.class)
	public void recursionWithoutMaximumLengthInsideLookbehindShouldntWork() {
		Pattern.compile("(?<=(?'digits'))(?(DEFINE)(?<digits>a\\d+))");
	}

}
