package org.dualnback.android;

import org.dualnback.android.Pair;

import java.util.ArrayList;
import java.util.Random;

import android.media.MediaPlayer;

import android.content.res.Resources;

import java.io.FileDescriptor;

public class Stimulus
{
	private int box_loc_;
	private int sound_index_;

	private static ArrayList<FileDescriptor> sounds_;

	public static void loadResources(Resources res) {
		sounds_ = new ArrayList<FileDescriptor>();

		sounds_.add(res.openRawResourceFd(R.raw.c).getFileDescriptor());
		sounds_.add(res.openRawResourceFd(R.raw.h).getFileDescriptor());
		sounds_.add(res.openRawResourceFd(R.raw.k).getFileDescriptor());
		sounds_.add(res.openRawResourceFd(R.raw.l).getFileDescriptor());
		sounds_.add(res.openRawResourceFd(R.raw.q).getFileDescriptor());
		sounds_.add(res.openRawResourceFd(R.raw.r).getFileDescriptor());
		sounds_.add(res.openRawResourceFd(R.raw.s).getFileDescriptor());
		sounds_.add(res.openRawResourceFd(R.raw.t).getFileDescriptor());
	}

	public Stimulus(int box_loc, int sound_index)
	{
		box_loc_ = box_loc;
		sound_index_ = sound_index;
	}

	public boolean visualMatch(Stimulus other) {
		return equivalent(box_loc_, other.box_loc_);
	}

	public boolean auralMatch(Stimulus other) {
		return equivalent(sound_index_, other.sound_index_);
	}

	private static boolean equivalent(int a, int b) {
		// -1 counts as not filled in, and never counts as a match
		return a != -1 && b != -1 && a == b;
	}

	public static Stimulus makeRandom(Stimulus other, boolean match_visual, boolean match_audio) {
		int sound_index;
		int box_loc;

		Random generator = new Random();

		if(match_visual)
			box_loc = other.box_loc_;
		else
			do {
				box_loc = generator.nextInt(8);
			} while(box_loc == other.box_loc_);

		if(match_audio)
			sound_index = other.sound_index_;
		else
			do {
				sound_index = generator.nextInt(8);
			} while(sound_index == other.sound_index_);

		return new Stimulus(box_loc, sound_index);
	}

	void displayVisual(Grid x) {
		Pair<Integer, Integer> d = new Pair(0, 1);
		Pair<Integer, Integer> r = new Pair(1, 0);
		Pair<Integer, Integer> u = new Pair(0, -1);
		Pair<Integer, Integer> l = new Pair(-1, 0);

		ArrayList< Pair<Integer, Integer> > spiral = new ArrayList< Pair<Integer, Integer> >();
		spiral.add(d); spiral.add(d);
		spiral.add(r); spiral.add(r);
		spiral.add(u); spiral.add(u);
		spiral.add(l); spiral.add(l);

		Pair<Integer, Integer> loc = new Pair(0, 0);
		for(int i = 0; i < box_loc_; ++i) {
			loc.first += spiral.get(i).first;
			loc.second += spiral.get(i).second;
		}

		x.lightSquare(loc.first, loc.second);
	}

	void playSound(MediaPlayer mp) {
		try {
			mp.setDataSource(sounds_.get(sound_index_));
			mp.prepare();
			mp.start();
		} catch(java.io.IOException e) {
		}
	}
}
