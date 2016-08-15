package net.androtweet.buddy.base;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import net.androtweet.buddy.DeepLink;

public abstract class BaseFragment extends Fragment {
    public boolean isFragmentVisible = false;
    private Context context;
    private int resourceId;
    private View rootView;
    private DeepLink deepLink;

    protected abstract int setResourceID();

    protected abstract void fragmentCreated(Bundle savedInstanceState);

    protected abstract void onFragmentBeingDestroed();

    protected abstract void onResumeFragment();

    protected abstract void initializeScreen();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    public final void init() {
        resourceId = setResourceID();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentCreated(savedInstanceState);
    }

    @Override
    public View getView() {
        return rootView;
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();
        View rootView = inflater.inflate(resourceId, container, false);

        context = rootView.getContext();
        this.rootView = rootView;
        onFragmentCreateView();
        initializeScreen();
        return rootView;
    }

    public void onFragmentCreateView() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onFragmentBeingDestroed();
    }

    @Override
    public void onResume() {
        super.onResume();

        isFragmentVisible = true;
        onResumeFragment();

    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentVisible = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void closeKeyboard(View v) {

        try {
            InputMethodManager imm = (InputMethodManager) this.getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {

        }

    }

    protected void onFragmentBeingClose() {
    }

    public void setDeepLink(DeepLink deepLink) {
        this.deepLink = deepLink;
    }
}
