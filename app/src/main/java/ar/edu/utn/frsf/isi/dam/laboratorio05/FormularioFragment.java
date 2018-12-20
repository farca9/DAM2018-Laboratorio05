package ar.edu.utn.frsf.isi.dam.laboratorio05;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.Reclamo;


public class FormularioFragment extends Fragment {


    private Spinner spinner;
    private Button btnBuscarPorTipo;
    private ArrayAdapter<Reclamo.TipoReclamo> tipoReclamoAdapter;
    private FormularioListener listener;
    private boolean check=false;

    public FormularioFragment() {

    }

    public interface FormularioListener{
        public void devolverTipo(Reclamo.TipoReclamo tipoReclamo);
    }

    public void setListener(FormularioListener listener){
        this.listener=listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_formulario, container, false);

        spinner=v.findViewById(R.id.spinnerTipoForm);
        btnBuscarPorTipo=v.findViewById(R.id.btnBuscarPorTipo);

        tipoReclamoAdapter = new ArrayAdapter<Reclamo.TipoReclamo>(getActivity(), android.R.layout.simple_spinner_item, Reclamo.TipoReclamo.values());
        tipoReclamoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(tipoReclamoAdapter);

        btnBuscarPorTipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.devolverTipo((Reclamo.TipoReclamo)spinner.getAdapter().getItem(spinner.getSelectedItemPosition()));
            }
        });


        return v;
    }

}
