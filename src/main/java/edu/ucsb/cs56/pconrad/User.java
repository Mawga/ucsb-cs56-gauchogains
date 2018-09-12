package edu.ucsb.cs56.pconrad;

public class User {
	private String email;
	private String firstName;
	private String lastName;
	private String password;

	public User (String email, String firstName, String lastName,
			String password) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
	}
	public String getFirstName() {
		return this.firstName;
	}
	public String getLastName() {
		return this.lastName;
	}
	public String getPassword() {
		return this.password;
	}
	public String getEmail() {
		return this.email;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
                this.lastName = lastName;
        }
	public void setEmail(String email) {
                this.email = email;
        }
	public void setPassword(String password) {
                this.password = password;
        }
	
	@Override
	public String toString() {
		return this.email + " " + this.firstName + " " + this.lastName;
	}
}
