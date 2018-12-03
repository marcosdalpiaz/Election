package br.edu.ulbra.election.election.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.edu.ulbra.election.election.output.v1.VoterOutput;

@Service
public class LoginClientService {

	private final LoginClient loginClient;
	
	@Autowired
	public LoginClientService(LoginClient loginClient) {
		this.loginClient = loginClient;
	}
	
	public VoterOutput checkToken(@PathVariable(value = "token") String token){
        return loginClient.checkToken(token);
    }
	
	@FeignClient(value = "candidate-service", url = "${url.login-service}")
	private interface LoginClient {
		@GetMapping("/login/v1/check/{token}")
	    public VoterOutput checkToken(@PathVariable(value = "token") String token);
	}
}
