package com.nullspace.estore.services

import com.google.api.client.googleapis.util.Utils
import com.google.firebase.database.*
import com.nullspace.estore.services.interfaces.IStripeService
import com.nullspace.estore.entities.Charge
import com.nullspace.estore.entities.Order
import com.stripe.Stripe
import com.stripe.exception.*
import com.stripe.model.Dispute
import com.stripe.model.Refund
import org.springframework.context.annotation.DependsOn
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.util.*
import com.google.api.client.json.GenericJson
import java.io.*


@Service
@DependsOn("FirebaseAppConfig")
class StripeService : IStripeService {
    init {
        val `is` = ClassPathResource("stripe.json")
        val data = readFromInputStream(`is`.inputStream)
        val jsonFactory = Utils.getDefaultJsonFactory()
        val parsed = jsonFactory.fromString(data, GenericJson::class.java)
        Stripe.apiKey = parsed["apiKey"].toString()
        println("Stripe is init")
        database = FirebaseDatabase.getInstance()
    }

    @Throws(IOException::class)
    private fun readFromInputStream(inputStream: InputStream): String {
        val resultStringBuilder = StringBuilder()
        BufferedReader(InputStreamReader(inputStream)).use { br ->
            var line: String
            var stop = false
            while (!stop && br.ready()) {
                line = br.readLine()
                if(line == null) {
                    stop = true
                }
                resultStringBuilder.append(line).append("\n")
            }
        }
        return resultStringBuilder.toString()
    }

    private fun setupOrderListener() {
        val ref = database.getReference("orders/")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String) {
                requestOrderProcessing(dataSnapshot)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String) {
                requestOrderProcessing(dataSnapshot)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun createDispute(dispute: Dispute) {
        try {
            val ref = database.getReference("disputes/")
            val disputeUpdates = HashMap<String, Any>()
            disputeUpdates[dispute.charge] = dispute
            ref.setValueAsync(disputeUpdates)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun createRefund(refund: Refund) {
        try {
            val ref = database.getReference("refunds/")
            val refundUpdates = HashMap<String, Any>()
            refundUpdates[refund.charge] = refund
            ref.setValueAsync(refundUpdates)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun changeOrderStatus(chargeId: String, status: String, message: String?) {
        try {
            val ref = database.getReference("charges/$chargeId")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val myCharge = dataSnapshot.getValue(Charge::class.java)
                    val orderRef = database.getReference("orders/" + myCharge.id + "/" + myCharge.orderId)
                    if (myCharge.id != null && myCharge.orderId != null) {
                        val orderParams = HashMap<String, Any>()
                        orderParams["status"] = status
                        orderParams["orderMessage"] = message.toString()
                        orderRef.updateChildrenAsync(orderParams)

                        val params = HashMap<String, Any>()
                        params["status"] = status

                        ref.updateChildrenAsync(params)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun requestOrderProcessing(dataSnapshot: DataSnapshot) {
        for (child in dataSnapshot.children) {
            try {
                val order = child.getValue(Order::class.java)

                if (order.status.equals("processing")) {
                    println("Attempting to process")

                    val params = HashMap<String, Any>()
                    params["amount"] = order.total
                    params["currency"] = "usd"
                    params["source"] = order.token?.id.toString()

                    try {
                        val charge = com.stripe.model.Charge.create(params)

                        println("charge: " + charge.id)

                        if (dataSnapshot.key != null && child.key != null) {
                            val childref = database.getReference("orders/" + dataSnapshot.key + "/" + child.key)
                            val childUpdates = HashMap<String, Any>()
                            childUpdates["status"] = "processed"
                            childUpdates["chargeId"] = charge.id
                            childUpdates["amount"] = charge.amount

                            childref.updateChildrenAsync(childUpdates)

                            val chargeRef = database.getReference("charges/" + charge.id)
                            val chargeUpdates = HashMap<String, Any>()
                            chargeUpdates["status"] = "waiting"
                            chargeUpdates["id"] = dataSnapshot.key
                            chargeUpdates["orderId"] = child.key
                            chargeUpdates["amount"] = charge.amount

                            chargeRef.updateChildrenAsync(chargeUpdates)
                            val orderTotal = order.total
                            val total = orderTotal / 100

                            println("A charge of $$total should have been placed")
                        }
                    } catch (e: CardException) {
                        // Since it's a decline, CardException will be caught
                        println("status is - " + e.code)
                        println("message is - " + e.param)
                        println("Card error: Payment failure $e")
                    } catch (e: InvalidRequestException) {
                        // Invalid parameters were supplied to Stripe's API
                        println("Invalid request: Payment failure: $e")
                    } catch (e: AuthenticationException) {
                        // Authentication with Stripe's API failed (maybe the API key is wrong)
                        println("Stripe auth error: Payment failure: $e")
                    } catch (e: ApiConnectionException) {
                        // Network communication with Stripe failed
                        // TODO - retry?
                        println("Need to add retry: Payment failure: $e")
                    } catch (e: ApiException) {
                        println("API Failure: Payment failure: $e")
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    companion object {

        private lateinit var database: FirebaseDatabase
    }
}