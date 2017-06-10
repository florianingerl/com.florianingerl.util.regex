package com.florianingerl.util.regex.tests;

import com.florianingerl.util.regex.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class PluginTest {

	@Test
	public void testCountInEnglishNode() {
		Pattern.installPlugin("english", CountInEnglishNode.class);

		Pattern pattern = Pattern.compile("(\\c{english}(,|$))+");

		Matcher matcher = pattern.matcher("one");
		assertTrue(matcher.matches());

		matcher = pattern.matcher("one,two");
		assertTrue(matcher.matches());

		matcher = pattern.matcher("one,two,three");
		assertTrue(matcher.matches());

		matcher = pattern.matcher("one,three");
		assertFalse(matcher.matches());

		Pattern.uninstallPlugin("english");

	}

}
