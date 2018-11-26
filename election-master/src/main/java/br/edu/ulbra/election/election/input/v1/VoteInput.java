package br.edu.ulbra.election.election.input.v1;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Election Input Information")
public class VoteInput {

	@ApiModelProperty(example = "1", notes = "Election Unique Identification")
	private Long electionId;
	@ApiModelProperty(example = "2", notes = "Voter Unique Identifier")
	private Long voterId;
	@ApiModelProperty(example = "3", notes = "Candidate Unique Identifier")
	private Long numberElection;

	public Long getElectionId() {
		return electionId;
	}

	public void setElectionId(Long electionId) {
		this.electionId = electionId;
	}

	public Long getVoterId() {
		return voterId;
	}

	public void setVoterId(Long voterId) {
		this.voterId = voterId;
	}

	public Long getNumberElection() {
		return numberElection;
	}

	public void setNumberElection(Long numberElection) {
		this.numberElection = numberElection;
	}
}
