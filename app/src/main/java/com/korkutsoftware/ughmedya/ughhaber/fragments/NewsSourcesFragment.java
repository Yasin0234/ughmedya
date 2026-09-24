package com.korkutsoftware.ughmedya.ughhaber.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.korkutsoftware.ughmedya.R;
import com.korkutsoftware.ughmedya.ughhaber.adapters.NewsAdapter;
import com.korkutsoftware.ughmedya.ughhaber.models.NewsItem;
import twitter4j.Twitter;
import twitter4j.v1.Status;
import java.util.ArrayList;
import java.util.List;

public class NewsSourcesFragment extends Fragment {

    private static final String TAG = "NewsSourcesFragment";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView statusTv;
    private NewsAdapter adapter;
    private List<NewsItem> newsList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Twitter Keys
    private static final String CONSUMER_KEY = "OrhZn8HT0VFct2XPLqEfmLCJd";
    private static final String CONSUMER_SECRET = "AtA68Lv6VAL9RyFdtbPwO6Ia4IjBI83GxMUxXVY1MimC6KCmLS";
    private static final String ACCESS_TOKEN = "1946097508653940737-kspULwF7WV2fJQMruogzBzizhGnEsC";
    private static final String ACCESS_TOKEN_SECRET = "ONOGM5cug1xDwmcLC4exDkxKM5tZXhmA2LUV0c5SjsttL";
    
    private static final String TWITTER_USERNAME = "ughhaber";
    private static final String DEFAULT_RSS_URL = "https://www.trthaber.com/manset_articles.rss";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_sources, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        statusTv = view.findViewById(R.id.status_tv);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NewsAdapter(newsList, (newsItem, mediaUrl) -> saveToFirestore(newsItem));
        recyclerView.setAdapter(adapter);

        startFetching();

        return view;
    }

    private void startFetching() {
        newsList.clear();
        adapter.notifyDataSetChanged();
        updateStatus("Haberler yükleniyor...");
        
        fetchTwitterWithTwitter4J();
        fetchRssFeed(DEFAULT_RSS_URL);
    }

    private void updateStatus(String msg) {
        if (isAdded() && getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (statusTv != null) {
                    statusTv.setVisibility(View.VISIBLE);
                    statusTv.setText("Durum: " + msg);
                }
            });
        }
    }

    private void fetchTwitterWithTwitter4J() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                Twitter twitter = Twitter.newBuilder()
                  .prettyDebugEnabled(true)
                  .oAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET)
                  .oAuthAccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET)
                  .build();
                
                List<Status> statuses = twitter.v1().timelines().getUserTimeline(TWITTER_USERNAME);
                
                List<NewsItem> twitterItems = new ArrayList<>();
                for (Status status : statuses) {
                    NewsItem item = new NewsItem();
                    item.setId(String.valueOf(status.getId()));
                    item.setContent(status.getText());
                    item.setSource("Twitter (@ughhaber)");
                    item.setTimestamp(Timestamp.now());

                    // Media check
                    if (status.getMediaEntities() != null && status.getMediaEntities().length > 0) {
                        item.setImageUrl(status.getMediaEntities()[0].getMediaURLHttps());
                    }
                    twitterItems.add(item);
                }

                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        newsList.addAll(0, twitterItems);
                        adapter.notifyDataSetChanged();
                        updateStatus("Twitter ve RSS aktif.");
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Twitter fetching failed", e);
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        updateStatus("Twitter Hatası: " + e.getMessage());
                    });
                }
            }
        }).start();
    }

    private void fetchRssFeed(String urlString) {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(urlString);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                java.io.InputStream stream = conn.getInputStream();
                org.xmlpull.v1.XmlPullParserFactory factory = org.xmlpull.v1.XmlPullParserFactory.newInstance();
                org.xmlpull.v1.XmlPullParser parser = factory.newPullParser();
                parser.setInput(stream, null);

                List<NewsItem> rssItems = new ArrayList<>();
                int eventType = parser.getEventType();
                NewsItem currentItem = null;

                while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                    String name = parser.getName();
                    switch (eventType) {
                        case org.xmlpull.v1.XmlPullParser.START_TAG:
                            if ("item".equalsIgnoreCase(name)) {
                                currentItem = new NewsItem();
                                currentItem.setSource("RSS");
                                currentItem.setTimestamp(Timestamp.now());
                            } else if (currentItem != null) {
                                if ("title".equalsIgnoreCase(name)) {
                                    currentItem.setTitle(parser.nextText());
                                } else if ("description".equalsIgnoreCase(name)) {
                                    currentItem.setContent(parser.nextText());
                                } else if ("link".equalsIgnoreCase(name)) {
                                    currentItem.setId(parser.nextText());
                                } else if ("enclosure".equalsIgnoreCase(name)) {
                                    String type = parser.getAttributeValue(null, "type");
                                    if (type != null && type.startsWith("image/")) {
                                        currentItem.setImageUrl(parser.getAttributeValue(null, "url"));
                                    }
                                }
                            }
                            break;
                        case org.xmlpull.v1.XmlPullParser.END_TAG:
                            if ("item".equalsIgnoreCase(name) && currentItem != null) {
                                rssItems.add(currentItem);
                                currentItem = null;
                            }
                            break;
                    }
                    eventType = parser.next();
                }

                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        newsList.addAll(rssItems);
                        adapter.notifyDataSetChanged();
                        updateStatus("RSS verileri eklendi.");
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "RSS fetching failed", e);
                updateStatus("RSS Hatası: " + e.getMessage());
            }
        }).start();
    }

    private void saveToFirestore(NewsItem newsItem) {
        newsItem.setUpdated_at(Timestamp.now());
        db.collection("haberler")
                .add(newsItem)
                .addOnSuccessListener(documentReference -> 
                        Toast.makeText(getContext(), "Kayıt Başarılı", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> 
                        Toast.makeText(getContext(), "Kayıt Hatası", Toast.LENGTH_SHORT).show());
    }
}