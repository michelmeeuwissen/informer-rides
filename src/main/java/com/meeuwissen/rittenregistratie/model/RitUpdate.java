package com.meeuwissen.rittenregistratie.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RitUpdate {
    @JsonProperty("vehicle_id")
    private String voertuigId;

    @JsonProperty("date")
    private String datum;

    @JsonProperty("from_relation")
    private String fromRelation;

    @JsonProperty("from_country")
    private String fromCountry;

    @JsonProperty("from_zip")
    private String fromPostcode;

    @JsonProperty("to_relation")
    private String toRelation;

    @JsonProperty("to_country")
    private String toCountry;

    @JsonProperty("to_zip")
    private String toPostcode;

    @JsonProperty("distance")
    private String afstand;

    @JsonProperty
    private String type;

    @JsonProperty
    private String description;

    @JsonProperty
    private String comment;
}
