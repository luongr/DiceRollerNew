package luongr.diceroller.Multiplayer.fragment.MultiplayerHostFragment.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import luongr.diceroller.Multiplayer.thread.HostServerThread;
import luongr.diceroller.R;

import static android.bluetooth.BluetoothAdapter.EXTRA_SCAN_MODE;
import static android.bluetooth.BluetoothAdapter.SCAN_MODE_NONE;


/**
 * Created by Luong Randy on 1/1/2018.
 */

public class MultiplayerHostFragment extends Fragment {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int extra = intent.getIntExtra(EXTRA_SCAN_MODE,1);
            Log.d("HostFragmentReciever",action);
            Log.d("HostFragmentReciever","" + extra);
            if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)){
                if(BluetoothAdapter.SCAN_MODE_CONNECTABLE == extra || BluetoothAdapter.SCAN_MODE_NONE == extra){
                    progressBar.setVisibility(View.INVISIBLE);
                }
                if(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE == extra){
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @BindView(R.id.progressbar)
    ProgressBar progressBar;

    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnStop)
    Button btnStop;

    //IMultiplayerHostMenu listener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mutliplayer_host,container,false);
        ButterKnife.bind(this,view);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        getContext().registerReceiver(receiver,filter);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(receiver);
    }

    @OnClick(R.id.btnStart)
    public void onStartPressed(){
        btnStart.setVisibility(View.GONE);
        btnStop.setVisibility(View.VISIBLE);
        startDiscovery();
        HostServerThread hostServerThread = new HostServerThread();
        hostServerThread.start();
        Log.d("HostFragment","Thread:" + hostServerThread.isAlive());
    }

    @OnClick(R.id.btnStop)
    public void onStopPressed(){
    }


    private void startDiscovery() {
        Log.d("MultiplayerHost","Start Discovery");
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }
}
