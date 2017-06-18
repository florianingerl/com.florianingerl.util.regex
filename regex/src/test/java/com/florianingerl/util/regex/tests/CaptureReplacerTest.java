package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.florianingerl.util.regex.CaptureReplacer;
import com.florianingerl.util.regex.CaptureTreeNode;
import com.florianingerl.util.regex.DefaultCaptureReplacer;
import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;

public class CaptureReplacerTest {

	@Test
	public void test() throws FileNotFoundException, IOException {

		String regex = IOUtils.toString(
				new FileInputStream(getClass().getClassLoader().getResource("term.regex").getFile()), "UTF-8");
		Pattern p = Pattern.compile(regex);

		String term = "(6*[6+7+8]+9)*78*[4*(6+5)+4]";

		Matcher m = p.matcher(term);

		String expected = "product(sum(product(6,sum(6,7,8)),9),78,sum(product(4,sum(6,5)),4))";
		CaptureReplacer replacer = new DefaultCaptureReplacer() {

			@Override
			public String replace(CaptureTreeNode node) {
				if ("sum".equals(node.getGroupName())) {
					return "sum(" + node.getChildren().stream().filter(n -> "summand".equals(n.getGroupName()))
							.map(n -> replace(n)).collect(Collectors.joining(",")) + ")";
				} else if ("product".equals(node.getGroupName())) {
					return "product(" + node.getChildren().stream().filter(n -> "factor".equals(n.getGroupName()))
							.map(n -> replace(n)).collect(Collectors.joining(",")) + ")";
				} else if (Arrays.asList("summand", "factor").contains(node.getGroupName())) {
					return replace(node.getChildren().get(0));
				} else {
					return super.replace(node);
				}
			}

		};
		String replacement = m.replaceAll(replacer);
		System.out.println(replacement);
		System.out.println(expected);

		assertEquals(expected, replacement);

		replacement = m.replaceFirst(replacer);
		System.out.println(replacement);
		assertEquals(expected, replacement);
	}

	@Test
	public void defaultReplacerShouldIgnoreGroupsInLookaround() {
		Pattern p = Pattern.compile("(?<=(Row='))(\\d++)(?=('))");
		Matcher m = p.matcher("Row='100' Row='40'");
		String actual = m.replaceAll(new DefaultCaptureReplacer() {

			@Override
			public String replace(CaptureTreeNode node) {
				if (node.getGroupNumber() == 2) {
					int i = Integer.parseInt(node.getCapture().getValue());
					return "" + (i + 1);
				} else
					return super.replace(node);
			}

		});
		assertEquals(actual, "Row='101' Row='41'");
	}

	@Test
	public void test2() throws FileNotFoundException, IOException {
		Pattern pattern = Pattern.compile("(?x)\r\n" + "(?(DEFINE)\r\n"
				+ "(?<sum> (?'summand')(?:\\+(?'summand'))+ )\r\n" + "(?<summand> (?'product') | (?'number') )\r\n"
				+ "(?<product> (?'factor')(?:\\*(?'factor'))+ )\r\n" + "(?<factor>(?'number') )\r\n"
				+ "(?<number>\\d++)\r\n" + ")\r\n" + "(?'sum')");
		Matcher matcher = pattern.matcher("First: 6+7*8 Second: 6*8+7");
		CaptureReplacer replacer = new DefaultCaptureReplacer() {

			@Override
			public String replace(CaptureTreeNode node) {
				if ("sum".equals(node.getGroupName())) {
					return "\\sum{" + node.getChildren().stream().filter(n -> "summand".equals(n.getGroupName()))
							.map(n -> replace(n)).collect(Collectors.joining(",")) + "}";
				} else if ("product".equals(node.getGroupName())) {
					return "\\product{" + node.getChildren().stream().filter(n -> "factor".equals(n.getGroupName()))
							.map(n -> replace(n)).collect(Collectors.joining(",")) + "}";
				} else
					return super.replace(node);
			}

		};
		String replacement = matcher.replaceAll(replacer);
		System.out.println(replacement);
		assertEquals("First: \\sum{6,\\product{7,8}} Second: \\sum{\\product{6,8},7}", replacement);

		matcher.reset();
		replacement = matcher.replaceFirst(replacer);
		assertEquals("First: \\sum{6,\\product{7,8}} Second: 6*8+7", replacement);
	}

	@Test
	public void some() {
		Matcher matcher = Pattern.compile("(?x)" + "(?(DEFINE)" + "(?<sum> (?'summand')(?:\\+(?'summand'))+ )"
				+ "(?<summand> (?'product') |  (?'number') )" + "(?<product> (?'factor')(?:\\*(?'factor'))+ )"
				+ "(?<factor>(?'number') ) " + "(?<number>\\d++)" + ")" + "(?'sum')").matcher("5+6*8");
		matcher.matches();
		System.out.println(matcher.captureTree());
	}

	@Test
	public void abcTest() {
		Pattern pattern = Pattern.compile("(?<abc>ABC\\((?<arg1>(?:(?'abc')|[^,])+)\\,(?<arg2>(?:(?'abc')|[^)])+)\\))");
		Matcher matcher = pattern.matcher("ABC(ABC(20,2),5)");
		String replacement = matcher.replaceAll(new DefaultCaptureReplacer() {
			@Override
			public String replace(CaptureTreeNode node) {
				if ("abc".equals(node.getGroupName())) {
					return "(" + replace(node.getChildren().get(0)) + ")%(" + replace(node.getChildren().get(1)) + ")";
				} else
					return super.replace(node);
			}

		});
		System.out.println(replacement);
		assertEquals("((20)%(2))%(5)", replacement);
	}

	@Test
	public void abcTest2() {
		Pattern pattern = Pattern.compile("(?<fraction>(?<arg>\\(((?:(?'fraction')|[^)])+)\\))%(?'arg'))");
		Matcher matcher = pattern.matcher("((20)%(2))%(5)");
		String replacement = matcher.replaceAll(new DefaultCaptureReplacer() {
			@Override
			public String replace(CaptureTreeNode node) {
				if ("fraction".equals(node.getGroupName())) {
					return "ABC(" + replace(node.getChildren().get(0)) + "," + replace(node.getChildren().get(1)) + ")";
				} else if ("arg".equals(node.getGroupName())) {
					return replace(node.getChildren().get(0));
				} else
					return super.replace(node);
			}

		});
		System.out.println(replacement);
		assertEquals("ABC(ABC(20,2),5)", replacement);
	}

}
