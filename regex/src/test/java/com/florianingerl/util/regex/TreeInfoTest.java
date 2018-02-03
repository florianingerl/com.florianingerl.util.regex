package com.florianingerl.util.regex;

import static org.junit.Assert.*;

import org.junit.Test;

public class TreeInfoTest {

	private static void check(String regex, int minLength, boolean maxValid, int maxLength, int [] groups) {
		Pattern p = Pattern.compile(regex);
		Pattern.TreeInfo info = new Pattern.TreeInfo();
		p.matchRoot.study(info);
		assertTrue(minLength == info.minLength);
		assertTrue(maxValid == info.maxValid);
		assertFalse(maxValid && maxLength != info.maxLength);
		System.out.println(info.groups );
		assertEquals( groups.length, info.groups.size() );
		for(int group : groups) {
			assertTrue(info.groups.contains(group));
		}
	}

	@Test
	public void minMaxLengthTest() throws Exception {
		check("(a(?1)?z)", 2, false, -1, new int[] { 1 });
		check("(a(?1)??z)", 2, false, -1, new int[] { 1 } );
		check("(a(?1)z|q)", 1, false, -1, new int[] { 1 } );
		check("1(jT(\\<((?1)(,|(?=\\>)))+\\>)?)2", 4, false, -1, new int[] {1,2,3,4});

		check("(\\(([^()]+|(?1))*+\\))", 2, false, -1, new int[] {1,2});

		check("1(?<first>a\\((?<second>(?'first')|[a-zA-Z]),(?'second')\\))2", 8, false, -1, new int[] {1,2});

		check("(?:(a)|bc)(?(1)A|BBBBBB)", 2, true, 8, new int[] {1});
	}
	
	@Test
	public void groupTest() {
		Pattern p = Pattern.compile("(?(DEFINE)(?<A>a(?<B>b)))(?'A')");
		Pattern.TreeInfo info = new Pattern.TreeInfo();
		p.matchRoot.study(info);
		assertEquals(1, info.groups.size() );
		assertTrue( info.groups.contains(1));
		
		p = Pattern.compile("(?:(?<A>a)|(?<B>b))");
		info = new Pattern.TreeInfo();
		p.matchRoot.study(info);
		assertEquals(2, info.groups.size() );
		
		p = Pattern.compile("(a)?(?(1)(?<A>a)|(?<B>b))");
		info = new Pattern.TreeInfo();
		p.matchRoot.study(info);
		assertEquals(3, info.groups.size() );
		
		p = Pattern.compile("(a(b)c)+");
		info = new Pattern.TreeInfo();
		p.matchRoot.study(info);
		assertEquals(2, info.groups.size() );
	}
	
	

}
