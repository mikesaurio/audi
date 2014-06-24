package iimas.labplc.audi;

import iimas.labplc.audi.services.Servicio_geolocalizacion;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Audi_activity_main extends Activity {

	//Declarando Variables
	TextView coordenadas; //Mostrara las coordenadas
	Button iniciarService; //Boton que iniciara el servicio de localizacion
	Button detenerService; //Boton que detendra el servicio de localizacion
	private LocationManager mLocationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audi_activity_main);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		coordenadas = (TextView) findViewById(R.id.Coordenadas);
		iniciarService = (Button) findViewById(R.id.Iniciar);
		iniciarService.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
					if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					showDialogGPS("GPS apagado", "Deseas activarlo?");		
				}else{
					
					 
					  //Se inicia el servicio de geolocalizacion
					 
					//Geolocalizarme.mainActivity = MainActivity.this;
					startService(new Intent(Audi_activity_main.this,Servicio_geolocalizacion.class));
					bloquearBoton(true);
				}
				
				
			}
		});
		detenerService = (Button) findViewById(R.id.Detener);
		detenerService.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				/**
				 * Se detiene el servicio de geolocalizacion
				 */
				bloquearBoton(false);
				stopService(new Intent(Audi_activity_main.this,Servicio_geolocalizacion.class));
				coordenadas.setText(getString(R.string.esperando));//regresamos a texto default

			}
		});

	}
	

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
	 * manejo de transmiciones
	 */
	private BroadcastReceiver onBroadcast = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctxt, Intent i) {
			
				//blanqueamos el texto de las coordenadas si esta el texto default
				if (coordenadas.getText().equals(getString(R.string.esperando))) {
					coordenadas.setText("");
				}
				
				String datos = i.getStringExtra("coordenadas");//obtenemos las coordenadas envidas del servicioGeolocalizaci—n
				String[] tokens = datos.split(";");//separamos por tocken
				coordenadas.append("latitud: " + tokens[0]+ " longitud: " + tokens[1]);
				coordenadas.append("\n");//agregamos salto de linea
		}
	};

	
	//Metodo que bloquea los botones
	public void bloquearBoton(boolean b){
		if(b==true){
			iniciarService.setEnabled(false);
			detenerService.setEnabled(true);
		}else{
			iniciarService.setEnabled(true);
			detenerService.setEnabled(false);
		}
		
	}
	/**
	 * Muestra el dialogo en caso de que el GPS este apagado
	 * 
	 * @param titulo Titulo del dialogo
	 * @param message Mensaje del dialogo
	 */
	public void showDialogGPS(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(Audi_activity_main.this);
        builder.setTitle(title);
        builder.setMessage(message);
		builder.setPositiveButton("S’", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				startActivity(settingsIntent);
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
	}
}
