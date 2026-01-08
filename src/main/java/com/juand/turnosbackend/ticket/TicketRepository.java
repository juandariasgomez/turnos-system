package com.juand.turnosbackend.ticket;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

  @Override
  @NonNull
  <S extends Ticket> S save(@NonNull S entity);

  Optional<Ticket> findFirstByStatusOrderByCreatedAtAsc(TicketStatus status);

  List<Ticket> findTop10ByStatusOrderByCalledAtDesc(TicketStatus status);
}
