/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *******************************************************************************/

package ca.mcgill.cs.swevo.qualyzer.editors;

import java.io.File;
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;

/**
 * Contains the jlgui.BasicPlayer and handles all the actions on it.
 */
public class AudioPlayer
{
	
	private static final int MICROSECONDS = 1000000;
	
	private Logger fLogger;
	
	private MyPlayer fPlayer;
	private long fMicroSecondsPos;
	private int fSecondsPos;
	private double fLength;
	
	private TranscriptEditor fEditor;
	
	private String fAudioFile;
	private boolean fIsMP3;
	private boolean fIsWAV;
	
	//WAV only
	private long fMicSecondPosAfterSeek;
	private double fMicSecondsPerByte;
	
	/**
	 * Takes the filename of the audio file and the editor that is opening it.
	 * @param audioFile
	 * @param editor
	 */
	public AudioPlayer(String audioFile, TranscriptEditor editor)
	{
		fLogger = LoggerFactory.getLogger(AudioPlayer.class);
		
		fPlayer = new MyPlayer();
		fEditor = editor;
		fAudioFile = audioFile;
		
		fMicroSecondsPos = 0;
		fSecondsPos = 0;
		fLength = 0;
		
		fIsMP3 = false;
		fIsWAV = false;
		
		fPlayer.addBasicPlayerListener(createBasicPlayerListener());
		
		File file = new File(fAudioFile);
		try
		{
			fPlayer.open(file);
		}
		catch(BasicPlayerException e)
		{
			fLogger.error("AudioPlayer: Could not open", e); //$NON-NLS-1$
			throw new QualyzerException(MessagesClient.getString("editors.AudioPlayer.audioOpenFailed", "ca.mcgill.cs.swevo.qualyzer.editors.Messages"), e); //$NON-NLS-1$
		}
	}
	
	/**
	 * Handles updates as the file plays.
	 * @return
	 */
	private BasicPlayerListener createBasicPlayerListener()
	{
		return new BasicPlayerListener(){

			@SuppressWarnings("unchecked")
			@Override
			public void opened(Object arg0, Map arg1)
			{
				fLength = ((Integer) arg1.get("audio.length.frames")) / //$NON-NLS-1$
					((Float) arg1.get("audio.framerate.fps")); //$NON-NLS-1$ 
				
				double lengthMicSec = fLength * MICROSECONDS;
				fMicSecondsPerByte = lengthMicSec / (Integer)arg1.get("audio.length.bytes"); //$NON-NLS-1$
				fEditor.setLength(fLength);
				
				if(arg1.get("audio.type").equals("WAVE")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					fIsWAV = true;
				}
				else if(arg1.get("audio.type").equals("MP3")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					fIsMP3 = true;
				}
			}
			@SuppressWarnings("unchecked")
			@Override
			public void progress(int arg0, long arg1, byte[] arg2, Map arg3)
			{
				if(fIsMP3)
				{
					fMicroSecondsPos = Long.valueOf((Long) arg3.get("mp3.position.microseconds")); //$NON-NLS-1$
				}
				else if(fIsWAV)
				{
					fMicroSecondsPos = fMicSecondPosAfterSeek + arg1;
				}
				
				if(fSecondsPos != fMicroSecondsPos / MICROSECONDS)
				{
					fSecondsPos = (int) fMicroSecondsPos / MICROSECONDS;
					fEditor.setSeconds(fSecondsPos);
				}
			}

			@Override
			public void setController(BasicController arg0){}
			@Override
			public void stateUpdated(BasicPlayerEvent arg0){}			
		};
	}

	/**
	 * Handles the play button being pressed.
	 */
	public void play()
	{
		try
		{
			if(fPlayer.getStatus() == BasicPlayer.PAUSED)
			{
				fPlayer.resume();
			}
			else if(fPlayer.getStatus() == BasicPlayer.STOPPED || fPlayer.getStatus() == BasicPlayer.OPENED)
			{
				fPlayer.play();
			}
		}
		catch(BasicPlayerException e)
		{
			fLogger.error("AudioPlayer: Could not play audio", e); //$NON-NLS-1$
			throw new QualyzerException(MessagesClient.getString("editors.AudioPlayer.playFailed", "ca.mcgill.cs.swevo.qualyzer.editors.Messages"), e); //$NON-NLS-1$
		}
	}
	
	/**
	 * Handles the pause button being pressed.
	 */
	public void pause()
	{
		try
		{
			if(fPlayer.getStatus() == BasicPlayer.PLAYING)
			{
				fPlayer.pause();
				
			}
		}
		catch(BasicPlayerException e)
		{
			fLogger.error("AudioPlayer: Could not pause", e); //$NON-NLS-1$
			throw new QualyzerException(MessagesClient.getString("editors.AudioPlayer.pauseFailed", "ca.mcgill.cs.swevo.qualyzer.editors.Messages"), e); //$NON-NLS-1$
		}
	}
	
	/**
	 * Handles the stop button being pressed.
	 */
	public void stop()
	{
		try
		{
			fPlayer.stop();
			fSecondsPos = 0;
			fMicroSecondsPos = 0;
			fMicSecondPosAfterSeek = 0;
		}
		catch(BasicPlayerException e)
		{
			fLogger.error("AudioPlayer: Could not stop.", e); //$NON-NLS-1$
			throw new QualyzerException(MessagesClient.getString("editors.AudioPlayer.stopFailed", "ca.mcgill.cs.swevo.qualyzer.editors.Messages"), e); //$NON-NLS-1$
		}
	}

	/**
	 * Forces the file to seek to the specified time.
	 * @param selection A time in seconds to seek to.
	 */
	public void jumpToTime(int selection)
	{
		long bytes = (long) (selection * MICROSECONDS / fMicSecondsPerByte);
		try
		{
			fMicSecondPosAfterSeek = selection * MICROSECONDS;
			fPlayer.seek(bytes);
			fEditor.setSeconds(selection);
		}
		catch (BasicPlayerException e)
		{
			fLogger.error("Audio Player: Could not seek", e); //$NON-NLS-1$
			throw new QualyzerException(MessagesClient.getString("editors.AudioPlayer.seekFailed", "ca.mcgill.cs.swevo.qualyzer.editors.Messages"), e); //$NON-NLS-1$
		}
		
	}

	/**
	 * Try to open a new audio file.
	 * @param audioFile
	 */
	public void open(String audioFile)
	{
		fAudioFile = audioFile;
		File file = new File(fAudioFile);
		try
		{
			reset();
			fPlayer.open(file);			
		}
		catch(BasicPlayerException e)
		{
			fLogger.error("AudioPlayer: Could not open", e); //$NON-NLS-1$
			throw new QualyzerException(MessagesClient.getString("editors.AudioPlayer.cannotOpenAudio", "ca.mcgill.cs.swevo.qualyzer.editors.Messages"), e); //$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	private void reset()
	{
		fMicroSecondsPos = 0;
		fSecondsPos = 0;
		fLength = 0;
		fAudioFile = ""; //$NON-NLS-1$
		fIsMP3 = false;
		fIsWAV = false;
		fMicSecondPosAfterSeek = 0;
		fMicSecondsPerByte = 0;
	}
	
	/**
	 * The audio player is closing so stop the file from playing and close the stream.
	 */
	public void close()
	{
		
		try
		{
			fPlayer.stop();
		}
		catch (BasicPlayerException e1)
		{
			//Just keep on going.
			//System.out.println("ate stop exception");
		}
		
		fPlayer.closeStream();
	}
	
	/**
	 * This gives me access to the close stream method.
	 *
	 */
	private class MyPlayer extends BasicPlayer
	{
		/* (non-Javadoc)
		 * @see javazoom.jlgui.basicplayer.BasicPlayer#closeStream()
		 */
		@Override
		public void closeStream()
		{
			super.closeStream();
		}
	}
	
}
