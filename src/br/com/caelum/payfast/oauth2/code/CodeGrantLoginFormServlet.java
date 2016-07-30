package br.com.caelum.payfast.oauth2.code;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

@WebServlet("/oauth/code/form")
public class CodeGrantLoginFormServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			HttpSession session = req.getSession();
			OAuthAuthzRequest oauthReq = new OAuthAuthzRequest(req);
			String clientId = oauthReq.getClientId();
			String redirectURI = oauthReq.getRedirectURI();
			
			if("livraria_id".equals(clientId)){
				session.setAttribute("redirectURI", redirectURI);
				req.getRequestDispatcher("/login.jsp").forward(req, resp);
			} else {
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
