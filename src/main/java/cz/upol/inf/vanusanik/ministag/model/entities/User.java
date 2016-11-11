package cz.upol.inf.vanusanik.ministag.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sys_usertable")
public class User extends BasicEntity {
	
	@Id
	@Column(nullable = false, length=32, unique = true, name = "ID")
	private String login;

	@Column(length = 64)
	private String password;
	
	@Column(length = 64)
	private String name;
	
	@Column(nullable = false)
	private Roles role;
	
	private String salt;
	
	@Override
	public String displayShort() {
		return name;
	}

	@Override
	public String getPrimaryKey() {
		return "login";
	}

	@Override
	public String getMappedName() {
		return "User";
	}

	@Override
	public Object getPrimaryKeyValue() {
		return getLogin();
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
	
}
