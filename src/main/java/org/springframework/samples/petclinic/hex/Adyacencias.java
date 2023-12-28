package org.springframework.samples.petclinic.hex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public class Adyacencias {
    
    static Map<Integer, List<Integer>> adyacentesPorPosicion = new HashMap<>();

    static {
        adyacentesPorPosicion.put(0, Arrays.asList(1, 2, 3));
        adyacentesPorPosicion.put(1, Arrays.asList(0, 2, 4));
        adyacentesPorPosicion.put(2, Arrays.asList(0, 1, 3, 4, 5, 6));
        adyacentesPorPosicion.put(3, Arrays.asList(0, 2, 6, 8, 11, 42));
        adyacentesPorPosicion.put(4, Arrays.asList(1, 2, 5, 14, 15));
        adyacentesPorPosicion.put(5, Arrays.asList(2, 4, 6, 14, 17, 42));
        adyacentesPorPosicion.put(6, Arrays.asList(2, 3, 5, 11, 17, 42));
        adyacentesPorPosicion.put(7, Arrays.asList(8, 9, 10));
        adyacentesPorPosicion.put(8, Arrays.asList(3, 7, 9, 11));
        adyacentesPorPosicion.put(9, Arrays.asList(7, 8, 10, 11, 12, 13));
        adyacentesPorPosicion.put(10, Arrays.asList(7, 9, 13));
        adyacentesPorPosicion.put(11, Arrays.asList(3, 6, 8, 9, 12, 42));
        adyacentesPorPosicion.put(12, Arrays.asList(9, 11, 13, 22, 42));
        adyacentesPorPosicion.put(13, Arrays.asList(9, 10, 12, 21, 22));
        adyacentesPorPosicion.put(14, Arrays.asList(4, 5, 15, 16, 17));
        adyacentesPorPosicion.put(15, Arrays.asList(14, 16, 18));
        adyacentesPorPosicion.put(16, Arrays.asList(14, 15, 17, 18, 19));
        adyacentesPorPosicion.put(17, Arrays.asList(5, 14, 16, 20, 42));
        adyacentesPorPosicion.put(18, Arrays.asList(15, 16, 19));
        adyacentesPorPosicion.put(19, Arrays.asList(16, 18, 20, 29));
        adyacentesPorPosicion.put(20, Arrays.asList(16, 17, 19, 28, 29, 42));
        adyacentesPorPosicion.put(21, Arrays.asList(13, 22, 23, 24));
        adyacentesPorPosicion.put(22, Arrays.asList(12, 13, 21, 23, 25, 42));
        adyacentesPorPosicion.put(23, Arrays.asList(21, 22, 24, 25, 26, 27));
        adyacentesPorPosicion.put(24, Arrays.asList(21, 23, 27));
        adyacentesPorPosicion.put(25, Arrays.asList(22, 23, 26, 35, 42));
        adyacentesPorPosicion.put(26, Arrays.asList(23, 25, 27, 28, 35, 38));
        adyacentesPorPosicion.put(27, Arrays.asList(23, 24, 26));
        adyacentesPorPosicion.put(28, Arrays.asList(20, 29, 30, 31, 42));
        adyacentesPorPosicion.put(29, Arrays.asList(19, 20, 28, 30, 32));
        adyacentesPorPosicion.put(30, Arrays.asList(28, 29, 31, 32, 33, 34));
        adyacentesPorPosicion.put(31, Arrays.asList(28, 30, 34, 36, 39, 42));
        adyacentesPorPosicion.put(32, Arrays.asList(29, 30, 33));
        adyacentesPorPosicion.put(33, Arrays.asList(30, 32, 34));
        adyacentesPorPosicion.put(34, Arrays.asList(30, 31, 33, 39));
        adyacentesPorPosicion.put(35, Arrays.asList(25, 26, 36, 37, 38, 42));
        adyacentesPorPosicion.put(36, Arrays.asList(31, 35, 37, 39, 42));
        adyacentesPorPosicion.put(37, Arrays.asList(35, 36, 38, 39, 40, 41));
        adyacentesPorPosicion.put(38, Arrays.asList(26, 35, 37, 41));
        adyacentesPorPosicion.put(39, Arrays.asList(31, 34, 36, 37, 40));
        adyacentesPorPosicion.put(40, Arrays.asList(37, 39, 41));
        adyacentesPorPosicion.put(41, Arrays.asList(37, 38, 40));
        adyacentesPorPosicion.put(42, Arrays.asList(5, 6, 11, 12, 17, 20, 22, 25, 28, 31, 35, 36));
    }

}
