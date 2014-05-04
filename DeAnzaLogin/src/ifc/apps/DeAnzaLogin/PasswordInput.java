package ifc.apps.DeAnzaLogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by inaki on 3/22/14.
 */
public class PasswordInput extends Activity {
    String FILENAME = "hello_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(checkFile()){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);

        }

        setContentView(R.layout.firsttime);
        Button button = (Button) findViewById(R.id.button);
        final PasswordInput pass=this;
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                EditText password = (EditText) findViewById(R.id.password);
                EditText username = (EditText) findViewById(R.id.username);
               // EditText host = (EditText) findViewById(R.id.editText);
                String spassword=password.getText().toString();
                String susername=username.getText().toString()+"\n";
               // String shost=host.getText().toString()+"\n";
                if(spassword.isEmpty()||susername.isEmpty()){
                    //tell user he sucks
                }

                FileOutputStream fos = null;
                try {
                    fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);

                } catch (FileNotFoundException e) {
                  //  e.printStackTrace();
                  //  Log.d("de anza", "file not found");
                }
                try {
                 //   fos.write(shost.getBytes());
                    fos.write(susername.getBytes());
                    fos.write(spassword.getBytes());
                } catch (IOException e) {
                   // e.printStackTrace();
                   // Log.d("de anza","failed to write to file");
                }
                try {
                    fos.close();
                } catch (IOException e) {
                   // e.printStackTrace();
                }

               
                if(checkFile()){
                    Intent i = new Intent(pass, MainActivity.class);
                    startActivity(i);

                }
               


            }

         });

    }



   
    public boolean checkFile(){
        File file=new File(getFilesDir(),FILENAME);
        if (file.exists()){
            return true;
        }
        else {
            return false;
        }
    }


}