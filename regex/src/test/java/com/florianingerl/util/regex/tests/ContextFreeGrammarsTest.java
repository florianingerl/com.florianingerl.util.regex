package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.florianingerl.util.regex.CaptureReplacer;
import com.florianingerl.util.regex.DefaultCaptureReplacer;
import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;

public class ContextFreeGrammarsTest {

	@Test
	public void test() throws FileNotFoundException, IOException {

		String regex = IOUtils.toString(
				new FileInputStream(getClass().getClassLoader().getResource("abnotequal.regex").getFile()), "UTF-8");
		Pattern p = Pattern.compile(regex);

		assertFalse(p.matcher("ab").matches());
		assertTrue(p.matcher("abb").matches() );
		assertFalse(p.matcher("aabb").matches());
		assertTrue(p.matcher("aab").matches());
	}
	
	@Test
	public void test2() throws FileNotFoundException, IOException {

		String regex = IOUtils.toString(
				new FileInputStream(getClass().getClassLoader().getResource("casoftenasaandb.regex").getFile()), "UTF-8");
		Pattern p = Pattern.compile(regex);

		assertTrue(p.matcher("abbccc").matches());
		assertFalse(p.matcher("abbc").matches() );
		assertFalse(p.matcher("aabbcc").matches());
		assertTrue(p.matcher("aacc").matches());
		assertTrue(p.matcher("bbcc").matches());
	}

}
