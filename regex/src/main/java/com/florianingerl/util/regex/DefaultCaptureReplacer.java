package com.florianingerl.util.regex;

/**
 * A reasonable default implementation of {@link CaptureReplacer}.
 * 
 * <p>
 * A {@link CaptureTreeNode} is replaced by the replacement strings of all its
 * children (ignoring those inside lookarounds) and the text in between the
 * children.
 * </p>
 * 
 * @author Florian Ingerl
 * @see Matcher#replaceAll(CaptureReplacer)
 */
public class DefaultCaptureReplacer implements CaptureReplacer {

	private CharSequence input;

	@Override
	public CharSequence getInput() {
		return input;
	}

	@Override
	public void setInput(CharSequence input) {
		this.input = input;

	}

	/**
	 * A {@link CaptureTreeNode} is replaced by the replacement strings of all
	 * its children (ignoring those inside lookarounds) and the text in between
	 * the children.
	 */
	@Override
	public String replace(CaptureTreeNode node) {
		StringBuffer sb = new StringBuffer();
		int j = node.getCapture().getStart();
		for (CaptureTreeNode child : node.getChildren()) {
			if (child.inLookaround)
				continue;
			sb.append(input.subSequence(j, child.getCapture().getStart()));
			sb.append(replace(child));
			j = child.getCapture().getEnd();
		}
		sb.append(input.subSequence(j, node.getCapture().getEnd()));
		return sb.toString();
	}

}
