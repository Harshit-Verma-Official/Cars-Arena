package autocrew.akd.enterntainment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GoogleSignInSystem {

    private static String userName;
    private static String userEmail;
    private static String userId;
    private static FirebaseFirestore firebaseFirestore;

    public static void handleSignInResult(GoogleSignInResult result, FirebaseAuth firebaseAuth, Context context, Dialog loadingDialog) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            MainActivity.currentGoogleAccount = account;
            userName = account.getDisplayName();
            userEmail = account.getEmail();
            // you can store user data to SharedPreference
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            loadingDialog.show();
            firebaseAuthWithGoogle(credential, firebaseAuth, context, loadingDialog);
        } else {
            // Google Sign In failed, update UI appropriately
            Toast.makeText(context, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
        }
    }

    private static void firebaseAuthWithGoogle(AuthCredential credential, final FirebaseAuth firebaseAuth, final Context context, final Dialog loaddingDialog) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseFirestore = FirebaseFirestore.getInstance();
                            firebaseFirestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            ////////// If user is already exists
                                            Toast.makeText(context, "Welcome Back, " + document.get("first_name").toString(), Toast.LENGTH_SHORT).show();
                                            MainActivity.fromGoogle = false;
                                            loaddingDialog.dismiss();
                                            Intent appMainActivity = new Intent(context, AppMainActivity.class);
                                            context.startActivity(appMainActivity);
                                            ((Activity) context).finish();
                                            ////////// If user is already exists
                                        } else {
                                            //////// If user signed up for the first time
                                            Map<String, Object> userData = new HashMap<>();
                                            String[] splittedNameList = userName.split(" ");
                                            if (splittedNameList.length == 2) {
                                                userData.put("first_name", splittedNameList[0]);
                                                userData.put("last_name", splittedNameList[1]);
                                            } else {
                                                userData.put("first_name", splittedNameList[0]);
                                                userData.put("last_name", "");
                                            }
                                            userData.put("email", userEmail);
                                            userData.put("DOB", "dd/mm/yyyy");
                                            userData.put("user_id", firebaseAuth.getCurrentUser().getUid());
                                            userData.put("user_name", (userName.trim().replace(" ", "").toLowerCase() + String.valueOf(gen())));
                                            userData.put("user_profile_pic", "");

                                            firebaseFirestore
                                                    .collection("USERS")
                                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .set(userData)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                MainActivity.fromGoogle = false;
                                                                loaddingDialog.dismiss();
                                                                Intent appMainActivity = new Intent(context, AppMainActivity.class);
                                                                context.startActivity(appMainActivity);
                                                                ((Activity) context).finish();
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                            //////// If user signed up for the first time
                                        }
                                    } else {
                                        loaddingDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            task.getException().printStackTrace();
                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            loaddingDialog.dismiss();
                        }
                    }
                });
    }

    public static int gen() {
        Random r = new Random(System.currentTimeMillis());
        return 10000 + r.nextInt(20000);
    }
}
