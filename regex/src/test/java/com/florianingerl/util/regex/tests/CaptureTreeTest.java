package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.florianingerl.util.regex.CaptureTree;
import com.florianingerl.util.regex.CaptureTreeNode;
import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class CaptureTreeTest {

	
	
	private void processFile(String filename) throws IOException {
		Scanner scanner = new Scanner(new FileInputStream(getClass().getClassLoader().getResource(filename).getFile()));
		String line = null;
		int count = 0;
		List<Pair<String, Integer>> statistics = new LinkedList<Pair<String, Integer>>();
		while (scanner.hasNextLine() && !(line = scanner.nextLine()).isEmpty()) {
			String file = line;
			Pattern pattern = Pattern.compile(IOUtils
					.toString(new FileInputStream(getClass().getClassLoader().getResource(file).getFile()), "UTF-8"));
			int j = 0;
			while (scanner.hasNextLine() && !(line = scanner.nextLine()).isEmpty()) {
				String[] tokens = line.split(" ");
				Matcher matcher = pattern.matcher(tokens[0]);
				matcher.setMode(Matcher.CAPTURE_TREE);
				if (tokens.length == 2) {
					System.out.println("Pattern: " + matcher.pattern().pattern() + " Input: " + tokens[0]);
					assertTrue(matcher.matches());
					Pattern gtp = Pattern.compile(tokens[1]);
					String captureTree = matcher.captureTree().toString();
					System.out.println("Capture tree for " + tokens[0]);
					System.out.println(captureTree);
					assertTrue(captureTree + " doesn't match the pattern " + tokens[1],
							gtp.matcher(matcher.captureTree().toString()).matches());
				} else {
					assertFalse(matcher.matches());
				}
				++j;
				++count;
				line = null;
			}

			statistics.add(new ImmutablePair(file, j));

			if (line == null)
				break;

		}
		System.err.println("Executed " + count + " tests from CaptureTreeTestCases.txt");
		for (Pair<String, Integer> pair : statistics) {
			System.err.println("\tExecuted " + pair.getRight() + " tests from " + pair.getLeft());
		}
	}

	@Test
	public void captureTreeTests() throws IOException {
		processFile("CaptureTreeTestCases.txt");
	}
	
	@Test(expected=IllegalStateException.class)
	public void captureTreeShouldntBeAvailableIfItsModeIsOff() {
		Pattern p = Pattern.compile("(a(b))");
		Matcher m = p.matcher("ab");
		assertTrue((m.getMode() & Matcher.CAPTURE_TREE) == 0);
		assertTrue(m.matches());
		System.out.println( m.captureTree() );
	}
	
	@Test
	public void backtrackingOfCaptureTreeGenerationShouldWork() {
		check("(?>(a))?a", "a", "0\n");
		check("(?<groupName>c(?>(a))?ad)","cad","0\n\tgroupName\n");
		check("a(?<groupName>(?<=(a))b|c)","ac","0\n\tgroupName\n");
		check("\\ud800\\udc61(?<groupName>(?<=(\\ud800\\udc61))b|c)","\\ud800\\udc61c","0\n\tgroupName\n");
		check("(?<groupName>(?=(a))ab|ac)","ac","0\n\tgroupName\n");
		check("(?<groupName>(?=(\\ud800\\udc61))\\ud800\\udc61b|\\ud800\\udc61c)","\\ud800\\udc61c","0\n\tgroupName\n");
		check("(?(?=(a))ab)|ac","ac","0\n");
		check("(?!(a))a|(?<groupName>a)","a","0\n\tgroupName\n");
		check("(?!(\\ud800\\udc61))\\ud800\\udc61|(?<groupName>\\ud800\\udc61)","\\ud800\\udc61","0\n\tgroupName\n");
		check("a(?:(?<!(a))b|(?<groupName>b))","ab","0\n\tgroupName\n");
		check("\\ud800\\udc61(?:(?<!(\\ud800\\udc61))b|(?<groupName>b))","\\ud800\\udc61b","0\n\tgroupName\n");
		check("(?:(a)++|(?<groupName>a)+)a","aaa","0\n\tgroupName\n\tgroupName\n");
		
		check("(?=a)*?ab|ac","ac","0\n");
		
	}
	
	@Test
	@Ignore
	public void failingTest() {
		check("(?:(?=(a))*?ab|ac)","ac","0\n");
	}
	
	
	
	private static void check(String pattern, String input, String captureTree) {
		pattern = replaceSupplementaryCharacters(pattern);
		check(Pattern.compile(pattern), input, captureTree);
	}
	
	private static String replaceSupplementaryCharacters(String pattern) {
		int index;
		while ((index = pattern.indexOf("\\u")) != -1) {
			StringBuffer temp = new StringBuffer(pattern);
			String value = temp.substring(index + 2, index + 6);
			char aChar = (char) Integer.parseInt(value, 16);
			String unicodeChar = "" + aChar;
			temp.replace(index, index + 6, unicodeChar);
			pattern = temp.toString();
		}
		return pattern;
	}

	private static void check(Pattern pattern, String input, String captureTree) {
		input = replaceSupplementaryCharacters(input);
		Matcher m = pattern.matcher(input);
		m.setMode(Matcher.CAPTURE_TREE);
		assertTrue(m.matches());
		assertEquals(captureTree, m.captureTree().toString());
	}
	
	

}
