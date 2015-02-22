package com.example.adan.geodrawerwiki;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


public class galleryListActivity extends ActionBarActivity {

    GridView mGridView = null;
    galleryListAdapter mListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_layout);

        mGridView = (GridView) findViewById(R.id.galleryGridView);
        if (mGridView != null) {
            mListAdapter = new galleryListAdapter(getApplicationContext());
            mGridView.setAdapter(mListAdapter);

            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try
                    {
                        galleryListAdapter.Album album = (galleryListAdapter.Album)mListAdapter.getItem(position);
                        if (album != null)
                        {
                            String folderName = album.folderName;

                            Intent data = new Intent();
                            data.putExtra("folder_name", folderName);
                            setResult(RESULT_OK, data);
                            finish();
                        }
                    }
                    catch (Exception exc)
                    {

                    }

                }
            });
        }
    }


}
