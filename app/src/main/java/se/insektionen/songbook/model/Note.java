package se.insektionen.songbook.model;


import android.os.Bundle;

import com.google.auto.value.AutoValue;

import java.util.Date;

/**
 * Data model for a note.
 */

@AutoValue
public abstract class Note {
	public static Note create(String title, String text, Date creationDate) {
		return new AutoValue_Note(title, text, creationDate);
	}

	public abstract String title();

	public abstract String text();

	public abstract Date creationDate();

	public Bundle toBundle() {
		Bundle bundle = new Bundle();
		bundle.putString(Keys.TITLE, title());
		bundle.putString(Keys.TEXT, text());
		bundle.putString(Keys.DATE, creationDate().toString());

		return bundle;
	}

	public static Note fromBundle(Bundle bundle) {
		String title = bundle.getString(Keys.TITLE);
		String text = bundle.getString(Keys.TEXT);
		Date date = new Date(bundle.getString(Keys.DATE));

		return Note.create(title, text, date);
	}

	private static class Keys {
		static final String TITLE = "title";
		static final String TEXT = "text";
		static final String DATE = "date";

		private Keys() {
		}
	}
}

