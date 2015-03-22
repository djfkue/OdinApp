package com.argonmobile.odinapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.argonmobile.odinapp.cardsui.objects.CardStack;
import com.argonmobile.odinapp.cardsui.views.CardUI;
import com.argonmobile.odinapp.dummy.MyImageCard;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecentProfileFragment extends Fragment {


    private CardUI mCardView;

    public RecentProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recent_profile, container, false);
        // init CardView
        mCardView = (CardUI) rootView.findViewById(R.id.cardsview);
        mCardView.setSwipeable(true);

        CardStack stack2 = new CardStack();
        stack2.setTitle("REGULAR CARDS");
        mCardView.addStack(stack2);

        mCardView.addCardToLastStack(new MyImageCard("Get the CardsUI view", R.drawable.profile_1));

        return rootView;
    }


}
