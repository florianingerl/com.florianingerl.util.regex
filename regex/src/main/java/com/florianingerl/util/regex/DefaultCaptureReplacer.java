package com.florianingerl.util.regex;

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

	@Override
	public String replace(CaptureTreeNode node) {
		StringBuffer sb = new StringBuffer();
		int j = node.getCapture().getStart();
		for (CaptureTreeNode child : node.getChildren()) {
			sb.append(input.subSequence(j, child.getCapture().getStart()));
			sb.append(replace(child));
			j = child.getCapture().getEnd();
		}
		sb.append(input.subSequence(j, node.getCapture().getEnd()));
		return sb.toString();
	}

}
