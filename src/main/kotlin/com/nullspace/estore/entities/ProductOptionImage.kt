package com.nullspace.estore.entities

import com.fasterxml.jackson.annotation.*

import javax.persistence.*

@Entity
@Table(name = "product_option_images")
class ProductOptionImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
    var location: String? = null
    var hasThumb: Boolean = false
    var order: Int = 0

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_options_id", insertable = false, updatable = false)
    @JsonIgnore
    var option: ProductOption? = null
}
