package br.com.caelum.payfast.oauth2.password;

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

@WebServlet("/oauth/password/token")
public class PasswordGrantTokenServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject
	private TokenDao tokenDao;

	public void doPost(HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException {
		try{
			OAuthTokenRequest oauthRequest = new OAuthTokenRequest(req);
			
			String clientId = oauthRequest.getClientId();
			String clientSecret = oauthRequest.getClientSecret();
			String username = oauthRequest.getUsername();
			String password = oauthRequest.getPassword();
			String grantType = oauthRequest.getGrantType();
			
			// o tipo de grant deve ser PASSWORD
			if ("PASSWORD".equals(grantType)) {
				res.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			// Valida��o da aplica��o cliente (livraria)
			if (!"livraria_id".equals(clientId)
					|| !"livraria_secret".equals(clientSecret)) {
				res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			
			// Valida��o do usu�rio que est� fazendo a compra
			if (!"usuario".equals(username) || !"senha".equals(password)) {
				res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			// Se tudo estiver v�lido, gera o accessToken
			String accessToken = null;
			OAuthResponse tokenResponse = null;
			
			// C�digo para gerar o access token
			OAuthIssuer issuer = new OAuthIssuerImpl(new MD5Generator());
			accessToken = issuer.accessToken();
			tokenDao.adicionaAccessToken(accessToken);
			tokenResponse = OAuthASResponse
					.tokenResponse(HttpServletResponse.SC_OK)
					.setAccessToken(accessToken)
					.setTokenType("bearer")
					.buildJSONMessage();
			
			// Envia o token para o cliente
			res.setHeader("Content-type", "application/json");
			res.getWriter().print(tokenResponse.getBody());
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		
	}
}
