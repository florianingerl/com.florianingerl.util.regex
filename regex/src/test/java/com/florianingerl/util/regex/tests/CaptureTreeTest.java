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

}
