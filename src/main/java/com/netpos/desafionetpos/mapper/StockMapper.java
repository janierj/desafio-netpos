package com.netpos.desafionetpos.mapper;

import com.netpos.desafionetpos.dto.StockDTO;
import com.netpos.desafionetpos.entity.Stock;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockMapper extends EntityMapper<StockDTO, Stock> {


}
