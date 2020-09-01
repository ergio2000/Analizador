package pe.pucp.analizador.ultimas;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import pe.pucp.analizador.R;
import pe.pucp.analizador.imagenModel;
import pe.pucp.analizador.ultimas.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class imagenModelRecyclerViewAdapter extends RecyclerView.Adapter<imagenModelRecyclerViewAdapter.ViewHolder> {

    //private final List<DummyItem> mValues;
    private final List<imagenModel> mValues;

    //public imagenModelRecyclerViewAdapter(List<DummyItem> items) {
    public imagenModelRecyclerViewAdapter(List<imagenModel> items) {
        mValues = items;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_imagenmodel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).id);
        holder.mIdView.setText(mValues.get(position).Archivo);
        //holder.mContentView.setText(mValues.get(position).content);
        holder.mContentView.setText(mValues.get(position).Descripcion);
        holder.mElementosView.setText(mValues.get(position).Elementos());

        //imagen
        Glide.with(holder.image.getContext())
                .load( holder.mItem.RutaRemota )
                .error(R.drawable.ic_action_ultimasfotos)
                .into( holder.image);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mElementosView;

        public ImageView image;//imagen

        //public DummyItem mItem;
        public imagenModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
            mElementosView = view.findViewById(R.id.elementos);
            image = view.findViewById(R.id.imagen_foto);

        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}