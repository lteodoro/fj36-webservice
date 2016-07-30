package br.com.caelum.payfast.oauth2;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TokenDao {
	
	private List<String> accessTokens = new ArrayList<>();
	private List<String> authorizationCodes = new ArrayList<>();
	
	public void adicionaAccessToken(String token) {
		accessTokens.add(token);
	}
	
	public boolean existeAccessToken(String token) {
		return accessTokens.contains(token);
	}
	
	public void adicionaAuthorizationCode(String code) {
		authorizationCodes.add(code);
	}
	
	public boolean existeAuthorizationCode(String code) {
		return authorizationCodes.contains(code);
	}
	
}
