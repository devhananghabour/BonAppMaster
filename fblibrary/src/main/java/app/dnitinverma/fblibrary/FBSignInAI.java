package app.dnitinverma.fblibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import app.dnitinverma.fblibrary.interfaces.FBSignCallback;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by appinventiv on 7/9/17.
 */

public class FBSignInAI {
    private static FBSignInAI mFBSignInAI;
    private Activity mActivity;
    private CallbackManager callbackManager;
    private FBSignCallback fbSignCallback;


    /*
    * @param - activity
    * @param - fbSignCallback interface to return callback
    *
    */
    public static FBSignInAI getInstance(Activity activity, FBSignCallback fbSignCallback) {
        //if (mFBSignInAI == null) {
            mFBSignInAI = new FBSignInAI(activity,fbSignCallback);
            FacebookSdk.sdkInitialize(activity);
       // }
        return mFBSignInAI;
    }

    /*
    * Initialize global variable
    */
    public FBSignInAI(Activity activity, FBSignCallback fbSignCallback) {
        this.mActivity = activity;
        this.fbSignCallback = fbSignCallback;
    }

    /*
    *  Sign In Method
    */
    public void doSignIn() {
        callbackManager = CallbackManager.Factory.create();
        LoginViaFacebook();
        LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "user_location", "email", "user_birthday"));
    }

    /*
   *  Sign out Method
   */
    public void doSignOut() {
        LoginManager.getInstance().logOut();
        fbSignCallback.fbSignOutSuccessResult();
    }

    /*
     * To get user profile information from facebook
     */
    public void LoginViaFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject jsonObject,
                                    GraphResponse response) {
                                if (jsonObject != null) {
                                    try {
                                        fbSignCallback.fbSignInSuccessResult(jsonObject);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, picture, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                fbSignCallback.fbSignInCancel();
            }

            @Override
            public void onError(FacebookException exception) {
                fbSignCallback.fbSignInFailure(exception);
            }
        });
    }

    /*
    *  return callback to facebook using callbackmanager
    */
    public void setActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
