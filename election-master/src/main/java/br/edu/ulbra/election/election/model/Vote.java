package br.edu.ulbra.election.election.model;

import javax.persistence.*;

import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.repository.VoteRepository;

@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private Long voterId;

    @Column (nullable = true)
    private Long candidateId;

    @Column (nullable = false)
    private Boolean blankVote;

    @Column (nullable = false)
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

    public void setVoterId(Long voterId) {
        this.voterId = voterId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
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

		Long voterTest = voteInput.getVoterId();
		Long electionTest = voteInput.getElectionId();

		Iterable<Vote> list = voteRepository.findAll();

		for (Vote v : list) {
			if (v.getVoterId().equals(voterTest) && electionTest.equals(v.getElection().getId())) {
				return true;
			}
		}
		return false;
	}
}
