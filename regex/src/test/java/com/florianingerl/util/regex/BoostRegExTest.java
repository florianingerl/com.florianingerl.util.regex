package com.florianingerl.util.regex;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class BoostRegExTest {

	private static boolean failure = false;
	private static int failCount = 0;
	private static String firstFailure = null;

	@Test
	public void callMain(){
		try {
			main(null);
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}	
	}
	
	public static void main(String [] args){
		basic_tests();
		test_non_marking_paren();
		test_partial_match();
		test_nosubs();
		if(failure){throw new RuntimeException("RegExTest failed, 1st failure: " + firstFailure); }
		else{System.err.println("OKAY: All tests passed.");}
	}
	private static void report(String testName) {
		int spacesToAdd = 30 - testName.length();
		StringBuffer paddedNameBuffer = new StringBuffer(testName);
		for (int i = 0; i < spacesToAdd; i++)
			paddedNameBuffer.append(" ");
		String paddedName = paddedNameBuffer.toString();
		System.err.println(paddedName + ": " + (failCount == 0 ? "Passed" : "Failed(" + failCount + ")"));
		if (failCount > 0) {
			failure = true;
			if (firstFailure == null) {
				firstFailure = testName;
			}
		}
		failCount = 0;
	}
	private static void check(String regex, int flags, String s, int [] data ){
		Pattern p = Pattern.compile(regex, flags );
		Matcher m = p.matcher(s);
		int i = 0;
		while(data[i] != -2 ){
			if(!m.find() ){
				++failCount;
				return;
			}
			int j = 0;
			while( data[i] != -2 ){
				if(m.start(j) != data[i++] || m.end(j) != data[i++] ){
					++failCount;
				}
				++j;
			}
			++i;
		}
		if( m.find() )++failCount;
	}
	private static void basic_tests(){
		check("Z",0|Pattern.DOTALL,"aaa", new int[]{-2, -2});
		check("Z",0|Pattern.DOTALL,"xxxxZZxxx", new int[]{4, 5, -2, 5, 6, -2, -2});
		check("(a)",0|Pattern.DOTALL,"zzzaazz", new int[]{3, 4, 3, 4, -2, 4, 5, 4, 5, -2, -2});
		check("()",0|Pattern.DOTALL,"zzz", new int[]{0, 0, 0, 0, -2, 1, 1, 1, 1, -2, 2, 2, 2, 2, -2, 3, 3, 3, 3, -2, -2});
		check("()",0|Pattern.DOTALL,"", new int[]{0, 0, 0, 0, -2, -2});
		check("",0|Pattern.DOTALL,"abc", new int[]{0, 0, -2, 1, 1, -2, 2, 2, -2, 3, 3, -2, -2});
		check("a",0|Pattern.DOTALL,"b", new int[]{-2, -2});
		check("\\(\\)",0|Pattern.DOTALL,"()", new int[]{0, 2, -2, -2});
		check("\\(a\\)",0|Pattern.DOTALL,"(a)", new int[]{0, 3, -2, -2});
		check("p(a)rameter",0|Pattern.DOTALL,"ABCparameterXYZ", new int[]{3, 12, 4, 5, -2, -2});
		check("[pq](a)rameter",0|Pattern.DOTALL,"ABCparameterXYZ", new int[]{3, 12, 4, 5, -2, -2});
		check(".",0|Pattern.DOTALL,"a", new int[]{0, 1, -2, -2});
		check(".",0|Pattern.DOTALL,"\n", new int[]{0, 1, -2, -2});
		check(".",0|Pattern.DOTALL,"\r", new int[]{0, 1, -2, -2});
		check(".",0|Pattern.DOTALL,"\0", new int[]{0, 1, -2, -2});
		check(".",0,"a", new int[]{0, 1, -2, -2});
		check(".",0,"\n", new int[]{-2, -2});
		check(".",0,"\r", new int[]{-2, -2});
		check(".",0,"\0", new int[]{0, 1, -2, -2});

		report("basic_tests");
	}

	private static void test_non_marking_paren(){
		check("(?:abc)+",0|Pattern.DOTALL,"xxabcabcxx", new int[]{2, 8, -2, -2});
		check("(?:a+)(b+)",0|Pattern.DOTALL,"xaaabbbx", new int[]{1, 7, 4, 7, -2, -2});
		check("(a+)(?:b+)",0|Pattern.DOTALL,"xaaabbba", new int[]{1, 7, 1, 4, -2, -2});
		check("(?:(a+)b+)",0|Pattern.DOTALL,"xaaabbba", new int[]{1, 7, 1, 4, -2, -2});
		check("(?:a+(b+))",0|Pattern.DOTALL,"xaaabbba", new int[]{1, 7, 4, 7, -2, -2});
		check("a+(?x:#b+\n)b+",0|Pattern.DOTALL,"xaaabbba", new int[]{1, 7, -2, -2});
		check("(a)(?:b|$)",0|Pattern.DOTALL,"ab", new int[]{0, 2, 0, 1, -2, -2});
		check("(a)(?:b|$)",0|Pattern.DOTALL,"a", new int[]{0, 1, 0, 1, -2, -2});

		report("test_non_marking_paren");
	}

	private static void test_partial_match(){

		report("test_partial_match");
	}

	private static void test_nosubs(){
		check("a(b?c)+d",0|Pattern.DOTALL,"accd", new int[]{0, 4, -2, -2});
		check("(wee|week)(knights|night)",0|Pattern.DOTALL,"weeknights", new int[]{0, 10, -2, -2});
		check(".*",0|Pattern.DOTALL,"abc", new int[]{0, 3, -2, 3, 3, -2, -2});
		check("a(b|(c))d",0|Pattern.DOTALL,"abd", new int[]{0, 3, -2, -2});
		check("a(b|(c))d",0|Pattern.DOTALL,"acd", new int[]{0, 3, -2, -2});
		check("a(b*|c|e)d",0|Pattern.DOTALL,"abbd", new int[]{0, 4, -2, -2});
		check("a(b*|c|e)d",0|Pattern.DOTALL,"acd", new int[]{0, 3, -2, -2});
		check("a(b*|c|e)d",0|Pattern.DOTALL,"ad", new int[]{0, 2, -2, -2});
		check("a(b?)c",0|Pattern.DOTALL,"abc", new int[]{0, 3, -2, -2});
		check("a(b?)c",0|Pattern.DOTALL,"ac", new int[]{0, 2, -2, -2});
		check("a(b+)c",0|Pattern.DOTALL,"abc", new int[]{0, 3, -2, -2});
		check("a(b+)c",0|Pattern.DOTALL,"abbbc", new int[]{0, 5, -2, -2});
		check("a(b*)c",0|Pattern.DOTALL,"ac", new int[]{0, 2, -2, -2});
		check("(a|ab)(bc([de]+)f|cde)",0|Pattern.DOTALL,"abcdef", new int[]{0, 6, -2, -2});
		check("a([bc]?)c",0|Pattern.DOTALL,"abc", new int[]{0, 3, -2, -2});
		check("a([bc]?)c",0|Pattern.DOTALL,"ac", new int[]{0, 2, -2, -2});
		check("a([bc]+)c",0|Pattern.DOTALL,"abc", new int[]{0, 3, -2, -2});
		check("a([bc]+)c",0|Pattern.DOTALL,"abcc", new int[]{0, 4, -2, -2});
		check("a([bc]+)bc",0|Pattern.DOTALL,"abcbc", new int[]{0, 5, -2, -2});
		check("a(bb+|b)b",0|Pattern.DOTALL,"abb", new int[]{0, 3, -2, -2});
		check("a(bbb+|bb+|b)b",0|Pattern.DOTALL,"abb", new int[]{0, 3, -2, -2});
		check("a(bbb+|bb+|b)b",0|Pattern.DOTALL,"abbb", new int[]{0, 4, -2, -2});
		check("a(bbb+|bb+|b)bb",0|Pattern.DOTALL,"abbb", new int[]{0, 4, -2, -2});
		check("(.*).*",0|Pattern.DOTALL,"abcdef", new int[]{0, 6, -2, 6, 6, -2, -2});
		check("(a*)*",0|Pattern.DOTALL,"bc", new int[]{0, 0, -2, 1, 1, -2, 2, 2, -2, -2});

		report("test_nosubs");
	}

}
