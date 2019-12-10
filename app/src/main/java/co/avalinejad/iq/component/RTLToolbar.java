package co.avalinejad.iq.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.avalinejad.iq.R;

/**
 * Created by alirezaahmadi on 12/26/2015 AD.
 */
public class RTLToolbar extends RelativeLayout implements View.OnClickListener {

    private ImageView iconIV;
    private TextView titleTV;

    public RTLToolbar(Context context) {
        super(context);
        initializeViews(context);
    }

    public RTLToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public RTLToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }


    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.componet_rtl_toolbar, this);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        iconIV = findViewById(R.id.toolbar_rtl_icon);
        iconIV.setOnClickListener(this);
        titleTV = findViewById(R.id.toolbar_rtl_title);
    }


    public void setNavigationIcon(int imageResourceId){
        iconIV.setImageResource(imageResourceId);
    }

    public void setTitle(String title){
        titleTV.setText(title);
    }

    public void setTitle(int stringResourceId){
        titleTV.setText(stringResourceId);
    }

    public void setNavigationOnClickListener(OnClickListener onClickListener){
        iconIV.setOnClickListener(onClickListener);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.toolbar_rtl_icon){
          //  ((DrawerActivity) v.getContext()).openDrawer();
            Toast.makeText(getContext(), "clicked on drawer button", Toast.LENGTH_SHORT).show();
        }
    }
}
