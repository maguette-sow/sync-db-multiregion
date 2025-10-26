package com.example.dsms.repository.dakar;

import com.example.dsms.model.SyncMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncMetadataRepository extends JpaRepository<SyncMetadata, Long> {
}
