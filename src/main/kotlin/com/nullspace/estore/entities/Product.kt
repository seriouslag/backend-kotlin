package com.nullspace.estore.entities

import javax.persistence.*

@Entity
@Table(name = "products")
class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0
    var name: String? = null
    var description: String? = null

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    @OrderBy("id asc")
    var options: Set<ProductOption>? = null

    val defaultImageUrl: String
        get() {
            val firstOptionWithLocation = this.options!!.stream()
                    .map<String> { productOption ->
                        val location = productOption.images?.stream()
                                ?.filter { currentImage -> !currentImage.location.equals("") }
                                ?.findFirst()
                                ?.map { image -> image.location}

                        location?.orElse("")

                    }.findFirst()

            return firstOptionWithLocation.orElse("")

        }
}
