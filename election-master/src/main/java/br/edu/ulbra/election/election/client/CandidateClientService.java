package br.edu.ulbra.election.election.client;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import br.edu.ulbra.election.election.output.v1.CandidateOutput;

@Service
public class CandidateClientService {

	private final CandidateClient candidateClient;

	@Autowired
	public CandidateClientService(CandidateClient candidateClient) {
		this.candidateClient = candidateClient;
	}

	public CandidateOutput verifyElection(Long id) {
		return this.candidateClient.verifyElection(id);
	}

	public CandidateOutput verifyNumber(Long election, Long numero) {
		return this.candidateClient.verifyNumber(election, numero);
	}

	public CandidateOutput getById(Long candidateId) {
		return this.candidateClient.getById(candidateId);
	}

	public ArrayList<CandidateOutput> getListCandidatesByElectionId(Long electionId) {
		return this.candidateClient.getListCandidatesByElectionId(electionId);
	}

	@FeignClient(value = "candidate-service", url = "${url.candidate-service}")
	private interface CandidateClient {

		@GetMapping("/v1/candidate/getCandidateElection/{electionId}")
		CandidateOutput verifyElection(@PathVariable(name = "electionId") Long electionId);

		@GetMapping("/v1/candidate/getByElectionAndNumber/{numberElection}/{electionId}")
		CandidateOutput verifyNumber(@PathVariable(name = "numberElection") Long numberElection,
				@PathVariable(name = "electionId") Long electionId);

		@GetMapping("/v1/candidate/{candidateId}")
		CandidateOutput getById(@PathVariable(name = "candidateId") Long candidateId);

		@GetMapping("/v1/candidate/getCandidateList/{electionId}")
		ArrayList<CandidateOutput> getListCandidatesByElectionId(@PathVariable(name = "electionId") Long electionId);
	}

}
