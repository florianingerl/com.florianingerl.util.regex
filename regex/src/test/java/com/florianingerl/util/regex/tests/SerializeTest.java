package com.florianingerl.util.regex.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.florianingerl.util.regex.Pattern;

public class SerializeTest {

	@Test
	public void serializationOfPatternsWithMatchFlagsTurnedOnAndOffShouldWork() {
		Pattern p = Pattern.compile("a(?-i)b", Pattern.CASE_INSENSITIVE );
		assertTrue(p.matcher("Ab").matches() );
		assertFalse(p.matcher("AB").matches() );
		p = RegExTest.serializeAndDeserialize(p);
		assertTrue(p.matcher("Ab").matches() );
		assertFalse(p.matcher("AB").matches() );
	}
	
	@Test
	public void queryFlagsOnAPatternThatTurnsFlagsOnAndOffShouldWork() {
		Pattern p = Pattern.compile("a(?-i)b", Pattern.CASE_INSENSITIVE );
		assertEquals(Pattern.CASE_INSENSITIVE, p.flags());
	}

}
