package com.florianingerl.util.regex;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
public class BoostRegExTest {

	private static boolean failure = false;
	private static int failCount = 0;
	private static String firstFailure = null;

	@Test
	public void callMain() {
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
		test_tricky_cases();
		test_tricky_cases2();
		test_tricky_cases3();
		test_alt();
		if(failure){throw new RuntimeException("BoostRegExTest failed, 1st failure: " + firstFailure); }
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
	private static void checkExpectedFail(String p, int flags) {
		try {
			Pattern.compile(p, flags);
		} catch (PatternSyntaxException pse) {
			return;
		}
		failCount++;
	}
	private static void basic_tests(){
		check("Z",0|Pattern.MULTILINE|Pattern.DOTALL,"aaa", new int[]{-2, -2});
		check("Z",0|Pattern.MULTILINE|Pattern.DOTALL,"xxxxZZxxx", new int[]{4, 5, -2, 5, 6, -2, -2});
		check("(a)",0|Pattern.MULTILINE|Pattern.DOTALL,"zzzaazz", new int[]{3, 4, 3, 4, -2, 4, 5, 4, 5, -2, -2});
		check("()",0|Pattern.MULTILINE|Pattern.DOTALL,"zzz", new int[]{0, 0, 0, 0, -2, 1, 1, 1, 1, -2, 2, 2, 2, 2, -2, 3, 3, 3, 3, -2, -2});
		check("()",0|Pattern.MULTILINE|Pattern.DOTALL,"", new int[]{0, 0, 0, 0, -2, -2});
		check("",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 0, -2, 1, 1, -2, 2, 2, -2, 3, 3, -2, -2});
		check("a",0|Pattern.MULTILINE|Pattern.DOTALL,"b", new int[]{-2, -2});
		check("\\(\\)",0|Pattern.MULTILINE|Pattern.DOTALL,"()", new int[]{0, 2, -2, -2});
		check("\\(a\\)",0|Pattern.MULTILINE|Pattern.DOTALL,"(a)", new int[]{0, 3, -2, -2});
		check("p(a)rameter",0|Pattern.MULTILINE|Pattern.DOTALL,"ABCparameterXYZ", new int[]{3, 12, 4, 5, -2, -2});
		check("[pq](a)rameter",0|Pattern.MULTILINE|Pattern.DOTALL,"ABCparameterXYZ", new int[]{3, 12, 4, 5, -2, -2});
		check(".",0|Pattern.MULTILINE|Pattern.DOTALL,"a", new int[]{0, 1, -2, -2});
		check(".",0|Pattern.MULTILINE|Pattern.DOTALL,"\n", new int[]{0, 1, -2, -2});
		check(".",0|Pattern.MULTILINE|Pattern.DOTALL,"\r", new int[]{0, 1, -2, -2});
		check(".",0|Pattern.MULTILINE|Pattern.DOTALL,"\0", new int[]{0, 1, -2, -2});
		check(".",0|Pattern.MULTILINE,"a", new int[]{0, 1, -2, -2});
		check(".",0|Pattern.MULTILINE,"\n", new int[]{-2, -2});
		check(".",0|Pattern.MULTILINE,"\r", new int[]{-2, -2});
		check(".",0|Pattern.MULTILINE,"\0", new int[]{0, 1, -2, -2});

		report("basic_tests");
	}

	private static void test_non_marking_paren(){
		check("(?:abc)+",0|Pattern.MULTILINE|Pattern.DOTALL,"xxabcabcxx", new int[]{2, 8, -2, -2});
		check("(?:a+)(b+)",0|Pattern.MULTILINE|Pattern.DOTALL,"xaaabbbx", new int[]{1, 7, 4, 7, -2, -2});
		check("(a+)(?:b+)",0|Pattern.MULTILINE|Pattern.DOTALL,"xaaabbba", new int[]{1, 7, 1, 4, -2, -2});
		check("(?:(a+)b+)",0|Pattern.MULTILINE|Pattern.DOTALL,"xaaabbba", new int[]{1, 7, 1, 4, -2, -2});
		check("(?:a+(b+))",0|Pattern.MULTILINE|Pattern.DOTALL,"xaaabbba", new int[]{1, 7, 4, 7, -2, -2});
		check("a+(?x:#b+\n)b+",0|Pattern.MULTILINE|Pattern.DOTALL,"xaaabbba", new int[]{1, 7, -2, -2});
		check("(a)(?:b|$)",0|Pattern.MULTILINE|Pattern.DOTALL,"ab", new int[]{0, 2, 0, 1, -2, -2});
		check("(a)(?:b|$)",0|Pattern.MULTILINE|Pattern.DOTALL,"a", new int[]{0, 1, 0, 1, -2, -2});

		report("test_non_marking_paren");
	}

	private static void test_partial_match(){

		report("test_partial_match");
	}

	private static void test_nosubs(){
		check("a(b?c)+d",0|Pattern.MULTILINE|Pattern.DOTALL,"accd", new int[]{0, 4, -2, -2});
		check("(wee|week)(knights|night)",0|Pattern.MULTILINE|Pattern.DOTALL,"weeknights", new int[]{0, 10, -2, -2});
		check(".*",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 3, -2, 3, 3, -2, -2});
		check("a(b|(c))d",0|Pattern.MULTILINE|Pattern.DOTALL,"abd", new int[]{0, 3, -2, -2});
		check("a(b|(c))d",0|Pattern.MULTILINE|Pattern.DOTALL,"acd", new int[]{0, 3, -2, -2});
		check("a(b*|c|e)d",0|Pattern.MULTILINE|Pattern.DOTALL,"abbd", new int[]{0, 4, -2, -2});
		check("a(b*|c|e)d",0|Pattern.MULTILINE|Pattern.DOTALL,"acd", new int[]{0, 3, -2, -2});
		check("a(b*|c|e)d",0|Pattern.MULTILINE|Pattern.DOTALL,"ad", new int[]{0, 2, -2, -2});
		check("a(b?)c",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 3, -2, -2});
		check("a(b?)c",0|Pattern.MULTILINE|Pattern.DOTALL,"ac", new int[]{0, 2, -2, -2});
		check("a(b+)c",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 3, -2, -2});
		check("a(b+)c",0|Pattern.MULTILINE|Pattern.DOTALL,"abbbc", new int[]{0, 5, -2, -2});
		check("a(b*)c",0|Pattern.MULTILINE|Pattern.DOTALL,"ac", new int[]{0, 2, -2, -2});
		check("(a|ab)(bc([de]+)f|cde)",0|Pattern.MULTILINE|Pattern.DOTALL,"abcdef", new int[]{0, 6, -2, -2});
		check("a([bc]?)c",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 3, -2, -2});
		check("a([bc]?)c",0|Pattern.MULTILINE|Pattern.DOTALL,"ac", new int[]{0, 2, -2, -2});
		check("a([bc]+)c",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 3, -2, -2});
		check("a([bc]+)c",0|Pattern.MULTILINE|Pattern.DOTALL,"abcc", new int[]{0, 4, -2, -2});
		check("a([bc]+)bc",0|Pattern.MULTILINE|Pattern.DOTALL,"abcbc", new int[]{0, 5, -2, -2});
		check("a(bb+|b)b",0|Pattern.MULTILINE|Pattern.DOTALL,"abb", new int[]{0, 3, -2, -2});
		check("a(bbb+|bb+|b)b",0|Pattern.MULTILINE|Pattern.DOTALL,"abb", new int[]{0, 3, -2, -2});
		check("a(bbb+|bb+|b)b",0|Pattern.MULTILINE|Pattern.DOTALL,"abbb", new int[]{0, 4, -2, -2});
		check("a(bbb+|bb+|b)bb",0|Pattern.MULTILINE|Pattern.DOTALL,"abbb", new int[]{0, 4, -2, -2});
		check("(.*).*",0|Pattern.MULTILINE|Pattern.DOTALL,"abcdef", new int[]{0, 6, -2, 6, 6, -2, -2});
		check("(a*)*",0|Pattern.MULTILINE|Pattern.DOTALL,"bc", new int[]{0, 0, -2, 1, 1, -2, 2, 2, -2, -2});

		report("test_nosubs");
	}

	private static void test_tricky_cases(){
		check("a(((b)))c",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 3, 1, 2, 1, 2, 1, 2, -2, -2});
		check("a(b|(c))d",0|Pattern.MULTILINE|Pattern.DOTALL,"abd", new int[]{0, 3, 1, 2, -1, -1, -2, -2});
		check("a(b|(c))d",0|Pattern.MULTILINE|Pattern.DOTALL,"acd", new int[]{0, 3, 1, 2, 1, 2, -2, -2});
		check("a(b*|c)d",0|Pattern.MULTILINE|Pattern.DOTALL,"abbd", new int[]{0, 4, 1, 3, -2, -2});
		check("a[ab]{20}",0|Pattern.MULTILINE|Pattern.DOTALL,"aaaaabaaaabaaaabaaaab", new int[]{0, 21, -2, -2});
		check("a[ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab]",0|Pattern.MULTILINE|Pattern.DOTALL,"aaaaabaaaabaaaabaaaab", new int[]{0, 21, -2, -2});
		check("a[ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab](wee|week)(knights|night)",0|Pattern.MULTILINE|Pattern.DOTALL,"aaaaabaaaabaaaabaaaabweeknights", new int[]{0, 31, 21, 24, 24, 31, -2, -2});
		check("1234567890123456789012345678901234567890123456789012345678901234567890",0|Pattern.MULTILINE|Pattern.DOTALL,"a1234567890123456789012345678901234567890123456789012345678901234567890b", new int[]{1, 71, -2, -2});
		check("[ab][cd][ef][gh][ij][kl][mn]",0|Pattern.MULTILINE|Pattern.DOTALL,"xacegikmoq", new int[]{1, 8, -2, -2});
		check("[ab][cd][ef][gh][ij][kl][mn][op]",0|Pattern.MULTILINE|Pattern.DOTALL,"xacegikmoq", new int[]{1, 9, -2, -2});
		check("[ab][cd][ef][gh][ij][kl][mn][op][qr]",0|Pattern.MULTILINE|Pattern.DOTALL,"xacegikmoqy", new int[]{1, 10, -2, -2});
		check("[ab][cd][ef][gh][ij][kl][mn][op][q]",0|Pattern.MULTILINE|Pattern.DOTALL,"xacegikmoqy", new int[]{1, 10, -2, -2});
		check("(a)(b)(c)(d)(e)(f)(g)(h)",0|Pattern.MULTILINE|Pattern.DOTALL,"zabcdefghi", new int[]{1, 9, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, -2, -2});
		check("(a)(b)(c)(d)(e)(f)(g)(h)(i)",0|Pattern.MULTILINE|Pattern.DOTALL,"zabcdefghij", new int[]{1, 10, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, -2, -2});
		check("(a)(b)(c)(d)(e)(f)(g)(h)(i)(j)",0|Pattern.MULTILINE|Pattern.DOTALL,"zabcdefghijk", new int[]{1, 11, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, -2, -2});
		check("(a)(b)(c)(d)(e)(f)(g)(h)(i)(j)(k)",0|Pattern.MULTILINE|Pattern.DOTALL,"zabcdefghijkl", new int[]{1, 12, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, -2, -2});
		check("(a)d|(b)c",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{1, 3, -1, -1, 1, 2, -2, -2});
		check("_+((www)|(ftp)|(mailto)):_*",0|Pattern.MULTILINE|Pattern.DOTALL,"_wwwnocolon _mailto:", new int[]{12, 20, 13, 19, -1, -1, -1, -1, 13, 19, -2, -2});
		check("a(b)?c\\1d",0|Pattern.MULTILINE|Pattern.DOTALL,"acd", new int[]{-2, -2});
		check("a(b?c)+d",0|Pattern.MULTILINE|Pattern.DOTALL,"accd", new int[]{0, 4, 2, 3, -2, -2});
		check("(wee|week)(knights|night)",0|Pattern.MULTILINE|Pattern.DOTALL,"weeknights", new int[]{0, 10, 0, 3, 3, 10, -2, -2});
		check(".*",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 3, -2, 3, 3, -2, -2});
		check("a(b|(c))d",0|Pattern.MULTILINE|Pattern.DOTALL,"abd", new int[]{0, 3, 1, 2, -1, -1, -2, -2});
		check("a(b|(c))d",0|Pattern.MULTILINE|Pattern.DOTALL,"acd", new int[]{0, 3, 1, 2, 1, 2, -2, -2});
		check("a(b*|c|e)d",0|Pattern.MULTILINE|Pattern.DOTALL,"abbd", new int[]{0, 4, 1, 3, -2, -2});
		check("a(b*|c|e)d",0|Pattern.MULTILINE|Pattern.DOTALL,"acd", new int[]{0, 3, 1, 2, -2, -2});
		check("a(b*|c|e)d",0|Pattern.MULTILINE|Pattern.DOTALL,"ad", new int[]{0, 2, 1, 1, -2, -2});
		check("a(b?)c",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 3, 1, 2, -2, -2});
		check("a(b?)c",0|Pattern.MULTILINE|Pattern.DOTALL,"ac", new int[]{0, 2, 1, 1, -2, -2});
		check("a(b+)c",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 3, 1, 2, -2, -2});
		check("a(b+)c",0|Pattern.MULTILINE|Pattern.DOTALL,"abbbc", new int[]{0, 5, 1, 4, -2, -2});
		check("a(b*)c",0|Pattern.MULTILINE|Pattern.DOTALL,"ac", new int[]{0, 2, 1, 1, -2, -2});
		check("(a|ab)(bc([de]+)f|cde)",0|Pattern.MULTILINE|Pattern.DOTALL,"abcdef", new int[]{0, 6, 0, 1, 1, 6, 3, 5, -2, -2});
		check("a([bc]?)c",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 3, 1, 2, -2, -2});
		check("a([bc]?)c",0|Pattern.MULTILINE|Pattern.DOTALL,"ac", new int[]{0, 2, 1, 1, -2, -2});
		check("a([bc]+)c",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 3, 1, 2, -2, -2});
		check("a([bc]+)c",0|Pattern.MULTILINE|Pattern.DOTALL,"abcc", new int[]{0, 4, 1, 3, -2, -2});
		check("a([bc]+)bc",0|Pattern.MULTILINE|Pattern.DOTALL,"abcbc", new int[]{0, 5, 1, 3, -2, -2});
		check("a(bb+|b)b",0|Pattern.MULTILINE|Pattern.DOTALL,"abb", new int[]{0, 3, 1, 2, -2, -2});
		check("a(bbb+|bb+|b)b",0|Pattern.MULTILINE|Pattern.DOTALL,"abb", new int[]{0, 3, 1, 2, -2, -2});
		check("a(bbb+|bb+|b)b",0|Pattern.MULTILINE|Pattern.DOTALL,"abbb", new int[]{0, 4, 1, 3, -2, -2});
		check("a(bbb+|bb+|b)bb",0|Pattern.MULTILINE|Pattern.DOTALL,"abbb", new int[]{0, 4, 1, 2, -2, -2});
		check("(.*).*",0|Pattern.MULTILINE|Pattern.DOTALL,"abcdef", new int[]{0, 6, 0, 6, -2, 6, 6, 6, 6, -2, -2});
		check("(a*)*",0|Pattern.MULTILINE|Pattern.DOTALL,"bc", new int[]{0, 0, 0, 0, -2, 1, 1, 1, 1, -2, 2, 2, 2, 2, -2, -2});
		check("Z(((((((a+)+)+)+)+)+)+)+|Y(((((((a+)+)+)+)+)+)+)+|X(((((((a+)+)+)+)+)+)+)+|W(((((((a+)+)+)+)+)+)+)+|V(((((((a+)+)+)+)+)+)+)+|CZ(((((((a+)+)+)+)+)+)+)+|CY(((((((a+)+)+)+)+)+)+)+|CX(((((((a+)+)+)+)+)+)+)+|CW(((((((a+)+)+)+)+)+)+)+|CV(((((((a+)+)+)+)+)+)+)+|(a+)+",0|Pattern.MULTILINE|Pattern.DOTALL,"bc", new int[]{-2, -2});
		check("Z(((((((a+)+)+)+)+)+)+)+|Y(((((((a+)+)+)+)+)+)+)+|X(((((((a+)+)+)+)+)+)+)+|W(((((((a+)+)+)+)+)+)+)+|V(((((((a+)+)+)+)+)+)+)+|CZ(((((((a+)+)+)+)+)+)+)+|CY(((((((a+)+)+)+)+)+)+)+|CX(((((((a+)+)+)+)+)+)+)+|CW(((((((a+)+)+)+)+)+)+)+|CV(((((((a+)+)+)+)+)+)+)+|(a+)+",0|Pattern.MULTILINE|Pattern.DOTALL,"aaa", new int[]{0, 3, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      0, 3,
      -2, -2});
		check("Z(((((((a+)+)+)+)+)+)+)+|Y(((((((a+)+)+)+)+)+)+)+|X(((((((a+)+)+)+)+)+)+)+|W(((((((a+)+)+)+)+)+)+)+|V(((((((a+)+)+)+)+)+)+)+|CZ(((((((a+)+)+)+)+)+)+)+|CY(((((((a+)+)+)+)+)+)+)+|CX(((((((a+)+)+)+)+)+)+)+|CW(((((((a+)+)+)+)+)+)+)+|CV(((((((a+)+)+)+)+)+)+)+|(a+)+",0|Pattern.MULTILINE|Pattern.DOTALL,"Zaaa", new int[]{0, 4, 
      1, 4, 1, 4, 1, 4, 1, 4, 1, 4, 1, 4, 1, 4, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1,
      -2, -2});
		check("xyx*xz",0|Pattern.MULTILINE|Pattern.DOTALL,"xyxxxxyxxxz", new int[]{5, 11, -2, -2});
		check("a(b|c)*d",0|Pattern.MULTILINE|Pattern.DOTALL,"ad", new int[]{0, 2, -1, -1, -2, -2});
		check("a(b|c)*d",0|Pattern.MULTILINE|Pattern.DOTALL,"abcd", new int[]{0, 4, 2, 3, -2, -2});
		check("a(b|c)+d",0|Pattern.MULTILINE|Pattern.DOTALL,"abd", new int[]{0, 3, 1, 2, -2, -2});
		check("a(b|c)+d",0|Pattern.MULTILINE|Pattern.DOTALL,"abcd", new int[]{0, 4, 2, 3, -2, -2});
		check("a(b|c?)+d",0|Pattern.MULTILINE|Pattern.DOTALL,"ad", new int[]{0, 2, 1, 1, -2, -2});
		check("a(b|c){0,0}d",0|Pattern.MULTILINE|Pattern.DOTALL,"ad", new int[]{0, 2, -1, -1, -2, -2});
		check("a(b|c){0,1}d",0|Pattern.MULTILINE|Pattern.DOTALL,"ad", new int[]{0, 2, -1, -1, -2, -2});
		check("a(b|c){0,1}d",0|Pattern.MULTILINE|Pattern.DOTALL,"abd", new int[]{0, 3, 1, 2, -2, -2});
		check("a(b|c){0,2}d",0|Pattern.MULTILINE|Pattern.DOTALL,"ad", new int[]{0, 2, -1, -1, -2, -2});
		check("a(b|c){0,2}d",0|Pattern.MULTILINE|Pattern.DOTALL,"abcd", new int[]{0, 4, 2, 3, -2, -2});
		check("a(b|c){0,}d",0|Pattern.MULTILINE|Pattern.DOTALL,"ad", new int[]{0, 2, -1, -1, -2, -2});
		check("a(b|c){0,}d",0|Pattern.MULTILINE|Pattern.DOTALL,"abcd", new int[]{0, 4, 2, 3, -2, -2});
		check("a(b|c){1,1}d",0|Pattern.MULTILINE|Pattern.DOTALL,"abd", new int[]{0, 3, 1, 2, -2, -2});
		check("a(b|c){1,2}d",0|Pattern.MULTILINE|Pattern.DOTALL,"abd", new int[]{0, 3, 1, 2, -2, -2});
		check("a(b|c){1,2}d",0|Pattern.MULTILINE|Pattern.DOTALL,"abcd", new int[]{0, 4, 2, 3, -2, -2});
		check("a(b|c){1,}d",0|Pattern.MULTILINE|Pattern.DOTALL,"abd", new int[]{0, 3, 1, 2, -2, -2});
		check("a(b|c){1,}d",0|Pattern.MULTILINE|Pattern.DOTALL,"abcd", new int[]{0, 4, 2, 3, -2, -2});
		check("a(b|c){2,2}d",0|Pattern.MULTILINE|Pattern.DOTALL,"acbd", new int[]{0, 4, 2, 3, -2, -2});
		check("a(b|c){2,2}d",0|Pattern.MULTILINE|Pattern.DOTALL,"abcd", new int[]{0, 4, 2, 3, -2, -2});
		check("a(b|c){2,4}d",0|Pattern.MULTILINE|Pattern.DOTALL,"abcd", new int[]{0, 4, 2, 3, -2, -2});
		check("a(b|c){2,4}d",0|Pattern.MULTILINE|Pattern.DOTALL,"abcbd", new int[]{0, 5, 3, 4, -2, -2});
		check("a(b|c){2,4}d",0|Pattern.MULTILINE|Pattern.DOTALL,"abcbcd", new int[]{0, 6, 4, 5, -2, -2});
		check("a(b|c){2,}d",0|Pattern.MULTILINE|Pattern.DOTALL,"abcd", new int[]{0, 4, 2, 3, -2, -2});
		check("a(b|c){2,}d",0|Pattern.MULTILINE|Pattern.DOTALL,"abcbd", new int[]{0, 5, 3, 4, -2, -2});

		report("test_tricky_cases");
	}

	private static void test_tricky_cases2(){
		check("a(b|c?)+d",0|Pattern.MULTILINE|Pattern.DOTALL,"abcd", new int[]{0, 4, 3, 3, -2, -2});
		check("a(b+|((c)*))+d",0|Pattern.MULTILINE|Pattern.DOTALL,"abd", new int[]{0, 3, 2, 2, 2, 2, -1, -1, -2, -2});
		check("a(b+|((c)*))+d",0|Pattern.MULTILINE|Pattern.DOTALL,"abcd", new int[]{0, 4, 3, 3, 3, 3, 2, 3, -2, -2});
		check("//[^\\n]*",0|Pattern.MULTILINE|Pattern.DOTALL,"++i //here is a line comment\n", new int[]{4, 28, -2, -2});
		check("/\\*([^*]|\\*+[^*/])*\\*+/",0|Pattern.MULTILINE|Pattern.DOTALL,"/* here is a block comment */", new int[]{0, 29, 26, 27, -2, -2});
		check("/\\*([^*]|\\*+[^*/])*\\*+/",0|Pattern.MULTILINE|Pattern.DOTALL,"/**/", new int[]{0, 4, -1, -1, -2, -2});
		check("/\\*([^*]|\\*+[^*/])*\\*+/",0|Pattern.MULTILINE|Pattern.DOTALL,"/***/", new int[]{0, 5, -1, -1, -2, -2});
		check("/\\*([^*]|\\*+[^*/])*\\*+/",0|Pattern.MULTILINE|Pattern.DOTALL,"/****/", new int[]{0, 6, -1, -1, -2, -2});
		check("/\\*([^*]|\\*+[^*/])*\\*+/",0|Pattern.MULTILINE|Pattern.DOTALL,"/*****/", new int[]{0, 7, -1, -1, -2, -2});
		check("/\\*([^*]|\\*+[^*/])*\\*+/",0|Pattern.MULTILINE|Pattern.DOTALL,"/*****/*/", new int[]{0, 7, -1, -1, -2, -2});
		check("^\\p{Blank}*#([^\\n]*\\\\\\p{Space}+)*[^\\n]*",0|Pattern.MULTILINE|Pattern.DOTALL,"#define some_symbol", new int[]{0, 19, -1, -1, -2, -2});
		check("^\\p{Blank}*#([^\\n]*\\\\\\p{Space}+)*[^\\n]*",0|Pattern.MULTILINE|Pattern.DOTALL,"#define some_symbol(x) #x", new int[]{0, 25, -1, -1, -2, -2});
		check("^\\p{Blank}*#([^\\n]*\\\\\\p{Space}+)*[^\\n]*",0|Pattern.MULTILINE|Pattern.DOTALL,"#define some_symbol(x) \\  \r\n  foo();\\\r\n   printf(#x);", new int[]{0, 53, 30, 42, -2, -2});

		report("test_tricky_cases2");
	}

	private static void test_tricky_cases3(){
		check("((0x\\p{XDigit}+)|(\\p{Digit}+))u?((int(8|16|32|64))|L)?",0|Pattern.MULTILINE|Pattern.DOTALL,"0xFF", new int[]{0, 4, 0, 4, 0, 4, -1, -1, -1, -1, -1, -1, -1, -1, -2, -2});
		check("((0x\\p{XDigit}+)|(\\p{Digit}+))u?((int(8|16|32|64))|L)?",0|Pattern.MULTILINE|Pattern.DOTALL,"35", new int[]{0, 2, 0, 2, -1, -1, 0, 2, -1, -1, -1, -1, -1, -1, -2, -2});
		check("((0x\\p{XDigit}+)|(\\p{Digit}+))u?((int(8|16|32|64))|L)?",0|Pattern.MULTILINE|Pattern.DOTALL,"0xFFu", new int[]{0, 5, 0, 4, 0, 4, -1, -1, -1, -1, -1, -1, -1, -1, -2, -2});
		check("((0x\\p{XDigit}+)|(\\p{Digit}+))u?((int(8|16|32|64))|L)?",0|Pattern.MULTILINE|Pattern.DOTALL,"0xFFL", new int[]{0, 5, 0, 4, 0, 4, -1, -1, 4, 5, -1, -1, -1, -1, -2, -2});
		check("((0x\\p{XDigit}+)|(\\p{Digit}+))u?((int(8|16|32|64))|L)?",0|Pattern.MULTILINE|Pattern.DOTALL,"0xFFFFFFFFFFFFFFFFuint64", new int[]{0, 24, 0, 18, 0, 18, -1, -1, 19, 24, 19, 24, 22, 24, -2, -2});
		check("'([^\\\\']|\\\\.)*'",0|Pattern.MULTILINE|Pattern.DOTALL,"'\\x3A'", new int[]{0, 6, 4, 5, -2, -2});
		check("'([^\\\\']|\\\\.)*'",0|Pattern.MULTILINE|Pattern.DOTALL,"'\\''", new int[]{0, 4, 1, 3, -2, -2});
		check("'([^\\\\']|\\\\.)*'",0|Pattern.MULTILINE|Pattern.DOTALL,"'\\n'", new int[]{0, 4, 1, 3, -2, -2});
		check("0123456789@abcdefghijklmnopqrstuvwxyz\\[\\\\\\]\\^_`ABCDEFGHIJKLMNOPQRSTUVWXYZ\\{\\|\\}",0|Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL,"0123456789@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}", new int[]{0, 72, -2, -2});
		check("a",0|Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL,"A", new int[]{0, 1, -2, -2});
		check("A",0|Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL,"a", new int[]{0, 1, -2, -2});
		check("[abc]+",0|Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL,"abcABC", new int[]{0, 6, -2, -2});
		check("[ABC]+",0|Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL,"abcABC", new int[]{0, 6, -2, -2});
		check("[a-z]+",0|Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL,"abcABC", new int[]{0, 6, -2, -2});
		check("[A-Z]+",0|Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL,"abzANZ", new int[]{0, 6, -2, -2});
		check("[A-z]+",0|Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL,"abzABZ", new int[]{0, 6, -2, -2});
		check("\\w+",0|Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL,"abcZZZ", new int[]{0, 6, -2, -2});
		check("\\p{Alpha}+",0|Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL,"abyzABYZ", new int[]{0, 8, -2, -2});
		check("\\p{Alnum}+",0|Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL,"09abyzABYZ", new int[]{0, 10, -2, -2});
		check("\\(",0|Pattern.MULTILINE|Pattern.DOTALL,"(", new int[]{0, 1, -2, -2});
		check("\\)",0|Pattern.MULTILINE|Pattern.DOTALL,")", new int[]{0, 1, -2, -2});
		check("\\$",0|Pattern.MULTILINE|Pattern.DOTALL,"$", new int[]{0, 1, -2, -2});
		check("\\^",0|Pattern.MULTILINE|Pattern.DOTALL,"^", new int[]{0, 1, -2, -2});
		check("\\.",0|Pattern.MULTILINE|Pattern.DOTALL,".", new int[]{0, 1, -2, -2});
		check("\\*",0|Pattern.MULTILINE|Pattern.DOTALL,"*", new int[]{0, 1, -2, -2});
		check("\\+",0|Pattern.MULTILINE|Pattern.DOTALL,"+", new int[]{0, 1, -2, -2});
		check("\\?",0|Pattern.MULTILINE|Pattern.DOTALL,"?", new int[]{0, 1, -2, -2});
		check("\\[",0|Pattern.MULTILINE|Pattern.DOTALL,"[", new int[]{0, 1, -2, -2});
		check("\\]",0|Pattern.MULTILINE|Pattern.DOTALL,"]", new int[]{0, 1, -2, -2});
		check("\\|",0|Pattern.MULTILINE|Pattern.DOTALL,"|", new int[]{0, 1, -2, -2});
		check("\\\\",0|Pattern.MULTILINE|Pattern.DOTALL,"\\", new int[]{0, 1, -2, -2});
		check("#",0|Pattern.MULTILINE|Pattern.DOTALL,"#", new int[]{0, 1, -2, -2});
		check("\\#",0|Pattern.MULTILINE|Pattern.DOTALL,"#", new int[]{0, 1, -2, -2});
		check("a-",0|Pattern.MULTILINE|Pattern.DOTALL,"a-", new int[]{0, 2, -2, -2});
		check("\\-",0|Pattern.MULTILINE|Pattern.DOTALL,"-", new int[]{0, 1, -2, -2});
		check("\\{",0|Pattern.MULTILINE|Pattern.DOTALL,"{", new int[]{0, 1, -2, -2});
		check("\\}",0|Pattern.MULTILINE|Pattern.DOTALL,"}", new int[]{0, 1, -2, -2});
		check("0",0|Pattern.MULTILINE|Pattern.DOTALL,"0", new int[]{0, 1, -2, -2});
		check("1",0|Pattern.MULTILINE|Pattern.DOTALL,"1", new int[]{0, 1, -2, -2});
		check("9",0|Pattern.MULTILINE|Pattern.DOTALL,"9", new int[]{0, 1, -2, -2});
		check("b",0|Pattern.MULTILINE|Pattern.DOTALL,"b", new int[]{0, 1, -2, -2});
		check("B",0|Pattern.MULTILINE|Pattern.DOTALL,"B", new int[]{0, 1, -2, -2});
		check("\\<",0|Pattern.MULTILINE|Pattern.DOTALL,"<", new int[]{0, 1, -2, -2});
		check("\\>",0|Pattern.MULTILINE|Pattern.DOTALL,">", new int[]{0, 1, -2, -2});
		check("w",0|Pattern.MULTILINE|Pattern.DOTALL,"w", new int[]{0, 1, -2, -2});
		check("W",0|Pattern.MULTILINE|Pattern.DOTALL,"W", new int[]{0, 1, -2, -2});
		check("`",0|Pattern.MULTILINE|Pattern.DOTALL,"`", new int[]{0, 1, -2, -2});
		check(" ",0|Pattern.MULTILINE|Pattern.DOTALL," ", new int[]{0, 1, -2, -2});
		check("\\n",0|Pattern.MULTILINE|Pattern.DOTALL,"\n", new int[]{0, 1, -2, -2});
		check(",",0|Pattern.MULTILINE|Pattern.DOTALL,",", new int[]{0, 1, -2, -2});
		check("a",0|Pattern.MULTILINE|Pattern.DOTALL,"a", new int[]{0, 1, -2, -2});
		check("f",0|Pattern.MULTILINE|Pattern.DOTALL,"f", new int[]{0, 1, -2, -2});
		check("n",0|Pattern.MULTILINE|Pattern.DOTALL,"n", new int[]{0, 1, -2, -2});
		check("r",0|Pattern.MULTILINE|Pattern.DOTALL,"r", new int[]{0, 1, -2, -2});
		check("t",0|Pattern.MULTILINE|Pattern.DOTALL,"t", new int[]{0, 1, -2, -2});
		check("v",0|Pattern.MULTILINE|Pattern.DOTALL,"v", new int[]{0, 1, -2, -2});
		check("c",0|Pattern.MULTILINE|Pattern.DOTALL,"c", new int[]{0, 1, -2, -2});
		check("x",0|Pattern.MULTILINE|Pattern.DOTALL,"x", new int[]{0, 1, -2, -2});
		check(":",0|Pattern.MULTILINE|Pattern.DOTALL,":", new int[]{0, 1, -2, -2});
		check("(\\.\\p{Alnum}+){2}",0|Pattern.MULTILINE|Pattern.DOTALL,"w.a.b ", new int[]{1, 5, 3, 5, -2, -2});
		check("(?!foo)bar",0|Pattern.MULTILINE|Pattern.DOTALL,"foobar", new int[]{3, 6, -2, -2});
		check("(?!foo)bar",0|Pattern.MULTILINE|Pattern.DOTALL,"??bar", new int[]{2, 5, -2, -2});
		check("(?!foo)bar",0|Pattern.MULTILINE|Pattern.DOTALL,"barfoo", new int[]{0, 3, -2, -2});
		check("(?!foo)bar",0|Pattern.MULTILINE|Pattern.DOTALL,"bar??", new int[]{0, 3, -2, -2});
		check("(?!foo)bar",0|Pattern.MULTILINE|Pattern.DOTALL,"bar", new int[]{0, 3, -2, -2});
		check("a\\Z",0|Pattern.MULTILINE|Pattern.DOTALL,"a\nb", new int[]{-2, -2});
		check("()",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 0, 0, 0, -2, 1, 1, 1, 1, -2, 2, 2, 2, 2, -2, 3, 3, 3, 3, -2, -2});
		check("^()",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 0, 0, 0, -2, -2});
		check("^()+",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 0, 0, 0, -2, -2});
		check("^(){1}",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 0, 0, 0, -2, -2});
		check("^(){2}",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 0, 0, 0, -2, -2});
		check("^((){2})",0|Pattern.MULTILINE|Pattern.DOTALL,"abc", new int[]{0, 0, 0, 0, 0, 0, -2, -2});
		check("()",0|Pattern.MULTILINE|Pattern.DOTALL,"", new int[]{0, 0, 0, 0, -2, -2});
		check("()\\1",0|Pattern.MULTILINE|Pattern.DOTALL,"", new int[]{0, 0, 0, 0, -2, -2});
		check("()\\1",0|Pattern.MULTILINE|Pattern.DOTALL,"a", new int[]{0, 0, 0, 0, -2, 1, 1, 1, 1, -2, -2});
		check("a()\\1b",0|Pattern.MULTILINE|Pattern.DOTALL,"ab", new int[]{0, 2, 1, 1, -2, -2});
		check("a()b\\1",0|Pattern.MULTILINE|Pattern.DOTALL,"ab", new int[]{0, 2, 1, 1, -2, -2});
		check("([a-c]+)\\1",0|Pattern.MULTILINE|Pattern.DOTALL,"abcbc", new int[]{1, 5, 1, 3, -2, -2});
		check(".+abc",0|Pattern.MULTILINE|Pattern.DOTALL,"xxxxxxxxyyyyyyyyab", new int[]{-2, -2});
		check("(.+)\\1",0|Pattern.MULTILINE|Pattern.DOTALL,"abcdxxxyyyxxxyyy", new int[]{4, 16, 4, 10, -2, -2});
		check("[_]+$",0|Pattern.MULTILINE|Pattern.DOTALL,"___________________________________________x", new int[]{-2, -2});
		check("(a)(?:b)",0|Pattern.MULTILINE|Pattern.DOTALL,"ab", new int[]{0, 2, -2, -2});
		check("(?:\\d{9}.*){2}",0|Pattern.MULTILINE|Pattern.DOTALL,"123456789dfsdfsdfsfsdfds123456789b", new int[]{0, 34, -2, -2});
		check("(?:\\d{9}.*){2}",0|Pattern.MULTILINE|Pattern.DOTALL,"123456789dfsdfsdfsfsdfds12345678", new int[]{-2, -2});
		check("(?:\\d{9}.*){2}",0|Pattern.MULTILINE|Pattern.DOTALL,"123456789dfsdfsdfsfsdfds", new int[]{-2, -2});
		check("^(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]?[0-9])){3}$",0|Pattern.MULTILINE|Pattern.DOTALL,"1.2.03", new int[]{-2, -2});
		check("^(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]?[0-9])){3,4}$",0|Pattern.MULTILINE|Pattern.DOTALL,"1.2.03", new int[]{-2, -2});
		check("^(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]?[0-9])){3,4}?$",0|Pattern.MULTILINE|Pattern.DOTALL,"1.2.03", new int[]{-2, -2});

		report("test_tricky_cases3");
	}

	private static void test_alt(){
		check("a|b",0|Pattern.MULTILINE|Pattern.DOTALL,"a", new int[]{0, 1, -2, -2});
		check("a|b",0|Pattern.MULTILINE|Pattern.DOTALL,"b", new int[]{0, 1, -2, -2});
		check("a|b|c",0|Pattern.MULTILINE|Pattern.DOTALL,"c", new int[]{0, 1, -2, -2});
		check("a|(b)|.",0|Pattern.MULTILINE|Pattern.DOTALL,"b", new int[]{0, 1, 0, 1, -2, -2});
		check("(a)|b|.",0|Pattern.MULTILINE|Pattern.DOTALL,"a", new int[]{0, 1, 0, 1, -2, -2});
		check("a(b|c)",0|Pattern.MULTILINE|Pattern.DOTALL,"ab", new int[]{0, 2, 1, 2, -2, -2});
		check("a(b|c)",0|Pattern.MULTILINE|Pattern.DOTALL,"ac", new int[]{0, 2, 1, 2, -2, -2});
		check("a(b|c)",0|Pattern.MULTILINE|Pattern.DOTALL,"ad", new int[]{-2, -2});
		check("(a|b|c)",0|Pattern.MULTILINE|Pattern.DOTALL,"c", new int[]{0, 1, 0, 1, -2, -2});
		check("(a|(b)|.)",0|Pattern.MULTILINE|Pattern.DOTALL,"b", new int[]{0, 1, 0, 1, 0, 1, -2, -2});
		check("c|",0|Pattern.MULTILINE|Pattern.DOTALL," c", new int[]{0, 0, -2, 1, 2, -2, 2, 2, -2, -2});
		check("(|)",0|Pattern.MULTILINE|Pattern.DOTALL," c", new int[]{0, 0, 0, 0, -2, 1, 1, 1, 1, -2, 2, 2, 2, 2, -2, -2});
		check("(a|)",0|Pattern.MULTILINE|Pattern.DOTALL," a", new int[]{0, 0, 0, 0, -2, 1, 2, 1, 2, -2, 2, 2, 2, 2, -2, -2});
		check("a\\|",0|Pattern.MULTILINE|Pattern.DOTALL,"a|", new int[]{0, 2, -2, -2});

		report("test_alt");
	}

}
