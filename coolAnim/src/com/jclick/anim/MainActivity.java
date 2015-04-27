package com.jclick.anim;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
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
