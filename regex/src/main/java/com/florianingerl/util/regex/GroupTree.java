package com.florianingerl.util.regex;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class GroupTree {
	public int groupIndex;
	public String groupName;
	public Capture capture;
	public List<GroupTree> children = new LinkedList<GroupTree>();
	public GroupTree parent;
	boolean recursion;

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

	Capture findGroup(int group) {
		return findGroup(group, true);
	}

	private Capture findGroup(int group, boolean searchParent) {

		ListIterator<GroupTree> it = children.listIterator(children.size());
		Capture c = findGroup(group, it);
		if (c != null)
			return c;

		if (searchParent) {
			GroupTree current = this;
			while (!current.recursion && current.parent != null) {
				current = current.parent;
				it = current.children.listIterator(current.children.size() - 1);
				c = findGroup(group, it);
				if (c != null)
					return c;
			}
		}

		return null;
	}

	private Capture findGroup(int group, ListIterator<GroupTree> it) {
		while (it.hasPrevious()) {
			GroupTree child = it.previous();
			if (child.groupIndex == group)
				return child.capture;
			if (child.recursion)
				continue;
			Capture c = child.findGroup(group, false);
			if (c != null)
				return c;
		}
		return null;
	}

}
