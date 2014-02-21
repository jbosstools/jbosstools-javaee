package demo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="user")
@SessionScoped
public class User {
	private Map<String, User> users = new HashMap<String, User>();
	private Map<String, Collection> list = new HashMap<String, Collection>();

	private String name;

	public User() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String sayHello() {
		return "greeting";
	}

	public Map<String, User> getUsers() {
		return users;
	}

	public void setUsers(Map<String, User> users) {
		this.users = users;
	}

	public Map<String, Collection> getList() {
		return list;
	}

	public void setList(Map<String, Collection> list) {
		this.list = list;
	}
}