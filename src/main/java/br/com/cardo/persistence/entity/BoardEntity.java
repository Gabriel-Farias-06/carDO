package br.com.cardo.persistence.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class BoardEntity {

    private UUID id;
    private String name;
    private List<BoardColumnEntity> boardColumns = new ArrayList<>();
}
