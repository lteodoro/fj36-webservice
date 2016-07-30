package br.com.caelum.payfast.oauth2.code;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.message.OAuthResponse;

import br.com.caelum.payfast.oauth2.TokenDao;

@WebServlet("/oauth/code/token")
public class CodeGrantTokenServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject
	private TokenDao tokenDao;
	
	@SuppressWarnings("null")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			OAuthTokenRequest oauthRequest = new OAuthTokenRequest(req);
			String clientId = oauthRequest.getClientId();
			String secret = oauthRequest.getClientSecret();
			String code = oauthRequest.getCode();
			
			// Valida o authorization code
			if(!tokenDao.existeAuthorizationCode(code)){
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			// Valida as credenciais do client application
			if(!"livraria_id".equals(clientId) || !"livraria_secret".equals(secret)){
				resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			
			OAuthResponse oAuthResponse = null;
			// C�digo para gerar o access token
			OAuthIssuer issuer = new OAuthIssuerImpl(new MD5Generator());
			String accessToken = issuer.accessToken();
			tokenDao.adicionaAccessToken(accessToken);
			oAuthResponse = OAuthASResponse
					.tokenResponse(HttpServletResponse.SC_OK)
					.setAccessToken(accessToken)
					.setTokenType("Bearer")
					.buildJSONMessage();
			
			// Envia o access token para o client application
			resp.setHeader("Content-type", "application/json");
			resp.getWriter().print(oAuthResponse.getBody());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
}
