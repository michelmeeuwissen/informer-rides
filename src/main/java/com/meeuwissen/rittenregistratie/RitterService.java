package com.meeuwissen.rittenregistratie;

import com.meeuwissen.rittenregistratie.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoField.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RitterService {
    private static String VOERTUIG_ID = "43147";

    private final RestClient informerRestclient;


    public Ritten getRitten(){
        return informerRestclient.get().uri("rides").retrieve().body(Ritten.class);
    }

    public void updateRit(Rit rit){
        RitUpdate ritUpdate = RitUpdate.builder()
                .voertuigId(rit.getVoertuigId())
                .datum(rit.getDatum().toString())
                .fromRelation(rit.getFromRelation())
                .fromCountry(rit.getFromCountry())
                .fromPostcode(rit.getFromPostcode())
                .toRelation(rit.getToRelation())
                .toCountry(rit.getToCountry())
                .toPostcode(rit.getToPostcode())
                .afstand(rit.getAfstand())
                .type("Business")
                .description(rit.getDescription())
                .comment(rit.getComment()).build();


        RitResponse ritResponse = informerRestclient.put()
                .uri("ride/" + rit.getRitId())
                .body(ritUpdate)
                .retrieve()
                .body(RitResponse.class);

        log.info("update succesful for rit {} met rideId {}", rit.getRitId(), ritResponse.getRitId());
    }

    public void processRitten(FilePart content) throws IOException {
        Flux<String> cache = content.content().map(dataBuffer -> {
            InputStream inputStream = dataBuffer.asInputStream();
            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }).cache();

        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .appendValue(DAY_OF_MONTH, 2)
                .appendLiteral('-')
                .appendValue(MONTH_OF_YEAR, 2)
                .appendLiteral('-')
                .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                .toFormatter();


        cache.subscribe(s -> {
            Arrays.stream(s.split("\n")).forEach(rule -> {
                if(!rule.startsWith("# datum;")){
                    String[] split = rule.split(";");
                    RitCreate rit = RitCreate.builder()
                            .voertuigId(VOERTUIG_ID)
                            .datum(LocalDate.parse(split[0], dateTimeFormatter).toString())
                            .fromRelation("0")
                            .fromCountry("NL")
                            .fromPostcode(split[1])
                            .toRelation("0")
                            .toCountry("NL")
                            .toPostcode(split[2])
                            .afstand(split[3])
                            .description(split[4])
                            .comment("")
                            .type("business").build();
                    RitResponse ritResponse = informerRestclient.post()
                            .uri("ride/")
                            .body(rit)
                            .retrieve()
                            .body(RitResponse.class);

                    System.out.println(ritResponse);
                }
            });

        });



//        getLines(content).collectList().subscribe(strings -> {
//            strings.forEach(System.out::println);
//        });
//
//        Mono<List<String>> listMono = content.flatMapMany(this::getLines).collectList();
//        listMono.subscribe(strings -> {
//            strings.forEach(System.out::println);
//        });

    }

    public Flux<String> getLines(FilePart filePart) {
        return filePart.content()
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .map(this::processAndGetLinesAsList)
                .flatMapIterable(Function.identity());
    }

    private List<String> processAndGetLinesAsList(String string) {

        Supplier<Stream<String>> streamSupplier = string::lines;
        var isFileOk = streamSupplier.get().allMatch(line -> line.matches("^01\\d{9}$|^1\\d{9}|^d{0}$"));

        return isFileOk ? streamSupplier.get().filter(s -> !s.isBlank()).collect(Collectors.toList()) : new ArrayList<>();
    }
}
