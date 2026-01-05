package com.juand.turnosbackend.ticket;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

  Optional<Ticket> findFirstByStatusOrderByCreatedAtAsc(TicketStatus status);

  List<Ticket> findTop10ByStatusOrderByCalledAtDesc(TicketStatus status);
}
