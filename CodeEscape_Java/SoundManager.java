import javax.sound.sampled.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Procedural sci-fi sound effects synthesizer using pure Java standard library (javax.sound.sampled).
 * Zero external audio files or third-party dependencies required.
 */
public class SoundManager {
    private static final float SAMPLE_RATE = 22050.0f;
    private final ExecutorService soundPool;
    private boolean enabled;

    public SoundManager(boolean enabled) {
        this.enabled = enabled;
        this.soundPool = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "SoundThread");
            t.setDaemon(true);
            return t;
        });
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public void playStep() {
        if (!enabled) return;
        soundPool.submit(() -> playTone(450, 400, 20, 0.15f, WaveType.TRIANGLE));
    }

    public void playKeyPickup() {
        if (!enabled) return;
        soundPool.submit(() -> {
            playTone(587, 880, 100, 0.35f, WaveType.SINE);
            playTone(880, 1174, 120, 0.40f, WaveType.SINE);
        });
    }

    public void playDoorUnlock() {
        if (!enabled) return;
        soundPool.submit(() -> {
            playTone(440, 554, 80, 0.35f, WaveType.SINE);
            playTone(554, 659, 120, 0.40f, WaveType.SQUARE);
        });
    }

    public void playAccessDenied() {
        if (!enabled) return;
        soundPool.submit(() -> {
            playTone(150, 130, 140, 0.45f, WaveType.SQUARE);
            playTone(130, 110, 160, 0.50f, WaveType.SQUARE);
        });
    }

    public void playSwitchToggle() {
        if (!enabled) return;
        soundPool.submit(() -> playTone(750, 350, 45, 0.30f, WaveType.TRIANGLE));
    }

    public void playLaserDamage() {
        if (!enabled) return;
        soundPool.submit(() -> playTone(280, 90, 180, 0.50f, WaveType.SAWTOOTH));
    }

    public void playTerminalOpen() {
        if (!enabled) return;
        soundPool.submit(() -> {
            playTone(880, 1046, 60, 0.30f, WaveType.SINE);
            playTone(1046, 1318, 80, 0.35f, WaveType.SINE);
        });
    }

    public void playCorrect() {
        if (!enabled) return;
        soundPool.submit(() -> {
            playTone(523, 523, 70, 0.35f, WaveType.SINE);
            playTone(659, 659, 70, 0.35f, WaveType.SINE);
            playTone(784, 784, 140, 0.40f, WaveType.SINE);
        });
    }

    public void playIncorrect() {
        if (!enabled) return;
        soundPool.submit(() -> {
            playTone(240, 200, 120, 0.40f, WaveType.SAWTOOTH);
            playTone(200, 160, 160, 0.45f, WaveType.SAWTOOTH);
        });
    }

    public void playLevelComplete() {
        if (!enabled) return;
        soundPool.submit(() -> {
            playTone(523, 523, 90, 0.35f, WaveType.SINE);
            playTone(659, 659, 90, 0.35f, WaveType.SINE);
            playTone(784, 784, 90, 0.35f, WaveType.SINE);
            playTone(1046, 1046, 220, 0.45f, WaveType.SINE);
        });
    }

    public void playChipCollect() {
        if (!enabled) return;
        soundPool.submit(() -> playTone(1200, 1600, 70, 0.25f, WaveType.SINE));
    }

    private enum WaveType { SINE, SQUARE, TRIANGLE, SAWTOOTH }

    private void playTone(float startFreq, float endFreq, int durationMs, float volume, WaveType type) {
        try {
            int numSamples = (int) ((durationMs / 1000.0f) * SAMPLE_RATE);
            byte[] buffer = new byte[numSamples];

            for (int i = 0; i < numSamples; i++) {
                float progress = (float) i / numSamples;
                float currentFreq = startFreq + (endFreq - startFreq) * progress;
                float period = SAMPLE_RATE / currentFreq;
                float phase = (i % period) / period; // 0.0 to 1.0

                float sample = 0.0f;
                switch (type) {
                    case SINE:
                        sample = (float) Math.sin(2.0 * Math.PI * phase);
                        break;
                    case SQUARE:
                        sample = phase < 0.5f ? 1.0f : -1.0f;
                        break;
                    case TRIANGLE:
                        sample = phase < 0.5f ? (4.0f * phase - 1.0f) : (3.0f - 4.0f * phase);
                        break;
                    case SAWTOOTH:
                        sample = 2.0f * phase - 1.0f;
                        break;
                }

                // Envelope: slight attack and release to avoid audio click
                float envelope = 1.0f;
                int fadeSamples = Math.min(60, numSamples / 4);
                if (i < fadeSamples) envelope = (float) i / fadeSamples;
                else if (i > numSamples - fadeSamples) envelope = (float) (numSamples - i) / fadeSamples;

                buffer[i] = (byte) (sample * envelope * volume * 127.0f);
            }

            AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) return;

            try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                line.open(format);
                line.start();
                line.write(buffer, 0, buffer.length);
                line.drain();
            }
        } catch (Exception ignored) {
            // Audio hardware unavailable or muted - fails gracefully
        }
    }
}
