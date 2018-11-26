package br.edu.ulbra.election.election.api.v1;

import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.service.VoteService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/v1/vote")
public class VoteApi {

	private final VoteService voteService;

	@Autowired
	public VoteApi(VoteService voteService) {
		this.voteService = voteService;
	}

	@PutMapping("/")
	public GenericOutput electionVote(@RequestBody VoteInput voteInput) {
		return voteService.electionVote(voteInput);
	}

	@GetMapping("/{voterId}")
	@ApiOperation(value = "Get if exists voter in vote")
	public Boolean verificaVoter(@PathVariable Long voterId) {
		return voteService.verifyVoter(voterId);
	}

	@PutMapping("/multiple")
	public GenericOutput multipleElectionVote(@RequestBody List<VoteInput> voteInputList) {
		return new GenericOutput("OK");
	}
}
