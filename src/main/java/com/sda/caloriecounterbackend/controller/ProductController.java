package com.sda.caloriecounterbackend.controller;

import com.sda.caloriecounterbackend.dto.ProductDto;
import com.sda.caloriecounterbackend.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getProductsPage(@RequestParam(name = "page") Integer page,
                                                            @RequestParam(name = "size") Integer size) {
        return productService.getProductsPage(page, size);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody @Valid ProductDto productDto) {
        return productService.save(productDto);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyProduct(@PathVariable(name = "id") Long id,
                                           @RequestBody @Valid ProductDto productDto) {
        return productService.modify(id, productDto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return productService.delete(id);
    }

}
