package vijey.gamedesigner.cd.friendzone.registration

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import vijey.gamedesigner.cd.friendzone.R
import vijey.gamedesigner.cd.friendzone.messages.LatestMessagesActivity
import vijey.gamedesigner.cd.friendzone.models.User
import java.util.*

class RegisterActivity : AppCompatActivity() {
    companion object {
        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        tv_alreadyregister_register.setOnClickListener {
            //kill the activity
            finish()
        }

        btn_register_register.setOnClickListener {
            //call the method
            performeRegister()
        }

        btn_select_photo_register.setOnClickListener {
            //choose an Image
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectPhotoUri: Uri? = null
    //the method which help us to check the selected photo in the phone location
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //proceed and check what the selected image was...
            selectPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            btn_select_photo_register.alpha = 0f

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            btn_select_photo_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    //register function
    private fun performeRegister(){
        val email = edt_email_register.text.toString()
        val password = edt_password_register.text.toString()

        //if mail or password is empty
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Completer l'email et le mot de passe s'il vous plait !", Toast.LENGTH_LONG).show()
            return
        }
        //Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful){
                    return@addOnCompleteListener
                } else {
                    Toast.makeText(this, "Bienvenu ${it.result.user.uid}", Toast.LENGTH_LONG).show()
                    //call the methode
                    uploadImageToFirebaseStorage()
                }
            }.addOnFailureListener{
                Toast.makeText(this,  "Impossible de créer un compte: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    //upload image to Firebase method
    private fun uploadImageToFirebaseStorage(){
        if(selectPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectPhotoUri!!).addOnSuccessListener {
            Toast.makeText(this, "Success uploading image ${it.metadata?.path}", Toast.LENGTH_LONG).show()

            ref.downloadUrl.addOnSuccessListener {
                Toast.makeText(this, "file location: ${it}", Toast.LENGTH_LONG).show()
                saveUserToFirebaseDatabase(it.toString())
            }.addOnFailureListener {
                //do some logging here
            }
        }
    }

    //Save the user to FirebaseDatabase
    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(
            uid,
            edt_username_register.text.toString(),
            profileImageUrl
        )
        ref.setValue(user).addOnSuccessListener {
            Toast.makeText(this, "User saved on FirebaseDatabase", Toast.LENGTH_LONG).show()

            val intent = Intent(this, LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }.addOnFailureListener{
            Toast.makeText(this, "Impossible to add user on firebase: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }
}

