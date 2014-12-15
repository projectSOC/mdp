package basic;

import java.util.ArrayList;
import java.util.List;

public class Path {
	int length;
	List<Integer> path;
	int deadline;

	public Path() {
		path = new ArrayList();
		length = 0;
		deadline = 0;
	}
	public Path(int deadline) {
		path = new ArrayList();
		length = 0;
		this.deadline = deadline;
	}
	public Path(Path p) {
		this.path = new ArrayList(p.path);
		this.length = p.length;
		this.deadline = p.deadline;
	}

	public void addStateNodeID (int stateNodeID) {
		path.add(stateNodeID);
		length ++;
	}
	public void setDeadline (int deadline) {
		this.deadline = deadline;
	}
	public int getDeadline () {
		return deadline;
	}

	public int getCurrentStateNodeID () {
		return path.get(length - 1);
	}
}