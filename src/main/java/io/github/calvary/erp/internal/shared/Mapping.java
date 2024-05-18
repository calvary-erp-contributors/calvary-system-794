package io.github.calvary.erp.internal.shared;

import java.util.List;
import java.util.stream.Collectors;

public interface Mapping<v1, v2> {
    v2 mapToV2(v1 v1);

    v1 mapToV1(v2 v2);

    default List<v1> mapToV1(List<v2> v2) {
        return v2.stream().map(this::mapToV1).collect(Collectors.toList());
    }

    default List<v2> mapToV2(List<v1> v1) {
        return v1.stream().map(this::mapToV2).collect(Collectors.toList());
    }
}
