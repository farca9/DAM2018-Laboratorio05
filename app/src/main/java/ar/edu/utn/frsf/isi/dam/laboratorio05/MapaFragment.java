package ar.edu.utn.frsf.isi.dam.laboratorio05;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.MyDatabase;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.Reclamo;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {
    private GoogleMap miMapa;
    private OnMapaListener listener;
    private int tipoMapa = 0;
    private List<Reclamo> reclamos;
    private Reclamo reclamoSeleccionado;

    public MapaFragment() { }


    public interface OnMapaListener{
        void coordenadasSeleccionadas(LatLng c);
    }

    public void setListener(OnMapaListener listener){ this.listener = listener; }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        Bundle argumentos = getArguments();

        if (argumentos != null) {
            tipoMapa = argumentos.getInt("tipo_mapa", 0);
        }
        getMapAsync(this);

        if(tipoMapa == 2){
            buscarReclamos();
        }

        if(tipoMapa == 3){
            buscarReclamos();
        }

        if(tipoMapa == 4){
            buscarReclamos();
        }
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        miMapa = map;
        actualizarMapa();

        miMapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //Si el mapa es de tipo 1 se activa la opcion para que tome el click largo
                //Sino no hace nada
                if(tipoMapa == 1){
                    listener.coordenadasSeleccionadas(latLng);
                }
            }
        }
        );

        if(tipoMapa == 2){
            List<LatLng> coordenadas = new ArrayList<LatLng>();
            for(Reclamo r : reclamos){
                miMapa.addMarker(new MarkerOptions()
                .position(new LatLng(r.getLatitud(), r.getLongitud()))
                .title(r.getReclamo())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                );

                coordenadas.add(new LatLng(r.getLatitud(), r.getLongitud()));
            }

            LatLngBounds limite = establecerLimitesMapa(coordenadas);
            miMapa.moveCamera(CameraUpdateFactory.newLatLngBounds(limite, 300));
        }

        if(tipoMapa == 3){


            for(Reclamo r: reclamos){
                if(r.getId()==getArguments().getLong("idReclamo")){
                    reclamoSeleccionado=r;
                }
            }

            List<LatLng> coordenadas = new ArrayList<LatLng>();

            miMapa.addMarker(new MarkerOptions()
                    .position(new LatLng(reclamoSeleccionado.getLatitud(), reclamoSeleccionado.getLongitud()))
                    .title(reclamoSeleccionado.getReclamo()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );

            coordenadas.add(new LatLng(reclamoSeleccionado.getLatitud(), reclamoSeleccionado.getLongitud()));

            miMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(reclamoSeleccionado.getLatitud(), reclamoSeleccionado.getLongitud()),15));

            miMapa.addCircle(new CircleOptions()
            .center(new LatLng(reclamoSeleccionado.getLatitud(),reclamoSeleccionado.getLongitud()))
            .radius(500)
            .strokeColor(Color.RED)
            .fillColor(0x20FF0000)
            .strokeWidth(3));

        }

        if(tipoMapa == 4){
            buscarReclamos();
            List<LatLng> coordenadas = new ArrayList<LatLng>();
            for(Reclamo r : reclamos){
                coordenadas.add(new LatLng(r.getLatitud(), r.getLongitud()));
            }

            LatLngBounds limite = establecerLimitesMapa(coordenadas);
            miMapa.moveCamera(CameraUpdateFactory.newLatLngBounds(limite, 300));

            HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder().data(coordenadas).build();
            TileOverlay mOverlay = miMapa.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }
    }

    private LatLngBounds establecerLimitesMapa(List<LatLng> coordenadas){
        LatLngBounds.Builder limites = new LatLngBounds.Builder();

        for(Reclamo r: reclamos){
            limites.include(new LatLng(r.getLatitud(), r.getLongitud()));
        }

        return limites.build();
    }

    private void actualizarMapa(){
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
                if (grantResults.length > 0 && ((permisoUbicacionConcedido = grantResults[0]) == PackageManager.PERMISSION_GRANTED)){
                    actualizarMapa();
                }
                return;
        }
    }

    public void buscarReclamos(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                MapaFragment.this.reclamos = MyDatabase.getInstance(getActivity()).getReclamoDao().getAll();
            }
        });
        t.start();
    }


}
