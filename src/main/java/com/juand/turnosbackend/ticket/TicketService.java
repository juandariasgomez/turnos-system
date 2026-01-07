package com.juand.turnosbackend.ticket;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.juand.turnosbackend.api.dto.BoardItemDTO;
import com.juand.turnosbackend.api.dto.TicketResponseDTO;
import com.juand.turnosbackend.person.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

  private final TicketRepository ticketRepository;

  private final SimpMessagingTemplate ws;

  private final AtomicInteger counter = new AtomicInteger(10);

  public TicketResponseDTO create(final Person person) {
    char prefix = 'A';
    while (true) {
      String code = prefix + String.valueOf(counter.incrementAndGet());
      try {
        Ticket t = Ticket.builder()
          .code(code)
          .status(TicketStatus.CREATED)
          .createdAt(Instant.now())
          .person(person)
          .build();
        return buildTicketResponseDTO(ticketRepository.save(t));
      } catch (DataIntegrityViolationException ex) {
        // retry witch other code
      }
    }
  }

  public TicketResponseDTO callNext(final int moduleNumber) {
    Ticket t = ticketRepository.findFirstByStatusOrderByCreatedAtAsc(TicketStatus.CREATED).orElseThrow(() -> new ResponseStatusException(
      HttpStatus.NOT_FOUND, "No hay tickets pendientes"));
    t.setStatus(TicketStatus.CALLED);
    t.setCalledAt(Instant.now());
    t.setModuleNumber(moduleNumber);
    t = ticketRepository.save(t);
    // Hacer lógica para enviar al websocket el ticket llamado.
    BoardItemDTO dto = new BoardItemDTO(t.getCode(), t.getModuleNumber(), t.getPerson().getName() + " " + t.getPerson().getLastName());
    ws.convertAndSend("/topic/board", dto);
    return buildTicketResponseDTO(t);
  }

  public TicketResponseDTO serve(Long id) {
    if (id == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID no puede ser nulo");
    }
    Ticket t =
      ticketRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se ha encontrado el ticket"));
    t.setStatus(TicketStatus.SERVED);
    t.setServedAt(Instant.now());
    Map<String, Object> payload = new HashMap<>();
    payload.put("action", "refresh");
    ws.convertAndSend("/topic/board", payload);
    return buildTicketResponseDTO(t);
  }

  public List<BoardItemDTO> lastCalled(final int limit) {
    return ticketRepository.findTop10ByStatusOrderByCalledAtDesc(TicketStatus.CALLED).stream().limit(limit)
      .map(t -> new BoardItemDTO(t.getCode(), t.getModuleNumber(), t.getPerson().getName() + " " + t.getPerson().getLastName())).toList();
  }

  private TicketResponseDTO buildTicketResponseDTO(final Ticket ticket) {
    return new TicketResponseDTO(ticket.getId(), ticket.getCode(), ticket.getStatus(), ticket.getCreatedAt(),
      ticket.getCalledAt(), ticket.getServedAt(), ticket.getModuleNumber() != null ? ticket.getModuleNumber() : 0,
      ticket.getPerson().getName() + " " + ticket.getPerson().getLastName());
  }
}
