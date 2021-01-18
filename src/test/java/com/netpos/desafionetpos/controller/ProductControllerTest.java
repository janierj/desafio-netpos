package com.netpos.desafionetpos.controller;

import com.netpos.desafionetpos.DesafioNetposApplication;
import com.netpos.desafionetpos.dto.ProductDTO;
import com.netpos.desafionetpos.dto.StockDTO;
import com.netpos.desafionetpos.dto.vm.AlterStockVM;
import com.netpos.desafionetpos.dto.vm.ProductEditVM;
import com.netpos.desafionetpos.entity.Product;
import com.netpos.desafionetpos.entity.Stock;
import com.netpos.desafionetpos.entity.UserAccount;
import com.netpos.desafionetpos.entity.enumeration.Operation;
import com.netpos.desafionetpos.repository.ProductRepository;
import com.netpos.desafionetpos.repository.UserAccountRepository;
import com.netpos.desafionetpos.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ProductController} REST controller.
 */
@SpringBootTest(classes = {DesafioNetposApplication.class})
@AutoConfigureMockMvc
class ProductControllerTest {

    private static final String CONTROLLER_BASE_URL = "/products";

    private static final String DEFAULT_PRODUCT_NAME = "Apple iPhone 12";
    private static final String DEFAULT_PRODUCT_CODE = "123456";
    private static final BigDecimal DEFAULT_PRODUCT_PRICE = BigDecimal.valueOf(8000);
    private static final Integer DEFAULT_PRODUCT_STOCK_QUANTITY = 500;

    private static final String EDIT_PRODUCT_NAME = "Apple iPhone X";
    private static final BigDecimal EDIT_PRODUCT_PRICE = BigDecimal.valueOf(6000);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private MockMvc restAccountMockMvc;

    private Product product;

    public static Product createEntity() {
        Product product = new Product();
        product.setName(DEFAULT_PRODUCT_NAME);
        product.setCode(DEFAULT_PRODUCT_CODE);
        product.setPrice(DEFAULT_PRODUCT_PRICE);
        product.setStock(new Stock().quantity(DEFAULT_PRODUCT_STOCK_QUANTITY));
        return product;
    }


    @BeforeEach
    public void initTest() {
        product = createEntity();
    }

    private void insertProductWithUserAccount() {
        UserAccount userAccount = UserAccountControllerTest.createEntity();
        userAccountRepository.saveAndFlush(userAccount);
        product.setUserAccount(userAccount);
        productRepository.saveAndFlush(product);
    }

    @Test
    @Transactional
    void createProductIsSuccessful() throws Exception {
        UserAccount userAccount = UserAccountControllerTest.createEntity();
        userAccountRepository.save(userAccount);

        int databaseSizeBeforeInsert = productRepository.findAll().size();

        ProductDTO productDTO = new ProductDTO();
        productDTO.setCode(DEFAULT_PRODUCT_CODE);
        productDTO.setName(DEFAULT_PRODUCT_NAME);
        productDTO.setPrice(DEFAULT_PRODUCT_PRICE);
        productDTO.setStock(new StockDTO().quantity(DEFAULT_PRODUCT_STOCK_QUANTITY));

        restAccountMockMvc
                .perform(post(CONTROLLER_BASE_URL)
                        .header("user_id", userAccount.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(DEFAULT_PRODUCT_CODE))
                .andExpect(jsonPath("$.name").value(DEFAULT_PRODUCT_NAME))
                .andExpect(jsonPath("$.price").value(DEFAULT_PRODUCT_PRICE));

        int databaseSizeAfterInsert = productRepository.findAll().size();
        assertThat(databaseSizeAfterInsert).isEqualTo(databaseSizeBeforeInsert + 1);
    }

    @Test
    @Transactional
    void createProductWithIdIsNotAllowed() throws Exception {
        UserAccount userAccount = UserAccountControllerTest.createEntity();
        userAccountRepository.save(userAccount);

        int databaseSizeBeforeInsert = productRepository.findAll().size();

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setCode(DEFAULT_PRODUCT_CODE);
        productDTO.setName(DEFAULT_PRODUCT_NAME);
        productDTO.setPrice(DEFAULT_PRODUCT_PRICE);
        productDTO.setStock(new StockDTO().quantity(DEFAULT_PRODUCT_STOCK_QUANTITY));

        restAccountMockMvc
                .perform(post(CONTROLLER_BASE_URL)
                        .header("user_id", userAccount.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(productDTO)))
                .andExpect(status().isBadRequest());

        int databaseSizeAfterInsert = productRepository.findAll().size();
        assertThat(databaseSizeAfterInsert).isEqualTo(databaseSizeBeforeInsert);
    }

    @Test
    @Transactional
    void createProductWithDuplicatedCodeIsUnProcessableEntity() throws Exception {
        insertProductWithUserAccount();
        int databaseSizeBeforeInsert = productRepository.findAll().size();

        ProductDTO productDTO = new ProductDTO();
        productDTO.setCode(DEFAULT_PRODUCT_CODE);
        productDTO.setName(DEFAULT_PRODUCT_NAME);
        productDTO.setPrice(DEFAULT_PRODUCT_PRICE);
        productDTO.setStock(new StockDTO().quantity(DEFAULT_PRODUCT_STOCK_QUANTITY));

        restAccountMockMvc
                .perform(post(CONTROLLER_BASE_URL)
                        .header("user_id", product.getUserAccount().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(productDTO)))
                .andExpect(status().isUnprocessableEntity());

        int databaseSizeAfterInsert = productRepository.findAll().size();
        assertThat(databaseSizeAfterInsert).isEqualTo(databaseSizeBeforeInsert).isEqualTo(1);
    }

    @Test
    @Transactional
    void createProductWithNonExistentUserAccountShouldBeBadRequest() throws Exception {
        int databaseSizeBeforeInsert = productRepository.findAll().size();

        ProductDTO productDTO = new ProductDTO();
        productDTO.setCode(DEFAULT_PRODUCT_CODE);
        productDTO.setName(DEFAULT_PRODUCT_NAME);
        productDTO.setPrice(DEFAULT_PRODUCT_PRICE);
        productDTO.setStock(new StockDTO().quantity(DEFAULT_PRODUCT_STOCK_QUANTITY));

        restAccountMockMvc
                .perform(post(CONTROLLER_BASE_URL)
                        .header("user_id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(productDTO)))
                .andExpect(status().isBadRequest());

        int databaseSizeAfterInsert = productRepository.findAll().size();
        assertThat(databaseSizeAfterInsert).isEqualTo(databaseSizeBeforeInsert);

    }

    @Test
    @Transactional
    void getAllProductsWithSortAndFiltersShouldReturnResults() throws Exception {
        insertProductWithUserAccount();

        restAccountMockMvc
                .perform(get(CONTROLLER_BASE_URL + "?filter=Apple&order=name,DESC;price,ASC")
                        .header("user_id", product.getUserAccount().getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_PRODUCT_NAME)))
                .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_PRODUCT_CODE)))
                .andExpect(jsonPath("$.[*].stock.quantity").value(hasItem(DEFAULT_PRODUCT_STOCK_QUANTITY)));
    }

    @Test
    @Transactional
    void editProductIsSuccessful() throws Exception {
        insertProductWithUserAccount();

        ProductEditVM productEditVM = new ProductEditVM();
        productEditVM.setName(EDIT_PRODUCT_NAME);
        productEditVM.setPrice(EDIT_PRODUCT_PRICE);

        restAccountMockMvc
                .perform(put(CONTROLLER_BASE_URL + "/" + product.getId())
                        .header("user_id", product.getUserAccount().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(productEditVM)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value(DEFAULT_PRODUCT_CODE))
                .andExpect(jsonPath("$.name").value(EDIT_PRODUCT_NAME))
                .andExpect(jsonPath("$.price").value(EDIT_PRODUCT_PRICE));
    }

    @Test
    @Transactional
    void editNonExistentProductShouldReturnNotFound() throws Exception {
        insertProductWithUserAccount();

        ProductEditVM productEditVM = new ProductEditVM();
        productEditVM.setName(EDIT_PRODUCT_NAME);
        productEditVM.setPrice(EDIT_PRODUCT_PRICE);

        restAccountMockMvc
                .perform(put(CONTROLLER_BASE_URL + "/" + 123)
                        .header("user_id", product.getUserAccount().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(productEditVM)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void getOneProductDetailsByIdIsSuccessful() throws Exception {
        insertProductWithUserAccount();

        restAccountMockMvc
                .perform(get(CONTROLLER_BASE_URL + "/" + product.getId())
                        .header("user_id", product.getUserAccount().getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(DEFAULT_PRODUCT_NAME))
                .andExpect(jsonPath("$.code").value(DEFAULT_PRODUCT_CODE))
                .andExpect(jsonPath("$.price").value(DEFAULT_PRODUCT_PRICE))
                .andExpect(jsonPath("$.stock.quantity").value(DEFAULT_PRODUCT_STOCK_QUANTITY));
    }

    @Test
    @Transactional
    void softDeleteProductByIdIsSuccessful() throws Exception {
        insertProductWithUserAccount();
        int databaseSizeBeforeDelete = productRepository.findAll().size();
        assertThat(databaseSizeBeforeDelete).isEqualTo(1);

        restAccountMockMvc
                .perform(delete(CONTROLLER_BASE_URL + "/" + product.getId())
                        .header("user_id", product.getUserAccount().getId()))
                .andExpect(status().isNoContent());

        int databaseSizeAfterDelete = productRepository.findAll().size();

        assertThat(databaseSizeAfterDelete).isEqualTo(0);
    }

    @Test
    @Transactional
    void addProductStockIsSuccessful() throws Exception {
        insertProductWithUserAccount();

        AlterStockVM alterStockVM = new AlterStockVM();
        alterStockVM.setOperation(Operation.ADD);
        alterStockVM.setQuantity(500);  //Edge case

        restAccountMockMvc
                .perform(post(CONTROLLER_BASE_URL + "/" + product.getId() + "/stock")
                        .header("user_id", product.getUserAccount().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(alterStockVM)))
                .andExpect(status().isNoContent());

        Optional<Product> productAfterEdit = productRepository.findById(product.getId());
        assertThat(productAfterEdit).isNotEmpty();
        productAfterEdit.map(product1 -> assertThat(product1.getStock().getQuantity()).isEqualTo(DEFAULT_PRODUCT_STOCK_QUANTITY + 500));
    }

    @Test
    @Transactional
    void subProductStockIsSuccessful() throws Exception {
        insertProductWithUserAccount();

        AlterStockVM alterStockVM = new AlterStockVM();
        alterStockVM.setOperation(Operation.SUB);
        alterStockVM.setQuantity(500);  //Edge case

        restAccountMockMvc
                .perform(post(CONTROLLER_BASE_URL + "/" + product.getId() + "/stock")
                        .header("user_id", product.getUserAccount().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(alterStockVM)))
                .andExpect(status().isNoContent());

        Optional<Product> productAfterEdit = productRepository.findById(product.getId());
        assertThat(productAfterEdit).isNotEmpty();
        productAfterEdit.map(product1 -> assertThat(product1.getStock().getQuantity()).isEqualTo(DEFAULT_PRODUCT_STOCK_QUANTITY - 500));
    }

    @Test
    @Transactional
    void addProductStockOver1000IsNotAllowed() throws Exception {
        insertProductWithUserAccount();

        AlterStockVM alterStockVM = new AlterStockVM();
        alterStockVM.setOperation(Operation.ADD);
        alterStockVM.setQuantity(501);  //Edge case

        restAccountMockMvc
                .perform(post(CONTROLLER_BASE_URL + "/" + product.getId() + "/stock")
                        .header("user_id", product.getUserAccount().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(alterStockVM)))
                .andExpect(status().isUnprocessableEntity());

        Optional<Product> productAfterEdit = productRepository.findById(product.getId());
        assertThat(productAfterEdit).isNotEmpty();
        productAfterEdit.map(product1 -> assertThat(product1.getStock().getQuantity()).isEqualTo(DEFAULT_PRODUCT_STOCK_QUANTITY));
    }

    @Test
    @Transactional
    void addProductStockUnder0IsNotAllowed() throws Exception {
        insertProductWithUserAccount();

        AlterStockVM alterStockVM = new AlterStockVM();
        alterStockVM.setOperation(Operation.SUB);
        alterStockVM.setQuantity(501);  //Edge case

        restAccountMockMvc
                .perform(post(CONTROLLER_BASE_URL + "/" + product.getId() + "/stock")
                        .header("user_id", product.getUserAccount().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(alterStockVM)))
                .andExpect(status().isUnprocessableEntity());

        Optional<Product> productAfterEdit = productRepository.findById(product.getId());
        assertThat(productAfterEdit).isNotEmpty();
        productAfterEdit.map(product1 -> assertThat(product1.getStock().getQuantity()).isEqualTo(DEFAULT_PRODUCT_STOCK_QUANTITY));
    }

}