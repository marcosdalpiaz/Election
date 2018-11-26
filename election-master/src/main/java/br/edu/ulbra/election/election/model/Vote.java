package br.edu.ulbra.election.election.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.repository.VoteRepository;

@Entity
public class Vote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long voterId;

	@Column(nullable = false)
	private Long numberElection;

	@Column(nullable = false)
	private Boolean blankVote;

	@Column(nullable = false)
	private Boolean nullVote;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Election election;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVoterId() {
		return voterId;
	}

	public void setVoterId(Long voter_Id) {
		this.voterId = voter_Id;
	}

	public Long getNumberElection() {
		return numberElection;
	}

	public void setNumberElection(Long numberElection) {
		this.numberElection = numberElection;
	}

	public Boolean getBlankVote() {
		return blankVote;
	}

	public void setBlankVote(Boolean blankVote) {
		this.blankVote = blankVote;
	}

	public Boolean getNullVote() {
		return nullVote;
	}

	public void setNullVote(Boolean nullVote) {
		this.nullVote = nullVote;
	}

	public Election getElection() {
		return election;
	}

	public void setElection(Election election) {
		this.election = election;
	}

	public static boolean verifyVote(VoteInput voteInput, VoteRepository voteRepository) {

		Long idTest = voteInput.getVoterId();
		Long electionTest = voteInput.getElectionId();

		Iterable<Vote> list = voteRepository.findAll();

		for (Vote vote : list) {
			if (vote.getVoterId().equals(idTest) && electionTest.equals(vote.getElection().getId())) {
				return true;
			}
		}
		return false;
	}

}
