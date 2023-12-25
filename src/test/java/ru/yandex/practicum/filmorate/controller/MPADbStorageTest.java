package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MPAStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MPADbStorageTest {
    @Qualifier("MPADBStorage")
    private final MPAStorage mpaStorage;

    @Test
    public void findMPAById() {
        MPA mpa = mpaStorage.getMPAById(1);
        assertThat(mpa).isNotNull();
        assertEquals(1, mpa.getId());
        assertEquals("G", mpa.getName());
    }

    @Test
    public void getNotFoundMPA() {
        NotFoundException ex = assertThrows(NotFoundException.class, () -> mpaStorage.getMPAById(9999));
        assertEquals("MPA с идентификатором 9999 не существует!", ex.getMessage());
    }

    @Test
    public void getAllMPAs() {
        List<MPA> mpas = mpaStorage.getAllMPAs();
        assertThat(mpas).isNotNull();
        assertEquals(5, mpas.size());
        assertEquals("G", mpas.get(0).getName());
        assertEquals("R", mpas.get(3).getName());
    }

}
