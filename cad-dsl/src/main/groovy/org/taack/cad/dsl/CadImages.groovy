package org.taack.cad.dsl

import groovy.transform.CompileStatic

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.nio.charset.StandardCharsets

/**
 * Writes the images that a kernel renders as Netpbm data in the format of their extension (PNG, JPEG, BMP, GIF, TIFF),
 * with ImageIO: no image library is then needed in the kernel. PPM (P6) holds RGB pixels, PAM (P7) RGB or RGBA ones.
 */
@CompileStatic
final class CadImages {
    /** Extensions whose files keep the Netpbm data */
    private static final Set<String> NETPBM = ['ppm', 'pnm', 'pam'] as Set<String>

    private CadImages() {}

    /**
     * @return Whether the image of fileName can be transparent
     */
    static boolean keepsAlpha(String fileName) {
        extension(fileName) in ['png', 'gif', 'tif', 'tiff']
    }

    /**
     * Rewrites file, if it holds PPM or PAM data, in the format of its extension
     */
    static void convert(File file) {
        if (!file.isFile() || extension(file.name) in NETPBM) return
        byte[] data = file.bytes
        if (data.length < 2 || data[0] != ('P' as char) || !(data[1] in [('6' as char) as byte, ('7' as char) as byte])) return
        BufferedImage image = read(data)
        String format = extension(file.name)
        if (format == 'jpg') format = 'jpeg'
        if (format == 'tif') format = 'tiff'
        // JPEG and BMP have no alpha: written opaque
        if (image.type == BufferedImage.TYPE_INT_ARGB && !keepsAlpha(file.name)) image = opaque(image)
        if (!ImageIO.write(image, format, file)) throw new IllegalArgumentException("No image writer for ${file.name}")
    }

    private static String extension(String fileName) {
        int dot = fileName.lastIndexOf('.')
        dot < 0 ? '' : fileName.substring(dot + 1).toLowerCase()
    }

    // P6: width, height and maxval separated by whitespace, # comments to the end of their line; P7: lines of
    // WIDTH, HEIGHT, DEPTH, MAXVAL, TUPLTYPE up to ENDHDR
    private static BufferedImage read(byte[] data) {
        int[] at = [2] as int[]
        int width, height, depth
        if (data[1] == ('6' as char)) {
            width = number(data, at)
            height = number(data, at)
            if (number(data, at) > 255) throw new IllegalArgumentException('PPM data of 16 bits')
            at[0]++
            depth = 3
        } else {
            Map<String, String> fields = [:]
            while (true) {
                String line = line(data, at)
                if (line == 'ENDHDR') break
                if (line && !line.startsWith('#')) {
                    String[] kv = line.split(/\s+/, 2)
                    fields[kv[0]] = kv.length > 1 ? kv[1] : ''
                }
            }
            width = fields.WIDTH as int
            height = fields.HEIGHT as int
            depth = fields.DEPTH as int
            if ((fields.MAXVAL as int) > 255 || !(depth in [3, 4])) throw new IllegalArgumentException("PAM data not RGB or RGBA of 8 bits: $fields")
        }
        BufferedImage image = new BufferedImage(width, height, depth == 4 ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB)
        int[] row = new int[width]
        int p = at[0]
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = data[p++] & 0xff, g = data[p++] & 0xff, b = data[p++] & 0xff
                int a = depth == 4 ? data[p++] & 0xff : 0xff
                row[x] = (a << 24) | (r << 16) | (g << 8) | b
            }
            image.setRGB(0, y, width, 1, row, 0, width)
        }
        image
    }

    private static int number(byte[] data, int[] at) {
        while (true) {
            char c = (char) data[at[0]]
            if (c == ('#' as char)) {
                while ((char) data[at[0]] != ('\n' as char)) at[0]++
            } else if (!Character.isWhitespace(c)) break
            at[0]++
        }
        int v = 0
        while (Character.isDigit((char) data[at[0]])) v = v * 10 + Character.digit((char) data[at[0]++], 10)
        v
    }

    private static String line(byte[] data, int[] at) {
        int start = at[0]
        while ((char) data[at[0]] != ('\n' as char)) at[0]++
        new String(data, start, at[0]++ - start, StandardCharsets.ISO_8859_1).trim()
    }

    private static BufferedImage opaque(BufferedImage image) {
        BufferedImage rgb = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        rgb.createGraphics().with {
            drawImage(image, 0, 0, null)
            dispose()
        }
        rgb
    }
}
