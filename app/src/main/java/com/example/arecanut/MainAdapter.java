package com.example.arecanut;

import static android.media.CamcorderProfile.get;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainAdapter extends FirebaseRecyclerAdapter<MainModel,MainAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MainAdapter(@NonNull FirebaseRecyclerOptions<MainModel> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull MainModel model) {
       holder.title.setText(model.getTitle());
      holder.category.setText(model.getCategory());
      holder.scale.setText(model.getScale());
       holder.quantity.setText(model.getQuantity());
       holder.price.setText(model.getPrice());
       holder.description.setText(model.getDescription());
        Picasso.get().load(model.getImageUrl()).into(holder.img);
        Log.d("DescriptionDebug", "Description: " + model.getDescription());

       holder.btn1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final DialogPlus dialogPlus=DialogPlus.newDialog(holder.img.getContext())
                       .setContentHolder(new ViewHolder(R.layout.update_pop))
                       .setExpanded(true,1300)
                       .create();
//               dialogPlus.show();
               View view=dialogPlus.getHolderView();
               EditText title=view.findViewById(R.id.txttitle);
               EditText category=view.findViewById(R.id.txtquality);
               EditText quantity=view.findViewById(R.id.txtquantity);
               EditText price=view.findViewById(R.id.txtprice);
               EditText description=view.findViewById(R.id.txtdescript);

               Button btnupdate=view.findViewById(R.id.btnupdate);
                title.setText(model.getTitle());
                category.setText( model.getCategory());
                quantity.setText(model.getQuantity());
                price.setText(model.getPrice());
                description.setText(model.getDescription());
                dialogPlus.show();

                btnupdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String,Object>map =new HashMap<>();
                        map.put("title",title.getText().toString());
                        map.put("category",category.getText().toString());
                        map.put("quantity",quantity.getText().toString());
                        map.put("price",price.getText().toString());
                        map.put("description",description  .getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("Details").child(getRef(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.category.getContext(),"Data Updated Successfully",Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Toast.makeText(holder.category.getContext(),"Error While Updating",Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                });

                    }
                });
           }
       });
     holder.btn2.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             AlertDialog.Builder builder=new AlertDialog.Builder(holder.category.getContext());
             builder.setTitle("Are you Sure?");
             builder.setMessage("Deleted data can't be undo.");
             builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     FirebaseDatabase.getInstance().getReference().child("Details")
                             .child(getRef(position).getKey()).removeValue();
                 }
             });
             builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                  Toast.makeText(holder.category.getContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                 }
             });
             builder.show();
         }
     });

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item,parent,false);
        return new myViewHolder(view);
    }



    class myViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView title,quantity,category,price,description,location,scale;
        Button btn1,btn2;

//        Spinner sp2;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            img=(ImageView)itemView.findViewById(R.id.img1);
            title = (TextView)itemView.findViewById(R.id.categorytext);
            quantity = (TextView)itemView.findViewById(R.id.quantitytext);
            category =(TextView)itemView.findViewById(R.id.qualitytext);
            price = (TextView)itemView.findViewById(R.id.pricetext);
            description=(TextView)itemView.findViewById(R.id.textdescription);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            btn1=(Button)itemView.findViewById(R.id.button3);
            btn2=(Button)itemView.findViewById(R.id.button4);
            scale=(TextView)itemView.findViewById(R.id.spinner2txt);
        }
    }
}
