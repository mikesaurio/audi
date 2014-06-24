package iimas.labplc.audi;

import iimas.labplc.audi.services.Servicio_geolocalizacion;
import iimas.labplc.mx.utils.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Audi_activity_main extends Activity {


	/**
	 * Declaraci—n de variables
	 */
	TextView tvCoordenadas;//se mostrar‡n las coordenadas y la distancia acumulada
	Button btnRunService ;
	Button btnStopService;
	private LocationManager mLocationManager;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audi_activity_main);
		if (!Util.isNetworkConnectionOk(Audi_activity_main.this)) {
			showDialogGPS("falla","No tienes internet"); 		
		}else{
			init();
		}

	
		
	}

	public void init(){
		/**
		 * instancias y escuchas
		 */
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		tvCoordenadas = (TextView) findViewById(R.id.audi_main_tv_coordenadas);
		btnRunService = (Button) findViewById(R.id.audi_main_btn_Iniciar);
		btnRunService.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
					Servicio_geolocalizacion.taxiActivity = Audi_activity_main.this;
					startService(new Intent(Audi_activity_main.this,Servicio_geolocalizacion.class));
					bloquearBoton(true);

			}
		});
		btnStopService = (Button) findViewById(R.id.audi_main_btn_Detener);
		btnStopService.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				/**
				 * Se detiene el servicio de geolocalizaci—n
				 */
				bloquearBoton(false);
				stopService(new Intent(Audi_activity_main.this,Servicio_geolocalizacion.class));
				tvCoordenadas.setText(getString(R.string.esperando));//regresamos a texto default

			}
		});
	}
	/**
	 * bloquea el boton que inicia el servicio
	 * @param b (boolean)
	 */
	public void bloquearBoton(boolean b){
		if(b==true){
			btnRunService.setEnabled(false);
			btnStopService.setEnabled(true);
		}else{
			btnRunService.setEnabled(true);
			btnStopService.setEnabled(false);
		}

	}

	/**
	 * manejo de transmiciones
	 */
	private BroadcastReceiver onBroadcast = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctxt, Intent i) {

				//blanqueamos el texto de las coordenadas si esta el texto default
				if (tvCoordenadas.getText().equals(getString(R.string.esperando))) {
					tvCoordenadas.setText("");
				}

				String datos = i.getStringExtra("coordenadas");//obtenemos las coordenadas envidas del servicioGeolocalizaci—n
			if(datos!=null){
				String[] tokens = datos.split(";");//separamos por tocken
				tvCoordenadas.append("latitud: " + tokens[0]+ " longitud: " + tokens[1]);
				tvCoordenadas.append("\n");//agregamos salto de linea
			}else{
				Log.d("***************"	,"null");
			}

				//generamos la conexi—n con el servidor y mandamos las coordenads
				
		}
	};

	@Override
	protected void onPause() {
		unregisterReceiver(onBroadcast);
		super.onPause();
	}

	@Override
	protected void onResume() {
		registerReceiver(onBroadcast, new IntentFilter("key"));
		super.onResume();
	}



	/**
	 * Muestra di‡logo en dado caso que el GPS estŽ apagado
	 * 
	 * @param titulo T’tulo del di‡logo
	 * @param message Mensaje del di‡logo
	 */
	public void showDialogGPS(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(Audi_activity_main.this);
        builder.setTitle(title);
        builder.setMessage(message);
		builder.setPositiveButton("S’", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				finish();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
	}


}