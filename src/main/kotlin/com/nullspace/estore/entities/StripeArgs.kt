package com.nullspace.estore.entities

class StripeArgs {

    var billing_name: String? = null
    var billing_address_country: String? = null
    var billing_address_zip: String? = null
    var billing_address_state: String? = null
    var billing_address_line1: String? = null
    var billing_address_city: String? = null
    var billing_address_country_code: String? = null

    var shipping_name: String? = null
    var shipping_address_country: String? = null
    var shipping_address_zip: String? = null
    var shipping_address_state: String? = null
    var shipping_address_line1: String? = null
    var shipping_address_city: String? = null
    var shipping_address_country_code: String? = null
}
