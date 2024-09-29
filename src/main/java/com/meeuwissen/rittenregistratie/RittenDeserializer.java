package com.meeuwissen.rittenregistratie;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.meeuwissen.rittenregistratie.model.Rit;
import com.meeuwissen.rittenregistratie.model.Ritten;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class RittenDeserializer extends StdDeserializer<Ritten> {
    private static final long serialVersionUID = 1883547555539861L;

    public RittenDeserializer() {
        this(null);
    }

    protected RittenDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public Ritten deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);
        TreeNode rides = treeNode.get("rides");
        if(rides.isContainerNode()){
            Iterator<String> ritNames = rides.fieldNames();
            Ritten ritten = new Ritten();
            while(ritNames.hasNext()){
                String ritId = ritNames.next();
                TreeNode ritNode = rides.get(ritId);
                Rit rit = objectMapper.treeToValue(ritNode, Rit.class);
                ritten.getRitten().add(rit);
            }
            return ritten;
        }

        return new Ritten();
    }
}
