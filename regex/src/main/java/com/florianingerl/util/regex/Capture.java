package com.florianingerl.util.regex;

/**
 * The result of a successful capture of a <a href="Pattern.html#cg">capturing
 * group</a>.
 *
 * <p>
 * One group can be captured multiple times, e.g. if you apply the regex (a)+ to
 * the input string aaaa, then there are 4 {@link Capture} objects associated
 * with group 1.
 *
 * @author Florian Ingerl
 * @see Matcher#captureTree()
 */
public class Capture {

	private CharSequence text;
	private int start;
	private int end;

	Capture(CharSequence text, int start, int end) {
		this.text = text;
		this.start = start;
		this.end = end;
	}

	/**
	 * Returns the start index of the range of the input sequence that was
	 * captured
	 * 
	 * @return The start index of the range of the input sequence that was
	 *         captured
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Returns the end index of the range of the input sequence that was
	 * captured
	 * 
	 * @return The end index of the range of the input sequence that was
	 *         captured
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * Returns the substring of the input sequence that was captured
	 * 
	 * @return The substring of the input sequence that was captured
	 */
	public String getValue() {
		return text.subSequence(start, end).toString();
	}

}
