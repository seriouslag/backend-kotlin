package com.nullspace.estore.controllers

import com.nullspace.estore.entities.Exceptions.NotFound
import com.nullspace.estore.entities.Product
import com.nullspace.estore.repositories.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/api")
class ProductController @Autowired
constructor(private val productRepository: ProductRepository, environment: Environment) {
    private var isProd: Boolean = environment.activeProfiles.any { profile -> "prod" in profile}

    @GetMapping("/product/{id}")
    fun product(@PathVariable("id") id: Int): Product {
        val product = productRepository.getProductById(id)

        if (product.name != null) {
            if (!isProd) {
                System.out.println("Returning: " + product.name)
            }
        } else {
            throw NotFound("The product of id $id was not found.")
        }
        return product
    }

    @GetMapping("/search")
    fun product(@RequestParam("productName") productName: String): Set<Product> {
        val formattedName = productName.trim { it <= ' ' }

        return productRepository.findDistinctFirst5ByNameLikeIgnoreCaseOrderByName("%$formattedName%")
    }

    @GetMapping("/product/all")
    fun product(): Set<Product> {
        if (isProd) {
            println("Returning all products")
        }
        return productRepository.findAllByOrderByName()
    }

    @GetMapping("/searchpage")
    fun product(@RequestParam("page") pageNum: Int, @RequestParam("itemsPerPage") itemsPerPage: Int): Page<Product> {
        val pageRequest = PageRequest.of(pageNum, itemsPerPage)
        return productRepository.findAllByOrderByName(pageRequest)
    }
}