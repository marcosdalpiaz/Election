package br.edu.ulbra.election.election.service;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import br.edu.ulbra.election.election.output.v1.ElectionCandidateResultOutput;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.output.v1.ResultOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
import feign.FeignException;

@Service
public class ResultService {

	private final VoteRepository voteRepository;
	private final CandidateClientService candidateClientService;
	private final ElectionRepository electionRepository;

	@Autowired
	public ResultService(VoteRepository voteRepository, CandidateClientService candidateClientService, ElectionRepository electionRepository) {
		this.voteRepository = voteRepository;
		this.candidateClientService = candidateClientService;
		this.electionRepository = electionRepository;
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
	
	public ResultOutput getResultByElection(Long electionId) {

		if (electionId == null) {
			throw new GenericOutputException("Invalid id!");
		}

		Election elec = electionRepository.findFirstById(electionId);
		if (elec == null) {
			throw new GenericOutputException("Election not found!");
		}

		ResultOutput resultElection = new ResultOutput();
		List<CandidateOutput> candidatesA = new ArrayList<>();
		ArrayList<ElectionCandidateResultOutput> candidatesB = new ArrayList<>();

		Long votesValidate = (long) 0, votesBlank, votesNull;

		try {
			candidatesA = candidateClientService.getByElectionId(electionId);

			for (CandidateOutput c : candidatesA) {
				candidatesB.add(getResultByCandidate(c.getId()));
			}

			resultElection.setCandidates(candidatesB);

			for (ElectionCandidateResultOutput c : candidatesB) {
				votesValidate = votesValidate + c.getTotalVotes();
			}

			votesBlank = voteRepository.countByElectionIdAndBlankVote(electionId, true);
			votesNull = voteRepository.countByElectionIdAndNullVote(electionId, true);

			resultElection.setTotalVotes(votesValidate + votesBlank + votesNull);
			resultElection.setBlankVotes(votesBlank);
			resultElection.setNullVotes(votesNull);

			Election election = electionRepository.findFirstById(electionId);

			ElectionOutput electionOutput = new ElectionOutput();

			electionOutput.setDescription(election.getDescription());
			electionOutput.setId(election.getId());
			electionOutput.setStateCode(election.getStateCode());
			electionOutput.setYear(election.getYear());

			resultElection.setElection(electionOutput);

		} catch (FeignException e) {
			if (e.status() != 500) {
				throw new GenericOutputException(" " + e.status());
			}
		}
		return resultElection;
	}

}