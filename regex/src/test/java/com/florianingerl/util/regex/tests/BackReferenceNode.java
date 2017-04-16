package com.florianingerl.util.regex.tests;

import com.florianingerl.util.regex.*;

public class BackReferenceNode extends Pattern.CustomNode {

	private String groupName;
	
	public BackReferenceNode(String groupName)
	{
		this.groupName = groupName;
	}
	@Override
	protected boolean match(Matcher matcher, int i, CharSequence seq) {
		if( matcher.captures(groupName)==null || matcher.captures(groupName).isEmpty()  )
			return false;
		String s = matcher.captures(groupName).lastElement().getValue();
		for (int k = 0; k < s.length(); k++, i++) {
			if( i >= seq.length() || s.charAt(k) != seq.charAt(i) )
				return false;
		}
		return matchNext(matcher, i, seq);
	}

	@Override
	protected int minLength() {
		return 0;
	}

	@Override
	protected int maxLength() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected boolean isMaxValid() {
		return false;
	}

}
