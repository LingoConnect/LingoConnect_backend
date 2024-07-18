package LingoConnect.app.utils;

import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayInputStream;

@Component
public class AudioFileUtils {

    public static String convertAndStorePcm(byte[] audioData) throws IOException, UnsupportedAudioFileException {
        // 가정: audioData는 이미 PCM 형식이거나 변환 로직이 필요
        ByteArrayInputStream bis = new ByteArrayInputStream(audioData);
        AudioInputStream ais = AudioSystem.getAudioInputStream(bis);

        File directory = new File("src/main/resources/audio");
        File outputFile = new File(directory,"convertedAudio.pcm");
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputFile);

        return outputFile.getName();
    }
}