package se.insektionen.songbook.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import se.insektionen.songbook.R;
import se.insektionen.songbook.model.Note;

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

		view.setOnClickListener(new RootViewOnClickListener(textView));
		titleView.setOnClickListener(new TitleOnClickListener(note));

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

	private class RootViewOnClickListener implements View.OnClickListener {
		private final TextView textField;

		private RootViewOnClickListener(TextView textField) {
			this.textField = textField;
		}

		@Override
		public void onClick(View view) {
			textField.performClick();
		}
	}

	private class TitleOnClickListener implements View.OnClickListener {
		private final Note note;

		private TitleOnClickListener(Note note) {
			this.note = note;
		}

		@Override
		public void onClick(final View view) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			final EditText editText = new EditText(getContext());

			editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
			editText.setText(note.title());
			editText.setSelection(note.title().length());
			builder.setView(editText);
			builder.setTitle(R.string.add_title);

			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
					final String name = editText.getText().toString();
					final SharedPreferences prefs = getActivity().getSharedPreferences(NotesFragment.PREF_NAME, Context.MODE_PRIVATE);
					final Map<String, String> allNotes = (Map<String, String>) prefs.getAll();
					final TextView titleView = (TextView) view;

					allNotes.remove(note.title());
					allNotes.put(name, String.format("%s|%s", note.creationDate(), note.text()));
					titleView.setText(name);
					saveNotes(allNotes);
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			builder.show();
		}

		private void saveNotes(Map<String, String> allNotes) {
			final SharedPreferences.Editor editor = getActivity().getSharedPreferences(NotesFragment.PREF_NAME, Context.MODE_PRIVATE).edit();
			editor.clear();

			for (String name : allNotes.keySet()) {
				editor.putString(name, allNotes.get(name));
			}
			editor.apply();
		}
	}
}
