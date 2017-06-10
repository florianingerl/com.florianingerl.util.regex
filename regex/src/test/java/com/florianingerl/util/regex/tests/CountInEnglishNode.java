package com.florianingerl.util.regex.tests;

import com.florianingerl.util.regex.*;

public class CountInEnglishNode extends Pattern.CustomNode {

	private static final String[] NUMBERS = new String[] { "zero", "one", "two", "three", "four", "five", "six",
			"seven", "eight", "nine", "ten" };
	private static int minLength = 3;
	private static int maxLength = 5;

	@Override
	protected boolean match(Matcher matcher, int i, CharSequence seq) {
		int j = 0;
		Object data = retrieveData(matcher);
		if (data != null) {
			j = (int) data;
		}
		String number = NUMBERS[++j];
		for (int k = 0; k < number.length(); k++, i++) {
			if (i >= seq.length() || number.charAt(k) != seq.charAt(i))
				return false;
		}
		storeData(matcher, j);
		boolean r = matchNext(matcher, i, seq);
		storeData(matcher, j - 1);
		return r;

	}

	@Override
	protected int minLength() {
		return minLength;
	}

	@Override
	protected int maxLength() {
		return maxLength;
	}

	@Override
	protected boolean isMaxValid() {
		return true;
	}

	@Override
	protected boolean isDeterministic() {
		return true;
	}
}
