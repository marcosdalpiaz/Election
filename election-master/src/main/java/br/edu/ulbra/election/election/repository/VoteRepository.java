package br.edu.ulbra.election.election.repository;

import br.edu.ulbra.election.election.model.Vote;
import org.springframework.data.repository.CrudRepository;

public interface VoteRepository extends CrudRepository<Vote, Long> {

	Vote findFirstByVoterId(Long voterId);
	Long countByElectionId(Long electionId);
	Long countByElectionIdAndNumberElection(Long electionId, Long numberElection);
	Long countByElectionIdAndBlankVote(Long electionId, boolean a);
	Long countByElectionIdAndNullVote(Long electionId, boolean a);

}
