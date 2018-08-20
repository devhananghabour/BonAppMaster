package app.bonapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.app.bonapp.R;

import java.util.List;

import app.bonapp.models.user.AddressModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<AddressModel> addresses;

    private OnAddressSelectedListener listener;
    private boolean showSelectedAddressCheckBox;
    private CompoundButton previousSelectedAddressButton;

    public interface OnAddressSelectedListener {
        void onAddressSelected(AddressModel address);
    }

    public AddressAdapter(List<AddressModel> addresses, boolean showSelectedAddressCheckBox) {
        this.addresses = addresses;
        this.showSelectedAddressCheckBox = showSelectedAddressCheckBox;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_address, parent, false);
        return new AddressViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        holder.bind(addresses.get(position));
    }

    @Override
    public int getItemCount() {
        return addresses == null ? 0 : addresses.size();
    }

    public void removeAt(int position) {
        addresses.remove(position);
        notifyItemRemoved(position);
    }

    public void setOnAddressSelectedListener(OnAddressSelectedListener listener) {
        this.listener = listener;
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_address_tvName)
        TextView tvName;
        @BindView(R.id.row_address_tvAddress)
        TextView tvAddress;
        @BindView(R.id.row_address_tvCity)
        TextView tvCity;
        @BindView(R.id.row_address_tvArea)
        TextView tvArea;
        @BindView(R.id.row_address_tvPhone)
        TextView tvPhone;
        @BindView(R.id.row_address_tvNotes)
        TextView tvNotes;
        @BindView(R.id.row_address_cbSelectedAddress)
        CheckBox cbSelectedAddress;

        AddressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(AddressModel addressModel) {
            tvName.setText(addressModel.getName());
            tvAddress.setText(addressModel.getAddress());
            tvCity.setText(addressModel.getCityName());
            tvArea.setText(addressModel.getAreaName());
            tvPhone.setText(addressModel.getPhone());
            tvNotes.setText(addressModel.getNotes());
            if (showSelectedAddressCheckBox)
                cbSelectedAddress.setVisibility(View.VISIBLE);

        }

        @OnCheckedChanged(R.id.row_address_cbSelectedAddress)
        void onAddressChecked(CompoundButton button, boolean checked) {

            if (previousSelectedAddressButton != null) {
                previousSelectedAddressButton.setChecked(false);
            }

            if (listener != null) {
                if (checked) {
                    listener.onAddressSelected(addresses.get(getAdapterPosition()));
                } else {
                    listener.onAddressSelected(null);
                    previousSelectedAddressButton = null;
                }
            }
            previousSelectedAddressButton = button;
        }

        @OnClick(R.id.row_address_cardView)
        public void itemSelected() {
            cbSelectedAddress.setChecked(!cbSelectedAddress.isChecked());
//            if (listener != null) {
//                listener.onAddressSelected(addresses.get(getAdapterPosition()));
//            }
        }

    }
}
