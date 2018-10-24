package com.nullspace.estore.entities

import com.fasterxml.jackson.annotation.*
import org.hibernate.annotations.SortNatural

import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator::class)
@Entity
@Table(name = "product_options")
class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
    var type: String? = null
    var price: Double = 0.toDouble()
    var quantity: Int = 0

    @OneToMany(mappedBy = "option", fetch = FetchType.EAGER)
    @OrderBy("order asc")
    @SortNatural
    var images: Set<ProductOptionImage>? = null

    @OneToMany(mappedBy = "option", fetch = FetchType.EAGER)
    @SortNatural
    var suboptions: Set<ProductSuboption>? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    @JsonIgnore
    var product: Product? = null
}
