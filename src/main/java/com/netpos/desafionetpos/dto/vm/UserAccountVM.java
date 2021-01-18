package com.netpos.desafionetpos.dto.vm;

import com.netpos.desafionetpos.dto.UserAccountDTO;

import javax.validation.constraints.Size;

/**
 * View Model class para a criação de um novo UserAccount.
 * Esta clase é usada com o objetivo de ocultar a senha no UserAccountDTO
 */
public class UserAccountVM extends UserAccountDTO {

    @Size(min = 4, max = 100)
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
