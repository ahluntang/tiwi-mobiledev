package be.ugent.oomo.labo_2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import be.ugent.oomo.labo_2.utilities.ChatServerUtilities;
import be.ugent.oomo.labo_2.utilities.CommonUtilities;
import be.ugent.oomo.labo_2.utilities.GcmRegistrar;

public class LoginDialogFragment extends DialogFragment {
	
	private static final String TAG = LoginDialogFragment.class.getName();

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;
	
	private EditText mUsernameView;
	private String mUsername;
	private TextView mLoginStatusMessageView;
	private View mLoginFormView;
	private View mLoginStatusView;

	private Button mLoginButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, getTheme());
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
        final View customDialog = inflater.inflate(R.layout.login_dialog, container, false);
        mLoginFormView = customDialog.findViewById(R.id.login_form);
		mLoginStatusView = customDialog.findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) customDialog.findViewById(R.id.login_status_message);
        mUsernameView = ((EditText)customDialog.findViewById(R.id.username));
        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL || id == EditorInfo.IME_ACTION_DONE) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});
        mLoginButton = ((Button) customDialog.findViewById(R.id.btn_login));
        mLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptLogin();
			}
		});
        return customDialog;
    }

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUsernameView.setError(null);

		// Store values at the time of the login attempt.
		mUsername = mUsernameView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid username.
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		} else if (mUsername.length() < 5) {
			mUsernameView.setError(getString(R.string.error_invalid_username));
			focusView = mUsernameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mUsernameView.getWindowToken(), 0);
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute();
		}
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationStart(Animator animation) {
							mLoginStatusView.setVisibility(View.VISIBLE);
						}
						
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationStart(Animator animation) {
							mLoginFormView.setVisibility(View.VISIBLE);
						}
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	@Override
	public void onDestroy() {
    	if (mAuthTask != null) {
    		mAuthTask.cancel(true);
    	}
        super.onDestroy();
    }
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		
		private final Context context = getActivity().getApplicationContext();
		private Exception loginError = null;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				if (!GcmRegistrar.isRegistered(context)) {
					GcmRegistrar.register(context);
				}
				if (!ChatServerUtilities.isRegisteredOnServer(context)) {
					ChatServerUtilities.registerOnServer(context
							, GcmRegistrar.getRegistrationId(context)
							, mUsername);
				} else { // Device is already registered on GCM, check server.
					Log.i(TAG, getString(R.string.already_registered));
				}
				//CommonUtilities.username = mUsername;
			} catch (Exception ex) {
				loginError = ex;
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				dismiss();
			} else if (loginError != null) {
				Toast.makeText(context, loginError.getMessage(), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, "Unknown login error", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
