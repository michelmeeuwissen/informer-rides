package com.meeuwissen.rittenregistratie;

import com.meeuwissen.rittenregistratie.model.Ritten;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class RitterService {
    private final RestClient informerRestclient;


    public Ritten getRitten(){
        return informerRestclient.get().uri("rides").retrieve().body(Ritten.class);
    }

}
