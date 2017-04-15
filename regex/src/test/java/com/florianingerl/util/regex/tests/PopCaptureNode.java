package com.florianingerl.util.regex.tests;

import com.florianingerl.util.regex.*;

public class PopCaptureNode extends Pattern.CustomNode {

	private String groupName;
	
	public PopCaptureNode(String groupName)
	{
		this.groupName = groupName;
	}
	
	@Override
	public boolean match(Matcher matcher, int i, CharSequence seq)
	{
		if(matcher.captures(groupName) == null || matcher.captures(groupName).isEmpty() ) {
			return false;
		}
		
		Capture c = matcher.captures(groupName).pop();
		if(!matchNext(matcher, i, seq) )
		{
			matcher.captures(groupName).push(c);
			return false;
		}
		return true;
	}

	@Override
	protected int minLength() {
		return 0;
	}

	@Override
	protected int maxLength() {
		return 0;
	}

	@Override
	protected boolean isMaxValid() {
		return true;
	}
	
}
