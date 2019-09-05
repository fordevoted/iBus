package e.user.ibus.model;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import e.user.ibus.Presenter.KeyImeChange;

public class CustomEditText extends EditText {
	private KeyImeChange keyImeChangeListener ;

	public CustomEditText(Context context) {
		super(context);
	}
	public CustomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}
	public void setKeyImeChangeListener(KeyImeChange listener){
		keyImeChangeListener = listener;
	}
	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// User has pressed Back key. So hide the keyboard
			if(keyImeChangeListener != null){
				keyImeChangeListener.onKeyIme(keyCode, event);
				return true;
			}
		}
		return false;
	}
}
