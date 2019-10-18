package com.example.manisha.buzzfeedheadlines;
/*Name : Manisha Sathanur
Id - 801069595
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Articles> articlesArrayList=new ArrayList<Articles>();
    ProgressDialog progressDialog;
    TextView textViewTitle,textView;
    TextView textViewDate;
    TextView textViewDescription;
    TextView textViewPublishedOn,textViewProgress;
    ImageView imageView;
    Button buttonNext,buttonPrevious,buttonQuit;
    ProgressBar progressBar;
    int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewPublishedOn=findViewById(R.id.textViewPublishedOn);
        textViewTitle=findViewById(R.id.textViewHeadline);
         textViewDate=findViewById(R.id.textViewDate);
         textViewDescription=findViewById(R.id.textViewDesc);
         textView=findViewById(R.id.textViewDescription);
         textViewProgress=findViewById(R.id.textViewProgress);
         imageView=findViewById(R.id.imageView);
         progressBar=findViewById(R.id.progressBar);

         buttonNext=findViewById(R.id.buttonNext);
         buttonPrevious=findViewById(R.id.buttonPrevious);
        buttonQuit=findViewById(R.id.button);


         textViewPublishedOn.setVisibility(View.INVISIBLE);
         textViewTitle.setVisibility(View.INVISIBLE);
         textViewDescription.setVisibility(View.INVISIBLE);
         textViewDate.setVisibility(View.INVISIBLE);
         imageView.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
         buttonPrevious.setVisibility(View.INVISIBLE);
         buttonNext.setVisibility(View.INVISIBLE);
         buttonQuit.setVisibility(View.INVISIBLE);


        /*progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading news...");
        progressDialog.show();*/
        new GetAPI(this).execute("https://newsapi.org/v2/top-headlines?sources=buzzfeed&apiKey=4f7cc7902ae244dc8737d5da65b76a03");

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {

                    if (index == (articlesArrayList.size() - 1)) {
                        Toast.makeText(MainActivity.this, "Last feed...Please press previous", Toast.LENGTH_SHORT).show();
                    } else {
                        index++;
                        textViewTitle.setText((articlesArrayList.get(index)).title);
                        String publishedAt = (articlesArrayList.get(index)).publishedAt;
                        textViewDate.setText(publishedAt.substring(0, 10));
                        textViewDescription.setText(articlesArrayList.get(index).description);
                        Picasso.get().load((articlesArrayList.get(index)).urlToImage).into(imageView);
                    }
                }
                else
                    Toast.makeText(MainActivity.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
            }
        });

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    if (index == 0) {
                        Toast.makeText(MainActivity.this, " Please press next", Toast.LENGTH_SHORT).show();
                    } else {
                        index--;
                        textViewTitle.setText((CharSequence) (articlesArrayList.get(index)).title);
                        String publishedAt = (articlesArrayList.get(index)).publishedAt;
                        textViewDate.setText(publishedAt.substring(0, 9));
                        textViewDescription.setText((articlesArrayList.get(index)).description);
                        Picasso.get().load((articlesArrayList.get(index)).urlToImage).into(imageView);
                    }
                }
                else
                    Toast.makeText(MainActivity.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
            }
        });

        buttonQuit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
            }
        });
    }


    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    public void setArticles(ArrayList<Articles> articles) {


        progressBar.setEnabled(false);
        progressBar.setVisibility(View.INVISIBLE);
        textViewProgress.setVisibility(View.INVISIBLE);
        textViewPublishedOn.setVisibility(View.VISIBLE);;
        textViewTitle.setVisibility(View.VISIBLE);
        textViewDescription.setVisibility(View.VISIBLE);
        textViewDate.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        buttonPrevious.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        buttonNext.setVisibility(View.VISIBLE);
        buttonQuit.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        articlesArrayList=articles;
        textViewTitle.setText((articlesArrayList.get(index)).title);

        String publishedAt=(articlesArrayList.get(index)).publishedAt;
        textViewDate.setText(publishedAt.substring(0,9));
        textViewDescription.setText(articlesArrayList.get(index).description);
        Picasso.get().load((articlesArrayList.get(index)).urlToImage).into(imageView);
    }
}









