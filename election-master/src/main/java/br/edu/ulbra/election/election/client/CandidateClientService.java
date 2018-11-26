package br.edu.ulbra.election.election.client;

import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class CandidateClientService {

    private final CandidateClient candidateClient;

    @Autowired
    public CandidateClientService(CandidateClient candidateClient){
        this.candidateClient = candidateClient;
    }

    public List<CandidateOutput> getAll(){
        return this.candidateClient.getAll();
    }
    
    public List<CandidateOutput> getByElectionId(Long electionId) {
        return this.candidateClient.getByElectionId(electionId);
    }

    public CandidateOutput getById(Long id){
		return this.candidateClient.getById(id);
	}
    
    public CandidateOutput verificaElection(Long id) {
		return this.candidateClient.verificaElection(id);
	}

	public CandidateOutput verificaNumero(Long election, Long numero) {
		return this.candidateClient.verificaNumero(election, numero);
	}
    
    @FeignClient(name="candidate-service", url="http://localhost:8082")
    private interface CandidateClient {

        @GetMapping("/v1/candidate/")
        List<CandidateOutput> getAll();
        
        @GetMapping("/v1/candidate/election/{electionId}")
        List<CandidateOutput> getByElectionId(@PathVariable(name = "electionId") Long electionId);

        @GetMapping("/v1/candidate/{candidateId}")
		CandidateOutput getById(@PathVariable(name = "candidateId") Long candidateId);
        
        @GetMapping("/v1/candidate/getCandidateElection/{electionId}")
		CandidateOutput verificaElection(@PathVariable(name = "electionId") Long electionId);

		@GetMapping("/v1/candidate/getByElectionAndNumber/{numberElection}/{electionId}")
		CandidateOutput verificaNumero(@PathVariable(name = "numberElection") Long numberElection,
				@PathVariable(name = "electionId") Long electionId);
    }
}
