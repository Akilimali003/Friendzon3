package vijey.gamedesigner.cd.friendzone.registration

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import vijey.gamedesigner.cd.friendzone.R
import vijey.gamedesigner.cd.friendzone.messages.LatestMessagesActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tv_nothaveyetanaccount_login.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btn_login_login.setOnClickListener {

            Toast.makeText(this, "Please wait...", Toast.LENGTH_LONG).show()
            //call the method
            performeLogin()
        }

    }

    private fun performeLogin(){
        val email = edt_email_login.text.toString()
        val password = edt_password_login.text.toString()

        //if mail or password is empty
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Completer l'email et le mot de passe s'il vous plait !", Toast.LENGTH_LONG).show()
            return
        }
        //Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful){
                    return@addOnCompleteListener
                } else {
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    startActivity(intent)
                }
                //kill the login activity in the memory
                finish()
            }.addOnFailureListener{
                Toast.makeText(this,  "Impossible de vous logger: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}
