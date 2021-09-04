package com.comfunny.androidgoologinsdk

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener




class LoginActivity : AppCompatActivity(), View.OnClickListener {

    // 구글 로그인 연동에 필요한 변수
    lateinit var mGoogleSignInClient : GoogleSignInClient
    var RC_SIGN_IN = 111

    //UI
//    private var loginButton : ImageView? = null
    private var signInButton : SignInButton? = null
    private var signOutButton : Button? = null
    private var nickname : TextView? = null
    private var profilesImage : ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        loginButton = findViewById(R.id.loginButton)
        signOutButton = findViewById(R.id.sign_out_button)
        nickname = findViewById(R.id.nickname)
        profilesImage = findViewById(R.id.profilesImage)

        // Set the dimensions of the sign-in button.
        signInButton = findViewById(R.id.sign_in_button);
        signInButton?.setSize(SignInButton.SIZE_STANDARD)

        updateUI(null)

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //이벤트
        signInButton?.setOnClickListener(this) //클릭
        signOutButton?.setOnClickListener(this) //클릭
    }

    private fun signIn(){
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut(){
        Log.d(TAG, "signout")
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                // ...
                updateUI(null)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account.idToken
            Log.d(TAG, "?? ${account.email}")
            Log.d(TAG, "idToken = ${idToken}")

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?){

        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName: String? = acct.displayName
            val personGivenName: String? = acct.givenName
            val personFamilyName: String? = acct.familyName
            val personEmail: String? = acct.email
            val personId: String? = acct.id
            val personPhoto: Uri? = acct.photoUrl

            Log.d(TAG, "로그인 정보 있음")
            Log.d(TAG,
                "personName = ${personName}" +
                        "\npersonGivenName = ${personGivenName}" +
                        "\npersonFamilyName = ${personFamilyName}" +
                        "\npersonEmail = ${personEmail}" +
                        "\npersonId = ${personId}" +
                        "\npersonPhoto = ${personPhoto}"
            )

            nickname?.text = "${personName}"
            profilesImage?.let { Glide.with(it).load("${personPhoto}").circleCrop().into(it) }

            signInButton?.visibility = View.INVISIBLE;
            signOutButton?.visibility = View.VISIBLE;

        }else{
            Log.d(TAG, "로그인 정보 없음")

            nickname?.text = ""
            profilesImage?.let { null }

            signInButton?.visibility = View.VISIBLE;
            signOutButton?.visibility = View.INVISIBLE;
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.sign_in_button -> signIn()
            R.id.sign_out_button -> signOut()
        }
    }
}

