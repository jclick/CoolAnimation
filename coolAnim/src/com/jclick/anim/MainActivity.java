package com.jclick.anim;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	private static final String BTN_PAUSE_TEXT = "pause";
	
	private CoolProgress progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		progress = (CoolProgress) findViewById(R.id.progress);
		findViewById(R.id.btn_reset).setOnClickListener(this);
		findViewById(R.id.btn_start).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_reset:
			progress.reset();
			break;
		case R.id.btn_start:
			Button btn = (Button) v;
			if(btn.getText().equals(BTN_PAUSE_TEXT)){
				progress.stopPlay();
				btn.setText("start play");
			}else{
				progress.startPlay();
				btn.setText(BTN_PAUSE_TEXT);
			}
			break;

		default:
			break;
		}
	}
}
