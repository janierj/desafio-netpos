package com.netpos.desafionetpos.service;

import com.netpos.desafionetpos.config.Constants;
import com.netpos.desafionetpos.dto.ProductDTO;
import com.netpos.desafionetpos.dto.vm.AlterStockVM;
import com.netpos.desafionetpos.entity.Product;
import com.netpos.desafionetpos.entity.enumeration.Operation;
import com.netpos.desafionetpos.mapper.ProductMapper;
import com.netpos.desafionetpos.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findAllByNameAndCode(Long userAccountId, String filter, Sort order) {
        log.debug("Find all Products of by UserAccount {}, filter: {}, order by: {}", userAccountId, filter, order);
        List<Product> productsByFilters = productRepository.findByFilters(userAccountId, filter, order);
        return productMapper.toDto(productsByFilters);
    }

    @Transactional(readOnly = true)
    public ProductDTO findOne(Long productId, Long userAccountId) {
        log.debug("Find Product with ID: {}, and UserAccount with ID: {}", productId, userAccountId);
        Optional<Product> productOwner = productRepository.findOneByIdAndUserAccount_Id(productId, userAccountId);
        if (productOwner.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado");
        }
        return productMapper.toDto(productOwner.get());
    }

    public ProductDTO save(ProductDTO productDTO, Long userAccountId) {
        log.debug("Save a Product: {}, for the UserAccount Id: {}", productDTO, userAccountId);
        // Validates the "code" field in Product should be unique for each UserAccount
        Optional<Product> existCodeAndUserAccount = productRepository.findOneByCodeAndUserAccount_Id(productDTO.getCode(), userAccountId);
        if (productDTO.getId() == null && existCodeAndUserAccount.isPresent()) {   //Adding
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Já existe um produto com o código: " + productDTO.getCode());
        }

        Product product = productMapper.toEntity(productDTO);
        product = productRepository.save(product);

        return productMapper.toDto(product);
    }

    /**
     * Delete the Product by id.
     *
     * @param productId     the id of the Product to delete.
     * @param userAccountId the id of the UserAccount who owns the Product.
     */
    public void delete(Long productId, Long userAccountId) {
        log.debug("Request to delete product: {}, from UserAccount: {}", productId, userAccountId);
        ProductDTO productDTO = findOne(productId, userAccountId);
        productRepository.deleteById(productDTO.getId());
    }

    public void alterStock(AlterStockVM alterStockVM, Long productId, Long userAccountId) {
        ProductDTO productDTO = findOne(productId, userAccountId);
        int currentQuantity = productDTO.getStock().getQuantity();
        int alterQuantity = alterStockVM.getQuantity();

        if (alterStockVM.getOperation().equals(Operation.ADD)) {
            if (currentQuantity + alterQuantity > Constants.MAX_PRODUCT_STOCK) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Somente pode adicionar mais " + (Constants.MAX_PRODUCT_STOCK - currentQuantity) + " unidades");
            }
            productDTO.getStock().setQuantity(currentQuantity + alterQuantity);
        } else {
            if (currentQuantity - alterQuantity < 0) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Estoque insuficiente, somente pode retirar " + currentQuantity + " unidades");
            }
            productDTO.getStock().setQuantity(currentQuantity - alterQuantity);
        }
        Product product = productMapper.toEntity(productDTO);
        productRepository.save(product);
    }
}
