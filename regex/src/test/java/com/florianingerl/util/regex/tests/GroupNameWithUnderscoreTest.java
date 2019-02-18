package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;

import org.junit.Test;

public class GroupNameWithUnderscoreTest {

	@Test
	public void groupNamesWithUnderscoreInBackreferencesShouldWork() {
		
		Pattern p = Pattern.compile("(?<group_name>A|B)\\k<group_name>");
		assertTrue( p.matcher("AA").matches());
		assertTrue( p.matcher("BB").matches() );
		assertFalse( p.matcher("AB").matches());
		
	}
	
	
	
	@Test
	public void groupNameWithUnderscoreInRecursiveGroupShouldWork() {
		
		Pattern p = Pattern.compile("(?<group_name>A|B)(?'group_name')");
		assertTrue( p.matcher("AA").matches());
		assertTrue( p.matcher("BB").matches() );
		assertTrue( p.matcher("AB").matches());
	}
	
	@Test
	public void groupNameWithUnderscoreInConditionalShouldWork() {
		Pattern p = Pattern.compile("(?:(?<group_name>\\()|\\[)amen(?(group_name)\\)|\\])");
		assertTrue( p.matcher("(amen)").matches() );
		assertTrue( p.matcher("[amen]").matches());
		assertFalse( p.matcher("(amen]").matches() );
		assertFalse( p.matcher("[amen)").matches() );
	}
	
	@Test
	public void queryGroupWithUnderscoreShouldWork() {
		Pattern p = Pattern.compile("(?<group_name>A)");
		Matcher m = p.matcher("A");
		assertTrue( m.matches() );
		assertEquals("A", m.group("group_name"));
	}
	
	/*
	@Test
	public void groupNamesWithUnderscoreCanBeReplaced() {
		Pattern p = Pattern.compile("(?<group_name>A|b)");
		Matcher m = p.matcher("AmenErbarmen");
		
		String s = m.replaceAll("[${group_name}]");
		assertEquals("[A]menEr[b]armen", s);
	}*/

}
