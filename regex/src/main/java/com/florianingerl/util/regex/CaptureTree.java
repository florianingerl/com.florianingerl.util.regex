package com.florianingerl.util.regex;

/**
 * Contains all captures made during the previous match operation of all
 * <a href="Pattern.html#cg">capturing groups</a> in a hierarchical data
 * structure.
 * 
 * <p>
 * E.g.
 * </p>
 * 
 * <pre>
 * Matcher matcher = Pattern.compile("(?x)" + "(?(DEFINE)" + "(?&lt;sum&gt; (?'summand')(?:\\+(?'summand'))+ )"
 * 		+ "(?&lt;summand&gt;  (?'product') | (?'number') )" + "(?&lt;product&gt; (?'factor')(?:\\*(?'factor'))+ )"
 * 		+ "(?&lt;factor&gt;(?'number') )" + "(?&lt;number&gt;\\d++)" + ")" + "(?'sum')").matcher("5+6*8");
 * matcher.matches();
 * System.out.println(matcher.captureTree());
 * </pre>
 * 
 * prints out
 * 
 * <pre>
 * 0
 *	sum
 *		summand
 *			number
 *		summand
 *			product
 *				factor
 *					number
 *				factor
 *					number
 * </pre>
 * 
 * @author Florian Ingerl
 * @see Matcher#captureTree()
 */
public class CaptureTree {

	private CaptureTreeNode root;

	CaptureTree(CaptureTreeNode root) {
		this.root = root;
	}

	/**
	 * Returns the root of this capture tree
	 * 
	 * @return The root of this capture tree
	 */
	public CaptureTreeNode getRoot() {
		return root;
	}

	/**
	 * Returns a string representation of this capture tree
	 * 
	 * <p>
	 * Don't rely on the implementation of this method to remain unchanged
	 * </p>
	 * 
	 * @return A string representation of this capture tree
	 */
	public String toString() {
		return getRoot().toString();
	}

}
