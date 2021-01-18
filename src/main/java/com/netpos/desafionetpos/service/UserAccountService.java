package com.netpos.desafionetpos.service;

import com.netpos.desafionetpos.dto.UserAccountDTO;
import com.netpos.desafionetpos.dto.vm.UserAccountVM;
import com.netpos.desafionetpos.entity.UserAccount;
import com.netpos.desafionetpos.mapper.UserAccountMapper;
import com.netpos.desafionetpos.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserAccountService {

    private final Logger log = LoggerFactory.getLogger(UserAccountService.class);

    private final UserAccountRepository userAccountRepository;

    private final UserAccountMapper userAccountMapper;

    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository userAccountRepository, UserAccountMapper userAccountMapper, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.userAccountMapper = userAccountMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserAccountDTO> findAllByFullName(String term) {
        log.debug("Find all users by fullName: {}", term);
        List<UserAccount> userAccountListByFullName = userAccountRepository.findByFullNameStartingWithIgnoreCaseOrderByFullName(term);
        return userAccountMapper.toDto(userAccountListByFullName);
    }

    @Transactional(readOnly = true)
    public List<UserAccountDTO> findAll() {
        log.debug("Find all users");
        List<UserAccount> userAccountListByFullName = userAccountRepository.findByOrderByFullName();
        return userAccountMapper.toDto(userAccountListByFullName);
    }

    @Transactional(readOnly = true)
    public UserAccountDTO findOne(Long id) {
        log.debug("Find UserAccount with ID: {}", id);
        Optional<UserAccount> userAccountById = userAccountRepository.findById(id);
        return userAccountById
                .map(userAccountMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserAccount with ID " + id + " does not exist"));
    }

    public UserAccountDTO save(UserAccountVM userAccountVM) {
        log.debug("Save a new UserAccount {}", userAccountVM);
        Optional<UserAccount> userAccountExists = userAccountRepository.findOneByEmail(userAccountVM.getEmail());
        if (userAccountExists.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O email ja existe");
        }

        UserAccount userAccount = userAccountMapper.toEntity(userAccountVM);
        String encryptedPassword = passwordEncoder.encode(userAccountVM.getPassword());
        userAccount.setPassword(encryptedPassword);

        userAccount = userAccountRepository.save(userAccount);
        return userAccountMapper.toDto(userAccount);
    }
}
