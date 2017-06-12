package com.florianingerl.util.regex;

public class CaptureTree {

	private CaptureTreeNode root;

	public CaptureTree(CaptureTreeNode root) {
		this.root = root;
	}

	public CaptureTreeNode getRoot() {
		return root;
	}

	public String toString() {
		return getRoot().toString();
	}

}
