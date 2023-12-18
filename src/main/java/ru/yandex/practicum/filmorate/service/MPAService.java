package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MPAStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MPAService {
    private final MPAStorage mpaStorage;

    public List<MPA> getAllMPAs() {
        return mpaStorage.getAllMPAs();
    }

    public MPA getMPAById(final Optional<Integer> id) {
        if (id.isEmpty()) {
            log.warn("Попытка получить mpa с пустым уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор mpa не может быть пустым");
        }
        Integer fmpaId = id.get();
        if (fmpaId <= 0) {
            log.warn("Попытка получить mpa с неположительным уникальным идентификатором");
            throw new NotFoundException("Уникальный идентификатор mpa не может быть отрицательным или равным нулю");
        }
        return mpaStorage.getMPAById(fmpaId);
    }
}
