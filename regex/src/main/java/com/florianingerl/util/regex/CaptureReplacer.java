package com.florianingerl.util.regex;

public interface CaptureReplacer {

	public CharSequence getInput();

	public void setInput(CharSequence input);

	public String replace(CaptureTreeNode node);

}
