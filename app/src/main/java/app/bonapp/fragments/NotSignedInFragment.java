package app.bonapp.fragments;

import android.content.Intent;

import com.app.bonapp.R;

import app.bonapp.activities.RegisterActivity;
import butterknife.OnClick;

public class NotSignedInFragment extends BaseFragment {

    @Override
    protected int getResourceLayoutId() {
        return R.layout.fragment_not_signed_in;
    }

    @OnClick(R.id.fragment_not_signed_in_tv_sign_in)
    public void signIn() {
        startActivity(new Intent(getActivity(), RegisterActivity.class));
    }
}
