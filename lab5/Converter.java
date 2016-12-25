import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by anastasia on 22.12.16.
 */
public class Converter {

    public static void convert(String filename) throws IOException {
        File f = new File(filename);
        BufferedImage image = ImageIO.read(f);

        Fridrich method = new Fridrich(image);
        image = method.parseImage(); //write image

        f = new File("parsed" + filename);
        ImageIO.write(image, "png", f);
    }

    public static void main(String[] args) throws IOException {
        convert("img.png");
    }
}
