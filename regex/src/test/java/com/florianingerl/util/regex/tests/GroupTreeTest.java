package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.florianingerl.util.regex.CaptureTree;
import com.florianingerl.util.regex.CaptureTreeNode;
import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;

public class GroupTreeTest {

	@Test
	public void test() {
		Pattern p = Pattern.compile("(?<sum>(?<number>\\d+)\\+(?:(?'sum')|(?'number')))");
		Matcher s = p.matcher("5+8");
		assertTrue(s.matches());

		CaptureTree gt = s.captureTree();
		assertEquals("0\n\tsum\n\t\tnumber\n\t\tnumber\n", gt.toString());

		s = p.matcher("5+6+7");
		assertTrue(s.matches());

		gt = s.captureTree();
		System.out.println(gt);
		assertEquals("0\n\tsum\n\t\tnumber\n\t\tsum\n\t\t\tnumber\n\t\t\tnumber\n", gt.toString());
	}

	@Test
	public void test2() {
		Pattern p = Pattern.compile(
				"(?(DEFINE)(?<term>(?'number')|(?'sum')|(?'product'))(?<sum>(?'summand')(?:\\+(?'summand'))+)(?<product>(?'factor')(?:\\*(?'factor'))+)(?<summand>\\((?'term')\\)|(?'number')|(?'product'))(?<factor>(?'number')|\\((?'term')\\))(?<number>\\d+))(?'term')");
		Matcher m = p.matcher("5+6*9");
		assertTrue(m.matches());

		CaptureTree gt = m.captureTree();
		System.out.println(gt);

	}

	@Test
	public void test3() throws IOException {
		String regex = IOUtils.toString(
				new FileInputStream(getClass().getClassLoader().getResource("term.regex").getFile()), "UTF-8");
		Pattern p = Pattern.compile(regex);

		String term = "(6*[6+7+8]+9)*78*[4*(6+5)+4]";

		System.out.println("You see the term tree for: " + term);
		Matcher m = p.matcher(term);
		assertTrue(m.matches());
		System.out.println(m.captureTree());

		assertTrue(p.matcher("5").matches());
		assertTrue(p.matcher("4+55").matches());
		assertTrue(p.matcher("55+67+888").matches());
		assertTrue(p.matcher("6+99*2").matches());
		assertTrue(p.matcher("99*2+7").matches());
		assertTrue(p.matcher("(4+6*7)*(4+6)").matches());
		assertTrue(p.matcher("(6*[6+7+8]+9)*78*[4*(6+5)+4]").matches());

	}

}
