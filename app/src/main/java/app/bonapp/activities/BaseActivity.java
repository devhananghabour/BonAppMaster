package app.bonapp.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bonapp.R;

import butterknife.ButterKnife;

abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        ButterKnife.bind(this);
    }

    protected void setupToolbar(Toolbar toolbar, String title, Boolean showBackButton) {
        setSupportActionBar(toolbar);
        ImageView ivLogo = toolbar.findViewById(R.id.main_toolbar_ivLogo);
        TextView tvTitle = toolbar.findViewById(R.id.main_toolbar_tvTitle);
        if (ivLogo != null && tvTitle != null) {
            if (title.isEmpty()) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.appDarkGreen));
                tvTitle.setVisibility(View.INVISIBLE);
                ivLogo.setVisibility(View.VISIBLE);
            } else {
                toolbar.setBackgroundColor(getResources().getColor(R.color.white));
                ivLogo.setVisibility(View.INVISIBLE);
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(title);
            }
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_greenback);
        }
    }

    protected void showToastMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    abstract int getLayoutResourceId();

}
