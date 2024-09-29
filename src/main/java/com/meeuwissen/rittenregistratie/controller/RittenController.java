package com.meeuwissen.rittenregistratie.controller;

import com.meeuwissen.rittenregistratie.RitterService;
import com.meeuwissen.rittenregistratie.model.Ritten;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ritten")
public class RittenController {

    private final RitterService rittenService;

    @GetMapping()
    @ResponseBody
    public Ritten getRitten() {
        return rittenService.getRitten();
    }

    @GetMapping("afstand/{year}")
    @ResponseBody
    public Integer getRittenTotale(@PathVariable Integer year) {
      return rittenService.getRitten().getRitten().stream().filter(rit -> {
            return rit.getDatum().getYear() == year;
        }).mapToInt(rit -> {
          String afstand = rit.getAfstand();
          if(NumberUtils.isParsable(afstand)){
              return Integer.parseInt(afstand);
          }
          return 0;
      }).sum();
    }

}
