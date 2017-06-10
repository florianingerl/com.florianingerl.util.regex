package com.florianingerl.util.regex.tests;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;
import com.florianingerl.util.regex.PatternSyntaxException;

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

	public static void main(String[] args) {
		basic_tests();
		test_non_marking_paren();
		test_partial_match();
		test_nosubs();
		test_tricky_cases();
		test_tricky_cases2();
		test_tricky_cases3();
		test_alt();
		test_simple_repeats();
		test_simple_repeats2();
		test_fast_repeats();
		test_fast_repeats2();
		test_pocessive_repeats();
		test_independent_subs();
		test_conditionals();
		test_options();
		test_options2();
		test_options3();
		test_mark_resets();
		test_recursion();
		test_non_greedy_repeats();
		test_grep();
		test_backrefs();
		if (failure) {
			throw new RuntimeException("BoostRegExTest failed, 1st failure: " + firstFailure);
		} else {
			System.err.println("OKAY: All tests passed.");
		}
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

	private static void check(String regex, int flags, String s, int[] data) {
		int save = failCount;
		try {
			innerCheck(regex, flags, s, data);
		} catch (Exception e) {
			++failCount;
		}
		if (failCount > save) {
			System.err.println("Regex=\"" + regex + "\"String=\"" + s + "\"");
		}
	}

	private static void innerCheck(String regex, int flags, String s, int[] data) {
		Pattern p = Pattern.compile(regex, flags);
		Matcher m = p.matcher(s);
		int i = 0;
		while (data[i] != -2) {
			if (!m.find()) {
				++failCount;
				return;
			}
			int j = 0;
			while (data[i] != -2) {
				if (m.start(j) != data[i++] || m.end(j) != data[i++]) {
					++failCount;
				}
				++j;
			}
			++i;
		}
		if (m.find())
			++failCount;
	}

	private static void checkExpectedFail(String p, int flags) {
		try {
			Pattern.compile(p, flags);
		} catch (PatternSyntaxException pse) {
			return;
		}
		failCount++;
	}

	private static void basic_tests() {
		check("Z", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaa", new int[] { -2, -2 });
		check("Z", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xxxxZZxxx", new int[] { 4, 5, -2, 5, 6, -2, -2 });
		check("(a)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "zzzaazz",
				new int[] { 3, 4, 3, 4, -2, 4, 5, 4, 5, -2, -2 });
		check("()", 0 | Pattern.MULTILINE | Pattern.DOTALL, "zzz",
				new int[] { 0, 0, 0, 0, -2, 1, 1, 1, 1, -2, 2, 2, 2, 2, -2, 3, 3, 3, 3, -2, -2 });
		check("()", 0 | Pattern.MULTILINE | Pattern.DOTALL, "", new int[] { 0, 0, 0, 0, -2, -2 });
		check("", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc",
				new int[] { 0, 0, -2, 1, 1, -2, 2, 2, -2, 3, 3, -2, -2 });
		check("a", 0 | Pattern.MULTILINE | Pattern.DOTALL, "b", new int[] { -2, -2 });
		check("\\(\\)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "()", new int[] { 0, 2, -2, -2 });
		check("\\(a\\)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "(a)", new int[] { 0, 3, -2, -2 });
		check("p(a)rameter", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ABCparameterXYZ",
				new int[] { 3, 12, 4, 5, -2, -2 });
		check("[pq](a)rameter", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ABCparameterXYZ",
				new int[] { 3, 12, 4, 5, -2, -2 });
		check(".", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { 0, 1, -2, -2 });
		check(".", 0 | Pattern.MULTILINE | Pattern.DOTALL, "\n", new int[] { 0, 1, -2, -2 });
		check(".", 0 | Pattern.MULTILINE | Pattern.DOTALL, "\r", new int[] { 0, 1, -2, -2 });
		check(".", 0 | Pattern.MULTILINE | Pattern.DOTALL, "\0", new int[] { 0, 1, -2, -2 });
		check(".", 0 | Pattern.MULTILINE, "a", new int[] { 0, 1, -2, -2 });
		check(".", 0 | Pattern.MULTILINE, "\n", new int[] { -2, -2 });
		check(".", 0 | Pattern.MULTILINE, "\r", new int[] { -2, -2 });
		check(".", 0 | Pattern.MULTILINE, "\0", new int[] { 0, 1, -2, -2 });

		report("basic_tests");
	}

	private static void test_non_marking_paren() {
		check("(?:abc)+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xxabcabcxx", new int[] { 2, 8, -2, -2 });
		check("(?:a+)(b+)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xaaabbbx", new int[] { 1, 7, 4, 7, -2, -2 });
		check("(a+)(?:b+)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xaaabbba", new int[] { 1, 7, 1, 4, -2, -2 });
		check("(?:(a+)b+)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xaaabbba", new int[] { 1, 7, 1, 4, -2, -2 });
		check("(?:a+(b+))", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xaaabbba", new int[] { 1, 7, 4, 7, -2, -2 });
		check("a+(?x:#b+\n)b+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xaaabbba", new int[] { 1, 7, -2, -2 });
		check("(a)(?:b|$)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, 0, 1, -2, -2 });
		check("(a)(?:b|$)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { 0, 1, 0, 1, -2, -2 });

		report("test_non_marking_paren");
	}

	private static void test_partial_match() {

		report("test_partial_match");
	}

	private static void test_nosubs() {
		check("a(b?c)+d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "accd", new int[] { 0, 4, -2, -2 });
		check("(wee|week)(knights|night)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "weeknights",
				new int[] { 0, 10, -2, -2 });
		check(".*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 3, -2, 3, 3, -2, -2 });
		check("a(b|(c))d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abd", new int[] { 0, 3, -2, -2 });
		check("a(b|(c))d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "acd", new int[] { 0, 3, -2, -2 });
		check("a(b*|c|e)d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbd", new int[] { 0, 4, -2, -2 });
		check("a(b*|c|e)d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "acd", new int[] { 0, 3, -2, -2 });
		check("a(b*|c|e)d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ad", new int[] { 0, 2, -2, -2 });
		check("a(b?)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 3, -2, -2 });
		check("a(b?)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ac", new int[] { 0, 2, -2, -2 });
		check("a(b+)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 3, -2, -2 });
		check("a(b+)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbbc", new int[] { 0, 5, -2, -2 });
		check("a(b*)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ac", new int[] { 0, 2, -2, -2 });
		check("(a|ab)(bc([de]+)f|cde)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcdef", new int[] { 0, 6, -2, -2 });
		check("a([bc]?)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 3, -2, -2 });
		check("a([bc]?)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ac", new int[] { 0, 2, -2, -2 });
		check("a([bc]+)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 3, -2, -2 });
		check("a([bc]+)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcc", new int[] { 0, 4, -2, -2 });
		check("a([bc]+)bc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcbc", new int[] { 0, 5, -2, -2 });
		check("a(bb+|b)b", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abb", new int[] { 0, 3, -2, -2 });
		check("a(bbb+|bb+|b)b", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abb", new int[] { 0, 3, -2, -2 });
		check("a(bbb+|bb+|b)b", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbb", new int[] { 0, 4, -2, -2 });
		check("a(bbb+|bb+|b)bb", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbb", new int[] { 0, 4, -2, -2 });
		check("(.*).*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcdef", new int[] { 0, 6, -2, 6, 6, -2, -2 });
		check("(a*)*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "bc", new int[] { 0, 0, -2, 1, 1, -2, 2, 2, -2, -2 });

		report("test_nosubs");
	}

	private static void test_tricky_cases() {
		check("a(((b)))c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 3, 1, 2, 1, 2, 1, 2, -2, -2 });
		check("a(b|(c))d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abd", new int[] { 0, 3, 1, 2, -1, -1, -2, -2 });
		check("a(b|(c))d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "acd", new int[] { 0, 3, 1, 2, 1, 2, -2, -2 });
		check("a(b*|c)d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbd", new int[] { 0, 4, 1, 3, -2, -2 });
		check("a[ab]{20}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaaabaaaabaaaabaaaab",
				new int[] { 0, 21, -2, -2 });
		check("a[ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab]",
				0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaaabaaaabaaaabaaaab", new int[] { 0, 21, -2, -2 });
		check("a[ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab][ab](wee|week)(knights|night)",
				0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaaabaaaabaaaabaaaabweeknights",
				new int[] { 0, 31, 21, 24, 24, 31, -2, -2 });
		check("1234567890123456789012345678901234567890123456789012345678901234567890",
				0 | Pattern.MULTILINE | Pattern.DOTALL,
				"a1234567890123456789012345678901234567890123456789012345678901234567890b",
				new int[] { 1, 71, -2, -2 });
		check("[ab][cd][ef][gh][ij][kl][mn]", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xacegikmoq",
				new int[] { 1, 8, -2, -2 });
		check("[ab][cd][ef][gh][ij][kl][mn][op]", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xacegikmoq",
				new int[] { 1, 9, -2, -2 });
		check("[ab][cd][ef][gh][ij][kl][mn][op][qr]", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xacegikmoqy",
				new int[] { 1, 10, -2, -2 });
		check("[ab][cd][ef][gh][ij][kl][mn][op][q]", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xacegikmoqy",
				new int[] { 1, 10, -2, -2 });
		check("(a)(b)(c)(d)(e)(f)(g)(h)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "zabcdefghi",
				new int[] { 1, 9, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, -2, -2 });
		check("(a)(b)(c)(d)(e)(f)(g)(h)(i)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "zabcdefghij",
				new int[] { 1, 10, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, -2, -2 });
		check("(a)(b)(c)(d)(e)(f)(g)(h)(i)(j)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "zabcdefghijk",
				new int[] { 1, 11, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, -2, -2 });
		check("(a)(b)(c)(d)(e)(f)(g)(h)(i)(j)(k)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "zabcdefghijkl",
				new int[] { 1, 12, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, -2, -2 });
		check("(a)d|(b)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 1, 3, -1, -1, 1, 2, -2, -2 });
		check("_+((www)|(ftp)|(mailto)):_*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "_wwwnocolon _mailto:",
				new int[] { 12, 20, 13, 19, -1, -1, -1, -1, 13, 19, -2, -2 });
		check("a(b)?c\\1d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "acd", new int[] { -2, -2 });
		check("a(b?c)+d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "accd", new int[] { 0, 4, 2, 3, -2, -2 });
		check("(wee|week)(knights|night)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "weeknights",
				new int[] { 0, 10, 0, 3, 3, 10, -2, -2 });
		check(".*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 3, -2, 3, 3, -2, -2 });
		check("a(b|(c))d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abd", new int[] { 0, 3, 1, 2, -1, -1, -2, -2 });
		check("a(b|(c))d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "acd", new int[] { 0, 3, 1, 2, 1, 2, -2, -2 });
		check("a(b*|c|e)d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbd", new int[] { 0, 4, 1, 3, -2, -2 });
		check("a(b*|c|e)d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "acd", new int[] { 0, 3, 1, 2, -2, -2 });
		check("a(b*|c|e)d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ad", new int[] { 0, 2, 1, 1, -2, -2 });
		check("a(b?)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 3, 1, 2, -2, -2 });
		check("a(b?)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ac", new int[] { 0, 2, 1, 1, -2, -2 });
		check("a(b+)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 3, 1, 2, -2, -2 });
		check("a(b+)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbbc", new int[] { 0, 5, 1, 4, -2, -2 });
		check("a(b*)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ac", new int[] { 0, 2, 1, 1, -2, -2 });
		check("(a|ab)(bc([de]+)f|cde)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcdef",
				new int[] { 0, 6, 0, 1, 1, 6, 3, 5, -2, -2 });
		check("a([bc]?)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 3, 1, 2, -2, -2 });
		check("a([bc]?)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ac", new int[] { 0, 2, 1, 1, -2, -2 });
		check("a([bc]+)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 3, 1, 2, -2, -2 });
		check("a([bc]+)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcc", new int[] { 0, 4, 1, 3, -2, -2 });
		check("a([bc]+)bc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcbc", new int[] { 0, 5, 1, 3, -2, -2 });
		check("a(bb+|b)b", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abb", new int[] { 0, 3, 1, 2, -2, -2 });
		check("a(bbb+|bb+|b)b", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abb", new int[] { 0, 3, 1, 2, -2, -2 });
		check("a(bbb+|bb+|b)b", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbb", new int[] { 0, 4, 1, 3, -2, -2 });
		check("a(bbb+|bb+|b)bb", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbb", new int[] { 0, 4, 1, 2, -2, -2 });
		check("(.*).*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcdef",
				new int[] { 0, 6, 0, 6, -2, 6, 6, 6, 6, -2, -2 });
		check("(a*)*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "bc",
				new int[] { 0, 0, 0, 0, -2, 1, 1, 1, 1, -2, 2, 2, 2, 2, -2, -2 });
		check("Z(((((((a+)+)+)+)+)+)+)+|Y(((((((a+)+)+)+)+)+)+)+|X(((((((a+)+)+)+)+)+)+)+|W(((((((a+)+)+)+)+)+)+)+|V(((((((a+)+)+)+)+)+)+)+|CZ(((((((a+)+)+)+)+)+)+)+|CY(((((((a+)+)+)+)+)+)+)+|CX(((((((a+)+)+)+)+)+)+)+|CW(((((((a+)+)+)+)+)+)+)+|CV(((((((a+)+)+)+)+)+)+)+|(a+)+",
				0 | Pattern.MULTILINE | Pattern.DOTALL, "bc", new int[] { -2, -2 });
		check("Z(((((((a+)+)+)+)+)+)+)+|Y(((((((a+)+)+)+)+)+)+)+|X(((((((a+)+)+)+)+)+)+)+|W(((((((a+)+)+)+)+)+)+)+|V(((((((a+)+)+)+)+)+)+)+|CZ(((((((a+)+)+)+)+)+)+)+|CY(((((((a+)+)+)+)+)+)+)+|CX(((((((a+)+)+)+)+)+)+)+|CW(((((((a+)+)+)+)+)+)+)+|CV(((((((a+)+)+)+)+)+)+)+|(a+)+",
				0 | Pattern.MULTILINE | Pattern.DOTALL, "aaa",
				new int[] { 0, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0,
						3, -2, -2 });
		check("Z(((((((a+)+)+)+)+)+)+)+|Y(((((((a+)+)+)+)+)+)+)+|X(((((((a+)+)+)+)+)+)+)+|W(((((((a+)+)+)+)+)+)+)+|V(((((((a+)+)+)+)+)+)+)+|CZ(((((((a+)+)+)+)+)+)+)+|CY(((((((a+)+)+)+)+)+)+)+|CX(((((((a+)+)+)+)+)+)+)+|CW(((((((a+)+)+)+)+)+)+)+|CV(((((((a+)+)+)+)+)+)+)+|(a+)+",
				0 | Pattern.MULTILINE | Pattern.DOTALL, "Zaaa",
				new int[] { 0, 4, 1, 4, 1, 4, 1, 4, 1, 4, 1, 4, 1, 4, 1, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2, -2 });
		check("xyx*xz", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xyxxxxyxxxz", new int[] { 5, 11, -2, -2 });
		check("a(b|c)*d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ad", new int[] { 0, 2, -1, -1, -2, -2 });
		check("a(b|c)*d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcd", new int[] { 0, 4, 2, 3, -2, -2 });
		check("a(b|c)+d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abd", new int[] { 0, 3, 1, 2, -2, -2 });
		check("a(b|c)+d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcd", new int[] { 0, 4, 2, 3, -2, -2 });
		check("a(b|c?)+d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ad", new int[] { 0, 2, 1, 1, -2, -2 });
		check("a(b|c){0,0}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ad", new int[] { 0, 2, -1, -1, -2, -2 });
		check("a(b|c){0,1}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ad", new int[] { 0, 2, -1, -1, -2, -2 });
		check("a(b|c){0,1}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abd", new int[] { 0, 3, 1, 2, -2, -2 });
		check("a(b|c){0,2}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ad", new int[] { 0, 2, -1, -1, -2, -2 });
		check("a(b|c){0,2}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcd", new int[] { 0, 4, 2, 3, -2, -2 });
		check("a(b|c){0,}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ad", new int[] { 0, 2, -1, -1, -2, -2 });
		check("a(b|c){0,}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcd", new int[] { 0, 4, 2, 3, -2, -2 });
		check("a(b|c){1,1}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abd", new int[] { 0, 3, 1, 2, -2, -2 });
		check("a(b|c){1,2}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abd", new int[] { 0, 3, 1, 2, -2, -2 });
		check("a(b|c){1,2}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcd", new int[] { 0, 4, 2, 3, -2, -2 });
		check("a(b|c){1,}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abd", new int[] { 0, 3, 1, 2, -2, -2 });
		check("a(b|c){1,}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcd", new int[] { 0, 4, 2, 3, -2, -2 });
		check("a(b|c){2,2}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "acbd", new int[] { 0, 4, 2, 3, -2, -2 });
		check("a(b|c){2,2}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcd", new int[] { 0, 4, 2, 3, -2, -2 });
		check("a(b|c){2,4}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcd", new int[] { 0, 4, 2, 3, -2, -2 });
		check("a(b|c){2,4}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcbd", new int[] { 0, 5, 3, 4, -2, -2 });
		check("a(b|c){2,4}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcbcd", new int[] { 0, 6, 4, 5, -2, -2 });
		check("a(b|c){2,}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcd", new int[] { 0, 4, 2, 3, -2, -2 });
		check("a(b|c){2,}d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcbd", new int[] { 0, 5, 3, 4, -2, -2 });

		report("test_tricky_cases");
	}

	private static void test_tricky_cases2() {
		check("a(b|c?)+d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcd", new int[] { 0, 4, 3, 3, -2, -2 });
		check("a(b+|((c)*))+d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abd",
				new int[] { 0, 3, 2, 2, 2, 2, -1, -1, -2, -2 });
		check("a(b+|((c)*))+d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcd",
				new int[] { 0, 4, 3, 3, 3, 3, 2, 3, -2, -2 });
		check("//[^\\n]*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "++i //here is a line comment\n",
				new int[] { 4, 28, -2, -2 });
		check("/\\*([^*]|\\*+[^*/])*\\*+/", 0 | Pattern.MULTILINE | Pattern.DOTALL, "/* here is a block comment */",
				new int[] { 0, 29, 26, 27, -2, -2 });
		check("/\\*([^*]|\\*+[^*/])*\\*+/", 0 | Pattern.MULTILINE | Pattern.DOTALL, "/**/",
				new int[] { 0, 4, -1, -1, -2, -2 });
		check("/\\*([^*]|\\*+[^*/])*\\*+/", 0 | Pattern.MULTILINE | Pattern.DOTALL, "/***/",
				new int[] { 0, 5, -1, -1, -2, -2 });
		check("/\\*([^*]|\\*+[^*/])*\\*+/", 0 | Pattern.MULTILINE | Pattern.DOTALL, "/****/",
				new int[] { 0, 6, -1, -1, -2, -2 });
		check("/\\*([^*]|\\*+[^*/])*\\*+/", 0 | Pattern.MULTILINE | Pattern.DOTALL, "/*****/",
				new int[] { 0, 7, -1, -1, -2, -2 });
		check("/\\*([^*]|\\*+[^*/])*\\*+/", 0 | Pattern.MULTILINE | Pattern.DOTALL, "/*****/*/",
				new int[] { 0, 7, -1, -1, -2, -2 });
		check("^\\p{Blank}*#([^\\n]*\\\\\\p{Space}+)*[^\\n]*", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"#define some_symbol", new int[] { 0, 19, -1, -1, -2, -2 });
		check("^\\p{Blank}*#([^\\n]*\\\\\\p{Space}+)*[^\\n]*", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"#define some_symbol(x) #x", new int[] { 0, 25, -1, -1, -2, -2 });
		check("^\\p{Blank}*#([^\\n]*\\\\\\p{Space}+)*[^\\n]*", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"#define some_symbol(x) \\  \r\n  foo();\\\r\n   printf(#x);", new int[] { 0, 53, 30, 42, -2, -2 });

		report("test_tricky_cases2");
	}

	private static void test_tricky_cases3() {
		check("((0x\\p{XDigit}+)|(\\p{Digit}+))u?((int(8|16|32|64))|L)?", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"0xFF", new int[] { 0, 4, 0, 4, 0, 4, -1, -1, -1, -1, -1, -1, -1, -1, -2, -2 });
		check("((0x\\p{XDigit}+)|(\\p{Digit}+))u?((int(8|16|32|64))|L)?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "35",
				new int[] { 0, 2, 0, 2, -1, -1, 0, 2, -1, -1, -1, -1, -1, -1, -2, -2 });
		check("((0x\\p{XDigit}+)|(\\p{Digit}+))u?((int(8|16|32|64))|L)?", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"0xFFu", new int[] { 0, 5, 0, 4, 0, 4, -1, -1, -1, -1, -1, -1, -1, -1, -2, -2 });
		check("((0x\\p{XDigit}+)|(\\p{Digit}+))u?((int(8|16|32|64))|L)?", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"0xFFL", new int[] { 0, 5, 0, 4, 0, 4, -1, -1, 4, 5, -1, -1, -1, -1, -2, -2 });
		check("((0x\\p{XDigit}+)|(\\p{Digit}+))u?((int(8|16|32|64))|L)?", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"0xFFFFFFFFFFFFFFFFuint64", new int[] { 0, 24, 0, 18, 0, 18, -1, -1, 19, 24, 19, 24, 22, 24, -2, -2 });
		check("'([^\\\\']|\\\\.)*'", 0 | Pattern.MULTILINE | Pattern.DOTALL, "'\\x3A'",
				new int[] { 0, 6, 4, 5, -2, -2 });
		check("'([^\\\\']|\\\\.)*'", 0 | Pattern.MULTILINE | Pattern.DOTALL, "'\\''", new int[] { 0, 4, 1, 3, -2, -2 });
		check("'([^\\\\']|\\\\.)*'", 0 | Pattern.MULTILINE | Pattern.DOTALL, "'\\n'", new int[] { 0, 4, 1, 3, -2, -2 });
		check("0123456789@abcdefghijklmnopqrstuvwxyz\\[\\\\\\]\\^_`ABCDEFGHIJKLMNOPQRSTUVWXYZ\\{\\|\\}",
				0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL,
				"0123456789@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}",
				new int[] { 0, 72, -2, -2 });
		check("a", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "A", new int[] { 0, 1, -2, -2 });
		check("A", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { 0, 1, -2, -2 });
		check("[abc]+", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "abcABC",
				new int[] { 0, 6, -2, -2 });
		check("[ABC]+", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "abcABC",
				new int[] { 0, 6, -2, -2 });
		check("[a-z]+", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "abcABC",
				new int[] { 0, 6, -2, -2 });
		check("[A-Z]+", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "abzANZ",
				new int[] { 0, 6, -2, -2 });
		check("[A-z]+", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "abzABZ",
				new int[] { 0, 6, -2, -2 });
		check("\\w+", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "abcZZZ",
				new int[] { 0, 6, -2, -2 });
		check("\\p{Alpha}+", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "abyzABYZ",
				new int[] { 0, 8, -2, -2 });
		check("\\p{Alnum}+", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "09abyzABYZ",
				new int[] { 0, 10, -2, -2 });
		check("\\(", 0 | Pattern.MULTILINE | Pattern.DOTALL, "(", new int[] { 0, 1, -2, -2 });
		check("\\)", 0 | Pattern.MULTILINE | Pattern.DOTALL, ")", new int[] { 0, 1, -2, -2 });
		check("\\$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "$", new int[] { 0, 1, -2, -2 });
		check("\\^", 0 | Pattern.MULTILINE | Pattern.DOTALL, "^", new int[] { 0, 1, -2, -2 });
		check("\\.", 0 | Pattern.MULTILINE | Pattern.DOTALL, ".", new int[] { 0, 1, -2, -2 });
		check("\\*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "*", new int[] { 0, 1, -2, -2 });
		check("\\+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "+", new int[] { 0, 1, -2, -2 });
		check("\\?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "?", new int[] { 0, 1, -2, -2 });
		check("\\[", 0 | Pattern.MULTILINE | Pattern.DOTALL, "[", new int[] { 0, 1, -2, -2 });
		check("\\]", 0 | Pattern.MULTILINE | Pattern.DOTALL, "]", new int[] { 0, 1, -2, -2 });
		check("\\|", 0 | Pattern.MULTILINE | Pattern.DOTALL, "|", new int[] { 0, 1, -2, -2 });
		check("\\\\", 0 | Pattern.MULTILINE | Pattern.DOTALL, "\\", new int[] { 0, 1, -2, -2 });
		check("#", 0 | Pattern.MULTILINE | Pattern.DOTALL, "#", new int[] { 0, 1, -2, -2 });
		check("\\#", 0 | Pattern.MULTILINE | Pattern.DOTALL, "#", new int[] { 0, 1, -2, -2 });
		check("a-", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a-", new int[] { 0, 2, -2, -2 });
		check("\\-", 0 | Pattern.MULTILINE | Pattern.DOTALL, "-", new int[] { 0, 1, -2, -2 });
		check("\\{", 0 | Pattern.MULTILINE | Pattern.DOTALL, "{", new int[] { 0, 1, -2, -2 });
		check("\\}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "}", new int[] { 0, 1, -2, -2 });
		check("0", 0 | Pattern.MULTILINE | Pattern.DOTALL, "0", new int[] { 0, 1, -2, -2 });
		check("1", 0 | Pattern.MULTILINE | Pattern.DOTALL, "1", new int[] { 0, 1, -2, -2 });
		check("9", 0 | Pattern.MULTILINE | Pattern.DOTALL, "9", new int[] { 0, 1, -2, -2 });
		check("b", 0 | Pattern.MULTILINE | Pattern.DOTALL, "b", new int[] { 0, 1, -2, -2 });
		check("B", 0 | Pattern.MULTILINE | Pattern.DOTALL, "B", new int[] { 0, 1, -2, -2 });
		check("\\<", 0 | Pattern.MULTILINE | Pattern.DOTALL, "<", new int[] { 0, 1, -2, -2 });
		check("\\>", 0 | Pattern.MULTILINE | Pattern.DOTALL, ">", new int[] { 0, 1, -2, -2 });
		check("w", 0 | Pattern.MULTILINE | Pattern.DOTALL, "w", new int[] { 0, 1, -2, -2 });
		check("W", 0 | Pattern.MULTILINE | Pattern.DOTALL, "W", new int[] { 0, 1, -2, -2 });
		check("`", 0 | Pattern.MULTILINE | Pattern.DOTALL, "`", new int[] { 0, 1, -2, -2 });
		check(" ", 0 | Pattern.MULTILINE | Pattern.DOTALL, " ", new int[] { 0, 1, -2, -2 });
		check("\\n", 0 | Pattern.MULTILINE | Pattern.DOTALL, "\n", new int[] { 0, 1, -2, -2 });
		check(",", 0 | Pattern.MULTILINE | Pattern.DOTALL, ",", new int[] { 0, 1, -2, -2 });
		check("a", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { 0, 1, -2, -2 });
		check("f", 0 | Pattern.MULTILINE | Pattern.DOTALL, "f", new int[] { 0, 1, -2, -2 });
		check("n", 0 | Pattern.MULTILINE | Pattern.DOTALL, "n", new int[] { 0, 1, -2, -2 });
		check("r", 0 | Pattern.MULTILINE | Pattern.DOTALL, "r", new int[] { 0, 1, -2, -2 });
		check("t", 0 | Pattern.MULTILINE | Pattern.DOTALL, "t", new int[] { 0, 1, -2, -2 });
		check("v", 0 | Pattern.MULTILINE | Pattern.DOTALL, "v", new int[] { 0, 1, -2, -2 });
		check("c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "c", new int[] { 0, 1, -2, -2 });
		check("x", 0 | Pattern.MULTILINE | Pattern.DOTALL, "x", new int[] { 0, 1, -2, -2 });
		check(":", 0 | Pattern.MULTILINE | Pattern.DOTALL, ":", new int[] { 0, 1, -2, -2 });
		check("(\\.\\p{Alnum}+){2}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "w.a.b ",
				new int[] { 1, 5, 3, 5, -2, -2 });
		check("(?!foo)bar", 0 | Pattern.MULTILINE | Pattern.DOTALL, "foobar", new int[] { 3, 6, -2, -2 });
		check("(?!foo)bar", 0 | Pattern.MULTILINE | Pattern.DOTALL, "??bar", new int[] { 2, 5, -2, -2 });
		check("(?!foo)bar", 0 | Pattern.MULTILINE | Pattern.DOTALL, "barfoo", new int[] { 0, 3, -2, -2 });
		check("(?!foo)bar", 0 | Pattern.MULTILINE | Pattern.DOTALL, "bar??", new int[] { 0, 3, -2, -2 });
		check("(?!foo)bar", 0 | Pattern.MULTILINE | Pattern.DOTALL, "bar", new int[] { 0, 3, -2, -2 });
		check("a\\Z", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a\nb", new int[] { -2, -2 });
		check("()", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc",
				new int[] { 0, 0, 0, 0, -2, 1, 1, 1, 1, -2, 2, 2, 2, 2, -2, 3, 3, 3, 3, -2, -2 });
		check("^()", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 0, 0, 0, -2, -2 });
		check("^()+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 0, 0, 0, -2, -2 });
		check("^(){1}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 0, 0, 0, -2, -2 });
		check("^(){2}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 0, 0, 0, -2, -2 });
		check("^((){2})", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 0, 0, 0, 0, 0, -2, -2 });
		check("()", 0 | Pattern.MULTILINE | Pattern.DOTALL, "", new int[] { 0, 0, 0, 0, -2, -2 });
		check("()\\1", 0 | Pattern.MULTILINE | Pattern.DOTALL, "", new int[] { 0, 0, 0, 0, -2, -2 });
		check("()\\1", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { 0, 0, 0, 0, -2, 1, 1, 1, 1, -2, -2 });
		check("a()\\1b", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, 1, 1, -2, -2 });
		check("a()b\\1", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, 1, 1, -2, -2 });
		check("([a-c]+)\\1", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcbc", new int[] { 1, 5, 1, 3, -2, -2 });
		check(".+abc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xxxxxxxxyyyyyyyyab", new int[] { -2, -2 });
		check("(.+)\\1", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcdxxxyyyxxxyyy",
				new int[] { 4, 16, 4, 10, -2, -2 });
		check("[_]+$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "___________________________________________x",
				new int[] { -2, -2 });
		check("(a)(?:b)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, -2, -2 });
		check("(?:\\d{9}.*){2}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "123456789dfsdfsdfsfsdfds123456789b",
				new int[] { 0, 34, -2, -2 });
		check("(?:\\d{9}.*){2}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "123456789dfsdfsdfsfsdfds12345678",
				new int[] { -2, -2 });
		check("(?:\\d{9}.*){2}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "123456789dfsdfsdfsfsdfds",
				new int[] { -2, -2 });
		check("^(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]?[0-9])){3}$",
				0 | Pattern.MULTILINE | Pattern.DOTALL, "1.2.03", new int[] { -2, -2 });
		check("^(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]?[0-9])){3,4}$",
				0 | Pattern.MULTILINE | Pattern.DOTALL, "1.2.03", new int[] { -2, -2 });
		check("^(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]?[0-9])){3,4}?$",
				0 | Pattern.MULTILINE | Pattern.DOTALL, "1.2.03", new int[] { -2, -2 });

		report("test_tricky_cases3");
	}

	private static void test_alt() {
		check("a|b", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { 0, 1, -2, -2 });
		check("a|b", 0 | Pattern.MULTILINE | Pattern.DOTALL, "b", new int[] { 0, 1, -2, -2 });
		check("a|b|c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "c", new int[] { 0, 1, -2, -2 });
		check("a|(b)|.", 0 | Pattern.MULTILINE | Pattern.DOTALL, "b", new int[] { 0, 1, 0, 1, -2, -2 });
		check("(a)|b|.", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { 0, 1, 0, 1, -2, -2 });
		check("a(b|c)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, 1, 2, -2, -2 });
		check("a(b|c)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ac", new int[] { 0, 2, 1, 2, -2, -2 });
		check("a(b|c)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ad", new int[] { -2, -2 });
		check("(a|b|c)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "c", new int[] { 0, 1, 0, 1, -2, -2 });
		check("(a|(b)|.)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "b", new int[] { 0, 1, 0, 1, 0, 1, -2, -2 });
		check("c|", 0 | Pattern.MULTILINE | Pattern.DOTALL, " c", new int[] { 0, 0, -2, 1, 2, -2, 2, 2, -2, -2 });
		check("(|)", 0 | Pattern.MULTILINE | Pattern.DOTALL, " c",
				new int[] { 0, 0, 0, 0, -2, 1, 1, 1, 1, -2, 2, 2, 2, 2, -2, -2 });
		check("(a|)", 0 | Pattern.MULTILINE | Pattern.DOTALL, " a",
				new int[] { 0, 0, 0, 0, -2, 1, 2, 1, 2, -2, 2, 2, 2, 2, -2, -2 });
		check("a\\|", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a|", new int[] { 0, 2, -2, -2 });

		report("test_alt");
	}

	private static void test_simple_repeats() {
		check("a*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "b", new int[] { 0, 0, -2, 1, 1, -2, -2 });
		check("ab*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, -2, -2 });
		check("ab*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "sssabbbbbbsss", new int[] { 3, 10, -2, -2 });
		check("ab*c*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { 0, 1, -2, -2 });
		check("ab*c*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbb", new int[] { 0, 4, -2, -2 });
		check("ab*c*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "accc", new int[] { 0, 4, -2, -2 });
		check("ab*c*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcc", new int[] { 0, 5, -2, -2 });
		check("\n*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "\n\n", new int[] { 0, 2, -2, 2, 2, -2, -2 });
		check("\\**", 0 | Pattern.MULTILINE | Pattern.DOTALL, "**", new int[] { 0, 2, -2, 2, 2, -2, -2 });
		check("\\*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "*", new int[] { 0, 1, -2, -2 });
		check("(ab)*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abab", new int[] { 0, 4, 2, 4, -2, 4, 4, -2, -2 });
		check("ab+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { -2, -2 });
		check("ab+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, -2, -2 });
		check("ab+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "sssabbbbbbsss", new int[] { 3, 10, -2, -2 });
		check("ab+c+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbb", new int[] { -2, -2 });
		check("ab+c+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "accc", new int[] { -2, -2 });
		check("ab+c+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcc", new int[] { 0, 5, -2, -2 });
		check("\n+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "\n\n", new int[] { 0, 2, -2, -2 });
		check("\\+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "+", new int[] { 0, 1, -2, -2 });
		check("\\+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "++", new int[] { 0, 1, -2, 1, 2, -2, -2 });
		check("\\++", 0 | Pattern.MULTILINE | Pattern.DOTALL, "++", new int[] { 0, 2, -2, -2 });
		check("a?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "b", new int[] { 0, 0, -2, 1, 1, -2, -2 });
		check("ab?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { 0, 1, -2, -2 });
		check("ab?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, -2, -2 });
		check("ab?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "sssabbbbbbsss", new int[] { 3, 5, -2, -2 });
		check("ab?c?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { 0, 1, -2, -2 });
		check("ab?c?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbb", new int[] { 0, 2, -2, -2 });
		check("ab?c?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "accc", new int[] { 0, 2, -2, -2 });
		check("ab?c?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcc", new int[] { 0, 3, -2, -2 });
		check("\n?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "\n\n", new int[] { 0, 1, -2, 1, 2, -2, 2, 2, -2, -2 });
		check("\\?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "?", new int[] { 0, 1, -2, -2 });
		check("\\?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "?", new int[] { 0, 1, -2, -2 });
		check("\\??", 0 | Pattern.MULTILINE | Pattern.DOTALL, "??", new int[] { 0, 1, -2, 1, 2, -2, 2, 2, -2, -2 });
		check("a{2}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { -2, -2 });
		check("a{2}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aa", new int[] { 0, 2, -2, -2 });
		check("a{2}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaa", new int[] { 0, 2, -2, -2 });
		check("a{2,}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { -2, -2 });
		check("a{2,}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aa", new int[] { 0, 2, -2, -2 });
		check("a{2,}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaaa", new int[] { 0, 5, -2, -2 });
		check("a{2,4}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { -2, -2 });
		check("a{2,4}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aa", new int[] { 0, 2, -2, -2 });
		check("a{2,4}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaa", new int[] { 0, 3, -2, -2 });
		check("a{2,4}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaa", new int[] { 0, 4, -2, -2 });
		check("a{2,4}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaaa", new int[] { 0, 4, -2, -2 });
		check("a\\{\\}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a{}", new int[] { 0, 3, -2, -2 });
		check("a{2,4}?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { -2, -2 });
		check("a{2,4}?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aa", new int[] { 0, 2, -2, -2 });
		check("a{2,4}?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaa", new int[] { 0, 2, -2, -2 });
		check("a{2,4}?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaa", new int[] { 0, 2, -2, 2, 4, -2, -2 });
		check("a{2,4}?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaaa", new int[] { 0, 2, -2, 2, 4, -2, -2 });
		check("a{2,4}?$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aa", new int[] { 0, 2, -2, -2 });
		check("a{2,4}?$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaa", new int[] { 0, 3, -2, -2 });
		check("a{2,4}?$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaa", new int[] { 0, 4, -2, -2 });
		check("a{2,4}?$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaaa", new int[] { 1, 5, -2, -2 });
		check("^a{0,1}?$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaaa", new int[] { -2, -2 });
		check("^(?:a){0,1}?$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaaa", new int[] { -2, -2 });
		check("a}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a}", new int[] { 0, 2, -2, -2 });

		report("test_simple_repeats");
	}

	private static void test_simple_repeats2() {

		report("test_simple_repeats2");
	}

	private static void test_fast_repeats() {
		check("ab.*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy_", new int[] { 0, 4, -2, -2 });
		check("ab.*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy_", new int[] { 0, 5, -2, -2 });
		check("ab.*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy", new int[] { 0, 4, -2, -2 });
		check("ab.*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { 0, 5, -2, -2 });
		check("ab.*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, -2, -2 });
		check("ab.*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 4, -2, -2 });
		check(".*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { -2, -2 });
		check(".*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { -2, -2 });
		check("a+?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { -2, -2 });
		check("ab.{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy_", new int[] { 0, 6, -2, -2 });
		check("ab.{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab____xy_", new int[] { 0, 8, -2, -2 });
		check("ab.{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy_", new int[] { 0, 9, -2, -2 });
		check("ab.{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy", new int[] { 0, 6, -2, -2 });
		check("ab.{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy", new int[] { 0, 9, -2, -2 });
		check("ab.{2,5}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 4, -2, -2 });
		check("ab.{2,5}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_______", new int[] { 0, 7, -2, -2 });
		check("ab.{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab______xy", new int[] { -2, -2 });
		check("ab.{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { -2, -2 });
		check("ab.*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy_", new int[] { 0, 4, -2, -2 });
		check("ab.*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy_", new int[] { 0, 5, -2, -2 });
		check("ab.*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy", new int[] { 0, 4, -2, -2 });
		check("ab.*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { 0, 5, -2, -2 });
		check("ab.*?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, -2, -2 });
		check("ab.*?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 2, -2, -2 });
		check("ab.{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy_", new int[] { 0, 6, -2, -2 });
		check("ab.{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab____xy_", new int[] { 0, 8, -2, -2 });
		check("ab.{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy_", new int[] { 0, 9, -2, -2 });
		check("ab.{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy", new int[] { 0, 6, -2, -2 });
		check("ab.{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy", new int[] { 0, 9, -2, -2 });
		check("ab.{2,5}?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 4, -2, -2 });
		check("ab.{2,5}?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_______", new int[] { 0, 4, -2, -2 });
		check("ab.{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab______xy", new int[] { -2, -2 });
		check("ab.{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { -2, -2 });
		check("ab_*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy_", new int[] { 0, 4, -2, -2 });
		check("ab_*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy_", new int[] { 0, 5, -2, -2 });
		check("ab_*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy", new int[] { 0, 4, -2, -2 });
		check("ab_*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { 0, 5, -2, -2 });
		check("ab_*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, -2, -2 });
		check("ab_*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 4, -2, -2 });
		check("ab_*?z", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { -2, -2 });
		check("ab_{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy_", new int[] { 0, 6, -2, -2 });
		check("ab_{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab____xy_", new int[] { 0, 8, -2, -2 });
		check("ab_{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy_", new int[] { 0, 9, -2, -2 });
		check("ab_{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy", new int[] { 0, 6, -2, -2 });
		check("ab_{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy", new int[] { 0, 9, -2, -2 });
		check("ab_{2,5}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 4, -2, -2 });
		check("ab_{2,5}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_______", new int[] { 0, 7, -2, -2 });
		check("ab_{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab______xy", new int[] { -2, -2 });
		check("ab_{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { -2, -2 });
		check("ab_*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy_", new int[] { 0, 4, -2, -2 });
		check("ab_*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy_", new int[] { 0, 5, -2, -2 });
		check("ab_*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy", new int[] { 0, 4, -2, -2 });
		check("ab_*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { 0, 5, -2, -2 });
		check("ab_*?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, -2, -2 });
		check("ab_*?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 2, -2, -2 });
		check("ab_{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy_", new int[] { 0, 6, -2, -2 });
		check("ab_{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab____xy_", new int[] { 0, 8, -2, -2 });
		check("ab_{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy_", new int[] { 0, 9, -2, -2 });
		check("ab_{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy", new int[] { 0, 6, -2, -2 });
		check("ab_{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy", new int[] { 0, 9, -2, -2 });
		check("ab_{2,5}?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 4, -2, -2 });
		check("ab_{2,5}?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_______", new int[] { 0, 4, -2, -2 });
		check("ab_{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab______xy", new int[] { -2, -2 });
		check("ab_{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { -2, -2 });
		check("(5*?).somesite", 0 | Pattern.MULTILINE | Pattern.DOTALL, "//555.somesite",
				new int[] { 2, 14, 2, 5, -2, -2 });

		report("test_fast_repeats");
	}

	private static void test_fast_repeats2() {
		check("ab[_,;]*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy_", new int[] { 0, 4, -2, -2 });
		check("ab[_,;]*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy_", new int[] { 0, 5, -2, -2 });
		check("ab[_,;]*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy", new int[] { 0, 4, -2, -2 });
		check("ab[_,;]*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { 0, 5, -2, -2 });
		check("ab[_,;]*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, -2, -2 });
		check("ab[_,;]*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 4, -2, -2 });
		check("ab[_,;]*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__z", new int[] { -2, -2 });
		check("ab[_,;]*?z", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { -2, -2 });
		check("ab[_,;]*?.z", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__,;,__z", new int[] { 0, 10, -2, -2 });
		check("ab[_,;]*?.z", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__,;,__y", new int[] { -2, -2 });
		check("ab[_,;]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy_", new int[] { 0, 6, -2, -2 });
		check("ab[_,;]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab____xy_", new int[] { 0, 8, -2, -2 });
		check("ab[_,;]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy_", new int[] { 0, 9, -2, -2 });
		check("ab[_,;]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy", new int[] { 0, 6, -2, -2 });
		check("ab[_,;]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy", new int[] { 0, 9, -2, -2 });
		check("ab[_,;]{2,5}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 4, -2, -2 });
		check("ab[_,;]{2,5}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_______", new int[] { 0, 7, -2, -2 });
		check("ab[_,;]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab______xy", new int[] { -2, -2 });
		check("ab[_,;]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { -2, -2 });
		check("ab[_,;]*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy_", new int[] { 0, 4, -2, -2 });
		check("ab[_,;]*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy_", new int[] { 0, 5, -2, -2 });
		check("ab[_,;]*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy", new int[] { 0, 4, -2, -2 });
		check("ab[_,;]*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { 0, 5, -2, -2 });
		check("ab[_,;]*?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, -2, -2 });
		check("ab[_,;]*?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 2, -2, -2 });
		check("ab[_,;]{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy_", new int[] { 0, 6, -2, -2 });
		check("ab[_,;]{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab____xy_", new int[] { 0, 8, -2, -2 });
		check("ab[_,;]{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy_", new int[] { 0, 9, -2, -2 });
		check("ab[_,;]{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy", new int[] { 0, 6, -2, -2 });
		check("ab[_,;]{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy", new int[] { 0, 9, -2, -2 });
		check("ab[_,;]{2,5}?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 4, -2, -2 });
		check("ab[_,;]{2,5}?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_______", new int[] { 0, 4, -2, -2 });
		check("ab[_,;]{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab______xy", new int[] { -2, -2 });
		check("ab[_,;]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { -2, -2 });
		check("ab[_[.ae.]]*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy_", new int[] { 0, 4, -2, -2 });
		check("ab[_[.ae.]]*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy_", new int[] { 0, 5, -2, -2 });
		check("ab[_[.ae.]]*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy", new int[] { 0, 4, -2, -2 });
		check("ab[_[.ae.]]*xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { 0, 5, -2, -2 });
		check("ab[_[.ae.]]*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, -2, -2 });
		check("ab[_[.ae.]]*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 4, -2, -2 });
		check("ab[_[.ae.]]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy_", new int[] { 0, 6, -2, -2 });
		check("ab[_[.ae.]]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab____xy_", new int[] { 0, 8, -2, -2 });
		check("ab[_[.ae.]]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy_", new int[] { 0, 9, -2, -2 });
		check("ab[_[.ae.]]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy", new int[] { 0, 6, -2, -2 });
		check("ab[_[.ae.]]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy", new int[] { 0, 9, -2, -2 });
		check("ab[_[.ae.]]{2,5}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 4, -2, -2 });
		check("ab[_[.ae.]]{2,5}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_______", new int[] { 0, 7, -2, -2 });
		check("ab[_[.ae.]]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab______xy", new int[] { -2, -2 });
		check("ab[_[.ae.]]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { -2, -2 });
		check("ab[_[.ae.]]*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy_", new int[] { 0, 4, -2, -2 });
		check("ab[_[.ae.]]*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy_", new int[] { 0, 5, -2, -2 });
		check("ab[_[.ae.]]*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abxy", new int[] { 0, 4, -2, -2 });
		check("ab[_[.ae.]]*?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { 0, 5, -2, -2 });
		check("ab[_[.ae.]]*?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { 0, 2, -2, -2 });
		check("ab[_[.ae.]]*?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 2, -2, -2 });
		check("ab[_[.ae.]]{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy_", new int[] { 0, 6, -2, -2 });
		check("ab[_[.ae.]]{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab____xy_", new int[] { 0, 8, -2, -2 });
		check("ab[_[.ae.]]{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy_", new int[] { 0, 9, -2, -2 });
		check("ab[_[.ae.]]{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__xy", new int[] { 0, 6, -2, -2 });
		check("ab[_[.ae.]]{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_____xy", new int[] { 0, 9, -2, -2 });
		check("ab[_[.ae.]]{2,5}?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab__", new int[] { 0, 4, -2, -2 });
		check("ab[_[.ae.]]{2,5}?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_______", new int[] { 0, 4, -2, -2 });
		check("ab[_[.ae.]]{2,5}?xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab______xy", new int[] { -2, -2 });
		check("ab[_[.ae.]]{2,5}xy", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab_xy", new int[] { -2, -2 });
		check("([5[.ae.]]*?).somesite", 0 | Pattern.MULTILINE | Pattern.DOTALL, "//555.somesite",
				new int[] { 2, 14, 2, 5, -2, -2 });

		report("test_fast_repeats2");
	}

	private static void test_pocessive_repeats() {
		check("^(\\w++|\\s++)*$", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"now is the time for all good men to come to the aid of the party",
				new int[] { 0, 64, 59, 64, -2, -2 });
		check("^(\\w++|\\s++)*$", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"this is not a line with only words and spaces!", new int[] { -2, -2 });
		check("(\\d++)(\\w)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "12345a", new int[] { 0, 6, 0, 5, 5, 6, -2, -2 });
		check("(\\d++)(\\w)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "12345+", new int[] { -2, -2 });
		check("(\\d++)(\\w)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "12345", new int[] { -2, -2 });
		check("a++b", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaab", new int[] { 0, 4, -2, -2 });
		check("(a++b)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaab", new int[] { 0, 4, 0, 4, -2, -2 });
		check("([^()]++|\\([^()]*\\))+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "((abc(ade)ufh()()x",
				new int[] { 2, 18, 17, 18, -2, -2 });
		check("\\(([^()]++|\\([^()]+\\))+\\)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "(abc)",
				new int[] { 0, 5, 1, 4, -2, -2 });
		check("\\(([^()]++|\\([^()]+\\))+\\)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "(abc(def)xyz)",
				new int[] { 0, 13, 9, 12, -2, -2 });
		check("\\(([^()]++|\\([^()]+\\))+\\)", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"((()aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", new int[] { -2, -2 });
		check("x*+\\w", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xxxxx", new int[] { -2, -2 });
		check("x*+\\w", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xxxxxa", new int[] { 0, 6, -2, -2 });
		check("x{1,6}+\\w", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xxxxx", new int[] { -2, -2 });
		check("x{1,6}+\\w", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xxxxxa", new int[] { 0, 6, -2, -2 });
		check("x{1,5}+\\w", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xxxxxa", new int[] { 0, 6, -2, -2 });
		check("x{1,4}+\\w", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xxxxxa", new int[] { 0, 5, -2, -2 });
		check("x{1,3}+\\w", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xxxxxa", new int[] { 0, 4, -2, 4, 6, -2, -2 });

		report("test_pocessive_repeats");
	}

	private static void test_independent_subs() {
		check("(?>^abc)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 3, -2, -2 });
		check("(?>^abc)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "def\nabc", new int[] { 4, 7, -2, -2 });
		check("(?>^abc)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "defabc", new int[] { -2, -2 });
		check("(?>.*/)foo", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"/this/is/a/very/long/line/in/deed/with/very/many/slashes/in/it/you/see/", new int[] { -2, -2 });
		check("(?>.*/)foo", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"/this/is/a/very/long/line/in/deed/with/very/many/slashes/in/and/foo", new int[] { 0, 67, -2, -2 });
		check("(?>(\\.\\d\\d[1-9]?))\\d+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "1.230003938",
				new int[] { 1, 11, 1, 4, -2, -2 });
		check("(?>(\\.\\d\\d[1-9]?))\\d+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "1.875000282",
				new int[] { 1, 11, 1, 5, -2, -2 });
		check("(?>(\\.\\d\\d[1-9]?))\\d+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "1.235", new int[] { -2, -2 });
		check("^((?>\\w+)|(?>\\s+))*$", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"now is the time for all good men to come to the aid of the party",
				new int[] { 0, 64, 59, 64, -2, -2 });
		check("^((?>\\w+)|(?>\\s+))*$", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"this is not a line with only words and spaces!", new int[] { -2, -2 });
		check("((?>\\d+))(\\w)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "12345a",
				new int[] { 0, 6, 0, 5, 5, 6, -2, -2 });
		check("((?>\\d+))(\\w)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "12345+", new int[] { -2, -2 });
		check("((?>\\d+))(\\d)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "12345", new int[] { -2, -2 });
		check("(?>a+)b", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaab", new int[] { 0, 4, -2, -2 });
		check("((?>a+)b)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaab", new int[] { 0, 4, 0, 4, -2, -2 });
		check("(?>(a+))b", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaab", new int[] { 0, 4, 0, 3, -2, -2 });
		check("(?>b)+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaabbbccc", new int[] { 3, 6, -2, -2 });
		check("(?>a+|b+|c+)*c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaabbbbccccd",
				new int[] { 0, 8, -2, 8, 9, -2, 9, 10, -2, 10, 11, -2, -2 });
		check("((?>[^()]+)|\\([^()]*\\))+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "((abc(ade)ufh()()x",
				new int[] { 2, 18, 17, 18, -2, -2 });
		check("\\(((?>[^()]+)|\\([^()]+\\))+\\)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "(abc)",
				new int[] { 0, 5, 1, 4, -2, -2 });
		check("\\(((?>[^()]+)|\\([^()]+\\))+\\)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "(abc(def)xyz)",
				new int[] { 0, 13, 9, 12, -2, -2 });
		check("\\(((?>[^()]+)|\\([^()]+\\))+\\)", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"((()aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", new int[] { -2, -2 });
		check("(?>a*)*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { 0, 1, -2, 1, 1, -2, -2 });
		check("(?>a*)*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aa", new int[] { 0, 2, -2, 2, 2, -2, -2 });
		check("(?>a*)*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaa", new int[] { 0, 4, -2, 4, 4, -2, -2 });
		check("(?>a*)*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a", new int[] { 0, 1, -2, 1, 1, -2, -2 });
		check("(?>a*)*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaabcde",
				new int[] { 0, 3, -2, 3, 3, -2, 4, 4, -2, 5, 5, -2, 6, 6, -2, 7, 7, -2, -2 });
		check("((?>a*))*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaaa",
				new int[] { 0, 5, 5, 5, -2, 5, 5, 5, 5, -2, -2 });
		check("((?>a*))*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aabbaa",
				new int[] { 0, 2, 2, 2, -2, 2, 2, 2, 2, -2, 3, 3, 3, 3, -2, 4, 6, 6, 6, -2, 6, 6, 6, 6, -2, -2 });
		check("((?>a*?))*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaaa", new int[] { 0, 0, 0, 0, -2, 1, 1, 1, 1, -2,
				2, 2, 2, 2, -2, 3, 3, 3, 3, -2, 4, 4, 4, 4, -2, 5, 5, 5, 5, -2, -2 });
		check("((?>a*?))*", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aabbaa", new int[] { 0, 0, 0, 0, -2, 1, 1, 1, 1,
				-2, 2, 2, 2, 2, -2, 3, 3, 3, 3, -2, 4, 4, 4, 4, -2, 5, 5, 5, 5, -2, 6, 6, 6, 6, -2, -2 });
		check("word (?>(?:(?!otherword)[a-zA-Z0-9]+ ){0,30})otherword", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"word cat dog elephant mussel cow horse canary baboon snake shark otherword",
				new int[] { 0, 74, -2, -2 });
		check("word (?>(?:(?!otherword)[a-zA-Z0-9]+ ){0,30})otherword", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"word cat dog elephant mussel cow horse canary baboon snake shark", new int[] { -2, -2 });
		check("word (?>[a-zA-Z0-9]+ ){0,30}otherword", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"word cat dog elephant mussel cow horse canary baboon snake shark the quick brown fox and the lazy dog and several other words getting close to thirty by now I hope",
				new int[] { -2, -2 });
		check("word (?>[a-zA-Z0-9]+ ){0,30}otherword", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"word cat dog elephant mussel cow horse canary baboon snake shark the quick brown fox and the lazy dog and several other words getting close to thirty by now I really really hope otherword",
				new int[] { -2, -2 });
		check("((?>Z)+|A)+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ZABCDEFG", new int[] { 0, 2, 1, 2, -2, -2 });

		report("test_independent_subs");
	}

	private static void test_conditionals() {
		check("(?:(a)|b)(?(1)A|B)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aA", new int[] { 0, 2, 0, 1, -2, -2 });
		check("(?:(a)|b)(?(1)A|B)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "bB", new int[] { 0, 2, -2, -2 });
		check("(?:(a)|b)(?(1)A|B)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aB", new int[] { -2, -2 });
		check("(?:(a)|b)(?(1)A|B)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "bA", new int[] { -2, -2 });
		check("(a)?(?(1)A)B", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aAB", new int[] { 0, 3, 0, 1, -2, -2 });
		check("(a)?(?(1)A)B", 0 | Pattern.MULTILINE | Pattern.DOTALL, "B", new int[] { 0, 1, -1, -1, -2, -2 });
		check("(a)?(?(1)|A)B", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aB", new int[] { 0, 2, 0, 1, -2, -2 });
		check("(a)?(?(1)|A)B", 0 | Pattern.MULTILINE | Pattern.DOTALL, "AB", new int[] { 0, 2, -1, -1, -2, -2 });
		check("^(a)?(?(1)a|b)+$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aa", new int[] { 0, 2, 0, 1, -2, -2 });
		check("^(a)?(?(1)a|b)+$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "b", new int[] { 0, 1, -2, -2 });
		check("^(a)?(?(1)a|b)+$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "bb", new int[] { 0, 2, -2, -2 });
		check("^(a)?(?(1)a|b)+$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab", new int[] { -2, -2 });
		check("^(?(?=abc)\\w{3}:|\\d\\d)$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc:", new int[] { 0, 4, -2, -2 });
		check("^(?(?=abc)\\w{3}:|\\d\\d)$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "12", new int[] { 0, 2, -2, -2 });
		check("^(?(?=abc)\\w{3}:|\\d\\d)$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "123", new int[] { -2, -2 });
		check("^(?(?=abc)\\w{3}:|\\d\\d)$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xyz", new int[] { -2, -2 });
		check("(\\()?[^()]+(?(1)\\))", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcd",
				new int[] { 0, 4, -1, -1, -2, -2 });
		check("(\\()?[^()]+(?(1)\\))", 0 | Pattern.MULTILINE | Pattern.DOTALL, "(abcd)",
				new int[] { 0, 6, 0, 1, -2, -2 });
		check("(\\()?[^()]+(?(1)\\))", 0 | Pattern.MULTILINE | Pattern.DOTALL, "the quick (abcd) fox",
				new int[] { 0, 10, -1, -1, -2, 10, 16, 10, 11, -2, 16, 20, -1, -1, -2, -2 });
		check("(\\()?[^()]+(?(1)\\))", 0 | Pattern.MULTILINE | Pattern.DOTALL, "(abcd",
				new int[] { 1, 5, -1, -1, -2, -2 });
		check("\\b(?:(?:(one)|(two)|(three))(?:,|\\b)){3,}(?(1)|(?!))(?(2)|(?!))(?(3)|(?!))",
				0 | Pattern.MULTILINE | Pattern.DOTALL, "one,two,two, one", new int[] { -2, -2 });
		check("\\b(?:(?:(one)|(two)|(three))(?:,|\\b)){3,}(?(1)|(?!))(?(2)|(?!))(?(3)|(?!))",
				0 | Pattern.MULTILINE | Pattern.DOTALL, "one,three,two",
				new int[] { 0, 13, 0, 3, 10, 13, 4, 9, -2, -2 });
		check("\\b(?:(?:(one)|(two)|(three))(?:,|\\b)){3,}(?(1)|(?!))(?(2)|(?!))(?(3)|(?!))",
				0 | Pattern.MULTILINE | Pattern.DOTALL, "one,two,two,one,three,four",
				new int[] { 0, 22, 12, 15, 8, 11, 16, 21, -2, -2 });

		report("test_conditionals");
	}

	private static void test_options() {
		check("(?-m)^abc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc\nabc", new int[] { 0, 3, -2, -2 });
		check("(?-m)^abc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc\nabc", new int[] { 0, 3, -2, -2 });
		check("   ^    a   (?x:# begins with a\n)  b\\sc (?x:# then b c\n) $ (?x:# then end\n)",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "ab c", new int[] { 0, 4, -2, -2 });
		check("   ^    a   (?x:# begins with a\n)  b\\sc (?x:# then b c\n) $ (?x:# then end\n)",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "abc", new int[] { -2, -2 });
		check("   ^    a   (?x:# begins with a\n)  b\\sc (?x:# then b c\n) $ (?x:# then end\n)",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "ab cde", new int[] { -2, -2 });
		check("^1234(?x:# test newlines  inside\n)", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "1234",
				new int[] { 0, 4, -2, -2 });
		check("^1234 #comment in boost::regex::extended re\n",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "1234", new int[] { 0, 4, -2, -2 });
		check("#rhubarb\n  abcd", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "abcd",
				new int[] { 0, 4, -2, -2 });
		check("^1234 #comment in boost::regex::extended re\r\n",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "1234", new int[] { 0, 4, -2, -2 });
		check("#rhubarb\r\n  abcd", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "abcd",
				new int[] { 0, 4, -2, -2 });
		check("^abcd#rhubarb", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "abcd",
				new int[] { 0, 4, -2, -2 });
		check("^abcd#rhubarb", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcd#rhubarb", new int[] { 0, 12, -2, -2 });
		check("^a   b\n\n    c", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "abc",
				new int[] { 0, 3, -2, -2 });
		check("(?(?=[^a-z]+[a-z])  \\d{2}-[a-z]{3}-\\d{2}  |  \\d{2}-\\d{2}-\\d{2} ) ",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "12-sep-98", new int[] { 0, 9, -2, -2 });
		check("(?(?=[^a-z]+[a-z])  \\d{2}-[a-z]{3}-\\d{2}  |  \\d{2}-\\d{2}-\\d{2} ) ",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "12-09-98", new int[] { 0, 8, -2, -2 });
		check("(?(?=[^a-z]+[a-z])  \\d{2}-[a-z]{3}-\\d{2}  |  \\d{2}-\\d{2}-\\d{2} ) ",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "sep-12-98", new int[] { -2, -2 });
		check("ab", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "ab", new int[] { 0, 2, -2, -2 });
		check("   abc\\Q abc\\Eabc", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "abc abcabc",
				new int[] { 0, 10, -2, -2 });
		check("   abc\\Q abc\\Eabc", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "abcabcabc",
				new int[] { -2, -2 });
		check("abc#comment\n    \\Q#not comment\n    literal\\E",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "abc#not comment\n    literal",
				new int[] { 0, 27, -2, -2 });
		check("abc#comment\n    \\Q#not comment\n    literal",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "abc#not comment\n    literal",
				new int[] { 0, 27, -2, -2 });
		check("abc#comment\n    \\Q#not comment\n    literal\\E #more comment\n    ",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "abc#not comment\n    literal",
				new int[] { 0, 27, -2, -2 });
		check("abc#comment\n    \\Q#not comment\n    literal\\E #more comment",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "abc#not comment\n    literal",
				new int[] { 0, 27, -2, -2 });
		check("a(?x: b c )d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "XabcdY", new int[] { 1, 5, -2, -2 });
		check("a(?x: b c )d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "Xa b c d Y", new int[] { -2, -2 });

		report("test_options");
	}

	private static void test_options2() {
		check("a(?i:b)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 3, -2, -2 });
		check("a(?i:b)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aBc", new int[] { 0, 3, -2, -2 });
		check("a(?i:b)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ABC", new int[] { -2, -2 });
		check("a(?i:b)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abC", new int[] { -2, -2 });
		check("a(?i:b)c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aBC", new int[] { -2, -2 });
		check("a(?i:b)*c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aBc", new int[] { 0, 3, -2, -2 });
		check("a(?i:b)*c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aBBc", new int[] { 0, 4, -2, -2 });
		check("a(?i:b)*c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aBC", new int[] { -2, -2 });
		check("a(?i:b)*c", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aBBC", new int[] { -2, -2 });
		check("(?i:j)|h", 0 | Pattern.MULTILINE | Pattern.DOTALL, "J", new int[] { 0, 1, -2, -2 });
		check("(?i:j)|h", 0 | Pattern.MULTILINE | Pattern.DOTALL, "j", new int[] { 0, 1, -2, -2 });
		check("(?i:j)|h", 0 | Pattern.MULTILINE | Pattern.DOTALL, "h", new int[] { 0, 1, -2, -2 });
		check("(?i:j)|h", 0 | Pattern.MULTILINE | Pattern.DOTALL, "H", new int[] { -2, -2 });
		check("(?s-i:more.*than).*million", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL,
				"more than million", new int[] { 0, 17, -2, -2 });
		check("(?s-i:more.*than).*million", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL,
				"more than MILLION", new int[] { 0, 17, -2, -2 });
		check("(?s-i:more.*than).*million", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL,
				"more \n than Million", new int[] { 0, 19, -2, -2 });
		check("(?s-i:more.*than).*million", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL,
				"MORE THAN MILLION", new int[] { -2, -2 });
		check("(?s-i:more.*than).*million", 0 | Pattern.CASE_INSENSITIVE, "more \n than \n million",
				new int[] { -2, -2 });
		check("(?:(?s-i)more.*than).*million", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL,
				"more than million", new int[] { 0, 17, -2, -2 });
		check("(?:(?s-i)more.*than).*million", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL,
				"more than MILLION", new int[] { 0, 17, -2, -2 });
		check("(?:(?s-i)more.*than).*million", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL,
				"more \n than Million", new int[] { 0, 19, -2, -2 });
		check("(?:(?s-i)more.*than).*million", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL,
				"MORE THAN MILLION", new int[] { -2, -2 });
		check("(?:(?s-i)more.*than).*million", 0 | Pattern.CASE_INSENSITIVE, "more \n than \n million",
				new int[] { -2, -2 });
		check("(?<=^.{4})(?:bar|cat)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "fooocat", new int[] { 4, 7, -2, -2 });
		check("(?<=^.{4})(?:bar|cat)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "foocat", new int[] { -2, -2 });
		check("(?<=^a{4})(?:bar|cat)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaacat", new int[] { 4, 7, -2, -2 });
		check("(?<=^a{4})(?:bar|cat)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaacat", new int[] { -2, -2 });
		check("(?<=^\\p{Alpha}{4})(?:bar|cat)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaaacat",
				new int[] { 4, 7, -2, -2 });
		check("(?<=^\\p{Alpha}{4})(?:bar|cat)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaacat", new int[] { -2, -2 });
		check("a(?-i)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "ab",
				new int[] { 0, 2, -2, -2 });
		check("a(?-i)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "Ab",
				new int[] { 0, 2, -2, -2 });
		check("a(?-i)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "aB", new int[] { -2, -2 });
		check("a(?-i)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "AB", new int[] { -2, -2 });
		check("(?:(?-i)a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "ab",
				new int[] { 0, 2, -2, -2 });
		check("((?-i)a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "ab",
				new int[] { 0, 2, 0, 1, -2, -2 });
		check("(?:(?-i)a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "aB",
				new int[] { 0, 2, -2, -2 });
		check("((?-i)a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "aB",
				new int[] { 0, 2, 0, 1, -2, -2 });
		check("(?:(?-i)a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "Ab",
				new int[] { -2, -2 });
		check("(?:(?-i)a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "aB",
				new int[] { 0, 2, -2, -2 });
		check("((?-i)a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "aB",
				new int[] { 0, 2, 0, 1, -2, -2 });
		check("(?:(?-i)a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "Ab",
				new int[] { -2, -2 });
		check("(?:(?-i)a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "AB",
				new int[] { -2, -2 });
		check("(?-i:a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "ab",
				new int[] { 0, 2, -2, -2 });
		check("((?-i:a))b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "ab",
				new int[] { 0, 2, 0, 1, -2, -2 });
		check("(?-i:a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "aB",
				new int[] { 0, 2, -2, -2 });
		check("((?-i:a))b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "aB",
				new int[] { 0, 2, 0, 1, -2, -2 });
		check("(?-i:a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "AB",
				new int[] { -2, -2 });
		check("(?-i:a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "Ab",
				new int[] { -2, -2 });
		check("(?-i:a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "aB",
				new int[] { 0, 2, -2, -2 });
		check("((?-i:a))b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "aB",
				new int[] { 0, 2, 0, 1, -2, -2 });
		check("(?-i:a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "Ab",
				new int[] { -2, -2 });
		check("(?-i:a)b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "AB",
				new int[] { -2, -2 });
		check("((?-i:a.))b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "AB",
				new int[] { -2, -2 });
		check("((?-i:a.))b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "A\nB",
				new int[] { -2, -2 });
		check("((?s-i:a.))b", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "a\nB",
				new int[] { 0, 3, 0, 2, -2, -2 });
		check(".", 0 | Pattern.MULTILINE | Pattern.DOTALL, "\n", new int[] { 0, 1, -2, -2 });
		check(".", 0 | Pattern.MULTILINE, "\n", new int[] { -2, -2 });
		check(".", 0 | Pattern.MULTILINE | Pattern.DOTALL, "\n", new int[] { 0, 1, -2, -2 });
		check(".", 0 | Pattern.MULTILINE | Pattern.DOTALL, "\n", new int[] { 0, 1, -2, -2 });
		check(".", 0 | Pattern.MULTILINE, "\n", new int[] { -2, -2 });
		check(".", 0 | Pattern.MULTILINE, "\n", new int[] { -2, -2 });
		check("(?-s).", 0 | Pattern.MULTILINE | Pattern.DOTALL, "\n", new int[] { -2, -2 });
		check("(?-s).", 0 | Pattern.MULTILINE, "\n", new int[] { -2, -2 });
		check("(?-xism)d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "d", new int[] { 0, 1, -2, -2 });

		report("test_options2");
	}

	private static void test_options3() {
		check(".+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "  \n  ", new int[] { 0, 5, -2, -2 });
		check(".+", 0 | Pattern.MULTILINE, "  \n  ", new int[] { 0, 2, -2, 3, 5, -2, -2 });
		check(".+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "  \n  ", new int[] { 0, 5, -2, -2 });
		check(".+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "  \n  ", new int[] { 0, 5, -2, -2 });
		check(".+", 0 | Pattern.MULTILINE, "  \n  ", new int[] { 0, 2, -2, 3, 5, -2, -2 });
		check(".+", 0 | Pattern.MULTILINE, "  \n  ", new int[] { 0, 2, -2, 3, 5, -2, -2 });
		check("(?-s).+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "  \n  ", new int[] { 0, 2, -2, 3, 5, -2, -2 });
		check("(?-s).+", 0 | Pattern.MULTILINE, "  \n  ", new int[] { 0, 2, -2, 3, 5, -2, -2 });

		report("test_options3");
	}

	private static void test_mark_resets() {

		report("test_mark_resets");
	}

	private static void test_recursion() {
		check("(a(?1)b)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { -2, -2 });
		check("(a(?1)+b)", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { -2, -2 });
		check("^([^()]|\\((?1)*\\))*$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 3, -2, -2 });
		check("^([^()]|\\((?1)*\\))*$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a(b)c", new int[] { 0, 5, -2, -2 });
		check("^([^()]|\\((?1)*\\))*$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a(b(c))d", new int[] { 0, 8, -2, -2 });
		check("^([^()]|\\((?1)*\\))*$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "a(b(c)d", new int[] { -2, -2 });
		check("^\\>abc\\>([^()]|\\((?1)*\\))*\\<xyz\\<$", 0 | Pattern.MULTILINE | Pattern.DOTALL, ">abc>123<xyz<",
				new int[] { 0, 13, -2, -2 });
		check("^\\>abc\\>([^()]|\\((?1)*\\))*\\<xyz\\<$", 0 | Pattern.MULTILINE | Pattern.DOTALL, ">abc>1(2)3<xyz<",
				new int[] { 0, 15, -2, -2 });
		check("^\\>abc\\>([^()]|\\((?1)*\\))*\\<xyz\\<$", 0 | Pattern.MULTILINE | Pattern.DOTALL, ">abc>(1(2)3)<xyz<",
				new int[] { 0, 17, -2, -2 });
		check("^\\W*(?:((.)\\W*(?1)\\W*(?<-2>\\2)|)|((.)\\W*(?3)\\W*(?<-4>\\4)|\\W*.\\W*))\\W*$",
				0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL,
				"Satan, oscillate my metallic sonatas!", new int[] { 0, 37, -2, -2 });
		check("^\\W*(?:((.)\\W*(?1)\\W*(?<-2>\\2)|)|((.)\\W*(?3)\\W*(?<-4>\\4)|\\W*.\\W*))\\W*$",
				0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "The quick brown fox",
				new int[] { -2, -2 });
		check("^(\\d+|\\((?1)([+*-])(?1)\\)|-(?1))$", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL,
				"12", new int[] { 0, 2, -2, -2 });
		check("^(\\d+|\\((?1)([+*-])(?1)\\)|-(?1))$", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL,
				"-12", new int[] { 0, 3, -2, -2 });
		check("^(\\d+|\\((?1)([+*-])(?1)\\)|-(?1))$", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL,
				"((2+2)*-3)-7)", new int[] { -2, -2 });
		check("^(x(y|(?1){2})z)", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "xyz",
				new int[] { 0, 3, -2, -2 });
		check("^(x(y|(?1){2})z)", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "xxyzxyzz",
				new int[] { 0, 8, -2, -2 });
		check("^(x(y|(?1){2})z)", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "xxyzz",
				new int[] { -2, -2 });
		check("^(x(y|(?1){2})z)", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "xxyzxyzxyzz",
				new int[] { -2, -2 });
		check("^(a|b|c)=(?1)+", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "a=a",
				new int[] { 0, 3, -2, -2 });
		check("^(a|b|c)=(?1)+", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "a=b",
				new int[] { 0, 3, -2, -2 });
		check("^(a|b|c)=(?1)+", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "a=bc",
				new int[] { 0, 4, -2, -2 });
		check("^(a|b|c)=((?1))+", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "a=a",
				new int[] { 0, 3, -2, -2 });
		check("^(a|b|c)=((?1))+", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "a=b",
				new int[] { 0, 3, -2, -2 });
		check("^(a|b|c)=((?1))+", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "a=bc",
				new int[] { 0, 4, -2, -2 });
		check("(?<abc>a|b)(?<doe>d|e)(?'abc'){2}", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "bdaa",
				new int[] { 0, 4, -2, -2 });
		check("(?<abc>a|b)(?<doe>d|e)(?'abc'){2}", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "bdab",
				new int[] { 0, 4, -2, -2 });
		check("(?<abc>a|b)(?<doe>d|e)(?'abc'){2}", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "bddd",
				new int[] { -2, -2 });
		check("(?<abc>a|b)(?<doe>d|e)(?'abc'){2}", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "bdaa",
				new int[] { 0, 4, -2, -2 });
		check("(?<abc>a|b)(?<doe>d|e)(?'abc'){2}", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "bdab",
				new int[] { 0, 4, -2, -2 });
		check("(?<abc>a|b)(?<doe>d|e)(?'abc'){2}", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "bddd",
				new int[] { -2, -2 });
		check("^(?<ab>a)? (?(ab)b|c) (?(ab)d|e)", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "abd",
				new int[] { 0, 3, 0, 1, -2, -2 });
		check("^(?<ab>a)? (?(ab)b|c) (?(ab)d|e)", 0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "ce",
				new int[] { 0, 2, -1, -1, -2, -2 });
		check("namespace\\s+(\\w+)\\s+(\\{(?:[^{}]*(?:(?2)[^{}]*)*)?\\})", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"namespace one { namespace two { int foo(); } }", new int[] { 0, 46, -2, -2 });
		check("namespace\\s+(\\w+)\\s+(\\{(?:[^{}]*(?:(?2)[^{}]*)*)?\\})", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				"namespace one { namespace two { int foo(){} } { {{{ }  } } } {}}", new int[] { 0, 64, -2, -2 });

		check("^(?(DEFINE) (?<A> a) (?<B> b) )  (?'A') (?'B') ",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "abcd", new int[] { 0, 2, -2, -2 });
		check("(?(DEFINE)(?<byte>2[0-4]\\d|25[0-5]|1\\d\\d|[1-9]?\\d))\\b(?'byte')(\\.(?'byte')){3}",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "1.2.3.4", new int[] { 0, 7, -2, -2 });
		check("(?(DEFINE)(?<byte>2[0-4]\\d|25[0-5]|1\\d\\d|[1-9]?\\d))\\b(?'byte')(\\.(?'byte')){3}",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "131.111.10.206",
				new int[] { 0, 14, -2, -2 });
		check("(?(DEFINE)(?<byte>2[0-4]\\d|25[0-5]|1\\d\\d|[1-9]?\\d))\\b(?'byte')(\\.(?'byte')){3}",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "10.0.0.0", new int[] { 0, 8, -2, -2 });
		check("(?(DEFINE)(?<byte>2[0-4]\\d|25[0-5]|1\\d\\d|[1-9]?\\d))\\b(?'byte')(\\.(?'byte')){3}",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "10.6", new int[] { -2, -2 });
		check("(?(DEFINE)(?<byte>2[0-4]\\d|25[0-5]|1\\d\\d|[1-9]?\\d))\\b(?'byte')(\\.(?'byte')){3}",
				0 | Pattern.MULTILINE | Pattern.DOTALL | Pattern.COMMENTS, "455.3.4.5", new int[] { -2, -2 });

		report("test_recursion");
	}

	private static void test_non_greedy_repeats() {
		check("^a*?$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aa", new int[] { 0, 2, -2, -2 });
		check("^.*?$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aa", new int[] { 0, 2, -2, -2 });
		check("^(a)*?$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aa", new int[] { 0, 2, 1, 2, -2, -2 });
		check("^[ab]*?$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aa", new int[] { 0, 2, -2, -2 });
		check("a+?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aa", new int[] { 0, 1, -2, 1, 2, -2, -2 });
		check("a{1,3}?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaa", new int[] { 0, 1, -2, 1, 2, -2, 2, 3, -2, -2 });
		check("\\w+?w", 0 | Pattern.MULTILINE | Pattern.DOTALL, "...ccccccwcccccw",
				new int[] { 3, 10, -2, 10, 16, -2, -2 });
		check("\\W+\\w+?w", 0 | Pattern.MULTILINE | Pattern.DOTALL, "...ccccccwcccccw", new int[] { 0, 10, -2, -2 });
		check("abc|\\w+?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abd",
				new int[] { 0, 1, -2, 1, 2, -2, 2, 3, -2, -2 });
		check("abc|\\w+?", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcd", new int[] { 0, 3, -2, 3, 4, -2, -2 });
		check("\\<\\s*tag[^>]*\\>(.*?)\\<\\s*/tag\\s*\\>", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				" <tag>here is some text</tag> <tag></tag>", new int[] { 1, 29, 6, 23, -2, 30, 41, 35, 35, -2, -2 });
		check("\\<\\s*tag[^>]*\\>(.*?)\\<\\s*/tag\\s*\\>", 0 | Pattern.MULTILINE | Pattern.DOTALL,
				" < tag attr=\"something\">here is some text< /tag > <tag></tag>",
				new int[] { 1, 49, 24, 41, -2, 50, 61, 55, 55, -2, -2 });
		check("xx-{0,2}?(?:[+-][0-9])??\\z", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xx--",
				new int[] { 0, 4, -2, -2 });
		check("xx.{0,2}?(?:[+-][0-9])??\\z", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xx--",
				new int[] { 0, 4, -2, -2 });
		check("xx.{0,2}?(?:[+-][0-9])??\\z", 0 | Pattern.MULTILINE, "xx--", new int[] { 0, 4, -2, -2 });
		check("xx[/-]{0,2}?(?:[+-][0-9])??\\z", 0 | Pattern.MULTILINE | Pattern.DOTALL, "xx--",
				new int[] { 0, 4, -2, -2 });

		report("test_non_greedy_repeats");
	}

	private static void test_grep() {
		check("a", 0 | Pattern.MULTILINE | Pattern.DOTALL, " a a a aa",
				new int[] { 1, 2, -2, 3, 4, -2, 5, 6, -2, 7, 8, -2, 8, 9, -2, -2 });
		check("a+b+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aabaabbb ab",
				new int[] { 0, 3, -2, 3, 8, -2, 9, 11, -2, -2 });
		check("a(b*|c|e)d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "adabbdacd",
				new int[] { 0, 2, -2, 2, 6, -2, 6, 9, -2, -2 });
		check("a", 0 | Pattern.MULTILINE | Pattern.DOTALL, "\na\na\na\naa",
				new int[] { 1, 2, -2, 3, 4, -2, 5, 6, -2, 7, 8, -2, 8, 9, -2, -2 });
		check("^ab", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ab  \nab  ab\n", new int[] { 0, 2, -2, 5, 7, -2, -2 });
		check("^[^\\n]*\n", 0 | Pattern.MULTILINE | Pattern.DOTALL, "   \n  \n\n  \n",
				new int[] { 0, 4, -2, 4, 7, -2, 7, 8, -2, 8, 11, -2, -2 });
		check("\\babc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcabc abc\n\nabc",
				new int[] { 0, 3, -2, 7, 10, -2, 12, 15, -2, -2 });
		check("\\b\\w+\\W+", 0 | Pattern.MULTILINE | Pattern.DOTALL, " aa  aa  a ",
				new int[] { 1, 5, -2, 5, 9, -2, 9, 11, -2, -2 });
		check("\\Aabc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc   abc", new int[] { 0, 3, -2, -2 });
		check("\\G\\w+\\W+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc  abc a cbbb   ",
				new int[] { 0, 5, -2, 5, 9, -2, 9, 11, -2, 11, 18, -2, -2 });
		check("\\Ga+b+", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aaababb  abb", new int[] { 0, 4, -2, 4, 7, -2, -2 });
		check("abc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { 0, 3, -2, -2 });
		check("abc", 0 | Pattern.MULTILINE | Pattern.DOTALL, " abc abcabc",
				new int[] { 1, 4, -2, 5, 8, -2, 8, 11, -2, -2 });
		check("\\n\\n", 0 | Pattern.MULTILINE | Pattern.DOTALL, " \n\n\n       \n      \n\n\n\n  ",
				new int[] { 1, 3, -2, 18, 20, -2, 20, 22, -2, -2 });
		check("$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "   \n\n  \n\n\n",
				new int[] { 3, 3, -2, 4, 4, -2, 7, 7, -2, 8, 8, -2, 9, 9, -2, 10, 10, -2, -2 });
		check("A", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, " a a a aa",
				new int[] { 1, 2, -2, 3, 4, -2, 5, 6, -2, 7, 8, -2, 8, 9, -2, -2 });
		check("A+B+", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "aabaabbb ab",
				new int[] { 0, 3, -2, 3, 8, -2, 9, 11, -2, -2 });
		check("A(B*|c|e)D", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "adabbdacd",
				new int[] { 0, 2, -2, 2, 6, -2, 6, 9, -2, -2 });
		check("A", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "\na\na\na\naa",
				new int[] { 1, 2, -2, 3, 4, -2, 5, 6, -2, 7, 8, -2, 8, 9, -2, -2 });
		check("^aB", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "Ab  \nab  Ab\n",
				new int[] { 0, 2, -2, 5, 7, -2, -2 });
		check("\\babc", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "Abcabc aBc\n\nabc",
				new int[] { 0, 3, -2, 7, 10, -2, 12, 15, -2, -2 });
		check("ABC", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, "abc",
				new int[] { 0, 3, -2, -2 });
		check("abc", 0 | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL, " ABC ABCABC ",
				new int[] { 1, 4, -2, 5, 8, -2, 8, 11, -2, -2 });
		check("a|\\Ab", 0 | Pattern.MULTILINE | Pattern.DOTALL, "b ab", new int[] { 0, 1, -2, 2, 3, -2, -2 });
		check("a|^b", 0 | Pattern.MULTILINE | Pattern.DOTALL, "b ab\nb",
				new int[] { 0, 1, -2, 2, 3, -2, 5, 6, -2, -2 });
		check("a|\\bb", 0 | Pattern.MULTILINE | Pattern.DOTALL, "b ab\nb",
				new int[] { 0, 1, -2, 2, 3, -2, 5, 6, -2, -2 });
		check("\\Aabc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcabc", new int[] { 0, 3, -2, -2 });
		check("^abc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcabc", new int[] { 0, 3, -2, -2 });
		check("\\babc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcabc", new int[] { 0, 3, -2, -2 });
		check("\\babc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcabc", new int[] { 0, 3, -2, -2 });
		check("(?<=\\Aabc)?abc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcabc",
				new int[] { 0, 3, -2, 3, 6, -2, -2 });
		check("(?<=^abc)?abc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcabc", new int[] { 0, 3, -2, 3, 6, -2, -2 });
		check("(?<=\\babc)?abc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcabc",
				new int[] { 0, 3, -2, 3, 6, -2, -2 });
		check("(?<=\\babc)?abc", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcabc",
				new int[] { 0, 3, -2, 3, 6, -2, -2 });
		check("(?<=^).{2}|(?<=^.{3}).{2}", 0 | Pattern.MULTILINE | Pattern.DOTALL, "123456789",
				new int[] { 0, 2, -2, 3, 5, -2, -2 });

		report("test_grep");
	}

	private static void test_backrefs() {
		check("a(b*)c\\1d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbbd", new int[] { 0, 7, 1, 3, -2, -2 });
		check("a(b*)c\\1d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbd", new int[] { -2, -2 });
		check("a(b*)c\\1d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbbbd", new int[] { -2, -2 });
		check("^(.)\\1", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { -2, -2 });
		check("a([bc])\\1d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcdabbd", new int[] { 4, 8, 5, 6, -2, -2 });
		check("a(([bc])\\2)*d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbccd",
				new int[] { 0, 6, 3, 5, 3, 4, -2, -2 });
		check("a(([bc])\\2)*d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbd", new int[] { -2, -2 });
		check("a((b)*\\2)*d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbbd", new int[] { 0, 5, 1, 4, 2, 3, -2, -2 });
		check("(ab*)[ab]*\\1", 0 | Pattern.MULTILINE | Pattern.DOTALL, "ababaaa",
				new int[] { 0, 4, 0, 2, -2, 4, 7, 4, 5, -2, -2 });
		check("(a)\\1bcd", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aabcd", new int[] { 0, 5, 0, 1, -2, -2 });
		check("(a)\\1bc*d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aabcd", new int[] { 0, 5, 0, 1, -2, -2 });
		check("(a)\\1bc*d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aabd", new int[] { 0, 4, 0, 1, -2, -2 });
		check("(a)\\1bc*d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aabcccd", new int[] { 0, 7, 0, 1, -2, -2 });
		check("(a)\\1bc*[ce]d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aabcccd", new int[] { 0, 7, 0, 1, -2, -2 });
		check("^(a)\\1b(c)*cd$", 0 | Pattern.MULTILINE | Pattern.DOTALL, "aabcccd",
				new int[] { 0, 7, 0, 1, 4, 5, -2, -2 });
		check("a(b*)c\\1d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbbd", new int[] { 0, 7, 1, 3, -2, -2 });
		check("a(b*)c\\1d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbd", new int[] { -2, -2 });
		check("a(b*)c\\1d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbbbd", new int[] { -2, -2 });
		check("^(.)\\1", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { -2, -2 });
		check("a([bc])\\1d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcdabbd", new int[] { 4, 8, 5, 6, -2, -2 });
		check("a(b*)c\\1d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbbd", new int[] { 0, 7, 1, 3, -2, -2 });
		check("a(b*)c\\1d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbd", new int[] { -2, -2 });
		check("a(b*)c\\1d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbbbd", new int[] { -2, -2 });
		check("^(.)\\1", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { -2, -2 });
		check("a([bc])\\1d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcdabbd", new int[] { 4, 8, 5, 6, -2, -2 });
		check("^(.)\\1", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { -2, -2 });
		check("a([bc])\\1d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcdabbd", new int[] { 4, 8, 5, 6, -2, -2 });
		check("a(?<foo>(?<bar>(?<bb>(?<aa>b*))))c\\k<foo>d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbbd",
				new int[] { 0, 7, 1, 3, 1, 3, 1, 3, 1, 3, -2, -2 });
		check("a(?<foo>(?<bar>(?<bb>(?<aa>b*))))c\\k<foo>d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbd",
				new int[] { -2, -2 });
		check("a(?<foo>(?<bar>(?<bb>(?<aa>b*))))c\\k<foo>d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbbbd",
				new int[] { -2, -2 });
		check("^(?<foo>.)\\k<foo>", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { -2, -2 });
		check("a(?<foo>[bc])\\k<foo>d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcdabbd",
				new int[] { 4, 8, 5, 6, -2, -2 });
		check("a(?<foo>(?<bar>(?<bb>(?<aa>b*))))c\\k<foo>d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbbd",
				new int[] { 0, 7, 1, 3, 1, 3, 1, 3, 1, 3, -2, -2 });
		check("a(?<foo>(?<bar>(?<bb>(?<aa>b*))))c\\k<foo>d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbd",
				new int[] { -2, -2 });
		check("a(?<foo>(?<bar>(?<bb>(?<aa>b*))))c\\k<foo>d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abbcbbbd",
				new int[] { -2, -2 });
		check("^(?<foo>.)\\k<foo>", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abc", new int[] { -2, -2 });
		check("a(?<foo>[bc])\\k<foo>d", 0 | Pattern.MULTILINE | Pattern.DOTALL, "abcdabbd",
				new int[] { 4, 8, 5, 6, -2, -2 });

		report("test_backrefs");
	}

}
