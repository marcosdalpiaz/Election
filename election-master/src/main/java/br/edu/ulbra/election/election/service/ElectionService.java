package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.enums.StateCodes;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.ElectionInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final VoteRepository voteRepository;
    private final ModelMapper modelMapper;
    private final CandidateClientService candidateClientService;
    
    @Autowired
    public ElectionService(ElectionRepository electionRepository, ModelMapper modelMapper, CandidateClientService candidateClientService, VoteRepository voteRepository){
        this.electionRepository = electionRepository;
        this.modelMapper = modelMapper;
        this.candidateClientService = candidateClientService;
        this.voteRepository = voteRepository;
    }


    private static final String MESSAGE_INVALID_ID = "Invalid id";
    private static final String MESSAGE_ELECTION_NOT_FOUND = "Election not found";

    public List<ElectionOutput> getAll(){
        Type electionOutputListType = new TypeToken<List<ElectionOutput>>(){}.getType();
        return modelMapper.map(electionRepository.findAll(), electionOutputListType);
    }
    
    public List<ElectionOutput> getByYear(Integer year){
    	List<ElectionOutput> selectYear = getAll();
    	List<ElectionOutput> electionSelectYear = new ArrayList<>();
        for (ElectionOutput electionOutput : selectYear) {
        	int getYear = electionOutput.getYear();
			if(getYear == year) {
				electionSelectYear.add(electionOutput);
			}
		}
        return electionSelectYear;
    }
    
    public ElectionOutput create(ElectionInput electionInput) {
        validateInput(electionInput);
        validateDuplicate(electionInput, null);
        Election election = modelMapper.map(electionInput, Election.class);
        election = electionRepository.save(election);
        return modelMapper.map(election, ElectionOutput.class);
    }

    public ElectionOutput getById(Long electionId){
        if (electionId == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }

        Election election = electionRepository.findById(electionId).orElse(null);
        if (election == null){
            throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);
        }

        return modelMapper.map(election, ElectionOutput.class);
    }

    public ElectionOutput update(Long electionId, ElectionInput electionInput) {
        if (electionId == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }
        foundCandidateOnDeleteOrUpdate(electionId);
        validateInput(electionInput);
        validateDuplicate(electionInput, electionId);

        Election election = electionRepository.findById(electionId).orElse(null);
        if (election == null){
            throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);
        }
        
        Vote vote = voteRepository.findById(electionId).orElse(null);
        if (vote != null) {
        	 throw new GenericOutputException("This election have votes!"); 
        }

        election.setStateCode(electionInput.getStateCode());
        election.setDescription(electionInput.getDescription());
        election.setYear(electionInput.getYear());
        election = electionRepository.save(election);
        return modelMapper.map(election, ElectionOutput.class);
    }

    public GenericOutput delete(Long electionId) {
        if (electionId == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }
        this.foundCandidateOnDeleteOrUpdate(electionId);

        Election election = electionRepository.findById(electionId).orElse(null);
        if (election == null){
            throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);
        }
        
        Vote vote = voteRepository.findById(electionId).orElse(null);
        if (vote != null) {
        	 throw new GenericOutputException("This election have votes!"); 
        }
        
        electionRepository.delete(election);

        return new GenericOutput("Election deleted");
    }
    
    public void foundCandidateOnDeleteOrUpdate (Long electionId) {
    	List<CandidateOutput> listCdOut = candidateClientService.getAll();
    	boolean foundItem = false;
    	
    	for(int i =0; i < listCdOut.size() && !foundItem; i++) {
    		Long numberCdOut = listCdOut.get(i).getElectionOutput().getId();
    		if(numberCdOut.equals(electionId)) {
    			foundItem = true;
    		}
    	}
    	
    	if (foundItem) {
    		throw new GenericOutputException("Cannot delete eleciton with Candidate");
		}
    }

    private void validateDuplicate(ElectionInput electionInput, Long id){
        Election election = electionRepository.findFirstByYearAndStateCodeAndDescription(electionInput.getYear(), electionInput.getStateCode(), electionInput.getDescription());
        if (election != null && !election.getId().equals(id)){
            throw new GenericOutputException("Duplicate Code");
        }
    }

    private void validateInput(ElectionInput electionInput){
        if (StringUtils.isBlank(electionInput.getDescription()) || electionInput.getDescription().length() < 5){
            throw new GenericOutputException("Invalid Description");
        }
        if (StringUtils.isBlank(electionInput.getStateCode())){
            throw new GenericOutputException("Invalid State Code");
        }
        try {
             StateCodes.valueOf(electionInput.getStateCode());
        } catch (IllegalArgumentException e){
            throw new GenericOutputException("Invalid State Code");
        }
        if (electionInput.getYear() == null || electionInput.getYear() < 2000 || electionInput.getYear() > 2200){
            throw new GenericOutputException("Invalid Year");
        }
    }
}
