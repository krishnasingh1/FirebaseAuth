package com.example.kpcoder.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.example.kpcoder.R
import com.example.kpcoder.extensions.Extensions.toast
import com.example.kpcoder.utils.FirebaseUtils.firebaseAuth
import com.example.kpcoder.utils.FirebaseUtils.firebaseUser
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.activity_signup.*

class CreateAccountActivity : AppCompatActivity() {

    lateinit var userEmail: String
    lateinit var userPassword: String
    lateinit var createAccountInputsArray: Array<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        createAccountInputsArray = arrayOf(etEmail, etPassword, etConfirmPassword)
        btnCreateAccount.setOnClickListener {
            signIn()
        }

        btnSignIn2.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            toast(" please sigh into your account")
            finish()
        }
    }

    /* check if there's a signed-in user */

    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = firebaseAuth.currentUser
        user?.let {
            startActivity(Intent(this, HomeActivity::class.java))
            toast(" welcome back")
        }
    }

    private fun notEmpty(): Boolean = etEmail.text.toString().trim().isNotEmpty() &&
            etPassword.text.toString().trim().isNotEmpty() &&
            etConfirmPassword.text.toString().trim().isNotEmpty()

    private fun identicalPassword(): Boolean {
        var identical = false
        if (notEmpty() && etPassword.text.toString().trim() == etConfirmPassword.text.toString().trim()
        ) {
            identical = true
        }else if (!notEmpty()) {
            createAccountInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        }else {
            toast("Password sre not matching !")
        }
        return identical
    }

    private fun signIn() {
        if (identicalPassword()) {

            // identicalPassword() return true only when inputs are not empty and passwords are identical
            userEmail = etEmail.text.toString().trim()
            userPassword = etPassword.text.toString().trim()

            /* create a user */
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("created account successfully !")
                        sendEmailVarification()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }else {
                        toast("failed to Authenticate !")
                    }
                }

        }
    }

    /* send verification email to the new user . this will only
     * work if the firebase user is not null.
     */

    private fun sendEmailVarification() {
        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast(" email send to $userEmail")
                }
            }
        }
    }
}