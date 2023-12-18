package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/mpa")
public class MPAController {
    private final MPAService mpaService;

    @GetMapping
    public List<MPA> getAllMPAs() {
        log.debug("Пришёл запрос на получение всех mpa");
        List<MPA> mpas = mpaService.getAllMPAs();
        log.debug("Получение всех mpa прошло успешно. Получено mpa: ({})", mpas.size());
        return mpas;
    }

    @GetMapping(path = "/{id}")
    public MPA getFilmById(@PathVariable(name = "id") final Optional<Integer> id) {
        log.debug("Пришёл запрос на получение mpa по уникальному идентификатору.");
        MPA mpa = mpaService.getMPAById(id);
        log.debug("Получение mpa по уникальному идентификатору прошло успешно. Получен mpa: {}", mpa);
        return mpa;
    }
}
