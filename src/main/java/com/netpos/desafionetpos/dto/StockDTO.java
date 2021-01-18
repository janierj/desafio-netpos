package com.netpos.desafionetpos.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.netpos.desafionetpos.config.Constants;

import javax.validation.constraints.*;

public class StockDTO {

    @JsonIgnore
    private Long id;

    @Min(0)
    @Max(Constants.MAX_PRODUCT_STOCK)
    @NotNull
    private Integer quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public StockDTO quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }
}
