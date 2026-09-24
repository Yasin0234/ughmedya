package com.korkutsoftware.ughmedya.ughhaber.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.korkutsoftware.ughmedya.R;
import android.widget.ImageView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.bumptech.glide.Glide;

public class EditFragment extends Fragment {

    private TextInputEditText titleEt, contentEt, locationEt, sourceEt;
    private TextView previewContentTv, sidebarLocationTv, sidebarSourceTv;
    private ImageView previewImageBg;
    private MaterialButtonToggleGroup aspectRatioToggle;
    private MaterialCardView previewCard;
    private androidx.constraintlayout.widget.ConstraintLayout shareLayout;
    private String mediaUrl;

    public static EditFragment newInstance(String title, String content, String mediaUrl, String source) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        args.putString("mediaUrl", mediaUrl);
        args.putString("source", source);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        titleEt = view.findViewById(R.id.title_et);
        contentEt = view.findViewById(R.id.content_et);
        locationEt = view.findViewById(R.id.location_et);
        sourceEt = view.findViewById(R.id.source_et);

        previewContentTv = view.findViewById(R.id.preview_content_tv);
        sidebarLocationTv = view.findViewById(R.id.sidebar_location_tv);
        sidebarSourceTv = view.findViewById(R.id.sidebar_source_tv);
        previewImageBg = view.findViewById(R.id.preview_image_bg);
        aspectRatioToggle = view.findViewById(R.id.aspect_ratio_toggle);

        previewCard = view.findViewById(R.id.share_preview_card);
        shareLayout = view.findViewById(R.id.share_layout);
        Button previewBtn = view.findViewById(R.id.preview_btn);

        if (getArguments() != null) {
            titleEt.setText(getArguments().getString("title"));
            contentEt.setText(getArguments().getString("content"));
            mediaUrl = getArguments().getString("mediaUrl");
            sourceEt.setText(getArguments().getString("source"));
            locationEt.setText("TÜRKİYE");
        }

        previewBtn.setOnClickListener(v -> {
            String content = contentEt.getText().toString();
            String location = locationEt.getText().toString().toUpperCase();
            String source = sourceEt.getText().toString().toUpperCase();

            if (content.isEmpty()) {
                Toast.makeText(getContext(), "Lütfen içerik girin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Boyut ayarı
            int width = view.getWidth() - view.getPaddingLeft() - view.getPaddingRight();
            ViewGroup.LayoutParams lp = shareLayout.getLayoutParams();
            if (aspectRatioToggle.getCheckedButtonId() == R.id.btn_square) {
                lp.height = width;
            } else {
                lp.height = (int) (width * 1.25);
            }
            shareLayout.setLayoutParams(lp);

            // Metnin başına '//' ekliyoruz
            previewContentTv.setText("// " + content);

            // Konumu görseldeki gibi ortadan bölüp alt alta yazdırıyoruz (Örn: TÜRKİYE -> TÜR\nKİYE)
            sidebarLocationTv.setText(formatLocationText(location));

            sidebarSourceTv.setText(source);

            if (mediaUrl != null && !mediaUrl.isEmpty()) {
                Glide.with(this).load(mediaUrl).into(previewImageBg);
            }

            previewCard.setVisibility(View.VISIBLE);
            previewCard.setOnClickListener(cardView -> shareAsImage());
        });

        return view;
    }

    // Konum kelimesini ortadan ikiye bölen yardımcı fonksiyon
    private String formatLocationText(String text) {
        if (text == null || text.length() <= 3) return text;
        int mid = text.length() / 2;
        return text.substring(0, mid) + "\n" + text.substring(mid);
    }

    private void shareAsImage() {
        Bitmap bitmap = Bitmap.createBitmap(shareLayout.getWidth(), shareLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        shareLayout.draw(canvas);

        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "Haber", null);
        if(path != null) {
            Uri uri = Uri.parse(path);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, "Haberi Paylaş"));
        } else {
            Toast.makeText(getContext(), "Görsel kaydedilemedi, izinleri kontrol edin.", Toast.LENGTH_SHORT).show();
        }
    }
}