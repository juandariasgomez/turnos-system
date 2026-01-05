package com.juand.turnosbackend.api.dto;

import java.time.Instant;

import com.juand.turnosbackend.ticket.TicketStatus;

public record TicketResponseDTO(Long id, String code, TicketStatus status, Instant createdAt, Instant calledAt, Instant serveAt,
                                Integer moduleNumber, String fullName) {

}
