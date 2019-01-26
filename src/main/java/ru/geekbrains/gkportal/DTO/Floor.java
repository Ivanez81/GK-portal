package ru.geekbrains.gkportal.DTO;

import lombok.Data;
import ru.geekbrains.gkportal.entities.Flat;

import java.util.ArrayList;
import java.util.List;

@Data
public class Floor {
    private int number;
    List<Flat> flats = new ArrayList<>();
}
