package br.edu.ulbra.election.election.repository;

import org.springframework.data.repository.CrudRepository;
import br.edu.ulbra.election.election.model.Election;

public interface ElectionRepository extends CrudRepository<Election, Long> {

}
