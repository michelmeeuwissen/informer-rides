package com.meeuwissen.rittenregistratie.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RitResponse {

    @JsonProperty("ride_id")
    private String ritId;
}
