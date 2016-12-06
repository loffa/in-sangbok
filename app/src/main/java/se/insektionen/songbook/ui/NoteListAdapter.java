package se.insektionen.songbook.ui;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import se.insektionen.songbook.R;
import se.insektionen.songbook.model.Note;

public final class NoteListAdapter extends BaseAdapter {
	private final LayoutInflater mInflater;
	private final List<Note> mList;

	public NoteListAdapter(Context context, List<Note> list) {
		mList = list;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view;
		if (null == convertView) {
			view = mInflater.inflate(R.layout.list_item_note, parent, false);
		} else {
			view = convertView;
		}

		final Note note = mList.get(position);

		TextView titleText = (TextView) view.findViewById(R.id.note_list_primary);
		titleText.setText(note.title());

		TextView dateText = (TextView) view.findViewById(R.id.note_list_tertiary);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
		String dateTextString = format.format(note.creationDate());
		dateText.setText(dateTextString);

		return view;
	}
}
