package com.ingerlflori.util.regex;

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
