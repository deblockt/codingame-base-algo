package com.deblock.genetic;

import java.util.List;

public interface Gene<T extends Gene> {

    long getRank();

    List<T> crossing(T other);

    T mutating();

    boolean isValid();
}
