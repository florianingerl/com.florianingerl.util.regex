package com.florianingerl.util.regex.tests;

import com.florianingerl.util.regex.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class PluginTests {

	
	@Test
	public void testPopCaptureNode() {
		Pattern.installPlugin("popCapture", PopCaptureNode.class );
		
		Pattern pentagram = Pattern.compile("\\b((?<gut>[a-zA-Z])(?1)?\\k<gut>\\c{popCapture,gut}|[a-zA-Z])\\b");
		
		Matcher matcher = pentagram.matcher("anna");
		assertTrue( matcher.matches() );
		
		matcher = pentagram.matcher("lagerregal");
		assertTrue( matcher.matches() );
		
		matcher = pentagram.matcher("otito");
		assertTrue( matcher.matches() );
		
		
		Pattern.uninstallPlugin("popCapture");
	}
	
	@Test
	public void testCountInEnglishNode() {
		Pattern.installPlugin("english", CountInEnglishNode.class );
		
		Pattern pattern = Pattern.compile("(\\c{english}(,|$))+");
		
		Matcher matcher = pattern.matcher("one");
		assertTrue(matcher.matches() );
		
		matcher = pattern.matcher("one,two");
		assertTrue(matcher.matches() );
		
		matcher = pattern.matcher("one,two,three");
		assertTrue(matcher.matches() );
		
		matcher = pattern.matcher("one,three");
		assertFalse(matcher.matches() );
		
		Pattern.uninstallPlugin("english");
		
	}

}

 
