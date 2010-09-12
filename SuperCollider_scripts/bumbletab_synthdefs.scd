s.boot

(
~notes = ([8,7,6,5,4,9,8,7,8,7,6,5,4,5,6,7]
//	   ++[8,7,6,5,4,9,8,7,8,7,6,5,4,5,6,7]
//	   ++[8,7,6,5,6,5,4,3,4,3,2,1,2,9,8,7]
//	   ++[8,7,6,5,6,5,4,3,4,3,2,1,2,9,8,7]
	   ) + 40
	   + 12
	;
//~notes = ~notes ++ (~notes + 7);
~dur = 0.1;
~patt = Pbind(
//	\midinote, Pseq(~notes + 24),
	\midinote, Pseq(~notes),
	\dur, ~dur,
	\amp, 0.3
)
)
~patt.play;



(
SynthDef(\bumbletab_rec, { |dur=0.1, targetBus=0, inpitchBus=0, recBuf=0, whoToUnpause=0, ampthresh=0.1|
	var sig, amp, innote, diff, matches, matcheslong, sigdel, matcheslongdel, delaylen, undelaylen, faker;
	var note, demandtrig;
	var rec;
	
	demandtrig = LocalIn.kr + Impulse.kr(0);
	note    = Demand.kr(demandtrig, 0, Dseq(~notes, 1));
	note.poll(1, "notedemanded");
	
	sig = SoundIn.ar();
	
	sig = Normalizer.ar(sig, 1, 0.1);
	
	// Now decide if the note has been performed
	innote = Pitch.kr(sig).at(0).cpsmidi.poll(1, "innote");
	Out.kr(targetBus, note);
	Out.kr(inpitchBus, innote);
	diff = (innote - note).abs;
	amp = A2K.kr(Amplitude.ar(sig));
	matches = (diff < 0.5) * (amp > ampthresh); // tolerant!
	// we only trigger if the pitch matches for the desired amount of time
	matcheslong = Trig1.kr(Integrator.kr(matches, matches) >= (dur * ControlRate.ir), dur);
	
	//diff.poll(matcheslong);
	note.round.poll(matcheslong);
	innote.round.poll(matcheslong);
	
	LocalOut.kr(Trig1.kr(matcheslong, 0));
	
	// now delay the audio and delay the decision about whether to store it
	delaylen = 1;
	undelaylen = dur;
	
	sigdel         = DelayC.ar(sig        , delaylen, undelaylen);
	matcheslongdel = DelayN.kr(matcheslong, delaylen, 0);//( - undelaylen);
	
	
	rec = RecordBuf.ar(sigdel, recBuf, run: matcheslong, loop: 0, doneAction: 2);
	
	// unpause the playback one when done
	Pause.kr(Done.kr(rec), whoToUnpause);
	
	sig;
	DC.ar(0);
//	Out.ar(0, PinkNoise.ar(0.1));
}).writeDefFile;

SynthDef(\bumbletab_playback, {|out=0, recBuf=0, loop=1, doneAction=0| 
	Out.ar(out, PlayBuf.ar(1, recBuf, loop: loop, doneAction: doneAction))
}).writeDefFile;
)

// where the output will go:
b = Buffer.alloc(s, ~notes.size * ~dur * s.sampleRate);
~inpitchBus = Bus.control(s);
~targetBus = Bus.control(s);

y = Synth.newPaused(\bumbletab_playback, args: [\recBuf, b]);
x = Synth(\bumbletab_rec, args: [\dur, ~dur, \targetBus, ~targetBus, \inpitchBus, ~inpitchBus, \recBuf, b, \whoToUnpause, y]);
//y.free;


b.write("~/Music/SuperCollider Recordings/hackday4.aiff".standardizePath, "AIFF", "float");
