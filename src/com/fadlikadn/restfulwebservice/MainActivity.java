package com.fadlikadn.restfulwebservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Button GetServerData	=	(Button) findViewById(R.id.GetServerData);
		
		GetServerData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//WebSErver Request URL
				String serverURL	=	"http://androidexample.com/media/webservice/JsonReturn.php";
				
				//Use AsyncTask execute Method To Prevent ANR Problem
				new LongOperation().execute(serverURL);
				
			}
		});
	}
	
	//Class with extends AsyncTask class
	private class LongOperation extends AsyncTask<String, Void, Void>{
		
		//Required initialization
		private final HttpClient Client	=	new DefaultHttpClient();
		private String Content;
		private String Error	=	null;
		private ProgressDialog Dialog	=	new ProgressDialog(MainActivity.this);
		String data	=	"";
		TextView uiUpdate	=	(TextView) findViewById(R.id.output);
		TextView jsonParsed	=	(TextView) findViewById(R.id.jsonParsed);
		int sizeData	=	0;
		EditText serverText	=	(EditText) findViewById(R.id.serverText);
		
		@Override
		protected void onPreExecute() {
			//NOTE : Tou can call UI Element here.
			
			//Start Progress Dialog (Message)
			
			Dialog.setMessage("Please wait..");
			Dialog.show();
			
			try{
				//Set Request parameter
				data	+=	"&" + URLEncoder.encode("data", "UTF-8") + "=" + serverText.getText();
			} catch (UnsupportedEncodingException e){
				//catch block
				e.printStackTrace();
			}
			
			//super.onPreExecute();
		}
		
		//call after onPreExecute method
		@Override
		protected Void doInBackground(String... urls) {
			
			//Make Post Call to Web Server
			BufferedReader reader	=	null;
			try
			{
				//Defined URL where to send data
				URL url	=	new URL(urls[0]);
				
				//Send POST data request
				URLConnection conn	=	url.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter wr	=	new OutputStreamWriter(conn.getOutputStream());
				wr.write(data);
				wr.flush();
				
				//Get the server response
				reader	=	new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb	=	new StringBuilder();
				String line	=	null;
				
				//Read Server Response
				while((line = reader.readLine()) != null)
				{
					//Append server response in string
					sb.append(line + " ");
				}
				
				//Append server response to content string
				Content	=	sb.toString();
				
			} catch (Exception ex)
			{
				Error	=	ex.getMessage();
			}
			finally
			{
				try
				{
					reader.close();
				}
				catch(Exception ex)
				{}
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			//NOTE : You can call UI Element here.
			
			//Close progress dialog
			Dialog.dismiss();
			
			if(Error != null)
			{
				uiUpdate.setText("Output : " + Error);
			}
			else
			{
				//Show response JSON on Screen (activity)
				uiUpdate.setText(Content);
				
				//Start Parse Response JSON Data
				String OutputData	=	"";
				JSONObject jsonResponse;
				
				try
				{
					//Creates a new JSON Object with name/values mappings from the JSON string.
					jsonResponse	=	new JSONObject(Content);
					
					//Returns the value mapped by name if it exist and is a JSONArray
					//Returns null otherwise
					JSONArray jsonMainNode	=	jsonResponse.optJSONArray("Android");
					
					//Process each JSON Node
					int lengthJsonArr	=	jsonMainNode.length();
					
					for(int i=0; i < lengthJsonArr; i++)
					{
						//Get Object for each JSON Node
						JSONObject jsonChildNode	=	jsonMainNode.getJSONObject(i);
						
						//Fetch node values
						String name		=	jsonChildNode.optString("name").toString();
						String number	=	jsonChildNode.optString("number").toString();
						String date_added	=	jsonChildNode.optString("date_added").toString();
						
						OutputData	+=	" Name	: " + name + "\n"
									+	" Number	: " + number + "\n"
									+	" Time	: " + date_added + "\n"
									+ "-------------------------------";
					}
					//End parse response JSON data
					
					//Show parsed output on screen (activity)
					jsonParsed.setText(OutputData);
				} catch(JSONException e)
				{
					e.printStackTrace();
				}
			}
			
			//super.onPostExecute(result);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
