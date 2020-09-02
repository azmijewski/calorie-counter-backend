package com.sda.caloriecounterbackend.controller;

import com.sda.caloriecounterbackend.dto.ProductDto;
import com.sda.caloriecounterbackend.exception.ProductNotFoundException;
import com.sda.caloriecounterbackend.service.ProductService;
import com.sda.caloriecounterbackend.util.ResponseUriBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("api/v1/products")
@Log4j2
public class ProductController {

    private final ProductService productService;
    private final ResponseUriBuilder responseUriBuilder;

    public ProductController(ProductService productService, ResponseUriBuilder responseUriBuilder) {
        this.productService = productService;
        this.responseUriBuilder = responseUriBuilder;
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getProductsPage(@RequestParam(name = "page") Integer page,
                                                            @RequestParam(name = "size") Integer size) {
        Page<ProductDto> products = productService.getProductsPage(page, size);
        return ResponseEntity.ok(products);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto productDto;
        try {
            productDto = productService.getById(id);
        } catch (ProductNotFoundException e) {
            log.error(e);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productDto);
    }
    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody @Valid ProductDto productDto) {
        Long resultId = productService.save(productDto);
        URI location = responseUriBuilder.buildUri(resultId);
        return ResponseEntity.created(location).build();
    }
}
