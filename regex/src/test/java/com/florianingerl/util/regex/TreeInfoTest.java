package com.florianingerl.util.regex;

import static org.junit.Assert.*;

import org.junit.Test;

public class TreeInfoTest {

	private static void check(String regex, int minLength, boolean maxValid, int maxLength ){
		Pattern p = Pattern.compile(regex);
		Pattern.TreeInfo info = new Pattern.TreeInfo();
		p.matchRoot.study(info);
		assertTrue( minLength == info.minLength );
		assertTrue(maxValid == info.maxValid );
		assertFalse( maxValid && maxLength != info.maxLength);
	}
	
	@Test
	public void minMaxLengthTest() throws Exception {
		check("(a(?1)?z)", 2, false, -1);
		check("(a(?1)??z)", 2, false, -1);
		check("(a(?1)z|q)", 1, false, -1);
		check("1(jT(\\<((?1)(,|(?=\\>)))+\\>)?)2", 4, false, -1 );
		
		check("(\\(([^()]+|(?1))*+\\))", 2, false, -1 );
		check("\\b(([a-zA-Z])(?1)?(?<-2>\\2)|[a-zA-Z])\\b", 1, false, -1 );
		
		check("1(?<first>a\\((?<second>(?first)|[a-zA-Z]),(?second)\\))2", 8, false, -1 );
		
		check("(?:(a)|bc)(?(1)A|BBBBBB)", 2, true, 8 );
	}

}
