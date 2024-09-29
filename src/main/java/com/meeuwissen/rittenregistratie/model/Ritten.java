package com.meeuwissen.rittenregistratie.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.meeuwissen.rittenregistratie.RittenDeserializer;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonDeserialize(using = RittenDeserializer.class)
public class Ritten {
    private final List<Rit> ritten = new ArrayList<>();
}
