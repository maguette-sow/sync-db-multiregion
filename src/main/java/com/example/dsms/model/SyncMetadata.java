package com.example.dsms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class SyncMetadata {
    @Id
    private Long id = 1L;
    private LocalDateTime lastSync;

    public SyncMetadata() {}
    public SyncMetadata(LocalDateTime lastSync) { this.lastSync = lastSync; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getLastSync() { return lastSync; }
    public void setLastSync(LocalDateTime lastSync) { this.lastSync = lastSync; }
}
