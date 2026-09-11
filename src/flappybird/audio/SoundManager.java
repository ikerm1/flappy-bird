package flappybird.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;

/**
 * Handles all audio playback for the game.
 * Loads sounds from the classpath first, then falls back to the filesystem.
 */
public class SoundManager {

    public static final String SFX_JUMP      = "Oof_-_Roblox_-.wav";
    public static final String BGM_MENU      = "sonidito.wav";
    public static final String BGM_GAME_OVER = "Never_gonna_give_you_up.wav";

    private Clip   currentClip;
    private final String assetsPath;

    public SoundManager(String assetsPath) {
        this.assetsPath = assetsPath;
    }

    /** Stop current BGM and play a new one. Set loop=true for background music. */
    public void play(String filename, boolean loop) {
        stopCurrent();
        Clip clip = loadClip(filename);
        if (clip == null) return;
        currentClip = clip;
        if (loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            clip.start();
        }
    }

    /** Play a one-shot SFX without interrupting background music. */
    public void playSfx(String filename) {
        Clip clip = loadClip(filename);
        if (clip == null) return;
        clip.start();
        clip.addLineListener(ev -> {
            if (ev.getType() == LineEvent.Type.STOP) clip.close();
        });
    }

    public void stopCurrent() {
        if (currentClip != null) {
            if (currentClip.isRunning()) currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
    }

    private Clip loadClip(String filename) {
        // 1st: classpath
        try (InputStream is = getClass().getResourceAsStream("/flappybird/assets/" + filename)) {
            if (is != null) {
                AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
                Clip clip = AudioSystem.getClip();
                clip.open(audio);
                return clip;
            }
        } catch (Exception ignored) {}

        // 2nd: filesystem
        try {
            File f = new File(assetsPath + File.separator + filename);
            if (f.exists()) {
                AudioInputStream audio = AudioSystem.getAudioInputStream(f);
                Clip clip = AudioSystem.getClip();
                clip.open(audio);
                return clip;
            }
        } catch (Exception ignored) {}

        System.err.println("[SoundManager] Could not load: " + filename);
        return null;
    }
}
