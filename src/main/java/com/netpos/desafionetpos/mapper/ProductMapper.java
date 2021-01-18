package com.netpos.desafionetpos.mapper;

import com.netpos.desafionetpos.dto.ProductDTO;
import com.netpos.desafionetpos.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserAccountMapper.class, StockMapper.class})
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {

    @Mapping(source = "userAccount.id", target = "userAccount.id")
    @Mapping(source = "stock.id", target = "stock.id")
    ProductDTO toDto(Product product);

    @Mapping(source = "userAccount.id", target = "userAccount.id")
    @Mapping(source = "stock.id", target = "stock.id")
    Product toEntity(ProductDTO productDTO);

}
