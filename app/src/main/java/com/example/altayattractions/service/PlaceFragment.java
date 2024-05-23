package com.example.altayattractions.service;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.altayattractions.R;
import com.example.altayattractions.domain.Place;
import com.example.altayattractions.domain.Places;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PlaceFragment extends Fragment {
    final private SimpleLocation location;
    private ListView listView;

    public PlaceFragment(SimpleLocation location) {
        this.location = location;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attractions_list, container, false);
        listView = view.findViewById(R.id.listViewPlaces);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Place[] places = Places.getPlaces();
        PlacesAdapter adapter = new PlacesAdapter(requireContext(), places, this.location);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place place = (Place) parent.getItemAtPosition(position);
                showBottomSheetDialog(place);
            }
        });
    }

    private void showBottomSheetDialog(Place place) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(R.layout.dialog_fragment);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView item_distance = dialog.findViewById(R.id.item_distance);
        TextView item_name = dialog.findViewById(R.id.item_name);
        TextView item_address = dialog.findViewById(R.id.item_address);
        TextView item_info = dialog.findViewById(R.id.item_info);
        ImageView imageView = dialog.findViewById(R.id.item_image);

        item_distance.setText(String.format("%.1f км", place.getDistance()));
        item_name.setText(place.getName());
        item_address.setText(place.getAddress());
        item_info.setText(place.getInformations());

        FirebaseApp.initializeApp(requireContext());
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://alaty-map.appspot.com");
        StorageReference reference = firebaseStorage.getReference(place.getPathToImage());
        Glide.with(requireContext()).load(reference).into(imageView);
    }
}
