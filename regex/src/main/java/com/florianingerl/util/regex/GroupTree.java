package com.florianingerl.util.regex;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GroupTree {
	public int groupIndex;
	public String groupName;
	public Capture capture;
	public List<GroupTree> children = new LinkedList<GroupTree>();
	public GroupTree parent;

	void setGroupName(Map<Integer, String> groupNames) {
		groupName = groupNames.get(groupIndex);
		for (GroupTree gt : children)
			gt.setGroupName(groupNames);
	}

	public String print() {
		StringBuilder sb = new StringBuilder();
		print(sb, 0);
		return sb.toString();
	}

	private void print(StringBuilder sb, int numTabs) {

		for (int i = 0; i < numTabs; ++i) {
			sb.append("\t");
		}
		sb.append(groupName != null ? groupName : groupIndex);
		sb.append("\n");

		for (GroupTree gt : children) {
			gt.print(sb, numTabs + 1);
		}

	}
}
