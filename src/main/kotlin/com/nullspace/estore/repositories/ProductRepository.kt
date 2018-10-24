package com.nullspace.estore.repositories

import com.nullspace.estore.entities.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface ProductRepository : CrudRepository<Product, Int> {
    fun getProductById(ProductId: Int?): Product
    fun findDistinctFirst5ByNameLikeIgnoreCaseOrderByName(string: String): Set<Product>
    fun findAllByOrderByName(): Set<Product>
    fun findAllByOrderByName(page: Pageable): Page<Product>
    fun save(product: Product): Product
    fun findFirstByName(name: String): Product
    fun existsProductById(id: Int): Boolean
    @Transactional
    fun deleteProductById(id: Int): Int
}
