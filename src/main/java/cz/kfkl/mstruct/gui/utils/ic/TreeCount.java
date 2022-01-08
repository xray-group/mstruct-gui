package cz.kfkl.mstruct.gui.utils.ic;

import java.util.LinkedHashMap;
import java.util.Map;

import cz.kfkl.mstruct.gui.utils.validation.UnexpectedException;

public class TreeCount implements Count {
	private IC owner;
	private TreeCount parent;
	private String name;
	private String prefix;

	private long startTime = -1;

	long elapsed = 0;
	long maxElapsed = 0;
	int count = 0;

	Map<String, TreeCount> children = new LinkedHashMap<>();

	public TreeCount() {
	}

	public TreeCount(IC owner, TreeCount parent, String name) {
		this.owner = owner;
		this.parent = parent;
		this.name = name;

		parent.children.put(name, this);

		String parentPrefix = parent.prefix;
		this.prefix = (parentPrefix == null ? "" : parentPrefix + ".") + parent.children.size();
	}

	void start() {
		if (startTime > 0) {
			throw new UnexpectedException("StatCount [" + name + "] is already started.");
		}
		count++;
		startTime = System.nanoTime();
	}

	@Override
	public long stop() {
		if (startTime < 0) {
			throw new UnexpectedException("Cannot stop StatCount [" + name + "] as it wasn't started.");
		}
		long newElapsed = System.nanoTime() - startTime;

		elapsed += newElapsed;
		if (newElapsed > maxElapsed) {
			maxElapsed = newElapsed;
		}
		startTime = -1;
		owner.parentCount = parent;

		return newElapsed / 1000000;
	}

	public String getPrefixedName() {
		return prefix + " " + name;
	}

	int getNumberOfCounts() {
		int num = 1;
		for (TreeCount c : children.values()) {
			num += c.getNumberOfCounts();
		}

		return num;
	}

}
