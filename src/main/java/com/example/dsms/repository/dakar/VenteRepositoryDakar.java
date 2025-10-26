package com.example.dsms.repository.dakar;

import com.example.dsms.model.Vente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface VenteRepositoryDakar extends JpaRepository<Vente, UUID> {
    List<Vente> findByUpdatedAtAfter(LocalDateTime ts);
}
