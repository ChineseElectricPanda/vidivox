
======================================================================================
				Welcome to Vidivox!!
======================================================================================

--------------------------------------------------------------------------------------

Manual Contents:
	1. Running application
	2. Playing video and video options
	3. Audio Options

--------------------------------------------------------------------------------------

1. Running application:

	Opening with just jar file:
	Try double clicking the jar file, if it doesn't work
	open the terminal, navigate to the directory of the 
	jar file called "vidivox.jar". In the command line 
	enter the following:
	java -jar vidivox.jar

	If this does not work, right click on the jar file
	and select the properties option. Then select the 
	permissions tab. There will be a tick box saying
	whether to allow the file to execute as a program.
	Make sure that is ticked. Also change the "Access"
	to Read and Write for all people.
	OR
	In the terminal, while in the directory the jar file
	is in, enter the following command:
	chmod u+x vidivox.jar
	
	Opening through eclipse:
	Open up eclipse and when prompted select the
	working directory as "vidivox"
	To run application click on the green play button
	and run from the Main class 

--------------------------------------------------------------------------------------

2. Playing video and video options

	To play a video file (.mp4 or .avi) open the "File"
	menu, in the drop down menu select open video and
	navigate to where the video is and select it and it
	should begin playing.

	Note: The slider bar under the video can be dragged
	      around to start watching the video at any time

--------------------------------------------------------------------------------------

3. Audio Options
	
	In order to create audio, click on "Edit" in the menu
	bar and select "Audio Overlays.." from the drop down
	menu. This will open up another window which will
	initially only have 2 buttons.

	"Add Commentary" button allows you to type in text 
	which will be converted into an audio file automatically
	or you can specify to save it as an mp3 with the "Save to
	mp3" button. To hear an example of how the text entered
	sounds, press the play button and the audio will be 
	generated. The volume slider will let you control how
	loud the voice speaking is. 

	"Add Audio File" button allows you to load in a pre-existing
	audio file. Click on the "..." button to choose the file 
	to load in or to select another one to load in.

	To test how one comment/audio file sounds with a video or 
	with other audio tick the "Preview" box and press the 
	play button under the video. This will cause all audio files
	and comments ticked to be played with the video if there
	is one.

	In order to start the audio at a specific point in the video,
	modify the "Start Time" boxes. The first box refers to the 
	number of minutes after which the audio should start once 
	the video starts, the second box the number of seconds and
	the last box is for milliseconds. If you enter a number
	larger than 59 for seconds it will covert it into minutes and
	seconds automatically.

	To remove either a comment or audio file, click on the "X".

	Saving/Opening Project:
	If you want to save the comments and the audio files you have 
	added then click on the "Save Project" option from the drop
	down menu in "File". This will allow you to save all your
	work the way it is, video + audio without merging them together
	And to open this project just click on the "Open Project"
	option from the same drop down menu. 
	
	WARNING: To avoid errors, do not move the project files
		 once created

	Exporting: 
	In the "File" drop down menu there is a "Export" option. 
	Clicking this will cause the audio you have added to the project
	as well as the video you have added to be merged together
	and will prompt you to save the file. Save this file as a 
	video file (mp4 or avi depending on original file format).

--------------------------------------------------------------------------------------
