package com.juand.turnosbackend.api;

import java.util.List;

import com.juand.turnosbackend.api.dto.BoardItemDTO;
import com.juand.turnosbackend.api.dto.PersonRequestDTO;
import com.juand.turnosbackend.api.dto.TicketResponseDTO;
import com.juand.turnosbackend.person.Person;
import com.juand.turnosbackend.person.PersonRepository;
import com.juand.turnosbackend.ticket.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

  private final TicketService ticketService;

  private final PersonRepository personRepository;

  @PostMapping
  public TicketResponseDTO create(@RequestBody final PersonRequestDTO dto) {
    Person p = personRepository.findByDni(dto.dni()).orElseGet(() -> {
      Person person = new Person();
      person.setDni(dto.dni());
      person.setName(dto.firstName());
      person.setLastName(dto.lastName());
      return personRepository.save(person);
    });
    if (!dto.dni().matches("\\d{10}")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El DNI debe contener 10 dígitos como mínimo.");
    }
    return ticketService.create(p);
  }

  @GetMapping("/next")
  public TicketResponseDTO callNext(@RequestParam(name = "module") final int module) {
    return ticketService.callNext(module);
  }

  @PostMapping("/{id}/serve")
  public TicketResponseDTO serve(@PathVariable(name = "id") final Long id) {
    return ticketService.serve(id);
  }

  @GetMapping("/board/last")
  public List<BoardItemDTO> lastCall(@RequestParam(name = "limit", defaultValue = "3") final int limit) {
    return ticketService.lastCalled(limit);
  }
}
