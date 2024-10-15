package com.meeuwissen.rittenregistratie.controller;

import com.meeuwissen.rittenregistratie.RitterService;
import com.meeuwissen.rittenregistratie.controller.dto.RitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ritten")
public class RittenController {

    private final RitterService rittenService;

    @GetMapping()
    @ResponseBody
    public List<RitDto> getRitten() {
        return rittenService.getRitten().getRitten().stream().map(rit -> RitDto.builder()
                .omschrijving(rit.getDescription())
                .commentaar(rit.getComment())
                .postcode_van(rit.getFromPostcode())
                .postcode_naar(rit.getToPostcode())
                .datum(rit.getDatum())
                .kilometers(Integer.parseInt(rit.getAfstand()))
                .voertuigId(rit.getVoertuigId())
                .build()).sorted(Comparator.comparing(RitDto::getDatum).reversed()).collect(Collectors.toList());
    }

    @GetMapping("afstand/{year}")
    @ResponseBody
    public Integer getRittenTotale(@PathVariable Integer year) {
        return rittenService.getRitten().getRitten().stream().filter(rit -> rit.getDatum().getYear() == year).mapToInt(rit -> {
            String afstand = rit.getAfstand();
            if (NumberUtils.isParsable(afstand)) {
                return Integer.parseInt(afstand);
            }
            return 0;
        }).sum();
    }

    @PostMapping(value = "/verwerk")
    public Mono<ResponseEntity<Void>>  verwerkRitten(@RequestPart("file-data") FilePart file) {
        try {
            rittenService.processRitten(file);
        } catch (IOException e) {
            log.error("error during processing ritten file", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
        return Mono.just(ResponseEntity.ok().build());
//        return file.transferTo(new File("/tmp/file_" + System.currentTimeMillis() + ".txt"))
//                .then(Mono.just(ResponseEntity.ok().build()));
    }

//    @PostMapping("verwerk")
//    public ResponseEntity<String> verwerkRitten(@RequestParam("file") MultipartFile file) {
//
//
//        if (file.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
//        }
//
//        try {
//            log.info(file.getOriginalFilename());
//            log.info(file.getContentType());
//            // Get the file and save it somewhere
//            byte[] bytes = file.getBytes();
//            File uploadedFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
//            file.transferTo(uploadedFile);
//
//            return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully: " + file.getOriginalFilename());
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
//        }
//
//    }
//
//    @PutMapping("correct-ritten")
//    public void correctRitten() {
//        rittenService.correctAllRitten();
//    }

}
