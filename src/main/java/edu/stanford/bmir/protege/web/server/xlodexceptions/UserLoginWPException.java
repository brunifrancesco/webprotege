package edu.stanford.bmir.protege.web.server.xlodexceptions;

import edu.stanford.bmir.protege.web.shared.user.UserEmailAlreadyExistsException;

public class UserLoginWPException extends UserEmailAlreadyExistsException{

	public UserLoginWPException(String reason) {
		super(reason);
	}
	
	private String reason;

    public String getReason() {
        return reason;
    }
	

}
