package com.netpos.desafionetpos.controller;

import com.netpos.desafionetpos.dto.UserAccountDTO;
import com.netpos.desafionetpos.dto.vm.UserAccountVM;
import com.netpos.desafionetpos.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserAccountController {

    private final Logger log = LoggerFactory.getLogger(UserAccountController.class);

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    /**
     * {@code GET  /users} : obter todos os Usuarios.
     *
     * @param nome o termo para ser pesquisado na tabela UserAccount pelo campo nome.
     * @return a {@link ResponseEntity} com status {@code 200 (OK)} e a lista de Usuarios no corpo.
     */
    @GetMapping
    public ResponseEntity<List<UserAccountDTO>> getAllUserAccounts(@RequestParam(value = "q", required = false) String nome) {
        log.debug("Rest request to get UserAccounts by name: {}", nome);
        if (nome != null) {
            return ResponseEntity.ok().body(userAccountService.findAllByFullName(nome));
        }
        return ResponseEntity.ok(userAccountService.findAll());
    }

    /**
     * {@code GET  /users} : obter os detalhes de um Usuario.
     *
     * @param userId o ID do UserAccount.
     * @return a {@link ResponseEntity} com status {@code 200 (OK)} e o UserAccount no corpo.
     */
    @GetMapping("/{user_id}")
    public ResponseEntity<UserAccountDTO> getUserAccountById(@PathVariable(name = "user_id") Long userId) {
        log.debug("REST Request to get details from UserAccount with ID: {}", userId);
        UserAccountDTO userAccountDTO = userAccountService.findOne(userId);
        return ResponseEntity.ok(userAccountDTO);
    }

    /**
     * {@code POST  /users} : Criar um novo UserAccout.
     *
     * @param userAccountVM o UserAccount a criar.
     * @return the {@link ResponseEntity} com o status {@code 201 (Created)} e com o novo UserAccount no corpo, or com o status {@code 400 (Bad Request)} se o UserAccount ja possui um ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping
    public ResponseEntity<UserAccountDTO> createUser(@RequestBody @Valid UserAccountVM userAccountVM) throws URISyntaxException {
        log.debug("REST Request to create a new UserAccount");
        if (userAccountVM.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        UserAccountDTO result = userAccountService.save(userAccountVM);
        return ResponseEntity
                .created(new URI("/users/user_id" + result.getId()))
                .body(result);
    }

}
