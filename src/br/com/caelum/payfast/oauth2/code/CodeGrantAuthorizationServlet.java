package br.com.caelum.payfast.oauth2.code;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.as.response.OAuthASResponse.OAuthAuthorizationResponseBuilder;
import org.apache.oltu.oauth2.common.message.OAuthResponse;

import br.com.caelum.payfast.oauth2.TokenDao;

@WebServlet("/oauth/code/authorization")
public class CodeGrantAuthorizationServlet extends HttpServlet {
	@Inject
	private TokenDao tokenDao;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			HttpSession session = req.getSession();
			String login = req.getParameter("login");
			String senha = req.getParameter("senha");
			String redirectURI = (String) session.getAttribute("redirectURI");
			
			// Valida as credenciais do usu�rio
			if("usuario".equals(login) && "senha".equals(senha)) {
				OAuthResponse oAuthResponse = null;
				// c�digo para gerar o authorization code
				OAuthIssuer issuer = new OAuthIssuerImpl(new MD5Generator());
				String code = issuer.authorizationCode();
				tokenDao.adicionaAuthorizationCode(code);
				
				OAuthAuthorizationResponseBuilder builder = OAuthASResponse
						.authorizationResponse(req, 302);
				
				oAuthResponse = builder.location(redirectURI)
						.setCode(code)
						.buildQueryMessage();
				
				// Envia o authorization code para o client application
				resp.sendRedirect(oAuthResponse.getLocationUri());
				
			} else {
				req.getRequestDispatcher("/login.jsp")
					.forward(req, resp);
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
