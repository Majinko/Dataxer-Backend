package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class PackItemDTO implements Comparable<PackItemDTO> {
    private Long id;
    private ItemDTO item;
    private Integer position;
    private float qty;

    @Override
    public int compareTo(@NotNull PackItemDTO o) {
        if (this.getPosition() == null) {
            return 0;
        }

        return this.getPosition().compareTo(o.getPosition());
    }
}
