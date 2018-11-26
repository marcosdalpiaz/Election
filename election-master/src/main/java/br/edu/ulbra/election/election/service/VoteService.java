package br.edu.ulbra.election.election.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.client.VoterClientService;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
import feign.FeignException;

@Service
public class VoteService {

	private final VoteRepository voteRepository;
	private final ElectionRepository electionRepository;
	private final VoterClientService voterClientService;
	private final CandidateClientService candidateClientService;

	@Autowired
	public VoteService(VoteRepository voteRepository, ElectionRepository electionRepository,
			VoterClientService voterClientService, CandidateClientService candidateClientService) {
		this.voteRepository = voteRepository;
		this.electionRepository = electionRepository;
		this.voterClientService = voterClientService;
		this.candidateClientService = candidateClientService;
	}

	public GenericOutput electionVote(VoteInput voteInput) {

		Vote vote = new Vote();

		if (voteInput.getNumberElection() == null) {
			vote.setBlankVote(true);
			vote.setNullVote(false);
		} else {
			vote.setBlankVote(false);
		}

		validateInput(vote, voteInput);

		voteRepository.save(vote);

		return new GenericOutput("OK");
	}

	public GenericOutput multiple(List<VoteInput> voteInputList) {
		for (VoteInput voteInput : voteInputList) {
			this.electionVote(voteInput);
		}
		return new GenericOutput("OK");
	}

	private void validateInput(Vote vote, VoteInput voteInput) {

		if (Vote.verifyVote(voteInput, voteRepository)) {
			throw new GenericOutputException("Vote used");
		}

		Election election = electionRepository.findById(voteInput.getElectionId()).orElse(null);
		if (election == null) {
			throw new GenericOutputException("Invalid Election");
		} else {
			vote.setElection(election);
		}

		try {
			voterClientService.getById(voteInput.getVoterId());
			vote.setVoterId(voteInput.getVoterId());
		} catch (FeignException e) {
			if (e.status() == 500) {
				throw new GenericOutputException("Invalid Voter");
			}
		}

		try {
			if (!vote.getBlankVote()) {
				candidateClientService.verifyNumber(voteInput.getNumberElection(), voteInput.getElectionId());
				vote.setNullVote(false);
				vote.setNumberElection(voteInput.getNumberElection());
			}
		} catch (FeignException e) {
			if (e.status() == 500) {
				vote.setNullVote(true);
			} else {
				throw new GenericOutputException("Error");
			}
		}

	}

	public Boolean verifyVoter(Long voterId) {

		Vote vote = voteRepository.findFirstByVoterId(voterId);

		if (vote != null) {
			return true;
		} else {
			return false;
		}
	}
}
