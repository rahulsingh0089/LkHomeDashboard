package lenkeng.com.welcome.view;

import lenkeng.com.welcome.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LKDialog extends Dialog {
	
	private Context context;
	private Button bt_yes;
	private Button bt_no;
	private TextView mMessage;
	private String text;
	private View.OnClickListener mListener;
	
	public LKDialog(Context con, String message,View.OnClickListener listener,int theme){
		super(con,theme);
		this.text=message;
		this.mListener=listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reset_worn);
		
		bt_yes=(Button) this.findViewById(R.id.yes);
		bt_yes.setOnClickListener(mListener);
		bt_no=(Button) this.findViewById(R.id.no);
		bt_no.setOnClickListener(mListener);
		mMessage=(TextView)findViewById(R.id.Tiele);
		mMessage.setText(text);
	}
}
