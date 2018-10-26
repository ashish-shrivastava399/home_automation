package android.support.v11;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;


abstract public class GenericAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<T> dataList;
    protected Context context;


    public GenericAdapter(Context context, List<T> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    protected int getColor(@ColorRes int id) {
        return context.getResources().getColor(id);
    }

    protected boolean getHolderType(RecyclerView.ViewHolder holder, int type) {
        return holder.getItemViewType() == type;
    }

    public  void changeData(List<T> results){};

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }

        protected <T extends View> T findById(@IdRes int id) {
            if (id == View.NO_ID) {
                return null;
            }
            return itemView.findViewById(id);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        protected <T extends View> T findById(@IdRes int id) {
            if (id == View.NO_ID) {
                return null;
            }
            return itemView.findViewById(id);
        }

    }
}
