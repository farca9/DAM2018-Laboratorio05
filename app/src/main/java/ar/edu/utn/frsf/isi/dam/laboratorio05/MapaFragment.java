package ar.edu.utn.frsf.isi.dam.laboratorio05;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.MapaListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {
    private GoogleMap miMapa;
    private MapaListener listener;

    public MapaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        int tipoMapa = 0;
        Bundle argumentos = getArguments();
        if (argumentos != null) {
            tipoMapa = argumentos.getInt("tipo_mapa", 0);
        }
        getMapAsync(this);


        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        miMapa = map;
        solicitarPermisos();
    }

    public void setListener(MapaListener mapaListener) {
        this.listener = mapaListener;
    }

    private void solicitarPermisos(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((MainActivity)listener, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                return;
            }
        }

        miMapa.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int permisoUbicacionConcedido;

        switch(requestCode){
            case 0:
                if ((permisoUbicacionConcedido = grantResults[0]) == PackageManager.PERMISSION_GRANTED){
                    solicitarPermisos();
                }
                else{
                    return;
                }
        }
    }
}
