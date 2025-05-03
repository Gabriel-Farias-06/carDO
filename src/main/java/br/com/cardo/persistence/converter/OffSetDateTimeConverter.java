package br.com.cardo.persistence.converter;

import lombok.AllArgsConstructor;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class OffSetDateTimeConverter {

    public static OffsetDateTime toOffsetDateTime(final Timestamp value) {
        return OffsetDateTime.ofInstant(value.toInstant(),  UTC);
    }
}
