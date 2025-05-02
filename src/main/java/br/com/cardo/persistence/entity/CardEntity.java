package br.com.cardo.persistence.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Data
@ToString(exclude = "boardColumn")
public class CardEntity {

    private UUID id;
    private String title;
    private String description;
    private BoardColumnEntity boardColumn;
    private List<BlockEntity> blocks;
}
