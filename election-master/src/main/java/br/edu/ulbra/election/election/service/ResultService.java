package br.edu.ulbra.election.election.service;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
import feign.FeignException;
import br.edu.ulbra.election.election.output.v1.ElectionCandidateResultOutput;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.output.v1.ResultOutput;
import br.edu.ulbra.election.election.output.v1.CandidateOutput;

@Service
public class ResultService {

	private final VoteRepository voteRepository;
	private final ElectionRepository electionRepository;
	private final CandidateClientService candidateClientService;

	@Autowired
	public ResultService(VoteRepository voteRepository, ElectionRepository electionRepository,
			CandidateClientService candidateClientService) {
		this.voteRepository = voteRepository;
		this.electionRepository = electionRepository;
		this.candidateClientService = candidateClientService;
	}

	public ElectionCandidateResultOutput getResultByCandidate(Long candidateId) {

		if (candidateId == null) {
			throw new GenericOutputException("Invalid id!");
		}

		ElectionCandidateResultOutput result = new ElectionCandidateResultOutput();

		try {
			CandidateOutput candidate = candidateClientService.getById(candidateId);
			result.setCandidate(candidate);
			Long numberElection = candidate.getNumberElection();
			Long electionId = candidate.getElectionOutput().getId();
			Long totalVotes = voteRepository.countByElectionIdAndNumberElection(electionId, numberElection);

			if (totalVotes != 0) {
				result.setTotalVotes(totalVotes);
			} else {
				result.setTotalVotes((long) 0);
			}

		} catch (FeignException e) {
			if (e.status() == 500) {
				throw new GenericOutputException("Candidate not found!");
			}
		}

		return result;
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
		ArrayList<CandidateOutput> candidate = new ArrayList<>();
		ArrayList<ElectionCandidateResultOutput> candidateTwo = new ArrayList<>();

		Long validateVotes = (long) 0, votesBlank, votesNull;

		try {
			candidate = candidateClientService.getListCandidatesByElectionId(electionId);

			for (CandidateOutput c : candidate) {
				candidateTwo.add(getResultByCandidate(c.getId()));
			}

			resultElection.setCandidates(candidateTwo);

			for (ElectionCandidateResultOutput c : candidateTwo) {
				validateVotes = validateVotes + c.getTotalVotes();
			}

			votesBlank = voteRepository.countByElectionIdAndBlankVote(electionId, true);
			votesNull = voteRepository.countByElectionIdAndNullVote(electionId, true);

			resultElection.setTotalVotes(validateVotes + votesBlank + votesNull);
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