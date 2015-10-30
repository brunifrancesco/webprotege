package edu.stanford.bmir.protege.web.server.xlodrestmodels;

/***
 * Simple model to serialize a response object for WebProtege/XLod APIs
 * @author Francesco Bruni <brunifrancesco02@gmail.com>
 *
 */
public class Response{

	private String message;
	
	public Response(String message) {
		super();
		this.message = message;
	}
	public Response(){}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
