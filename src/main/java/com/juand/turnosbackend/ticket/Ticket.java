package com.juand.turnosbackend.ticket;

import java.time.Instant;

import com.juand.turnosbackend.person.Person;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "ticket", indexes = {@Index(columnList = "status"), @Index(columnList = "calledAt")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NonNull
  @Column(nullable = false, unique = true)
  private String code;

  @NonNull
  @Enumerated(EnumType.STRING)
  private TicketStatus status;

  private Instant createdAt;

  private Instant calledAt;

  private Instant servedAt;

  private Integer moduleNumber;

  @NonNull
  @ManyToOne(optional = false)
  private Person person;
}
