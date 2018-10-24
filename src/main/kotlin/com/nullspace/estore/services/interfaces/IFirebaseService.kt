package com.nullspace.estore.services.interfaces

interface IFirebaseService {
    fun addListenerForUserRole(uid: String, userRoleCallback: (String) -> Unit)
    fun firebaseUserExists(uid: String): Boolean
    fun addRoleToFirebaseUser(uid: String, role: String): Boolean
}
