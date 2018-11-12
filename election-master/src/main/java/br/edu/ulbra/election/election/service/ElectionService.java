package br.edu.ulbra.election.election.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.ElectionInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.output.v1.PartyOutput;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;

@Service
public class ElectionService {

	private final ElectionRepository electionRepository;
	private final ModelMapper modelMapper;
	private final String[] states = {"BR", "AC", "AL", "AM", "AP", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RO", "RS", "RR", "SC", "SE", "SP", "TO"};
	private static final String MESSAGE_INVALID_ID = "Invalid id";
    private static final String MESSAGE_ELECTION_NOT_FOUND = "Election not found";
    
    @Autowired
    public ElectionService(ElectionRepository electionRepository, ModelMapper modelMapper){
        this.electionRepository = electionRepository;
        this.modelMapper = modelMapper;
    }

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
        validateInput(electionInput, false);
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
        validateInput(electionInput, true);

        Election election = electionRepository.findById(electionId).orElse(null);
        if (election == null){
            throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);
        }

        election.setYear(electionInput.getYear());
        election.setStateCode(electionInput.getStateCode());
        election.setDescription(electionInput.getDescription());
        
        election = electionRepository.save(election);
        return modelMapper.map(election, ElectionOutput.class);
    }

    public GenericOutput delete(Long electionId) {
        if (electionId == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }

        Election election = electionRepository.findById(electionId).orElse(null);
        if (election == null){
            throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);
        }

        electionRepository.delete(election);

        return new GenericOutput("Election deleted");
    }

    private void validateInput(ElectionInput electionInput, boolean isUpdate){
        if (StringUtils.isBlank(electionInput.getStateCode())){
            throw new GenericOutputException("Invalid state code");
        }
        if (StringUtils.isBlank(electionInput.getDescription())){
            throw new GenericOutputException("Invalid description");
        }
        if (electionInput.getYear() == null){
                throw new GenericOutputException("Invalid year");
        }
        Integer maxYear = electionInput.getYear();
		if(maxYear < 2000 || maxYear >= 2200) {
			throw new GenericOutputException("O ano de eleição deve estar entre 2000 e 2199!");
		}
		Integer maxName = electionInput.getDescription().length();
        if(maxName < 5) {
        	throw new GenericOutputException("A descrição deve ter no mínimo 5 letras!");
        }
		boolean found = false;
		for (int i = 0; i < states.length && !found; i++) {
			if(states[i].equals(electionInput.getStateCode())){
				found = true;
			}
		}
		if(!found) {
			throw new GenericOutputException("Estado inexistente!");
		}
    }
}
