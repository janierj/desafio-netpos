package com.netpos.desafionetpos.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.assertj.core.api.Assertions.assertThat;


import com.netpos.desafionetpos.DesafioNetposApplication;
import com.netpos.desafionetpos.dto.vm.UserAccountVM;
import com.netpos.desafionetpos.entity.UserAccount;
import com.netpos.desafionetpos.repository.UserAccountRepository;
import com.netpos.desafionetpos.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UserAccountController} REST controller.
 */
@SpringBootTest(classes = {DesafioNetposApplication.class})
@AutoConfigureMockMvc
class UserAccountControllerTest {

    private static final String CONTROLLER_BASE_URL = "/users";

    private static final String DEFAULT_USER_ACCOUNT_FULL_NAME = "Janier J. Ramirez";
    private static final String DEFAULT_USER_ACCOUNT_EMAIL = "janier.test@gmail.com";
    private static final String DEFAULT_USER_ACCOUNT_PLAIN_PASSWORD = "defaultplainpassword";
    private static final String DEFAULT_PASSWORD_ENCODED = "pQwZuO07QBgZuF0guuIoh1pGg16Xkpbftb4yiFMUp2KDNyopHESng9i4GDSh";

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private MockMvc restAccountMockMvc;

    private UserAccount userAccount;


    public static UserAccount createEntity() {
        UserAccount userAccount = new UserAccount();
        userAccount.setFullName(DEFAULT_USER_ACCOUNT_FULL_NAME);
        userAccount.setEmail(DEFAULT_USER_ACCOUNT_EMAIL);
        userAccount.setPassword(DEFAULT_PASSWORD_ENCODED);
        return userAccount;
    }


    @BeforeEach
    public void initTest() {
        userAccount = createEntity();
    }

    @Test
    @Transactional
    void createUserAccountIsSuccessful() throws Exception {
        int databaseSizeBeforeInsert = userAccountRepository.findAll().size();

        UserAccountVM userAccountVM = new UserAccountVM();
        userAccountVM.setEmail(DEFAULT_USER_ACCOUNT_EMAIL);
        userAccountVM.setFullName(DEFAULT_USER_ACCOUNT_FULL_NAME);
        userAccountVM.setPassword(DEFAULT_USER_ACCOUNT_PLAIN_PASSWORD);

        restAccountMockMvc
                .perform(post(CONTROLLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(userAccountVM)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.email").value(DEFAULT_USER_ACCOUNT_EMAIL))
                .andExpect(jsonPath("$.full_name").value(DEFAULT_USER_ACCOUNT_FULL_NAME));

        int databaseSizeAfterInsert = userAccountRepository.findAll().size();
        assertThat(databaseSizeAfterInsert).isEqualTo(databaseSizeBeforeInsert + 1);

    }

    @Test
    @Transactional
    void createUserAccountWithEmailAlreadyExistsIsNotAllowed() throws Exception {
        userAccountRepository.saveAndFlush(userAccount);
        int databaseSizeBeforeInsert = userAccountRepository.findAll().size();

        UserAccountVM userAccountVM = new UserAccountVM();
        userAccountVM.setEmail(DEFAULT_USER_ACCOUNT_EMAIL);
        userAccountVM.setFullName(DEFAULT_USER_ACCOUNT_FULL_NAME);
        userAccountVM.setPassword(DEFAULT_USER_ACCOUNT_PLAIN_PASSWORD);

        restAccountMockMvc
                .perform(post(CONTROLLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(userAccountVM)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));

        int databaseSizeAfterInsert = userAccountRepository.findAll().size();
        assertThat(databaseSizeAfterInsert).isEqualTo(databaseSizeBeforeInsert);

    }

    @Test
    @Transactional
    void createUserAccountWithWrongEmailFormat() throws Exception {
        userAccountRepository.saveAndFlush(userAccount);
        int databaseSizeBeforeInsert = userAccountRepository.findAll().size();

        UserAccountVM userAccountVM = new UserAccountVM();
        userAccountVM.setEmail("wrong-email-format.com");
        userAccountVM.setFullName(DEFAULT_USER_ACCOUNT_FULL_NAME);
        userAccountVM.setPassword(DEFAULT_USER_ACCOUNT_PLAIN_PASSWORD);

        restAccountMockMvc
                .perform(post(CONTROLLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(userAccountVM)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));

        int databaseSizeAfterInsert = userAccountRepository.findAll().size();
        assertThat(databaseSizeAfterInsert).isEqualTo(databaseSizeBeforeInsert);

    }

    @Test
    @Transactional
    void createUserAccountWithIdIsNotAllowed() throws Exception {
        int databaseSizeBeforeInsert = userAccountRepository.findAll().size();

        UserAccountVM userAccountVM = new UserAccountVM();
        userAccountVM.setId(1L);
        userAccountVM.setEmail(DEFAULT_USER_ACCOUNT_EMAIL);
        userAccountVM.setFullName(DEFAULT_USER_ACCOUNT_FULL_NAME);
        userAccountVM.setPassword(DEFAULT_USER_ACCOUNT_PLAIN_PASSWORD);

        restAccountMockMvc
                .perform(post(CONTROLLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(userAccountVM)))
                .andExpect(status().isBadRequest());

        int databaseSizeAfterInsert = userAccountRepository.findAll().size();
        assertThat(databaseSizeAfterInsert).isEqualTo(databaseSizeBeforeInsert);
    }


    @Test
    @Transactional
    void getUserAccountByIdIsSuccessful() throws Exception {
        userAccount = userAccountRepository.saveAndFlush(userAccount);

        restAccountMockMvc
                .perform(get(CONTROLLER_BASE_URL + "/" + userAccount.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(userAccount.getId()))
                .andExpect(jsonPath("$.email").value(DEFAULT_USER_ACCOUNT_EMAIL))
                .andExpect(jsonPath("$.full_name").value(DEFAULT_USER_ACCOUNT_FULL_NAME));
    }

    @Test
    @Transactional
    void getAllUserAccountsWithFilterShouldReturnResults() throws Exception {
        userAccountRepository.saveAndFlush(userAccount);
        restAccountMockMvc
                .perform(get("/users?q=Janier"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_USER_ACCOUNT_EMAIL)))
                .andExpect(jsonPath("$.[*].full_name").value(hasItem(DEFAULT_USER_ACCOUNT_FULL_NAME)));
    }

    @Test
    @Transactional
    void getAllUserAccountsWithoutFiltersShouldReturnResults() throws Exception {
        userAccountRepository.saveAndFlush(userAccount);
        restAccountMockMvc
                .perform(get(CONTROLLER_BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_USER_ACCOUNT_EMAIL)))
                .andExpect(jsonPath("$.[*].full_name").value(hasItem(DEFAULT_USER_ACCOUNT_FULL_NAME)));
    }

    @Test
    @Transactional
    void getAllUserAccountsWithWrongFilterShouldNotReturnResults() throws Exception {
        userAccountRepository.saveAndFlush(userAccount);
        restAccountMockMvc
                .perform(get(CONTROLLER_BASE_URL + "?q=UserAccountNotExists"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isEmpty());
    }



}