package edu.stanford.bmir.protege.web.server.xlodrestcontrollers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface XLodAPIController {

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
	
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
}
