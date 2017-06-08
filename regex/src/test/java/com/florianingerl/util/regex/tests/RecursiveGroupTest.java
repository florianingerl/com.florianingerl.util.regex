package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Test;

import com.florianingerl.util.regex.PatternSyntaxException;

public class RecursiveGroupTest {

	@Test(expected = PatternSyntaxException.class)
	public void test1() {
		Pattern.compile("(?notdefined)group is not defined!");
	}

	@Test(expected = PatternSyntaxException.class)
	public void test2() {
		Pattern.compile("(?<=(?laterdefined))something(?(DEFINE)(?<laterdefined>a(?laterdefined)?b))");
	}

}
