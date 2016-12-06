package se.insektionen.songbook.ui;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import se.insektionen.songbook.R;
import se.insektionen.songbook.model.Note;
import se.insektionen.songbook.services.Preferences;

public final class NoteFragment extends Fragment implements MainActivity.HasNavigationItem, MainActivity.HasMenu {
	private final static String TAG = NoteFragment.class.getSimpleName();
	private Note note;


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
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_note, container, false);
		note = Note.fromBundle(getArguments());

		final TextView titleView = (TextView) view.findViewById(R.id.note_name);
		final TextView dateView = (TextView) view.findViewById(R.id.note_date);
		final TextView textView = (TextView) view.findViewById(R.id.note_layout);

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

	@Override
	public int getMenu() {
		return R.menu.note;
	}

	@Override
	public boolean onMenuItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.note_remove) {
			removeNote();
		}
		return true;
	}

	private void removeNote() {
		SharedPreferences.Editor editor = getActivity().getSharedPreferences(NotesFragment.PREF_NAME, Context.MODE_PRIVATE).edit();

		editor.remove(note.title());
		editor.apply();

		getActivity().getSupportFragmentManager().popBackStack();
	}
}
