package com.manasa.olympiadedgeai.auth;

import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult;
import com.amazonaws.regions.Regions;

public class CognitoAuthManager {

    // These should be loaded from your local.properties or BuildConfig
    private static final String USER_POOL_ID = "ap-south-1_uAl3BFVd0";
    private static final String CLIENT_ID = "7vpjgiinpbe97ui44itl76ibir";
    private static final Regions REGION = Regions.AP_SOUTH_1;

    private final CognitoUserPool userPool;
    private String userPassword; // Temporary storage during auth flow

    public CognitoAuthManager(Context context) {
        userPool = new CognitoUserPool(context, USER_POOL_ID, CLIENT_ID, null, REGION);
    }

    public void signUp(String username, String password, String email, AuthCallback callback) {
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        userAttributes.addAttribute("email", email);

        userPool.signUpInBackground(username, password, userAttributes, null, new SignUpHandler() {
            @Override
            public void onSuccess(CognitoUser user, SignUpResult signUpResult) {
                callback.onSuccess("Sign up successful! Please check your email for the verification code.");
            }

            @Override
            public void onFailure(Exception exception) {
                callback.onError("Sign up failed: " + exception.getMessage());
            }
        });
    }

    public void confirmSignUp(String username, String code, AuthCallback callback) {
        userPool.getUser(username).confirmSignUpInBackground(code, false, new GenericHandler() {
            @Override
            public void onSuccess() {
                callback.onSuccess("Account verified! You can now log in.");
            }

            @Override
            public void onFailure(Exception exception) {
                callback.onError("Verification failed: " + exception.getMessage());
            }
        });
    }

    public void login(String username, String password, AuthCallback callback) {
        this.userPassword = password;
        CognitoUser user = userPool.getUser(username);
        user.getSessionInBackground(new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                callback.onSuccess("Login successful!");
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                AuthenticationDetails authDetails = new AuthenticationDetails(userId, userPassword, null);
                authenticationContinuation.setAuthenticationDetails(authDetails);
                authenticationContinuation.continueTask();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {}

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {}

            @Override
            public void onFailure(Exception exception) {
                callback.onError("Login failed: " + exception.getMessage());
            }
        });
    }

    public boolean isUserLoggedIn() {
        try {
            return userPool.getCurrentUser() != null && userPool.getCurrentUser().getUserId() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void logout() {
        if (userPool.getCurrentUser() != null) {
            userPool.getCurrentUser().signOut();
        }
    }

    public interface AuthCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}
