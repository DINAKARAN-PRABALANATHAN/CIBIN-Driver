package com.cibinenterprizes.cibindrivers

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.ByteArrayOutputStream

class MyProfile : AppCompatActivity() {

    var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var TAKE_IMAGE_CODE = 10001
    var database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        var getData = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var userName =
                    snapshot.child("User Details").child(user?.uid.toString()).child("Profile")
                        .child("username").getValue().toString()
                var userId = snapshot.child("User Details").child(user?.uid.toString()).child("Profile")
                    .child("cdid").getValue().toString()
                var emailId =
                    snapshot.child("User Details").child(user?.uid.toString()).child("Profile")
                        .child("emailId").getValue().toString()
                var mobile =
                    snapshot.child("User Details").child(user?.uid.toString()).child("Profile")
                        .child("mobile").getValue().toString()
                var profilePhoto =
                    snapshot.child("User Details").child(user?.uid.toString()).child("Profile")
                        .child("profilePhoto").getValue().toString()
                my_profile_id.setText(userId)
                my_profile_user_name.setText(userName)
                my_profile_email_id.setText(emailId)
                my_profile_mobile_number.setText(mobile)
                Picasso.get().load(profilePhoto).into(my_profile_profile)

            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        database.addValueEventListener(getData)
        database.addListenerForSingleValueEvent(getData)

        my_profile_profile.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, TAKE_IMAGE_CODE)
            }
        }
        my_profile_back_botton.setOnClickListener {
            finish()
        }
        my_profile_update_password.setOnClickListener {
            if (user!=null && user!!.email !=null){
                val credential = EmailAuthProvider.getCredential(user!!.email!!, my_profile_current_password.text.toString())
                user?.reauthenticate(credential)?.addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this, "Auth successful", Toast.LENGTH_SHORT).show()
                        if (my_profile_new_password.text.toString() == my_profile_conform_password.text.toString()){
                            user?.updatePassword(my_profile_new_password.text.toString())?.addOnCompleteListener {task->
                                if(task.isSuccessful){
                                    Toast.makeText(this, "Your password changed Successfully", Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }else{
                        Toast.makeText(this, "Auth Failed", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_IMAGE_CODE){
            when(resultCode){
                RESULT_OK -> bitMap(data)
            }
        }
    }

    private fun bitMap(data: Intent?) {
        val bitmap = data?.getExtras()?.get("data")
        my_profile_profile.setImageBitmap(bitmap as Bitmap?)
        handleUpload(bitmap)
    }
    private fun handleUpload(bitmap: Bitmap?) {
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG,100, baos)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val firebaseStorage = FirebaseStorage.getInstance().reference.child(uid+".jpeg")
        firebaseStorage.putBytes(baos.toByteArray()).addOnSuccessListener {
            getDownloadUrl(firebaseStorage)
        }.addOnFailureListener {
            Toast.makeText(this, "Firebase Storage is not done", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDownloadUrl(firebaseStorage: StorageReference) {
        firebaseStorage.downloadUrl.addOnSuccessListener {

            setUserProfileUrl(it)
        }
    }
    private fun setUserProfileUrl(it: Uri?) {
        val url = it.toString().substring(0,it.toString().indexOf("&token"))
        val user = FirebaseAuth.getInstance().currentUser
        val request = UserProfileChangeRequest.Builder().setPhotoUri(it).build()
        user?.updateProfile(request)?.addOnSuccessListener {
            val idAuth = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val db = FirebaseDatabase.getInstance().reference
            db.child("User Details").child(idAuth).child("Profile").child("profilePhoto").setValue(url).addOnCompleteListener {
                Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show()
            }
        }?.addOnFailureListener {
            Toast.makeText(this, "Profile image failed", Toast.LENGTH_SHORT).show()
        }
    }
}