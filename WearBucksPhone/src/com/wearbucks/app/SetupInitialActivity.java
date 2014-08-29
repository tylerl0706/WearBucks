package com.wearbucks.app;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SetupInitialActivity extends FragmentActivity implements RequestEventListener {
	
	public LoginFragment loginFragment; 
	public AddCardFragment addCardFragment;
	public FragmentTransaction ft;
	public String response;
	
	// User's credentials
	public SharedPreferences pref;
	public SharedPreferences.Editor editor;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_initial);
        
        // Set action bar color
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_green)));
        
        loginFragment = new LoginFragment();
        addCardFragment = new AddCardFragment();
        ft = getSupportFragmentManager().beginTransaction();
        
        pref = MainActivity.pref;
        editor = MainActivity.editor;
        
        if (savedInstanceState == null) {
            ft.replace(R.id.frame_location, new WelcomeFragment());
            ft.commit();
        }
	}
	
	public void replaceFragment(View v) {
		
		int viewId = v.getId();
		
		ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_fragment, R.anim.exit_fragment);
        
        if(viewId == R.id.continue_to_login){
        	ft.setCustomAnimations(R.anim.enter_fragment, R.anim.exit_fragment);
            ft.replace(R.id.frame_location, loginFragment);
            ft.commit();
            
        } else if(viewId == R.id.continue_to_add_card){
        	ft.setCustomAnimations(R.anim.enter_fragment, R.anim.exit_fragment);
        	
        	try {
				if(loginFragment.isValid()) {
					Toast.makeText(getApplicationContext(), "Loading...",
							   Toast.LENGTH_LONG).show();
				    new AccountAsyncTask(this, loginFragment.getJsonString()).execute();
				} else {
					showError("Incorrect login", "Please check username and password");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
        } else if(viewId == R.id.continue_to_account_summary){  
        	ft.setCustomAnimations(R.anim.enter_fragment, R.anim.exit_fragment);
        	
        	if(addCardFragment.isValid()) {
                ft.replace(R.id.frame_location, new SummaryFragment());
                ft.commit();
        	} else {
        		showError("Incorrect card", "Please check card number");
        	}
        	
        } else if(viewId == R.id.continue_to_main){
        	saveUserData();
        	
        	Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        	
        } else if(viewId == R.id.back_to_welcome){
        	ft.setCustomAnimations(R.anim.enter_back, R.anim.exit_back);
            ft.replace(R.id.frame_location, new WelcomeFragment());
            ft.commit();
            
        } else if(viewId == R.id.back_to_login){
        	ft.setCustomAnimations(R.anim.enter_back, R.anim.exit_back);
            ft.replace(R.id.frame_location, loginFragment);
            ft.commit();
            
        } else if(viewId == R.id.back_to_addcard){
        	ft.setCustomAnimations(R.anim.enter_back, R.anim.exit_back);
            ft.replace(R.id.frame_location, addCardFragment);
            ft.commit();
        }
	}
	
	public void saveUserData() {
		editor.putString(MainActivity.USERNAME, loginFragment.username);
		editor.putString(MainActivity.PASSWORD, loginFragment.password);
		editor.putString(MainActivity.DEFAULTCARD, addCardFragment.cardNumber);
		editor.putString(MainActivity.LISTOFCARDS, "*" + addCardFragment.cardNumber + ";" + addCardFragment.selectedColor + "*");
		
		editor.commit();
	}
	
	public void showError(String error, String helpText) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.error_message, null);

		alertDialogBuilder.setView(promptsView);

		final TextView details = (TextView) promptsView.findViewById(R.id.error_details);
		final TextView help = (TextView) promptsView.findViewById(R.id.error_help);
		details.setText(error);
		help.setText(helpText);

		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Try Again",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				
			    }
			  });

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	// Disables back button in the setup
	@Override
	public void onBackPressed() {
	}
	
	@Override
	public void onEventCompleted(String val) {
		// TODO Auto-generated method stub
		
		ft.replace(R.id.frame_location, addCardFragment);
		ft.commit();
		
		response = val;
		
	}

	@Override
	public void onEventFailed() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "I <3 Isabella",
				   Toast.LENGTH_LONG).show();
	}
}
