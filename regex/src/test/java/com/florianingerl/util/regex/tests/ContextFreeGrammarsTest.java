package com.florianingerl.util.regex.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;

public class ContextFreeGrammarsTest {

	@Test
	public void test() throws FileNotFoundException, IOException {

		String regex = IOUtils.toString(
				new FileInputStream(getClass().getClassLoader().getResource("abnotequal.regex").getFile()), "UTF-8");
		System.out.println(regex);
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
		System.out.println(regex);
		Pattern p = Pattern.compile(regex);

		assertTrue(p.matcher("abbccc").matches());
		assertFalse(p.matcher("abbc").matches() );
		assertFalse(p.matcher("aabbcc").matches());
		assertTrue(p.matcher("aacc").matches());
		assertTrue(p.matcher("bbcc").matches());
	}

	
	@Test
	public void test3() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<S>aa(?'S')b|c))(?'S')");
		
		assertTrue(p.matcher("aacb").matches() );
		assertTrue(p.matcher("c").matches() );
		assertTrue(p.matcher("aaaacbb").matches() );
		assertFalse(p.matcher("acb").matches() );
		assertFalse(p.matcher("aaacb").matches() );
	}
	
	
	@Test
	public void test4() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<S>(?'C')b(?'B'))(?<B>b(?'B')|)(?<C>a(?'C')b|))(?'S')");
	
		assertTrue(p.matcher("b").matches() );
		assertTrue(p.matcher("bb").matches() );
		assertTrue(p.matcher("abb").matches() );
		assertTrue(p.matcher("aabbbb").matches() );
		assertFalse(p.matcher("ab").matches() );
		assertFalse(p.matcher("aab").matches() );
	}
	
	@Test
	public void test5() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<S>(?'T')aba(?'T')|(?'T')bab(?'T'))(?<T>a(?'T')|b(?'T')|c(?'T')|))(?'S')");
		
		assertTrue(p.matcher("abacccc").matches() );
		assertTrue(p.matcher("cccbab").matches() );
		assertTrue(p.matcher("accabacab").matches() );
		assertTrue(p.matcher("acbabccb").matches() );
		assertFalse(p.matcher("abcabc").matches() );
		
	}
	
	@Test
	public void test6() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<S>a(?'S')c|b(?'S')c|))(?'S')");
		
		assertTrue(p.matcher("abcc").matches() );
		assertTrue(p.matcher("abaccc").matches() );
		assertTrue(p.matcher("").matches() );
		assertFalse(p.matcher("abccc").matches() );
	}
}
