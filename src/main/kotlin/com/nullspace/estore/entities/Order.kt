package com.nullspace.estore.entities


class Order {

    var email: String? = null
    var date: Double = 0.toDouble()
    var total: Long = 0
    var cart: List<DbCartItem>? = null
    var token: StripeToken? = null
    var args: StripeArgs? = null
    var status: String? = null
    var amount: Long? = null
    var chargeId: String? = null

    var orderMessage: String? = null

    val tokenAsString: String
        get() = this.token!!.toString()


}
