package kr.icehs.intec.nocovice_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

import kr.icehs.intec.nocovice_01.databinding.ActivityQrBinding;
import kr.icehs.intec.nocovice_01.databinding.QrItemBinding;
import needle.Needle;
import needle.UiRelatedProgressTask;

public class QrActivity extends AppCompatActivity {

    ActivityQrBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.qrRecycler.setLayoutManager(new LinearLayoutManager(this));

        Needle.onBackgroundThread().execute(new UiRelatedProgressTask<DbTop, Void>() {
            @Override
            protected DbTop doWork() {
                try {
                    return new Gson().fromJson(DB.sendGet(), DbTop.class);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void thenDoUiRelatedWork(DbTop dbTop) {
                ArrayList<String> items = new ArrayList<>();
                ArrayList<DbRoomBooks> books = dbTop.roombooks;
                for(int i = 0; i < books.size(); i++) {
                    if(books.get(i).uNo == DB.session.uNo) {
                        String startTime = books.get(i).startTime;
                        items.add(startTime.substring(0, 4) + startTime.substring(5, 7) + startTime.substring(8, 10) + startTime.substring(11, 13));
                    }
                }
                binding.qrRecycler.setAdapter(new QrAdapter(items));
            }

            @Override
            protected void onProgressUpdate(Void unused) {

            }
        });
    }

    private class QrAdapter extends RecyclerView.Adapter<QrAdapter.QrViewHolder> {

        private ArrayList<String> items;

        private class QrViewHolder extends RecyclerView.ViewHolder {
            QrItemBinding binding;

            public QrViewHolder(QrItemBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

        public QrAdapter(ArrayList<String> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public QrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new QrViewHolder(QrItemBinding.inflate(getLayoutInflater()));
        }

        @Override
        public void onBindViewHolder(@NonNull QrViewHolder holder, int position) {
            holder.binding.dateTimeTextView.setText(items.get(position));
            holder.binding.dateTimeTextView.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), QrDialog.class);
                intent.putExtra("filename", getFileStreamPath("qr").getPath() + "resvon" + items.get(position) + "by" + DB.session.uNo + ".png");
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}