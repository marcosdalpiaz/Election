package br.edu.ulbra.election.election.model;

import javax.persistence.*;

import br.edu.ulbra.election.election.enums.StateCodes;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;

@Entity
public class Election {
	
	public static String states[] = { "AC", "AL", "AM", "AP", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG",
			"PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RO", "RS", "RR", "SC", "SE", "SP", "TO", "BR" };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private String stateCode;

    @Column(nullable = false)
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public static ElectionOutput verify(Election election, Integer year) {
		Integer y = election.getYear();

		if (y.equals(year)) {
			ElectionOutput electionOutput = new ElectionOutput();

			electionOutput.setId(election.getId());
			electionOutput.setStateCode(election.getStateCode());
			electionOutput.setDescription(election.getDescription());
			electionOutput.setYear(election.getYear());

			return electionOutput;
		} else {
			return null;
		}
	}

	public static boolean verifyState(String e) {
		for (int x = 0; x < states.length; x++) {
			if (states[x].equalsIgnoreCase(e)) {
				return true;
			}
		}
		return false;
	}

}
