package com.netpos.desafionetpos.controller;

import com.netpos.desafionetpos.controller.util.AppUtil;
import com.netpos.desafionetpos.dto.ProductDTO;
import com.netpos.desafionetpos.dto.UserAccountDTO;
import com.netpos.desafionetpos.dto.vm.AlterStockVM;
import com.netpos.desafionetpos.dto.vm.ProductEditVM;
import com.netpos.desafionetpos.service.ProductService;
import com.netpos.desafionetpos.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    private final UserAccountService userAccountService;

    public ProductController(ProductService productService, UserAccountService userAccountService) {
        this.productService = productService;
        this.userAccountService = userAccountService;
    }

    /**
     * {@code GET  /products} : Listar produtos com ordenação e filtro.
     *
     * @param userAccountId the UserAccount ID owner of the Products to retrieve.
     * @param filter        the term to use as filter.
     * @param order         the string sort to use.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Products in the body.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProductsByFilters(@RequestHeader("user_id") Long userAccountId,
                                                                    @RequestParam(required = false) String filter,
                                                                    @RequestParam(required = false) String[] order) throws URISyntaxException {
        log.debug("REST Request to filter Products by UserAccount: {}, term: {}, order: {}", userAccountId, filter, order);
        List<Sort.Order> sortListFromString = AppUtil.getSortListFromString(order);
        List<ProductDTO> result = productService.findAllByNameAndCode(userAccountId, filter, Sort.by(sortListFromString));
        return ResponseEntity.ok(result);
    }

    /**
     * {@code POST  /products} : Criar um novo Produto.
     *
     * @param productDTO the Product data to create.
     * @return the {@link ResponseEntity} com o status {@code 201 (Created)} e com o novo Produto no corpo.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestHeader("user_id") Long userAccountId,
                                                    @RequestBody @Valid ProductDTO productDTO) throws URISyntaxException {
        log.debug("REST Request to create a new Product: {}, for the UserAccountID: {}", productDTO, userAccountId);

        if (productDTO.getId() != null) {
            return ResponseEntity.badRequest().build();
        }

        UserAccountDTO currentUserAccount = userAccountService.findOne(userAccountId);
        productDTO.setUserAccount(new UserAccountDTO().id(currentUserAccount.getId()));

        ProductDTO result = productService.save(productDTO, currentUserAccount.getId());
        return ResponseEntity
                .created(new URI("/products/product_id" + result.getId()))
                .body(result);
    }

    /**
     * {@code PUT  /products/{product_id}} : Altera um produto.
     *
     * @param editProductVM the Product data to edit.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} containing the ProductDTO in the body.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{product_id}")
    public ResponseEntity<ProductDTO> editProduct(@RequestHeader("user_id") Long userAccountId,
                                                  @PathVariable("product_id") Long productId,
                                                  @RequestBody ProductEditVM editProductVM) throws URISyntaxException {
        log.debug("REST Request to edit the Product with ID: {}, for the UserAccountID: {}, with data: {}", productId, userAccountId, editProductVM);

        ProductDTO productDTO = productService.findOne(productId, userAccountId);
        productDTO.setName(editProductVM.getName());
        productDTO.setPrice(editProductVM.getPrice());

        ProductDTO result = productService.save(productDTO, userAccountId);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code GET  /products/{product_id}} : Detalhes de um Produto.
     *
     * @param userAccountId the UserAccount ID owner of the Products to retrieve.
     * @param productId     the id of the Product.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the Product in the body.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @GetMapping("/{product_id}")
    public ResponseEntity<ProductDTO> getOne(@RequestHeader("user_id") Long userAccountId,
                                             @PathVariable("product_id") Long productId) throws URISyntaxException {
        log.debug("REST Request get details of the Product ID: {}, owned by the UserAccount ID: {}", productId, userAccountId);
        ProductDTO productDTO = productService.findOne(productId, userAccountId);
        return ResponseEntity.ok(productDTO);
    }

    /**
     * {@code DELETE  /products/{product_id}} : delete the "id" chamado.
     *
     * @param productId the id of the Product to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{product_id}")
    public ResponseEntity<Void> deleteProduct(@RequestHeader("user_id") Long userAccountId,
                                              @PathVariable("product_id") Long productId) {
        log.debug("REST request to delete Product : {}, from UserAccountId: {}", productId, userAccountId);

        productService.delete(productId, userAccountId);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code POST  /products/{product_id}/stock} : Realiza uma operaçao ded entrada ou saida de estoque.
     *
     * @param userAccountId the UserAccount ID owner of the Products to retrieve.
     * @param productId     the id of the Product.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the Product in the body.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/{product_id}/stock")
    public ResponseEntity<Void> alterStock(@RequestHeader("user_id") Long userAccountId,
                                                 @PathVariable("product_id") Long productId,
                                                 @RequestBody @Valid AlterStockVM alterStockVM) throws URISyntaxException {
        log.debug("REST Request get details of the Product ID: {}, owned by the UserAccount ID: {}", productId, userAccountId);
        productService.alterStock(alterStockVM, productId, userAccountId);
        return ResponseEntity.noContent().build();
    }



}
