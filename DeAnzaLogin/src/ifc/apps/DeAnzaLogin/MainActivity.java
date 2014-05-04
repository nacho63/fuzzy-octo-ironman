package ifc.apps.DeAnzaLogin;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
//import android.util.Log;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
	private AdView adView;
	private String username;
	private String password;
	private boolean running;
	private int image=0;
	final int CHECK=1;
	final int CROSS=2;
	final int DENIED=3;
	boolean connection=false;
	private String message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		running=false;
		setContentView(R.layout.activity_main2);
		adView = (AdView)this.findViewById(R.id.adView);
		message="";

		final Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					if(!running){
						running=true;
						readFile();
						Integer i=0;
						RetreiveFeedTask task= (RetreiveFeedTask) new RetreiveFeedTask();
						task.execute(i);
					}

				}catch(IllegalStateException ex){}
			}

		});
	}
	public boolean hasInternet(){
		URL url=null;
		String test="";
		try {
			url = new URL("");
			URLConnection conn = url.openConnection();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				test+=line;

			}
		} catch (MalformedURLException e1) {
			connection=false;
			return false;

		} catch (IOException e) {
			connection=false;
			return false;
		}catch(Exception e){connection=false;return false;}

		if (!test.isEmpty()){
			if(test.toLowerCase().contains("connected")){
				connection=true;
				return true;
			}
			else{
				connection=false;
				return false;            		 
			}

		}
		else{
			connection=false;
			return false;

		}

	}

	private void readFile() {
		String FILENAME = "hello_file";
		BufferedReader reader=null;
		try {
			FileInputStream file= openFileInput(FILENAME);
			reader = new BufferedReader(new InputStreamReader(file));
			String line;
			if ((line = reader.readLine()) != null) {

				username=line;
				//  Log.d("de anza","username : "+username);
			}
			if ((line = reader.readLine()) != null) {

				password=line;
				//    Log.d("de anza","password : "+password);
			}

		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			// Log.d("de anza","file cannot be read");
		}
		catch (IOException e){
			// e.printStackTrace();
			// Log.d("de anza",e.toString());

		}finally {
			try {
				if (reader != null)reader.close();
			} catch (IOException ex) {
				//  ex.printStackTrace();
			}
		}




	}

	public void refresh(){
		TextView text=(TextView)findViewById(R.id.textView);
		text.setMovementMethod(new ScrollingMovementMethod());
		text.setText(message);
		text.setVisibility(View.VISIBLE);
		ImageView images=(ImageView)findViewById(R.id.imageView1);
		
		switch(image){
		case CROSS:
			images.setImageResource(R.drawable.cross);
			break;
		case CHECK:
			final AdRequest adRequest = new AdRequest.Builder()
			.addTestDevice("873A336AC2BD86361DE08F6E03B5CD49")
			.build();
			adView.loadAd(adRequest);        	            
			images.setImageResource(R.drawable.check);
			break;

		case DENIED:
			images.setImageResource(R.drawable.dialogerror);
			break;

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onResume() {
		if (adView != null) {
			adView.resume();
		}

		Thread th=new Thread(new Runnable() {
			public void run() {
				hasInternet();
			}
		});//
		th.start();
		try {
			th.join();
			if(connection==true){
				image=CHECK;
				message="Connected";
			}
			else{
				image=CROSS;
				message="Not Connected";
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			image=CROSS;
			message="Not Connected";
			e.printStackTrace();
		}

		refresh();

		super.onResume();



	}

	@Override
	public void onPause() {
		if (adView != null) {
			adView.pause();
		}

		super.onPause();
	}

	/** Called before the activity is destroyed. */
	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}


		super.onDestroy();
	}

	class RetreiveFeedTask extends AsyncTask<Integer,Void,Void>  {
	
		protected Void doInBackground(Integer... i) {

			// In a POST request, we don't pass the values in the URL.
			//Therefore we use only the web page URL as the parameter of the HttpPost argument
			if(!hasInternet()){
				HttpClient httpClient = new DefaultHttpClient();
				String host="http://securelogin.arubanetworks.com/cgi-bin/login";
				HttpPost httpPost = new HttpPost(host);

				// Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
				//uniquely separate by the other end.
				//To achieve that we use BasicNameValuePair
				//Things we need to pass with the POST request
				BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair("user", username);
				BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair("password", password);

				// We add the content that we want to pass with the POST request to as name-value pairs
				//Now we put those sending details to an ArrayList with type safe of NameValuePair
				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(usernameBasicNameValuePair);
				nameValuePairList.add(passwordBasicNameValuePAir);

				try {
					// UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs.
					//This is typically useful while sending an HTTP POST request.
					UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

					// setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
					httpPost.setEntity(urlEncodedFormEntity);

					try {
						// HttpResponse is an interface just like HttpPost.
						//Therefore we can't initialize them
						HttpResponse httpResponse = httpClient.execute(httpPost);

						// According to the JAVA API, InputStream constructor do nothing.
						//So we can't initialize InputStream although it is not an interface
						InputStream inputStream = httpResponse.getEntity().getContent();

						InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

						BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

						StringBuilder stringBuilder = new StringBuilder();

						String bufferedStrChunk = null;

						while((bufferedStrChunk = bufferedReader.readLine()) != null){
							stringBuilder.append(bufferedStrChunk);
						}
						String response=stringBuilder.toString();

						if(response.contains("User Authenticated")){
							message="Connected";
							image=CHECK;
							return null;
						}
						else{


							if(response.toLowerCase().contains("user")){
								message="Wrong password \n(or username)";     
								image=DENIED;
							}
							else{
								image=CROSS;
								message="Unknwon error(1)";
							}
						}
						return null;

					} catch (ClientProtocolException cpe) {
						message="Not Connected, check WiFi is on.";
						image=CROSS;

					} catch (IOException ioe) {
						message="Not Connected, check WiFi is on.";
						image=CROSS;
					}
					catch(Exception e){image=CROSS;

					message="Unknwon error(2)";
					}

				} catch (UnsupportedEncodingException uee) {
					image=CROSS;
					message="The app needs an update";
				}
				catch (Exception e) {
					image=CROSS;
					message="Unknwon error(3)";
				}

			}//end of if(!Connected)
			else{
				message="Connected";
				image=CHECK;
			}
			return null;

		}
		@Override
		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			refresh();
			running=false;
		}
	}//end of RetriveFeedTask class

}//end of MainActivity Class
