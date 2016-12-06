package se.insektionen.songbook.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import se.insektionen.songbook.R;
import se.insektionen.songbook.model.Note;
import se.insektionen.songbook.services.Preferences;

public final class NoteFragment extends Fragment implements MainActivity.HasNavigationItem {
	private final static String TAG = NoteFragment.class.getSimpleName();

	private Preferences mPrefs;

	public static NoteFragment createInstance(Note note) {
		NoteFragment fragment = new NoteFragment();
		fragment.setArguments(note.toBundle());
		return fragment;
	}

	@Override
	public int getItemId() {
		return 0;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mPrefs = new Preferences(context);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_note, container, false);
		Note note = Note.fromBundle(getArguments());

		TextView titleView = (TextView) view.findViewById(R.id.note_name);
		TextView dateView = (TextView) view.findViewById(R.id.note_date);
		TextView textView = (TextView) view.findViewById(R.id.note_layout);

		if (titleView != null) {
			titleView.setText(note.title());
		}

		if (dateView != null) {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
			String dateTextString = format.format(note.creationDate());
			dateView.setText(dateTextString);
		}

		if (textView != null) {
			textView.setText(note.text());
		}

		return view;
	}
}
