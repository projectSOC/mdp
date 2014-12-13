package basic;

import java.util.ArrayList;

public class Component {
	String id;
	ArrayList<String> children = new ArrayList<String>();
	
	public Component(String id){
		super();
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ArrayList<String> getChildren() {
		return children;
	}
	public void addChildren(String childId) {
		children.add(childId);
	}

}
