package com.nullspace.estore.entities

class DbCartItem {

    private var productId: Int = 0
    private var productOptionPrice: Long = 0
    private var productOptionId: Int = 0
    private var quantity: Int = 0
    var dateAdded: Double = 0.toDouble()

    fun getProductId(): Double {
        return productId.toDouble()
    }

    fun setProductId(productId: Int) {
        this.productId = productId
    }

    fun getProductOptionPrice(): Double {
        return productOptionPrice.toDouble()
    }

    fun setProductOptionPrice(productOptionPrice: Int) {
        this.productOptionPrice = productOptionPrice.toLong()
    }

    fun getProductOptionId(): Double {
        return productOptionId.toDouble()
    }

    fun setProductOptionId(productOptionId: Int) {
        this.productOptionId = productOptionId
    }

    fun getQuantity(): Double {
        return quantity.toDouble()
    }

    fun setQuantity(quantity: Int) {
        this.quantity = quantity
    }


}
