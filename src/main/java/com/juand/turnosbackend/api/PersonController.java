package com.juand.turnosbackend.api;

import java.util.Optional;

import com.juand.turnosbackend.api.dto.PersonResponseDTO;
import com.juand.turnosbackend.person.Person;
import com.juand.turnosbackend.person.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

  private final PersonRepository personRepository;

  @GetMapping("/searchByDNI/{dni}")
  public PersonResponseDTO searchByDNI(@PathVariable(name = "dni") final String dni) {
    if (!dni.matches("\\d{11}")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dni invalido");
    }
    Optional<Person> person = personRepository.findByDni(dni);
    if (person.isPresent()) {
      return new PersonResponseDTO(person.get().getDni(), person.get().getName(), person.get().getLastName());
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dni not found");
    }
  }
}
