package com.florianingerl.util.regex;

/**
 * Used to replace all captures of <a href="Pattern.html#cg">capturing
 * groups</a> recursively with a computed replacement string.
 * 
 * @author Florian Ingerl
 * @see Matcher#replaceAll(CaptureReplacer)
 */
public interface CaptureReplacer {

	/**
	 * Returns the whole input sequence
	 * 
	 * @return The whole input sequence
	 */
	public CharSequence getInput();

	/**
	 * Sets the whole input sequence
	 * 
	 * @param input
	 *            The whole input sequence
	 */
	public void setInput(CharSequence input);

	/**
	 * Computes the replacement string for the given {@link CaptureTreeNode}
	 * 
	 * @param node
	 *            The node to be replaced
	 * @return The replacement string for the given {@link CaptureTreeNode}
	 */
	public String replace(CaptureTreeNode node);

}
