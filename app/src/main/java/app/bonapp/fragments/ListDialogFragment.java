package app.bonapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;

public class ListDialogFragment extends DialogFragment {

    public static final String LIST_DATA = "list_data";
    public static final String TITLE = "title";

    private OnItemSelectedListener listener;

    public interface DialogList extends Parcelable {
        String getTitle();

        Object getObject();
    }

    public interface OnItemSelectedListener {
        void onItemSelected(Object object);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public static ListDialogFragment newInstance(ArrayList<DialogList> data, String title) {
        final ListDialogFragment listDialogFragment = new ListDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST_DATA, data);
        args.putString(TITLE, title);
        listDialogFragment.setArguments(args);
        return listDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getTitle())
                .setCancelable(false)
                .setItems(getItemsList(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (listener != null) {
                            listener.onItemSelected(getData().get(item).getObject());
                        }
                        getDialog().dismiss();

                    }
                }).create();
    }

    private CharSequence[] getItemsList() {
        ArrayList<String> items = new ArrayList<>();
        for (DialogList dialogList : getData()) {
            items.add(dialogList.getTitle());
        }
        return items.toArray(new CharSequence[items.size()]);
    }

    private ArrayList<DialogList> getData() {
        return getArguments().getParcelableArrayList(LIST_DATA);
    }

    private String getTitle() {
        return getArguments().getString(TITLE, "");
    }

}
