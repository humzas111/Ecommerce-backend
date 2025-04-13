package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFound;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFound("Category", "categoryId",categoryId));

        Product product = modelMapper.map(productDTO, Product.class);

        product.setCategory(category);
        double specialPrice= productDTO.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
        product.setSpecialPrice(specialPrice);
        product.setImage("defualt.png");
        Product saveProduct = productRepository.save(product);

        return modelMapper.map(saveProduct, ProductDTO.class);


    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();

        // Convert each Product entity to a ProductDTO
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());

        // Create a response object
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);

        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFound("Category", "categoryId", categoryId));

        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);

        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());

        ProductResponse response = new ProductResponse();
        response.setContent(productDTOs);

        return response;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword) {
        // Fetch products matching the keyword
        List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%'+ keyword+'%');

        // Map products to ProductDTO
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());

        // Prepare the response
        ProductResponse response = new ProductResponse();
        response.setContent(productDTOs);

        return response;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFound("Product", "productId", productId));

        Product product = modelMapper.map(productDTO, Product.class);

        existingProduct.setProductName(product.getProductName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDiscount(product.getDiscount());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setSpecialPrice(product.getSpecialPrice());

        Product savedProduct = productRepository.save(existingProduct);

        return modelMapper.map(savedProduct, ProductDTO.class);


    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFound("Product", "productId", productId));

        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);


    }


}
