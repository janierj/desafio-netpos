package com.netpos.desafionetpos.dto.vm;

import com.netpos.desafionetpos.config.Constants;
import com.netpos.desafionetpos.entity.enumeration.Operation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * View Model class to alter the Stock of a Product.
 */
public class AlterStockVM {

    @NotNull
    private Operation operation;

    @Min(0)
    @Max(Constants.MAX_PRODUCT_STOCK)
    @NotNull
    private Integer quantity;

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
