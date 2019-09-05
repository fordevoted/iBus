package e.user.ibus.Presenter;

import android.content.Context;
import android.view.View;

import e.user.ibus.model.ItemViewHolder;

public interface OnItemClickListener {
	void onItemClick(ItemViewHolder holder, int position);
	void onLikeClick(ItemViewHolder holder, int position);
	void onItemLongClick(View view, int position);
}

