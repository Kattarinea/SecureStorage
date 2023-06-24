package com.example.securestorage2_0;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Storage extends AppCompatActivity {

    static {
        System.loadLibrary("securestorage2_0");
    }


    ListView listView;

    private ArrayAdapter adapter;
    private ArrayList<String> array_list;
    String encryptDir = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Download/encrypt/".toString();
    String decryptDir = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Download/decrypt/".toString();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_storage);
        Toast.makeText(Storage.this, "HEY!", Toast.LENGTH_SHORT).show();

        listView = (ListView) findViewById(R.id.listview);

        array_list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array_list);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Storage.this);
                builder.setTitle("Decrypt?").setMessage("Would ypu like to decrypt this file: " + array_list.get(position).toString() + "?").setIcon(R.drawable.report).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        decryptFile(array_list.get(position).toString());
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyDataSetChanged();
                    }
                });
               // adapter.notifyDataSetChanged();
                builder.create().show();
            }
        });

        if (!new File(encryptDir).exists()) {
            new File(encryptDir).mkdir();
        }
        if (!new File(decryptDir).exists()) {
            new File(decryptDir).mkdir();
        }


        encryptFile();
        //UpdateData();



    }



    void encryptFile()  {
        array_list.clear();
        File encryptDir_ = new File(encryptDir);
        File[] files = encryptDir_.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (!files[i].getName().contains(".secure")) {
                    File newFile = new File(encryptDir + files[i].getName() + ".secure");
                    files[i].renameTo(newFile);
                    Crypt_AES(newFile.getAbsolutePath(), true);
                    array_list.add(files[i].getName() + ".secure");
                }
                else
                    array_list.add(files[i].getName());

            }
        }
       // adapter.notifyDataSetChanged();
    }



    void decryptFile(String name) {

        File file = new File(encryptDir+name);
        array_list.remove(file.getName());
        String newName = name.replace(".secure","");
        File newFile = new File(decryptDir+newName);
        file.renameTo(newFile);
        Crypt_AES(newFile.getAbsolutePath(), false);

    }

    ////////////////////////////////////////////////////////////////////создание MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.updateData:

                UpdateData();

                return true;
            case R.id.changePIN:

                Intent intent = new Intent(Storage.this, MainActivity.class);
                intent.putExtra("menuItem", "PIN");
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
/////////////////////////////////////////////////////////////////////////////////


    void UpdateData()  {
        encryptFile();
        adapter.notifyDataSetChanged();
    }

    /**
     * A native method that is implemented by the 'securestorage2_0' native library,
     * which is packaged with this application.
     *
     * @return
     */

    public native boolean Crypt_AES(String path, boolean flag);


}
