package com.endofjanuary.placement_example.repo

import android.util.Log
import com.endofjanuary.placement_example.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CompletableDeferred

class FireStoreDBImpl(
    private val db: FirebaseFirestore
) : FireStoreDBRepo {

    //  val userProfileDoc = db.document("/users/user_profile")

    private fun getUserProfileData() {
        db.collection("users").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {

                } else {
                    if (!value!!.isEmpty) {

                        val documents = value.documents

                        for (document in documents) {
                            val comment = document.get("comment") as String
                            val useremail = document.get("User E-mail") as String
                            val downloadURL = document.get("download URL") as String
                            // val post = Post(useremail, comment, downloadURL)
                        }
                    }
                }
            }

    }

    override suspend fun updateUserProfileData(
        userName: String,
        refine: Boolean?,
        save: Boolean?,
        userAuthID: String
    ): Resource<String> {

        val userProfileDoc = db.document("/users/${userAuthID}")

        val def = CompletableDeferred<Resource<String>>()
        try {

            val user = hashMapOf(
                "auto_refine" to (refine ?: false),
                "auto_save" to (save ?: false),
                "name" to userName,
            )

            userProfileDoc
                .set(user)
                .addOnSuccessListener {
                    Log.d("FIRESTORE upd", "Document has been saved!")
                    def.complete(Resource.Success("Document has been saved!"))
                }
                .addOnFailureListener { e ->
                    Log.w("FIRESTORE upd", "Error adding document", e)
                    throw Exception(e.message)
                }
            return def.await()
        } catch (e: Exception) {
            return Resource.Error("Error adding document ${e.message}")
        }
    }

}