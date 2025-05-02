package br.com.cardo.persistence.entity;

import java.util.stream.Stream;

public enum BoardColumnKindEnum {

    INITIAL, FINAL, CANCEL, PENDING;

    public static BoardColumnKindEnum findByName(String name) {
        return Stream.of(BoardColumnKindEnum.values()).filter(c -> c.name().equals(name)).findFirst().orElseThrow();
    }
}
