package se.insektionen.songbook.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import se.insektionen.songbook.R;
import se.insektionen.songbook.model.Note;
import se.insektionen.songbook.utils.AndroidUtils;

public class NotesFragment extends ListFragment implements MainActivity.HasNavigationItem, MainActivity.HasMenu {
	private static final int INTERNAL_LIST_CONTAINER_ID = 0x00ff0003; // from android.support.v4.app.ListFragment
	private static final String STATE_LIST_VIEW = "songbookListViewState";
	public static final String PREF_NAME = "preferences.notes";
	private static final String TAG = NotesFragment.class.getSimpleName();
	private NoteListAdapter mListAdapter;
	private Parcelable mListState;
	private boolean mIsLoaded;
	private List<Note> mNoteList;

	@Override
	public int getItemId() {
		return 0;
	}

	@Override
	public int getMenu() {
		return R.menu.notes;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			mListState = savedInstanceState.getParcelable(STATE_LIST_VIEW);
		}

		mNoteList = new ArrayList<>();
		initializeList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
		assert (root != null);

		@SuppressWarnings("ResourceType")
		View listContainer = root.findViewById(INTERNAL_LIST_CONTAINER_ID);
		root.removeView(listContainer);

		View outerContainer = inflater.inflate(R.layout.fragment_notes, root, false);
		FrameLayout innerContainer = (FrameLayout) outerContainer.findViewById(R.id.list_container);
		innerContainer.addView(listContainer);

		root.addView(outerContainer, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		return root;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (null == mListAdapter || position < 0 || position >= mListAdapter.getCount()) {
			return;
		}

		Note note = (Note) mListAdapter.getItem(position);
		NoteFragment fragment = NoteFragment.createInstance(note);

		Activity activity = getActivity();
		if (activity instanceof MainActivity) {
			AndroidUtils.hideSoftKeyboard(getContext(), getView());
			saveInstanceState();
			((MainActivity) activity).openFragment(fragment);
		} else {
			Log.e(TAG, "Activity holding fragment is not MainActivity!");
		}
	}

	@Override
	public boolean onMenuItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.notes_add_item) {
			createNewNote();
		}
		return true;
	}

	private void createNewNote() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

		final EditText editText = new EditText(getContext());
		builder.setView(editText);
		builder.setTitle(R.string.add_title);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int which) {
				final String name = editText.getText().toString();
				final Note note = Note.create(name, "", new Date());
				mNoteList.add(note);

				saveNotes();
				mListAdapter.notifyDataSetInvalidated();
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

	private void saveNotes() {
		final SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();

		for (Note note : mNoteList) {
			final String key = note.title();
			final String data = String.format(Locale.ENGLISH, "%s|%s", note.creationDate().toString(), note.text());

			editor.putString(key, data);
		}

		editor.apply();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!mIsLoaded) {
			initializeList();
			mIsLoaded = true;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if(null != getView()) {
			mListState = getListView().onSaveInstanceState();
			outState.putParcelable(STATE_LIST_VIEW, mListState);
		}
	}

	private void initializeList() {
		final SharedPreferences prefs = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		final Map<String, String> allNotes = (Map<String, String>) prefs.getAll();

		mNoteList.clear();
		for (String name : allNotes.keySet()) {
			final String dateAndText = allNotes.get(name);
			final String[] parts = dateAndText.split("\\|");
			final Date date = new Date(parts[0]);
			final String text;

			if (parts.length > 1) {
				text = parts[1];
			} else {
				text = "";
			}

			final Note note = Note.create(name, text, date);
			mNoteList.add(note);
		}

		mListAdapter = new NoteListAdapter(getContext(), mNoteList);
		setListAdapter(mListAdapter);

		if (null != mListState) {
			getListView().onRestoreInstanceState(mListState);
			mListState = null;
		}
	}

	private void saveInstanceState() {
		mListState = getListView().onSaveInstanceState();
	}
}
