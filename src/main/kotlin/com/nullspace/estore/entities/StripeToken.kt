package com.nullspace.estore.entities

class StripeToken {
    var id: String? = null
    var email: String? = null
    var `object`: String? = null
    var clientIp: String? = null
    var created: Int = 0
    var isLivemode: Boolean = false
    var type: String? = null
    var isUsed: Boolean = false
}
