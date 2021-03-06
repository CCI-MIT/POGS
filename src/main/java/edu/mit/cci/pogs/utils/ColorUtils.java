package edu.mit.cci.pogs.utils;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;



public class ColorUtils {

    public static String SUBJECT_DEFAULT_BACKGROUND_COLOR_ATTRIBUTE_NAME = "SUBJECT_DEFAULT_BACKGROUND_COLOR";
    public static String SUBJECT_DEFAULT_FONT_COLOR_ATTRIBUTE_NAME = "SUBJECT_DEFAULT_FONT_COLOR";
    // Stack overflow about colors:
    // https://stackoverflow.com/questions/470690/how-to-automatically-generate-n-distinct-colors

    //simple KELLY_COLOR APPROACH
    private static final String[] KELLY_COLORS = {
            new String("0xFFB300"),    // Vivid Yellow
            new String("0x803E75"),    // Strong Purple
            new String("0xFF6800"),    // Vivid Orange
            new String("0xA6BDD7"),    // Very Light Blue
            new String("0xC10020"),    // Vivid Red
            new String("0xCEA262"),    // Grayish Yellow
            new String("0x817066"),    // Medium Gray

            new String("0x007D34"),    // Vivid Green
            new String("0xF6768E"),    // Strong Purplish Pink
            new String("0x00538A"),    // Strong Blue
            new String("0xFF7A5C"),    // Strong Yellowish Pink
            new String("0x53377A"),    // Strong Violet
            new String("0xFF8E00"),    // Vivid Orange Yellow
            new String("0xB32851"),    // Strong Purplish Red
            new String("0xF4C800"),    // Vivid Greenish Yellow
            new String("0x7F180D"),    // Strong Reddish Brown
            new String("0x93AA00"),    // Vivid Yellowish Green
            new String("0x593315"),    // Deep Yellowish Brown
            new String("0xF13A13"),    // Vivid Reddish Orange
            new String("0x232C16"),    // Dark Olive Green
    };



    public final static Float MIN_COMPONENT = .8f;
    public final static Float MAX_COMPONENT = .3f;

    private final static float
            U_OFF = .436f,
            V_OFF = .615f;
    private static final long RAND_SEED = 0;
    private static Random rand = new Random(RAND_SEED);


    public static void main(String[] args) {

        Long.parseLong("-1");
        /*
        Color[] colors = ColorUtils.generateVisuallyDistinctColors(
                ((4 > 10) ? (4) : (10)),
                ColorUtils.MIN_COMPONENT, ColorUtils.MAX_COMPONENT);





        for (int i =0 ; i < 4; i++) {
            System.out.println(""+ String.format("#%02x%02x%02x", colors[i].getRed(),
                    colors[i].getGreen(), colors[i].getBlue()));
        }

         */
    }



    public static Color generateFontColorBasedOnBackgroundColor(Color color){
        Integer r = color.getRed();
        Integer g = color.getGreen();
        Integer b = color.getBlue();
        Integer yiq = ((r*299)+(g*587)+(b*114))/1000;
        return (yiq >= 128) ? Color.black : Color.white;

    }

    public static Color[] generateVisuallyDistinctColors(int ncolors) {
        return generateVisuallyDistinctColors(ncolors, MIN_COMPONENT, MAX_COMPONENT);
    }
    /*
     * Returns an array of ncolors RGB triplets such that each is as unique from the rest as possible
     * and each color has at least one component greater than minComponent and one less than maxComponent.
     * Use min == 1 and max == 0 to include the full RGB color range.
     *
     * Warning: O N^2 algorithm blows up fast for more than 100 colors.
     */
    public static Color[] generateVisuallyDistinctColors(int ncolors, float minComponent, float maxComponent) {
        rand.setSeed(RAND_SEED); // So that we get consistent results for each combination of inputs

        float[][] yuv = new float[ncolors][3];

        // initialize array with random colors
        for(int got = 0; got < ncolors;) {
            System.arraycopy(randYUVinRGBRange(minComponent, maxComponent), 0, yuv[got++], 0, 3);
        }
        // continually break up the worst-fit color pair until we get tired of searching
        for(int c = 0; c < ncolors * 1000; c++) {
            float worst = 8888;
            int worstID = 0;
            for(int i = 1; i < yuv.length; i++) {
                for(int j = 0; j < i; j++) {
                    float dist = sqrdist(yuv[i], yuv[j]);
                    if(dist < worst) {
                        worst = dist;
                        worstID = i;
                    }
                }
            }
            float[] best = randYUVBetterThan(worst, minComponent, maxComponent, yuv);
            if(best == null)
                break;
            else
                yuv[worstID] = best;
        }

        Color[] rgbs = new Color[yuv.length];
        for(int i = 0; i < yuv.length; i++) {
            float[] rgb = new float[3];
            yuv2rgb(yuv[i][0], yuv[i][1], yuv[i][2], rgb);
            rgbs[i] = new Color(rgb[0], rgb[1], rgb[2]);
            //System.out.println(rgb[i][0] + "\t" + rgb[i][1] + "\t" + rgb[i][2]);
        }

        List<Color> l = Arrays.asList(rgbs);
        Collections.shuffle(l);
        Color[] ret = new Color[l.size()];
        return l.toArray(ret);
    }

    public static void hsv2rgb(float h, float s, float v, float[] rgb) {
        // H is given on [0->6] or -1. S and V are given on [0->1].
        // RGB are each returned on [0->1].
        float m, n, f;
        int i;

        float[] hsv = new float[3];

        hsv[0] = h;
        hsv[1] = s;
        hsv[2] = v;
        System.out.println("H: " + h + " S: " + s + " V:" + v);
        if(hsv[0] == -1) {
            rgb[0] = rgb[1] = rgb[2] = hsv[2];
            return;
        }
        i = (int) (Math.floor(hsv[0]));
        f = hsv[0] - i;
        if(i % 2 == 0)
            f = 1 - f; // if i is even
        m = hsv[2] * (1 - hsv[1]);
        n = hsv[2] * (1 - hsv[1] * f);
        switch(i) {
            case 6:
            case 0:
                rgb[0] = hsv[2];
                rgb[1] = n;
                rgb[2] = m;
                break;
            case 1:
                rgb[0] = n;
                rgb[1] = hsv[2];
                rgb[2] = m;
                break;
            case 2:
                rgb[0] = m;
                rgb[1] = hsv[2];
                rgb[2] = n;
                break;
            case 3:
                rgb[0] = m;
                rgb[1] = n;
                rgb[2] = hsv[2];
                break;
            case 4:
                rgb[0] = n;
                rgb[1] = m;
                rgb[2] = hsv[2];
                break;
            case 5:
                rgb[0] = hsv[2];
                rgb[1] = m;
                rgb[2] = n;
                break;
        }
    }



    // From http://en.wikipedia.org/wiki/YUV#Mathematical_derivations_and_formulas
    public static void yuv2rgb(float y, float u, float v, float[] rgb) {
        rgb[0] = 1 * y + 0 * u + 1.13983f * v;
        rgb[1] = 1 * y + -.39465f * u + -.58060f * v;
        rgb[2] = 1 * y + 2.03211f * u + 0 * v;
    }

    public static void rgb2yuv(float r, float g, float b, float[] yuv) {
        yuv[0] = .299f * r + .587f * g + .114f * b;
        yuv[1] = -.14713f * r + -.28886f * g + .436f * b;
        yuv[2] = .615f * r + -.51499f * g + -.10001f * b;
    }

    private static float[] randYUVinRGBRange(float minComponent, float maxComponent) {
        while(true) {
            float y = rand.nextFloat(); // * YFRAC + 1-YFRAC);
            float u = rand.nextFloat() * 2 * U_OFF - U_OFF;
            float v = rand.nextFloat() * 2 * V_OFF - V_OFF;
            float[] rgb = new float[3];
            yuv2rgb(y, u, v, rgb);
            float r = rgb[0], g = rgb[1], b = rgb[2];
            if(0 <= r && r <= 1 &&
                    0 <= g && g <= 1 &&
                    0 <= b && b <= 1 &&
                    (r > minComponent || g > minComponent || b > minComponent) && // don't want all dark components
                    (r < maxComponent || g < maxComponent || b < maxComponent)) // don't want all light components

                return new float[]{y, u, v};
        }
    }

    private static float sqrdist(float[] a, float[] b) {
        float sum = 0;
        for(int i = 0; i < a.length; i++) {
            float diff = a[i] - b[i];
            sum += diff * diff;
        }
        return sum;
    }

    private static double worstFit(Color[] colors) {
        float worst = 8888;
        float[] a = new float[3], b = new float[3];
        for(int i = 1; i < colors.length; i++) {
            colors[i].getColorComponents(a);
            for(int j = 0; j < i; j++) {
                colors[j].getColorComponents(b);
                float dist = sqrdist(a, b);
                if(dist < worst) {
                    worst = dist;
                }
            }
        }
        return Math.sqrt(worst);
    }


    private static float[] randYUVBetterThan(float bestDistSqrd, float minComponent, float maxComponent, float[][] in) {
        for(int attempt = 1; attempt < 100 * in.length; attempt++) {
            float[] candidate = randYUVinRGBRange(minComponent, maxComponent);
            boolean good = true;
            for(int i = 0; i < in.length; i++)
                if(sqrdist(candidate, in[i]) < bestDistSqrd)
                    good = false;
            if(good)
                return candidate;
        }
        return null; // after a bunch of passes, couldn't find a candidate that beat the best.
    }

    public static Color decodeHtmlColorString(String colorString)
    {
        Color color;

        if (colorString.startsWith("#"))
        {
            colorString = colorString.substring(1);
        }
        if (colorString.endsWith(";"))
        {
            colorString = colorString.substring(0, colorString.length() - 1);
        }

        int red, green, blue;
        switch (colorString.length())
        {
            case 6:
                red = Integer.parseInt(colorString.substring(0, 2), 16);
                green = Integer.parseInt(colorString.substring(2, 4), 16);
                blue = Integer.parseInt(colorString.substring(4, 6), 16);
                color = new Color(red, green, blue);
                break;
            case 3:
                red = Integer.parseInt(colorString.substring(0, 1), 16);
                green = Integer.parseInt(colorString.substring(1, 2), 16);
                blue = Integer.parseInt(colorString.substring(2, 3), 16);
                color = new Color(red, green, blue);
                break;
            case 1:
                red = green = blue = Integer.parseInt(colorString.substring(0, 1), 16);
                color = new Color(red, green, blue);
                break;
            default:
                throw new IllegalArgumentException("Invalid color: " + colorString);
        }
        return color;
    }

}
