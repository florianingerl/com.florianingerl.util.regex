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

		String expected = "\\product{\\sum{\\product{6,\\sum{6,7,8}},9},78,\\sum{\\product{4,\\sum{6,5}},4}}";
		CaptureReplacer replacer = new DefaultCaptureReplacer() {

			private List<String> summandFactor = Arrays.asList("summand", "factor");
			private List<String> numSumProd = Arrays.asList("number", "sum", "product");

			@Override
			public String replace(CaptureTreeNode node) {
				if ("sum".equals(node.getGroupName())) {
					return "\\sum{" + node.getChildren().stream().filter(n -> "summand".equals(n.getGroupName()))
							.map(n -> replace(n)).collect(Collectors.joining(",")) + "}";
				} else if ("product".equals(node.getGroupName())) {
					return "\\product{" + node.getChildren().stream().filter(n -> "factor".equals(n.getGroupName()))
							.map(n -> replace(n)).collect(Collectors.joining(",")) + "}";
				} else if (summandFactor.contains(node.getGroupName())) {
					return replace(node.getChildren().stream().filter(n -> numSumProd.contains(n.getGroupName()))
							.findFirst().get());
				} else {
					return super.replace(node);
				}
			}

		};
		String actual = m.replaceAll(replacer);
		System.out.println(actual);

		assertEquals(expected, actual);

		actual = m.replaceFirst(replacer);
		System.out.println(actual);
		assertEquals(expected, actual);
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

}
