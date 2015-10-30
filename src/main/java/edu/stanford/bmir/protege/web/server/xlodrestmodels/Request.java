package edu.stanford.bmir.protege.web.server.xlodrestmodels;

/***
 * Simple model to serialize a http body request coming from XLod
 * @author Francesco Bruni <brunifrancesco02@gmail.com>
 *
 */
public class Request{

		private String username;
		private String email;
		private String password;
		
		
		public Request(String username, String email, String password) {
			super();
			this.username = username;
			this.email = email;
			this.password = password;
		}
		
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		
		
	}