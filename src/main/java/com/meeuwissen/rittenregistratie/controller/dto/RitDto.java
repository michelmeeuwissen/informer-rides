package com.meeuwissen.rittenregistratie.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Builder
public class RitDto {
    private String omschrijving;
    private String commentaar;
    private String postcode_van;
    private String postcode_naar;
    private String voertuigId;
    private LocalDate datum;
    private int kilometers;
}
