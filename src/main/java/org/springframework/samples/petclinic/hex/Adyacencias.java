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
        adyacentesPorPosicion.put(1, Arrays.asList(0, 3, 4));
        adyacentesPorPosicion.put(2, Arrays.asList(0, 3, 5, 15));
        adyacentesPorPosicion.put(3, Arrays.asList(0, 1, 2, 4, 5, 6));
        adyacentesPorPosicion.put(4, Arrays.asList(1, 3, 6, 7, 9));
        adyacentesPorPosicion.put(5, Arrays.asList(2, 3, 6, 15, 18, 42));
        adyacentesPorPosicion.put(6, Arrays.asList(3, 4, 5, 9, 42));
        adyacentesPorPosicion.put(7, Arrays.asList(4, 8, 9, 10));
        adyacentesPorPosicion.put(8, Arrays.asList(7, 10, 11));
        adyacentesPorPosicion.put(9, Arrays.asList(4, 6, 7, 10, 12, 42));
        adyacentesPorPosicion.put(10, Arrays.asList(7, 8, 9, 11, 12, 13));
        adyacentesPorPosicion.put(11, Arrays.asList(8, 10, 13));
        adyacentesPorPosicion.put(12, Arrays.asList(9, 10, 13, 21, 42));
        adyacentesPorPosicion.put(13, Arrays.asList(10, 12, 21, 22));
        adyacentesPorPosicion.put(14, Arrays.asList(15, 16, 17));
        adyacentesPorPosicion.put(15, Arrays.asList(2, 5, 14, 17, 18));
        adyacentesPorPosicion.put(16, Arrays.asList(14, 17, 19));
        adyacentesPorPosicion.put(17, Arrays.asList(14, 15, 16, 18, 18, 20));
        adyacentesPorPosicion.put(18, Arrays.asList(5, 15, 17, 20, 42));
        adyacentesPorPosicion.put(19, Arrays.asList(16, 17, 20, 28));
        adyacentesPorPosicion.put(20, Arrays.asList(17, 18, 19, 28, 29, 42));
        adyacentesPorPosicion.put(21, Arrays.asList(12, 13, 22, 23, 24, 42));
        adyacentesPorPosicion.put(22, Arrays.asList(13, 21, 24, 25));
        adyacentesPorPosicion.put(23, Arrays.asList(21, 24, 26, 36, 42));
        adyacentesPorPosicion.put(24, Arrays.asList(21, 22, 23, 25, 26, 27));
        adyacentesPorPosicion.put(25, Arrays.asList(22, 24, 27));
        adyacentesPorPosicion.put(26, Arrays.asList(23, 24, 27, 36, 39));
        adyacentesPorPosicion.put(27, Arrays.asList(24, 25, 26));
        adyacentesPorPosicion.put(28, Arrays.asList(19, 20, 29, 30, 31));
        adyacentesPorPosicion.put(29, Arrays.asList(20, 28, 31, 32, 42));
        adyacentesPorPosicion.put(30, Arrays.asList(28, 31, 33));
        adyacentesPorPosicion.put(31, Arrays.asList(28, 29, 30, 32, 33, 34));
        adyacentesPorPosicion.put(32, Arrays.asList(29, 31, 34, 35, 37, 42));
        adyacentesPorPosicion.put(33, Arrays.asList(30, 31, 34));
        adyacentesPorPosicion.put(34, Arrays.asList(31, 32, 33, 37));
        adyacentesPorPosicion.put(35, Arrays.asList(32, 36, 37, 38, 42));
        adyacentesPorPosicion.put(36, Arrays.asList(23, 26, 35, 38, 39, 42));
        adyacentesPorPosicion.put(37, Arrays.asList(32, 34, 35, 38, 40));
        adyacentesPorPosicion.put(38, Arrays.asList(35, 36, 37, 39, 40, 41));
        adyacentesPorPosicion.put(39, Arrays.asList(26, 36, 28, 41));
        adyacentesPorPosicion.put(40, Arrays.asList(37, 38, 41));
        adyacentesPorPosicion.put(41, Arrays.asList(38, 39, 40));
        adyacentesPorPosicion.put(42, Arrays.asList(5, 6, 9, 12, 21, 23, 35, 36, 29, 32, 18, 20));
    }

}
