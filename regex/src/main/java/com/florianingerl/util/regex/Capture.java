package com.florianingerl.util.regex;

/**
 * The result of a successfull group capture.
 *
 * <p>
 * One group can be matched multiple times, e.g. if you apply the regex (a)+ to
 * the input string aaaa, then there are 4 <code>Capture</code> objects
 * associated with group 1.
 *
 * @author Florian Ingerl
 * @see Matcher
 */
public class Capture implements Cloneable {

	private CharSequence text;
	private int start;
	private int end;

	Capture(CharSequence text, int start, int end) {
		this.text = text;
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public String getValue() {
		return text.subSequence(start, end).toString();
	}

	@Override
	public Capture clone() {
		return new Capture(this.text, this.start, this.end);
	}

}
