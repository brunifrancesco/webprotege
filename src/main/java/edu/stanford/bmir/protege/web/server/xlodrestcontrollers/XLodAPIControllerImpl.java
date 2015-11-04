package edu.stanford.bmir.protege.web.server.xlodrestcontrollers;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import edu.stanford.bmir.protege.web.server.xlodrestmodels.Request;
import edu.stanford.bmir.protege.web.server.xlodrestmodels.Response;
import edu.stanford.bmir.protege.web.server.xlodservices.XLodUserService;
import edu.stanford.bmir.protege.web.server.xlodservices.XLodUserServiceImpl;
import edu.stanford.bmir.protege.web.shared.user.UserEmailAlreadyExistsException;

public class XLodAPIControllerImpl extends HttpServlet implements XLodAPIController{
	
	private static final long serialVersionUID = 4786451351409040357L;
	private XLodUserService xLodUserService;
    
	//HttpServlet is not singleton, no way to use DI
	public XLodAPIControllerImpl() {
		xLodUserService = XLodUserServiceImpl.getInstance();
	}
	
	/***
	 * Handle update user data which came from XLOD. 
	 * This controller maps the URL /api/registeruser with a POST method.
	 * Return appropriate status code, based on success/fail of registration mechanism.
	 */
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType(javax.ws.rs.core.MediaType.APPLICATION_JSON);
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = req.getReader();
		//TODO handle better body parsing
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} finally {
			reader.close();
		}
		Gson gson = new Gson();
		try{
			Request data = gson.fromJson(sb.toString(), Request.class);
			xLodUserService.updateUserXLod(data);
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println(new Gson().toJson(new Response("Done!")));
		}catch(JsonSyntaxException jse){
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().println(new Gson().toJson(new Response("Malformed JSON")));
		}catch(UserPrincipalNotFoundException unte){
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			resp.getWriter().println(new Gson().toJson(new Response("User not found! Is the username correct?"))); 
		}catch(Exception e){
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().println(new Gson().toJson(new Response("Unexpected error.")));
		}
	}

	/***
	 * Handle new user signing up whose data came from XLOD. 
	 * This controller maps the URL /api/registeruser with a POST method.
	 * Return appropriate status code, based on success/fail of registration mechanism.
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType(javax.ws.rs.core.MediaType.APPLICATION_JSON);
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = req.getReader();
		//TODO handle better body parsing
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} finally {
			reader.close();
		}
		Gson gson = new Gson();
		try{
			Request data = gson.fromJson(sb.toString(), Request.class);
			xLodUserService.registerUserByXLod(data);
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println(new Gson().toJson(new Response("Done!")));
		}catch(JsonSyntaxException jse){
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().println(new Gson().toJson(new Response("Malformed JSON")));
		}catch (UserEmailAlreadyExistsException ue) {
			resp.setStatus(HttpServletResponse.SC_CONFLICT);
			resp.getWriter().println(new Gson().toJson(new Response("User seems already signed up. Please check data.")));
		}catch(Exception e){
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().println(new Gson().toJson(new Response("Unexpected error. The user already signed up, is it?")));
		}
			
	}

}
