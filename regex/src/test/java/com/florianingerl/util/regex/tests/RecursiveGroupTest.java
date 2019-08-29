package com.florianingerl.util.regex.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.florianingerl.util.regex.Pattern;
import com.florianingerl.util.regex.PatternSyntaxException;

public class RecursiveGroupTest {

	@Test(expected = PatternSyntaxException.class)
	public void recursionToNamedGroupThatsNotDefinedShouldCausePatternSyntaxException() {
		Pattern.compile("(?'notdefined')group is not defined");
	}

	@Test(expected = PatternSyntaxException.class)
	public void recursionToGroupThatsNotDefinedShouldCausePatternSyntaxException() {
		Pattern.compile("(?1)group is not defined");
	}

	@Test
	public void recursionToNamedGroupWithoutAMaximumLengthInsideLookbehindShouldCausePatternSyntaxException() {
		try {
			Pattern.compile("(?<=(?'laterdefined'))something(?(DEFINE)(?<laterdefined>a(?'laterdefined')?b))");
		} catch (PatternSyntaxException pse) {
			assertTrue(pse.getMessage().contains("obvious maximum length"));
			return;
		}
		assertTrue(false);
	}

	@Test
	public void recursionToGroupWithoutAMaximumLengthInsideLookbehindShouldCausePatternSyntaxException() {
		try {
			Pattern.compile("(?<=(?1))something(?(DEFINE)(a(?1)?b))");
		} catch (PatternSyntaxException pse) {
			assertTrue(pse.getMessage().contains("obvious maximum length"));
			return;
		}
		assertTrue(false);
	}

	@Test
	public void whenEnteringRecursionAllBackreferencesShouldInitiallyFail() {
		Pattern p = Pattern.compile("(?<first>[a-z])(?<second>\\k<first>)");
		assertTrue(p.matcher("bb").matches());
		assertFalse(p.matcher("ab").matches());

		p = Pattern.compile("(?(DEFINE)(?<second>\\k<first>))(?<first>[a-z])(?'second')");
		assertFalse(p.matcher("bb").matches());
		assertFalse(p.matcher("ab").matches());
	}

	@Test
	public void whenLeavingRecursionACaptureShouldBeMadeForTheGroupThatWasRecursedTo() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<first>[a-z]))(?'first')(?<second>\\k<first>)");
		assertTrue(p.matcher("bb").matches());
		assertFalse(p.matcher("ab").matches());

		p = Pattern.compile("(?(DEFINE)(?<first>(?<letter>[a-z])))(?'first')\\k<first>");
		assertTrue(p.matcher("aa").matches());
	}

	@Test
	public void whenLeavingRecursionAllCapturesMadeInsideTheRecursionShouldAffectBackreferencesOutside() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<first>(?<letter>[a-z])))(?'first')\\k<letter>");
		assertFalse(p.matcher("aa").matches());
	}

	@Test
	public void anagramsShouldBeMatched() {
		Pattern p = Pattern.compile(
				"(?(DEFINE)(?<letter>[a-zA-Z]))\\b(?<anagram>(?'letter')(?'anagram')?\\k<letter>|(?'letter'))\\b");
		assertTrue(p.matcher("radar").matches());
		assertTrue(p.matcher("aa").matches());

		p = Pattern.compile(
				"(?(DEFINE)(?<wrapper>(?<letter>[a-zA-Z])))\\b(?<anagram>(?'wrapper')(?'anagram')?\\k<letter>|(?'letter'))\\b");
		assertFalse(p.matcher("aa").matches());
		assertFalse(p.matcher("radar").matches());
	}
	
	@Test
	public void recursionShouldntLeadToInfiniteLoop() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<A>a|(?'A')a))(?'A')b");
		assertFalse(p.matcher("ccc").matches());
		assertTrue(p.matcher("ab").matches() );
		assertTrue(p.matcher("aab").matches() );
		assertTrue(p.matcher("aaab").matches() );
		
	}
	
	@Test
	public void recursionShouldntLeadToInfiniteLoop2() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<A>(?'A')|a))(?'A')");
		assertTrue(p.matcher("a").matches() );
		assertFalse(p.matcher("b").matches() );
	}
	
	@Test
	public void recursionShouldntLeadToInfiniteLoop3() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<A>(?'A')a|a))(?'A')b");
		assertFalse(p.matcher("aaab").matches() );
	}
	
	@Test
	public void recursionShouldntLeadToInfiniteLoop4() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<A>.+(?!)|a|(?'A')a))(?'A')b");
		assertTrue(p.matcher("aaab").matches() );
	}
	
	@Test
	public void recursionShouldntLeadToInfiniteLoop5() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<A>a(?'A')|a))(?'A')b");
		assertTrue(p.matcher("aaab").matches() );
	}
	
	@Test
	public void recursionShouldntLeadToInfiniteLoopSlice() {
		//This test covers static final class Slice extends SliceNode
		Pattern p = Pattern.compile("(?(DEFINE)(?<A>(?'A')??ab))(?'A')z");
		assertTrue(p.matcher("ababababz").matches() );
		
		p = Pattern.compile("(?(DEFINE)(?<A>(?'A')?ab))(?'A')z");
		assertFalse(p.matcher("ababababz").matches() );
	}
	
	@Test
	public void recursionShouldntLeadToInfiniteLoopSliceInsensitive() {
		//This test covers static class SliceI extends SliceNode
		Pattern p = Pattern.compile("(?(DEFINE)(?<A>(?'A')??ab))(?'A')z", Pattern.CASE_INSENSITIVE);
		assertTrue(p.matcher("AbaBABabz").matches() );
		
		p = Pattern.compile("(?(DEFINE)(?<A>(?'A')?ab))(?'A')z", Pattern.CASE_INSENSITIVE);
		assertFalse(p.matcher("AbaBABabz").matches() );
	}
	
	@Test
	public void recursionShouldntLeadToInfiniteLoopSliceInsensitiveUnicode() {
		//This test covers static final class SliceU extends SliceNode
		Pattern p = Pattern.compile("(?(DEFINE)(?<A>(?'A')??ab))(?'A')z", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		assertTrue(p.matcher("AbaBABabz").matches() );
		
		p = Pattern.compile("(?(DEFINE)(?<A>(?'A')?ab))(?'A')z", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE );
		assertFalse(p.matcher("AbaBABabz").matches() );
	}
	
	@Test
	public void recursionShouldntLeadToInfiniteLoopSliceWithSupplementaryChars() {
		//This test covers static final class SliceS extends SliceNode
		String s = "\uD840\uDC00\uD840\uDC00";
		Pattern p = Pattern.compile("(?(DEFINE)(?<A>(?'A')??"+s+"))(?'A')z");
		assertTrue(p.matcher(s+s+s+s+"z").matches() );
		
		p = Pattern.compile("(?(DEFINE)(?<A>(?'A')?"+s+"))(?'A')z" );
		assertFalse(p.matcher(s+s+s+s+"z").matches() );
	}
	
	@Test
	public void recursionShouldntLeadToInfiniteLoopSliceWithSupplementaryCharsCaseInsensitive() {
		//This test covers static class SliceIS extends SliceNode
		String s = "\uD840\uDC00\uD840\uDC00";
		Pattern p = Pattern.compile("(?(DEFINE)(?<A>(?'A')??"+s+"))(?'A')z", Pattern.CASE_INSENSITIVE);
		assertTrue(p.matcher(s+s+s+s+"z").matches() );
		
		p = Pattern.compile("(?(DEFINE)(?<A>(?'A')?"+s+"))(?'A')z", Pattern.CASE_INSENSITIVE );
		assertFalse(p.matcher(s+s+s+s+"z").matches() );
	}
	
	@Test
	public void recursionShouldntLeadToInfiniteLoopCaseInsensitive() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<A>(?'A')??a))(?'A')z", Pattern.CASE_INSENSITIVE);
		assertTrue(p.matcher("aaaaz").matches() );
		p = Pattern.compile("(?(DEFINE)(?<A>(?'A')?a))(?'A')z", Pattern.CASE_INSENSITIVE);
		assertFalse(p.matcher("aaaaz").matches() );
	}
	
	@Test
	public void sideEffectsOfAboveFix() {
		Pattern p = Pattern.compile("(?x) #comment mode\r\n" + 
				"(?(DEFINE)\r\n" + 
				"(?<sum> (?'summand1') \\+ (?'summand2') )\r\n" + 
				"(?<summand1> (?'number') | (?'sum') )\r\n" + 
				"(?<summand2> (?'number'))\r\n" + 
				"(?<number>\\d+)\r\n" + 
				") # end of define\r\n" + 
				"(?'sum')");
		assertTrue(p.matcher("5+6+7").matches() );
	}
	
	@Test
	public void groupDefinedButNeverRecursedToShouldntLeadToException() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<firstgroup>hello)(?<secondgroup>goodbye))(?'secondgroup')");
		
		assertTrue(p.matcher("goodbye").matches());
	}

}
