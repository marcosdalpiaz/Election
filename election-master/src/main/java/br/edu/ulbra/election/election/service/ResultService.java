package br.edu.ulbra.election.election.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.output.v1.ElectionCandidateResultOutput;
import br.edu.ulbra.election.election.repository.VoteRepository;
import feign.FeignException;

@Service
public class ResultService {

	private final VoteRepository voteRepository;
	private final CandidateClientService candidateClientService;

	@Autowired
	public ResultService(VoteRepository voteRepository, CandidateClientService candidateClientService) {
		this.voteRepository = voteRepository;
		this.candidateClientService = candidateClientService;
	}

	public ElectionCandidateResultOutput getResultByCandidate(Long candidateId) {

		ElectionCandidateResultOutput resultado = new ElectionCandidateResultOutput();      
		try {
			Long totVotes;
			resultado.setCandidate(candidateClientService.getById(candidateId));

			totVotes = new Long ((long) voteRepository.findByCandidateId(candidateId).size());

			if (totVotes.equals(null)) {
				resultado.setTotalVotes((long) 0);
			} else {
				resultado.setTotalVotes(totVotes);
			}

		} catch (FeignException e) {
			if (e.status() == 500) {
				throw new GenericOutputException("Candidate not found");
			}
		}
		return resultado;
	}
}