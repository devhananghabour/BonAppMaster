package app.bonapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.bonapp.utils.AppUtils;
import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    private int containerId;
    protected AppUtils appUtils;

    protected abstract int getResourceLayoutId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getResourceLayoutId(), container, false);
        ButterKnife.bind(this, view);
        if (container != null) {
            containerId = container.getId();
        }
        appUtils = AppUtils.getInstance();
        return view;
    }

    protected void replaceFragmentInActivity(Fragment fragment) {
        if (containerId != 0 && getFragmentManager() != null) {
            getFragmentManager().beginTransaction().replace(containerId, fragment).addToBackStack(fragment.getClass().getName()).commit();
        }
    }

    protected void changeToolbar(){
        if(getActivity() instanceof AppCompatActivity){
            ((AppCompatActivity)getActivity()).getSupportActionBar();
        }
    }
}
