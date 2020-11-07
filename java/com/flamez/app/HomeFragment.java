package com.flamez.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    // ---------------------------------------------------------------------------------------------

    /*RecyclerView homeFeedRecyclerView;
    PostListAdapter postListAdapter;
    private List<PostRow> postRows;
    RecyclerView.LayoutManager layoutManager;

    SwipeRefreshLayout refreshLayout;*/

    // ---------------------------------------------------------------------------------------------

    public HomeFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_home, container, false);

        // ---------------------------------------------------------------------------------------------

        /*homeFeedRecyclerView = (RecyclerView) rootview.findViewById(R.id.home_recyclerView);
        postRows = new ArrayList<>();
        postListAdapter = new PostListAdapter(getContext(), postRows);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        refreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.home_refreshView);*/

        // ---------------------------------------------------------------------------------------------

        /*homeFeedRecyclerView.setAdapter(postListAdapter);
        homeFeedRecyclerView.setLayoutManager(layoutManager);
        homeFeedRecyclerView.setItemAnimator(new DefaultItemAnimator());

        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));*/

        // ---------------------------------------------------------------------------------------------

        // Testing RecyclerVIew

        /*PostRow postRowBuilder = new PostRow();
        postRowBuilder.setPostUserName("Mahid Nahian");
        postRowBuilder.setPostUserDpurl("https://images.pexels.com/photos/324658/pexels-photo-324658.jpeg?auto=compress&cs=tinysrgb&h=350");
        postRowBuilder.setPostImageUrl("http://www.bigapplesoftball.com/wp-content/uploads/sites/107/2017/11/WKNwZaExiYJKkX9N9dLm3EhJ.jpeg");
        postRowBuilder.setPostLikeCount(710432000);
        postRowBuilder.setPostCommentCount(10000);
        postRowBuilder.setPostDescText("Maybe i don't understand this. Girl youre making it hard for me, Girl  WASSUP BIXXX  :D \nyoure making it hard for me, Girl youre making it hard for me. You're changing i can't stand this. MWy hweart can't take this damage :'");
        postRowBuilder.setPostFirstComment("Hey im the first comment dude : BLOOOO YEXX VIXX D");
        postRowBuilder.setPostFirstCommentDpurl("http://cdn7.viralscape.com/wp-content/uploads/2014/03/stunning-portrait-8.jpg");
        postRows.add(postRowBuilder);

        postRowBuilder = new PostRow();
        postRowBuilder.setPostUserName("Mahid Nahian");
        postRowBuilder.setPostUserDpurl("https://images.pexels.com/photos/324658/pexels-photo-324658.jpeg?auto=compress&cs=tinysrgb&h=350");
        postRowBuilder.setPostLikeCount(40000);
        postRowBuilder.setPostCommentCount(20);
        postRowBuilder.setPostDescText("Maybe i don't understand this. Girl youre making it hard for me, Girl  WASSUP BIXXX  :D \nyoure making it hard for me, Girl youre making it hard for me. You're changing i can't stand this. MWy hweart can't take this damage :'");
        postRowBuilder.setPostFirstComment("Hey im the first comment dude : BLOOOO YEXX VIXX D");
        postRowBuilder.setPostFirstCommentDpurl("http://cdn7.viralscape.com/wp-content/uploads/2014/03/stunning-portrait-8.jpg");
        postRows.add(postRowBuilder);

        postListAdapter.notifyDataSetChanged();*/

        // ---------------------------------------------------------------------------------------------



        // ---------------------------------------------------------------------------------------------

        return rootview;

    }

    // ---------------------------------------------------------------------------------------------




    // ---------------------------------------------------------------------------------------------

}