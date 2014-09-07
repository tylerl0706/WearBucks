package com.wearbucks.app;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class BarcodeAsyncTask extends AsyncTask<Void, Void, Void>{

	private String barcode;
	private Bitmap barcodeImage;
	private Context main;
	private Object notiMan;
	
	public BarcodeAsyncTask(String b, Context context, Object n) {
		// TODO Auto-generated constructor stub
		barcode = b;
		main = context;
		notiMan = n;
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		InputStream inputStream = null;
		Bitmap result = null;
		
		String url = "http://www.voindo.eu/UltimateBarcodeGenerator/barcode/barcode.processor.php?encode=QRCODE&qrdata_type=text&qr_btext_text=" + barcode + "&qr_link_link=&qr_sms_phone=&qr_sms_msg=&qr_phone_phone=&qr_vc_N=&qr_vc_C=&qr_vc_J=&qr_vc_W=&qr_vc_H=&qr_vc_AA=&qr_vc_ACI=&qr_vc_AP=&qr_vc_ACO=&qr_vc_E=&qr_vc_U=&qr_mec_N=&qr_mec_P=&qr_mec_E=&qr_mec_U=&qr_email_add=&qr_email_sub=&qr_email_msg=&qr_wifi_ssid=&qr_wifi_type=wep&qr_wifi_pass=&qr_geo_lat=&qr_geo_lon=&bdata_matrix=123&bdata_pdf=123&bdata=123&height=500&scale=1&bgcolor=%23ffffff&color=%23000000&file=&folder=";
		
		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if(inputStream != null)
				result = BitmapFactory.decodeStream(inputStream);
			else
				result = null;

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		
		barcodeImage = result;
		
		return null;
	}
	
	@Override
    protected void onPostExecute(Void aVoid) {
		sendNotification(barcode, barcodeImage);
	}
	

	public void sendNotification(String barcodeNumber, Bitmap barcodeImage){
    	//create intents
    	final Intent emptyIntent = new Intent();
    	PendingIntent pendingIntent = PendingIntent.getActivity(main, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    			
    	//create big notification action
    	//Intent dismissIntent = new Intent(this, );
    	
    	
    	//expanded barcode image notification
    	NotificationCompat.BigPictureStyle notiStyle = new 
    	        NotificationCompat.BigPictureStyle();
    	
    	notiStyle.setBigContentTitle("WearBucks");
    	notiStyle.setSummaryText("Barcode for card ending in " + ("" + barcodeNumber).substring(("" + barcodeNumber).length()-4));
    	notiStyle.bigPicture( barcodeImage );
    	
    	NotificationManager notiManager = (NotificationManager) notiMan;
    	notiManager.cancel(101);
    	
    	//////
    	PendingIntent dismissPendingIntent = PendingIntent.getActivity(main, 0, new Intent(main,
				MainActivity.class), 0);
    	
    	//compact notification showing stats
    	NotificationCompat.Builder mBuilder =
    		    new NotificationCompat.Builder(main)
    		    .setSmallIcon(R.drawable.wearbucks_logo)
    		    .setContentTitle("WearBucks")
    		    .setContentText("card (" + ("" + barcodeNumber).substring(("" + barcodeNumber).length()-4) + ")")
    		    .setContentIntent(pendingIntent)
    		    .setStyle(notiStyle)
    		    //.setOngoing(true)
    		    .addAction(R.drawable.wearbucks_logo, "dismiss", dismissPendingIntent)
    		    ;
    	
    	//build and send notification
    	notiManager.notify(101, mBuilder.build());
    }


	
}