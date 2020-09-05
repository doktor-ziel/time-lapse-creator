package pl.backlog.green;

import org.bytedeco.ffmpeg.ffmpeg;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.opencv.imgcodecs.Imgcodecs.IMREAD_COLOR;

public class VideoMaker {

    public static Mat readFrame(String path) {
        return Imgcodecs.imread(path, IMREAD_COLOR);
    }

    public static Size getExpectedSize(String path) throws IOException {
        Path inputDirPath = Paths.get(path);
        String imagePath = Files.list(inputDirPath).findFirst().orElseThrow().toString();
        Mat image = Imgcodecs.imread(imagePath, IMREAD_COLOR);
        return image.size();
    }

    public static void main(String[] args) throws IOException {
        Loader.load(opencv_java.class);
        Loader.load(ffmpeg.class);
        String dirPath = "src/main/resources/picts";
        String outputPath = "src/main/resources/video.avi";
        Size size = getExpectedSize(dirPath);

        VideoWriter videoWriter = new VideoWriter(outputPath, VideoWriter.fourcc('F', 'M', 'P', '4'), 4, size, true);
        if(!videoWriter.isOpened()) {
            videoWriter.release();
            throw new IllegalArgumentException("Video Writer Exception: VideoWriter not opened, check parameters.");
        }
        Path inputDirPath = Paths.get(dirPath);
        Files.list(inputDirPath)
                .peek(System.out::println)
                .map(p -> readFrame(p.toAbsolutePath().toString()))
                .forEach(videoWriter::write);
        videoWriter.release();
    }
}
