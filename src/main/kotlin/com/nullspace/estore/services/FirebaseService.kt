package com.nullspace.estore.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nullspace.estore.services.interfaces.IFirebaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.HashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Service
class FirebaseService
// private static boolean isInit = false;

@Autowired
constructor() : IFirebaseService {
    init {
        database = FirebaseDatabase.getInstance()
    }

    override fun addListenerForUserRole(uid: String, userRoleCallback: (String) -> Unit) {
        database = FirebaseDatabase.getInstance()
        val ref = database!!.getReference("roles/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot != null) {
                    val value = dataSnapshot.getValue(String::class.java)
                    println(value)
                    userRoleCallback(value)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Firebase admin role check cancelled")
            }
        })
    }

    override fun firebaseUserExists(uid: String): Boolean {
        try {
            val user = FirebaseAuth.getInstance().getUser(uid)

            return user != null
        } catch (fbae: FirebaseAuthException) {
            System.out.println("Firebase Auth Exception. Error code: " + fbae.errorCode + "; " + fbae.message)
            return false
        }

    }

    override fun addRoleToFirebaseUser(uid: String, role: String): Boolean {
        try {
            val firebaseLatch = CountDownLatch(1)
            val ref = database!!.getReference("roles/")
            val roleMap = HashMap<String, Any>()
            roleMap[uid] = role
            ref.setValue(roleMap) { error, ref1 ->
                if (error != null) {
                    println("Data could not be saved " + error.message)
                } else {
                    println("Data saved successfully.")
                }
                firebaseLatch.countDown()
            }
            firebaseLatch.await(1L, TimeUnit.SECONDS)
            return true
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return false
        }

    }

    companion object {

        private var database: FirebaseDatabase? = null
    }
}