/*
 *
 */
package com.churchclerk.baseapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;

/**
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class BaseModel {
    private UUID    id;
    private boolean active;
    @CreatedDate
    private Date    createdDate;
    @CreatedBy
    private String  createdBy;
    @LastModifiedDate
    private Date    updatedDate;
    @LastModifiedBy
    private String  updatedBy;

    /**
     *
     * @param source
     */
    public void copy(BaseModel source) {
        setId(source.getId());
        setActive(source.isActive());
        setCreatedDate(source.getCreatedDate());
        setCreatedBy(source.getCreatedBy());
        setUpdatedDate(source.getUpdatedDate());
        setUpdatedBy(source.getUpdatedBy());
    }

    /**
     *
     * @param source
     */
    public void copyNonNulls(BaseModel source) {
        copy(source.getId(), this::setId);
        setActive(source.isActive());
        copy(source.getCreatedDate(), this::setCreatedDate);
        copy(source.getCreatedBy(), this::setCreatedBy);
        copy(source.getUpdatedDate(), this::setUpdatedDate);
        copy(source.getUpdatedBy(), this::setUpdatedBy);
    }

    /**
     *
     * @param value
     * @param consumer
     * @param <T>
     */
    protected static <T> void copy(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }
}
