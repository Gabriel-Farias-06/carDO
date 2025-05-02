package br.com.cardo.persistence.entity;

import lombok.Data;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@ToString(exclude = "card")
public class BlockEntity {

    private UUID id;
    private String title;
    private OffsetDateTime blockedAt;
    private String blockReason;
    private OffsetDateTime unblockedAt;
    private String unblockReason;
    private CardEntity card;
}
