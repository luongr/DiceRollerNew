package luongr.diceroller.Multiplayer.fragment.MultiplayerHostRollFragment.view;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import luongr.diceroller.Adapters.Selection.SelectionAdapter;
import luongr.diceroller.Multiplayer.fragment.MultiplayerHostRollFragment.model.MultiplayerHostRollFragmentInteractor;
import luongr.diceroller.Multiplayer.fragment.MultiplayerHostRollFragment.presenter.IMultiplayerHostRollFragmentPresenter;
import luongr.diceroller.Multiplayer.fragment.MultiplayerHostRollFragment.presenter.MultiplayerHostRollFragmentPresenter;
import luongr.diceroller.Multiplayer.service.MultiplayerBluetoothService;
import luongr.diceroller.R;

/**
 * Created by Luong Randy on 1/16/2018.
 */

public class MultiplayerHostRollFragment extends Fragment {

    @BindView(R.id.rvUserSelections)
    RecyclerView rvSelection;
    @BindView(R.id.edtSelection)
    EditText edtSelection;
    @BindView(R.id.btnAddSelection)
    Button btnAddSelection;
    @BindView(R.id.txtUserSelectionHeader)
    TextView txtUserSelectionHeader;

    MultiplayerBluetoothService mpBluetoothService;
    BluetoothSocket socket = null;
    IMultiplayerHostRollFragmentPresenter presenter;
    SelectionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multiplayer_join_roll,container,false);
        ButterKnife.bind(this,view);
        if(socket == null){
            Log.d("MultiplayerJoinRoll", "Null Socket");
        }
        mpBluetoothService = new MultiplayerBluetoothService(socket,mHandler);
        presenter = new MultiplayerHostRollFragmentPresenter(this,new MultiplayerHostRollFragmentInteractor(getContext()));
        presenter.checkMaxSelections();
        setUpRV();
        txtUserSelectionHeader.setText(presenter.getDiceInfoHeader());
        //sends dice information to all connected devices and releases them from the loading screen.
        Log.d("MultiplayerJoinRoll", "Send Dice Info");
        mpBluetoothService.write(presenter.diceInfo());
        return view;
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case MultiplayerBluetoothService.MessageConstants.MESSAGE_WRITE:
                    break;
                case MultiplayerBluetoothService.MessageConstants.MESSAGE_READ:
                    //when handler reads the 0 which is defined by your constant read the object buffer
                    byte[] readBuffer = (byte[]) msg.obj;
                    //now we can convert the byte to string
                    //set up readMessage like this so we get the appropriate size for string via arg1
                    String readMessage = new String(readBuffer, 0, msg.arg1);
                    presenter.parseMessageList(readMessage);
                    //Log.d("HostReadMessage",readMessage);
                    //Log.d("HostReadMessage",String.valueOf(msg.arg1));
                    //Log.d("HostReadMessage",String.valueOf(msg.arg2));
                    break;
            }
        }
    };

    @OnClick(R.id.btnAddSelection)
    public void addSelection(){
        presenter.addSelection(edtSelection.getText().toString());
        synchronized(adapter){
            adapter.notifyDataSetChanged();
        }
        adapter.notifyDataSetChanged();
        presenter.checkMaxSelections();
    }

    private void setUpRV() {
         adapter = new SelectionAdapter(getContext(), presenter.getSelectionList(), new SelectionAdapter.Callback() {
            @Override
            public void onRemoved() {
                //might need to run a check to see the number of available selections
                presenter.checkMaxSelections();

            }
        });
        rvSelection.setAdapter(adapter);
        rvSelection.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @OnClick(R.id.btnConfirmSelections)
    public void onConfirm(){
        //TODO: confirm dialog to new roll screen.
    }

    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }

    public void hideAddSelection() {
        btnAddSelection.setVisibility(View.INVISIBLE);
        edtSelection.setVisibility(View.INVISIBLE);
    }

    public void showAddSelection() {
        btnAddSelection.setVisibility(View.VISIBLE);
        edtSelection.setVisibility(View.VISIBLE);
    }
}
