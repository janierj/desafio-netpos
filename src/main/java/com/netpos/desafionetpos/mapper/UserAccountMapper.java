package com.netpos.desafionetpos.mapper;

import com.netpos.desafionetpos.dto.UserAccountDTO;
import com.netpos.desafionetpos.entity.UserAccount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserAccountMapper extends EntityMapper<UserAccountDTO, UserAccount> {

}
