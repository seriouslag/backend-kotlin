package com.nullspace.estore.services.interfaces

import com.google.firebase.database.DataSnapshot
import com.stripe.model.Dispute
import com.stripe.model.Refund

interface IStripeService {
    fun createDispute(dispute: Dispute)
    fun createRefund(refund: Refund)
    fun changeOrderStatus(chargeId: String, status: String, message: String?)
    fun requestOrderProcessing(dataSnapshot: DataSnapshot)
}
